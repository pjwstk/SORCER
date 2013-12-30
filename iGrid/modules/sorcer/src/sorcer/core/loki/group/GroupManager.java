package sorcer.core.loki.group;


import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

import net.jini.admin.Administrable;
import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;
import net.jini.core.lookup.ServiceID;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import sorcer.core.SorcerConstants;
import sorcer.core.context.ArrayContext;
import sorcer.core.loki.db.DatabaseInitializer;
import sorcer.core.loki.db.DerbyConnector;
import sorcer.core.loki.db.GStoreDB;
import sorcer.core.provider.Provider;
import sorcer.core.proxy.Partnership;
import sorcer.core.proxy.RemotePartner;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;

/**
 * Group Manger implements the Group Management interface
 * in order to connect and interact with the 
 * 
 * @author Daniel Kerr
 */

public class GroupManager implements GroupManagement, RemotePartner, Partnership, Serializable, SorcerConstants {
	//------------------------------------------------------------------------------------------------------------
	
	/** logging object */
	private final static Logger logger = Logger.getLogger(GroupManager.class.getName());
	/** serializable UID */
	private final static long serialVersionUID = 86753098675309L;
	
	/** administrative object */
	private Administrable admin;
	/** provider object */
	private Provider partner;
	/** service id */
	private ServiceID serviceID;
	
