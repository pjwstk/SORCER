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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * Adapter for RemoteInputStream. NB This is an Adapter not a Proxy because the
 * interface is different.
 * 
 */

public class InputStreamAdapter extends InputStream implements Serializable {

	private RemoteInputStream rin;

	public InputStreamAdapter(InputStream in) throws RemoteException {
		this(new RemoteInputStreamServer(in));
	}

	public InputStreamAdapter(RemoteInputStream rin) {
		super();
		this.rin = rin;
	}

	public int available() throws IOException {
		return rin.available();
	}

	public void close() throws IOException {
		rin.close();
		rin = null;
	}

	public int read() throws IOException {
		byte[] buffer = new byte[1];
		int result = read(buffer);
		if (result > 0)
			result = buffer[0] & 0xff;
		return result;
	}

	public int read(byte[] buffer) throws IOException {
		return read(buffer, 0, buffer.length);
	}

	public int read(byte[] buffer, int offset, int count) throws IOException {
		byte[] result = rin.readBytes(count);
		// System.out.println(this+".read(buffer,"+offset+","+count+")="+result.length);
		System.arraycopy(result, 0, buffer, offset, result.length);
		return result.length;
	}

	public void read(File file) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(file));
		byte[] data = new byte[32768];
		int numBytes;
		try {
			while ((numBytes = read(data, 0, 32768)) != -1) {
				// System.out.println("______readBytes"+Debug.arrayToString(data));
				// System.out.flush();
				bos.write(data, 0, numBytes);
			}
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			bos.close();
		}
		close();
	}

	public long skip(long count) throws IOException {
		return rin.skip(count);
	}
}
