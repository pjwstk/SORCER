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

package sorcer.scaf.card;

import java.awt.Color;
import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

import opencard.core.event.CTListener;
import opencard.core.event.CardTerminalEvent;
import opencard.core.event.EventGenerator;
import opencard.core.service.CardRequest;
import opencard.core.terminal.CommandAPDU;
import opencard.core.terminal.ResponseAPDU;
import opencard.core.util.HexString;
import opencard.opt.util.PassThruCardService;

/**
 * <p>
 * JavaCard Implementation of SmartCard. Needs user password to to log into
 * javacard. A window is shown if the card is not plugged in.
 * <p>
 * Uses PassThruCardService to access CardEdgeApplet residing on the card. Java
 * Card has to have CardEdgeApplet installed as all the commands presented to
 * the card are specific to that applet. For more information on CardEdgeApplet
 * visit www.muscle.org
 * <p>
 * Provides implementation of all SmartCard function like for password
 * verification, hash generation, encryption (strings and objects) and
 * retrieving user certificates from card.
 * 
 * @author Saurabh Bhatla
 *@see SmartCard
 */

public class JavaCard implements CTListener, SmartCard {
	/**
	 *OCF SmartCard object
	 */
	private opencard.core.service.SmartCard sm;
	/**
	 *OCF Card Request
	 */
	CardRequest cr;
	/**
	 *Information window
	 */
	JWindow window;
	/**
	 *To check if card is plugged in
	 */
	boolean cardIn = false;

	/**
	 *Card Service used to access applet
	 */
	private PassThruCardService myService = null;
	/**
	 *host challenge created by card
	 */
	String host_challenge;
	/**
	 * lenght of host challenge
	 */
	public static byte CHLG_LEN;

	static {
		CHLG_LEN = (byte) 0x08;
	}

	/**
	 * Synchronization monitor if used
	 */
	private final static Object monitor = "synchronization monitor";

	// private static final String chv1 = "00000000";
	// JFrame frame;
	/**
	 * Panel that contains the information label
	 */
	JPanel panel;
	/**
	 * Information label that gets shown in information window
	 */
	JLabel label;

	/**
	 * Default Constructor
	 * 
	 * @exception Exception
	 *                if initialization could not be done
	 */
	public JavaCard() throws Exception {

		out("INITIALIZED-----------------------");
		// this.frame = frame;
		panel = new JPanel();
		label = new JLabel("Please Insert Card");
		init();
	}

	/**
	 *Called by constructor to initialize java card
	 * 
	 * @exception Exception
	 *                if initialization could not be done
	 */
	private void init() throws Exception {
		getWindow();
		internalInit();
		// shutdown();

	}

	/**
	 * Internal init used by init() to initialize java card
	 * 
	 * @exception Exception
	 *                if initialization could not be done
	 */
	private void internalInit() throws Exception {
		out("Insert your card ...");

		Thread currentThread = Thread.currentThread();
		ClassLoader currentCL = currentThread.getContextClassLoader();
		final ClassLoader cl = this.getClass().getClassLoader();
		Thread.currentThread().setContextClassLoader(cl);
		if (opencard.core.service.SmartCard.isStarted() == false) {
			opencard.core.service.SmartCard.start();
		}

		out("Insert your card ...");

		cardIn = true;

		// window.toFront();

		cr = new CardRequest(CardRequest.ANYCARD, null,
				PassThruCardService.class);
		sm = opencard.core.service.SmartCard.waitForCard(cr);
		EventGenerator.getGenerator().addCTListener(this);
		window.hide();
		// currentThread.setContextClassLoader(currentCL);
	}

	/**
	 * Displays the information window to plug in Java Card
	 */
	public void getWindow() {
		if (window == null) {

			label.setBounds(140, 90, 150, 20);
			label.setSize(new Dimension(150, 20));
			panel.add(label);

			panel.setLayout(null);
			panel.setBorder(BorderFactory.createLineBorder(Color.black));

			window = new JWindow();
			window.setSize(400, 200);
			window.setLocation(300, 300);
			window.getContentPane().add(panel);

		}

		// window.update(window.getGraphics());
		window.show();
		// return window;
	}

	/**
	 * Signalled when cars is inserted into the reader
	 * 
	 * @param card
	 *            terminal event
	 */
	public void cardInserted(CardTerminalEvent ctEvent) {
		out("Card In");
		cardIn = true;
	}

