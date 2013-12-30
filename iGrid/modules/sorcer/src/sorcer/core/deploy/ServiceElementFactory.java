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
package sorcer.core.deploy;

import net.jini.config.ConfigurationException;
import org.rioproject.RioVersion;
import org.rioproject.config.Configuration;
import org.rioproject.exec.ExecDescriptor;
import org.rioproject.opstring.ClassBundle;
import org.rioproject.opstring.ServiceBeanConfig;
import org.rioproject.opstring.ServiceElement;
import org.rioproject.resolver.Artifact;
import sorcer.core.signature.ServiceSignature;
import sorcer.jini.lookup.entry.DeployInfo;
import sorcer.util.Sorcer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Create a {@link ServiceElement} from a {@link ServiceSignature}.
 *
 * @author Dennis Reedy
 */
public final class ServiceElementFactory {
    static final Logger logger = Logger.getLogger(ServiceElementFactory.class.getName());
    /* The default provider codebase jars */
    static final List<String> commonDLJars = Arrays.asList("sorcer-prv-dl.jar",
                                                           "jsk-dl.jar",
                                                           "rio-api-"+ RioVersion.VERSION+".jar",
                                                           "serviceui.jar",
                                                           "jmx-lookup.jar",
                                                           "provider-ui.jar",
                                                           "exertlet-ui.jar");
    /* The default provider implementation jar */
    static final List<String> commonImplJars = Arrays.asList("sorcer-prv.jar");

    private ServiceElementFactory(){}

    /**
     * Create a {@link ServiceElement}.
     *
     * @param signature The {@link ServiceSignature}, must not be {@code null}.
     *
     * @return A {@code ServiceElement}
     *
     * @throws ConfigurationException if there are problem reading the configuration
     */
    public static ServiceElement create(ServiceSignature signature) throws IOException, ConfigurationException {
        Deployment deployment = signature.getDeployment();
        Configuration configuration = Configuration.getInstance(deployment.getConfigs());
        String component = "sorcer.core.provider.ServiceProvider";

        String name = deployment.getName();
        if(name==null) {
            name = configuration.getEntry(component, "name", String.class, null);
        }
        String[] interfaces = configuration.getEntry("sorcer.core.exertion.deployment",
                                                     "interfaces",
                                                     String[].class);
        String[] codebaseJars = deployment.getCodebaseJars();
        if(codebaseJars==null) {
            codebaseJars = configuration.getEntry("sorcer.core.exertion.deployment",
                                                  "codebaseJars",
                                                  String[].class);
        }
        String[] implJars = deployment.getClasspathJars();
        if(implJars==null) {
            implJars = configuration.getEntry("sorcer.core.exertion.deployment",
                                              "implJars",
                                              String[].class);
        }
        String jvmArgs = deployment.getJvmArgs();
        if(jvmArgs==null) {
            jvmArgs = configuration.getEntry("sorcer.core.exertion.deployment",
                                             "jvmArgs",
                                             String.class,
                                             null);
        }
        Boolean fork = deployment.getFork();
        if(fork==null) {
            fork = configuration.getEntry("sorcer.core.exertion.deployment",
                                          "fork",
                                          Boolean.class,
                                          Boolean.FALSE);
        }
        String providerClass = configuration.getEntry("sorcer.core.exertion.deployment",
                                                      "providerClass",
                                                      String.class,
                                                      null);
        int maxPerNode = deployment.getMaxPerCybernode();
        if(maxPerNode==0) {
            maxPerNode = configuration.getEntry("sorcer.core.exertion.deployment",
                                                "perNode",
                                                int.class,
                                                1);
        }
        ServiceDetails serviceDetails = new ServiceDetails(name,
                                                           interfaces,
                                                           codebaseJars,
                                                           implJars,
                                                           providerClass,
                                                           jvmArgs,
                                                           fork,
                                                           maxPerNode);
        return create(serviceDetails, deployment);
    }

