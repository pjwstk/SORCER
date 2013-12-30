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

/*
 * Interface defining the methods that return various attributes of the
 * leafnodes of a ComprehensiveContext.
 * 
 * @author Thimmayya Ame
 */
public interface ContextMetaData {

	/**
	 * Returns the number of columns/paths in the ComprehensiveContext
	 * 
	 */
	// Returns the number of columns/paths in the ComprehensiveContext
	public int getColumnCount();

	/**
	 * Returns the label of the column at index
	 * 
	 * @param int index of the column/attribute
	 */
	// Returns the label of the column at index i;
	public String getColumnLabel(int column)
			throws ComprehensiveContextException;

	/**
	 * 
	 * Returns the name of the column at index i;
	 * 
	 * @param int index of the column/attribute
	 */
	// Returns the name of the column at index i;
	public String getColumnName(int column)
			throws ComprehensiveContextException;

	/**
	 * Returns the SQL type of the designated column
	 * 
	 * @param int index of the column/attribute
	 */
	// Returns the SQL type of the designated column
	public int getColumnType(int column) throws ComprehensiveContextException;

	/**
	 * Returns the datatype of the column at index i;
	 * 
	 * @param int index of the column/attribute
	 */
	// Returns the datatype of the column at index i;
	public String getColumnTypeName(int column)
			throws ComprehensiveContextException;

	// **** All methods taking columnIndex may be reimplemented to take
	// columnName as parameter too****//
	// public String getColumnTypeName(String columnName);

}
