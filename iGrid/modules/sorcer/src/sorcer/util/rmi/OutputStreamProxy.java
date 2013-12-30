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

package sorcer.util.rmi;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * Proxy for RemoteOutputStream
 */

public class OutputStreamProxy extends OutputStream implements Serializable {

	private RemoteOutputStream rout;

	public OutputStreamProxy(OutputStream out) throws RemoteException {
		this(new RemoteOutputStreamServer(out));
	}

	public OutputStreamProxy(RemoteOutputStream rout) {
		super();
		this.rout = rout;
	}

	public void close() throws IOException {
		rout.close();
		rout = null;
	}

	public void flush() throws IOException {
		rout.flush();
	}

	public void write(int oneByte) throws IOException {
		write(new byte[] { (byte) oneByte }, 0, 1);
	}

	public void write(byte[] buffer) throws IOException {
		write(buffer, 0, buffer.length);
	}

	public void write(byte[] buffer, int offset, int count) throws IOException {
		rout.write(buffer, offset, count);
	}

	public void write(File file) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				file));
		byte[] data = new byte[32768];
		int numBytes;
		while ((numBytes = bis.read(data, 0, 32768)) != -1)
			write(data, 0, numBytes);
		bis.close();
		close();
	}

	public void write(String[] str) throws IOException {
		if (str == null)
			return;
		for (int i = 0; i < str.length; i++)
			write(str[i].getBytes());
		close();
	}

}
