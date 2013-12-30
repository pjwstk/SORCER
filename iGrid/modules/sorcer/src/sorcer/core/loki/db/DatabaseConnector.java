package sorcer.core.loki.db;

import java.sql.Connection;

/**
 * The database connector intergaface outlines methods for
 * the connection and deconnection to a specified database.
 * <p>
 * In the loki framework this is used by the group manager
 * object to access the group data store object.
 * 
 * @author Daniel Kerr
 */

public interface DatabaseConnector
{
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * creates a connection and connects
	 * 
	 * @return the created connection
	 */
	public Connection getConnection();
	
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * closes the created connection
	 */
	public void closeConnection();
	
	//------------------------------------------------------------------------------------------------------------
}