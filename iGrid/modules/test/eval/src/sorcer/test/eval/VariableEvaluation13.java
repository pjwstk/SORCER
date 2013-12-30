/*
 * Copyright 2009 the original author or authors.
 * Copyright 2009 SorcerSoft.org.
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

package sorcer.test.eval;

import static sorcer.co.operator.names;
import static sorcer.co.operator.duo;
import static sorcer.co.operator.values;
import static sorcer.vo.operator.designVars;
import static sorcer.vo.operator.linkedVars;
import static sorcer.vo.operator.output;
import static sorcer.vo.operator.parametricModel;
import static sorcer.vo.operator.parametricTable;
import static sorcer.vo.operator.responseModel;
import static sorcer.vo.operator.outputVars;

import java.io.IOException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.config.ConfigurationException;
import sorcer.core.context.model.var.ParametricModel;
import sorcer.core.context.model.var.ResponseModel;
import sorcer.core.context.model.var.VarModel;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.EvaluationException;
import sorcer.service.ExertionException;
import sorcer.service.SignatureException;
import sorcer.util.Log;
import sorcer.vfe.ServiceEvaluator;
import sorcer.vfe.VarException;
import sorcer.vfe.evaluator.JepEvaluator;

/**
 * Example on how to create a Response and Parametric models
 */
@SuppressWarnings("unchecked")
public class VariableEvaluation13 {

	private static Logger logger = Log.getTestLog();

	public static void main(String[] args) throws Exception {
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());

