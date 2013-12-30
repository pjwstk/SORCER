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

package sorcer.core;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import sorcer.util.DocumentDescriptor;

public interface DocumentFileStorer extends FileStorer, Remote {

	public Integer removeNode(DocumentDescriptor desc) throws RemoteException,
			IOException;

	/**
	 * create a folder
	 * 
	 * @param parent
	 *            path, node/folder name, can have properties in the future
	 * @return row count or 0
	 */

	public String createNode(DocumentDescriptor desc) throws RemoteException,
			IOException;

	public Map<String, Object> getProperties(DocumentDescriptor desc)
			throws RemoteException, IOException;

	public Map<String, Object> setProperties(DocumentDescriptor desc)
			throws RemoteException, IOException;

	/**
	 * paste a file/folder for a Move/Copy operation fwang
	 */
	public Integer paste(DocumentDescriptor desc) throws RemoteException,
			IOException;
}