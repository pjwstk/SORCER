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

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class DataProtocolStream implements ProtocolStream {
	public DataInputStream in;
	public DataOutputStream out;

	/**
	 * Writes an 32-bit int to the underlying output stream.
	 * 
	 * @param v
	 *            - integer to be written.
	 * @return void
	 */
	public void writeInt(int v) throws java.io.IOException {
		out.writeInt(v);
	}

	/**
	 * Reads a signed 32-bit integer from this data input stream.
	 * 
	 * @return int
	 */
	public int readInt() throws java.io.IOException {
		return in.readInt();
	}

	/**
	 * Reads the next line of text from this protocol input stream.
	 * 
	 * @return String
	 */
	public String readLine() throws java.io.IOException {
		return in.readUTF();
	}

	/**
	 * Reads the next line of text from this protocol input stream, and escape
	 * new line charachter.
	 * 
	 * @return String
	 */
	public String readEscapedLine() throws java.io.IOException {
		return in.readUTF();
	}

	/**
	 * Writes a String, and then finish the line.
	 * 
	 * @param str
	 *            - a string to be written.
	 * @return void
	 */
	public void writeLine(String str) throws java.io.IOException {
		out.writeUTF(str);
	}

	public void writeObject(Object obj) throws java.io.IOException {
		// do Nothing.
	}

	public boolean isObjectStream() {
		return false;
	}

	/**
	 * Writes a String, and then finish the line.
	 * 
	 * @param str
	 *            - a string to be written.
	 * @return void
	 */
	public void writeEscapedLine(String str) throws java.io.IOException {
		out.writeUTF(str);
	}

	/**
	 * Flush the output stream.
	 */
	public void flush() throws java.io.IOException {
		out.flush();
	}

	/**
	 * Close the input and output streams.
	 */
	public void close() throws java.io.IOException {
		in.close();
		out.close();
	}

	/**
	 * Write a done mark.
	 */
	public void done() throws java.io.IOException {
		out.writeUTF("_DONE_");
	}
}