	/**
	 * Signalled when card is removed from the reader
	 * 
	 * @param card
	 *            terminal event
	 */
	public void cardRemoved(CardTerminalEvent ctEvent) {
		out("Card Out");
		cardIn = false;
	}

	/**
	 * Retuns true if card is in the reader
	 * 
	 * @return true is card is in the reader
	 */
	public boolean getCardIn() {
		return cardIn;
	}

	/**
	 * Gets Open Card Framework's Smart Card object
	 */
	public opencard.core.service.SmartCard getSmartCard() {
		return sm;
	}

	/**
	 * Shuts down java card
	 */
	public void shutdown()// throws Exception {
	{
		try {
			// window = null;
			opencard.core.service.SmartCard.shutdown();
		} catch (Exception e) {
			out("Exception in shutDown()" + e);
		}
	}

	/**
	 * Gets Card Service from SmartCard object. This service object is used by
	 * all the functions to communicate with the card.
	 * 
	 * @exception Exception
	 *                if service could not be loaded
	 */
	private PassThruCardService getService() throws Exception {
		if (myService == null) {
			myService = (PassThruCardService) sm.getCardService(
					PassThruCardService.class, true);
		}
		return myService;
	}

	/**
	 * Creates a host challenge using current time in milliseconds.
	 * 
	 * @return challenge string generated
	 */
	private String createHostChallenge() {
		byte[] buf = new byte[CHLG_LEN];
		byte[] seed = Long.toString(System.currentTimeMillis()).getBytes();
		Random rand = new SecureRandom(seed);
		rand.nextBytes(buf);
		out("Challenge is:----->" + HexString.hexify(buf));
		return new String(buf);
	}

