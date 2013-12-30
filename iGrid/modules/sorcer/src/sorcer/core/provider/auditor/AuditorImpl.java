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

package sorcer.core.provider.auditor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.security.KeyStore;
import java.security.Principal;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Iterator;

import javax.security.auth.Subject;

import sorcer.core.SorcerConstants;
import sorcer.core.provider.ServiceProvider;
import sorcer.security.sign.SignedServiceTask;
import sorcer.service.Context;
import sorcer.service.ServiceExertion;

import com.sun.jini.start.LifeCycle;

/**
 * <p>
 * AuditorImp is implementation of Auditor interface and is a service provider
 * in SORCER which is used in SCAF to save SignedServiceTasks. It uses Mckoi as
 * an embedded database to save SignedServieTask. It receives SignedServiceTask
 * along with principal name in service context. This principal name is used to
 * identify the rows in the table that stores information about tasks.
 *<p>
 * A JDBCQueryTool is provided with SCAF that is used to verify any repudiation
 * claims.
 */
public class AuditorImpl extends ServiceProvider implements Auditor,
		SorcerConstants {

	/**
	 * Default Constructor
	 * 
	 * @exception RemoteException
	 *                if remote communication could not be performed
	 */
	public AuditorImpl() throws RemoteException {
		// do nothing
	}

	public AuditorImpl(String args[], LifeCycle lifeCycle) throws Exception {
		super(args, lifeCycle);
	}

	/*
	 * public ServiceContext audit(ProviderContext ctx){
	 * Util.debug(this,"Inside audit (ProvideContext ctx) method of AuditorImpl"
	 * ); audit((ServiceContext)ctx); return null; }
	 */

	/**
	 * Audits the information sent in context in a persistent storage.
	 */
	public void audit(Context ctx) {
		save(ctx);
	}

	private void test(SignedServiceTask signedTask) {
		try {
			FileInputStream input = new FileInputStream("trustore");
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(input, new String("client").toCharArray());
			input.close();

			// Get the cert to be signed
			// System.out.println("------------>"+(String)result.getObject(1));
			java.security.cert.Certificate cert = keyStore
					.getCertificate("server");
			// System.out.println("------------>"+(String)result.getObject(1));

			CertificateFactory certificatefactory = CertificateFactory
					.getInstance("X.509");
			java.security.cert.Certificate certificate = certificatefactory
					.generateCertificate(new ByteArrayInputStream(cert
							.getEncoded()));

			X509Certificate mycert = (X509Certificate) certificate;

			// System.out.println("Before: "+HexString.hexify(mycert.getPublicKey().getEncoded())
			// );

			RSAPublicKey key = (RSAPublicKey) mycert.getPublicKey();
			// PrivateKey privateKey =
			// (PrivateKey)keyStore.getKey(certToSignAlias, certPassword);
			// byte[] encoded = cert.getEncoded();
			// RSAPublicKey key=(RSAPublicKey)cert.getPublicKey();
			System.out.println("------------>" + key);
			System.out.println("------------>" + signedTask.getObject());

			Signature signature1 = Signature.getInstance("MD5withRSA");
			signature1.initVerify(key);
			ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
			ObjectOutputStream objectoutputstream = new ObjectOutputStream(
					bytearrayoutputstream);
			objectoutputstream.writeObject(signedTask.getObject());
			objectoutputstream.flush();
			objectoutputstream.close();
			byte[] content = bytearrayoutputstream.toByteArray();
			signature1.update(content);
			boolean answer = signature1.verify(signedTask
					.getProcessByteSignature());
			// System.out.println("\\\\\\\\\\\\\\\\\\\\ "+new
			// String(signedTask.getSignature()));
			// boolean answer =
			// signedTask.verify(key,Signature.getInstance("MD5withRSA"));
			System.out.println("iiiiiiiii " + answer);
		} catch (Exception e) {
			System.out.println("DDDdddDDDDDDDDDD");
		}
	}

	private void save(Context ctx) {
		// Register the Mckoi JDBC Driver
		try {
			Class.forName("com.mckoi.JDBCDriver").newInstance();
		} catch (Exception e) {
			System.out.println("Unable to register the JDBC Driver.\n"
					+ "Make sure the JDBC driver is in the\n" + "classpath.\n");
			e.printStackTrace();
			// return ;
			System.exit(1);
		}

		// This URL specifies we are connecting with a local database
		// within the file system. './db.conf' is the path of the
		// configuration file of the database to embed.
		String url = "jdbc:mckoi:local://./db.conf?create=true";
		String url1 = "jdbc:mckoi:local://./db.conf";

		// The username / password to connect under.
		String username = "sorcer";
		String password = "sorcer.B20";

		ResultSet result;

		// Make a connection with the local database.
		Connection connection;
		Statement statement;
		try {
			try {
				connection = DriverManager.getConnection(url, username,
						password);
			} catch (Exception e) {
				System.out
						.println("Unable to make a connection to the database.\n"
								+ "The reason: " + e.getMessage());
				// e.printStackTrace();
			}
			connection = DriverManager.getConnection(url1, username, password);
			statement = connection.createStatement();

			try {
				System.out.println("-- Creating Tables --");
				statement.executeQuery("    CREATE TABLE SORCER_AUDIT ( "
						+ "       AUDIT_NAME      VARCHAR(100) NOT NULL, "
						+ "       AUDIT_DATE       DATE, "
						+ "       AUDIT_TIME       TIME, "
						+ "       AUDIT_TASK      BLOB) ");
			} catch (Exception e) {
				System.out.println("Unable to create table.\n" + "The reason: "
						+ e.getMessage());
			}

			PreparedStatement ps = connection
					.prepareStatement("INSERT INTO SORCER_AUDIT ( AUDIT_NAME , AUDIT_DATE, AUDIT_TIME, AUDIT_TASK ) VALUES (?,?,?,?)");

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oout = new ObjectOutputStream(baos);
			System.out.println("----------------------->"
					+ ctx.getValue("TASK"));

			// File file=new File("Test");
			// FileOutputStream fos=new FileOutputStream(file);
			// fos.write(((SignedServiceTask)ctx.getValue("TASK")).getSignature());
			// fos.close();
			oout.writeObject(ctx.getValue("TASK"));
			oout.close();

			// test((SignedServiceTask)ctx.getValue("TASK"));

			Context sc = ((ServiceExertion) ((SignedServiceTask) ctx
					.getValue("TASK")).getObject()).getContext();
			String cn;
			if (sc.getPrincipal() != null) {
				// System.out.println("-*******************---"+sc.getPrincipal().getName());
				cn = sc.getPrincipal().getName();
				int i = cn.indexOf(",");
				cn = cn.substring(i + 1);
				i = cn.indexOf(",");
				cn = cn.substring(3, i);
				cn = replaceSpace(cn);
				// Util.debug(this,"Comparing "+p.getName()+" , "+sc.getPrincipal().getName());
				System.out.println("----------------------->" + cn);
				ps.setString(1, cn);
			}

			// javax.security.auth.x500.X500Principal
			// pp=(javax.security.auth.x500.X500Principal)ctx.getValue("SUBJECT");
			// Subject subject=(Subject)ctx.getValue("SUBJECT");
			// Iterator it = subject.getPrincipals().iterator();
			// while(it.hasNext()){
			// Principal p = (Principal)it.next();
			// String cn=p.getName();
			// int i=cn.indexOf(",");
			else {
				// cn = cn.substring(i);
				Subject subject = (Subject) ctx.getValue("SUBJECT");
				Iterator it = subject.getPrincipals().iterator();
				// while(it.hasNext()){
				Principal p = (Principal) it.next();
				cn = p.getName();
				cn = cn.substring(3, 10);
				cn = replaceSpace(cn);
				// Util.debug(this,"Comparing "+p.getName()+" , "+sc.getPrincipal().getName());
				System.out.println("----------------------->" + cn);
				ps.setString(1, cn);
			}
			Calendar cal = Calendar.getInstance();
			ps.setDate(2, new java.sql.Date(cal.getTimeInMillis()));
			ps.setDate(3, new java.sql.Date(cal.getTimeInMillis()));
			ps.setBytes(4, baos.toByteArray());

			ps.execute();

			System.out.println("___________________________________________");

			/*
			 * result = statement.executeQuery("SELECT * FROM SORCER_AUDIT"); if
			 * (result.next()) { System.out.println("Principal:  " +
			 * result.getObject(1));
			 * 
			 * byte[] buf = result.getBytes(4); if (buf != null) {
			 * ObjectInputStream objectIn = new ObjectInputStream(new
			 * ByteArrayInputStream(buf)); SignedServiceTask
			 * task=(SignedServiceTask)objectIn.readObject();
			 * 
			 * RemoteServiceTask etask =
			 * RemoteServiceTask.getRemoteTask((ServiceTask)task.getObject());
			 * String providerId = etask.getMethod().getProviderName();
			 * System.out.println("PROVIDER-------------------->"+providerId); }
			 * }
			 */
			connection.close();

		} catch (Exception e) {
			System.out.println("Unable to insert in table .\n" + "The reason: "
					+ e.getMessage());
			e.printStackTrace();
		}

	}

	private String replaceSpace(String s) {
		// String s = new String("abc def");
		char[] ca = s.toCharArray();
		int iStrLen = s.length();
		char[] CResultArr = new char[iStrLen + 1];
		int j = 0;
		for (int i = 0; i < iStrLen; i++) {
			switch (ca[i]) {
			case ' ':
				CResultArr[j] = '_';
				j++;
				break;
			default:
				CResultArr[j] = ca[i];
				j++;
				break;
			} // end switch
		} // end for
		s = new String(CResultArr);
		// System.out.println(s);
		return s.trim();
	}
}
