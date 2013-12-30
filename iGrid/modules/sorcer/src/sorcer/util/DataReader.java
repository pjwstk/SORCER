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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * DataReader creates a lineBuffer (Vector) from a URL, file or string
 */
public class DataReader {
	public static int port;
	public static String host;
	public static boolean notLimitedBuffer = true;
	public static String delimiter = ",";
	public Vector lineBuffer;
	private DataInputStream inStream = null;
	private static int bufSize = 100;
	private int currentLine;
	private int rowSetSize;
	private int lineCount;
	private boolean EOFReached;
	private boolean EOF;
	private boolean BOF;

	public DataReader(URL documentBase, String filename, int bufferSize) {
		URL url = null;
		URLConnection connection = null;
		rowSetSize = bufferSize;

		try {
			if (filename != null)
				url = new URL(documentBase, filename);
			else
				url = documentBase;

			connection = url.openConnection();

			// prepare to access URL that is text file
			inStream = new DataInputStream(new BufferedInputStream(connection
					.getInputStream()));
		} catch (MalformedURLException e1) {
			System.err.println("Invalid URL" + documentBase + "/" + filename);
			e1.printStackTrace();
		} catch (IOException e2) {
			System.err.println("Could not open URL" + documentBase + "/"
					+ filename);
			e2.printStackTrace();
		} catch (SecurityException e3) {
			System.err.println("Security exception " + e3.getMessage());
			e3.printStackTrace();
		}
		lineCount = 0;
		currentLine = 0;
		EOFReached = false;
		lineBuffer = new Vector(rowSetSize, rowSetSize);
		readRowSet();
	}

	public DataReader(URL documentBase, String filename) {
		this(documentBase, filename, bufSize);
	}

	public DataReader(URL url) {
		this(url, null);
	}

	public DataReader(File file, int bufferSize) {
		try {
			inStream = new DataInputStream(new FileInputStream(file));
		} catch (IOException e) {
			System.out.println("Could not open a file: " + file.getName());
			System.out.println("Exception " + e.getMessage());
			System.exit(0);
		}

		rowSetSize = bufferSize;
		lineCount = 0;
		currentLine = 0;
		EOFReached = false;
		lineBuffer = new Vector(rowSetSize, rowSetSize);
		readRowSet();
	}

	public DataReader(File file) {
		this(file, bufSize);
	}

	public DataReader(Vector buffer) {
		rowSetSize = buffer.size();
		lineCount = 0;
		currentLine = 0;
		EOFReached = false;
		lineBuffer = buffer;
	}

	public DataReader(String data, int bufferSize) {
		inStream = new DataInputStream(new StringBufferInputStream(data));
		rowSetSize = bufferSize;
		lineCount = 0;
		currentLine = 0;
		EOFReached = false;
		lineBuffer = new Vector(rowSetSize, rowSetSize);
		readRowSet();
	}

	public DataReader(String data) {
		this(data, bufSize);
	}

	public void addRow(String data) {
		inStream = new DataInputStream(new StringBufferInputStream(data));
		EOFReached = false;
		readRowSet();
	}

	public String deleteRow(int rowNumber) {
		BOF = false;
		EOF = false;
		String row = null;

		if ((rowNumber > lineCount) & (EOFReached)) {
			EOF = true;
			return null;
		} else {
			row = (String) lineBuffer.elementAt(rowNumber - 1);
			lineBuffer.removeElementAt(rowNumber - 1);
			lineCount--;
			return row;
		}
	}

	private void readRowSet() {
		int setCount = 0;
		String s = null;

		if (notLimitedBuffer) {
			try {
				while (!EOFReached) {
					if ((s = inStream.readLine()) != null) {
						lineBuffer.addElement(s);
						lineCount++;
						setCount++;
					} else
						EOFReached = true;
				}
			} catch (IOException e) {
				System.out.println("File Read Error");
			}
		} else {
			try {
				while ((setCount < rowSetSize) & (!EOFReached)) {
					if ((s = inStream.readLine()) != null) {
						lineBuffer.addElement(s);
						lineCount++;
						setCount++;
					} else
						EOFReached = true;
				}
			} catch (IOException e) {
				System.out.println("File Read Error");
			}
		}
	}

	public String nextline() {
		BOF = false;
		EOF = false;

		if (currentLine < lineCount) {
			currentLine++;
			return (String) lineBuffer.elementAt(currentLine - 1);
		} else {
			readRowSet();
			if (EOFReached) {
				EOF = true;
				return null;
			} else {
				currentLine++;
				return (String) lineBuffer.elementAt(currentLine - 1);
			}
		}
	}

