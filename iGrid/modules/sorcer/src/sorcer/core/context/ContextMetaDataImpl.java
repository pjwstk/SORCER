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

import sorcer.core.InclusiveContext;
import sorcer.core.ComprehensiveContextException;
import sorcer.core.ContextMetaData;
import sorcer.core.context.node.ContextNode;

/*
 * Class which returns attributes about the leafnodes of a ComprehensiveContext.
 * The attributes are extracted from the ContextNode contained for each path.
 * 
 * @author Thimmayya Ame
 */
public class ContextMetaDataImpl implements ContextMetaData {

	// dataTypeMap maps the integer values defined in java.sql.Types to their
	// respective String Names
	private static Hashtable dataTypeMap = new Hashtable();

	// The ComprehensiveContext for which this class handles metadata queries
	private InclusiveContext compContext;

	private int currentColumn;

	public ContextMetaDataImpl(InclusiveContext compContext) {
		this.compContext = compContext;
	}

	static {
		dataTypeMap.put(new Integer(java.sql.Types.ARRAY), "ARRAY");
		dataTypeMap.put(new Integer(java.sql.Types.BIGINT), "BIGINT");
		dataTypeMap.put(new Integer(java.sql.Types.BINARY), "BINARY");
		dataTypeMap.put(new Integer(java.sql.Types.BIT), "BIT");
		dataTypeMap.put(new Integer(java.sql.Types.BLOB), "BLOB");
		dataTypeMap.put(new Integer(java.sql.Types.BOOLEAN), "BOOLEAN");
		dataTypeMap.put(new Integer(java.sql.Types.CHAR), "CHAR");
		dataTypeMap.put(new Integer(java.sql.Types.CLOB), "CLOB");
		dataTypeMap.put(new Integer(java.sql.Types.DATALINK), "DATALINK");
		dataTypeMap.put(new Integer(java.sql.Types.DATE), "DATE");
		dataTypeMap.put(new Integer(java.sql.Types.DECIMAL), "DECIMAL");
		dataTypeMap.put(new Integer(java.sql.Types.DISTINCT), "DISTINCT");
		dataTypeMap.put(new Integer(java.sql.Types.DOUBLE), "DOUBLE");
		dataTypeMap.put(new Integer(java.sql.Types.FLOAT), "FLOAT");
		dataTypeMap.put(new Integer(java.sql.Types.INTEGER), "INTEGER");
		dataTypeMap.put(new Integer(java.sql.Types.JAVA_OBJECT), "JAVA_OBJECT");
		dataTypeMap.put(new Integer(java.sql.Types.LONGVARBINARY),
				"LONGVARBINARY");
		dataTypeMap.put(new Integer(java.sql.Types.LONGVARCHAR), "LONGVARCHAR");
		dataTypeMap.put(new Integer(java.sql.Types.NULL), "NULL");
		dataTypeMap.put(new Integer(java.sql.Types.NUMERIC), "NUMERIC");
		dataTypeMap.put(new Integer(java.sql.Types.OTHER), "OTHER");
		dataTypeMap.put(new Integer(java.sql.Types.REAL), "REAL");
		dataTypeMap.put(new Integer(java.sql.Types.REF), "REF");
		dataTypeMap.put(new Integer(java.sql.Types.SMALLINT), "SMALLINT");
		dataTypeMap.put(new Integer(java.sql.Types.STRUCT), "STRUCT");
		dataTypeMap.put(new Integer(java.sql.Types.TIME), "TIME");
		dataTypeMap.put(new Integer(java.sql.Types.TIMESTAMP), "TIMESTAMP");
		dataTypeMap.put(new Integer(java.sql.Types.TINYINT), "TINYINT");
		dataTypeMap.put(new Integer(java.sql.Types.VARBINARY), "VARBINARY");
		dataTypeMap.put(new Integer(java.sql.Types.VARCHAR), "VARCHAR");
	}

	// Returns the number of columns/paths in the ComprehensiveContext
	public int getColumnCount() {
		return compContext.size();
	}

	// Returns the label of the column at index i;
	public String getColumnLabel(int column)
			throws ComprehensiveContextException {
		currentColumn = column - 1;
		ContextNode cnode = (ContextNode) compContext
				.getValueAtIndex(currentColumn);
		return cnode.getLabel();
	}

	// Returns the name of the column at index i;
	public String getColumnName(int column)
			throws ComprehensiveContextException {
		currentColumn = column - 1;
		return (String) compContext.getPathAtIndex(currentColumn);
		// ContextNode cnode =
		// (ContextNode)compContext.getValueAtIndex(currentColumn);
		// return cnode.getName();
	}

	// Returns the SQL datatype of the column at index i;
	public int getColumnType(int column) throws ComprehensiveContextException {
		currentColumn = column - 1;
		ContextNode cnode = (ContextNode) compContext
				.getValueAtIndex(currentColumn);
		return cnode.getDataType();
	}

	// Returns the datatype of the column at index i;
	public String getColumnTypeName(int column)
			throws ComprehensiveContextException {
		currentColumn = column - 1;
		ContextNode cnode = (ContextNode) compContext
				.getValueAtIndex(currentColumn);
		return (String) dataTypeMap.get(new Integer(cnode.getDataType()));
	}

}
