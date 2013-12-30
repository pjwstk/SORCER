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

import net.jini.config.ConfigurationException

/**
 * Writes configuration entries into an existing configuration file.
 *
 * @author Dennis Reedy
 */
class ConfigWriter {
    private final marker = "//GEN-BEGIN"

    def write(configFile, properties, interfaces, currentConfig) {
        StringBuilder builder = new StringBuilder()
        for(String line : configFile.readLines()) {
            if(line.contains(marker))
                break
            builder << line
            builder << "\n"
        }

        String codebase = formatCodebase(properties["sorcer.provider.codebase"] as String)
        String classPath = listToString(properties["sorcer.provider.classpath"]).replaceAll("\\\\", "/")

        /* Test for an update to the configuration. If values have not changed do not update the file*/
        try {
            String[] currentInterfaces = currentConfig.getEntry("sorcer.core.exertion.deployment",
                                                                "interfaces",
                                                                String[].class)
            String[] currentCodebaseJars = currentConfig.getEntry("sorcer.core.exertion.deployment",
                                                                  "codebaseJars",
                                                                  String[].class)
            String[] currentImplJars = currentConfig.getEntry("sorcer.core.exertion.deployment",
                                                              "implJars",
                                                              String[].class)
            String currentProviderClass = currentConfig.getEntry("sorcer.core.exertion.deployment",
                                                                 "providerClass",
                                                                 String.class)
            boolean interfacesEqual = Arrays.equals(interfaces as String[], currentInterfaces)
            boolean codebaseJarsEqual = Arrays.equals(currentCodebaseJars, toStringArray(codebase))
            boolean implJarsEqual = Arrays.equals(currentImplJars, toStringArray(classPath))
            boolean providerClassEqual = properties["provider.class"] == currentProviderClass
            boolean jvmArgsEqual = true
            boolean maxPerCybernodeEqual = true
            if(properties["fork"]!=null) {
                currentConfig.getEntry("sorcer.core.exertion.deployment",
                                       "fork",
                                       Boolean.class)
            }
            if(properties["jvmArgs"]!=null) {
                String currentJvmArgs = currentConfig.getEntry("sorcer.core.exertion.deployment",
                                                               "jvmArgs",
                                                               String.class)
                String newJvmArgs = properties["jvmArgs"]
                jvmArgsEqual = currentJvmArgs.equals(newJvmArgs)
            }
            if(properties["perNode"]!=null) {
                int currentMaxPerCybernode = currentConfig.getEntry("sorcer.core.exertion.deployment",
                                                                    "perNode",
                                                                    Integer.class)
                int maxPerCybernode = Integer.parseInt(properties["perNode"] as String)
                maxPerCybernodeEqual = currentMaxPerCybernode==maxPerCybernode
            }
            if(!(interfacesEqual && codebaseJarsEqual && implJarsEqual && providerClassEqual && jvmArgsEqual && maxPerCybernodeEqual)) {
                doWrite(configFile, builder, codebase, classPath, interfaces, properties)
            }
        } catch (ConfigurationException e) {
            doWrite(configFile, builder, codebase, classPath, interfaces, properties)
        }
    }

    private void doWrite(configFile, builder, codebase, classPath, interfaces, properties) {
        println "Writing ${configFile.path}"
        /* These tables are used to get the lengths of the declarations so indentation can occur, allowing a more
         * readable configuration */
        def configContentTable = [
                "codebase" :   "codebaseJars = new String[]{",
                "interfaces" : "interfaces = new String[]{",
                "classpath" :  "implJars = new String[]{"]

        def groovyContentTable = [
                "codebase" :   "String[] codebaseJars = [",
                "interfaces" : "String[] interfaces = [",
                "classpath" :  "String[] implJars = ["]


        String content
        if(configFile.getName().endsWith("config"))  {
            content = asConfig(properties,
                               indent(codebase, configContentTable["codebase"].length()+4),
                               indent(classPath, configContentTable["classpath"].length()+4),
                               indent(listToString(interfaces), configContentTable["interfaces"].length()+4))
        } else {
            content = asGroovy(properties,
                               indent(codebase, groovyContentTable["codebase"].length()+4),
                               indent(classPath, groovyContentTable["classpath"].length()+4),
                               indent(listToString(interfaces), groovyContentTable["interfaces"].length()+4))
        }
        builder << banner()
        builder << content
        configFile.text = builder.toString()
    }

