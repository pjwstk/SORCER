package sorcer.tools.configbuilder;

import junit.framework.Assert;
import net.jini.config.ConfigurationException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.util.FileUtils;
import org.junit.Test;
import org.rioproject.config.Configuration;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;

/**
 * Test for {@code ConfigBuilder}
 *
 * @author Dennis Reedy
 */
public class ConfigBuilderTest {

    @Test
    public void testConfigBuilder() throws IOException, InterruptedException, ConfigurationException {
        File baseDir = new File(System.getProperty("user.dir"));
        File configFile = new File(baseDir, "/src/test/resources/configs/startup.config");
        File configFileBackup = new File(baseDir, "/src/test/resources/configs/startup.config.back");
        FileUtils fileUtils = FileUtils.getFileUtils();
        try {
            fileUtils.copyFile(configFile, configFileBackup, null, true);
            String antBuildFilePathAndName = System.getProperty("user.dir") + "/src/test/resources/av-prv-build.xml";
            File antFile = new File(antBuildFilePathAndName);
            Project project = new Project();
            project.init();
            ProjectHelper.configureProject(project, antFile);
            project.executeTarget("config");

            verify(project, configFile);
        } finally {
            File newConfigFile = new File(baseDir, "/src/test/resources/configs/startup.config.gen");
            fileUtils.copyFile(configFile, newConfigFile, null, true);
            fileUtils.copyFile(configFileBackup, configFile, null, true);
            configFileBackup.delete();
        }
    }

    @Test
    public void testConfigBuilderOverwrite() throws IOException, InterruptedException, ConfigurationException {
        File baseDir = new File(System.getProperty("user.dir"));
        File configFile = new File(baseDir, "/src/test/resources/configs/startup.config");
        File configFileBackup = new File(baseDir, "/src/test/resources/configs/startup.config.back");
        FileUtils fileUtils = FileUtils.getFileUtils();
        try {
            fileUtils.copyFile(configFile, configFileBackup, null, true);
            String antBuildFilePathAndName = System.getProperty("user.dir") + "/src/test/resources/av-prv-build.xml";
            File antFile = new File(antBuildFilePathAndName);
            Project project = new Project();
            project.init();
            ProjectHelper.configureProject(project, antFile);
            project.executeTarget("config");

            /* Execute again, this will test whether we replace the output */
            project.executeTarget("config");

            verify(project, configFile);

            /* Ensure that the file has not been changed */
            File newConfigFile = new File(baseDir, "/src/test/resources/configs/startup.config");
            Assert.assertTrue(configFile.lastModified()==newConfigFile.lastModified());
        } finally {
            fileUtils.copyFile(configFileBackup, configFile, null, true);
            configFileBackup.delete();
        }
    }

    private void verify(Project project, File configFile) throws ConfigurationException {
        URLClassLoader classLoader = ConfigBuilder.getProjectClassLoader(project);
        Configuration configuration = Configuration.getInstance(classLoader, configFile.getPath());
        String[] interfaces = configuration.getEntry("sorcer.core.exertion.deployment",
                                                     "interfaces",
                                                     String[].class);
        Assert.assertNotNull(interfaces);
        Assert.assertTrue(interfaces.length == 1);
        Assert.assertTrue(interfaces[0].equals("engineering.provider.av.AirVehicleRemoteInterface"));
    }

}
