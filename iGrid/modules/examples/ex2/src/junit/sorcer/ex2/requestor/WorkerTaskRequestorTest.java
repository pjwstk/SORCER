package junit.sorcer.ex2.requestor;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.core.transaction.TransactionException;

import org.junit.Before;
import org.junit.Test;

import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.ObjectTask;
import sorcer.core.signature.ObjectSignature;
import sorcer.ex2.provider.WorkerProvider;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;
import sorcer.service.SignatureException;
import sorcer.util.Log;

import com.gargoylesoftware.base.testing.TestUtil;

/**
 * @author Mike Sobolewski
 * 
 */
public class WorkerTaskRequestorTest {
	private static Logger logger = Log.getTestLog();
	
	private Context context;
	private String hostname;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		hostname = InetAddress.getLocalHost().getHostName();

		context = new ServiceContext("work");
		context.putValue("requstor/name", hostname);
		context.putValue("requestor/operand/1", 11);
		context.putValue("requestor/operand/2", 101);
		context.putValue("to/provider/name", "Testing Provider");
	}

	@Test
	public void contextSerializationTest() throws IOException {
		// test serialization of the requestor's context
		TestUtil.testSerialization(context, true);
	}
	
	@Test
	public void providerResultTest() throws RemoteException, ContextException, TransactionException, 
		ExertionException, UnknownHostException, SignatureException {
		
		ObjectSignature signature = new ObjectSignature("doWork", WorkerProvider.class);

		Exertion task = new ObjectTask("work", signature, context);
		task = task.exert();
		//logger.info("result: " + task);
		assertEquals((Integer)task.getContext().getValue("provider/result"), new Integer(1111));
	}
	
	@Test
	public void providerMessageTest() throws RemoteException, ContextException, TransactionException, 
		ExertionException, UnknownHostException, SignatureException {
		
		ObjectSignature signature = new ObjectSignature("doWork", WorkerProvider.class);

		Exertion task = new ObjectTask("work", signature, context);
		task = task.exert();
		//logger.info("result: " + task);
		assertEquals(task.getContext().getValue("provider/message"), "Done work: 1111");
	}
	
	@Test
	public void providerHostNameTest() throws RemoteException, ContextException, TransactionException, 
		ExertionException, UnknownHostException, SignatureException {
		
		ObjectSignature signature = new ObjectSignature("doWork", WorkerProvider.class);
		Exertion task = new ObjectTask("work", signature, context);
		task = task.exert();
		//logger.info("result: " + task);
		assertEquals(task.getContext().getValue("provider/host/name"), hostname);
	}
}