	public String prevline() {
		BOF = false;
		EOF = false;
		currentLine--;
		if (currentLine >= 0) {
			return (String) lineBuffer.elementAt(currentLine);
		} else {
			currentLine--;
			BOF = true;
			return "";
		}
	}

	public String getRow(int rowNumber) {
		BOF = false;
		EOF = false;

		while ((rowNumber > lineCount) & (!EOFReached)) {
			readRowSet();
		}
		if ((rowNumber > lineCount) & (EOFReached)) {
			EOF = true;
			return null;
		} else {
			return (String) lineBuffer.elementAt(rowNumber - 1);
		}
	}

	public String[] getRowArray(int rowNumber) {

		String row = getRow(rowNumber);
		if (row == null)
			return null;
		else {
			StringTokenizer tokenString = new StringTokenizer(row, delimiter);
			int numberOfTokens = tokenString.countTokens();
			String[] tokens = new String[numberOfTokens];
			for (int i = 0; i < numberOfTokens; i++)
				tokens[i] = tokenString.nextToken();
			return tokens;
		}
	}

	public boolean updateRow(int rowNumber, String theRow) {
		if (rowNumber > lineCount) {
			return false;
		}
		lineBuffer.removeElementAt(rowNumber - 1);
		lineBuffer.insertElementAt(theRow, rowNumber - 1);
		return true;
	}

	public boolean updateRow(int rowNumber, String[] sArray) {
		String theRow = null;

		if (rowNumber > lineCount) {
			return false;
		}
		for (int i = 0; i < sArray.length; i++)
			theRow.concat(sArray[i]).concat(delimiter);
		lineBuffer.removeElementAt(rowNumber - 1);
		lineBuffer.insertElementAt(theRow, rowNumber - 1);
		return true;
	}

	public void close() {
		try {
			inStream.close();
		} catch (IOException e) {
			System.out.println("File Close Error");
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < lineBuffer.size(); i++) {
			sb.append((String) lineBuffer.elementAt(i));
			sb.append("\n");
		}
		return sb.toString();
	}

	public static String cgiPOST(String script, String data) {
		String home = DataReader.host;
		int port = DataReader.port;
		Socket s = null;
		String rdata = "";

		try {
			s = new Socket(home, port);

			DataOutputStream os = new DataOutputStream(s.getOutputStream());
			DataInputStream is = new DataInputStream(s.getInputStream());

			os.writeBytes("POST " + script + " HTTP/1.0\r\n"
					+ "Content-type: application/octet-stream\r\n"
					+ "Content-length: " + data.length() + "\r\n\r\n");
			os.writeBytes(data);

			String line;
			while ((line = is.readLine()) != null)
				rdata += line + "\n";
			is.close();
			os.close();
		} catch (Exception e) {
			System.out.println("Error " + e);
			if (s != null)
				try {
					s.close();
				} catch (IOException ex) {
				}
		}
		return rdata;
	}

	public static String cgiGET(String script, String args) {
		String home = DataReader.host;
		int port = DataReader.port;
		Socket s = null;
		String rdata = "";

		try {
			s = new Socket(home, port);
			DataOutputStream os = new DataOutputStream(s.getOutputStream());
			DataInputStream is = new DataInputStream(s.getInputStream());

			if (args.length() == 0)
				os.writeBytes("GET " + script + "\r\n\r\n");
			else
				os.writeBytes("GET " + script + "?" + args + "\r\n\r\n");

			String line;
			while ((line = is.readLine()) != null)
				rdata += line + "\n";
			is.close();
			os.close();
		} catch (Exception e) {
			System.out.println("Error " + e);
			if (s != null)
				try {
					s.close();
				} catch (IOException ex) {
				}
		}
		return rdata;
	}

	public Hashtable getSQLQueries() {
		Hashtable sqlList = new Hashtable();
		String lineString, keyString = null;
		StringBuffer sqlString = new StringBuffer(512);
		int sep;

		for (int i = 0; i < lineBuffer.size(); i++) {
			lineString = (String) lineBuffer.elementAt(i);
			if (lineString.indexOf(":") == -1) {
				lineString.trim();
				sqlString.append(lineString).append(" ");
			} else {
				if (keyString != null) {
					sqlList.put(keyString, sqlString.toString());
					sqlString.setLength(0);
				}
				sep = lineString.indexOf(":");
				keyString = lineString.substring(0, sep);
				sqlString.append(lineString.substring(sep + 1)).append(" ");
			}
		}
		sqlList.put(keyString, sqlString.toString());
		return sqlList;
	}
}
