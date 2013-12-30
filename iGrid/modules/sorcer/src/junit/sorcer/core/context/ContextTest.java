package junit.sorcer.core.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static sorcer.co.operator.entry;
import static sorcer.co.operator.map;
import static sorcer.eo.operator.asis;
import static sorcer.eo.operator.context;
import static sorcer.eo.operator.get;
import static sorcer.eo.operator.in;
import static sorcer.eo.operator.name;
import static sorcer.eo.operator.out;
import static sorcer.eo.operator.put;
import static sorcer.eo.operator.result;
import static sorcer.eo.operator.revaluable;
import static sorcer.eo.operator.revalue;
import static sorcer.eo.operator.value;
import static sorcer.po.operator.args;
import static sorcer.po.operator.in;
import static sorcer.po.operator.invoker;
import static sorcer.po.operator.par;
import static sorcer.po.operator.pars;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.Test;

import sorcer.core.context.ContextLink;
import sorcer.core.context.PositionalContext;
import sorcer.core.context.ServiceContext;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.ExertionException;

/**
 * @author Mike Sobolewski
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ContextTest {
	private final static Logger logger = Logger
			.getLogger(ContextTest.class.getName());

	@SuppressWarnings("unchecked")
	@Test
	public void contextCreationTest() throws ExertionException, ContextException {
		Map<String, Double> m = map(entry("k1", 1.0), entry("k2", 2.0));
		//logger.info("map m:  " + m);
		assertTrue("Wrong value for k1=1.0", m.get("k1").equals(1.0));

		Context<?> cxt = context(in("k1", 1.0), in("k2", 2.0), in("k3", 3.0), out("k4", 4.0));
//		logger.info("in/out context: " + cxt);
		assertEquals(get(cxt, "k1"), 1.0);
		assertEquals(get(cxt, "k2"), 2.0);
		assertEquals(get(cxt, "k3"), 3.0);
		assertEquals(get(cxt, "k4"), 4.0);
		assertEquals(get(cxt, 1), 1.0);
		assertEquals(get(cxt, 2), 2.0);
		assertEquals(get(cxt, 3), 3.0);
		assertEquals(get(cxt, 4), 4.0);
		
		assertEquals(((PositionalContext)cxt).getTally(), 4);
		//logger.info("tally: " + ((PositionalContext)cxt).getTally());
		put(cxt, entry("k4", par("x1", 50.0)));
//		logger.info("tally after k4: " + ((PositionalContext)cxt).getTally());
		assertEquals(((PositionalContext)cxt).getTally(), 4);
//		logger.info("value k4: " + get(cxt, "k4"));
		assertEquals(revalue(cxt, "k4"), 50.0);
		assertEquals(name(asis(cxt, "k4")), "x1");
		
		put(cxt, entry("k5", par("x2", 100.0)));
//		logger.info("tally after k5: " + ((PositionalContext)cxt).getTally());
		assertEquals(((PositionalContext)cxt).getTally(), 5);
//		logger.info("value k5: " + get(cxt, "k5"));
		assertEquals(revalue(cxt, "k5"), 100.0);
	
		cxt = context(entry("k1", 1.0), entry("k2", 2.0), entry("k3", 3.0));
//		logger.info("context cxt:  " + cxt.getClass());
		//logger.info("entry context cxt:  " + cxt);
		assertEquals(get(cxt, "k2"), 2.0);
		assertEquals(get(cxt, "k3"), 3.0);
	}
	
	@Test
	public void contextClosureTest() throws ExertionException, ContextException, RemoteException {
		Context<?> cxt = context(in("x1"), in("x2"), 
				in(par("y", invoker("e1", "x1 * x2", pars("x1", "x2")))));
		revaluable(cxt);
		
//		logger.info("x1 value: " + value(cxt, "x1", entry("x1", 10.0), entry("x2", 50.0)));
//		logger.info("x2 value: " + value(cxt, "x2"));
//		logger.info("y value: " + value(cxt, "y"));

		logger.info("cxt value:  " + value(cxt, "y", entry("x1", 10.0), entry("x2", 50.0)));
		assertEquals(value(cxt, "y", entry("x1", 10.0), entry("x2", 50.0)), 500.0);
	}
	
	@Test
	public void evaluatedContextTest() throws ExertionException, ContextException {
		Context<?> cxt = context(in(par("x1")), in(par("x2")), 
				in(par("y", invoker("e1", "x1 * x2", pars("x1", "x2")))));
		revaluable(cxt);
//		logger.info("cxt: " + cxt);

		//logger.info("cxt value:  " + value(cxt, "y", entry("x1", 10.0), entry("x2", 50.0)));
		assertEquals(value(cxt, "y", entry("x1", 10.0), entry("x2", 50.0)), 500.0);
	}
	
	@Test
	public void evaluatedContextWithResultTest() throws ExertionException, ContextException {
		Context<?> cxt = context(in(par("x1")), in(par("x2")), 
				in(par("y", invoker("e1", "x1 * x2", pars("x1", "x2")))),
				result("y"));
//		logger.info("cxt: " + cxt);
//		logger.info("return path: " + cxt.getReturnPath());
		revaluable(cxt);
//		logger.info("cxt2: " + cxt);
//		logger.info("cxt value:  " + value(cxt, entry("x1", 10.0), entry("x2", 50.0)));
		
		// No path for the evaluation is specified in the context cxt
		assertEquals(value(cxt, entry("x1", 10.0), entry("x2", 50.0)), 500.0);
	}
	
	@Test
	public void evaluateAcrossContextsTest() throws ExertionException, ContextException {
		Context<?> cxt = context(in(par("x1")), in(par("x2")), 
				in(par("y", invoker("e1", "x1 * x2", pars("x1", "x2")))),
				result("y"));
		revaluable(cxt);
		Context<?> cxt0 = context(in(par("x11", 10.0)), in(par("x21", 50.0)));
		logger.info("x11: " + value(cxt0, "x11"));
		logger.info("x21: " + value(cxt0,"x21"));
		
//		logger.info("cxt value:  " + value(cxt, entry("x1", value(cxt0, "x11")), entry("x2", value(cxt0,"x21"))));
		assertEquals(value(cxt, entry("x1", value(cxt0, "x11")), entry("x2", value(cxt0,"x21"))), 500.0);
	}
	
	@Test
	public void linkedContext() throws Exception {
		Context addContext = new PositionalContext("add");
		addContext.putInValue("arg1/value", 90.0);
		addContext.putInValue("arg2/value", 110.0);
		
		Context multiplyContext = new PositionalContext("multiply");
		multiplyContext.putInValue("arg1/value", 10.0);
		multiplyContext.putInValue("arg2/value", 70.0);
		
		ServiceContext invokeContext = new ServiceContext("invoke");
//		add additional tests with offset
//		invokeContext.putLink("add", addContext, "offset");
//		invokeContext.putLink("multiply", multiplyContext, "offset");
		
		invokeContext.putLink("add", addContext);
		invokeContext.putLink("multiply", multiplyContext);
		
		ContextLink addLink = (ContextLink)invokeContext.getLink("add");
		ContextLink multiplyLink = (ContextLink)invokeContext.getLink("multiply");
		
//		logger.info("invoke context: " + invokeContext);

//		logger.info("path arg1/value: " + addLink.getContext().getValue("arg1/value"));
		assertEquals(addLink.getContext().getValue("arg1/value"), 90.0);
//		logger.info("path arg2/value: " + multiplyLink.getContext().getValue("arg2/value"));
		assertEquals(multiplyLink.getContext().getValue("arg2/value"), 70.0);
//		logger.info("path add/arg1/value: " + invokeContext.getValue("add/arg1/value"));		
		assertEquals(invokeContext.getValue("add/arg1/value"), 90.0);
//		logger.info("path multiply/arg2/value: " + invokeContext.getValue("multiply/arg2/value"));		
		assertEquals(invokeContext.getValue("multiply/arg2/value"), 70.0);

	}
	
	@Test
	public void weakValueTest() throws Exception {
		Context cxt = context("add", in("arg/x1", 20.0), in("arg/x2", 80.0));
		
//		logger.info("arg/x1 = " + cxt.getValue("arg/x1"));
		assertEquals(cxt.getValue("arg/x1"), 20.0);
//		logger.info("val x1 = " + cxt.getValue("x1"));
		assertEquals(cxt.getValue("x1"), null);
//		logger.info("weak x1 = " + cxt.getWeakValue("arg/var/x1"));
		assertEquals(cxt.getWeakValue("arg/var/x1"), 20.0);
	}
}