    static ServiceElement create(final ServiceDetails serviceDetails,
                                 final Deployment deployment) throws IOException {
        ServiceElement service = new ServiceElement();

        String websterUrl;
        if(deployment.getWebsterUrl()==null) {
            websterUrl = Sorcer.getWebsterUrl();
            if(logger.isLoggable(java.util.logging.Level.FINE))
                logger.fine("Set code base derived from Sorcer.getWebsterUrl: "+websterUrl);
        } else {
            websterUrl = deployment.getWebsterUrl();
            if(logger.isLoggable(java.util.logging.Level.FINE))
                logger.fine("Set code base derived from Deployment: "+websterUrl);
        }
        /* Create client (export) ClassBundle */
        List<ClassBundle> exports = new ArrayList<ClassBundle>();
        for(String s : serviceDetails.interfaces) {
            ClassBundle export = new ClassBundle(s);
            if(serviceDetails.codebaseJars.length==1 && Artifact.isArtifact(serviceDetails.codebaseJars[0])) {
                export.setArtifact(serviceDetails.codebaseJars[0]);
            } else {
                export.setJARs(appendJars(commonDLJars, serviceDetails.codebaseJars));
                export.setCodebase(websterUrl);
            }
            exports.add(export);
        }

		/* Create service implementation ClassBundle */
        ClassBundle main = new ClassBundle(serviceDetails.providerClass==null?deployment.getImpl():serviceDetails.providerClass);
        if(serviceDetails.implJars.length==1 && Artifact.isArtifact(serviceDetails.implJars[0])) {
            main.setArtifact(serviceDetails.implJars[0]);
        } else {
            main.setJARs(appendJars(commonImplJars, serviceDetails.implJars));
            main.setCodebase(websterUrl);
        }

		/* Set ClassBundles to ServiceElement */
        service.setComponentBundle(main);
        service.setExportBundles(exports.toArray(new ClassBundle[exports.size()]));

        String serviceName;
        if(serviceDetails.name==null) {
		    /* Get the (simple) name from the fully qualified interface */
            if(deployment.getName()==null) {
                StringBuilder nameBuilder = new StringBuilder();
                for(String s : serviceDetails.interfaces) {
                    String value;
                    int ndx = s.lastIndexOf(".");
                    if (ndx > 0) {
                        value = s.substring(ndx + 1);
                    } else {
                        value = s;
                    }
                    if(nameBuilder.length()>0) {
                        nameBuilder.append(" | ");
                    }
                    nameBuilder.append(value);
                }
                serviceName = nameBuilder.toString();
            } else {
                serviceName = deployment.getName();
            }
        } else {
            serviceName = serviceDetails.name;
        }

        if(serviceDetails.maxPerNode>0) {
            service.setMaxPerMachine(serviceDetails.maxPerNode);

        }

		/* Create simple ServiceBeanConfig */
        Map<String, Object> configMap = new HashMap<String, Object>();
        configMap.put(ServiceBeanConfig.NAME, serviceName);
        configMap.put(ServiceBeanConfig.GROUPS, Sorcer.getLookupGroups());
        ServiceBeanConfig sbc = new ServiceBeanConfig(configMap,
                                                      new String[]{getConfigurationAsString(deployment.getConfigs())});
        sbc.addAdditionalEntries(new DeployInfo(deployment.getType().name(), deployment.getUnique().name(), deployment.getIdle()));
        service.setServiceBeanConfig(sbc);
        service.setPlanned(deployment.getMultiplicity());                
        
        /* If the service is to be forked, create an ExecDescriptor */
        if(serviceDetails.fork) {
            service.setFork(true);
            if(serviceDetails.jvmArgs!=null) {
                ExecDescriptor execDescriptor = new ExecDescriptor();
                execDescriptor.setInputArgs(serviceDetails.jvmArgs);
                service.setExecDescriptor(execDescriptor);
            }
        }
        return service;
    }

    private static String[] appendJars(final List<String> base, final String... jars) {
        List<String> jarList = new ArrayList<String>();
        jarList.addAll(base);
        for(String jar : jars) {
            if(jarList.contains(jar)) {
                continue;
            }
            jarList.add(jar);
        }
        return jarList.toArray(new String[jarList.size()]);
    }

    private static String getConfigurationAsString(final String[] configArgs) throws IOException {
        String config = configArgs[0];
        File configFile = new File(config);
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(configFile));
            String line;
            String ls = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
        } finally {
            if(reader!=null)
                reader.close();
        }
        return stringBuilder.toString();
    }

    private static class ServiceDetails {
        final String name;
        final String[] interfaces;
        final String[] codebaseJars;
        final String[] implJars;
        final String providerClass;
        final String jvmArgs;
        final boolean fork;
        final int maxPerNode;

        private ServiceDetails(String name,
                               String[] interfaces,
                               String[] codebaseJars,
                               String[] implJars,
                               String providerClass,
                               String jvmArgs,
                               boolean fork,
                               int maxPerNode) {
            this.name = name;
            this.interfaces = interfaces;
            this.codebaseJars = codebaseJars;
            this.implJars = implJars;
            this.providerClass = providerClass;
            this.jvmArgs = jvmArgs;
            this.fork = fork;
            this.maxPerNode = maxPerNode;
        }
    }
}
