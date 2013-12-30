/**
 * Deployment configuration for the minimum IGrid
 *
 * @author Dennis Reedy
 */
import org.rioproject.RioVersion
import sorcer.util.SorcerEnv

def appendJars(def dlJars) {
    def commonDLJars = ["sorcer-prv-dl.jar", "jsk-dl.jar", "rio-api-${RioVersion.VERSION}.jar", "serviceui.jar", "jmx-lookup-2.1.jar"]
    dlJars.addAll(commonDLJars)
    return dlJars as String[]
}

def getIGridHome() {
    String iGridHome = System.getProperty("IGRID_HOME", System.getenv("IGRID_HOME"))
    if(iGridHome==null) {
        throw new RuntimeException("IGRID_HOME must be set")
    }
    return iGridHome
}

deployment(name: "Sorcer OS") {

    groups SorcerEnv.getLookupGroups()

    codebase SorcerEnv.getWebsterUrl()

    service(name: SorcerEnv.getActualName('Transaction Manager')) {
        interfaces {
            classes 'net.jini.core.transaction.server.TransactionManager'
            resources 'mahalo-dl.jar', 'jsk-dl.jar'
        }
        implementation(class: 'com.sun.jini.mahalo.TransientMahaloImpl') {
            resources "mahalo.jar"
        }
        configuration new File("${getIGridHome()}/bin/jini/configs/mahalo.config").text
        maintain 1
    }

    service(name: SorcerEnv.getActualSpaceName(), fork:'yes', jvmArgs:"-DiGrid.home=${getIGridHome()}") {
        interfaces {
            classes 'net.jini.space.JavaSpace05'
            resources 'blitz-dl.jar', 'blitzui.jar'
        }
        implementation(class: 'org.dancres.blitz.remote.BlitzServiceImpl') {
            resources "blitz.jar", "blitzui.jar", "backport-util-concurrent60.jar",
                      "serviceui.jar", "outrigger-dl.jar", "sorcer-prv.jar"
        }
        configuration new File("${getIGridHome()}/bin/blitz/configs/blitz.config").text
        maintain 1
    }

    service(name: SorcerEnv.getActualName("Jobber"), fork:'yes') {
        interfaces {
            classes 'sorcer.core.provider.Jobber'
            resources appendJars(["jobber-dl.jar", "exertlet-ui.jar"])
        }
        implementation(class: 'sorcer.core.provider.jobber.ServiceJobber') {
            resources "jobber.jar", "sorcer-prv.jar", "monitor-api-${RioVersion.VERSION}.jar", "rio-api-${RioVersion.VERSION}.jar"
        }
        configuration new File("${getIGridHome()}/bin/sorcer/jobber/configs/jobber-prv.config").text
        maintain 1
    }

    service(name: SorcerEnv.getActualName("Spacer"), fork:'yes') {
        interfaces {
            classes 'sorcer.core.provider.Spacer'
            resources appendJars(["spacer-dl.jar"])
        }
        implementation(class: 'sorcer.core.provider.jobber.ServiceSpacer') {
            resources "spacer.jar", "sorcer-prv.jar", "monitor-api-${RioVersion.VERSION}.jar"
        }
        configuration new File("${getIGridHome()}/bin/sorcer/jobber/configs/spacer-prv.config").text
        maintain 1
    }

    service(name: SorcerEnv.getActualName("Cataloger"), fork:'yes') {
        interfaces {
            classes 'sorcer.core.provider.Cataloger'
            resources appendJars(["cataloger-dl.jar", "exertlet-ui.jar",])
        }
        implementation(class: 'sorcer.core.provider.cataloger.ServiceCataloger') {
            resources "cataloger.jar", "sorcer-prv.jar"
        }
        configuration new File("${getIGridHome()}/bin/sorcer/cataloger/configs/cataloger-prv.config").text
        maintain 1
    }

    service(name: SorcerEnv.getActualName("Logger")) {
        interfaces {
            classes 'sorcer.core.RemoteLogger'
            resources appendJars(["logger-dl.jar"])
        }
        implementation(class: 'sorcer.core.provider.logger.ServiceLogger') {
            resources "logger.jar", "sorcer-prv.jar"
        }
        configuration new File("${getIGridHome()}/bin/sorcer/logger/configs/logger-prv.config").text
        maintain 1
    }

    service(name: SorcerEnv.getActualName("Exert Monitor"), fork:'yes', jvmArgs:"-DiGrid.home=${getIGridHome()}") {
        interfaces {
            classes 'sorcer.core.monitor.MonitoringManagement'
            resources appendJars(["exertmonitor-dl.jar", "exertlet-ui.jar"])
        }
        implementation(class: 'sorcer.core.provider.exertmonitor.ExertMonitor') {
            resources 'exertmonitor.jar', "sorcer-prv.jar", "monitor-api-${RioVersion.VERSION}.jar"
        }
        configuration new File("${getIGridHome()}/bin/sorcer/exertmonitor/configs/exertmonitor-prv.config").text
        maintain 1
    }

    service(name: SorcerEnv.getActualName("Concatenator"), fork:'yes', jvmArgs:"-DiGrid.home=${getIGridHome()}") {
        interfaces {
            classes 'sorcer.core.provider.Concatenator'
            resources appendJars(["concatenator-dl.jar", "exertlet-ui.jar"])
        }
        implementation(class: 'sorcer.core.provider.jobber.ServiceConcatenator') {
            resources "concatenator.jar", "sorcer-prv.jar", "monitor-api-${RioVersion.VERSION}.jar", "rio-api-${RioVersion.VERSION}.jar"
        }
        configuration new File("${getIGridHome()}/bin/sorcer/jobber/configs/concatenator-prv.config").text
        maintain 1
    }
    
    service(name: SorcerEnv.getActualName("Exerter")) {
        interfaces {
            classes 'sorcer.service.Exerter'
            resources appendJars(["exertlet-ui.jar"])
        }
        implementation(class: 'sorcer.core.provider.ServiceTasker') {
            resources "exerter.jar", "sorcer-prv.jar", "monitor-api-${RioVersion.VERSION}.jar", "rio-api-${RioVersion.VERSION}.jar"
        }
        configuration new File("${getIGridHome()}/bin/sorcer/exerter/configs/exerter-prv.config").text
        maintain 1
    }
    
        service(name: SorcerEnv.getActualName("Database Storage"), fork:'yes', jvmArgs:"-DiGrid.home=${getIGridHome()}") {
        interfaces {
            classes 'sorcer.core.provider.DatabaseStorer'
            resources appendJars(["dbp-prv-dl.jar", "exertlet-ui.jar"])
        }
        implementation(class: 'sorcer.core.provider.dbp.DatabaseProvider') {
            resources "dbp-prv.jar", "sorcer-prv.jar", "monitor-api-${RioVersion.VERSION}.jar", "rio-api-${RioVersion.VERSION}.jar"
        }
        configuration new File("${getIGridHome()}/bin/sorcer/dbp/configs/dbp-prv.config").text
        maintain 1
    }
}
