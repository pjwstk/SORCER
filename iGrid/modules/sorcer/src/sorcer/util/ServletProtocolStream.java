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

import java.io.BufferedReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

public class ServletProtocolStream implements ProtocolStream {
	public BufferedReader in;
	public PrintWriter out;
	public ObjectOutputStream outStream;

	/**
	 * Writes an 32-bit int to the underlying output stream.
	 * 
	 * @param v
	 *            - integer to be written.
	 * @return void
	 */
	public void writeInt(int v) throws java.io.IOException {
		out.println(String.valueOf(v));
	}

	/**
	 * Reads a signed 32-bit integer from this data input stream.
	 * 
	 * @return int
	 */
	public int readInt() throws java.io.IOException {
		return Integer.parseInt(in.readLine());
	}

	/**
	 * Reads the next line of text from this protocol input stream.
	 * 
	 * @return String
	 */
	public String readLine() throws java.io.IOException {
		return in.readLine();
	}

	/**
	 * Reads the next line of text from this protocol input stream, and escape
	 * new line charachter.
	 * 
	 * @return String
	 */
	public String readEscapedLine() throws java.io.IOException {
		return SorcerUtil.escapeReturns(in.readLine());
	}

	/**
	 * Writes a String, and then finish the line.
	 * 
	 * @param str
	 *            - a string to be written.
	 * @return void
	 */
	public void writeLine(String str) throws java.io.IOException {
		if (out != null)
			out.println(str);
		else if (outStream != null)
			outStream.writeObject(str);
	}

	public void writeObject(Object obj) throws java.io.IOException {
		outStream.writeObject(obj);
	}

	public boolean isObjectStream() {
		return (outStream != null);
	}

	/**
	 * Writes a String with escaped line delimiters, and then finish the line.
	 * 
	 * @param str
	 *            - a string to be written.
	 * @return void
	 */
	public void writeEscapedLine(String str) throws java.io.IOException {
		out.println(SorcerUtil.escapeReturns(str));
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
		// do nothing, handled by writeProtocolData in ApplicationServlet
	}

	/**
	 * Write a done mark.
	 */
	public void done() throws java.io.IOException {
		if (outStream != null)
			outStream.writeObject("_DONE_");
		else
			out.println("_DONE_");
	}
}
