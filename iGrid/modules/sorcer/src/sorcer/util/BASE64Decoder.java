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
 * Utility class to do Base64 decoding, as defined by RFC 2045, section 6.8
 * (http://www.ietf.org/rfc/rfc2045.txt) Uses the same class and function names
 * as Sun's implementation from sun.misc
 */
public class BASE64Decoder {

	/**
	 * Bit mask for one byte worth of bits in Base64 encoding. Equivalent to
	 * binary value 11111111b.
	 */
	private static final int EIGHT_BIT_MASK = 0xFF;

	/**
	 * Decode an input String using Base64
	 * 
	 * @param data
	 *            The String to be decoded
	 * @return The appropriate byte array
	 */
	public byte[] decodeBuffer(String data) {
		// Create a wrapper around the input to screen out non-Base64 characters
		StringWrapper wrapper = new StringWrapper(data);
		// A Base64 byte array is 75% the size of its String representation
		int byteArrayLength = wrapper.getUsefulLength() * 3 / 4;

		byte result[] = new byte[byteArrayLength];

		int byteTriplet = 0;
		int byteIndex = 0;

		// Continue until we have less than 4 full characters left to
		// decode in the input.
		while (byteIndex + 2 < byteArrayLength) {

			// Package a set of four characters into a byte triplet
			// Each character contributes 6 bits of useful information
			byteTriplet = mapCharToInt(wrapper.getNextUsefulChar());
			byteTriplet <<= 6;
			byteTriplet |= mapCharToInt(wrapper.getNextUsefulChar());
			byteTriplet <<= 6;
			byteTriplet |= mapCharToInt(wrapper.getNextUsefulChar());
			byteTriplet <<= 6;
			byteTriplet |= mapCharToInt(wrapper.getNextUsefulChar());

			// Grab a normal byte (eight bits) out of the byte triplet
			// and put it in the byte array
			result[byteIndex + 2] = (byte) (byteTriplet & EIGHT_BIT_MASK);
			byteTriplet >>= 8;
			result[byteIndex + 1] = (byte) (byteTriplet & EIGHT_BIT_MASK);
			byteTriplet >>= 8;
			result[byteIndex] = (byte) (byteTriplet & EIGHT_BIT_MASK);
			byteIndex += 3;
		}

		// Check if we have one byte left to decode
		if (byteIndex == byteArrayLength - 1) {
			// Take out the last two characters from the String
			byteTriplet = mapCharToInt(wrapper.getNextUsefulChar());
			byteTriplet <<= 6;
			byteTriplet |= mapCharToInt(wrapper.getNextUsefulChar());

			// Remove the padded zeros
			byteTriplet >>= 4;
			result[byteIndex] = (byte) (byteTriplet & EIGHT_BIT_MASK);
		}

		// Check if we have two bytes left to decode
		if (byteIndex == byteArrayLength - 2) {
			// Take out the last three characters from the String
			byteTriplet = mapCharToInt(wrapper.getNextUsefulChar());
			byteTriplet <<= 6;
			byteTriplet |= mapCharToInt(wrapper.getNextUsefulChar());
			byteTriplet <<= 6;
			byteTriplet |= mapCharToInt(wrapper.getNextUsefulChar());

			// Remove the padded zeros
			byteTriplet >>= 2;
			result[byteIndex + 1] = (byte) (byteTriplet & EIGHT_BIT_MASK);
			byteTriplet >>= 8;
			result[byteIndex] = (byte) (byteTriplet & EIGHT_BIT_MASK);
		}

		return result;
	}

	/**
	 * Convert a Base64 character to its 6 bit value as defined by the mapping.
	 * 
	 * @param c
	 *            Base64 character to decode
	 * @return int representation of 6 bit value
	 */
	private int mapCharToInt(char c) {
		if (c >= 'A' && c <= 'Z') {
			return c - 'A';
		}

		if (c >= 'a' && c <= 'z') {
			return (c - 'a') + BASE64Encoder.LOWER_CASE_A_VALUE;
		}

		if (c >= '0' && c <= '9') {
			return (c - '0') + BASE64Encoder.ZERO_VALUE;
		}

		if (c == '+') {
			return BASE64Encoder.PLUS_VALUE;
		}

		if (c == '/') {
			return BASE64Encoder.SLASH_VALUE;
		}

		throw new IllegalArgumentException(c
				+ " is not a valid Base64 character.");
	}

	/**
	 * Simple class to wrap around the String input to ignore all of the
	 * non-Base64 characters in the input. Note that although '=' is a valid
	 * character, it does not contribute to the total number of output bytes,
	 * and is therefore ignored
	 */
	private class StringWrapper {

		/**
		 * The input String to be decoded
		 */
		private String mString;

		/**
		 * Current position in the String
		 */
		private int mIndex = 0;

		/**
		 * Total number of Base64 characters in the input
		 */
		private int mUsefulLength;

		/**
		 * @param c
		 *            Character to be examined
		 * @return Whether or not the character is a Base64 character
		 */
		private boolean isUsefulChar(char c) {
			return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
					|| (c >= '0' && c <= '9') || (c == '+') || (c == '/');
		}

		/**
		 * Create the wrapper and determine the number of Base64 characters in
		 * the input
		 * 
		 * @param s
		 *            Input String to be decoded
		 */
		public StringWrapper(String s) {
			mString = s;
			mUsefulLength = 0;
			int length = mString.length();
			for (int i = 0; i < length; i++) {
				if (isUsefulChar(mString.charAt(i))) {
					mUsefulLength++;
				}
			}
		}

		/**
		 * @return Total number of Base64 characters in the input. Does not
		 *         include '='
		 */
		public int getUsefulLength() {
			return mUsefulLength;
		}

		/**
		 * Traverse the String until hitting the next Base64 character. Assumes
		 * that there is still another valid Base64 character left in the
		 * String.
		 */
		public char getNextUsefulChar() {
			char result = '_'; // Start with a non-Base64 character
			while (!isUsefulChar(result)) {
				result = mString.charAt(mIndex++);
			}

			return result;
		}
	}
}
