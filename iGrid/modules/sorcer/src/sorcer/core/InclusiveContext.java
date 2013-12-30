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

import java.util.Iterator;

import sorcer.service.Context;
import sorcer.service.ContextException;

/**
 * The InclusiveContext is an interface designed to enable CDS providers to
 * encapsulate the query for data from multiple EDS providers into a single
 * request. This inclusive context is also designed to be a container for the
 * view of data which satisfies the query. The InclusiveContext can return an
 * iterator which can iterate through the tuples which satisfy the query. It
 * also returns a metadata object which basically contains information about the
 * attributes of the query. Additionally, the InclusiveContext can return an
 * object which can return each attribute in various data type formats as
 * requested.
 * 
 * @author Thimmayya Ame
 */

public interface InclusiveContext extends Context {

	/**
	 * This method enables the user to give a different context-specific name to
	 * an existing path. The map gives the user the ability to specify names for
	 * attributes depending on the context of the query
	 * 
	 * @param newPath
	 *            specify new name for path
	 * @param oldPath
	 *            name of old path
	 */
	public void addKeyToMap(String newPath, String oldPath);

	/**
	 * Method to remove a given path from the Map.
	 * 
	 * @param path
	 *            name of old path
	 */
	public void removePathFromMap(String path)
			throws ComprehensiveContextException;

	// ******************************************************************
	// Other methods already implemented in the Impl should be added here
	// ******************************************************************
	/**
	 * This method enables the user to give a different context-specific name to
	 * an existing path. The map gives the user the ability to specify names for
	 * attributes depending on the context of the query
	 * 
	 * @param newPath
	 *            specify new name for path
	 * @param oldPath
	 *            name of old path
	 * @param value
	 *            value to which this path is associated
	 * @return
	 */
	public Object put(String newPath, String oldPath, Object value)
			throws ContextException, ComprehensiveContextException;

	/**
	 * Changes the index of this particular attribute in the query represented.
	 * by this comprehensivecontext
	 * 
	 * @param path
	 *            name of the path
	 * @param index
	 *            index of the path
	 * @return
	 */
	public boolean changeIndex(String path, int index)
			throws ComprehensiveContextException;

	/**
	 * Changes the index of this particular attribute in the query represented.
	 * by this comprehensivecontext
	 * 
	 * @param oldIndex
	 *            old index of the path
	 * @param newIndex
	 *            new index of the path
	 * @return true if successful
	 */
	public boolean changeIndex(int oldIndex, int newIndex)
			throws ComprehensiveContextException;

	/**
	 * Gets the value at the given index.
	 * 
	 * @param index
	 *            index of the path
	 * @return
	 */
	public Object getValueAtIndex(int index)
			throws ComprehensiveContextException;

	/**
	 * Gets the value for the given path in the comprehensivecontext.
	 * 
	 * @param path
	 *            name of path for which the value is to be retrieved
	 * @return the value at the specified path
	 */
	public Object getValue(String path);

	/**
	 * Sets the index of a given path.
	 * 
	 * @param path
	 *            the name of the path whose index is to be retrieved
	 * @return
	 */
	public int getIndex(String path) throws ComprehensiveContextException;

	/**
	 * Gets the mapped path at the given index.
	 * 
	 * @param index
	 *            index of the path
	 * @return the mapped path for the specified index
	 */
	public String getPathAtIndex(int index)
			throws ComprehensiveContextException;

	/**
	 * Gets the real path at the given index.
	 * 
	 * @param index
	 *            index of the path
	 * @return the real path for the specified index
	 */
	public String getRealPathAtIndex(int index)
			throws ComprehensiveContextException;

	/**
	 * Gets the real path for the mapped path.
	 * 
	 * @param path
	 *            specify the name of the path whose real path is to be
	 *            retrieved
	 * @return the real path for the specified mapped path
	 */
	public String getRealPath(String path) throws ComprehensiveContextException;

	/**
	 * Returns the number of paths stored.
	 * 
	 * @return number of paths
	 */
	public int size();

	/**
	 * Returns the iterator for this comprehensivecontext. The iterator allows
	 * the user to retrieve the data satisfying the query row per row.
	 * 
	 * @return the iterator for this ComprehensiveContext.
	 */
	public Iterator iterator();

	/**
	 * Returns a ContextMetaData object which contains metadata info about this
	 * comprehensivecontext.
	 * 
	 * @return metadata about this ComprehensiveContext
	 */
	public ContextMetaData getContextMetaData();

	// public void setIterator(Iterator iterator);
}
