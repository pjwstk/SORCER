package junit.sorcer.util.bdb.objects;

import static sorcer.eo.operator.clear;
import static sorcer.eo.operator.context;
import static sorcer.eo.operator.cxt;
import static sorcer.eo.operator.delete;
import static sorcer.eo.operator.exert;
import static sorcer.eo.operator.in;
import static sorcer.eo.operator.list;
import static sorcer.eo.operator.result;
import static sorcer.eo.operator.sig;
import static sorcer.eo.operator.size;
import static sorcer.eo.operator.store;
import static sorcer.eo.operator.task;
import static sorcer.eo.operator.value;
import static sorcer.po.operator.par;

import java.io.IOException;
import java.net.URL;
import java.rmi.RMISecurityManager;
import java.util.List;
import java.util.logging.Logger;

import net.jini.id.Uuid;

import org.junit.Assert;
import org.junit.Test;

import sorcer.core.provider.DatabaseStorer;
import sorcer.core.provider.StorageManagement;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.ExertionException;
import sorcer.service.ServiceExertion;
import sorcer.service.SignatureException;
import sorcer.service.Task;
import sorcer.util.Sorcer;
import sorcer.util.bdb.objects.SorcerDatabaseViews.Store;
import sorcer.util.url.sos.SdbURLStreamHandlerFactory;
import sorcer.util.url.sos.SdbUtil;

/**
* @author Mike Sobolewski
*/
@SuppressWarnings({ "rawtypes", "unchecked"})
public class ObjectStoreTest {
	private final static Logger logger = Logger.getLogger(ObjectStoreTest.class.getName());
		
	static {
		URL.setURLStreamHandlerFactory(new SdbURLStreamHandlerFactory());
		//System.setProperty("java.protocol.handler.pkgs", "sorcer.util.bdb.sos");

		System.setProperty("java.security.policy", Sorcer.getHome()
				+ "/configs/policy.all");
		
		System.setSecurityManager(new RMISecurityManager());
		
		Sorcer.setCodeBase(new String[] { "sdb-prv-dl.jar",  "sorcer-prv-dl.jar" });
		System.out.println("CLASSPATH :" + System.getProperty("java.class.path"));
		
		ServiceExertion.debug = true;
	}
	
	@Test
	public void storeTest() throws SignatureException, ExertionException, ContextException, IOException {
		Context data = cxt("stored", in("arg/x3", par("x3")), in("arg/x4", par("x4")), result("result/y"));
		
		Task objectStoreTask = task(
				"objectStore",
				sig("contextStore", DatabaseStorer.class, Sorcer.getActualDatabaseStorerName()),
					SdbUtil.getStoreContext(data));
		
		//objectStoreTask = exert(objectStoreTask);
		
		//logger.info("objectStoreTask: " + objectStoreTask);
		//logger.info("objectStoreTask context: " + context(objectStoreTask));
		URL objURL = (URL)value(objectStoreTask);
		logger.info("stored object URL: " + objURL);
		logger.info("retrieved object: " + objURL.getContent());
		
		Assert.assertEquals(data, objURL.getContent());
	}

	@Test
	public void storageContextTest() throws SignatureException, ExertionException, ContextException, IOException {
		Context data = cxt("stored", in("arg/x3", par("x3")), in("arg/x4", par("x4")), result("result/y"));
		
		Task objectStoreTask = task(
				"objectStore",
				sig("contextStore", DatabaseStorer.class, Sorcer.getActualDatabaseStorerName()),
				SdbUtil.getStoreContext(data));
		
		//objectStoreTask = exert(objectStoreTask);
		
		//logger.info("objectStoreTask: " + objectStoreTask);
		//logger.info("objectStoreTask context: " + context(objectStoreTask));
		URL objURL = (URL)value(objectStoreTask);
		logger.info("stored object URL: " + objURL);
//		logger.info("retrieved object: " + objURL.getContent());
		
		Assert.assertEquals(data, objURL.getContent());
	}
	
	@Test
	public void storeOperatorTest() throws SignatureException, ExertionException, ContextException, IOException {
		Context data = cxt("stored", in("arg/x3", par("x3")), in("arg/x4", par("x4")), result("result/y"));
		
		URL objURL = store(data);
		
		logger.info("stored object URL: " + objURL);
//		logger.info("retrieved object: " + objURL.getContent());
		
		Assert.assertEquals(data, objURL.getContent());
	}
	
