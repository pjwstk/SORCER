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
import static sorcer.co.operator.header;
import static sorcer.co.operator.row;
import static sorcer.co.operator.table;
import static sorcer.co.operator.values;
import static sorcer.eo.operator.context;
import static sorcer.eo.operator.dispatcher;
import static sorcer.eo.operator.in;
import static sorcer.eo.operator.initialDesign;
import static sorcer.eo.operator.input;
import static sorcer.eo.operator.inputs;
import static sorcer.eo.operator.model;
import static sorcer.eo.operator.optiTask;
import static sorcer.eo.operator.optimizer;
import static sorcer.eo.operator.outerSig;
import static sorcer.eo.operator.parametricTask;
import static sorcer.eo.operator.responseTask;
import static sorcer.eo.operator.result;
import static sorcer.eo.operator.sig;
import static sorcer.eo.operator.strategy;
import static sorcer.eo.operator.value;
import static sorcer.po.operator.par;
import static sorcer.vo.operator.evaluate;
import static sorcer.vo.operator.parametricTable;
import static sorcer.vo.operator.responseTable;
import static sorcer.vo.operator.responses;
import static sorcer.vo.operator.setParametricTable;
import static sorcer.vo.operator.value;
import static sorcer.vo.operator.var;

import java.io.File;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import org.junit.Test;

import sorcer.core.context.model.explore.Explorer;
import sorcer.core.context.model.var.ParametricModel;
import sorcer.core.exertion.ModelTask;
import sorcer.model.geometry.RectModelBuilder;
import sorcer.service.ContextException;
import sorcer.service.ExertionException;
import sorcer.service.Signature;
import sorcer.service.SignatureException;
import sorcer.service.Strategy.Opti;
import sorcer.service.Task;
import sorcer.util.Sorcer;
import sorcer.vfe.ParametricModeling;
import sorcer.vfe.ResponseModeling;
import sorcer.vfe.util.Response;
import sorcer.vfe.util.Table;
import engineering.optimization.conmin.provider.ConminDispatcher;
import engineering.optimization.conmin.provider.ConminOptimizerJNA;
import engineering.optimization.conmin.provider.ConminState;
import engineering.optimization.conmin.provider.ConminStrategy;

@SuppressWarnings("unchecked")
public class VarModelsTest {

	private final static Logger logger = Logger.getLogger(VarModelsTest.class
			.getName());

	static {
		System.setProperty("java.security.policy", Sorcer.getHome()
				+ "/configs/policy.all");
		System.setSecurityManager(new RMISecurityManager());
		
		Sorcer.setCodeBase(new String[] { "rect-dl.jar",  "sorcer-prv-dl.jar" });
		System.setProperty("conmin.strategy.file", Sorcer.getHome()
				+ "/modules/engineering/optimization/conmin/data/conminrosenSuzukiMin.dat");
		System.setProperty("dispatcher.strategy.file", Sorcer.getHome()
				+ "/modules/engineering/junit/opti/mf-explorer/data/rsDispatcherStrategy.dat");
		System.setProperty("optimizer.data.dir", Sorcer.getHome()
				+ "/modules/engineering/optimization/conmin/data/test");
		
		System.setSecurityManager(new RMISecurityManager());
		Sorcer.setCodeBase(new String[] { "rs-explorer-10a-dl.jar",
				"conminoptimization-dl.jar", "sorcer-prv-dl.jar",
				"sorcer-modeling-lib.jar" });
		System.out.println("CLASSPATH :" + System.getProperty("java.class.path"));
	}

	@Test
	public void areaTest() throws ExertionException, ContextException,
			SignatureException, RemoteException {
		ParametricModel model = RectModelBuilder.getParametricModel();
		double inx = 1.0;
		double iny = 1.1;
		var(model, "x", inx);
		var(model, "y", iny);
		double area = (Double) value(model, "area");
		assertTrue((inx * iny) == area);
	}

	@Test
	public void aspectTest() throws ExertionException, ContextException,
			SignatureException, RemoteException {
		ParametricModel model = RectModelBuilder.getParametricModel();
		double inx = 1.0;
		double iny = 1.1;
		var(model, "x", inx);
		var(model, "y", iny);
		double result = (Double) value(model, "aspect");
		assertTrue((iny / inx) == result);
	}

	@Test
	public void perimeterTest() throws ExertionException, ContextException,
			SignatureException, RemoteException {
		ParametricModel model = RectModelBuilder.getParametricModel();
		double inx = 1.0;
		double iny = 1.0;
		var(model, "x", inx);
		var(model, "y", iny);
		double result = (Double) value(model, "perimeter");
		assertTrue(2 * (inx + iny) == result);
	}
	
	@Test
	public void intraRectResponseTest() throws SignatureException,
			ExertionException, ContextException {
		Signature sig = sig("getResponseModel", RectModelBuilder.class);
		ModelTask task = responseTask(
				outerSig("evaluateResponses", sig),
				context(inputs(in("x", 1.0), in("y", 1.0)),
						responses("area", "perimeter"), 
						result("model/responses")));

		Response response = (Response)value(task);
		logger.info("response: " + response);
//		logger.info("area: " + value(response, "area"));
		assertTrue(value(response, "area").equals( 1.0));
//		logger.info("perimeter: " + value(response, "perimeter"));
		assertTrue(value(response, "perimeter").equals( 4.0));

	}
	
