package sorcer.core.loki.db;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.logging.*;
import java.util.Vector;

import sorcer.core.context.ArrayContext;
import sorcer.service.Context;

/**
 * 
 * @author Daniel Kerr
 */

public class GStoreDB
{
	//------------------------------------------------------------------------------------------------------------
	
	/** logger object */
	private final static Logger logger = Logger.getLogger(GStoreDB.class.getName());

	/** connection object */
	private final Connection connection;
  
	/** insert new activity entry */
	private final PreparedStatement addNewActivityEntry;
	/** insert new execution entry */
	private final PreparedStatement addNewExecutionEntry;
	/** insert new exertion entry */
	private final PreparedStatement addNewExertionEntry;
	/** insert new group entry */
	private final PreparedStatement addNewGroupEntry;
	/** insert new member entry */
	private final PreparedStatement addNewMemberEntry;
	/** insert new membership entry */
	private final PreparedStatement addNewMembershipEntry;
	
	/** select activity entry */
	private final PreparedStatement listActivityInfo;
	/** select exertion entry */
	private final PreparedStatement listExertionInfo;
	/** select group entry */
	private final PreparedStatement listGroupInfo;
	/** select member entry */
	private final PreparedStatement listMemberInfo;
	
	/** select all groups entries */
	private final PreparedStatement listGroups;
	/** select group exertions */
	private final PreparedStatement listGroupExertions;
	/** select group members */
	private final PreparedStatement listGroupMembers;
	/** select group action */
	private final PreparedStatement listGroupAction;
	/** select action information */
	private final PreparedStatement listActionInfo;

