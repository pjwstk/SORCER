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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Date;

import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateIssuerName;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateSubjectName;
import sun.security.x509.CertificateValidity;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

public class SignCertificate {

	// Algorithm to use for signing the certificate
	private static final String SIG_ALG_NAME = "MD5WithRSA";

	// Validity for the certificate, in days
	private static final int VALIDITY = 365;

	/**
	 * Usage: SignCertificate keystore CAAlias certToSignAlias newAlias
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 5) {
			System.err
					.println("Usage: java sorcer.scurity.util.SignCertificate keystore CAAlias certToSignAlias newAlias certNo");
			System.exit(1);
		}

		String keystoreFile = args[0];
		String caAlias = args[1];
		String certToSignAlias = args[2];
		String newAlias = args[3];
		String certNo = args[4];

		// Begin by getting a password and reading in the keystore
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Keystore password: ");
		char[] password = in.readLine().toCharArray();
		System.out.print("CA (" + caAlias + ") password: ");
		char[] caPassword = in.readLine().toCharArray();
		System.out.print("Cert (" + certToSignAlias + ") password: ");
		char[] certPassword = in.readLine().toCharArray();

		// Read in the keystore
		FileInputStream input = new FileInputStream(keystoreFile);
		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(input, password);
		input.close();

		// Get the CA's private key for signing
		PrivateKey caPrivateKey = (PrivateKey) keyStore.getKey(caAlias,
				caPassword);
		// Get the CA's certificate
		java.security.cert.Certificate caCert = keyStore
				.getCertificate(caAlias);

		// Now we need to create an X509CertImpl so that we can get ahold
		// of the issuer
		byte[] encoded = caCert.getEncoded();
		X509CertImpl caCertImpl = new X509CertImpl(encoded);
		X509CertInfo caCertInfo = (X509CertInfo) caCertImpl
				.get(X509CertImpl.NAME + "." + X509CertImpl.INFO);

		X500Name issuer = (X500Name) caCertInfo.get(X509CertInfo.SUBJECT + "."
				+ CertificateIssuerName.DN_NAME);

		// Get the cert to be signed
		java.security.cert.Certificate cert = keyStore
				.getCertificate(certToSignAlias);
		PrivateKey privateKey = (PrivateKey) keyStore.getKey(certToSignAlias,
				certPassword);
		encoded = cert.getEncoded();
		X509CertImpl certImpl = new X509CertImpl(encoded);
		X509CertInfo certInfo = (X509CertInfo) certImpl.get(X509CertImpl.NAME
				+ "." + X509CertImpl.INFO);

		// Set the validity
		Date firstDate = new Date();
		Date lastDate = new Date(firstDate.getTime() + VALIDITY * 24 * 60 * 60
				* 1000L);
		CertificateValidity interval = new CertificateValidity(firstDate,
				lastDate);

		certInfo.set(X509CertInfo.VALIDITY, interval);

		// Make a new serial number
		certInfo.set(X509CertInfo.SERIAL_NUMBER,
		// new CertificateSerialNumber((int)(firstDate.getTime()/1000)));
				new CertificateSerialNumber(Integer.parseInt(certNo)));

		// Set the issuer
		certInfo.set(
				X509CertInfo.ISSUER + "." + CertificateSubjectName.DN_NAME,
				issuer);

		AlgorithmId algorithm = new AlgorithmId(
				AlgorithmId.md5WithRSAEncryption_oid);
		certInfo.set(CertificateAlgorithmId.NAME + "."
				+ CertificateAlgorithmId.ALGORITHM, algorithm);
		X509CertImpl newCert = new X509CertImpl(certInfo);

		// Actually sign the certificate
		newCert.sign(caPrivateKey, SIG_ALG_NAME);
		keyStore.setKeyEntry(newAlias, privateKey, certPassword,
				new java.security.cert.Certificate[] { newCert });

		// Store the keystore
		FileOutputStream output = new FileOutputStream(keystoreFile);
		keyStore.store(output, password);
		output.close();

	}
}
