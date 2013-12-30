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

import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.rmi.server.LogStream;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;

/**
 * RemoteOutputStreamServer This exports all the useful methods of
 * <code>OutputStream</code>, providing a means of writing data to a remote
 * target.
 * 
 * @see java.io.OutputStream
 * 
 */
public class RemoteOutputStreamServer extends UnicastRemoteObject implements
		RemoteOutputStream, Unreferenced {
	public static final int BufferSize = 32768;

	private OutputStream out;

	public RemoteOutputStreamServer(OutputStream out) throws RemoteException {
		super();
		this.out = out;
	}

	/*
	 * protected void finalize() throws Throwable { // NB note canonical form.
	 * Our own code must not throw; super.finalize() may. try { //
	 * LogStream.log(getServiceName()).println("Finalizing"); } catch (Throwable
	 * t) { } super.finalize(); }
	 */

	/**
	 * Close this stream.
	 * 
	 * @exception RemoteException
	 *                on any remote error
	 */
	public void close() throws RemoteException, IOException {
		// System.out.println(this+".close()");
		out.close();
	}

	/**
	 * Flush this stream.
	 * 
	 * @exception RemoteException
	 *                on any remote error
	 */
	public void flush() throws RemoteException, IOException {
		// System.out.println(this+".flush()");
		out.flush();
	}

	/**
	 * Write to the output stream.
	 * 
	 * @exception RemoteException
	 *                on any remote error
	 * @exception IOException
	 *                on any I/O error at the remote
	 */
	public void write(int oneByte) throws RemoteException, IOException {
		out.write(oneByte);
	}

	/**
	 * Write to the output stream.
	 * 
	 * @exception RemoteException
	 *                on any remote error
	 * @exception IOException
	 *                on any I/O error at the remote
	 */
	public void write(byte[] buffer) throws RemoteException, IOException {
		out.write(buffer);
	}

	/**
	 * Write to the output stream.
	 * 
	 * @exception RemoteException
	 *                on any remote error
	 * @exception IOException
	 *                on any I/O error at the remote
	 */
	public void write(byte[] buffer, int offset, int count)
			throws RemoteException, IOException {
		out.write(buffer, offset, count);
	}

	public String getServiceName() {
		return getClass().getName();
	}

	// Unreferenced interface
	public void unreferenced() {
		LogStream.log(getServiceName()).println("idle");
	}

}
