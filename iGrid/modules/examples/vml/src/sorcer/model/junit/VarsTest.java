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

import static org.junit.Assert.assertTrue;
import static sorcer.co.operator.entry;
import static sorcer.eo.operator.context;
import static sorcer.eo.operator.in;
import static sorcer.eo.operator.sig;
import static sorcer.eo.operator.value;
import static sorcer.vo.operator.cmdEvaluator;
import static sorcer.vo.operator.contextFilter;
import static sorcer.vo.operator.expression;
import static sorcer.vo.operator.fieldFilter;
import static sorcer.vo.operator.groovy;
import static sorcer.vo.operator.jep;
import static sorcer.vo.operator.methodEvaluator;
import static sorcer.vo.operator.methodFilter;
import static sorcer.vo.operator.obeyBounds;
import static sorcer.vo.operator.propertiesFilter;
import static sorcer.vo.operator.proxy;
import static sorcer.vo.operator.set;
import static sorcer.vo.operator.stringToDouble;
import static sorcer.vo.operator.var;
import static sorcer.vo.operator.vars;

import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sorcer.model.geometry.ContextualRectImpl;
import sorcer.model.geometry.ParametricRectImpl;
import sorcer.model.geometry.Rectangle;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.EvaluationException;
import sorcer.service.ExertionException;
import sorcer.service.SignatureException;
import sorcer.util.Sorcer;
import sorcer.vfe.ParametricModeling;
import sorcer.vfe.ServiceEvaluator.SPI;
import sorcer.vfe.Var;

@SuppressWarnings({"rawtypes", "unchecked"})
public class VarsTest {

	private final static Logger logger = Logger.getLogger(VarsTest.class
			.getName());

	static {
		System.setProperty("java.security.policy", Sorcer.getHome()
				+ "/configs/policy.all");
		System.setSecurityManager(new RMISecurityManager());
		Sorcer.setCodeBase(new String[] { "rect-dl.jar",  "sorcer-prv-dl.jar" });
		System.out.println("CLASSPATH :" + System.getProperty("java.class.path"));
	}
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void independentVarTest() throws EvaluationException  {
		thrown.expect(EvaluationException.class);
		thrown.expectMessage("attempted to set its value");
		
		Var<Double> x = var("x", 2.0);
		assertTrue(value(x) == 2.0);
		
		set(x, 3.0);
		assertTrue(value(x) == 3.0);

		// bounded vars
		Var<Double> y = var("x", 20.0, 0.0, 50.0);
		assertTrue(value(y) == 20.0);
		
		set(y, -1.0);
		// default is ignoreBounds = true;
		logger.info("y obey bounds 20.0-50.0 for -1.0: " + value(y)); 
		assertTrue(value(y) == -1.0);
		
		set(y, 100.0);
		logger.info("disobey bounds 20.0-50.0 for 100.0 y: " + value(y)); 
		assertTrue(value(y) == 100.0);
//		obeyBounds(y); // throws EvaluationException
		obeyBounds(y);
		logger.info("obey bounds 20.0-50.0 for 100.0 y: " + value(y)); 
		assertTrue(value(y) == 50.0);
	}
	
	@Test
	public void expressionVarTest() throws EvaluationException  {
		Var x = var("x", 2.0);
		Var y = var("y", 3.0);
		Var exprArea = var("area", expression("x * y", vars(x, y), SPI.JEP));
		Var gvyArea = var("area", groovy("x * y", vars(x, y)));
		Var jepArea = var("area", jep("x * y", vars(x, y)));
		
		assertTrue((Double)value(exprArea) == 6.0);
		assertTrue((Double)value(gvyArea) == 6.0);
		assertTrue((Double)value(jepArea) == 6.0);
	}
	
	@Test
	public void freeExpressionVarTest() throws EvaluationException  {
		Var area = var("area", groovy("x * y", vars("x", "y")));
		
		//logger.info("area value: " + value(area));
		assertTrue((Double)value(area, entry("x", 2.0), entry("y", 3.0)) == 6.0);
	}
	
	@Test
	public void parametricAreaMethodEvaluatorVarTest() throws EvaluationException  {
		Var area = var("area", methodEvaluator(ParametricRectImpl.class, "area", 
							new Class[] { double.class, double.class },
							new Object[] { 2.0, 3.0 }));
		
		//logger.info("area value: " + value(area));
		assertTrue((Double)value(area) == 6.0);
	}
	