	@Test
	public void retrievalContextTest() throws SignatureException, ExertionException, ContextException, IOException {
		Context data = cxt("store", in("arg/x3", par("x3")), in("arg/x4", par("x4")), result("result/y"));
		
		Uuid uuid = data.getId();
		store(data);
		
		Task objectRetrieveTask = task(
				"retrieve",
				sig("contextRetrieve", DatabaseStorer.class, Sorcer.getActualDatabaseStorerName()),
					SdbUtil.getRetrieveContext(uuid, Store.context));
				
		objectRetrieveTask = exert(objectRetrieveTask);
//		logger.info("objectRetrieveTask: " + objectRetrieveTask);
		Object retrived = value(context(objectRetrieveTask));
//		logger.info("objectRetrieveTask context: " + retrived);
		Assert.assertEquals(data, retrived);
	}
	
	@Test
	public void updateContextTest() throws SignatureException, ExertionException, ContextException, IOException {
		Context data = cxt("store", in("arg/x3", par("x3")), in("arg/x4", par("x4")), result("result/y"));
		Context updatedData = cxt("store", in("arg/x3", par("x3", 10.0)), in("arg/x4", par("x4", 20.0)));
		
		//store task to be executed for data
		//URL objURL = store(data);
		String storageName = Sorcer.getActualName(Sorcer.getDatabaseStorerName());
		Task objectStoreTask = task(
				"store",
				sig("contextStore", DatabaseStorer.class, storageName),
				SdbUtil.getStoreContext(data));
	
		objectStoreTask = exert(objectStoreTask);
		Uuid objUuid = (Uuid)value(context(objectStoreTask), StorageManagement.object_uuid);

		//updated task to be executed for updatedData
		Task objectUpdateTask = task(
				"update",
				sig("contextUpdate", DatabaseStorer.class, Sorcer.getActualDatabaseStorerName()),
				SdbUtil.getUpdateContext(updatedData, objUuid));
		
		objectUpdateTask = exert(objectUpdateTask);
		
		//retrieve task to be executed for updatedData in the previous task
		Task objectRetrieveTask = task(
				"retrieve",
				sig("contextRetrieve", DatabaseStorer.class,
						Sorcer.getActualDatabaseStorerName()),
				SdbUtil.getRetrieveContext(objUuid, Store.context));
		
		objectRetrieveTask = exert(objectRetrieveTask);
		logger.info("updated data: " + updatedData);
		logger.info("retrieved updated data: " + value(context(objectRetrieveTask), StorageManagement.object_retrieved));
		Assert.assertEquals(value(context(objectRetrieveTask),StorageManagement.object_retrieved), updatedData);
	}
	
	@Test
	public void listStoredEntriesTest() throws SignatureException, ExertionException, ContextException, IOException {
		Context data1 = cxt("stored", in("arg/x3", par("x3")), in("arg/x4", par("x4")), result("result/y"));
		//logger.info("id1: " + data1.getId());
		URL objURL1 = store(data1);
		List<String> content = list(objURL1);
		int initSize = content.size();
		//logger.info("initial store size: " + initSize);
		//logger.info("content 1: " + content);

		Context data2 = cxt("stored", in("arg/x5", par("x5")));
		//logger.info("id2: " + data2.getId());
		URL objURL2 = store(data2);
		content = list(objURL2);
		//logger.info("content size: " + content.size());
		//logger.info("content 2: " + content);

		Assert.assertEquals(content.size(), initSize + 1);
	}

	//@Ignore
	@Test
	public void deleteStoredEntriesTest() throws SignatureException, ExertionException, ContextException, IOException {
		Context data1 = cxt("stored", in("arg/x3", par("x3")), in("arg/x4", par("x4")), result("result/y"));
		//logger.info("id1: " + data1.getId());
		URL objURL1 = store(data1);
		List<String> content = list(objURL1);
		int initSize = content.size();
		//logger.info("initial store size: " + initSize);
		//logger.info("content 1: " + content);

		Context data2 = cxt("stored", in("arg/x5", par("x5")));
		//logger.info("id2: " + data2.getId());
		URL objURL2 = store(data2);
		content = list(objURL2);
		//logger.info("content size: " + content.size());
		//logger.info("content 2: " + content);
		Assert.assertEquals(content.size(), initSize + 1);

		delete(objURL1);
		content = list(objURL2);
		Assert.assertEquals(content.size(), initSize);
		
		objURL2 = store(data1);
		content = list(objURL2);
		Assert.assertEquals(content.size(), initSize + 1);

		delete(data1);
		content = list(objURL2);
		Assert.assertEquals(content.size(), initSize);
		
		//TODO, verify - was OK
		int storeSize = size(Store.context);
		logger.info("storeSize before clear: " + storeSize);
		
		int size = (int)clear(Store.context);
		logger.info("cleared tally: " + size);
//		Assert.assertEquals(storeSize, size);
		
		storeSize = size(Store.context);
		logger.info("storeSize after clear: " + storeSize);
//		Assert.assertEquals(storeSize, 0);

	}
}
