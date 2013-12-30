/*
 * Created on Jan 20, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp.test;

import java.io.IOException;
import java.io.StringReader;

import javax.sql.DataSource;

import org.sadun.text.ffp.FFPParseException;
import org.sadun.text.ffp.FlatFileParser;
import org.sadun.util.pool.connection.ConfigurableDataSource;

/**
 *
 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano Sadun</a>
 * @version 1.0
 */
public class DBInsertionListenerTest extends FFPTest {
	
	FlatFileParser ffp;
	DataSource ds;
	String jdbcURL;

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(DBInsertionListenerTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
		super.setUp();
		jdbcURL="jdbc:microsoft:sqlserver://localhost:1433;User=sa;Password=;DatabaseName=ipm";
		ds=new ConfigurableDataSource(jdbcURL);
		ffp = new FlatFileParser();
		setupBetFor00(ffp);
	}
	
	public void testDBInsertion() throws IOException, FFPParseException {
		ffp.addListener(new DBInsertionListener(ds, "test format 00"));
		ffp.parse(new StringReader(test00));
	}

}
