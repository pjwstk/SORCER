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

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import sorcer.core.InclusiveContext;
import sorcer.service.ContextException;
import sorcer.util.SorcerUtil;

/*
 * Iterator returned by a ComprehensiveDataStoreProvider. This class should
 * preferably be immutable so that a requestor cannot modify this object to get
 * data which was not specified when the ComprehensiveContext was created for
 * this request.
 * 
 * 
 * @author Thimmayya Ame
 */
public class ComprehensiveContextIterator implements Iterator, Serializable {

	private static String COLUMN_DELIM = ",";

	private int contextKey;

	private int tupleCount;

	private Iterator[] iSet;

	// private ContextIterator[] ciSet;

	private InclusiveContext compContext;

	// Hashtable keeps mapping to index of the sorted paths retrieved from
	// compContext.getPaths();
	// compContext internally has a hashtable to map the paths in the order they
	// were selected by the requestor.
	// However the iterators get the results in sorted order of the paths.
	// So additional mapping is need to decide which column contains which data
	private Hashtable pathToIndexMap;

	private Vector buffer = new Vector(20);

	private Object[] currentTuple;

	private String currentTupleAsString; // Just for storage sake .

	// Stores the index of the column from which data was last read
	// The requestor asks for columns w.r.t a sequence starting at 1
	// Therefore, currentColumn is always (requestedIndex - 1).
	private int currentColumn;

	// public ComprehensiveContextIterator(){
	// Util.debug(this,"Initializing ComprehensiveContextIterator............
	// ");
	// }

	public ComprehensiveContextIterator(InclusiveContext compContext) {
		this.compContext = compContext;
		currentTuple = new Object[compContext.size()];
		getPathToIndexMap();
	}

	public ComprehensiveContextIterator(Iterator[] iSet,
			InclusiveContext compContext) {
		this.iSet = iSet;
		this.compContext = compContext;
		currentTuple = new Object[compContext.size()];

		init1();
		getPathToIndexMap();
	}

	public void setElementaryIterators(Iterator[] iSet) {
		this.iSet = iSet;
		init1();
	}

	private void init1() {
		tupleCount = 0;
		// ciSet = new ContextIterator[iSet.length];
		// for(int i=0;i<iSet.length;i++)
		// ciSet[i] = (ContextIterator)iSet[i];
		// Util.debug(this,"Initialized CompIterator " + this.toString());
	}

	// initialize pathToIndexMap
	private void getPathToIndexMap() {
		pathToIndexMap = new Hashtable();
		Enumeration e;
		try {
			e = compContext.contextPaths();
			int i = 0;
			while (e.hasMoreElements())
				pathToIndexMap.put(e.nextElement(), new Integer(i));
		} catch (ContextException e1) {
			e1.printStackTrace();
		}
	}

	public boolean hasNext() {
		// not implemented
		if (tupleCount == -2)
			return false;
		return true;
	}

	public void remove() throws UnsupportedOperationException,
			IllegalStateException {
		// not implemented
	}

	private boolean disconnect() {
		tupleCount = -2;
		// for(int i=0;i<ciSet.length;i++)
		// ciSet[i].close();
		return true;
	}

	public Object next() throws NoSuchElementException {
		if (tupleCount == -2)
			return "EOD";
		// if(tupleCount == -1) connect();
		StringBuffer sb = new StringBuffer().append(String.valueOf(tupleCount))
				.append("\n");

		StringBuffer data = new StringBuffer(200);
		String s;
		if (tupleCount >= 0)
			for (int i = 0; i < iSet.length; i++) {
				s = (String) iSet[i].next();
				if ("EOD".equals(s)) {
					tupleCount = -2;
					disconnect();
					return ("EOD");
				}
				data.append(s);
				if (i < iSet.length - 1)
					data.append(COLUMN_DELIM);
			}
		tupleCount++;
		/***********************************************************************
		 * BUFFER NOT IMPLEMENTED YET
		 **********************************************************************/

		buffer.add(data.toString());
		currentTupleAsString = data.toString();
		// splitTupleIntoColumns();

		return buffer.remove(0);
	}

	private void splitTupleIntoColumns() {
		String[] columns = SorcerUtil.tokenize(currentTupleAsString,
				COLUMN_DELIM);
		for (int i = 0; i < compContext.size(); i++)
			currentTuple[i] = columns[i];
	}

	// public static void main(String args[]) throws Exception{

	// ComprehensiveContextIterator cci = new ComprehensiveContextIterator();
	// }

	// public static void main(String args[]) throws Exception{
	// ContextIterator ci1 = new
	// ContextIterator(InetAddress.getLocalHost(),5027,10,10);
	// ContextIterator ci2 = new
	// ContextIterator(InetAddress.getLocalHost(),5028,10,10);
	// Vector files = new Vector(2,2);
	// Vector columns = new Vector(2,2);
	// files.add("test.arff");
	// files.add("test1.arff");
	// // files.add("test.arff");
	// columns.add("not yet");
	// ci1.setFileList(files);
	// ci2.setFileList(files);
	// ci1.setColumnNames(columns);
	// ci2.setColumnNames(columns);

	// ContextIterator[] ci = new ContextIterator[2];
	// ci[0]=ci1;
	// ci[1]=ci2;

	// ContextIteratorSet cis = new ContextIteratorSet(ci);

	// String output;
	// while(!"EOD".equals(output=(String)cis.next())){
	// // for(int i=0;i<10;i++){
	// // output = (String)ci.next();
	// System.out.println("Output ==> " + output);
	// }
	// }
}
