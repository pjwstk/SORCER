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

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.SignatureException;

/**
 * <p>
 * A generic interface that needs to be implemented by any class that aims to
 * provide smart card functionality.
 * <p>
 * As of now only one implementation(Java Card) is provided with SCAF. New
 * implementation can be added by implementing this interface.
 */
public interface SmartCard {
	/**
	 * Verifies user pin presented.
	 * 
	 * @param passwordd
	 *            of the user that needs to be authenticated
	 * @return true if the user is verified and false otherwise
	 */
	public boolean verifyPin(String password);

	/**
	 * Returns hash of the challenge string presented
	 * 
	 * @param challenge
	 *            string
	 * @return hash of challenge created using private key from card or null if
	 *         hash could not be calculated or exception is thrown
	 */
	public byte[] getHash(String challenge);

	/**
	 * Returns user certificate from SmartCard
	 * 
	 * @param password
	 *            of user that owns the card
	 * @return certificate read from the card
	 */
	public byte[] getCertificate(String password);

	/**
	 * Encrypts the hash presented
	 * 
	 * @param hash
	 *            of the challenge that needs to be encrypted
	 * @param password
	 *            of user that owns the card
	 * @return encrypted hash or null if exception is thrown
	 */
	public byte[] sign(byte[] hash, String password);

	/**
	 * Encrypts the object presented by first calculating the has for it.
	 * 
	 * @param serializable
	 *            object that needs to be encrypted
	 * @param password
	 *            of user that owns the card
	 * @return encrypted object or null if exception is thrown
	 * @exception IOException
	 *                if the object can not be accessed
	 * @exception InvalidKeyException
	 *                if the key read from card is not valid
	 * @exception SignatureException
	 *                if the signature is not valid
	 */
	public byte[] signObject(Serializable object, String password)
			throws IOException, InvalidKeyException, SignatureException;

}
