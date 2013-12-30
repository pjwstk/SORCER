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
import static sorcer.eo.operator.args;
import static sorcer.eo.operator.context;
import static sorcer.eo.operator.in;
import static sorcer.eo.operator.job;
import static sorcer.eo.operator.out;
import static sorcer.eo.operator.parameterTypes;
import static sorcer.eo.operator.parameterValues;
import static sorcer.eo.operator.path;
import static sorcer.eo.operator.pipe;
import static sorcer.eo.operator.result;
import static sorcer.eo.operator.sig;
import static sorcer.eo.operator.task;
import static sorcer.eo.operator.value;
import static sorcer.vo.operator.var;

import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import org.junit.Test;

import sorcer.core.provider.jobber.ServiceJobber;
import sorcer.model.geometry.ContextualCircleImpl;
import sorcer.model.geometry.ContextualRect;
import sorcer.model.geometry.ContextualRectImpl;
import sorcer.model.geometry.ParametricCircleImpl;
import sorcer.model.geometry.ParametricRect;
import sorcer.model.geometry.ParametricRectImpl;
import sorcer.model.geometry.RemoteContextualRect;
import sorcer.model.geometry.RemoteParametricRect;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.ExertionException;
import sorcer.service.Job;
import sorcer.service.SignatureException;
import sorcer.service.Task;
import sorcer.util.Sorcer;
import sorcer.vfe.Var;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ExertVarsTest {

	private final static Logger logger = Logger.getLogger(ExertVarsTest.class
			.getName());

	static {
		System.setProperty("java.security.policy", Sorcer.getHome()
				+ "/configs/policy.all");
		System.setSecurityManager(new RMISecurityManager());
		Sorcer.setCodeBase(new String[] { "rect-dl.jar",  "sorcer-prv-dl.jar" });
		System.out.println("CLASSPATH :" + System.getProperty("java.class.path"));
	}
	
	@Test
	public void contextualRectImplTaskVarTest() throws ContextException, ExertionException,
			SignatureException {
		Task implTask = task("area", 
				sig("area", ContextualRectImpl.class),
				context(in("arg/x", 2.0), in("arg/y", 3.0), result("area")));
		
		double value = (Double)value(implTask);
		logger.info("area: " + value);
		assertTrue(value == 6.0);
		
		Var area = var("area", implTask);
		//logger.info("area value: " + value(area));
		assertTrue((Double)value(area) == 6.0);
	}
	
	@Test
	public void contextualRectTaskVarTest() throws ContextException, ExertionException,
			SignatureException {
		Task netTask = task("area", 
				sig("area", ContextualRect.class),
				context(in("arg/x", 2.0), in("arg/y", 3.0), result("area")));
		
		double value = (Double)value(netTask);
		logger.info("area: " + value);
		assertTrue(value == 6.0);
		
		Var area = var("area", netTask);
		//logger.info("area value: " + value(area));
		assertTrue((Double)value(area) == 6.0);
	}
	
	@Test
	public void remoteContextualRectTaskVarTest() throws ContextException, ExertionException,
			SignatureException {
		Task netTask = task("area", 
				sig("area", RemoteContextualRect.class),
				context(in("arg/x", 2.0), in("arg/y", 3.0), result("area")));
		
		double value = (Double)value(netTask);
		logger.info("area: " + value);
		assertTrue(value == 6.0);
		
		Var area = var("area", netTask);
		//logger.info("area value: " + value(area));
		assertTrue((Double)value(area) == 6.0);
	}
	
	@Test
	public void parametricCircleImplTaskVarTest() throws ContextException, ExertionException,
			SignatureException {
		// parameterTypes the list of parameters
		// args the arguments used for the method call
		Task imlParmTask = task("area", 
				sig("area", ParametricCircleImpl.class),
				context(parameterTypes(double.class), 
						args(2.0), 
						result("area")));
		
		double value = (Double)value(imlParmTask);
		//logger.info("area: " + value);
		assertTrue(value == 12.566370614359172);
		
		Var area = var("area", imlParmTask);
		//logger.info("area value: " + value(area));
		assertTrue((Double)value(area) == 12.566370614359172);
	}
	
	@Test
	public void parametricRectImplTaskVarTest() throws ContextException, ExertionException,
			SignatureException {
		// parameterTypes the list of parameters
		// args the arguments used for the method call
		Task imlParmTask = task("area", 
				sig("area", ParametricRectImpl.class),
				context(parameterTypes(double.class, double.class), 
						args(2.0, 3.0), 
						result("area")));
		
		double value = (Double)value(imlParmTask);
		logger.info("area: " + value);
		assertTrue(value == 6.0);
		
		Var area = var("area", imlParmTask);
		//logger.info("area value: " + value(area));
		assertTrue((Double)value(area) == 6.0);
	}
	
	@Test
	public void parametricRectTaskVarTest() throws ContextException, ExertionException,
			SignatureException {
		// parameterTypes the list of parameters
		// args the arguments used for the method call
		Task netTask = task("area", 
				sig("area", ParametricRect.class),
				context(parameterTypes(double.class, double.class), 
						args(2.0, 3.0), 
						result("area")));
		
		double value = (Double)value(netTask);
		logger.info("area: " + value);
		assertTrue(value == 6.0);
		
		Var area = var("area", netTask);
		//logger.info("area value: " + value(area));
		assertTrue((Double)value(area) == 6.0);
	}
	
	@Test
	public void remoteParametricRectTaskVarTest() throws ContextException, ExertionException,
			SignatureException {
		// parameterTypes the list of parameters
		// args the arguments used for the method call
		Task netTask = task("area", 
				sig("area", RemoteParametricRect.class),
				context(parameterTypes(double.class, double.class), 
						args(2.0, 3.0), 
						result("area")));
		
		double value = (Double)value(netTask);
		logger.info("area: " + value);
		assertTrue(value == 6.0);
		
		Var area = var("area", netTask);
		//logger.info("area value: " + value(area));
		assertTrue((Double)value(area) == 6.0);
	}
	
	@Test
	public void rectCircleContextualJobVarTest() throws ExertionException,
			SignatureException, ContextException {

		Task rectTask = task("area", 
				sig("area", ContextualRectImpl.class),
				context(in("arg/x", 2.0), in("arg/y", 3.0), result("area")));

		Task circleTask = task(
				"circumference",
				sig("circumference", ContextualCircleImpl.class),
				context(in("arg/radius"), result("result")));
		
		Job rcJob = job("rc", sig("execute", ServiceJobber.class),
				context(result("rc/circumference/result")),
				rectTask, circleTask,
				pipe(out(rectTask, "area"), 
					 in(circleTask, "arg/radius")));
		
//		logger.info("context: " + jobContext(exert(rcJob)));
		double value = (Double)value(rcJob);
		logger.info("circumference: " + value);
		assertTrue(value == 37.69911184307752);
		
		Var area = var("area", rcJob);
//		logger.info("area value: " + value(area));
		assertTrue((Double)value(area) == 37.69911184307752);
	}
	
	@Test
	public void rectCircleParametricJobVarTest() throws ExertionException,
			SignatureException, ContextException {

		Task rectTask = task(
				"area",
				sig("area", ParametricRectImpl.class),
				context(parameterTypes(double.class, double.class),
						parameterValues(2.0, 3.0), result("area")));
		
		Task circleTask = task(
				"circumference",
				sig("circumference", ParametricCircleImpl.class),
				context(parameterTypes(double.class),
						parameterValues(),
						result("result")));
		
		Job rcJob = job("rc", sig("execute", ServiceJobber.class),
				context(result("rc/circumference/result")),
				rectTask, circleTask,
				 pipe(out(rectTask, path("area")), 
					  in(circleTask, path(Context.PARAMETER_VALUES + "[0]"))));
		
		double value = (Double)value(rcJob);
		//logger.info("circumference: " + value);
		assertTrue(value == 37.69911184307752);
		
		Var area = var("area", rcJob);
//		logger.info("area value: " + value(area));
		assertTrue((Double)value(area) == 37.69911184307752);
	}
	
	@Test
	public void rectCircleMixedParametricJobVarTest() throws ExertionException,
			SignatureException, ContextException {

		Task rectTask = task("area", 
				sig("area", ContextualRectImpl.class),
				context(in("arg/x", 2.0), in("arg/y", 3.0), result("area")));
		
		Task circleTask = task(
				"circumference",
				sig("circumference", ParametricCircleImpl.class),
				context(parameterTypes(double.class),
						parameterValues(),
						result("result")));
		
		Job rcJob = job("rc", sig("execute", ServiceJobber.class),
				context(result("rc/circumference/result")),
				rectTask, circleTask,
				 pipe(out(rectTask, path("area")), 
					  in(circleTask, path(Context.PARAMETER_VALUES + "[0]"))));
		
		double value = (Double)value(rcJob);
		//logger.info("circumference: " + value);
		assertTrue(value == 37.69911184307752);
		
		Var area = var("area", rcJob);
//		logger.info("area value: " + value(area));
		assertTrue((Double)value(area) == 37.69911184307752);
	}
}