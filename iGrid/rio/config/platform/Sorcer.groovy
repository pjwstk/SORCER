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

import org.rioproject.config.PlatformCapabilityConfig

/**
 * Declares Sorcer capability in the platform
 */
class SorcerPlatformConfig {

    def getPlatformCapabilityConfigs() {
        def configs = []
        File sorcerLibDir = new File(System.getProperty("IGRID_HOME")+File.separator+"lib"+File.separator+"sorcer"+File.separator+"lib")
        if(sorcerLibDir.exists()) {
            /*["sorcer-prv.jar", "sorcer-vfe-lib.jar", "sorcer-modeling-lib.jar"].each { jar ->*/
            ["sorcer-env.jar"].each { jar ->
                File jarFile = new File(sorcerLibDir, jar)
                if(jarFile.exists()) {
                    configs << new PlatformCapabilityConfig("Sorcer ${jarFile.name}",
                                                            "12",
                                                            "",
                                                            "",
                                                            jarFile.path)
                } else {
                    System.err.println("The ${jarFile.path} does not exist, cannot add Sorcer jar to platform")
                }
            }
        } else {
            System.err.println("The ${sorcerLibDir.path} does not exist, cannot add Sorcer jars to platform")
        }

        File commonLibDir = new File(System.getProperty("IGRID_HOME")+File.separator+"lib"+File.separator+"common")
        if(commonLibDir.exists()) {
            File jar = new File(commonLibDir, "je-4.1.21.jar")
            if(jar.exists()) {
                configs << new PlatformCapabilityConfig("Sleepy Cat",
                                                        "4.1.21",
                                                        "",
                                                        "",
                                                        jar.path)
            } else {
                System.err.println("The ${commonLibDir.path} does not exist, cannot add sleepy cat to platform")
            }
        } else {
            System.err.println("The ${commonLibDir.path} does not exist, cannot add sleepy cat to platform")
        }
        return configs
    }

}
