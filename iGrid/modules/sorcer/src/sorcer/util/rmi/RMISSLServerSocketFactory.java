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

package sorcer.util.rmi;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;

public class RMISSLServerSocketFactory implements RMIServerSocketFactory,
		Serializable {

	public ServerSocket createServerSocket(int port) throws IOException {
		SSLServerSocketFactory ssf = null;
		try {
			// set up key manager to do server authentication
			SSLContext ctx;
			KeyManagerFactory kmf;
			KeyStore ks;
			char[] passphrase = "passphrase".toCharArray();

			ctx = SSLContext.getInstance("TLS");
			kmf = KeyManagerFactory.getInstance("SunX509");
			ks = KeyStore.getInstance("JKS");

			ks.load(new FileInputStream("testkeys"), passphrase);
			kmf.init(ks, passphrase);
			ctx.init(kmf.getKeyManagers(), null, null);

			ssf = ctx.getServerSocketFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ssf.createServerSocket(port);
	}
}
