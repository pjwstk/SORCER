/*
 * @(#)ConnectionEvent.java	1.1 99/05/11
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

import java.sql.SQLException;

/**
 * <P>The ConnectionEvent class provides information about the source of
 * a connection related event.  ConnectionEvent objects provide the 
 * following information:
 * <UL>
 *   <LI>the pooled connection that generated the event
 *   <LI>the SQLException about to be thrown to the application
 *   ( in the case of an error event)
 * </UL>
 */

public class ConnectionEvent extends java.util.EventObject {

  /**
   * <P>Construct a ConnectionEvent object.  SQLException defaults to null.
   *
   * @param con the pooled connection that is the source of the event
   */
  public ConnectionEvent(PooledConnection con) {
    super(con);         
  }

  /**
   * <P>Construct a ConnectionEvent object.
   *
   * @param con the pooled connection that is the source of the event
   * @param ex the SQLException about to be thrown to the application
   */
  public ConnectionEvent(PooledConnection con, SQLException ex) {
    super(con);  
    this.ex = ex;
  }
 
  /**
   * <P>Get the SQLException. May be null.
   *
   * @return the SQLException about to be thrown
   */
  public SQLException getSQLException() { return ex; }

  private SQLException ex = null;

 } 





