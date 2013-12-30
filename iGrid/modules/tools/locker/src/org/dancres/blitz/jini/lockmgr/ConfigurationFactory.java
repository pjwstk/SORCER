/*
 Copyright 2005 Dan Creswell (dan@dancres.org)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 License for the specific language governing permissions and limitations under
 the License.
*/

package org.dancres.blitz.jini.lockmgr;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationProvider;
import net.jini.config.ConfigurationException;

import net.jini.security.BasicProxyPreparer;
import net.jini.security.ProxyPreparer;

import com.sun.jini.config.Config;

public class ConfigurationFactory {
    private static String[] theArgs = {"config/lockmgr.config"};
    private static Configuration theConfig;

    public static final String BLITZ_MODULE = "org.dancres.blitz.jini.lockmgr";

    /**
       Configure the arguments for finding a Configuration
     */
    public static void setup(String[] anArgs) {
        System.err.println("ConfigurationFactory will load config from: " +
                           anArgs[0]);
        theArgs = anArgs;
    }

    /**
       Attempt to load configured config file or the default which is
       "config/lockmgr.config"
     */
    private static synchronized void load() throws ConfigurationException {
        System.err.println("Loading config from: " + theArgs[0]);

        theConfig =
            ConfigurationProvider.getInstance(theArgs,
                                              ConfigurationFactory.class.getClassLoader());
    }

    /**
       Attempt to obtain the configuration
     */
    public static synchronized Configuration getConfig() 
        throws ConfigurationException {
        if (theConfig == null) {
            load();
        }

        return theConfig;
    }

    public static synchronized Object getEntry(String aName, Class aType)
        throws ConfigurationException {
        return getConfig().getEntry(BLITZ_MODULE, aName, aType);
    }

    public static synchronized Object getEntry(String aName, Class aType,
                                               Object aDefault)
        throws ConfigurationException {

        return getConfig().getEntry(BLITZ_MODULE, aName, aType, aDefault);
    }

    public static synchronized Object getEntry(String aName, Class aType,
                                               Object aDefault, Object aData)
        throws ConfigurationException {
        return getConfig().getEntry(BLITZ_MODULE, aName, aType,
                                    aDefault, aData);
    }
}
