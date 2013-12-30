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
 * RemoteInputStream interface This exports all the useful methods of
 * <code>InputStream</code>, providing a means of retrieving data from a remote
 * source.
 *<p>
 * The read() method differs from the classical InputStream set, because RMI
 * does not allow for in/out parameters, so supplying a buffer to be read into
 * cannot work. Instead, the read() API returns a byte[] of a length decided by
 * it, or throws an IOException.
 *<p>
 * mark/reset are not supported.
 * 
 * @see java.io.InputStream
 * 
 */

public interface RemoteInputStream extends Remote {
	/**
	 * Determine the number of bytes that can be read.
	 * 
	 * @return the available byte count.
	 * @exception RemoteException
	 *                on any remote error
	 */
	public int available() throws RemoteException, IOException;

	/**
	 * Close this input stream.
	 * 
	 * @exception RemoteException
	 *                on any remote error
	 */
	public void close() throws RemoteException, IOException;

	/**
	 * Mark the current position in the input stream.
	 * 
	 * @param readLimit
	 *            the number of bytes that can be read before this mark is
	 *            invalidated.
	 * @exception RemoteException
	 *                on any remote error
	 */
	// public void mark(int readLimit)
	// throws RemoteException,
	// IOException;

	/**
	 * @return true iff mark() is supported at the remote.
	 * @exception RemoteException
	 *                on any remote error
	 */
	// public boolean markSupported()
	// throws RemoteException,
	// IOException;

	/**
	 * Read byte(s) from the input stream. Returns a byte array of size 0 or
	 * more, where 0 indicates EOF. (java.il.EOFException is not thrown.)
	 * 
	 * @return a byte array of size >= 0.
	 * @param count
	 *            Maximum number of bytes to read; the returned buffer may be
	 *            smaller or 0-length.
	 * @exception RemoteException
	 *                on any remote error
	 * @exception IOException
	 *                on any I/O error at the remote
	 */
	public byte[] readBytes(int count) throws RemoteException, IOException;

	/**
	 * Reset the input stream.
	 * 
	 * @exception RemoteException
	 *                on any remote error
	 */
	// public void reset()
	// throws RemoteException,
	// IOException;

	/**
	 * Skip bytes in the input stream.
	 * 
	 * @param count
	 *            Number of bytes to skip
	 * @return the number of bytes actually skipped
	 * @exception RemoteException
	 *                on any remote error
	 */
	public long skip(long count) throws RemoteException, IOException;

}
