/*
 * @(#)RowSetMetaData.java	1.1 99/05/11
 * 
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * 
 */

package javax.sql;

import java.sql.*;

/**
 * <P>The RowSetMetaData interface extends ResultSetMetaData with 
 * methods that allow a metadata object to be initialized.  A 
 * RowSetReader may create a RowSetMetaData and pass it to a rowset
 * when new data is read.
 */

public interface RowSetMetaData extends ResultSetMetaData {

  /**
   * Set the number of columns in the RowSet.
   *
   * @param columnCount number of columns.
   * @exception SQLException if a database-access error occurs.
   */
  void setColumnCount(int columnCount) throws SQLException;

  /**
   * Specify whether the is column automatically numbered, thus read-only.
   *
   * @param column the first column is 1, the second is 2, ...
   * @param property is either true or false.
   *
   * @default is false.
   * @exception SQLException if a database-access error occurs.
   */
  void setAutoIncrement(int columnIndex, boolean property) throws SQLException;

  /**
   * Specify whether the column is case sensitive.
   *
   * @param column the first column is 1, the second is 2, ...
   * @param property is either true or false.
   *
   * @default is false.
   * @exception SQLException if a database-access error occurs.
   */
  void setCaseSensitive(int columnIndex, boolean property) throws SQLException;	

  /**
   * Specify whether the column can be used in a where clause.
   *
   * @param column the first column is 1, the second is 2, ...
   * @param property is either true or false.
   *
   * @default is false.
   * @exception SQLException if a database-access error occurs.
   */
  void setSearchable(int columnIndex, boolean property) throws SQLException;
  
  /**
   * Specify whether the column is a cash value.
   *
   * @param column the first column is 1, the second is 2, ...
   * @param property is either true or false.
   *
   * @default is false.
   * @exception SQLException if a database-access error occurs.
   */
  void setCurrency(int columnIndex, boolean property) throws SQLException;
  
  /**
   * Specify whether the column's value can be set to NULL.
   *
   * @param column the first column is 1, the second is 2, ...
   * @param property is either one of columnNoNulls, columnNullable or columnNullableUnknown.
   *
   * @default is columnNullableUnknown.
   * @exception SQLException if a database-access error occurs.
   */
  void setNullable(int columnIndex, int property) throws SQLException;

  /**
   * Speicfy whether the column is a signed number.
   *
   * @param column the first column is 1, the second is 2, ...
   * @param property is either true or false.
   *
   * @default is false.
   * @exception SQLException if a database-access error occurs.
   */
  void setSigned(int columnIndex, boolean property) throws SQLException;
  
  /**
   * Specify the column's normal max width in chars.
   *
   * @param column the first column is 1, the second is 2, ...
   * @param size size of the column
   *
   * @exception SQLException if a database-access error occurs.
   */
  void setColumnDisplaySize(int columnIndex, int size) throws SQLException;
  
  /**
   * Specify the suggested column title for use in printouts and
   * displays, if any.
   *
   * @param column the first column is 1, the second is 2, ...
   * @param label the column title
   * @exception SQLException if a database-access error occurs.
   */
  void setColumnLabel(int columnIndex, String label) throws SQLException;	
  
  /**
   * Specify the column name.
   *
   * @param column the first column is 1, the second is 2, ...
   * @param columnName the column name
   * @exception SQLException if a database-access error occurs.
   */
  void setColumnName(int columnIndex, String columnName) throws SQLException;
  
  /**
   * Specify the column's table's schema, if any.
   *
   * @param column the first column is 1, the second is 2, ...
   * @param schemaName the schema name
   * @exception SQLException if a database-access error occurs.
   */
  void setSchemaName(int columnIndex, String schemaName) throws SQLException;
  
  /**
   * Specify the column's number of decimal digits.
   *
   * @param column the first column is 1, the second is 2, ...
   * @param precision number of decimal digits.
   * @exception SQLException if a database-access error occurs.
   */
  void setPrecision(int columnIndex, int precision) throws SQLException;
  
  /**
   * Specify the column's number of digits to right of the decimal point.
   *
   * @param column the first column is 1, the second is 2, ...
   * @param scale number of digits to right of decimal point.
   * @exception SQLException if a database-access error occurs.
   */
  void setScale(int columnIndex, int scale) throws SQLException;	
  
  /**
   * Specify the column's table name, if any.
   *
   * @param column the first column is 1, the second is 2, ...
   * @param tableName column's table name.
   * @exception SQLException if a database-access error occurs.
   */
  void setTableName(int columnIndex, String tableName) throws SQLException;
  
  /**
   * Specify the column's table's catalog name, if any.
   *
   * @param column the first column is 1, the second is 2, ...
   * @param catalogName column's catalog name.
   * @exception SQLException if a database-access error occurs.
   */
  void setCatalogName(int columnIndex, String catalogName) throws SQLException;
  
  /**
   * Specify the column's SQL type.
   *
   * @param column the first column is 1, the second is 2, ...
   * @param SQLType column's SQL type.
   * @exception SQLException if a database-access error occurs.
   * @see Types
   */
  void setColumnType(int columnIndex, int SQLType) throws SQLException;
  
  /**
   * Specify the column's data source specific type name, if any.
   *
   * @param column the first column is 1, the second is 2, ...
   * @param typeName data source specific type name.
   * @exception SQLException if a database-access error occurs.
   */
  void setColumnTypeName(int columnIndex, String typeName) throws SQLException;

}





