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

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import sorcer.core.ComprehensiveContextException;
import sorcer.core.ComprehensiveContextModel;
import sorcer.core.InclusiveContext;
import sorcer.service.ContextException;
import sorcer.util.SorcerUtil;

/*
 * This class is analogus to ResultSet and iterates over the data conatined in a
 * set of ComprehensiveContexts
 * 
 * Current implementation of getXXX methods are based on the assumption that all
 * values retrieved are in the form of Strings. If objects are to be handled,
 * these methods require modification along with the method for reading the
 * data. This might be implemented in ContextIterator too.
 */
public class ComprehensiveContextModelImpl implements ComprehensiveContextModel {

	private InclusiveContext compContext;

	private Vector tuple;

	private Hashtable pathToIndexMap;

	private int currentColumn;

	private boolean nullValue = false;

	String[] currentTuple;

	private static String COLUMN_DELIM = ",";

	// private Iterator colIterator;

	public ComprehensiveContextModelImpl(InclusiveContext compContext) {
		this.compContext = compContext;
		getPathToIndexMap();
	}

	public ComprehensiveContextModelImpl(InclusiveContext compContext,
			Vector tuple) {
		this(compContext);
		this.tuple = tuple;
		getPathToIndexMap();
		// colIterator = tuple.iterator;
	}

	public ComprehensiveContextModelImpl(InclusiveContext compContext,
			String tuple, String columnDelimiter) {
		Vector v = initTupleVector(tuple, columnDelimiter);
		this.compContext = compContext;
		this.tuple = v;
		getPathToIndexMap();
	}

	private Vector initTupleVector(String tuple, String delim) {
		String[] tupleArray = SorcerUtil.getTokens(tuple, delim);
		Vector vec = new Vector(tupleArray.length);
		for (int i = 0; i < tupleArray.length; i++)
			vec.add(tupleArray[i]);

		return vec;
	}

	public void setTuple(Vector tuple) {
		this.tuple = tuple;
		// colIterator = tuple.iterator;
	}

	public void setTuple(String tuple, String delim) {
		Vector v = initTupleVector(tuple, delim);
		this.tuple = v;
		// colIterator = v.iterator;
	}

	// initialize pathToIndexMap
	private void getPathToIndexMap() {
		pathToIndexMap = new Hashtable();
		try {
			Enumeration e = compContext.contextPaths();
			int i = 0;
			while (e.hasMoreElements())
				pathToIndexMap.put(e.nextElement(), new Integer(i++));
		} catch (ContextException ce) {
			ce.printStackTrace();
		}
	}

	// private void splitTupleIntoColumns(){
	// String[] columns = Util.tokenize(currentTupleAsString,COLUMN_DELIM);
	// for(int i=0; i<compContext.size();i++)
	// currentTuple[i]= columns[i];
	// }

	// find the index of a column , if columnName does not exist throws
	// ComprehensiveContextException
	public int findColumn(String columnName)
			throws ComprehensiveContextException {
		return compContext.getIndex(columnName) + 1;
	}

	// returns the value of this column as a String
	private String getValueAt(int column) throws ComprehensiveContextException {
		currentColumn = column - 1;
		String path = compContext.getPathAtIndex(currentColumn);
		// System.out.println("path: " + path);
		int colNumInTuple = ((Integer) pathToIndexMap.get(path)).intValue();
		// System.out.println("colNumInTuple : " + colNumInTuple);
		// System.out.println("Value at : " +
		// (String)tuple.elementAt(colNumInTuple));
		String str = (String) tuple.elementAt(colNumInTuple);
		if (str.startsWith("'"))
			str = str.substring(1, str.length() - 1);
		if (str.equals("?"))
			nullValue = true;
		else
			nullValue = false;

		return str;
	}

	// returns the value of this column as a String
	public String getString(int column) throws ComprehensiveContextException {
		return getValueAt(column);
	}

	public String getString(String column) throws ComprehensiveContextException {
		return getString(findColumn(column));
	}

	// returns the value of this column as a Boolean
	// If 0 is stored in the database, it is returned as false, else true
	// or
	// If "false" is stored in the database false is returned, else true
	public boolean getBoolean(int column) throws ComprehensiveContextException {
		String retValue = getValueAt(column);
		if (retValue.equals("false") || retValue.equals("0")
				|| retValue.equalsIgnoreCase("no"))
			return false;
		// try{
		// int b = Integer.parseInt(retValue);
		// if(b == 0) return false;
		// // return true;
		// }catch(Exception e){
		// return false;
		// // throw new ComprehensiveContextException(" The value is not a
		// boolean Value : " + retValue);
		// }
		return true;
	}

	public boolean getBoolean(String column)
			throws ComprehensiveContextException {
		return getBoolean(findColumn(column));
	}

	// returns the value of this column as a double
	public double getDouble(int column) throws ComprehensiveContextException {
		return Double.parseDouble(getValueAt(column));
	}

	// returns the value of this column as a double
	public double getDouble(String column) throws ComprehensiveContextException {
		return getDouble(findColumn(column));
	}

	// returns the value of this column as a byte
	public byte getByte(int column) throws ComprehensiveContextException {
		return Byte.parseByte(getValueAt(column));
	}

	// returns the value of this column as a byte
	public byte getByte(String column) throws ComprehensiveContextException {
		return getByte(findColumn(column));
	}

	public int getInt(int column) throws ComprehensiveContextException {
		return Integer.parseInt(getValueAt(column));
	}

	public int getInt(String column) throws ComprehensiveContextException {
		return getInt(findColumn(column));
	}

	// public Integer getInteger(int colNum){
	// return new Integer(getInt(colNum));
	// }

	// returns the value of this column as a long
	public long getLong(int column) throws ComprehensiveContextException {
		return Long.parseLong(getValueAt(column));
	}

	// returns the value of this column as a long
	public long getLong(String column) throws ComprehensiveContextException {
		return getLong(findColumn(column));
	}

	// returns the value of this column as a float
	public float getFloat(int column) throws ComprehensiveContextException {
		return Float.parseFloat(getValueAt(column));
	}

	// returns the value of this column as a float
	public float getFloat(String column) throws ComprehensiveContextException {
		return getFloat(findColumn(column));
	}

	// returns the value of this column as a Date
	public Date getDate(int column) throws ComprehensiveContextException {
		return new java.util.Date(getValueAt(column));
	}

	// returns the value of this column as a Date
	public Date getDate(String column) throws ComprehensiveContextException {
		return getDate(findColumn(column));
	}

	public boolean wasNull() {
		// not implemented. currently the ComprehensiveIterator exits when one
		// of the inner iterators exits
		// return false;
		return nullValue;
	}

}
