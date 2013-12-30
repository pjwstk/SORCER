/*
 * @(#)PooledConnection.java	1.1 99/05/11
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

import java.sql.Connection;
import java.sql.SQLException;

/**
 * <p>A PooledConnection object is a connection object that provides
 * hooks for connection pool management.  A PooledConnection object
 * represents a physical connection to a data source.
 */

public interface PooledConnection {

  /**
   * <p>Create an object handle for this physical connection.  The object
   * returned is a temporary handle used by application code to refer to
   * a physical connection that is being pooled.
   *
   * @return  a Connection object
   * @exception SQLException if a database-access error occurs.
   */
  Connection getConnection() throws SQLException;
      
  /**
   * <p>Close the physical connection.
   *
   * @exception SQLException if a database-access error occurs.
   */
  void close() throws SQLException;
      
  /**
   * <P> Add an event listener.
   */
  void addConnectionEventListener(ConnectionEventListener listener);

  /**
   * <P> Remove an event listener.
   */
  void removeConnectionEventListener(ConnectionEventListener listener);
 } 





