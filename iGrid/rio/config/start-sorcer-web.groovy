/*
 * This configuration is used to start a Webster serving up Sorcer resources
 */

import org.rioproject.config.Component

import com.sun.jini.start.ServiceDescriptor
import net.jini.config.ConfigurationException
import org.rioproject.boot.ServiceDescriptorUtil

//import sorcer.util.Sorcer;

@Component('com.sun.jini.start')
class StartSorcerWebsterConfig {

//	def getWebsterPort() {
//		return Sorcer.getWebsterPort();
//	}
	
    def checkAppendPathSeparator(String dir) {
        if(!dir.endsWith(File.separator))
            dir = dir+File.separator
        return dir
    }

    String[] getWebsterRoots(String iGridHome, String rioHome) {
        iGridHome = checkAppendPathSeparator(iGridHome)
        rioHome = checkAppendPathSeparator(rioHome)
        def websterRoots = [
                iGridHome + "lib", ";" ,
                iGridHome + "lib/sorcer/lib", ";" ,
                iGridHome + "lib/sorcer/lib-dl", ";" ,
                iGridHome + "lib/eng/lib-dl", ";" ,
                iGridHome + "lib/river/lib", ";" ,
                iGridHome + "lib/river/lib-dl", ";" ,
                rioHome   + "lib-dl", ";" ,
                iGridHome + "lib/common/blitz", ";" ,
                iGridHome + "lib/common/blitz/thirdpartylib", ";" ,
                iGridHome + "deploy", ";" ,
                iGridHome + "data", ";" ,
                iGridHome + "lib/common/jfreechart", ";" ,
                iGridHome + "../Products/lib/products/lib-dl"]
        return websterRoots as String[]
    }

    def getValue(String propertyName) {
        String value = System.getProperty(propertyName, System.getenv(propertyName))
        if(value==null) {
            throw new ConfigurationException("${propertyName} must be set either as an environment variable or as a system property")
        }
        return value
    }

    ServiceDescriptor[] getServiceDescriptors() {
        String rioHome = getValue("RIO_HOME")
        String iGridHome = getValue("IGRID_HOME")

        def websterRoots = getWebsterRoots(iGridHome, rioHome)

        String policyFile = rioHome+'/policy/policy.all'

        def serviceDescriptors = [
            //ServiceDescriptorUtil.getWebster(policyFile, '9010', websterRoots as String[]),
			ServiceDescriptorUtil.getWebster(policyFile, '50001', websterRoots as String[])
			//ServiceDescriptorUtil.getWebster(policyFile, getWebsterPort(), websterRoots as String[])
        ]

        return (ServiceDescriptor[])serviceDescriptors
    }

}
