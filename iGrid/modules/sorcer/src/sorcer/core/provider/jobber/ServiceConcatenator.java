/*
 * Copyright 2009 the original author or authors.
 * Copyright 2009 SorcerSoft.org.
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
package sorcer.core.provider.jobber;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.security.auth.Subject;

import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.id.UuidFactory;
import sorcer.core.SorcerConstants;
import sorcer.core.dispatch.BlockThread;
import sorcer.core.exertion.NetJob;
import sorcer.core.provider.Concatenator;
import sorcer.core.provider.ControlFlowManager;
import sorcer.core.provider.ProviderDelegate;
import sorcer.core.provider.ServiceProvider;
import sorcer.service.Block;
import sorcer.service.Condition;
import sorcer.service.Executor;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;
import sorcer.service.Job;
import sorcer.service.ServiceExertion;
import sorcer.service.Signature;

import com.sun.jini.start.LifeCycle;

/**
 * ServiceJobber - The SORCER rendezvous service provider that provides
 * coordination for executing exertions using directly (PUSH) service providers.
 * 
 */
public class ServiceConcatenator extends ServiceProvider implements Concatenator, Executor, SorcerConstants {
	private Logger logger = Logger.getLogger(ServiceConcatenator.class.getName());

	public ServiceConcatenator() throws RemoteException {
		// do nothing
	}

	// require constructor for Jini 2 NonActivatableServiceDescriptor
	public ServiceConcatenator(String[] args, LifeCycle lifeCycle) throws Exception {
		super(args, lifeCycle);
		initLogger();
	}
	
	private void initLogger() {
		Handler h = null;
		try {
			logger = Logger.getLogger("local." + ServiceConcatenator.class.getName() + "."
					+ getProviderName());
			h = new FileHandler(System.getProperty(IGRID_HOME)
					+ "/logs/remote/local-Concatenator-" + delegate.getHostName() + "-" + getProviderName()
					+ "%g.log", 20000, 8, true);
			if (h != null) {
				h.setFormatter(new SimpleFormatter());
				logger.addHandler(h);
			}
			logger.setUseParentHandlers(false);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setServiceID(Exertion ex) {
		try {
			if (getProviderID() != null) {
				logger.finest(getProviderID().getLeastSignificantBits() + ":"
						+ getProviderID().getMostSignificantBits());
				((ServiceExertion) ex).setLsbId(getProviderID()
						.getLeastSignificantBits());
				((ServiceExertion) ex).setMsbId(getProviderID()
						.getMostSignificantBits());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public Exertion service(Exertion exertion) throws RemoteException, ExertionException {
		logger.entering(this.getClass().getName(), "service: " + exertion.getName());
		try {
			// Concatenator overrides SorcerProvider.service method here
			setServiceID(exertion);
			// Create an instance of the ExertionProcessor and call on the
			// process method, returns an Exertion
			Exertion exrt = new ControlFlowManager(exertion, delegate, this).process(threadManager);
			exrt.getDataContext().setExertion(null);
			return exrt;
		} 
		catch (Exception e) {
			e.printStackTrace();
			throw new ExertionException();
		}
	}

	public Exertion execute(Exertion exertion) throws RemoteException,
			TransactionException, ExertionException {
		return execute(exertion, null);
	}
	
	public Exertion execute(Exertion exertion, Transaction txn)
			throws TransactionException, ExertionException, RemoteException {
		//logger.info("*********************************************ServiceConcatenator.exert(), exertion = " + exertion);
		Exertion ex = doBlock(exertion, txn);
		//logger.info("*********************************************ServiceConcatenator.exert(), ex = " + ex);
		return ex;
	}

	public Exertion doBlock(Exertion block) {
		return doBlock(block, null);
	}
	
	public Exertion doBlock(Exertion block, Transaction txn) {
		//logger.info("*********************************************ServiceJobber.doJob(), job = " + job);
		setServiceID(block);
		try {
			if (((ServiceExertion)block).getControlContext().isMonitorable()
					&& !(((NetJob)block).getControlContext()).isWaitable()) {
				replaceNullExertionIDs(block);
				new BlockThread((Block) block, this).start();
				return block;
			} else {
				BlockThread blockThread = new BlockThread((Block) block, this);
				blockThread.start();
				blockThread.join();
				Block result = blockThread.getResult();
				Condition.cleanupScripts(result);
				logger.finest("<==== Result: " + result);
				return result;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getDataURL(String filename) {
		return ((ProviderDelegate) getDelegate()).getProviderConfig().getProperty(
				"provider.dataURL")
				+ filename;
	}

	private String getDataFilename(String filename) {
		return ((ProviderDelegate) getDelegate()).getProviderConfig().getDataDir() + "/"
				+ filename;
	}

	/** {@inheritDoc} */
	public boolean isAuthorized(Subject subject, Signature signature) {
		return true;
	}


	private void replaceNullExertionIDs(Exertion ex) {
		if (ex != null && ((ServiceExertion) ex).getId() == null) {
			((ServiceExertion) ex)
					.setId(UuidFactory.generate());
			if (((ServiceExertion) ex).isJob()) {
				for (int i = 0; i < ((Job) ex).size(); i++)
					replaceNullExertionIDs(((Job) ex).get(i));
			}
		}
	}

}