	@Test
	public void contextualAreaMethodEvaluatorVarTest() throws ContextException  {
		Var area = var("area", 
				methodEvaluator(ContextualRectImpl.class, "area", 
					new Class[] { Context.class },
					new Object[] { context(in("arg/x", 2.0), in("arg/y", 3.0)) }),
				contextFilter("area"));
		
		//logger.info("area value: " + value(area));
		assertTrue((Double)value(area) == 6.0);
	}
	
	@Test
	public void fieldFilterlVarTest() throws EvaluationException  {
		Object object = new Rectangle(3.0, 2.0);
		Var x = var("x", fieldFilter(object, "length"));
		Var y = var("y", fieldFilter(object, "width"));
		
		logger.info("length value: " + value(x));
		logger.info("width value: " + value(y));
		
		assertTrue((Double)value(x) == 3.0);
		assertTrue((Double)value(y) == 2.0);
		
		Var area = var("area", expression("x * y", vars(x, y)));
		assertTrue((Double)value(area) == 6.0);
	}
	
	@Test
	public void methodFilterlVarTest() throws EvaluationException  {
		Var x = var("x", 2.0);
		Var y = var("y", 3.0);
		Object rect = new Rectangle(x, y);
		
		// the object 'rect' depends on two vars x, and y,
		// it becomes the value of the var 'area', however
		// the value of the var is filtered out from the object 'rect' 
		// with its ' method 'getArea'
		Var area = var("area", rect, methodFilter("getArea"));
		assertTrue((Double)value(area) == 6.0);
		
		// when object 'rect' changes then the value 
		// of the 'area' var changes accordingly
		set(x, 3.0);
		set(y, 4.0);
		assertTrue((Double)value(area) == 12.0);
	}
	
	// A command line system call for the value of var with a PropertiesFilter
	// and a stringToDouble converter reading the the standard out
	@Test
	public void cmdStremmVarTest() throws ContextException  {
		String jar = Sorcer.getHome() + "/lib/sorcer/lib/out-rect.jar ";
		Var area = var("area", 
				cmdEvaluator("java -jar " + jar + "area 2.0 3.0"),
				propertiesFilter("area"), stringToDouble());
		
		// with no stringToDouble() converter
		// assertTrue(value(area).equals("6.0")); 
		assertTrue((Double)value(area) == 6.0);
	}

	
	// A command line system call for the value of var with a PropertiesFilter
	// and a stringToDouble converter reading the the result from a given file (tmp/rect-out)
	// The integration with a created file by the system call is one by 
	// the property 'out-file' written to the standard out (see FileRectCalculator.java)
	// that is interpreted by the propertiesFilter (the PropertiesFilter class).
	// In that case the created file by the system call can be used by others as well.
	@Test
	public void cmdFileVarTest() throws ContextException  {
		String jar = Sorcer.getHome() + "/lib/sorcer/lib/file-rect.jar ";
		Var area = var("area", 
				cmdEvaluator("java -jar " + jar + "area 2.0 3.0 tmp/rect-out"),
				propertiesFilter("area"), stringToDouble());
		
		// with no stringToDouble() converter
		// assertTrue(value(area).equals("6.0")); 
		assertTrue((Double)value(area) == 6.0);
	}
	
	@Test
	public void multiRemoteAliasingVarTest() throws ContextException, ExertionException,
			SignatureException {

		Var y1 = var("y1", proxy("y", sig(ParametricModeling.class, 
					"Rect Parametric Model")));
		//logger.info("x value: " + value(x));
		set(y1, 10.0);
		//logger.info("x value: " + value(x));
		assertTrue((Double)value(y1) == 10.0);
		
		Var x1 = var("x1", proxy("x", sig(ParametricModeling.class, 
					"Rect Parametric Model")));
		//logger.info("x1 value: " + value(x1));
		set(x1, 20.0);
		//logger.info("x1 value: " + value(x1));
		assertTrue((Double)value(x1) == 20.0);

		Var area = var("area", groovy("x1 * y1", vars(x1, y1)));
		
		//logger.info("area value: " + value(area));
		assertTrue((Double)value(area) == 200.0);
	}
	
}