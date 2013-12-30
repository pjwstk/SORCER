/*
 * Copyright to the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sorcer.tools.configbuilder

import net.jini.config.Configuration
import net.jini.config.ConfigurationProvider
import org.apache.tools.ant.*

/**
 * The {@code ConfigBuilder} builds configurations for signature based deployment.
 *
 * @author Dennis Reedy
 */
class ConfigBuilder extends Task {
    private String configFile
    private String target
    private String fork
    private String jvmArgs
    private String starter
    private String perNode = "1"
    static final String DEFAULT_TARGET = "run.provider"

    void setTarget(String target) {
        this.target = target
    }

    void setConfig(String configFile) {
        this.configFile = configFile
    }

    void setFork(String fork) {
        this.fork = fork;
    }

    void setJvmArgs(String jvmArgs) {
        this.jvmArgs = jvmArgs
    }

    void setStarter(String starter) {
        this.starter = starter
    }

    void setPerNode(String perNode) {
        this.perNode = perNode
    }

    @Override
    void execute() throws BuildException {
        target = target==null?DEFAULT_TARGET:target
        if(configFile==null)
            throw new BuildException("A configuration file containing sorcer.core.provider.ServiceProvider properties must be provided")
        if(starter==null)
            throw new BuildException("The Ant file used to start the Provider must be provided")
        log "Config file:  ${configFile}"
        log "Starter file: ${starter}"
        log "Target:       ${target}"
        if(fork!=null)
            log "Fork:         ${fork}"
        if(jvmArgs!=null)
            log "JVMArgs:      ${jvmArgs}"
        if(perNode!=null)
            log "PerNode:      ${perNode}"
        def properties = [:]

        File antFile = new File(starter);
        if(!antFile.exists())
            throw new BuildException("Cannot locate ${starter}")
        File serviceConfigFile = new File(configFile);
        if(!serviceConfigFile.exists())
            throw new BuildException("Cannot locate ${configFile}")
        if(!(serviceConfigFile.name.endsWith("config") || serviceConfigFile.name.endsWith("groovy")))
            throw new BuildException("Unsupported ${configFile} type")
        Project project = new Project();
        project.init()
        ProjectHelper.configureProject(project, antFile)

        String projectClasspath = getProjectClassPath(project)

        StringBuilder jvmArgsBuilder = new StringBuilder()
        if(jvmArgs)
            jvmArgsBuilder.append(jvmArgs)
        def targets = project.getTargets();
        for(Object key : targets.keySet()) {
            if(key==target) {
                def target = targets[key]
                for(def task : target.tasks) {
                    for(def child : task.children) {
                        if(child.componentName=="sysproperty") {
                            RuntimeConfigurable runtime = child.getWrapper()
                            for (Enumeration e = runtime.attributeMap.elements(); e.hasMoreElements();) {
                                String property = e.nextElement()
                                String value = e.nextElement()
                                if(value!=null && value.length()>0)
                                    properties[property] = value
                            }
                        }
                        if(child.componentName=="jvmarg") {
                            RuntimeConfigurable runtime = child.getWrapper()
                            String jvmArg = runtime.attributeMap["value"]
                            if(jvmArgsBuilder.length()>0)
                                jvmArgsBuilder.append(" ")
                            jvmArgsBuilder.append(jvmArg)
                        }
                    }
                }

            }
        }
        def userProperties = project.properties
        String providerClass = null
        properties.each { key, value ->
            String declared = resolveProperty(value, userProperties)
            if(declared!=null) {
                properties[key] = declared
            }
            if(value=="\${provider.class}") {
                providerClass = declared
            }
        }
        if(providerClass!=null)
            properties["provider.class"] = providerClass

        properties["sorcer.provider.classpath"] = fixClassPath(projectClasspath, userProperties["lib"] as String)
        URLClassLoader classLoader = getProjectClassLoader(projectClasspath)
        Configuration currentConfig = ConfigurationProvider.getInstance([configFile] as String[], classLoader)
        Class[] publishedInterfaces = (Class[])currentConfig.getEntry("sorcer.core.provider.ServiceProvider",
                                                               "publishedInterfaces",
                                                               Class[].class,
                                                               null)
        def interfaces = []
        for(Class c : publishedInterfaces) {
            interfaces << c.name
        }
        if(fork)
            properties["fork"] = "yes"
        if(jvmArgsBuilder.length()>0)
            properties["jvmArgs"] = jvmArgsBuilder.toString()
        properties["perNode"] = perNode
        ConfigWriter configWriter = new ConfigWriter()
        configWriter.write(serviceConfigFile, properties, interfaces, currentConfig)
    }

    def static getProjectClassPath(Project project) {
        String projectClasspath = null
        def refs = project.getReferences()
        for(Object key : refs.keySet()) {
            if(key=="project.classpath") {
                projectClasspath = refs[key]
            }
        }
        projectClasspath
    }

    /* Opened up for testing */
    static URLClassLoader getProjectClassLoader(Project project) {
        getProjectClassLoader(getProjectClassPath(project))
    }

    def static getProjectClassLoader(String projectClassPath) {
        def urls = []
        def classPath = projectClassPath.split(File.pathSeparator)
        classPath.each { element ->
            File file = new File(element as String)
            urls << file.toURI().toURL()
        }

        return new URLClassLoader(urls as URL[])
    }

    def fixClassPath(String projectClassPath, String libDir) {
        def classPath = []
        if(!libDir.endsWith("/"))
            libDir = libDir+"/"
        projectClassPath.split(File.pathSeparator).each { path ->
            if(!(path.contains("jsk") || path.contains("rio"))) {
                classPath << path.substring(libDir.length())
            } else {
                log "Trimmed $path"
            }
        }
        classPath
    }

    def String resolveProperty(prop, properties) {
        String result
        if(prop.startsWith("\${")) {
            result = expandProperties(prop, properties)
            if(result ==null)
                result = prop
        } else {
            result = prop
        }
        result
    }

    def expandProperties(final String arg, final Hashtable properties) {
        String start="\${";
        String end = "}";
        int s = 0;
        int e  ;
        StringBuilder result = new StringBuilder();
        while((e = arg.indexOf(start, s)) >= 0) {
            String str = arg.substring(e+start.length());
            int n = str.indexOf(end);
            if(n != -1) {
                result.append(arg.substring(s, e));
                String prop = str.substring(0, n);
                if(prop.equals("/")) {
                    result.append(File.separator);
                } else if(prop.equals(":")) {
                    result.append(File.pathSeparator);
                } else {
                    String value = properties.get(prop);
                    if(value == null)
                        return null
                    result.append(value);
                }
                s = e+start.length()+prop.length()+end.length();
            } else {
                result.append(start);
                s = e+start.length();
            }
        }
        result.append(arg.substring(s));
        return result.toString()
    }
}