	/** location of database */
	private String databaseDir;
	/** database object */
	private GStoreDB database;
	
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * constructor for initializing the database and logger
	 */
	public GroupManager()
	{
		logger.info("Group Manager Started");
		this.initDB();
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * initialize the database, and create if not existent
	 */
	private void initDB()
	{
		Configuration config;
		String[] args = new String[] {System.getProperty("sorcer.provider.config")};

		try
		{ config = ConfigurationProvider.getInstance(args); }
		catch (ConfigurationException ce)
		{ logger.warning("Unable to load configuration file. " + ce.toString()); return; }
		
		try
		{
			databaseDir = (String) config.getEntry("GStoreConfig", "DatabaseDir", String.class, "/GStoreDB");
			logger.config("GStoreDB directory: " + databaseDir);
		}
		catch (ConfigurationException ce) { logger.warning("Error reading config file."); }
		    
		database = null;
		logger.info("Creating GStoreDB");
		
		try
		{ database = new DatabaseInitializer(new DerbyConnector((databaseDir))).getDatabase(); }
		catch (SQLException e)
		{
			logger.severe("Unable to create GStoreDB.");
			while (e != null)
			{ logger.severe(e.toString()); e = e.getNextException(); }
		}
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * get administrative object
	 * 
	 * @return
	 */
	public Object getAdmin() throws RemoteException
	{ return admin; }

	/**
	 * set administrative object
	 * 
	 * @param admin
	 */
	public void setAdmin(Object admin)
	{ this.admin = (Administrable) admin; }

	/**
	 * get inner provider
	 * 
	 * @return remote provider object
	 */
	public Remote getInner() throws RemoteException
	{ return (Remote) partner; }

	/**
	 * set inner provider
	 * 
	 * @param provider
	 */
	public void setInner(Object provider)
	{ partner = (Provider) provider; }

	/**
	 * return service object
	 * 
	 * @param exertion
	 * @param transaction
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Exertion service(Exertion exertion, Transaction transaction) throws RemoteException
	{
		try
		{ return partner.service(exertion, null); }
		catch (ExertionException ee)
		{ throw new RemoteException("Administration Failed",ee); }
		catch (TransactionException te)
		{ throw new RemoteException("Transaction Problem",te); }
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * retrieves provider id
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context getProviderID (Context context) throws RemoteException
	{
		if (serviceID == null)
		{
			UUID newID = UUID.randomUUID();
			serviceID = new ServiceID(newID.getMostSignificantBits(),newID.getLeastSignificantBits());
		}
		
		ArrayContext ctxt = (ArrayContext) context;
		
		try
		{ ctxt.ov(0, serviceID); }
		catch (ContextException e)
		{ logger.warning("Context error while sending ServiceID."); }
		    
		return ctxt;
	}

	//------------------------------------------------------------------------------------------------------------
/*	
	public Context testExertion() throws RemoteException
	{	
		try
		{
			int gSize = 5;
			KeyGenerator kg = new KeyGenerator();
			JavaSpace space = ProviderAccessor.getSpace();
	
			KeyPair[] pairs = new KeyPair[gSize];
	        KeyAgreement[] agrees = new KeyAgreement[gSize];
	     
	        // CREATOR -------------------------------------------------
	        
	        pairs[0] = kg.genKeyPair();
	        agrees[0] = kg.genKeyAgreement(pairs[0]);
	        
	        ExertionEnvelop ckpee = ExertionEnvelop.getTemplate();
	        ckpee.serviceType = "CreatorsPublicKey";
	        space.takeIfExists(ckpee,null,Lease.FOREVER);
	        ckpee.exertion = KPExertion.get(true,null,pairs[0].getPublic());
	        space.write(ckpee,null,Lease.FOREVER);
	        
			// ALL/CREATOR ---------------------------------------------
			
            ExertionEnvelop ckpeeT = ExertionEnvelop.getTemplate();
            ckpeeT.serviceType = "CreatorsPublicKey";
            ExertionEnvelop ckpeeRes = (ExertionEnvelop) space.take(ckpeeT,null,Lease.FOREVER);
            KPExertion ckpeRes = (KPExertion)ckpeeRes.exertion;
            
	        for(int i=1;i<gSize;++i)
	        {
	            pairs[i] = kg.genKeyPair();
	            agrees[i] = kg.genKeyAgreement(pairs[i]);
	            
	            //---------------
	            KeyAgreement localKA = kg.genKeyAgreement(pairs[i]);
	            localKA.doPhase(ckpeRes.publicKey, true);
	            
	            Cipher enCipher = Cipher.getInstance("DES");
	            enCipher.init(Cipher.ENCRYPT_MODE,localKA.generateSecret("DES"));
	            
	            ByteArrayOutputStream l_baos = new ByteArrayOutputStream();
				ObjectOutputStream l_oos = new ObjectOutputStream(l_baos);
				l_oos.writeObject(new MarshalledObject(pairs[i]));
				//---------------

				ExertionEnvelop kpEE = ExertionEnvelop.getTemplate();
		        kpEE.serviceType = "MemberKeyPairKeyAgreement";
		        kpEE.exertionID = "Member Number "+i;
		        space.takeIfExists(kpEE,null,Lease.FOREVER);
		        kpEE.exertion = KPExertion.get(false,enCipher.doFinal(l_baos.toByteArray()),pairs[i].getPublic());
		        space.write(kpEE,null,Lease.FOREVER);
	        }
			
	        // CREATOR -------------------------------------------------
	        
	        Vector<String> ids = new Vector<String>();
	        ids.add("Member Number 0");
	        Vector<KeyPair> r_pairs = new Vector<KeyPair>();
	        r_pairs.add(pairs[0]);
	        
	        for(int i=1;i<gSize;++i)
	        {
	        	ExertionEnvelop kpeeTemp = ExertionEnvelop.getTemplate();
	        	kpeeTemp.serviceType = "MemberKeyPairKeyAgreement";
	        	ExertionEnvelop kpeeRes = (ExertionEnvelop)space.takeIfExists(kpeeTemp,null,Lease.FOREVER);
	        	KPExertion kpeRes = (KPExertion)kpeeRes.exertion;
	        	
	            //---------------
	        	KeyAgreement localKA = kg.genKeyAgreement(pairs[0]);
	        	localKA.doPhase(kpeRes.publicKey, true);
	            
	        	Cipher deCipher = Cipher.getInstance("DES");
	            deCipher.init(Cipher.DECRYPT_MODE,localKA.generateSecret("DES"));
	            
	            ByteArrayInputStream l_bais = new ByteArrayInputStream(deCipher.doFinal(kpeRes.keyPair));
            	ObjectInputStream l_ois = new ObjectInputStream(l_bais);
				MarshalledObject mo = (MarshalledObject) l_ois.readObject();
				//---------------
				
				ids.add(kpeeRes.exertionID);
	        	r_pairs.add((KeyPair) mo.get());
	        }
	        
	        ExertionEnvelop cckEE = ExertionEnvelop.getTemplate();
	        cckEE.serviceType = "ComplimentaryCompoundKeys";
	        space.takeIfExists(cckEE,null,Lease.FOREVER);
	        cckEE.exertion = CCKExertion.get(kg.genCompoundKeys(ids,r_pairs));
	        space.write(cckEE,null,Lease.FOREVER);
	        
			// ALL/CREATOR ---------------------------------------------

	        ExertionEnvelop cckeeTemp = ExertionEnvelop.getTemplate();
	        cckeeTemp.serviceType = "ComplimentaryCompoundKeys";
			ExertionEnvelop cckeeRes = (ExertionEnvelop)space.take(cckeeTemp,null,Lease.FOREVER);
			CCKExertion cckeRes = (CCKExertion)cckeeRes.exertion;
	        
	        EncryptionManager[] ems = new EncryptionManager[gSize];
			
			for(int i=0;i<gSize;++i)
			{
				ems[i] = new EncryptionManager();
				ems[i].init(kg.genSharedKey(agrees[i],cckeRes.ccKeys.get("Member Number "+i)));	
			}
			
			// CREATOR -------------------------------------------------
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(new MarshalledObject(createSpaceJob()));
			byte[] ba = baos.toByteArray();
			byte[] cipherText = ems[0].encrypt(ba);

			ExertionEnvelop envelop = ExertionEnvelop.getTemplate();
			envelop.serviceType = "DemoTypeEnvelope";
			envelop.encryptedExertion = cipherText;
		 				
			space.write(envelop, null, Lease.FOREVER);
			
			// ALL -----------------------------------------------------
			
			ExertionEnvelop template = ExertionEnvelop.getTemplate();
			template.exertion = null;
			template.exertionID = null;
			template.isJob = null;
			template.parentID = null;
			template.providerName = null;
			template.providerSubject = null;
			template.serviceType = "DemoTypeEnvelope";
			template.state = null;
			
			ExertionEnvelop resultEnvelop = (ExertionEnvelop) space.take(template, null, Lease.FOREVER);
			
			for(int i=0;i<gSize;++i)
			{
				byte[] plaintext = ems[i].decrypt(resultEnvelop.encryptedExertion);

				ByteArrayInputStream bais = new ByteArrayInputStream(plaintext);
				ObjectInputStream ois = new ObjectInputStream(bais);
				MarshalledObject mo = (MarshalledObject) ois.readObject();
				Exertion eex = (Exertion) mo.get();
				
				logger.info("MEMBER "+i+" : "+eex.toString());
			}
			
			//----------------------------------------------------------
		}
		catch(Exception e){logger.severe("Problem: "+e.toString());}

		return new IndexedContext();
	}
*/	
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * execute update
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context executeUpdate(Context context) throws RemoteException
	{
		logger.info("Execute Update");
		return database.executeUpdate(context);
	}
	
	/**
	 * execute query
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context executeQuery(Context context) throws RemoteException
	{
		logger.info("Execute Query");
		return database.executeQuery(context);
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * add activity entry
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context addActivityEntry(Context context) throws RemoteException
	{
		logger.info("Add Activity Entry");
		return database.addActivityEntry(context);
	}
	
	/**
	 * add execution entry
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context addExecutionEntry(Context context) throws RemoteException
	{
		logger.info("Add Execution Entry");
		return database.addExecutionEntry(context);
	}
	
	/**
	 * add exertion entry
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context addExertionEntry(Context context) throws RemoteException
	{
		logger.info("Add Exertion Entry");
		return database.addExertionEntry(context);	
	}
	
	/**
	 * add group entry
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context addGroupEntry(Context context) throws RemoteException
	{
		logger.info("Add Group Entry");
		return database.addGroupEntry(context);
	}
	
	/**
	 * add member entry
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context addMemberEntry(Context context) throws RemoteException
	{
		logger.info("Add Member Entry");
		return database.addMemberEntry(context);
	}
	
	/**
	 * add membership entry
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context addMembershipEntry(Context context) throws RemoteException
	{
		logger.info("Add Membership Entry");
		return database.addMembershipEntry(context);	
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * get all groups
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context getGroups(Context context) throws RemoteException
	{
		logger.info("Get All Groups");
		return database.getGroups(context);	
	}	
	
	/**
	 * get group exertions
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context getGroupExertions(Context context) throws RemoteException
	{
		logger.info("Get Group Exertions");
		return database.getGroupExertions(context);
	}
	
	/**
	 * get group members
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context getGroupMembers(Context context) throws RemoteException
	{
		logger.info("Get Group Members");
		return database.getGroupMembers(context);
	}
	
	/**
	 * get group actions
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context getGroupAction(Context context) throws RemoteException
	{
		logger.info("Get Group Action");
		return database.getGroupAction(context);
	}
	
	/**
	 * get action info
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context getActionInfo(Context context) throws RemoteException
	{
		logger.info("Get Action Info");
		return database.getActionInfo(context);
	}

	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * get activity entry
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context getActivityEntry(Context context) throws RemoteException
	{
		logger.info("Get Activity Entry");
		return database.getActivityEntry(context);
	}
	
	/**
	 * get exertion entry
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context getExertionEntry(Context context) throws RemoteException
	{
		logger.info("Get Exertion Entry");
		return database.getExertionEntry(context);	
	}
	
	/**
	 * get group entry
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context getGroupEntry(Context context) throws RemoteException
	{
		logger.info("Get Group Entry");
		return database.getGroupEntry(context);
	}
	
	/**
	 * get member entry
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context getMemberEntry(Context context) throws RemoteException
	{
		logger.info("Get Member Entry");
		return database.getMemberEntry(context);
	}
	
	//------------------------------------------------------------------------------------------------------------

	/**
	 * get all activities
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context getActivities(Context context) throws RemoteException
	{
		logger.info("Get All Activities");
		return database.getActivities(context);
	}

	/**
	 * get all executions
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context getExecutions(Context context) throws RemoteException
	{
		logger.info("Get All Executions");
		return database.getExecutions(context);
	}

	/**
	 * get all exertions
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context getExertions(Context context) throws RemoteException
	{
		logger.info("Get All Exertions");
		return database.getExertions(context);
	}

	/**
	 * get all members
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context getMembers(Context context) throws RemoteException
	{
		logger.info("Get All Members");
		return database.getMembers(context);
	}

	/**
	 * get all memberships
	 * 
	 * @param context
	 * @return
	 * @throws RemoteException
	 * @see RemoteException
	 */
	public Context getMemberships(Context context) throws RemoteException {
		logger.info("Get All Memberships");
		return database.getMemberships(context);	
	}

	/* (non-Javadoc)
	 * @see sorcer.service.Service#service(sorcer.service.Exertion)
	 */
	@Override
	public Exertion service(Exertion exertion) throws TransactionException,
			ExertionException, RemoteException {
		return service(exertion, null);
	}

	
	//------------------------------------------------------------------------------------------------------------
}