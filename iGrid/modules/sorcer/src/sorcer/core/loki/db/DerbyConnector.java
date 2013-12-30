package sorcer.core.loki.db;

import java.sql.*;
import java.util.*;
import java.util.logging.*;

/**
 * Derby Database specific <code>DatabaseConnector</code>
 * 
 * @author Daniel Kerr
 */

public class DerbyConnector implements DatabaseConnector
{
	//------------------------------------------------------------------------------------------------------------
	
	/** derby driver name */
	private final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	/** name of derby protocol */
	private final String protocol = "jdbc:derby:";
	/** connection object */
	private final Connection connection;
	/** logger object */
	private final Logger logger;
	
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * initializes variables and creates database connection
	 * 
	 * @param databaseDir directory of database
	 */
	public DerbyConnector(String databaseDir) throws SQLException
	{
		logger = Logger.getLogger(this.getClass().getName());
	    logger.setLevel(Level.ALL);
	
		try
		{ Class.forName(driver).newInstance(); }
		catch (InstantiationException e)
		{ logger.severe("Unable to instantiate GStore driver."); }
		catch (IllegalAccessException e)
		{ logger.severe("Unable to access GStore driver."); }
		catch (ClassNotFoundException e)
		{ logger.severe("GStore driver class not found."); }
		
		logger.fine("Loaded database driver.");
		
		Properties props = new Properties();
		props.put("user", "loki");
		props.put("password", "abracadabra");
		
		connection = DriverManager.getConnection(protocol + databaseDir + "/GStore;create=true", props);
		logger.fine("Connected to Loki database.");
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * @return the created connection
	 */
	public Connection getConnection()
	{ return connection; }
	  
	/**
	 * closes the created connection, and shutsdown the derby protocol
	 */
	public void closeConnection()
	{
		try
		{ connection.close(); }
		catch (SQLException e)
		{ logger.warning("Unable to close Derby database connection."); }
		
		boolean gotSQLExc = false;
		
		try
		{ DriverManager.getConnection(protocol + ";shutdown=true"); }
		catch (SQLException se)
		{ gotSQLExc = true; }
		  
		if (!gotSQLExc)
		{ logger.warning("Derby did not shut down normally"); }
		else
		{ logger.fine("Derby shut down normally"); }
	}
	
	//------------------------------------------------------------------------------------------------------------
}