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

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.rioproject.impl.opstring.OpString;
import org.rioproject.impl.opstring.OpStringLoader;
import org.rioproject.opstring.ClassBundle;
import org.rioproject.opstring.OperationalString;
import org.rioproject.opstring.ServiceElement;
import org.rioproject.opstring.UndeployOption;

import sorcer.core.signature.NetSignature;
import sorcer.core.signature.ServiceSignature;
import sorcer.service.Exertion;
import sorcer.service.ServiceExertion;
import sorcer.util.Sorcer;

/**
 * Create an {@link OperationalString} from an {@link Exertion}.
 *
 * @author Dennis Reedy
 */
public final class OperationalStringFactory {
    static final Logger logger = Logger.getLogger(OperationalStringFactory.class.getName());
    private OperationalStringFactory() {
    }

    /**
     * Create {@link OperationalString}s from an {@code Exertion}.
     *
     * @param exertion The exertion, must not be {@code null}.
     *
     * @return An {@code Map} of {@code Deployment.Type} keys with{@code List<OperationalString> values composed of
     * services created from {@link ServiceSignature}s. If there are no services, return and empty {@code Map}.
     *
     * @throws IllegalArgumentException if the {@code exertion} is {@code null}.
     * @throws Exception if there are configuration issues, if the iGrid opstring cannot be loaded
     */
    public static Map<Deployment.Unique, List<OperationalString>> create(final Exertion exertion) throws Exception {
        if(exertion==null)
            throw new IllegalArgumentException("exertion is null");

        OperationalString iGridDeployment = getIGridDeployment();
        Iterable<NetSignature> netSignatures = getNetSignatures(exertion);
        List<NetSignature> selfies = new ArrayList<NetSignature>();
        List<NetSignature> federated = new ArrayList<NetSignature>();

        List<OperationalString> uniqueOperationalStrings = new ArrayList<OperationalString>();

        for(NetSignature netSignature : netSignatures) {
            if(netSignature.getDeployment()==null)
                continue;
            if(netSignature.getDeployment().getType()==Deployment.Type.SELF) {
                selfies.add(netSignature);
            } else if(netSignature.getDeployment().getType()==Deployment.Type.FED) {
                federated.add(netSignature);
            }
        }

        List<OperationalString> operationalStrings = new ArrayList<OperationalString>();

        for(NetSignature self : selfies) {
            ServiceElement service = ServiceElementFactory.create(self);
            OpString opString = new OpString(createDeploymentID(service), null);
            service.setOperationalStringName(opString.getName());
            opString.addService(service);
            opString.setUndeployOption(getUndeployOption(self.getDeployment()));
            opString.addOperationalString(iGridDeployment);
            if(self.getDeployment().getUnique()== Deployment.Unique.YES) {
                uniqueOperationalStrings.add(opString);
            } else {
                operationalStrings.add(opString);
            }
        }

        List<ServiceElement> services = new ArrayList<ServiceElement>();
        int idle = 0;
        for(NetSignature signature : federated) {
            services.add(ServiceElementFactory.create(signature));
            if(signature.getDeployment().getIdle()>idle) {
                idle = signature.getDeployment().getIdle();
            }
        }
        if(services.isEmpty()) {
            logger.warning(String.format("No services configured for exertion %s", exertion.getName()));
            return null;
        }
        OpString opString = new OpString(exertion.getDeploymentId(),
                                         null);
        for(ServiceElement service : services) {
            service.setOperationalStringName(opString.getName());
            opString.addService(service);
        }
        opString.setUndeployOption(getUndeployOption(idle));
        opString.addOperationalString(iGridDeployment);
        Deployment eDeployment = ((ServiceSignature) exertion.getProcessSignature()).getDeployment();
        Deployment.Unique unique = eDeployment==null? Deployment.Unique.NO:eDeployment.getUnique();
        if(unique == Deployment.Unique.YES) {
            uniqueOperationalStrings.add(opString);
        } else {
            operationalStrings.add(opString);
        }
        Map<Deployment.Unique, List<OperationalString>> opStringMap = new HashMap<Deployment.Unique, List<OperationalString>>();
        opStringMap.put(Deployment.Unique.YES, uniqueOperationalStrings);
        opStringMap.put(Deployment.Unique.NO, operationalStrings);
        return opStringMap;
    }

    private static UndeployOption getUndeployOption(final Deployment deployment) {
        UndeployOption undeployOption = null;
        if(deployment!=null) {
            undeployOption = getUndeployOption(deployment.getIdle());
        }
        return undeployOption;
    }

    private static UndeployOption getUndeployOption(final int idleTimeout) {
        UndeployOption undeployOption = null;
        if (idleTimeout > 0) {
            undeployOption = new UndeployOption((long) idleTimeout,
                                                UndeployOption.Type.WHEN_IDLE,
                                                TimeUnit.MINUTES);
        }
        return undeployOption;
    }

    private static Iterable<NetSignature> getNetSignatures(final Exertion exertion) {
        List<NetSignature> signatures = new ArrayList<NetSignature>();
        if(exertion instanceof ServiceExertion) {
            ServiceExertion serviceExertion = (ServiceExertion)exertion;
            signatures.addAll(serviceExertion.getAllNetTaskSignatures());
        }
        return signatures;
    }

    private static String createDeploymentID(ServiceElement service) throws NoSuchAlgorithmException {
        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append(service.getName());
        for(ClassBundle export : service.getExportBundles()) {
            nameBuilder.append(export.getClassName());
        }
        return Deployment.createDeploymentID(nameBuilder.toString());
    }

    private static OperationalString getIGridDeployment() throws Exception {
        File iGridDeployment = new File(Sorcer.getSorcerHomeDir(), "configs/opstrings/iGridBoot.groovy");
        OpStringLoader opStringLoader = new OpStringLoader(OperationalStringFactory.class.getClassLoader());
        OperationalString[] loaded = opStringLoader.parseOperationalString(iGridDeployment);
        return loaded[0];
    }

}
