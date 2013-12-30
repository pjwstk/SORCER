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

import java.util.Date;

/*
 * Interface defining the various methods in which data in a row can be be
 * accessed.
 * 
 * @author Thimmayya Ame
 */
public interface ComprehensiveContextModel {

	/**
	 * *************************************************************************
	 * ***********************
	 */
	// **** All methods taking columnIndex may be reimplemented to take
	// columnName as parameter too****//
	/**
	 * *************************************************************************
	 * ***********************
	 */
	/**
	 * find the index of a column , if columnName does not exist throws
	 * ComprehensiveContextException
	 * 
	 * @param name
	 *            of the column/attribute
	 * @return int index of the column
	 */
	// find the index of a column , if columnName does not exist throws
	// ComprehensiveContextException
	public int findColumn(String columnName)
			throws ComprehensiveContextException;

	/**
	 * returns the value of this attribute as a String
	 * 
	 * @param int index of the attribute
	 */
	// returns the value of this column as a String
	public String getString(int column) throws ComprehensiveContextException;

	/**
	 * returns the value of this attribute as a String
	 * 
	 * @param String
	 *            name of the attribute
	 */
	// returns the value of this column as a String
	public String getString(String column) throws ComprehensiveContextException;

	/**
	 * returns the value of this attribute as a boolean
	 * 
	 * @param int index of the attribute
	 */
	public boolean getBoolean(int column) throws ComprehensiveContextException;

	/**
	 * returns the value of this attribute as a boolean
	 * 
	 * @param String
	 *            name of the attribute
	 */
	public boolean getBoolean(String column)
			throws ComprehensiveContextException;

	/**
	 * returns the value of this attribute as a double
	 * 
	 * @param int index of the attribute
	 */
	public double getDouble(int column) throws ComprehensiveContextException;

	/**
	 * returns the value of this attribute as a double
	 * 
	 * @param String
	 *            name of the attribute
	 */
	public double getDouble(String column) throws ComprehensiveContextException;

	/**
	 * returns the value of this attribute as a byte
	 * 
	 * @param int index of the attribute
	 */
	public byte getByte(int column) throws ComprehensiveContextException;

	/**
	 * returns the value of this attribute as a byte
	 * 
	 * @param String
	 *            name of the attribute
	 */
	public byte getByte(String column) throws ComprehensiveContextException;

	/**
	 * returns the value of this attribute as a int
	 * 
	 * @param String
	 *            name of the attribute
	 */
	public int getInt(String column) throws ComprehensiveContextException;

	/**
	 * returns the value of this attribute as a int
	 * 
	 * @param int index of the attribute
	 */
	public int getInt(int column) throws ComprehensiveContextException;

	/**
	 * returns the value of this attribute as a long
	 * 
	 * @param String
	 *            name of the attribute
	 */
	public long getLong(String column) throws ComprehensiveContextException;

	/**
	 * returns the value of this attribute as a long
	 * 
	 * @param int index of the attribute
	 */
	public long getLong(int column) throws ComprehensiveContextException;

	/**
	 * returns the value of this attribute as a float
	 * 
	 * @param int index of the attribute
	 */
	public float getFloat(int column) throws ComprehensiveContextException;

	/**
	 * returns the value of this attribute as a float
	 * 
	 * @param String
	 *            name of the attribute
	 */

	public float getFloat(String column) throws ComprehensiveContextException;

	/**
	 * returns the value of this attribute as a Date object
	 * 
	 * @param int index of the attribute
	 */

	public Date getDate(int column) throws ComprehensiveContextException;

	/**
	 * returns the value of this attribute as a Date object
	 * 
	 * @param String
	 *            name of the attribute
	 */

	public Date getDate(String column) throws ComprehensiveContextException;

	// returns true if the value of the last column returned was null
	public boolean wasNull();

}
