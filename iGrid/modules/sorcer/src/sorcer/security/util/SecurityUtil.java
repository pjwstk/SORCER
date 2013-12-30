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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PublicKey;

import net.jini.url.httpmd.WrongMessageDigestException;
import fi.hut.tcm.spki.SPKICertificate;
import fi.hut.tcm.spki.impl.SpkiCertFactory;

public class SecurityUtil {

	public SecurityUtil() {
	}

	public static String computeMD5(String filename)
			throws NullPointerException,
			java.security.NoSuchAlgorithmException, java.io.IOException,
			java.io.FileNotFoundException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		FileInputStream ifs = new FileInputStream(filename);
		ByteArrayOutputStream byteOutput = null;
		StringBuffer hexString = new StringBuffer();
		byte[] bytes = new byte[1024];
		int read;

		byteOutput = new ByteArrayOutputStream();
		while ((read = ifs.read(bytes)) > -1)
			byteOutput.write(bytes, 0, read);
		byte[] filebytes = byteOutput.toByteArray();
		// System.out.println("the files string is = "+new String(filebytes));
		byte[] hash = md.digest(filebytes);
		for (int i = 0; i < hash.length; i++) {
			hexString.append(Integer.toHexString(0xFF & hash[i]));
		}
		// System.out.println("HextSring="+hexString.toString());
		return hexString.toString();// new String(hash, "UTF-8");
	}

	/*
	 * public static boolean isModified(String hash, String filename) throws
	 * WrongMessageDigestException{ }
	 */

	public static boolean isModified(String hash1, String hash2)
			throws WrongMessageDigestException {
		if (hash1.equals(hash2)) {
			return false;
		} else {
			throw new WrongMessageDigestException(
					" The file has been modified - Execution Not allowed");
		}
	}

	public static SPKICertificate getSPKICert(KeyPair issuer,
			PublicKey subject, boolean delegation, fi.hut.tcm.spki.Tag tag)
			throws NullPointerException {
		SpkiCertFactory cf = new SpkiCertFactory();
		SPKICertificate cert = cf.makeCert(issuer, subject, delegation, tag);
		if (cert == null) {
			throw new NullPointerException(
					"Certificate (Null) was not generated properly");
		} else {
			return cert;
		}

	}

	public static void main(String args[]) {
		try {
			String hash = SecurityUtil.computeMD5("a.txt");
			System.out.println("The hash of file a.txt=" + hash);
			String hash2 = SecurityUtil.computeMD5("b.txt");
			System.out.println("The hash of file b.txt=" + hash);
			if (!SecurityUtil.isModified(hash, hash2)) {
				System.out.println("IntegrityCheckSuccess");
			} else {
				System.out.println("----");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