    private String asConfig(properties, codebase, classPath, interfaces) {
        StringBuilder configBuilder = new StringBuilder()
        configBuilder << "sorcer.core.exertion.deployment {\n"
        configBuilder << "    interfaces = new String[]{"+interfaces+"};\n\n"
        configBuilder << "    codebaseJars = new String[]{"+codebase+"};\n\n"
        configBuilder << "    implJars = new String[]{"+classPath+"};\n"
        if(properties["provider.class"]!=null) {
            configBuilder << "\n"
            configBuilder << "    providerClass = \""+properties["provider.class"]+"\";\n"
        }
        if(properties["fork"]!=null) {
            configBuilder << "\n"
            configBuilder << "    fork = Boolean.valueOf(true);\n"
        }
        if(properties["jvmArgs"]!=null) {
            configBuilder << "\n"
            configBuilder << "    jvmArgs = \"${properties["jvmArgs"]}\";\n"
        }
        if(properties["perNode"]!=null) {
            int perNode = Integer.parseInt(properties["perNode"] as String)
            if(perNode>0) {
                configBuilder << "\n"
                configBuilder << "    perNode = ${perNode};\n"
            }
        }
        configBuilder << "}\n"
        return configBuilder.toString()
    }

    private String asGroovy(properties, codebase, classPath, interfaces) {
        StringBuilder configBuilder = new StringBuilder()
        configBuilder << "@org.rioproject.config.Component('sorcer.core.exertion.deployment')\n"
        configBuilder << "class DeployConfig {\n"
        configBuilder << "    String[] interfaces = ["+interfaces+"]\n\n"
        configBuilder << "    String[] codebaseJars = ["+codebase+"]\n\n"
        configBuilder << "    String[] implJars = ["+classPath+"]\n"
        if(properties["provider.class"]!=null) {
            configBuilder << "\n"
            configBuilder << "    String providerClass = \""+properties["provider.class"]+"\"\n"
        }
        if(properties["fork"]!=null) {
            configBuilder << "\n"
            configBuilder << "    boolean fork = true\n"
        }
        if(properties["jvmArgs"]!=null) {
            configBuilder << "\n"
            configBuilder << "    String jvmArgs = \"${properties["jvmArgs"]}\"\n"
        }
        if(properties["perNode"]!=null) {
            int perNode = Integer.parseInt(properties["perNode"] as String)
            if(perNode>0) {
                configBuilder << "\n"
                configBuilder << "    int perNode = ${perNode}\n"
            }
        }
        configBuilder << "}\n"
        return configBuilder.toString()
    }

    private String listToString(list) {
        if(list instanceof List) {
            StringBuilder builder = new StringBuilder()
            for(String s : list) {
                if(builder.length()>0) {
                    builder << ",\n"
                }
                builder << "\"$s\""
            }
            return builder.toString()
        }
        return list
    }

    private String formatCodebase(String codebase) {
        def jars = []
        String[] parts = codebase.split(" ")
        for(String part : parts) {
            int ndx = part.lastIndexOf("/")
            String result
            if(ndx!=-1) {
                result = part.substring(ndx+1)
            } else {
                result = part
            }
            jars << result
        }
        return listToString(jars)
    }

    private String[] toStringArray(String s) {
        StringTokenizer tok = new StringTokenizer(s, ",\n\"")
        String[] array = new String[tok.countTokens()]
        int i=0;
        while(tok.hasMoreTokens()) {
            array[i] = tok.nextToken();
            i++;
        }
        return array;
    }

    def banner() {
        StringBuilder banner = new StringBuilder()
        banner << "$marker Do not remove\n"
        banner << "/*\n"
        banner << " * Generated BY ConfigBuilder by ${System.getProperty("user.name")} on ${new Date()}\n"
        banner << " *\n"
        banner << " * WARNING: Do NOT modify this code. The content of this configuration is\n"
        banner << " * always regenerated by the Config Builder.\n"
        banner << " */\n"
    }

    def indent(String s, int indent) {
        int lineNumber = 0
        StringBuilder builder = new StringBuilder()
        List<String> lines = s.readLines()
        if(lines.size()==1)
            return s
        for(String line : lines) {
            if(lineNumber>0) {
                for(int i=0; i< indent; i++)
                    builder << " "
            }
            builder << line
            lineNumber++
            if(lineNumber<lines.size())
                builder << "\n"
        }
        return builder.toString()
    }
}
