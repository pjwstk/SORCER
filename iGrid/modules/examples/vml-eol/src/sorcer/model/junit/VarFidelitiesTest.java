/*
 * Copyright 2012 the original author or authors.
 * Copyright 2012 SorcerSoft.org.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sorcer.model.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static sorcer.co.operator.entry;
import static sorcer.eo.operator.args;
import static sorcer.eo.operator.context;
import static sorcer.eo.operator.in;
import static sorcer.eo.operator.parameterTypes;
import static sorcer.eo.operator.result;
import static sorcer.eo.operator.sig;
import static sorcer.eo.operator.task;
import static sorcer.eo.operator.value;
import static sorcer.vo.operator.addFidelity;
import static sorcer.vo.operator.cmdEvaluator;
import static sorcer.vo.operator.contextFilter;
import static sorcer.vo.operator.expression;
import static sorcer.vo.operator.fi;
import static sorcer.vo.operator.fidelity;
import static sorcer.vo.operator.fieldFilter;
import static sorcer.vo.operator.groovy;
import static sorcer.vo.operator.jep;
import static sorcer.vo.operator.methodEvaluator;
import static sorcer.vo.operator.propertiesFilter;
import static sorcer.vo.operator.proxy;
import static sorcer.vo.operator.selectFidelity;
import static sorcer.vo.operator.set;
import static sorcer.vo.operator.stringToDouble;
import static sorcer.vo.operator.var;
import static sorcer.vo.operator.vars;

import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import org.junit.Test;

import sorcer.core.context.model.var.ParametricModel;
import sorcer.model.geometry.ContextualRect;
import sorcer.model.geometry.ContextualRectImpl;
import sorcer.model.geometry.ParametricRect;
import sorcer.model.geometry.ParametricRectImpl;
import sorcer.model.geometry.RectModelBuilder;
import sorcer.model.geometry.Rectangle;
import sorcer.model.geometry.RemoteContextualRect;
import sorcer.model.geometry.RemoteParametricRect;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.ExertionException;
import sorcer.service.SignatureException;
import sorcer.service.Task;
import sorcer.util.Sorcer;
import sorcer.vfe.Fidelity;
import sorcer.vfe.FilterException;
import sorcer.vfe.ParametricModeling;
import sorcer.vfe.ServiceEvaluator.SPI;
import sorcer.vfe.Var;

@SuppressWarnings({"rawtypes", "unchecked"})
public class VarFidelitiesTest {

	private final static Logger logger = Logger.getLogger(VarFidelitiesTest.class
			.getName());

	static {
		System.setProperty("java.security.policy", Sorcer.getHome()
				+ "/configs/policy.all");
		System.setSecurityManager(new RMISecurityManager());
		Sorcer.setCodeBase(new String[] { "rect-dl.jar",  "sorcer-prv-dl.jar" });
		System.out.println("CLASSPATH :" + System.getProperty("java.class.path"));
	}
	
	private Task objectTask;
	private Task netTask;
	private Task remoteTask;
	private Task objParmTask;
	private Task netParamTask;
	private Task remoteParamTask;
	private ParametricModel model;
	
	@Test
	public void multiFidelityTest() throws ContextException,
			ExertionException, SignatureException, FilterException {
		
		model = RectModelBuilder.getParametricModel();
		
		objectTask = task("area", 
			sig("area", ContextualRectImpl.class),
			context(in("arg/x", 2.0), in("arg/y", 3.0), result("area")));
		
		netTask = task("area", 
			sig("area", ContextualRect.class), 
			context(in("arg/x", 2.0), in("arg/y", 3.0),
				result("area")));
		
		remoteTask = task("area", 
			sig("area", RemoteContextualRect.class), 
			context(in("arg/x", 2.0), in("arg/y", 3.0),
				result("area")));
		
		objParmTask = task("area", 
			sig("area", ParametricRectImpl.class),
			context(parameterTypes(double.class, double.class), 
				args(2.0, 3.0), 
				result("area")));
		
		netParamTask = task("area", 
			sig("area", ParametricRect.class),
			context(parameterTypes(double.class, double.class), 
				args(2.0, 3.0), 
				result("area")));
		
		remoteParamTask = task("area", 
			sig("area", RemoteParametricRect.class),
			context(parameterTypes(double.class, double.class), 
				args(2.0, 3.0), 
				result("area")));
		
		// a jar for the system call with the cmdFi fidelity
		String jarFile = Sorcer.getHome() + "/lib/sorcer/lib/out-rect.jar ";
		
		// proxy vars x1 and y1 from net parametric model used in fidelity netVarFi
		Var x1 = var("x1", proxy("x", sig(ParametricModeling.class, "Rect Parametric Model")));
		set(x1, 2.0);
		Var y1 = var("y1", proxy("y", sig(ParametricModeling.class, "Rect Parametric Model")));
		set(y1, 3.0);
		
		Object rect = new Rectangle(x1, y1);
		
		// sixteen architectural var fidelities
		// architectural fidelities can be used to achieve desired
		// integrations and interoperability for various data formats and services
		Var area = var("area", 
				// 1) fidelity with an expression evaluator, with a given SPI - service provider interface
				fidelity("xprFi", expression("x * y", vars(var("x", 2.0), var("y", 3.0)), SPI.JEP)), //JEP
				// 2) fidelity with a JEP evaluator
				fidelity("jepFi", jep("x * y", vars(var("x", 2.0), var("y", 3.0)))), 
				// 3) fidelity with a Groovy evaluator
				fidelity("gvyFi", groovy("x * y", vars(var("x", 2.0), var("y", 3.0)))),
				// 4) fidelity with an evaluator of another var in the underlying model (var aliasing)
				fidelity("modelFi", var("area", model)), 
				// 5) fidelity with an evaluator of another var in the underlying remote model (var remote aliasing)
				fidelity("proxyFi", proxy("area", sig(ParametricModeling.class, "Rect Parametric Model"))),
				// 6) fidelity with a method evaluator
				fidelity("mtdFi", methodEvaluator(ParametricRectImpl.class, "area", 
						new Class[] { double.class, double.class },
						new Object[] { 2.0, 3.0 })),
				// 7) fidelity with a context method task evaluator and a context filter
				fidelity("cxtMtdFi", methodEvaluator(ContextualRectImpl.class, "area", 
						new Class[] { Context.class },
						new Object[] { context(in("arg/x", 2.0), in("arg/y", 3.0)) }), 
					contextFilter("area")),
				// 8) fidelity with an object task evaluator
				fidelity("objCxtFi", objectTask),
				// 9) fidelity with an net task evaluator
				fidelity("netCxtFi", netTask),
				// 10) fidelity with an net task evaluator
				fidelity("remoteCxtFi", remoteTask),
				// 11) fidelity with an net task evaluator
				fidelity("objParamFi", objParmTask),
				// 12) fidelity with an net task evaluator
				fidelity("netParamFi", netParamTask),
				// 13) fidelity with an net task evaluator
				fidelity("remoteParamFi", remoteParamTask),
				// 14) fidelity with a CmdEvaluator evaluator for a system call and two filters
				fidelity("cmdFi", cmdEvaluator("java -jar " + jarFile + "area 2.0 3.0"),
						propertiesFilter("area"), stringToDouble()));
		
		// 15) linked net var fidelity with a two aliased net proxy vars x1 and y1
		addFidelity(area, fidelity("netVarFi", groovy("x1 * y1", vars(x1, y1))));
		
		Object object = new Rectangle(3.0, 2.0);
		Var x = var("x", fieldFilter(object, "length"));
		Var y = var("y", fieldFilter(object, "width"));
		// 16) linked var to object fields length and width
		addFidelity(area, fidelity("objFilterVarFi", groovy("x * y", vars(x, y))));
		
//		logger.info("area: " + value(area, fi("cxtMtdFi")));

		// to avoid side effects from previous model evaluations 
		assertEquals(value(area, fi("xprFi")), 6.0);
		assertEquals(value(area, fi("jepF")), 6.0);
		assertEquals(value(area, fi("gvyF")), 6.0);
		assertEquals(value(area, fi("modelFi")), 6.0);
		assertEquals(value(area, fi("proxyFi"), entry("x", 2.0), entry("y", 3.0)), 6.0);
		assertEquals(value(area, fi("mtdF")), 6.0);
		assertEquals(value(area, fi("cxtMtdFi")), 6.0);
		assertEquals(value(area, fi("objCxtFi")), 6.0);
		assertEquals(value(area, fi("netCxtFi")), 6.0);
		assertEquals(value(area, fi("remoteCxtFi")), 6.0);
		assertEquals(value(area, fi("objParamFi")), 6.0);
		assertEquals(value(area, fi("netParamFi")), 6.0);
		assertEquals(value(area, fi("remoteParamFi")), 6.0);
		assertEquals(value(area, fi("cmdFi")), 6.0);
		assertEquals(value(area, fi("netVarFi")), 6.0);
		assertEquals(value(area, fi("objFilterVarFi")), 6.0);
		
		// testing fidelity selection
		logger.info("area fidelity: " + fidelity(area));
		assertTrue(fidelity(area).equals("objFilterVarFi"));
		selectFidelity(area, "jepF");
		logger.info("area fidelity: " + fidelity(area));
		assertTrue(fidelity(area).equals("jepF"));
	}

}