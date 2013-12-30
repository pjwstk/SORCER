package sorcer.core.loki.db;

import java.sql.*;

/**
 * initialize the database as a GStoreDB
 * 
 * @author Daniel Kerr
 */

public class DatabaseInitializer
{
	//------------------------------------------------------------------------------------------------------------
	
	/** database object */
	private final GStoreDB groupstoreDB;
	
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * initializes the local database object as a GStoreSB
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @throws SQLException
	 * @see GStoreDB, SQLException
	 */
	public DatabaseInitializer(String url, String username, String password) throws SQLException
	{
		final Connection connection = DriverManager.getConnection(url, username, password);
		groupstoreDB = new GStoreDB(connection);
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * initializes the local database object as a GStoreSB
	 * 
	 * @param connector the connection to the database
	 * @throws SQLException
	 * @see GStoreDB, SQLException
	 */
	public DatabaseInitializer(DatabaseConnector connector) throws SQLException
	{ groupstoreDB = new GStoreDB(connector.getConnection()); }
	
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * returns database object
	 */
	public GStoreDB getDatabase()
	{ return groupstoreDB; }
	
	//------------------------------------------------------------------------------------------------------------
}