		int test = new Integer(args[0]);
		switch (test) {
		case 1: doResponseAnalysis(); break;
		case 2: doResponseAnalysisTest(); break;
		case 3: doParametricAnalysis(); break;
		case 4: getResponseModel1(); break;
		}
	}
	
	public static void doResponseAnalysis() throws ParException, RemoteException,
	EvaluationException, ConfigurationException, ContextException, ExertionException, SignatureException {
		// test ResponseModel
		// response "xl1 + 2 * x2^2 + 5 * x4 + 4 * x3^3"
		// linked variable "x1 + 3 * x3^2"
		ResponseModel rm = getResponseModel();
		
		rm.setDesignVarValues(duo("x1", 1), duo("x2", 2), duo("x3", 3), duo("x4", 4));

		logger.info("\n\nresponse: " + rm.getResponseValue("y1"));
		logger.info("\n\nresponses: " + rm.getResponses());
	}
	
	public static void doResponseAnalysisTest() throws ParException, RemoteException,
	EvaluationException, ConfigurationException, ContextException, ExertionException, SignatureException {
		// test ResponseModel
		// response "xl1 + 2 * x2^2 + 5 * x4 + 4 * x3^3"
		// linked variable "x1 + 3 * x3^2"
		ResponseModel rm = getResponseModel();
		
		rm.setDesignVarValues(duo("x1", 1), duo("x2", 2), duo("x3", 3), duo("x4", 4));
		logger.info("\n\nx2 value: " +rm.getInputValue("x1"));
		logger.info("\n\nx3 value: " +rm.getInputValue("x3"));
		logger.info("\n\nxl1 value: " +rm.getLinkedValue("xl1"));
		logger.info("\n\nresponse: " + rm.getResponseValue("y1"));
		//rm.evaluate();
	}

	public static void doParametricAnalysis() throws ParException, EvaluationException, 
	ConfigurationException, ContextException, ExertionException, SignatureException, IOException {
		// test ParametricModel
		// test ResponseModel
		// response "xl1 + 2 * x2^2 + 5 * x4 + 4 * x3^3"
		// linked variable "x1 + 3 * x3^2"
		ParametricModel pm = getParametricModel();
		pm.evaluate();
		
		logger.info("\n\nParametricAnalysis ouput table: " + pm.getParametricTable());
		logger.info("\n\nParametricAnalysis parametric table: " + pm.getOutTable());
	}
	
	public static ResponseModel getResponseModel() throws ParException,
			RemoteException, EvaluationException, ConfigurationException,
			ContextException, ExertionException, SignatureException {

		int designVarCount = 4;
		int responseVarCount = 1;
		int linkedVarCount = 1;

		// create a parametric analysis model
		// response "xl1 + 2 * x2^2 + 5 * x4 + 4 * x3^3"
		// linked variable "x1 + 3 * x3^2"
		ResponseModel rm = responseModel("Response Analysis", 
				designVars("x", designVarCount), 
				linkedVars("xl", linkedVarCount), 
				outputVars("y", responseVarCount));

		configureResponseModel(rm);
		logger.info("literal context: " + rm);

		return rm;
	}
	
	private static Context configureResponseModel(ResponseModel model)
			throws ContextException, EvaluationException, RemoteException {
		// setup evaluators and filters for model variables
		
		// configure linked variable
		ServiceEvaluator x1le = new JepEvaluator("x1le", "x1 + 3 * x3^2");
		model.setLinkedEvaluator("xl1", x1le);
		x1le.addArg(model.getDesignVar("x1")).
			addArg(model.getDesignVar("x3"));
		
		// configure response variable
		ServiceEvaluator y1e = new JepEvaluator("y1e",
				"xl1 + 2 * x2^2 + 5 * x4 + 4 * x3^3");
		model.setResponseEvaluator("y1", y1e);
		y1e.addArg(model.getLinkedVar("xl1")).
			addArg(model.getDesignVar("x2"));
		y1e.addArg(model.getDesignVar("x3")).
			addArg(model.getDesignVar("x4"));
		
		return model;
	}
	
	public static ParametricModel getParametricModel() throws ParException,
			EvaluationException, ConfigurationException,
			ContextException, ExertionException, SignatureException, IOException {

		int designVarCount = 4;
		int responseVarCount = 1;
		int linkedVarCount = 1;

		// create a parametric analysis model
		// response "xl1 + 2 * x2^2 + 5 * x4 + 4 * x3^3"
		// linked variable "x1 + 3 * x3^2"  
		ParametricModel model = parametricModel("Parametric Analysis",
			designVars("x", designVarCount), 
			linkedVars("xl", linkedVarCount), 
			outputVars("y", responseVarCount), 
			parametricTable(
				names("x1","x2","x3", "x4"), 
				values(1, 2, 3, 4),
				values(2, 4, 6, 8),
				values(4, 8, 12, 16),
				output("http://127.0.0.1/data/analysis.data")));

		configureParametricModel(model);
		logger.info("parameric model: " + model);

		return model;
	}

	private static Context configureParametricModel(VarModel model)
			throws ContextException, EvaluationException, RemoteException {
		// setup evaluators and filters for model variables

		logger.info("\n\nconfigureParametricModel: " + model);
		
		// configure linked variable
		ServiceEvaluator x1le = new JepEvaluator("x1 + 3 * x3^2");
		model.setLinkedEvaluator("xl1", x1le);
		x1le.addArg(model.getDesignVar("x1")).addArg(
				model.getDesignVar("x3"));
		
		// configure response variable
		ServiceEvaluator y1e = new JepEvaluator("xl1 + 2 * x2^2 + 5 * x4 + 4 * x3^3");
		model.setResponseEvaluator("y1", y1e);
		y1e.addArg(model.getLinkedVar("xl1")).addArg(
				model.getDesignVar("x2"));
		y1e.addArg(model.getDesignVar("x3")).addArg(
				model.getDesignVar("x4"));

		return model;
	}

	public static ResponseModel getResponseModel1() throws ParException,
			RemoteException, EvaluationException, ConfigurationException,
			ContextException, ExertionException, SignatureException {

		int designVarCount = 20;
		int responseVarCount = 20;
		int linkedVarCount = 20;

		// create an analysis model
		// design variables: cs1-cs20, alpha
		// linked variables: cs21-cs40
		// responses: lpusi, iDrag
		// "
		// ResponseModel rm = ResponseModel("Response Analysis",
		// DesignVars("cs",designVarCount),
		// DesignVars("alpha"),
		// LinkedVars("cs", linkedVarCount,21),
		// ResponseVars("lpus", responseVarCount),
		// ResponseVars("iDrag"));
		ResponseModel rm = responseModel("Response Analysis", 
				linkedVars("cs", linkedVarCount, 21), 
				designVars("cs", designVarCount), 
				outputVars("lpus", responseVarCount),
				outputVars("iDrag"),
				designVars("alpha"));

		// configureResponseModel(rm);
		logger.info("literal context: " + rm);

		return rm;
	}

}
