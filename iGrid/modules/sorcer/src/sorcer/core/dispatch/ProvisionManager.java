/*
 * Copyright 2012 the original author or authors.
 * Copyright 2012 SorcerSoft.org.
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
package sorcer.core.dispatch;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;

import org.rioproject.deploy.DeployAdmin;
import org.rioproject.deploy.ServiceBeanInstance;
import org.rioproject.deploy.ServiceProvisionListener;
import org.rioproject.impl.opstring.OpString;
import org.rioproject.monitor.ProvisionMonitor;
import org.rioproject.opstring.OperationalString;
import org.rioproject.opstring.OperationalStringManager;
import org.rioproject.opstring.ServiceElement;

import sorcer.core.deploy.Deployment;
import sorcer.core.deploy.OperationalStringFactory;
import sorcer.service.Exertion;
import sorcer.util.ProviderLookup;

/**
 * The {@code ProvisionManager} handles the dynamic creation of {@link OperationalString}s created
 * from {@link Exertion}s.
 *
 * @author Dennis Reedy
 * @author Mike Sobolewski
 */
public class ProvisionManager {
	private static final Logger logger = Logger.getLogger(ProvisionManager.class.getName());
	private final Exertion exertion;
	private OperationalStringManager opStringManager;
	private DeployAdmin deployAdmin;
	private String opStringName;
	
	public ProvisionManager(final Exertion exertion) {
		this.exertion = exertion;
	}
	
    public boolean deployServices() throws DispatcherException {
        Map<Deployment.Unique, List<OperationalString>> deployments;
        try {
            deployments = OperationalStringFactory.create(exertion);
        } catch (Exception e) {
            throw new DispatcherException(String.format("While trying to create deployment for exertion %s",
                                                        exertion.getName()),
                                          e);
        }
        if(deployments.isEmpty()) {
            return false;
        }
        try {
            ProvisionMonitor provisionMonitor = (ProvisionMonitor)ProviderLookup.getService(ProvisionMonitor.class);
            if (provisionMonitor != null) {
                for (Map.Entry<Deployment.Unique, List<OperationalString>> entry : deployments.entrySet()) {
                    for (OperationalString deployment : entry.getValue()) {
                        logger.info(String.format("Processing deployment %s", deployment.getName()));
                        deployAdmin = (DeployAdmin) provisionMonitor.getAdmin();
                        if(deployAdmin.hasDeployed(deployment.getName())) {
                            if (entry.getKey() == Deployment.Unique.YES) {
                                String newName = createDeploymentName(deployment.getName(),
                                                                      deployAdmin.getOperationalStringManagers());
                                logger.info(String.format("Deployment for %s already exists, created new name [%s], " +
                                                          "proceed with autonomic deployment",
                                                          deployment.getName(), newName));
                                ((OpString)deployment).setName(newName);
                            } else {
                                logger.info(String.format("Deployment for %s already exists", deployment.getName()));
                                continue;
                            }
                        } else {
                            logger.info(String.format("Deployment for %s not found, proceed with autonomic deployment",
                                                      deployment.getName()));
                        }
                        ServiceProvisionListener serviceProvisionListener = null;
                        DeployListener deployListener = new DeployListener(deployment.getServices().length);
                        try {
                            serviceProvisionListener = deployListener.export();
                        } catch (ExportException e) {
                            logger.log(Level.WARNING, "Unable to export the ServiceProvisionListener", e);
                        }
                        deployAdmin.deploy(deployment, serviceProvisionListener);
                        opStringName = deployment.getName();
                        opStringManager = deployAdmin.getOperationalStringManager(opStringName);
                        if (!deployListener.await()) {
                            throw new DispatcherException(String.format("Failed to provision exertion %s",
                                                                        exertion.getName()));
                        }
                    }
                }
            } else {
                logger.warning(String.format("Unable to obtain a ProvisionMonitor for %s", exertion.getName()));
            }
        } catch (Exception e) {
            logger.log(Level.WARNING,
                       String.format("Unable to process deployment for %s", exertion.getName()),
                       e);
            throw new DispatcherException(String.format("While trying to provision exertion %s", exertion.getName()), e);
        }
        return true;
    }

    public void undeploy() {
		if(deployAdmin!=null) {
			try {
				deployAdmin.undeploy(opStringName);
			} catch (Exception e) {
				logger.log(Level.WARNING, "Unable to undeploy "+opStringName, e);
			}
		} 
	}
	
	public OperationalStringManager getOperationalStringManager() {
		return opStringManager;
	}

    class DeployListener implements ServiceProvisionListener {
        private final Exporter exporter;
        private ServiceProvisionListener remoteRef;
        private final CountDownLatch countDownLatch;
        private final AtomicBoolean success = new AtomicBoolean(true);

        DeployListener(final int numServices) {
            exporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
                                             new BasicILFactory(),
                                             false,
                                             true);
            countDownLatch = new CountDownLatch(numServices);
        }

        ServiceProvisionListener export() throws ExportException {
            if(remoteRef==null) {
                remoteRef = (ServiceProvisionListener) exporter.export(this);
            }
            return remoteRef;
        }

        void unexport() {
            exporter.unexport(true);
            remoteRef = null;
        }

        boolean await() throws InterruptedException {
            countDownLatch.await();
            return success.get();
        }

        public void succeeded(ServiceBeanInstance serviceBeanInstance) throws RemoteException {
            logger.info(String.format("Service [%s/%s] provisioned on machine %s",
                                      serviceBeanInstance.getServiceBeanConfig().getOperationalStringName(),
                                      serviceBeanInstance.getServiceBeanConfig().getName(),
                                      serviceBeanInstance.getHostName()));
            countDownLatch.countDown();
            if(countDownLatch.getCount()==0) {
                unexport();
            }
        }

        public void failed(ServiceElement serviceElement, boolean resubmitted) throws RemoteException {
            logger.warning(String.format("Service [%s/%s] failed, undeploy",
                                         serviceElement.getServiceBeanConfig().getOperationalStringName(),
                                         serviceElement.getServiceBeanConfig().getName()));
            success.set(false);
            undeploy();
            unexport();
            while(countDownLatch.getCount()>0) {
                countDownLatch.countDown();
            }
        }
    }

    private String createDeploymentName(final String baseName, OperationalStringManager... managers) throws RemoteException {
        int known = 0;
        for(OperationalStringManager manager : managers) {
            if(manager.getOperationalString().getName().startsWith(baseName)) {
                known++;
            }
        }
        return String.format("%s-(%s)", baseName, known);
    }

}
