/*
 * Copyright 2009 the original author or authors.
 * Copyright 2009 SorcerSoft.org.
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

package sorcer.core.context;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import sorcer.core.ComprehensiveContextException;
import sorcer.core.ContextMetaData;
import sorcer.core.InclusiveContext;
import sorcer.core.context.node.ContextNode;
import sorcer.service.ContextException;

/*******************************************************************************
 * BUG FIX: If we try to add the same pathname twice it does
 * not add properly. Need to check if the newPath already exists and change what
 * it does currently, so that two paths with the same name do not exist.
 *******************************************************************************
 */

/**
 * The Comprehensive Context is a data structure designed to enable CDS
 * providers to encapsulate the query for data from multiple EDS providers into
 * a single request. This data structure is also designed to be a container for
 * the view of data which satisfies the query. The ComprehensiveContext can
 * return an iterator which can iterate through the tuples which satisfy the
 * query. It also returns a metaata object which basically contains information
 * about the attributes of the query. Additionally, the ComprehensiveContext can
 * return a object which can return each attributed in various data type formats
 * as requested.
 * 
 * @author Thimmayya Ame
 */
public class ComprehensiveContext extends ServiceContext implements
		InclusiveContext {

	// Mapping newPathNames to oldPathNames
	private Hashtable contextMap;

	// To maintain a integer index for each path.
	// Requestor may ask to the ComprehensiveContext to an element at a
	// particular index;
	private Vector pathIndex = new Vector(10, 10);

	private ComprehensiveContextIterator compContextIterator;

	private ContextMetaDataImpl contextMetaData;

	/**
	 * @param String
	 *            name of the context
	 * 
	 */
	public ComprehensiveContext(String name) {
		super(name);
		contextMap = new Hashtable();
	}

	/**
	 * Method to set the iterator for this ComprehensiveContext
	 * 
	 * @param ComprehensiveContextIterator
	 *            the implementation of the iterator for the comprehensive
	 *            context
	 * 
	 */
	public void setContextIterator(
			ComprehensiveContextIterator compContextIterator) {
		this.compContextIterator = compContextIterator;
	}

	/**
	 * Method to get the iterator for this ComprehensiveContext
	 * 
	 * @return Iterator iterator for this ComprehensiveContext
	 */
	public Iterator iterator() {
		return (Iterator) this.compContextIterator;
	}

	/**
	 * returns a ContextMetaData object which contains the ComprehensiveContext
	 * associated with this iterator Sending a reference to the private
	 * compContext in the ContextMetaData constructor partially violates the
	 * principles of Immutability. Future improvement suggests usage of
	 * compContext.clone() method. @ return ContextMetaData
	 */
	public ContextMetaData getContextMetaData() {
		return (ContextMetaData) (new ContextMetaDataImpl(this));
	}

	public void addKeyToMap(String newPath, String oldPath) {
		// System.out.println("=====> Put path in Map =" + newPath);
		contextMap.put(newPath, oldPath);
		// System.out.println("=====> Got put path in Map = " +
		// contextMap.get(newPath) );
	}

	// Override put(Object,Object) method of superclasses to ensure that the Map
	// is maintained
	//@Override
	public Object put(String path, Object value) {
		if (!(value instanceof ContextNode))
				throw new UnsupportedOperationException(
						"The value inserted should always be a ContextNode"
								+ " which contains attributes of the data to be stored.");
			
		if (!contextMap.containsKey(path))
			addKeyToMap(path, path);
		pathIndex.add(path);
		return super.put(path, value);
	}

	public Object put(int location, String path, Object value)
			throws ContextException, ComprehensiveContextException {
		if (!(value instanceof ContextNode))
			throw new ComprehensiveContextException(
					"The value inserted should always be a ContextNode"
							+ " which contains attributes of the data to be stored.");
		if (!contextMap.containsKey(path))
			addKeyToMap(path, path);
		pathIndex.insertElementAt(path, location);
		return super.put(path, value);
	}

	public Object put(String newPath, String oldPath, Object value)
			throws ContextException, ComprehensiveContextException {
		if (!(value instanceof ContextNode))
			throw new ComprehensiveContextException(
					"The value inserted should always be a ContextNode"
							+ " which contains attributes of the data to be stored.");
		if (!contextMap.containsKey(newPath))
			addKeyToMap(newPath, oldPath);
		pathIndex.add(newPath);
		return super.put(newPath, value);
	}

	public Object put(int location, String newPath, String oldPath, Object value)
			throws ContextException, ComprehensiveContextException {
		if (!(value instanceof ContextNode))
			throw new ComprehensiveContextException(
					"The value inserted should always be a ContextNode"
							+ " which contains attributes of the data to be stored.");
		if (!contextMap.containsKey(newPath))
			addKeyToMap(newPath, oldPath);
		pathIndex.insertElementAt(newPath, location);
		return super.put(newPath, value);
	}

	public Object putValue(String path, Object value) throws ContextException {
		if (!(value instanceof ContextNode))
			throw new ContextException(
					"The value inserted into a ComprehensiveContext should always be a ContextNode"
							+ " which contains attributes of the data to be stored.");
		if (!contextMap.containsKey(path))
			addKeyToMap(path, path);
		pathIndex.add(path);
		return super.putValue(path, value);
	}

	public Object putValue(int location, String path, Object value)
			throws ContextException, ComprehensiveContextException {
		if (!(value instanceof ContextNode))
			throw new ComprehensiveContextException(
					"The value inserted into a ComprehensiveContext should always be a ContextNode"
							+ " which contains attributes of the data to be stored.");
		if (!contextMap.containsKey(path))
			addKeyToMap(path, path);
		pathIndex.insertElementAt(path, location);
		return super.putValue(path, value);
	}

	public Object putValue(String newPath, String oldPath, Object value)
			throws ContextException, ComprehensiveContextException {
		if (!(value instanceof ContextNode))
			throw new ComprehensiveContextException(
					"The value inserted should always be a ContextNode"
							+ " which contains attributes of the data to be stored.");
		if (!contextMap.containsKey(newPath))
			addKeyToMap(newPath, oldPath);
		pathIndex.add(newPath);
		return super.putValue(newPath, value);
	}

	public Object putValue(int location, String newPath, String oldPath,
			Object value) throws ContextException,
			ComprehensiveContextException {
		if (!(value instanceof ContextNode))
			throw new ComprehensiveContextException(
					"The value inserted should always be a ContextNode"
							+ " which contains attributes of the data to be stored.");
		if (!contextMap.containsKey(newPath))
			addKeyToMap(newPath, oldPath);
		pathIndex.insertElementAt(newPath, location);
		return super.putValue(newPath, value);
	}

	public Object putValue(String path, Object value, String association)
			throws ContextException {
		if (!(value instanceof ContextNode))
			throw new ContextException(
					"The value inserted into a ComprehensiveContext should always be a ContextNode"
							+ " which contains attributes of the data to be stored.");
		if (!contextMap.containsKey(path))
			addKeyToMap(path, path);
		pathIndex.add(path);
		return super.putValue(path, value, association);
	}

	public Object putValue(int location, String path, Object value,
			String association) throws ContextException,
			ComprehensiveContextException {
		if (!(value instanceof ContextNode))
			throw new ComprehensiveContextException(
					"The value inserted should always be a ContextNode"
							+ " which contains attributes of the data to be stored.");
		if (!contextMap.containsKey(path))
			addKeyToMap(path, path);
		pathIndex.insertElementAt(path, location);
		return super.putValue(path, value, association);
	}

	public Object putValue(String newPath, String oldPath, Object value,
			String association) throws ContextException,
			ComprehensiveContextException {
		if (!(value instanceof ContextNode))
			throw new ComprehensiveContextException(
					"The value inserted should always be a ContextNode"
							+ " which contains attributes of the data to be stored.");
		if (!contextMap.containsKey(newPath))
			addKeyToMap(newPath, oldPath);
		pathIndex.add(newPath);
		return super.putValue(newPath, value, association);
	}

	public Object putValue(int location, String newPath, String oldPath,
			Object value, String association) throws ContextException,
			ComprehensiveContextException {
		if (!(value instanceof ContextNode))
			throw new ComprehensiveContextException(
					"The value inserted should always be a ContextNode"
							+ " which contains attributes of the data to be stored.");
		if (!contextMap.containsKey(newPath))
			addKeyToMap(newPath, oldPath);
		pathIndex.insertElementAt(newPath, location);
		return super.putValue(newPath, value, association);
	}

	public String getRealPath(String path) throws ComprehensiveContextException {
		// Util.debug(this,"=====>Got path from Map = " +
		// (String)contextMap.get(path));
		System.out.println("request path" + path);
		System.out.println("=====>Got path from Map = "
				+ (String) contextMap.get(path));
		if (contextMap.containsKey(path))
			return (String) contextMap.get(path);
		else
			throw new ComprehensiveContextException(" No such Path in Map");
	}

	// Removes the value that was stored for this path. The map b/w oldPath and
	// newPath is removed too.
	public void removePathFromMap(String path) {
		try {
			for (int i = 0; i < pathIndex.size(); i++)
				if (((String) pathIndex.elementAt(i)).equals(path))
					pathIndex.removeElementAt(i);
			String rPath = getRealPath(path);
			contextMap.remove(path);
			super.removePath(path);
		} catch (ContextException ce) {
			ce.printStackTrace();
		} catch (ComprehensiveContextException cce) {
			cce.printStackTrace();
		}
		return;
	}

	// Overrides Hashtable remove() method to maintain indices
	// public void remove(Object value){
	// String path = value.toString();
	// for(int i=0;i<pathIndex.size();i++)
	// if(((String)pathIndex.elementAt(i)).equals(path))
	// pathIndex.removeElementAt(i);
	// contextMap.remove(path);
	// removePathFromMap(path);
	// }

	// overrides removepath() method in ServiceContext
	public void removePath(String path) {
		removePathFromMap(path);
	}

	// Overrides Hashtable clear() method
	public void clear() {
		pathIndex.clear();
		super.clear();
	}

	// returns the first location of the NewPath in the vector
	// Returns -1 if not found
	// The last part of the string may also be given instead of the entire path.
	// However, this would throw exception if more than one path ends with the
	// same substring
	// For exact values, give fully qualified path
	public int getIndex(String path) throws ComprehensiveContextException {
		if (path == null)
			throw new ComprehensiveContextException(" Null path given !!!");
		// System.out.println("path : " + path);

		int j = -1;
		for (int i = 0; i < pathIndex.size(); i++) {
			// System.out.println("Comparing : "
			// +(String)pathIndex.elementAt(i));
			if (((String) pathIndex.elementAt(i)).endsWith(path)) {
				if (j == -1)
					j = i;
				else
					throw new ComprehensiveContextException(
							"Element occurs more than once. Please specify full path : "
									+ path);
			}
		}
		// System.out.println("Occured at: " + j);
		return j;
	}

	// gets the virtual path stored at index in pathIndex
	public String getPathAtIndex(int index)
			throws ComprehensiveContextException {
		if (index < 0)
			throw new ComprehensiveContextException("Invalid index given : "
					+ index + " < 0");
		if (index >= pathIndex.size())
			throw new ComprehensiveContextException(
					"Invalid index given:Out of Bounds: " + index + "  > "
							+ pathIndex.size());

		return (String) pathIndex.elementAt(index);
	}

	// gets the actual path from the contextmap for the path stored at index in
	// pathIndex
	public String getRealPathAtIndex(int index)
			throws ComprehensiveContextException {
		if (index < 0)
			throw new ComprehensiveContextException("Invalid index given : "
					+ index + " < 0");
		if (index >= pathIndex.size())
			throw new ComprehensiveContextException(
					"Invalid index given:Out of Bounds: " + index + "  > "
							+ pathIndex.size());

		return (String) contextMap.get((String) pathIndex.elementAt(index));
	}

	// gets the value stored for the virtual path stored at index in pathIndex
	public Object getValueAtIndex(int index)
			throws ComprehensiveContextException {
		if (index < 0)
			throw new ComprehensiveContextException("Invalid index given : "
					+ index + " < 0");
		if (index >= pathIndex.size())
			throw new ComprehensiveContextException(
					"Invalid index given:Out of Bounds: " + index + "  > "
							+ pathIndex.size());

		return get((String) pathIndex.elementAt(index));
	}

	// gets the value stored for this virtual path
	public Object getValue(String path) {
		System.out.println("Printing path being searched for : " + path);
		System.out.println(" ====In getValue()=======");
		// print();
		if (path != null)
			return get(path);
		// else throw new ComprehensiveContextException("Null path passed to
		// ComprehensiveContextException : null keys are not allowed");
		return null;
	}

	public boolean changeIndex(String path, int index)
			throws ComprehensiveContextException {
		if (index < 0)
			throw new ComprehensiveContextException("Invalid index given : "
					+ index + " < 0");
		if (index >= pathIndex.size())
			throw new ComprehensiveContextException(
					"Invalid index given:Out of Bounds: " + index + "  > "
							+ pathIndex.size());

		Object obj = null;
		for (int i = 0; i < pathIndex.size(); i++)
			if (((String) pathIndex.elementAt(i)).equals(path)) {
				obj = pathIndex.remove(i);
				pathIndex.insertElementAt(obj, index);
				return true;
			}
		return false;
	}

	public boolean changeIndex(int oldIndex, int newIndex)
			throws ComprehensiveContextException {
		if (oldIndex < 0)
			throw new ComprehensiveContextException("Invalid index given : "
					+ oldIndex + " < 0");
		if (oldIndex >= pathIndex.size())
			throw new ComprehensiveContextException(
					"Invalid index given:Out of Bounds: " + oldIndex + "  > "
							+ pathIndex.size());

		if (newIndex < 0)
			throw new ComprehensiveContextException("Invalid index given : "
					+ newIndex + " < 0");
		if (newIndex >= pathIndex.size())
			throw new ComprehensiveContextException(
					"Invalid index given:Out of Bounds: " + newIndex + "  > "
							+ pathIndex.size());

		Object obj = pathIndex.remove(oldIndex);
		pathIndex.insertElementAt(obj, newIndex);

		return true;
	}

	// returns the number of path-value pairs stored
	public int size() {
		return pathIndex.size();
	}

	public void print() {
		try {

			System.out.println(" =======Printing order of the paths===== ");
			for (int i = 0; i < size(); i++)
				System.out.println(i + " : " + getPathAtIndex(i));

			System.out.println(" =======Printing MAP===== ");
			for (int i = 0; i < size(); i++)
				System.out.println(i + " : " + getPathAtIndex(i) + " : "
						+ getRealPathAtIndex(i));

			System.out.println("=========Printing ServiceContext=============");
			System.out.println(toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