	/**
	 * Returns hash of the challenge string presented
	 * 
	 * @param challenge
	 *            string
	 * @return hash of challenge created using private key from card or null if
	 *         hash could not be calculated or exception is thrown
	 */
	public byte[] getHash(String challenge) {
		byte hash[] = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(baos);

			out.writeObject(challenge);
			out.flush();
			out.close();
			baos.close();

			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(baos.toByteArray());
			hash = md.digest();
			return hash;
		} catch (Exception e) {
			out("Exception in getHash() " + e);
			return hash;
		}
	}

	/**
	 * USed by verify() to verify user pin presented.
	 * 
	 * @param passwordd
	 *            of the user that needs to be authenticated
	 * @return true if the user is verified and false otherwise
	 */
	public boolean verifyPin(String passwd) {
		try {
			internalInit();
			selectApplet();
			boolean result = verify(passwd);
			// shutdown();
			return result;
		} catch (Exception e) {
			out("Exception in verifyPin() " + e);
			return false;
		}

	}

	/**
	 *Selects CardEdgeApplet
	 */
	public void selectApplet() {
		try {
			out("Selecting MuscleCard Applet");
			byte[] data = new byte[11];
			data[0] = (byte) 0x00;
			data[1] = (byte) 0xA4;
			data[2] = (byte) 0x04;
			data[3] = (byte) 0x00;
			data[4] = (byte) 0x06;
			data[5] = (byte) 0xA0;
			data[6] = (byte) 0x00;
			data[7] = (byte) 0x00;
			data[8] = (byte) 0x00;
			data[9] = (byte) 0x01;
			data[10] = (byte) 0x01;

			CommandAPDU com = new CommandAPDU(data);

			ResponseAPDU resp = getService().sendCommandAPDU(com);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Verifies user pin presented.
	 * 
	 * @param passwordd
	 *            of the user that needs to be authenticated
	 * @return true if the user is verified and false otherwise
	 */
	public boolean verify(String passwd) {
		if (passwd.length() < 8)
			return false;

		try {
			byte[] pass = passwd.getBytes();
			out("Verifying PIN");
			byte[] data = new byte[13];
			data[0] = (byte) 0xB0;
			data[1] = (byte) 0x42;
			data[2] = (byte) 0x01;
			data[3] = (byte) 0x00;
			data[4] = (byte) 0x08;
			data[5] = pass[0];
			data[6] = pass[1];
			data[7] = pass[2];
			data[8] = pass[3];
			data[9] = pass[4];
			data[10] = pass[5];
			data[11] = pass[6];
			data[12] = pass[7];

			CommandAPDU com = new CommandAPDU(data);

			ResponseAPDU resp = getService().sendCommandAPDU(com);
			if (resp.sw() != 0x9000) {
				return false;
			}
			return true;
		} catch (Exception e) {
			out("Exception in verify()" + e);
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * Encrypts the hash presented
	 * 
	 * @param hash
	 *            of the challenge that needs to be encrypted
	 * @param password
	 *            of user that owns the card
	 * @return encrypted hash or null if exception is thrown
	 */
	public byte[] sign(byte[] hash, String password) {
		try {
			byte len;
			byte[] sig1, sig2;
			// internalInit();
			// if(!cardIn) {
			internalInit();
			// }
			verify(password);
			sigInit();
			len = sigFinal(hash);
			sig1 = getResponse(len);
			sig2 = new byte[128];

			System.arraycopy(sig1, 2, sig2, 0, 128);
			// shutdown();
			return sig2;
		} catch (Exception e) {
			out("Exception in sign() " + e);
			return null;
		}
	}

	/**
	 * Sends SigInt command to CardEdgeApplet. Sends parameters like which key,
	 * algorith etc to use.
	 */
	protected void sigInit() {
		try {
			out("Sending Signature Init");
			byte[] data = new byte[10];
			data[0] = (byte) 0xB0;
			data[1] = (byte) 0x36;
			data[2] = (byte) 0x00;
			data[3] = (byte) 0x01;
			data[4] = (byte) 0x05;
			data[5] = (byte) 0x00;
			data[6] = (byte) 0x03;
			data[7] = (byte) 0x01;
			data[8] = (byte) 0x00;
			data[9] = (byte) 0x00;

			CommandAPDU com = new CommandAPDU(data);

			ResponseAPDU resp = getService().sendCommandAPDU(com);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends SigFinal command to CardEdgeApplet
	 * 
	 * @param hash
	 *            of the data that needs to be signed
	 * @return response from CardEdgeApplet
	 */
	protected byte sigFinal(byte[] hash) {
		CommandAPDU com = null;
		ResponseAPDU resp = null;

		try {
			out("Sending Signature Final");
			byte[] data = new byte[136];
			data[0] = (byte) 0xB0;
			data[1] = (byte) 0x36;
			data[2] = (byte) 0x00;
			data[3] = (byte) 0x03;
			data[4] = (byte) 0x83;
			data[5] = (byte) 0x01;
			data[6] = (byte) 0x00;
			data[7] = (byte) 0x80;
			data[8] = (byte) 0x00;
			data[9] = (byte) 0x01;

			for (int i = 10; i < 101; i++)
				data[i] = (byte) 0xFF;
			data[101] = (byte) 0x00;

			data[102] = (byte) 0x30;
			data[103] = (byte) 0x20;
			data[104] = (byte) 0x30;
			data[105] = (byte) 0x0C;
			data[106] = (byte) 0x06;
			data[107] = (byte) 0x08;
			data[108] = (byte) 0x2A;
			data[109] = (byte) 0x86;
			data[110] = (byte) 0x48;
			data[111] = (byte) 0x86;
			data[112] = (byte) 0xF7;
			data[113] = (byte) 0x0D;
			data[114] = (byte) 0x02;
			data[115] = (byte) 0x05;
			data[116] = (byte) 0x05;
			data[117] = (byte) 0x00;
			data[118] = (byte) 0x04;
			data[119] = (byte) 0x10;

			System.arraycopy(hash, 0, data, 120, hash.length);

			com = new CommandAPDU(data);

			resp = getService().sendCommandAPDU(com);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp.sw2();
	}

	/**
	 * Retrieves response apdus from card. Used by all the methods to get the
	 * result of the operation executed
	 * 
	 * @param length
	 *            of the data that needs to be red
	 *@return data that is read as a response to some previous command apdu
	 *         sent
	 */
	protected byte[] getResponse(byte len) {
		CommandAPDU com = null;
		ResponseAPDU resp = null;

		try {
			out("Getting Data");
			byte data[] = new byte[5];
			data[0] = (byte) 0xB0;
			data[1] = (byte) 0xC0;
			data[2] = (byte) 0x00;
			data[3] = (byte) 0x00;
			data[4] = len;

			com = new CommandAPDU(data);
			resp = getService().sendCommandAPDU(com);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return resp.data();

	}

	/**
	 * Reads user certificate data from SmartCard
	 * 
	 * @param offset1
	 *            begining point to read data from
	 * @param offset2
	 *            ending point of adata to be read
	 * @return certificate data read from the card
	 */
	protected byte readCertificate(byte off1, byte off2, byte len) {
		CommandAPDU com = null;
		ResponseAPDU resp = null;

		try {
			out("Reading Certificate Data");
			byte data[] = new byte[14];

			data[0] = (byte) 0xB0;
			data[1] = (byte) 0x56;
			data[2] = (byte) 0x00;
			data[3] = (byte) 0x00;
			data[4] = (byte) 0x09;
			data[5] = (byte) 0x43;
			data[6] = (byte) 0x30;
			data[7] = (byte) 0x00;
			data[8] = (byte) 0x00;
			data[9] = (byte) 0x00;
			data[10] = (byte) 0x00;
			data[11] = off1;
			data[12] = off2;
			data[13] = len;

			com = new CommandAPDU(data);
			resp = getService().sendCommandAPDU(com);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return resp.sw2();
	}

	/**
	 * Returns user certificate from SmartCard
	 * 
	 * @param password
	 *            of user that owns the card
	 * @return certificate read from the card
	 */
	public byte[] getCertificate(String password) {
		try {
			byte[] cert = new byte[1135];
			byte len;
			byte data[];

			if (!cardIn) {
				internalInit();
			}
			// internalInit();
			selectApplet();
			verify(password);
			len = readCertificate((byte) 0x00, (byte) 0x00, (byte) 0xFF);
			data = getResponse(len);
			System.arraycopy(data, 0, cert, 0, 255);

			len = readCertificate((byte) 0x00, (byte) 0xFF, (byte) 0xFF);
			data = getResponse(len);
			System.arraycopy(data, 0, cert, 255, 255);

			try {
				len = readCertificate((byte) 0x01, (byte) 0xFE, (byte) 0xFF);
				data = getResponse(len);
				System.arraycopy(data, 0, cert, 510, 255);
			} catch (Exception e) {
				len = readCertificate((byte) 0x01, (byte) 0xFE, (byte) 0x0F);
				data = getResponse(len);
				System.arraycopy(data, 0, cert, 510, 15);
				return cert;
			}

			/*
			 * len=readCertificate((byte)0x01, (byte)0xFE, (byte)0xFF);
			 * data=getResponse(len); System.arraycopy(data ,0, cert, 510, 255);
			 */

			len = readCertificate((byte) 0x02, (byte) 0xFD, (byte) 0xFF);
			data = getResponse(len);
			System.arraycopy(data, 0, cert, 765, 255);

			len = readCertificate((byte) 0x03, (byte) 0xFC, (byte) 0x0A);
			data = getResponse(len);
			System.arraycopy(data, 0, cert, 1020, 10);

			// shutdown();
			return cert;
		} catch (Exception e) {
			out("Exception in getCertificate() " + e);
			return null;
		}

	}

	/**
	 * Encrypts the object presented by first calculating the has for it.
	 * 
	 * @param serializable
	 *            object that needs to be encrypted
	 * @param password
	 *            of user that owns the card
	 * @return encrypted object
	 * @exception IOException
	 *                if the object can not be accessed
	 * @exception InvalidKeyException
	 *                if the key read from card is not valid
	 * @exception SignatureException
	 *                if the signature is not valid
	 */
	public byte[] signObject(Serializable object, String password)
			throws IOException, InvalidKeyException, SignatureException// ,
																		// JavaCardException
	{
		ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
		ObjectOutputStream objectoutputstream = new ObjectOutputStream(
				bytearrayoutputstream);
		objectoutputstream.writeObject(object);
		objectoutputstream.flush();
		objectoutputstream.close();
		bytearrayoutputstream.close();
		java.security.MessageDigest md;
		try {
			md = java.security.MessageDigest.getInstance("MD5");
			md.update(bytearrayoutputstream.toByteArray());
			byte[] hash = md.digest();
			verifyPin(password);
			return sign(hash, password);

		} catch (Exception e) {
			out("Exception in SorcerSignedObject constructor" + e);
		}
		return null;

	}

	/**
	 * Prints debug statements
	 * 
	 * @param string
	 *            to be displayed
	 */
	public void out(String str) {
		Debug.out("JavaCard >> " + str);
	}
}