	@Test
	public void interRectResponseTest() throws SignatureException,
			ExertionException, ContextException {
		Signature sig = sig(ResponseModeling.class, "Rect Parametric Model");
		ModelTask task = responseTask(
				outerSig("evaluateResponses", sig),
				context(inputs(in("x", 1.0), in("y", 1.0)),
						responses("area", "perimeter"), 
						result("model/responses")));

		Response response = (Response)value(task);
		logger.info("response: " + response);
////		logger.info("area: " + value(response, "area"));
//		assertTrue(value(response, "area").equals( 1.0));
////		logger.info("perimeter: " + value(response, "perimeter"));
//		assertTrue(value(response, "perimeter").equals( 4.0));
	}
	
	@Test
	public void evaluateResponseRectTableTest() throws ExertionException,
			ContextException, SignatureException, RemoteException {
		Table expected = table(header("x", "y", "area", "aspect", "perimeter"),
				row(1.0, 0.1, 0.1, 0.1, 2.2),
				row(1.0, 1.0, 1.0, 1.0, 4.0),
				row(1.0, 2.0, 2.0, 2.0, 6.0),
				row(2.0, 2.0, 4.0, 1.0, 8.0));	
//		logger.info("expected output table:\n" + expected);
		
		Table dataTable = table(header("x", "y"), 
				row(1.0, 0.1),
				row(1.0, 1.0), 
				row(1.0, 2.0), 
				row(2.0, 2.0));
	
		ParametricModel model = RectModelBuilder.getParametricModel();
		evaluate(setParametricTable(model, dataTable));
//		logger.info("table of results:\n" + responseTable(model));
		assertTrue(responseTable(model).equals(expected));
	}

	@Test
	public void execParametricRectTaskTest() throws ExertionException, ContextException,
			SignatureException, RemoteException {
		Signature sig = sig("getParametricModel", RectModelBuilder.class);

		String dataURL = Sorcer.getWebsterUrl()
				+ "/geometry/parametric_datafile.txt";
		String outputURL = Sorcer.getWebsterUrl()
				+ "/geometry/response_data.txt";

		ModelTask task = parametricTask(
				outerSig("evaluateResponseTable", sig), 
					context(parametricTable(dataURL, ", "),
						responseTable(outputURL, ", "), result("table/out")));

		 Table resultTable = (Table) value(task);

		Table expected = table(header("x", "y", "area", "aspect", "perimeter"),
				row(1.0, 1.0, 1.0, 1.0, 4.0),
				row(1.0, 2.0, 2.0, 2.0, 6.0),
				row(2.0, 1.0, 2.0, 0.5, 6.0),
				row(2.0, 2.0, 4.0, 1.0, 8.0),
				row(1.1, 1.0, 1.1, 0.9090909090909091, 4.2),
				row(0.1, 0.1, 0.010000000000000002, 1.0, 0.4));
		
//		logger.info("table of results:\n" + resultTable);
		assertTrue(resultTable.equals(expected));
	}
	
	@Test
	public void exertParametricRectTaskTest() throws ExertionException, ContextException,
			SignatureException, RemoteException {
		Signature sig = sig(ParametricModeling.class, "Rect Parametric Model");
		String dataURL = Sorcer.getWebsterUrl()
				+ "/geometry/parametric_datafile.txt";
		String outputURL = Sorcer.getWebsterUrl()
				+ "/geometry/response_data.txt";
		
		ModelTask task = parametricTask(
				outerSig("evaluateResponseTable", sig), 
				sorcer.eo.operator.context(parametricTable(dataURL, ", "),
						responseTable(outputURL, ", "), result("table/out")));

		Table resultTable = (Table) value(task);
		Table expected = table(header("x", "y", "area", "aspect", "perimeter"),
				row(1.0, 1.0, 1.0, 1.0, 4.0),
				row(1.0, 2.0, 2.0, 2.0, 6.0),
				row(2.0, 1.0, 2.0, 0.5, 6.0),
				row(2.0, 2.0, 4.0, 1.0, 8.0),
				row(1.1, 1.0, 1.1, 0.9090909090909091, 4.2),
				values(0.1, 0.1, 0.010000000000000002, 1.0, 0.4));
		
		logger.info("table of results:\n" + resultTable);
		assertTrue(resultTable.equals(expected));
	}
	
	@Test
	public void optiRectTaskTest() throws Exception {
		Task optiTask = optiTask(sig("explore", Explorer.class),
				context(initialDesign(input("x", 20.0), input("y", 30.0)),
						par("optimizer/strategy", new ConminStrategy(new File(System.getProperty("conmin.strategy.file")))),
						result("exploration/results")),
						strategy(Opti.MIN, 
								dispatcher(sig(ConminDispatcher.class)),
								model(sig("getOptimizationModel", RectModelBuilder.class)),
								optimizer(sig(ConminOptimizerJNA.class))));						

		ConminState cs = (ConminState)value(optiTask);
		//			logger.info(">>>>>>>>>>>>> exploration results: " + cs);
		logger.info("exploration results: " + cs);
		logger.info("CONMIN Iteration #: " + cs.getIter());
		logger.info("Objective Function: " + cs.getOBJ());
		logger.info("Number of Objective Evaluations: " + cs.numObjEvals());
		logger.info("Number of Constraint Evaluations: " + cs.numConEvals());
		logger.info("Number of Objective Gradient Evaluations: " + cs.numObjGradEvals());
		logger.info("Number of Objective Gradient Evaluations: " + cs.numObjConEvals());

		assertEquals(7, cs.getIter());
		assertEquals(9958.941236707842, cs.getOBJ(), 0.25);
		assertEquals(12, cs.numObjEvals());
		assertEquals(12, cs.numConEvals());
		assertEquals(6, cs.numObjGradEvals());
		assertEquals(6, cs.numObjConEvals());
	}
	
}