/*
 * Created on Jan 19, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp.test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.sadun.text.ffp.AbortFFPException;
import org.sadun.text.ffp.BaseListener;
import org.sadun.text.ffp.FlatFileParser;
import org.sadun.text.ffp.LineFormat;
import org.sadun.util.ObjectLister;

/**
 *
 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano Sadun</a>
 * @version 1.0
 */
public class DBInsertionListener extends BaseListener implements FlatFileParser.AdvancedListener {
	
	private static ObjectLister objectLister =new ObjectLister((char)0);
	
	class InsertData {
		
		private Connection conn;
		private PreparedStatement stmt;
		
		private InsertData(Connection conn, LineFormat format) throws SQLException {
			stmt=conn.prepareStatement(mkSql(format));
		}
		
		private String mkSql(LineFormat format) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			pw.print("insert into ");
			pw.print(table);
			pw.print("(");
			objectLister.list(format.iterator());
			pw.print(") values (");
			int fieldsCount=format.getFieldsCount();
			for(int i=0;i<fieldsCount;i++) {
				pw.print("?");
				if (i<fieldsCount-1) pw.print(",");
			}
			pw.print(")");
			return sw.toString();
		}

		void setValues(String[] values) throws SQLException {
			for(int i=0;i<values.length;i++) {
				stmt.setObject(i, values[i]);
			}
		}

		void doInsert() throws SQLException {
			stmt.execute();
		}

		void closeConnection() throws SQLException {
			conn.close();
		}
	}

	private DataSource ds;
	private String table;
	private Connection sharedConnection=null;
    // Map LineFormat -> InsertDatasg
	private Map insertDataMap = new HashMap();
	private boolean shareConnection=true;
	
	
	public DBInsertionListener(DataSource ds, String table) {
		this(ds, table, table);
	}
	
	public DBInsertionListener(DataSource ds, String format, String table) {
		super(format);
		this.ds=ds;
		this.table=table;
	}
	 
	
	/* (non-Javadoc)
	 * @see org.sadun.text.ffp.BaseListener#onFormatName(java.lang.String, int, int, java.lang.String[])
	 */
	protected void onFormatName(
		LineFormat format,
		int logicalLineCount,
		int physicalLineCount,
		String[] values) throws AbortFFPException {
		try {
			InsertData data=getInsertData(format);
			data.setValues(values);
			data.doInsert();
		} catch (SQLException e) {
			throw new AbortFFPException("Problem inserting values ["+objectLister.list(values)+"]", e);
		}
	}
	
	/**
	 * @param format
	 * @return
	 */
	private InsertData getInsertData(LineFormat format) throws SQLException {
		InsertData data = (InsertData)insertDataMap.get(format);
		if (data==null) {
			if (shareConnection) data=new InsertData(getSharedConnection(), format);
			else data=new InsertData(ds.getConnection(), format);
		}
		return data;
	}


	/**
	 * @return
	 */
	private Connection getSharedConnection() throws SQLException {
		if (sharedConnection==null) sharedConnection=ds.getConnection();
		return sharedConnection;
	}


	/* (non-Javadoc)
	 * @see org.sadun.text.ffp.FlatFileParser.AdvancedListener#parsingStarted()
	 */
	public void parsingStarted()  throws AbortFFPException {
		// Do nothing
	}
	
	
	/* (non-Javadoc)
	 * @see org.sadun.text.ffp.FlatFileParser.AdvancedListener#parsingTerminated(boolean)
	 */
	public void parsingTerminated(boolean successful) {
		if (shareConnection) {
			if (sharedConnection!=null)
				try {
					sharedConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		} else {
			for(Iterator i=insertDataMap.keySet().iterator();i.hasNext();) {
				InsertData data=(InsertData)insertDataMap.get(i.next());
				try {
					data.closeConnection();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
		}
	}
	
	

}