	/** select all activities entries */
	private final PreparedStatement listActivities;
	/** select all execution entries */
	private final PreparedStatement listExecutions;
	/** select all exertion entries */
	private final PreparedStatement listExertions;
	/** select all member entries */
	private final PreparedStatement listMembers;
	/** select all membership entries */
	private final PreparedStatement listMemberships;
	
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * initialize all statements and database
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	protected GStoreDB(final Connection connection) throws SQLException
	{
		logger.setLevel(Level.ALL);
		logger.fine("Starting up GStore");
		
		this.connection = connection;
		this.connection.setAutoCommit(false);
		
		createTables();

		logger.fine("Preparing statements");
		this.addNewActivityEntry = this.connection.prepareStatement(
		    "INSERT INTO LOK_ACTIVITY (ACTION,CREATION_DATE,COMMENT) " +
			"VALUES (?,?,?)",Statement.RETURN_GENERATED_KEYS);
		this.addNewExecutionEntry = this.connection.prepareStatement(
			"INSERT INTO LOK_EXECUTION (ACTIVITY_SEQ_ID,MEMBER_SEQ_ID) " +
			"VALUES (?,?)");
		this.addNewExertionEntry = this.connection.prepareStatement(
			"INSERT INTO LOK_EXERTION (GROUP_SEQ_ID,NAME,TYPE,CREATION_DATE,CREATED_BY,LAST_UPDATE_DATE,LAST_UPDATED_BY) " +
			"VALUES (?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
		this.addNewGroupEntry = this.connection.prepareStatement(
			"INSERT INTO LOK_GROUP (ADMIN_SEQ_ID,NAME,EXP_DATE,COMMENT,CREATION_DATE,CREATED_BY,LAST_UPDATE_DATE,LAST_UPDATED_BY) " +
			"VALUES (?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
		this.addNewMemberEntry = this.connection.prepareStatement(
		    "INSERT INTO LOK_MEMBER (NAME,PUBLIC_KEY,KEY_AGREEMENT,CREATION_DATE,CREATED_BY,LAST_UPDATE_DATE,LAST_UPDATE_BY) " +
		    "VALUES (?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
		this.addNewMembershipEntry = this.connection.prepareStatement(
		    "INSERT INTO LOK_MEMBERSHIP (GROUP_SEQ_ID,MEMBER_SEQ_ID) " +
			"VALUES (?,?)");

		this.listActivityInfo = this.connection.prepareStatement(
		    "SELECT * FROM LOK_ACTIVITY WHERE ACTIVITY_SEQ_ID = ?");
		this.listExertionInfo = this.connection.prepareStatement(
		    "SELECT * FROM LOK_EXERTION WHERE EXERTION_SEQ_ID = ?");
		this.listGroupInfo = this.connection.prepareStatement(
	    	"SELECT * FROM LOK_GROUP WHERE GROUP_SEQ_ID = ?");
		this.listMemberInfo = this.connection.prepareStatement(
		    "SELECT * FROM LOK_MEMBER WHERE MEMBER_SEQ_ID = ?");
		
		this.listGroups = this.connection.prepareStatement(
    	"SELECT * FROM LOK_GROUP");
		this.listGroupExertions = this.connection.prepareStatement(
    		"SELECT NAME, EXERTION_SEQ_ID FROM LOK_EXERTION WHERE GROUP_SEQ_ID = ?");
		this.listGroupMembers = this.connection.prepareStatement(
	    	"SELECT LOK_MEMBER.NAME, LOK_MEMBER.MEMBER_SEQ_ID " +
	    	"FROM LOK_MEMBER, LOK_MEMBERSHIP " +
	    	"WHERE LOK_MEMBER.MEMBER_SEQ_ID = LOK_MEMBERSHIP.MEMBER_SEQ_ID " +
	    	"AND LOK_MEMBERSHIP.GROUP_SEQ_ID = ?");
		this.listGroupAction = this.connection.prepareStatement(
			"SELECT " +
				"LOK_EXECUTION.ACTIVITY_SEQ_ID, " +
				"LOK_EXECUTION.MEMBER_SEQ_ID " +
			"FROM LOK_ACTIVITY, LOK_EXECUTION, LOK_MEMBERSHIP " +
			"WHERE " +
				"LOK_ACTIVITY.ACTIVITY_SEQ_ID = LOK_EXECUTION.ACTIVITY_SEQ_ID " +
				"AND LOK_MEMBERSHIP.MEMBER_SEQ_ID = LOK_EXECUTION.MEMBER_SEQ_ID " +
				"AND LOK_ACTIVITY.ACTION = ? " +
				"AND LOK_MEMBERSHIP.GROUP_SEQ_ID = ?");
		this.listActionInfo = this.connection.prepareStatement(
			"SELECT " +
				"LOK_EXECUTION.ACTIVITY_SEQ_ID, " +
				"LOK_ACTIVITY.ACTION, " +
				"LOK_ACTIVITY.CREATION_DATE, " +
				"LOK_ACTIVITY.COMMENT, " +
				"LOK_EXECUTION.MEMBER_SEQ_ID " +
			"FROM LOK_ACTIVITY, LOK_EXECUTION " +
			"WHERE " +
				"LOK_ACTIVITY.ACTIVITY_SEQ_ID = LOK_EXECUTION.ACTIVITY_SEQ_ID " +
				"AND LOK_ACTIVITY.ACTIVITY_SEQ_ID = ?");
		

		this.listActivities = this.connection.prepareStatement(
		    "SELECT * FROM LOK_ACTIVITY");
		this.listExecutions = this.connection.prepareStatement(
	    	"SELECT * FROM LOK_EXECUTION");
		this.listExertions = this.connection.prepareStatement(
		    "SELECT * FROM LOK_EXERTION");
		this.listMembers = this.connection.prepareStatement(
		    "SELECT * FROM LOK_MEMBER");
		this.listMemberships = this.connection.prepareStatement(
		    "SELECT * FROM LOK_MEMBERSHIP");
		
		logger.fine("Finished Preparing statements");
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * create the following tables if the do not exist:
	 * 		<code>activity</code>
	 * 		<code>execution</code>
	 * 		<code>exertion</code>
	 * 		<code>group</code>
	 * 		<code>member</code>
	 * 		<code>membership</code>
	 * 
	 * @throws SQLException
	 */
	protected void createTables() throws SQLException
	{
		logger.fine("Creating GStore Tables");
		
		final Statement statement = this.connection.createStatement();
		DatabaseMetaData dbmd = this.connection.getMetaData();
		ResultSet results = null;

		logger.fine("Trying to make LOK_ACTIVITY table.");
		results = dbmd.getTables(null, null, "LOK_ACTIVITY", null);
		if (results.next() && results.getString(3).equals("LOK_ACTIVITY"))
		{ logger.fine("LOK_ACTIVITY table exists."); }
		else
		{
			StringBuffer sbuf = new StringBuffer();
			sbuf.append("CREATE TABLE LOK_ACTIVITY (");
			sbuf.append("ACTIVITY_SEQ_ID BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT PK_LOK_ACTIVITY_SEQ_ID PRIMARY KEY,");
			sbuf.append("ACTION VARCHAR(50) NOT NULL,");
			sbuf.append("CREATION_DATE TIMESTAMP,");
			sbuf.append("COMMENT VARCHAR(500))");
			statement.execute(sbuf.toString());
			logger.fine("Made LOK_ACTIVITY table.");
		}

		logger.fine("Trying to make LOK_MEMBER table.");
		results = dbmd.getTables(null, null, "LOK_MEMBER", null);
		if (results.next() && results.getString(3).equals("LOK_MEMBER"))
		{ logger.fine("LOK_MEMBER table exists."); }
		else
		{
			StringBuffer sbuf = new StringBuffer();
			sbuf.append("CREATE TABLE LOK_MEMBER (");
			sbuf.append("MEMBER_SEQ_ID BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT PK_LOK_MEMBER_SEQ_ID PRIMARY KEY,");
			sbuf.append("NAME VARCHAR(50) NOT NULL,");
			sbuf.append("PUBLIC_KEY VARCHAR(5000) NOT NULL,");
			sbuf.append("KEY_AGREEMENT VARCHAR(5000) NOT NULL,");
			sbuf.append("CREATION_DATE TIMESTAMP,");
			sbuf.append("CREATED_BY VARCHAR(50),");
			sbuf.append("LAST_UPDATE_DATE TIMESTAMP,");
			sbuf.append("LAST_UPDATE_BY VARCHAR(50))");
			statement.execute(sbuf.toString());
			logger.fine("Made LOK_MEMBER table.");
		}

		logger.fine("Trying to make LOK_GROUP table.");
		results = dbmd.getTables(null, null, "LOK_GROUP", null);
		if (results.next() && results.getString(3).equals("LOK_GROUP"))
		{ logger.fine("LOK_GROUP table exists."); }
		else
		{
			StringBuffer sbuf = new StringBuffer();
			sbuf.append("CREATE TABLE LOK_GROUP (");
			sbuf.append("GROUP_SEQ_ID BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT PK_LOK_GROUP_SEQ_ID PRIMARY KEY,");
			sbuf.append("ADMIN_SEQ_ID BIGINT NOT NULL,");
			sbuf.append("NAME VARCHAR(50) NOT NULL,");
			sbuf.append("EXP_DATE TIMESTAMP NOT NULL,");
			sbuf.append("COMMENT VARCHAR(500),");
			sbuf.append("CREATION_DATE TIMESTAMP,");
			sbuf.append("CREATED_BY VARCHAR(50),");
			sbuf.append("LAST_UPDATE_DATE TIMESTAMP,");
			sbuf.append("LAST_UPDATED_BY VARCHAR(50))");
			statement.execute(sbuf.toString());
			logger.fine("Made LOK_GROUP table.");
			
			statement.execute("ALTER TABLE LOK_GROUP ADD FOREIGN KEY (ADMIN_SEQ_ID) REFERENCES LOK_MEMBER(MEMBER_SEQ_ID)");
			logger.fine("Added ADMIN_SEQ_ID FK");
		}

		logger.fine("Trying to make LOK_EXERTION table.");
		results = dbmd.getTables(null, null, "LOK_EXERTION", null);
		if (results.next() && results.getString(3).equals("LOK_EXERTION"))
		{ logger.fine("LOK_EXERTION table exists."); }
		else
		{
			StringBuffer sbuf = new StringBuffer();
			sbuf.append("CREATE TABLE LOK_EXERTION (");
			sbuf.append("EXERTION_SEQ_ID BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT PK_LOK_EXERTION_SEQ_ID PRIMARY KEY,");
			sbuf.append("GROUP_SEQ_ID BIGINT NOT NULL,");
			sbuf.append("NAME VARCHAR(50) NOT NULL,");
			sbuf.append("TYPE VARCHAR(500) NOT NULL,");
			sbuf.append("CREATION_DATE TIMESTAMP,");
			sbuf.append("CREATED_BY VARCHAR(50),");
			sbuf.append("LAST_UPDATE_DATE TIMESTAMP,");
			sbuf.append("LAST_UPDATED_BY VARCHAR(50))");
			statement.execute(sbuf.toString());
			logger.fine("Made LOK_EXERTION table.");
			
			statement.execute("ALTER TABLE LOK_EXERTION ADD FOREIGN KEY (GROUP_SEQ_ID) REFERENCES LOK_GROUP");
			logger.fine("Added GROUP_SEQ_ID FK");
		}

		logger.fine("Trying to make LOK_EXECUTION table.");
		results = dbmd.getTables(null, null, "LOK_EXECUTION", null);
		if (results.next() && results.getString(3).equals("LOK_EXECUTION"))
		{ logger.fine("LOK_EXECUTION table exists."); }
		else
		{
			StringBuffer sbuf = new StringBuffer();
			sbuf.append("CREATE TABLE LOK_EXECUTION (");
			sbuf.append("ACTIVITY_SEQ_ID BIGINT NOT NULL,");
			sbuf.append("MEMBER_SEQ_ID BIGINT NOT NULL)");
			statement.execute(sbuf.toString());
			logger.fine("Made LOK_EXECUTION table.");

			statement.execute("ALTER TABLE LOK_EXECUTION ADD PRIMARY KEY (ACTIVITY_SEQ_ID,MEMBER_SEQ_ID)");
			logger.fine("Added Primary Key");
		}

		logger.fine("Trying to make LOK_MEMBERSHIP table.");
		results = dbmd.getTables(null, null, "LOK_MEMBERSHIP", null);
		if (results.next() && results.getString(3).equals("LOK_MEMBERSHIP"))
		{ logger.fine("LOK_MEMBERSHIP table exists."); }
		else
		{
			StringBuffer sbuf = new StringBuffer();
			sbuf.append("CREATE TABLE LOK_MEMBERSHIP (");
			sbuf.append("GROUP_SEQ_ID BIGINT NOT NULL,");
			sbuf.append("MEMBER_SEQ_ID BIGINT NOT NULL)");
			statement.execute(sbuf.toString());
			logger.fine("Made LOK_MEMBERSHIP table.");
			
			statement.execute("ALTER TABLE LOK_MEMBERSHIP ADD PRIMARY KEY (GROUP_SEQ_ID,MEMBER_SEQ_ID)");
			logger.fine("Added Primary Key");
					
			statement.execute("ALTER TABLE LOK_MEMBERSHIP ADD  FOREIGN KEY (GROUP_SEQ_ID) REFERENCES LOK_GROUP");
			logger.fine("Added GROUP_SEQ_ID FK");
					  
			statement.execute("ALTER TABLE LOK_MEMBERSHIP ADD  FOREIGN KEY (MEMBER_SEQ_ID) REFERENCES LOK_MEMBER");
			logger.fine("Added MEMBER_SEQ_ID FK");
		}
		
		statement.close();
		results.close();
		this.connection.commit();
		logger.fine("GStore tables created.");
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	public Context executeUpdate(Context context) throws RemoteException
	{
		ArrayContext ctxt = (ArrayContext) context;
		try
		{
			String update = (String)ctxt.iv(0);
			logger.fine("Executing update: "+update);
			try
			{
				Statement stmnt = this.connection.createStatement();
				stmnt.executeUpdate(update);
				this.connection.commit();
			}
			catch (SQLException e)
			{
				logger.warning("SQL error while executing update: " + e.getMessage());
				throw new RemoteException("SQL error while executing update: " + e.getMessage());
			}
		}
		catch (Exception e)
		{ logger.warning("Context error: " + e.toString() + "\n"); e.printStackTrace(); }
		
		return ctxt;
	}
	
	public Context executeQuery(Context context) throws RemoteException
	{
		ArrayContext ctxt = (ArrayContext) context;
	    
		try
		{
			Vector<Vector<Object>> listing = new Vector<Vector<Object>>();
			
			String query = (String)ctxt.iv(0);
			logger.fine("Executing query: "+query);
			
			try
			{
				Statement stmnt = this.connection.createStatement();
				ResultSet results = stmnt.executeQuery(query);
				ResultSetMetaData rsmd = results.getMetaData();
				
				while (results.next())
				{
					Vector<Object> entry = new Vector<Object>();
					
					for(int i=0;i<rsmd.getColumnCount();++i)
					{ entry.add(results.getString(i+1)); }
					
					listing.add(entry);
				}
			}
			catch (SQLException e)
			{ logger.warning("SQL error while executing query"); }
			
			ctxt.ov(0,listing);
		}
		catch (Exception e)
		{ logger.warning("Context error: " + e.toString() + "\n"); e.printStackTrace(); }
		    
		return ctxt;
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * add activity entry
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 */
	public Context addActivityEntry(Context context) throws RemoteException
	{
		logger.fine("Creating a new activity entry");
		ArrayContext ctxt = (ArrayContext) context;
		try
		{
			try
			{
				this.addNewActivityEntry.setString(1,(String)ctxt.iv(0));
				this.addNewActivityEntry.setTimestamp(2,Timestamp.valueOf((String)ctxt.iv(1)));
				this.addNewActivityEntry.setString(3,(String)ctxt.iv(2));
				this.addNewActivityEntry.executeUpdate();
				
				ResultSet rs = this.addNewActivityEntry.getGeneratedKeys();
				rs.next();
				ctxt.ov(0,rs.getString(1));
				rs.close();
				
				this.connection.commit();
			}
			catch (SQLException e)
			{
				logger.warning("SQL error while making a new activity: " + e.getMessage());
				throw new RemoteException("SQL error while making a new activity: " + e.getMessage());
			}
		}
		catch (Exception e)
		{ logger.warning("Context error: " + e.toString() + "\n"); e.printStackTrace(); }
		
		return ctxt;
	}
	
	/**
	 * add execution entry
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 */
	public Context addExecutionEntry(Context context) throws RemoteException
	{
		logger.fine("Creating a new execution entry");
		ArrayContext ctxt = (ArrayContext) context;
		long entry = -1;
		try
		{
			try
			{
				this.addNewExecutionEntry.setLong(1,Long.valueOf((String)ctxt.iv(0)));
				this.addNewExecutionEntry.setLong(2,Long.valueOf((String)ctxt.iv(1)));
				this.addNewExecutionEntry.execute();
				this.connection.commit();
			}
			catch (SQLException e)
			{
				logger.warning("SQL error while making a new execution: " + e.getMessage());
				throw new RemoteException("SQL error while making a new execution: " + e.getMessage());
			}  
			ctxt.ov(0, entry);
		}
		catch (Exception e)
		{ logger.warning("Context error: " + e.toString() + "\n"); e.printStackTrace(); }
		
		return ctxt;
	}
	
	/**
	 * add exertion entry
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 */
	public Context addExertionEntry(Context context) throws RemoteException
	{
		logger.fine("Creating a new exertion entry");
		ArrayContext ctxt = (ArrayContext) context;
		try
		{
			try
			{
				this.addNewExertionEntry.setLong(1,Long.valueOf((String)ctxt.iv(0)));
				this.addNewExertionEntry.setString(2,(String)ctxt.iv(1));
				this.addNewExertionEntry.setString(3,(String)ctxt.iv(2));
				this.addNewExertionEntry.setTimestamp(4,Timestamp.valueOf((String)ctxt.iv(3)));
				this.addNewExertionEntry.setString(5,(String)ctxt.iv(4));
				this.addNewExertionEntry.setTimestamp(6,Timestamp.valueOf((String)ctxt.iv(5)));
				this.addNewExertionEntry.setString(7,(String)ctxt.iv(6));
				this.addNewExertionEntry.executeUpdate();
				
				ResultSet rs = this.addNewExertionEntry.getGeneratedKeys();
				rs.next();
				ctxt.ov(0,rs.getString(1));
				rs.close();
				
				this.connection.commit();
			}
			catch (SQLException e)
			{
				logger.warning("SQL error while making a new exertion: " + e.getMessage());
				throw new RemoteException("SQL error while making a new exertion: " + e.getMessage());
			}
		}
		catch (Exception e)
		{ logger.warning("Context error: " + e.toString() + "\n"); e.printStackTrace(); }
		
		return ctxt;
	}
	
	/**
	 * add group entry
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 */
	public Context addGroupEntry(Context context) throws RemoteException
	{
		logger.fine("Creating a new group entry");
		ArrayContext ctxt = (ArrayContext) context;
		try
		{
			try
			{
				this.addNewGroupEntry.setLong(1,Long.valueOf((String)ctxt.iv(0)));
				this.addNewGroupEntry.setString(2,(String)ctxt.iv(1));
				this.addNewGroupEntry.setTimestamp(3,Timestamp.valueOf((String)ctxt.iv(2)));
				this.addNewGroupEntry.setString(4,(String)ctxt.iv(3));
				this.addNewGroupEntry.setTimestamp(5,Timestamp.valueOf((String)ctxt.iv(4)));
				this.addNewGroupEntry.setString(6,(String)ctxt.iv(5));
				this.addNewGroupEntry.setTimestamp(7,Timestamp.valueOf((String)ctxt.iv(6)));
				this.addNewGroupEntry.setString(8,(String)ctxt.iv(7));
				this.addNewGroupEntry.executeUpdate();
				
				ResultSet rs = this.addNewGroupEntry.getGeneratedKeys();
				rs.next();
				ctxt.ov(0,rs.getString(1));
				rs.close();
				
				this.connection.commit();
			}
			catch (SQLException e)
			{
				logger.warning("SQL error while making a new group: " + e.getMessage());
				throw new RemoteException("SQL error while making a new group: " + e.getMessage());
			}
		}
		catch (Exception e)
		{ logger.warning("Context error: " + e.toString() + "\n"); e.printStackTrace(); }
		
		return ctxt;
	}
	
	/**
	 * add member entry
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 */
	public Context addMemberEntry(Context context) throws RemoteException
	{
		logger.fine("Creating a new member entry");
		ArrayContext ctxt = (ArrayContext) context;
		try
		{
			try
			{
				this.addNewMemberEntry.setString(1,(String)ctxt.iv(0));
				this.addNewMemberEntry.setString(2,(String)ctxt.iv(1));
				this.addNewMemberEntry.setString(3,(String)ctxt.iv(2));
				this.addNewMemberEntry.setTimestamp(4,Timestamp.valueOf((String)ctxt.iv(3)));
				this.addNewMemberEntry.setString(5,(String)ctxt.iv(4));
				this.addNewMemberEntry.setTimestamp(6,Timestamp.valueOf((String)ctxt.iv(5)));
				this.addNewMemberEntry.setString(7,(String)ctxt.iv(6));
				this.addNewMemberEntry.executeUpdate();
				
				ResultSet rs = this.addNewMemberEntry.getGeneratedKeys();
				rs.next();
				ctxt.ov(0,rs.getString(1));
				rs.close();
				
				this.connection.commit();
			}
			catch (SQLException e)
			{
				logger.warning("SQL error while making a new member: " + e.getMessage());
				throw new RemoteException("SQL error while making a new member: " + e.getMessage());
			}
		}
		catch (Exception e)
		{ logger.warning("Context error: " + e.toString() + "\n"); e.printStackTrace(); }
		
		return ctxt;
	}
	
	/**
	 * add membership entry
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 */
	public Context addMembershipEntry(Context context) throws RemoteException
	{
		logger.fine("Creating a new membership entry");
		ArrayContext ctxt = (ArrayContext) context;
		long entry = -1;
		try
		{
			try
			{
				this.addNewMembershipEntry.setLong(1,Long.valueOf((String)ctxt.iv(0)));
				this.addNewMembershipEntry.setLong(2,Long.valueOf((String)ctxt.iv(1)));
				this.addNewMembershipEntry.execute();
				this.connection.commit();
			}
			catch (SQLException e)
			{
				logger.warning("SQL error while making a new membership: " + e.getMessage());
				throw new RemoteException("SQL error while making a new membership: " + e.getMessage());
			}  
			ctxt.ov(0, entry);
		}
		catch (Exception e)
		{ logger.warning("Context error: " + e.toString() + "\n"); e.printStackTrace(); }
		
		return ctxt;
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * get all groups
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 */
	public Context getGroups(Context context) throws RemoteException
	{
		ArrayContext ctxt = (ArrayContext) context;
	    
		try
		{
			Vector<Vector<Object>> groupListing = new Vector<Vector<Object>>();
			try
			{
				ResultSet results = this.listGroups.executeQuery();
				while (results.next())
				{
					Vector<Object> groupEntry = new Vector<Object>();
					groupEntry.add(results.getLong(1));
					groupEntry.add(results.getLong(2));
					groupEntry.add(results.getString(3));
					groupEntry.add(results.getTimestamp(4));
					groupEntry.add(results.getString(5));
					groupEntry.add(results.getTimestamp(6));
					groupEntry.add(results.getString(7));
					groupEntry.add(results.getTimestamp(8));
					groupEntry.add(results.getString(9));
					
					logger.info("Group entry: " + groupEntry.toString());
					groupListing.add(groupEntry);
				}
			}
			catch (SQLException e)
			{ logger.warning("SQL error while getting group listing."); }
			
			ctxt.ov(0,groupListing);
		}
		catch (Exception e)
		{ logger.warning("Context error: " + e.toString() + "\n"); e.printStackTrace(); }
		    
		return ctxt;
	}

	/**
	 * get group exertions
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 */
	public Context getGroupExertions(Context context) throws RemoteException
	{
		ArrayContext ctxt = (ArrayContext) context;
	    
		try
		{
			Vector<Vector<Object>> gExertionListing = new Vector<Vector<Object>>();
			try
			{
				long GroupID = (Long) ctxt.iv(0);
				this.listGroupExertions.setLong(1,GroupID);
				
				ResultSet results = this.listGroupExertions.executeQuery();
				while (results.next())
				{
					Vector<Object> gExertionEntry = new Vector<Object>();
					gExertionEntry.add(results.getString(1));
					gExertionEntry.add(results.getLong(2));
					
					logger.info("Group exertion entry: " + gExertionEntry.toString());
					gExertionListing.add(gExertionEntry);
				}
			}
			catch (SQLException e)
			{ logger.warning("SQL error while getting group exertion listing."); }
			
			ctxt.ov(0,gExertionListing);
		}
		catch (Exception e)
		{ logger.warning("Context error: " + e.toString() + "\n"); e.printStackTrace(); }
		    
		return ctxt;
	}
	
	/**
	 * get group members
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 */
	public Context getGroupMembers(Context context) throws RemoteException
	{
		ArrayContext ctxt = (ArrayContext) context;
	    
		try
		{
			Vector<Vector<Object>> gMemberListing = new Vector<Vector<Object>>();
			try
			{
				long GroupID = (Long) ctxt.iv(0);
				this.listGroupMembers.setLong(1,GroupID);
				
				ResultSet results = this.listGroupMembers.executeQuery();
				while (results.next())
				{
					Vector<Object> gMemberEntry = new Vector<Object>();
					gMemberEntry.add(results.getString(1));
					gMemberEntry.add(results.getLong(2));
					
					logger.info("Group member entry: " + gMemberEntry.toString());
					gMemberListing.add(gMemberEntry);
				}
			}
			catch (SQLException e)
			{ logger.warning("SQL error while getting group member listing."); }
			
			ctxt.ov(0,gMemberListing);
		}
		catch (Exception e)
		{ logger.warning("Context error: " + e.toString() + "\n"); e.printStackTrace(); }
		    
		return ctxt;
	}
	
	/**
	 * get group action
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 */
	public Context getGroupAction(Context context) throws RemoteException
	{
		ArrayContext ctxt = (ArrayContext) context;
	    
		try
		{
			Vector<Vector<Object>> gActionListing = new Vector<Vector<Object>>();
			try
			{
				String GroupAction = (String)ctxt.iv(0);
				long GroupID = (Long)ctxt.iv(1);
				this.listGroupAction.setString(1,GroupAction);
				this.listGroupAction.setLong(2,GroupID);
				
				ResultSet results = this.listGroupAction.executeQuery();
				while (results.next())
				{
					Vector<Object> gActionEntry = new Vector<Object>();
					gActionEntry.add(results.getLong(1));
					gActionEntry.add(results.getLong(2));
					
					logger.info("Group action entry: " + gActionEntry.toString());
					gActionListing.add(gActionEntry);
				}
			}
			catch (SQLException e)
			{ logger.warning("SQL error while getting group action listing."); }
			
			ctxt.ov(0,gActionListing);
		}
		catch (Exception e)
		{ logger.warning("Context error: " + e.toString() + "\n"); e.printStackTrace(); }
		    
		return ctxt;
	}
	
	/**
	 * get group action
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 */
	public Context getActionInfo(Context context) throws RemoteException
	{
		ArrayContext ctxt = (ArrayContext) context;
	    
		try
		{
			Vector<Vector<Object>> gActionListing = new Vector<Vector<Object>>();
			try
			{
				long action = (Long)ctxt.iv(0);
				this.listActionInfo.setLong(1,action);
				
				ResultSet results = this.listActionInfo.executeQuery();
				while (results.next())
				{
					Vector<Object> gActionEntry = new Vector<Object>();
					gActionEntry.add(results.getLong(1));
					gActionEntry.add(results.getString(2));
					gActionEntry.add(results.getDate(3));
					gActionEntry.add(results.getString(4));
					gActionEntry.add(results.getLong(5));
					
					logger.info("Action info entry: " + gActionEntry.toString());
					gActionListing.add(gActionEntry);
				}
			}
			catch (SQLException e)
			{ logger.warning("SQL error while getting action listing."); }
			
			ctxt.ov(0,gActionListing);
		}
		catch (Exception e)
		{ logger.warning("Context error: " + e.toString() + "\n"); e.printStackTrace(); }
		    
		return ctxt;
	}
	
	//------------------------------------------------------------------------------------------------------------

	/**
	 * get activity entry
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 */
	public Context getActivityEntry(Context context) throws RemoteException
	{
		ArrayContext ctxt = (ArrayContext) context;
	    
		try
		{
			Vector<Vector<Object>> activityListing = new Vector<Vector<Object>>();
			try
			{
				long ActivityID = (Long) ctxt.iv(0);
				this.listActivityInfo.setLong(1,ActivityID);
				
				ResultSet results = this.listActivityInfo.executeQuery();
				while (results.next())
				{
					Vector<Object> activityEntry = new Vector<Object>();
					activityEntry.add(results.getLong(1));
					activityEntry.add(results.getString(2));
					activityEntry.add(results.getTimestamp(3));
					activityEntry.add(results.getString(4));
					
					logger.info("Activity entry: " + activityEntry.toString());
					activityListing.add(activityEntry);
				}
			}
			catch (SQLException e)
			{ logger.warning("SQL error while getting activity listing."); }
			
			
			ctxt.ov(0,activityListing);
		}
		catch (Exception e)
		{ logger.warning("Context error: " + e.toString() + "\n"); e.printStackTrace(); }
		    
		return ctxt;
	}

	/**
	 * get exertion entry
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 */
	public Context getExertionEntry(Context context) throws RemoteException
	{
		ArrayContext ctxt = (ArrayContext) context;
	    
		try
		{			
			Vector<Vector<Object>> exertionListing = new Vector<Vector<Object>>();
			try
			{
				long ExertionID = (Long) ctxt.iv(0);
				this.listExertionInfo.setLong(1,ExertionID);
				
				ResultSet results = this.listExertionInfo.executeQuery();
				while (results.next())
				{
					Vector<Object> exertionEntry = new Vector<Object>();
					exertionEntry.add(results.getLong(1));
					exertionEntry.add(results.getLong(2));
					exertionEntry.add(results.getString(3));
					exertionEntry.add(results.getString(4));
					exertionEntry.add(results.getTimestamp(5));
					exertionEntry.add(results.getString(6));
					exertionEntry.add(results.getTimestamp(7));
					exertionEntry.add(results.getString(8));
										
					logger.info("Exertion entry: " + exertionEntry.toString());
					exertionListing.add(exertionEntry);
				}
			}
			catch (SQLException e)
			{ logger.warning("SQL error while getting exertion listing."); }
			
			ctxt.ov(0,exertionListing);
		}
		catch (Exception e)
		{ logger.warning("Context error: " + e.toString() + "\n"); e.printStackTrace(); }
		    
		return ctxt;
	}

	/**
	 * get group entry
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 */
	public Context getGroupEntry(Context context) throws RemoteException
	{
		ArrayContext ctxt = (ArrayContext) context;
	    
		try
		{
			Vector<Vector<Object>> groupListing = new Vector<Vector<Object>>();
			try
			{
				long GroupID = (Long) ctxt.iv(0);
				this.listGroupInfo.setLong(1,GroupID);
				
				ResultSet results = this.listGroupInfo.executeQuery();
				while (results.next())
				{
					Vector<Object> groupEntry = new Vector<Object>();
					groupEntry.add(results.getLong(1));
					groupEntry.add(results.getLong(2));
					groupEntry.add(results.getString(3));
					groupEntry.add(results.getTimestamp(4));
					groupEntry.add(results.getString(5));
					groupEntry.add(results.getTimestamp(6));
					groupEntry.add(results.getString(7));
					groupEntry.add(results.getTimestamp(8));
					groupEntry.add(results.getString(9));
					
					logger.info("Group entry: " + groupEntry.toString());
					groupListing.add(groupEntry);
				}
			}
			catch (SQLException e)
			{ logger.warning("SQL error while getting group listing."); }
			
			ctxt.ov(0,groupListing);
		}
		catch (Exception e)
		{ logger.warning("Context error: " + e.toString() + "\n"); e.printStackTrace(); }
		    
		return ctxt;
	}

	/**
	 * get member entry
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 */
	public Context getMemberEntry(Context context) throws RemoteException
	{
		ArrayContext ctxt = (ArrayContext) context;
	    
		try
		{
			Vector<Vector<Object>> memberListing = new Vector<Vector<Object>>();
			try
			{
				long MemberID = (Long) ctxt.iv(0);
				this.listMemberInfo.setLong(1,MemberID);
				
				ResultSet results = this.listMemberInfo.executeQuery();
				while (results.next())
				{
					Vector<Object> memberEntry = new Vector<Object>();
					memberEntry.add(results.getLong(1));
					memberEntry.add(results.getString(2));
					memberEntry.add(results.getString(3));
					memberEntry.add(results.getString(4));
					memberEntry.add(results.getTimestamp(5));
					memberEntry.add(results.getString(6));
					memberEntry.add(results.getTimestamp(7));
					memberEntry.add(results.getString(8));
					
					logger.info("Member entry: " + memberEntry.toString());
					memberListing.add(memberEntry);
				}
			}
			catch (SQLException e)
			{ logger.warning("SQL error while getting member listing."); }
			
			ctxt.ov(0,memberListing);
		}
		catch (Exception e)
		{ logger.warning("Context error: " + e.toString() + "\n"); e.printStackTrace(); }
		    
		return ctxt;
	}

	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * get all activity entries
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 */
	public Context getActivities(Context context) throws RemoteException
	{
		ArrayContext ctxt = (ArrayContext) context;
	    
		try
		{
			Vector<Vector<Object>> activityListing = new Vector<Vector<Object>>();
			try
			{
				ResultSet results = this.listActivities.executeQuery();
				while (results.next())
				{
					Vector<Object> activityEntry = new Vector<Object>();
					activityEntry.add(results.getLong(1));
					activityEntry.add(results.getString(2));
					activityEntry.add(results.getTimestamp(3));
					activityEntry.add(results.getString(4));
					
					logger.info("Activity entry: " + activityEntry.toString());
					activityListing.add(activityEntry);
				}
			}
			catch (SQLException e)
			{ logger.warning("SQL error while getting activity listing."); }
			
			ctxt.ov(0,activityListing);
		}
		catch (Exception e)
		{ logger.warning("Context error: " + e.toString() + "\n"); e.printStackTrace(); }
		    
		return ctxt;
	}

	/**
	 * get all executions
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 */
	public Context getExecutions(Context context) throws RemoteException
	{
		ArrayContext ctxt = (ArrayContext) context;
	    
		try
		{
			Vector<Vector<Object>> executionListing = new Vector<Vector<Object>>();
			try
			{
				ResultSet results = this.listExecutions.executeQuery();
				while (results.next())
				{
					Vector<Object> executionEntry = new Vector<Object>();
					executionEntry.add(results.getLong(1));
					executionEntry.add(results.getLong(2));
					
					logger.info("Execution entry: " + executionEntry.toString());
					executionListing.add(executionEntry);
				}
			}
			catch (SQLException e)
			{ logger.warning("SQL error while getting execution listing."); }
			
			ctxt.ov(0,executionListing);
		}
		catch (Exception e)
		{ logger.warning("Context error: " + e.toString() + "\n"); e.printStackTrace(); }
		    
		return ctxt;
	}
	
	/**
	 * get all exertions
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 */
	public Context getExertions(Context context) throws RemoteException
	{
		ArrayContext ctxt = (ArrayContext) context;
	    
		try
		{
			Vector<Vector<Object>> exertionListing = new Vector<Vector<Object>>();
			try
			{
				ResultSet results = this.listExertions.executeQuery();
				while (results.next())
				{
					Vector<Object> exertionEntry = new Vector<Object>();
					exertionEntry.add(results.getLong(1));
					exertionEntry.add(results.getLong(2));
					exertionEntry.add(results.getString(3));
					exertionEntry.add(results.getString(4));
					exertionEntry.add(results.getTimestamp(5));
					exertionEntry.add(results.getString(6));
					exertionEntry.add(results.getTimestamp(7));
					exertionEntry.add(results.getString(8));
					
					logger.info("Exertion entry: " + exertionEntry.toString());
					exertionListing.add(exertionEntry);
				}
			}
			catch (SQLException e)
			{ logger.warning("SQL error while getting exertion listing."); }
			
			ctxt.ov(0,exertionListing);
		}
		catch (Exception e)
		{ logger.warning("Context error: " + e.toString() + "\n"); e.printStackTrace(); }
		    
		return ctxt;
	}
	
	/**
	 * get all members
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 */
	public Context getMembers(Context context) throws RemoteException
	{
		ArrayContext ctxt = (ArrayContext) context;
	    
		try
		{
			Vector<Vector<Object>> memberListing = new Vector<Vector<Object>>();
			try
			{
				ResultSet results = this.listMembers.executeQuery();
				while (results.next())
				{
					Vector<Object> memberEntry = new Vector<Object>();
					memberEntry.add(results.getLong(1));
					memberEntry.add(results.getString(2));
					memberEntry.add(results.getString(3));
					memberEntry.add(results.getString(4));
					memberEntry.add(results.getTimestamp(5));
					memberEntry.add(results.getString(6));
					memberEntry.add(results.getTimestamp(7));
					memberEntry.add(results.getString(8));
					
					logger.info("Member entry: " + memberEntry.toString());
					memberListing.add(memberEntry);
				}
			}
			catch (SQLException e)
			{ logger.warning("SQL error while getting member listing."); }
			
			ctxt.ov(0,memberListing);
		}
		catch (Exception e)
		{ logger.warning("Context error: " + e.toString() + "\n"); e.printStackTrace(); }
		    
		return ctxt;
	}

	/**
	 * get all memberships
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 */
	public Context getMemberships(Context context) throws RemoteException
	{
		ArrayContext ctxt = (ArrayContext) context;
	    
		try
		{
			Vector<Vector<Object>> membershipListing = new Vector<Vector<Object>>();
			try
			{
				ResultSet results = this.listMemberships.executeQuery();
				while (results.next())
				{
					Vector<Object> membershipEntry = new Vector<Object>();
					membershipEntry.add(results.getLong(1));
					membershipEntry.add(results.getLong(2));
					
					logger.info("Membership entry: " + membershipEntry.toString());
					membershipListing.add(membershipEntry);
				}
			}
			catch (SQLException e)
			{ logger.warning("SQL error while getting membership listing."); }
			
			ctxt.ov(0,membershipListing);
		}
		catch (Exception e)
		{ logger.warning("Context error: " + e.toString() + "\n"); e.printStackTrace(); }
		    
		return ctxt;
	}
	
	//------------------------------------------------------------------------------------------------------------
}