/*
 * Copyright 2010 the original author or authors.
 * Copyright 2010 SorcerSoft.org.
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

package sorcer.test.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import java.util.logging.Logger;

import jgapp.util.Util;
import sorcer.arithmetic.Averager;
import sorcer.arithmetic.AveragerImpl;
import sorcer.arithmetic.PartnerAveragerImpl;
import sorcer.arithmetic.AveragerRemote;
import sorcer.core.proxy.Outer;
import sorcer.core.proxy.Partner;
import sorcer.core.proxy.Partnership;
import sorcer.core.proxy.RemotePartner;
import sorcer.util.Sorcer;
import sorcer.util.Log;

public class RemoteTester {

	private static Logger logger = Log.getTestLog();

	public static void main(String[] args) {
		RemoteTester tester = new RemoteTester();
		tester.run();
	}

	private void run() {

		logger.info("iGrid.home: " + System.getProperty("iGrid.home"));

		Properties props = Sorcer.getEnvProperties();
		Util.logProperties(props, "SORCER Environment configurtion");
		AveragerRemote aimpl = null, aimpl_stub = null;
		
		//public class AveragerImpl implements AveragerRemote, Serializable 
		aimpl = new AveragerImpl();
		try {
			aimpl_stub = (AveragerRemote) UnicastRemoteObject.exportObject(aimpl);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		// Remote implementation testing
		if (aimpl instanceof Remote)
			logger.info("AveragerImpl is Remote");
		else 
			logger.info("AveragerImpl is NOT Remote");
		
		if (aimpl instanceof Averager)
			logger.info("AveragerImpl is Averager\n\n");
		else 
			logger.info("AveragerImpl is NOT Averager\n\n");
		
		// stub testing
		if (aimpl_stub instanceof Remote)
			logger.info("AveragerImpl_Stub is Remote");
		else 
			logger.info("AveragerImpl_Stub is NOT Remote");
		
		
		if (aimpl_stub instanceof Averager)
			logger.info("AveragerImpl_Stub is Averager\n\n");
		else 
			logger.info("AveragerImpl_Stub is NOT Averager\n\n");
		
		
		// public class AveragerPartnerImpl implements AveragerRemote, Partner, Serializable
		PartnerAveragerImpl apimlp = new PartnerAveragerImpl();
		AveragerRemote apimlp_stub = null;
		try {
			apimlp_stub = (AveragerRemote) UnicastRemoteObject.exportObject(apimlp);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		// Testing implementation interfaces
		if (apimlp instanceof Remote)
			logger.info("AveragerPartnerImpl is Remote");
		else 
			logger.info("AveragerPartnerImpl is NOT Remote");
		
		if (apimlp instanceof Averager)
			logger.info("AveragerPartnerImpl is Averager");
		else 
			logger.info("AveragerPartnerImpl is NOT Averager");
		
		if (apimlp instanceof Outer)
			logger.info("AveragerPartnerImpl is Outer");
		else 
			logger.info("AveragerPartnerImpl is NOT Outer");
		
		if (apimlp instanceof Partnership)
			logger.info("AveragerPartnerImpl is Partnership");
		else 
			logger.info("AveragerPartnerImpl is NOT Partnership");
		
		if (apimlp instanceof Partner)
			logger.info("AveragerPartnerImpl is Partner");
		else 
			logger.info("AveragerPartnerImpl is NOT Partner");
		
		if (apimlp instanceof RemotePartner)
			logger.info("AveragerPartnerImpl is RemotePartner\n\n");
		else 
			logger.info("AveragerPartnerImpl is NOT RemotePartner\n\n");
		
       // Stub testing
		if (apimlp_stub instanceof Remote)
			logger.info("AveragerPartnerImpl_Stub is Remote");
		else 
			logger.info("AveragerPartnerImpl_Stub is NOT Remote");
		
		if (apimlp_stub instanceof Averager)
			logger.info("AveragerPartnerImpl_Stub is Averager");
		else 
			logger.info("AveragerPartnerImpl_Stub is NOT Averager");
		
		if (apimlp_stub instanceof Outer)
			logger.info("AveragerPartnerImpl_Stub is Outer" + (Outer)apimlp_stub);
		else 
			logger.info("AveragerPartnerImpl_Stub is NOT Outer");
		
		if (apimlp_stub instanceof Partnership)
			logger.info("AveragerPartnerImpl_Stub is Partnership");
		else 
			logger.info("AveragerPartnerImpl_Stub is NOT Partnership");
		
		if (apimlp_stub instanceof Partner)
			logger.info("AveragerPartnerImpl_Stub is Partner");
		else 
			logger.info("AveragerPartnerImpl_Stub is NOT Partner");
		
		if (apimlp_stub instanceof RemotePartner)
			logger.info("AveragerPartnerImpl_Stub is RemotePartner");
		else 
			logger.info("AveragerPartnerImpl_Stub is NOT RemotePartner");
	}
}