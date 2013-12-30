package junit.sorcer.core.signature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static sorcer.eo.operator.deploy;
import static sorcer.eo.operator.idle;
import static sorcer.eo.operator.provider;
import static sorcer.eo.operator.sig;
import static sorcer.po.operator.invoker;
import static sorcer.po.operator.par;
import static sorcer.po.operator.pars;

import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import junit.sorcer.core.provider.Adder;
import junit.sorcer.core.provider.AdderImpl;

import org.junit.Ignore;
import org.junit.Test;

import sorcer.core.context.model.par.Par;
import sorcer.core.invoker.GroovyInvoker;
import sorcer.core.provider.Jobber;
import sorcer.service.ContextException;
import sorcer.service.ExertionException;
import sorcer.service.Signature;
import sorcer.service.SignatureException;
import sorcer.util.Sorcer;

/**
 * @author Mike Sobolewski
 */

public class SignatureTest {
	private final static Logger logger = Logger
			.getLogger(SignatureTest.class.getName());

	static {
		System.setProperty("java.util.logging.config.file",
				Sorcer.getHome() + "/configs/sorcer.logging");
		System.setProperty("java.security.policy", Sorcer.getHome()
				+ "/configs/policy.all");
		System.setSecurityManager(new RMISecurityManager());
		Sorcer.setCodeBase(new String[] { "arithmetic-beans.jar" });
	}
	
	@Test
	public void providerTest() throws ExertionException, ContextException, SignatureException {
		
		Signature s1 = sig("add", new AdderImpl());
		//logger.info("provider of s1: " + provider(s1));
		assertTrue(provider(s1) instanceof  AdderImpl);
		
		Signature s2 = sig("add", AdderImpl.class);
		//logger.info("provider of s2: " + provider(s2));
		assertTrue(provider(s2) instanceof  AdderImpl);

		Signature s4 = sig(invoker("new Date()"));
		//logger.info("provider of s4: " + provider(s4));
		assertTrue(provider(s4) instanceof  GroovyInvoker);
		
		Signature s6 = sig(par("x3", invoker("x3-e", "x1 - x2", pars("x1", "x2"))));
		logger.info("provider of s6: " + provider(s6));
		assertTrue(provider(s6) instanceof Par);

	}
	
	@Ignore
	@Test
	public void netProviderTest() throws SignatureException  {
		Signature s3 = sig("add", Adder.class);
		logger.info("provider of s3: " + provider(s3));
		assertTrue(provider(s3) instanceof Adder);
	}
	
	@Test
	public void deploySigTest() throws SignatureException  {
		Signature deploySig = sig("service", Jobber.class, "Jobber", deploy(idle(1)));
		logger.info("deploySig: " + deploySig);
		assertEquals(deploySig.getProviderName(), Sorcer.getActualName("Jobber"));
		assertEquals(deploySig.getSelector(), "service");
	}
	
}