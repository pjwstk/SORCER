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
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RemoteOutputStream interface This exports all the useful methods of
 * <code>OutputStream</code>, providing a means of writing data to a remote
 * target.
 *<p>
 * 
 * @see java.io.OutputStream
 * 
 */
public interface RemoteOutputStream extends Remote {
	/**
	 * Close this stream.
	 * 
	 * @exception RemoteException
	 *                on any remote error
	 * @exception IOException
	 *                on any I/O error
	 */
	public void close() throws RemoteException, IOException;

	/**
	 * Flush this stream.
	 * 
	 * @exception RemoteException
	 *                on any remote error
	 * @exception IOException
	 *                on any I/O error
	 */
	public void flush() throws RemoteException, IOException;

	/**
	 * Write to the output stream
	 * 
	 * @param oneByte
	 *            single byte to write
	 * @exception RemoteException
	 *                on any remote error
	 * @exception IOException
	 *                on any I/O error
	 */
	public void write(int oneByte) throws RemoteException, IOException;

	/**
	 * Write to the output stream
	 * 
	 * @param buffer
	 *            Bytes to write
	 * @exception RemoteException
	 *                on any remote error
	 * @exception IOException
	 *                on any I/O error
	 */
	public void write(byte[] buffer) throws RemoteException, IOException;

	/**
	 * Write to the output stream
	 * 
	 * @param buffer
	 *            Bytes to write
	 * @param offset
	 *            Offset within buffer of first byte to write
	 * @param count
	 *            Number of bytes to write.
	 * @exception RemoteException
	 *                on any remote error
	 * @exception IOException
	 *                on any I/O error
	 */
	public void write(byte[] buffer, int offset, int count)
			throws RemoteException, IOException;

}
