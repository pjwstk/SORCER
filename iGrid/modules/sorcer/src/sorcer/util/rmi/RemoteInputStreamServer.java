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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.rmi.RemoteException;
import java.rmi.server.LogStream;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;

/**
 * RemoteInputStreamServer This exports all the useful methods of
 * <code>InputStream</code>, providing a means of retrieving data from a remote
 * source.
 * 
 * @see java.io.InputStream
 * 
 */

public class RemoteInputStreamServer extends UnicastRemoteObject implements
		RemoteInputStream, Unreferenced {
	public static final int BufferSize = 32768;

	static {
		// System.setProperty("java.rmi.server.logCalls","true");
	}

	private InputStream in;

	public RemoteInputStreamServer(InputStream in) throws RemoteException {
		super();
		this.in = in;
	}

	protected void finalize() throws Throwable {
		// NB note canonical form. Our own code must not throw; super.finalize()
		// may.
		try {
			LogStream.log(getServiceName()).println("Finalizing");
		} catch (Throwable t) {

		}
		super.finalize();
	}

	/**
	 * Determine the number of bytes that can be read.
	 * 
	 * @return the available byte count.
	 * @exception RemoteException
	 *                on any remot error
	 */
	public int available() throws RemoteException, IOException {
		return in.available();
	}

	/**
	 * Close this input stream.
	 * 
	 * @exception RemoteException
	 *                on any remote error
	 */
	public void close() throws RemoteException, IOException {
		System.out.println(this + ".close()");
		in.close();
	}

	/**
	 * Mark the current position in the input stream.
	 * 
	 * @param readLimit
	 *            the number of bytes that can be read before this mark is
	 *            invalidated.
	 * @exception RemoteException
	 *                on any remote error
	 */
	public void mark(int readLimit) throws RemoteException, IOException {
		in.mark(readLimit);
	}

	/**
	 * @return true iff mark() is supported at the remote.
	 * @exception RemoteException
	 *                on any remote error
	 */
	public boolean markSupported() throws RemoteException, IOException {
		return in.markSupported();
	}

	/**
	 * Read byte(s) from the input stream.
	 * 
	 * @return a byte array of size >= 0.
	 * @exception RemoteException
	 *                on any remote error
	 * @exception IOException
	 *                on any I/O error at the remote
	 */
	public byte[] readBytes(int count) throws RemoteException, IOException {
		System.out.print(this + ".readBytes(" + count + ")");
		int actual = 0;
		byte[] buffer = new byte[count];
		while ((actual = in.read(buffer)) == 0) {
			if (in instanceof PipedInputStream) {
				System.out.println(this + ".readBytes(): available()="
						+ in.available() + ": waiting for pipe");
				// LogStream.log("RemoteInputStream").println("waiting for pipe");
				try {
					synchronized (in) {
						in.wait();
					}
				} catch (InterruptedException e) {
					break;
				}
			} else
				break;
		}
		if (actual < 0)
			throw new EOFException();
		byte[] result = new byte[actual];
		System.arraycopy(buffer, 0, result, 0, actual);
		return result;
	}

	/*
	 * public int read() throws RemoteException, IOException { return in.read();
	 * }
	 * 
	 * // Useless because buffer is not returned public int read(byte[] buffer)
	 * throws RemoteException, IOException { return in.read(buffer); }
	 * 
	 * // Useless because buffer is not returned public int read(byte[]
	 * buffer,int offset,int count) throws RemoteException, IOException { return
	 * in.read(buffer,offset,count); }
	 */

	/**
	 * Reset the input stream.
	 * 
	 * @exception RemoteException
	 *                on any remote error
	 */
	public void reset() throws RemoteException, IOException {
		in.reset();
	}

	/**
	 * Skip bytes in the input stream.
	 * 
	 * @param count
	 *            Number of bytes to skip
	 * @exception RemoteException
	 *                on any remote error
	 */
	public long skip(long count) throws RemoteException, IOException {
		return in.skip(count);
	}

	public String getServiceName() {
		return getClass().getName();
	}

	// Unreferenced interface
	public void unreferenced() {
		LogStream.log(getServiceName()).println("idle");
	}

}
