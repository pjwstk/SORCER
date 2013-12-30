/*
 * @(#)RowSetInternal.java	1.1 99/05/11
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
 * 
 * A rowset object presents itself to a reader or writer as an instance
 * of RowSetInternal.  The RowSetInternal interface contains additional
 * methods that let the reader or writer access and modify the internal
 * state of the rowset.
 * 
 */

public interface RowSetInternal {

  /**
   * Get the parameters that were set on the rowset.
   *
   * @return an array of parameters
   * @exception SQLException if a database-access error occurs.
   */
  Object[] getParams() throws SQLException;

  /**
   * Get the Connection passed to the rowset.
   *
   * @return the Connection passed to the rowset, or null if none
   * @exception SQLException if a database-access error occurs.
   */
  Connection getConnection() throws SQLException;

  /**    
   * Set the rowset's metadata.
   *
   * @param a metadata object
   * @exception SQLException if a database-access error occurs.
   */
  void setMetaData(RowSetMetaData md) throws SQLException;

  /** 
   * Returns a result set containing the original value of the rowset.
   * The cursor is positioned before the first row in the result set.
   * Only rows contained in the result set returned by getOriginal()
   * are said to have an original value.
   *
   * @return the original value of the rowset
   * @exception SQLException if a database-access error occurs.
   */
  public ResultSet getOriginal() throws SQLException;

  /**
   * Returns a result set containing the original value of the current
   * row only.  If the current row has no original value an empty result set
   * is returned. If there is no current row an exception is thrown.
   *
   * @return the original value of the row
   * @exception SQLException if a database-access error occurs.
   */
  public ResultSet getOriginalRow() throws SQLException;

}





