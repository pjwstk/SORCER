/*
 * @(#)RowSetListener.java	1.1 99/05/11
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

/**
 * <P>The RowSetListener interface is implemented by a 
 * component that wants to be notified when a significant 
 * event happens in the life of a RowSet
 */

public interface RowSetListener extends java.util.EventListener {

  /**
   * <P>Called when the rowset is changed.
   *
   * @param event an object describing the event
   */
  void rowSetChanged(RowSetEvent event);

  /**
   * <P>Called when a row is inserted, updated, or deleted.
   *
   * @param event an object describing the event
   */
  void rowChanged(RowSetEvent event);

  /**
   * Called when a rowset's cursor is moved.
   *
   * @param event an object describing the event
   */
  void cursorMoved(RowSetEvent event);
}
