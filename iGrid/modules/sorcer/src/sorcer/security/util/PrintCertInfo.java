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

package sorcer.security.util;

import java.io.FileInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

/**
 * PrintCertInfo
 * 
 * This class creates a Java Certificate object from a DER-encoded certificate
 * in a file and prints out some basic info about it.
 * 
 * Usage: java fiper.util.PrintCertInfo filename
 */
public class PrintCertInfo {

	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
			System.err
					.println("Usage: java fiper.util.scurity.PrintCertInfo certificateFilename");
			System.exit(1);
		}

		CertificateFactory certFactory = CertificateFactory
				.getInstance("X.509");

		// Open up a certificate file
		FileInputStream fis = new FileInputStream(args[0]);

		// Generate a certificate from that file
		Certificate cert = certFactory.generateCertificate(fis);
		fis.close();

		// Display general information about the certificate
		System.out.println(cert);
	}
}
