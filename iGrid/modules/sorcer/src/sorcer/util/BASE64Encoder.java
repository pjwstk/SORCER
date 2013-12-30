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

package sorcer.util;

/**
 * Utility class to do Base64 encoding, as defined by RFC 2045, section 6.8
 * (http://www.ietf.org/rfc/rfc2045.txt) Uses the same class and function names
 * as Sun's implementation from sun.misc
 */
public class BASE64Encoder {

	/**
	 * Byte value that maps to 'a' in Base64 encoding
	 */
	final static int LOWER_CASE_A_VALUE = 26;

	/**
	 * Byte value that maps to '0' in Base64 encoding
	 */
	final static int ZERO_VALUE = 52;

	/**
	 * Byte value that maps to '+' in Base64 encoding
	 */
	final static int PLUS_VALUE = 62;

	/**
	 * Byte value that maps to '/' in Base64 encoding
	 */
	final static int SLASH_VALUE = 63;

	/**
	 * Bit mask for one character worth of bits in Base64 encoding. Equivalent
	 * to binary value 111111b.
	 */
	private final static int SIX_BIT_MASK = 63;

	/**
	 * Convert a byte to an integer. Needed because in Java bytes are signed,
	 * and for Base64 purposes they are not. If not done this way, when
	 * converted to an int, 0xFF will become -127
	 * 
	 * @param b
	 *            Byte value to be converted
	 * @return Value as an integer, as if byte was unsigned
	 */
	private int convertUnsignedByteToInt(byte b) {
		if (b >= 0) {
			return (int) b;
		}

		return 256 + b;
	}

	/**
	 * Encode an array of bytes using Base64
	 * 
	 * @param data
	 *            [] The bytes to be encoded
	 * @return A valid Base64 representation of the input
	 */
	public String encode(byte data[]) {
		// Base64 encoding yields a String that is 33% longer than the byte
		// array
		int charCount = ((data.length * 4) / 3) + 4;

		// New lines will also be needed for every 76 charactesr, so allocate a
		// StringBuffer that is long enough to hold the full result without
		// having to expand later
		StringBuffer result = new StringBuffer((charCount * 77) / 76);

		int byteArrayLength = data.length;
		int byteArrayIndex = 0;
		int byteTriplet = 0;
		while (byteArrayIndex < byteArrayLength - 2) {
			// Build the 24 bit byte triplet from the input data
			byteTriplet = convertUnsignedByteToInt(data[byteArrayIndex++]);
			// Each input byte contributes 8 bits to the triplet
			byteTriplet <<= 8;
			byteTriplet |= convertUnsignedByteToInt(data[byteArrayIndex++]);
			byteTriplet <<= 8;
			byteTriplet |= convertUnsignedByteToInt(data[byteArrayIndex++]);

			// Look at the lowest order six bits and remember them
			byte b4 = (byte) (SIX_BIT_MASK & byteTriplet);
			// Move the byte triplet to get the next 6 bit value
			byteTriplet >>= 6;
			byte b3 = (byte) (SIX_BIT_MASK & byteTriplet);
			byteTriplet >>= 6;
			byte b2 = (byte) (SIX_BIT_MASK & byteTriplet);
			byteTriplet >>= 6;
			byte b1 = (byte) (SIX_BIT_MASK & byteTriplet);

			// Add the Base64 encoded character to the result String
			result.append(mapByteToChar(b1));
			result.append(mapByteToChar(b2));
			result.append(mapByteToChar(b3));
			result.append(mapByteToChar(b4));

			// There are 57 bytes for every 76 characters, so wrap the line when
			// needed
			if (byteArrayIndex % 57 == 0) {
				result.append("\n");
			}
		}

		// Check if we have one byte left over
		if (byteArrayIndex == byteArrayLength - 1) {
			// Convert our one byte to an int
			byteTriplet = convertUnsignedByteToInt(data[byteArrayIndex++]);
			// Right pad the second 6 bit value with zeros
			byteTriplet <<= 4;

			byte b2 = (byte) (SIX_BIT_MASK & byteTriplet);
			byteTriplet >>= 6;
			byte b1 = (byte) (SIX_BIT_MASK & byteTriplet);

			result.append(mapByteToChar(b1));
			result.append(mapByteToChar(b2));

			// Add "==" to the output to make it a multiple of 4 Base64
			// characters
			result.append("==");
		}

		// Check if we have two byte left over
		if (byteArrayIndex == byteArrayLength - 2) {
			// Convert our two bytes to an int
			byteTriplet = convertUnsignedByteToInt(data[byteArrayIndex++]);
			byteTriplet <<= 8;
			byteTriplet |= convertUnsignedByteToInt(data[byteArrayIndex++]);
			// Right pad the third 6 bit value with zeros
			byteTriplet <<= 2;

			byte b3 = (byte) (SIX_BIT_MASK & byteTriplet);
			byteTriplet >>= 6;
			byte b2 = (byte) (SIX_BIT_MASK & byteTriplet);
			byteTriplet >>= 6;
			byte b1 = (byte) (SIX_BIT_MASK & byteTriplet);

			result.append(mapByteToChar(b1));
			result.append(mapByteToChar(b2));
			result.append(mapByteToChar(b3));

			// Add "==" to the output to make it a multiple of 4 Base64
			// characters
			result.append("=");
		}

		return result.toString();
	}

	/**
	 * Convert a byte between 0 and 63 to its Base64 character equivalent
	 * 
	 * @param b
	 *            Byte value to be converted
	 * @return Base64 char value
	 */
	private char mapByteToChar(byte b) {
		if (b < LOWER_CASE_A_VALUE) {
			return (char) ('A' + b);
		}

		if (b < ZERO_VALUE) {
			return (char) ('a' + (b - LOWER_CASE_A_VALUE));
		}

		if (b < PLUS_VALUE) {
			return (char) ('0' + (b - ZERO_VALUE));
		}

		if (b == PLUS_VALUE) {
			return '+';
		}

		if (b == SLASH_VALUE) {
			return '/';
		}

		throw new IllegalArgumentException("Byte " + new Integer(b)
				+ " is not a valid Base64 value");
	}

}
