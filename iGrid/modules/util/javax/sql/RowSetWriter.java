/*
 * @(#)RowSetWriter.java	1.1 99/05/11
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
 * <P>An object that implements the RowSetWriter interface may be registered
 * with a RowSet object that supports the reader/writer paradigm.
 * The RowSetWriter.writeRow() method is called internally by a RowSet that supports 
 * the reader/writer paradigm to write the contents of the rowset to a data source.
 */

public interface RowSetWriter {

  /**
    <P>This method is called to write data to the data source
    that is backing the rowset.

    * @param caller the calling rowset 
    * @return true if the row was written, false if not due to a conflict
    * @exception SQLException if a database-access error occur
   */
  boolean writeData(RowSetInternal caller) throws SQLException;  

}
