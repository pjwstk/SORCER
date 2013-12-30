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

import static sorcer.co.operator.entry;
import static sorcer.co.operator.list;
import static sorcer.co.operator.loop;
import static sorcer.co.operator.names;
import static sorcer.co.operator.values;
import static sorcer.vo.operator.args;
import static sorcer.vo.operator.constraintVars;
import static sorcer.vo.operator.outputVar;
import static sorcer.vo.operator.outputVars;
import static sorcer.vo.operator.designVars;
import static sorcer.vo.operator.differentiation;
import static sorcer.vo.operator.evaluation;
import static sorcer.vo.operator.evaluator;
import static sorcer.vo.operator.evaluators;
import static sorcer.vo.operator.expression;
import static sorcer.vo.operator.gradient;
import static sorcer.vo.operator.input;
import static sorcer.vo.operator.linkedVars;
import static sorcer.vo.operator.objectiveVars;
import static sorcer.vo.operator.optimizationModel;
import static sorcer.vo.operator.output;
import static sorcer.vo.operator.invariantVars;
import static sorcer.vo.operator.parametricModel;
import static sorcer.vo.operator.parametricTable;
import static sorcer.vo.operator.realization;
import static sorcer.vo.operator.responseModel;
import static sorcer.vo.operator.var;
import static sorcer.vo.operator.vars;
import static sorcer.vo.operator.wrt;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Logger;

import sorcer.core.context.model.opti.OptimizationModel;
import sorcer.core.context.model.var.ParametricModel;
import sorcer.core.context.model.var.ResponseModel;
import sorcer.core.context.model.var.VarModel;
import sorcer.service.Configurable;
import sorcer.service.ConfigurationException;
import sorcer.service.Configurator;
import sorcer.service.ContextException;
import sorcer.service.EvaluationException;
import sorcer.service.ExertionException;
import sorcer.service.SignatureException;
import sorcer.util.Log;
import sorcer.vfe.ServiceEvaluator;
import sorcer.vfe.FilterException;
import sorcer.vfe.Var;
import sorcer.vfe.VarException;
import sorcer.vfe.VarInfo.Relation;
import sorcer.vfe.VarInfo.Target;
import sorcer.vfe.evaluator.ExpressionEvaluator;
import sorcer.vfe.evaluator.FiniteDifferenceEvaluator;
import sorcer.vfe.evaluator.JepEvaluator;
import sorcer.vfe.util.VarList;

/**
 * Example on how to create a ContextModel for numerical analysis
 */
@SuppressWarnings("unchecked")
public class RosenSuzukiFunction1 {

	private static Logger logger = Log.getTestLog();

	public static void main(String[] args) throws Exception {
		int test = new Integer(args[0]);
		switch (test) {
			case 1: doResponseAnalysis(); break;
			case 2: doParametricAnalysis(); break;
			case 3: doFileParametricAnalysis(); break;
			case 4: doUrlParametricAnalysis(); break;
			case 5: doSensitivitiesAnalysis(); break;
			case 6: doFunctionOfFunctionResponseAnalysis(); break;
			case 7: doFunctionOfFunctionFileParametricAnalysis(); break;
			case 8: doFunctionOfFunctionSensitivityAnalysis(); break;
			case 9: doMultiFidelityResponseAnalysis(); break;
			case 10: doMultifidelityParametricAnalysis(); break;
			case 11: doMutlifidelitySensitivityAnalysis(); break;
			case 12: doOptimization(); break;
		}
	}
	public static void doResponseAnalysis() throws ParException, RemoteException,
	EvaluationException, ConfigurationException, ContextException, ExertionException, SignatureException {
		// test Rosen Suzuki ResponseModel
		ResponseModel rm = getResponseModel();
		
		rm.setDesignVarValues(entry("x1", 2.0), entry("x2", 3.0), entry("x3", 4.0), entry("x4", 5.0));
		logger.info("\n\nRosen Suzuki response model: " + rm);
		
		Var x4 = rm.getDesignVar("x4");
		logger.info("\n\nx4 lower bound: " + x4.getLowerBound());
		logger.info("\n\nx4 upper bound: " + x4.getUpperBound());
		logger.info("\n\nx4 value: " + x4.getValue());
		
		// get all responses for the given values of xi
		logger.info("\n\nresponses: " + rm.getResponses());
		
		// examine the responseVar f
		Var f = rm.getOutputVar("f");
		ExpressionEvaluator ee = (ExpressionEvaluator)f.getEvaluator();
		logger.info("\n\nexpression:\n" + ee.getName() + "/" + ee.getExpression());
		logger.info("\n\nresponse:\n" + ee.getName() + "/" + f.getValue());
	}
	
	public static ResponseModel getResponseModel() throws ParException,
			RemoteException, EvaluationException, ConfigurationException,
			ContextException, ExertionException, SignatureException {

		// ResponseModel
		// design vars: x1, x2, x3, x4
		// response vars: f, g1, g2, g3
		// response
		// f:fe="x1^2-5.0*x1+x2^2-5.0*x2+2.0*x3^2-21.0*x3+x4^2+7.0*x4+50.0"
		// response g1:g1e="x1^2+x1+x2^2-x2+x3^2+x3+x4^2-x4-8.0"
		// response g2:g2e="x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"
		// response g3:g3e="2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"

		// define the model
		int designVarCount = 4;
		int responseVarCount = 4;

//		ResponseModel rm = responseModel("Response Analysis", designVars("x",
//				designVarCount - 1), designVars(var("x4", 0.0, 100.0)),
//				responseVars("f"), 
//				responseVars("g", responseVarCount - 1),
//				realization("g1", evaluation("g1e")), 
//				realization("g2",evaluation("g2e")),
//				realization("g3", evaluation("g3e")));

		ResponseModel rm = responseModel("Rosen-Suzuki Response Model", 
				designVars(vars(loop("i",1,designVarCount),"x$i$", 0.0, -100.0, 100.0)),
				outputVar("f",
						realization(
							evaluation("FExacte", 
								differentiation(wrt(names(loop("i",1,designVarCount),"x$i$")), gradient("FExacteg1"))))), 
				outputVars(loop("i",1,3),"g$i$",
						realization( 
								evaluation("g$i$Exacte",
										differentiation(wrt(names(loop("k",1,designVarCount),"x$k$")), gradient("g$i$Exacteg1")))))
						);
		// configure the model
		configureAnalysisModel(rm);
		logger.info("response model: " + rm);

		return rm;
	}

	private static VarModel configureAnalysisModel(VarModel model)
			throws ContextException, EvaluationException {
		// setup evaluators and filters for model vars

		// design vars: x1, x2, x3, x4
		// response vars: f, g1, g2, g3
		// response
		// f:fe="x1^2-5.0*x1+x2^2-5.0*x2+2.0*x3^2-21.0*x3+x4^2+7.0*x4+50.0"
		// response g1:g1e="x1^2+x1+x2^2-x2+x3^2+x3+x4^2-x4-8.0"
		// response g2:g2e="x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"
		// response g3:g3e="2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"

		// configure response vars
		// this can be done either programatically or declaratively
		ServiceEvaluator fe = evaluator("fe","x1^2-5.0*x1+x2^2-5.0*x2+2.0*x3^2-21.0*x3+x4^2+7.0*x4+50.0");
		model.setResponseEvaluator("f", fe);
		fe.addArgs(model.getDesignVars("x1", "x2", "x3", "x4"));
		// an alternative approach would be to do it declaratively
		//var(model,"f","fe",evaluator("fe","x1^2-5.0*x1+x2^2-5.0*x2+2.0*x3^2-21.0*x3+x4^2+7.0*x4+50.0"),args("x1", "x2", "x3", "x4"));
	
		ServiceEvaluator g1e = evaluator("g1e", "x1^2+x1+x2^2-x2+x3^2+x3+x4^2-x4-8.0");
		model.setResponseEvaluator("g1", g1e);
		g1e.addArgs(model.getDesignVars("x1", "x2", "x3", "x4"));

		ServiceEvaluator g2e = evaluator("g2e","x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0");
		model.setResponseEvaluator("g2", g2e);
		g2e.addArgs(model.getDesignVars("x1", "x2", "x3", "x4"));

		ServiceEvaluator g3e = evaluator("g3e", "2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0");
		model.setResponseEvaluator("g3", g3e);
		g3e.addArgs(model.getDesignVars("x1", "x2", "x3", "x4"));

		return model;
	}
	
	public static void doParametricAnalysis() throws ParException, EvaluationException, 
	ConfigurationException, ContextException, ExertionException, SignatureException, IOException {
		// test Rosen Suzuki ParametricModel
		ParametricModel am = getParametricModel();
		
		am.evaluate();
		
		am.writeOutputTableToFile();
		
		logger.info("\n\nRosen Suzuki Parametric Analysis input table: " + am.getParametricTable());
		logger.info("\n\nRosen Suzuki Parametric Analysis parametric table: " + am.getOutTable());
	}

	public static ParametricModel getParametricModel() throws ParException,
			EvaluationException, ConfigurationException, ContextException,
			ExertionException, SignatureException, IOException {
		// ParametricModel
		// design vars: x1, x2, x3, x4
		// response vars: f, g1, g2, g3
		// response
		// f:fe="x1^2-5.0*x1+x2^2-5.0*x2+2.0*x3^2-21.0*x3+x4^2+7.0*x4+50.0"
		// response g1:g1e="x1^2+x1+x2^2-x2+x3^2+x3+x4^2-x4-8.0"
		// response g2:g2e="x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"
		// response g3:g3e="2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"

		// need a method that looks like this
		// ParametricModel am = parametricModel("Parametric Analysis",
		// responseModelThatIsAlreadyConfigured,ParametricTable(),output());
		int designVarCount = 4;
		int responseVarCount = 4;
		ParametricModel pm = parametricModel("Parametric Analysis", designVars(
				"x", designVarCount), outputVars("f"), outputVars("g",
				responseVarCount - 1), parametricTable(names("x1", "x2", "x3",
				"x4"), values(1.1, 2.2, 3.3, 4.4), values(2.2, 4.2, 6.6, 8.8),
				values(4.4, 8.8, 12.1, 16.1), output("../data/out.data", ", ")));

		configureAnalysisModel(pm);

		logger.info("parametric model: " + pm);
		return pm;
	}
	
	private static void doMutlifidelitySensitivityAnalysis() throws RemoteException, ContextException, 
		EvaluationException, ConfigurationException {
		
		ResponseModel sm = getMultiFidelitySensitivitiesModel();
		logger.info("-- model: " + sm.getName() + "\n" + sm);
		
		sm.setDesignVarValues(entry("x1", 2.0), entry("x2", 3.0), entry("x3", 4.0), entry("x4", 5.0));
		//resting the value set in the model already to 50, no further change to parameters
		sm.setInvarinatVarValues(entry("p1", 60.0));
		
		Var f = sm.getOutputVar("f");
		logger.info("-- f value: " + f.getValue());
		
		ServiceEvaluator def = f.getDifferentiator();
		System.out.println("f1 derivative evaluator: " + def);
		
		System.out.println("partial derivative f wrt f1|fe2g1: " + f.getPartialDerivative("f1", "fe2g1"));
		
		System.out.println("partial derivative f wrt f1|fe2g2: " + f.getPartialDerivative("f1", "fe2g2"));
		
		f.selectFidelity("fe1");
		System.out.println("total derivative f1 wrt x1|fe1g1: " + f.getTotalDerivative("fe1g1", sm.getVar("x1")));
		System.out.println("total derivative f1 wrt x2|fe1g1: " + f.getTotalDerivative( "fe1g1", sm.getVar("x2")));
		
//		Var g1 = sm.getResponseVar("g1");
//		logger.info("-- g1 value: " + g1.getValue());
//		Evaluator deg1 = ((Evaluator)g1.getEvaluator()).getDerivativeEvaluator();
//		
//		System.out.println("g1 derivative evaluator: " + deg1);
//		
//		g1.selectEvaluation("g1e1");
//		deg1 = ((Evaluator)g1.getEvaluator()).getDerivativeEvaluator();
//		System.out.println("g1 derivative evaluator: " + deg1);
//		
//		System.out.println("partial derivative g1 wrt x2|g1e1g1: " + deg1.getPartialDerivative("x2", "g1e1g1"));		
		
//			
//		System.out.println("partial derivative f3: " + def.getPartialDerivative("f3", "fe2g2"));
//		
//		System.out.println("partial derivative f4: " + def.getPartialDerivative("f4", "fe2g2"));
//
//		System.out.println("partial derivative x3: " + def.getPartialDerivative("x3", "fe2g2"));
//
//		System.out.println("partial derivative xl1: " + def.getPartialDerivative("xl1", "fe2g2"));
		
//		logger.info("-- Rosen-Suzuki model partial gradients: " + sm.getPartialGradients());
		logger.info("-- Rosen-Suzuki total gradients: "    + sm.getTotalDerivativeTables());
	}

	private static ResponseModel getMultiFidelitySensitivitiesModel() throws ContextException, RemoteException, EvaluationException, ConfigurationException {
			// Multi-Fidelity SensitivityModel 
			
			// Base Model Definition
			// design vars: x1, x2, x3, x4
			// response vars: f, g1, g2, g3
			// response f:fe="x1^2-5.0*x1+x2^2-5.0*x2+2.0*x3^2-21.0*x3+x4^2+7.0*x4+50.0"
			// response g1:g1e="x1^2+x1+x2^2-x2+x3^2+x3+x4^2-x4-8.0" 
			// response g2:g2e="x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"
			// response g3:g3e="2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"
			
	// f:fe1
			// design vars: x1, x2, x3, x4
			// response vars: f
			// response f:fe1="x1^2-5.0*x1+x2^2-5.0*x2+2.0*x3^2-21.0*x3+x4^2+7.0*x4+50.0"
				//dfdxi:dfe1dxie1
					// partial derivative dfe1dx1e1 = "2.0*x1-5.0"
					// partial derivative dfe1dx2e1 = "2.0*x2-5.0"
					// partial derivative dfe1dx3e1 = "4.0*x3-21.0"
					// partial derivative dfe1dx4e1 = "2.0*x4+7.0"
				//dfdxi:dfe1dxie2
					// partial derivative dfe1dx1e2 = (fe1(x1+deltax1) -fe1(x1))/deltax1
					// partial derivative dfe1dx2e2 = (fe1(x2+deltax2) -fe1(x1))/deltax2
					// partial derivative dfe1dx3e2 = (fe1(x3+deltax3) -fe1(x1))/deltax3
					// partial derivative dfe1dx4e2 = (fe1(x4+deltax4) -fe1(x1))/deltax4
	// f:fe2
			// design vars: x1, x2, x3, x4
			// linked vars: xl1="7.0*x4"
			// parameters: p1=50.
			// response vars: f, f1, f2, f3, f4,
			// response f:fe2="f1+f3+f4-21.0*x3+xl1+p1"
			// dfdxi:dfe2dxie1
					// partial derivative dfe2df1e1="1.0"
					// partial derivative dfe2df3e1="1.0"
					// partial derivative dfe2df4e1="1.0"
					// partial derivative dfe2dx3e1="-21.0"
					// partial derivative dfe2dxl1e1="1.0"
			// dfdxi:dfe2dxie2
				// partial derivative dfe2df1e2 = (fe2(f1+deltaf1) -fe2(f1))/deltaf1
				// partial derivative dfe2df3e2 = (fe2(f3+deltaf3) -fe2(f3))/deltaf3
				// partial derivative dfe2df4e2 = (fe2(f4+deltaf4) -fe2(f4))/deltaf4
				// partial derivative dfe2dx3e2 = (fe2(x3+deltax3) -fe2(x3))/deltax3
				// partial derivative dfe2dxl1e2 = (fe2(xl1+deltaxl1) -fe2(x1l))/deltaxl1
		
				// response f1:f1e="x1^2+x2^2"
					// partial derivative df1/dx1:df1edx1e1 ="2.0*x1"
					// partial derivative df1/dx2:df1edx2e1="2.0*x2"
				// response f2:f2e="x1+x2"
					// partial derivative df2/dx1:df2edx1e1="1.0"
					// partial derivative df2/dx2:df2edx2e1="1.0"
				// response f3:f3e="-5.0*f2"
					// partial derivative df3/df2:df3edf2e1="-5.0"
				// response f4:f4e=2.0*x3^2+x4^2
					// df4dxie1:df
						// partial derivative df4/dx3:df4edx1e1="4.0*x3"
						// partial derivative df4/dx4:df4edx4e1="2.0*x4"
					// df4dxie2
						// partial derivative df4/dx3:df4edx3e2=(f4e(x3+deltax3) -f4e(x3))/deltax3
						// partial derivative df4/dx4:df4edx4e2=(f4e(x4+deltax4) -f4e(x4))/deltax4
	//g1:g1e1
			// response g1:g1e1="x1^2+x1+x2^2-x2+x3^2+x3+x4^2-x4-8.0" 
				 //dg1/dxi:dg1e1dxie1
					// partial derivative dg1/dx1:dg1e1dx1e1 = "2.0*x1+1.0"
					// partial derivative dg1/dx2:dg1e1dx2e1 = "2.0*x2-1.0"
					// partial derivative dg1/dx3:dg1e1dx3e1 = "2.0*x3+1.0"
					// partial derivative dg1/dx4:dg1e1dx4e1 = "2.0*x4-1.0"
				//dg1/dxi:dg1e1dxie2
					// partial derivative dg1/dx1:dg1e1dx1e2 = (g1e1(x1+deltax1) -g1e1(x1))/deltax1
					// partial derivative dg1/dx2:dg1e1dx2e2 = (g1e1(x2+deltax2) -g1e1(x2))/deltax2
					// partial derivative dg1/dx3:dg1e1dx3e2 = (g1e1(x3+deltax3) -g1e1(x3))/deltax3
					// partial derivative dg1/dx4:dg1e1dx4e2 = (g1e1(x4+deltax4) -g1e1(x4))/deltax4
	//g1:g1e2
			
			// response g1:g1e2="f1+f2-2.0*x2+x3^2+x3+x4^2-x4-8.0" 
			     //dg1/dxi:dg1e2dxie1
					// partial derivative dg1/df1:dg1e2df1e1="1.0"
					// partial derivative dg1/df2:dg1e2df2e1="1.0"
					// partial derivative dg1/dx2:dg1e2dx2e1="-2.0"
					// partial derivative dg1/dx3:dg1e2dx3e1=2.0*x3+1.0"
					// partial derivative dg1/dx4:dg1e2dx4e1=2.0*x4-1.0"
			     //dg1/dxi:dg1dxie2
					// partial derivative dg1/df1:dg1e2df1e2=(g1e2(f1+deltaf1) -g1e2(f1))/deltaf1
					// partial derivative dg1/df2:dg1e2df2e2=(g1e2(f2+deltaf2) -g1e2(f2))/deltaf2
					// partial derivative dg1/dx2:dg1e2dx2e2=(g1e2(x2+deltax2) -g1e2(x2))/deltax2
					// partial derivative dg1/dx3:dg1e2dx3e2=(g1e2(x3+deltax3) -g1e2(x3))/deltax3
					// partial derivative dg1/dx4:dg1e2dx4e2=(g1e2(x4+deltax4) -g1e2(x4))/deltax4
			
			// response g2:g2e="x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"
					// partial derivative dg2/dx1:dg2edx1e1 = "2.0*x1-1.0"
					// partial derivative dg2/dx2:dg2edx2e1 = "4.0*x2"
					// partial derivative dg2/dx3:dg2edx3e1 = "2.0*x3"
					// partial derivative dg2/dx4:dg2edx4e1 = "4.0*x4-1.0"
			// response g3:g3e="2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"
					// partial derivative dg3/dx1:dg3edx1e1 = "4.0*x1+2.0"
					// partial derivative dg3/dx2:dg3edx2e1 = "2.0*x2-1.0"
					// partial derivative dg3/dx3:dg3edx3e1 = "2.0*x3"
					// partial derivative dg3/dx4:dg3edx4e1 = "-1.0"	
			
		int designVarCount = 4;
			
		ResponseModel sm = responseModel("Rosen-Suzuki", 
			designVars("x", designVarCount), 
			linkedVars("xl1"),
			//parameterVars("p1"),
			parameterVars(var("p1", 50.0)),
			outputVars("f"),
				realization("f", evaluation("fe1"), evaluation("fe2"),
					differentiation("fe1", wrt(names(loop(1, 4), "x")), gradient("fe1g1"), gradient("fe1g2")),     
					differentiation("fe2", wrt("f1", "f3", "f4", "x3", "xl1"), gradient("fe2g1"), gradient("fe2g2"))),
			outputVars("f",4),
				realization("f1", differentiation(wrt("x1", "x2"))),
				realization("f2", differentiation(wrt("x1", "x2"))),
				realization("f3", differentiation(wrt("f2"))),
				realization("f4", evaluation("f4e"), 
					differentiation("f4e", wrt("x3", "x4"), gradient("f4eg1"), gradient("f4eg2"))),
				outputVars("g", 3),
				
				realization("g1", evaluation("g1e1"), evaluation("g1e2"), 
					differentiation("g1e1", wrt(names(loop(4), "x")), gradient("g1e1g1"), gradient("g1e1g2")), 
					differentiation("g1e2", wrt("f1", "f2", "x2", "x3", "x4"), gradient("g1e2g1"), gradient("g1e2g2"))),
// other options
//				realization("g1", 
//					evaluation("g1e1", args(names(loop(4), "x")),
//						differentiation(gradient("g1e1g1"), gradient("g1e1g2"))), 
//					evaluation("g1e2", args("f1", "f2", "x2", "x3", "x4"), 
//						differentiation(gradient("g1e2g1"), gradient("g1e2g2")))),
					
//				realization("g1", 
//					evaluation("g1e1", 
//						differentiation(wrt(names(loop(4), "x")), gradient("g1e1g1"), gradient("g1e1g2"))), 
//					evaluation("g1e2", 
//						differentiation(wrt("f1", "f2", "x2", "x3", "x4"), gradient("g1e2g1"), gradient("g1e2g2")))),
					
				realization("g2", differentiation(wrt(names(loop(4), "x")))),	
				realization("g3", differentiation(wrt("x1", "x2", "x3", "x4"))));
			
			Configurator cfr = getMultiFidelityAnalysisConfigurator(sm);
			cfr.configure();
			//configureMultiFidelityAnalysisModel(sm);
			configureMultiFidelitySensitivityModel(sm);
			
			return sm;
		}

	private static ResponseModel configureMultiFidelitySensitivityModel(
			ResponseModel sm) throws ContextException {
			// Multi-Fidelity SensitivityModel 
			
	// f:fe1
			// design vars: x1, x2, x3, x4
			// response vars: f
			// response f:fe1="x1^2-5.0*x1+x2^2-5.0*x2+2.0*x3^2-21.0*x3+x4^2+7.0*x4+50.0"
				//dfdxi:dfe1dxie1
					// partial derivative dfe1dx1e1 = "2.0*x1-5.0"
					// partial derivative dfe1dx2e1 = "2.0*x2-5.0"
					// partial derivative dfe1dx3e1 = "4.0*x3-21.0"
					// partial derivative dfe1dx4e1 = "2.0*x4+7.0"
				//dfdxi:dfe1dxie2
					// partial derivative dfe1dx1e2 = (fe1(x1+deltax1) -fe1(x1))/deltax1
					// partial derivative dfe1dx2e2 = (fe1(x2+deltax2) -fe1(x1))/deltax2
					// partial derivative dfe1dx3e2 = (fe1(x3+deltax3) -fe1(x1))/deltax3
					// partial derivative dfe1dx4e2 = (fe1(x4+deltax4) -fe1(x1))/deltax4
	// f:fe2
			// design vars: x1, x2, x3, x4
			// linked vars: xl1="7.0*x4"
			// parameters: p1=50.
			// response vars: f, f1, f2, f3, f4,
			// response f:fe2="f1+f3+f4-21.0*x3+xl1+p1"
						  // f1:f1e = x1^2+x2^2
						  // f2:f2e = x1+x2
						  // f3:f3e=-5f2
					      // f4:f4e=2x3^2+x4^2
			// dfdxi:dfe2dxie1
					// partial derivative dfe2df1e1="1.0"
					// partial derivative dfe2df3e1="1.0"
					// partial derivative dfe2df4e1="1.0"
					// partial derivative dfe2dx3e1="-21.0"
					// partial derivative dfe2dxl1e1="1.0"
			// dfdxi:dfe2dxie2
				// partial derivative dfe2df1e2 = (fe2(f1+deltaf1) -fe2(f1))/deltaf1
				// partial derivative dfe2df3e2 = (fe2(f3+deltaf3) -fe2(f3))/deltaf3
				// partial derivative dfe2df4e2 = (fe2(f4+deltaf4) -fe2(f4))/deltaf4
				// partial derivative dfe2dx3e2 = (fe2(x3+deltax3) -fe2(x3))/deltax3
				// partial derivative dfe2dxl1e2 = (fe2(xl1+deltaxl1) -fe2(x1l))/deltaxl1
				// response f1:f1e="x1^2+x2^2"
					// partial derivative df1/dx1 ="2.0*x1"
					// partial derivative df1/dx2="2.0*x2"
				// response f2:f2e="x1+x2"
					// partial derivative df2/dx1="1.0"
					// partial derivative df2/dx2="1.0"
				// response f3:f3e="-5.0*f2"
					// partial derivative df3/df2="-5.0"
				// response f4:f4e=2.0*x3^2+x4^2
					// df4dxie1
						// partial derivative df4/dx3:df4edx1e1="4.0*x3"
						// partial derivative df4/dx4:df4edx4e1="2.0*x4"
					// df4dxie2
						// partial derivative df4/dx3:df4edx3e2=(f4e(x3+deltax3) -f4e(x3))/deltax3
						// partial derivative df4/dx4:df4edx4e2=(f4e(x4+deltax4) -f4e(x4))/deltax4
	//g1:g1e1
			// response g1:g1e1="x1^2+x1+x2^2-x2+x3^2+x3+x4^2-x4-8.0" 
				 //dg1/dxi:dg1e1dxie1
					// partial derivative dg1/dx1:dg1e1dx1e1 = "2.0*x1+1.0"
					// partial derivative dg1/dx2:dg1e1dx2e1 = "2.0*x2-1.0"
					// partial derivative dg1/dx3:dg1e1dx3e1 = "2.0*x3+1.0"
					// partial derivative dg1/dx4:dg1e1dx4e1 = "2.0*x4-1.0"
				//dg1/dxi:dg1e1dxie2
					// partial derivative dg1/dx1:dg1e1dx1e2 = (g1e1(x1+deltax1) -g1e1(x1))/deltax1
					// partial derivative dg1/dx2:dg1e1dx2e2 = (g1e1(x2+deltax2) -g1e1(x2))/deltax2
					// partial derivative dg1/dx3:dg1e1dx3e2 = (g1e1(x3+deltax3) -g1e1(x3))/deltax3
					// partial derivative dg1/dx4:dg1e1dx4e2 = (g1e1(x4+deltax4) -g1e1(x4))/deltax4
	//g1:g1e2
			
			// response g1:g1e2="f1+f2-2.0*x2+x3^2+x3+x4^2-x4-8.0" 
			     //dg1/dxi:dg1e2dxie1
					// partial derivative dg1/df1:dg1e2df1e1="1.0"
					// partial derivative dg1/df2:dg1e2df2e1="1.0"
					// partial derivative dg1/dx2:dg1e2dx2e1="-2.0"
					// partial derivative dg1/dx3:dg1e2dx3e1=2.0*x3+1.0"
					// partial derivative dg1/dx4:dg1e2dx4e1=2.0*x4-1.0"
			     //dg1/dxi:dg1dxie2
					// partial derivative dg1/df1:dg1e2df1e2=(g1e2(f1+deltaf1) -g1e2(f1))/deltaf1
					// partial derivative dg1/df2:dg1e2df2e2=(g1e2(f2+deltaf2) -g1e2(f2))/deltaf2
					// partial derivative dg1/dx2:dg1e2dx2e2=(g1e2(x2+deltax2) -g1e2(x2))/deltax2
					// partial derivative dg1/dx3:dg1e2dx3e2=(g1e2(x3+deltax3) -g1e2(x3))/deltax3
					// partial derivative dg1/dx4:dg1e2dx4e2=(g1e2(x4+deltax4) -g1e2(x4))/deltax4
			
			// response g2="x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"
					// partial derivative dg2dx1 = "2.0*x1-1.0"
					// partial derivative dg2dx2 = "4.0*x2"
					// partial derivative dg2dx3 = "2.0*x3"
					// partial derivative dg2dx4 = "4.0*x4-1.0"
			// response g3="2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"
					// partial derivative dg3dx1 = "4.0*x1+2.0"
					// partial derivative dg3dx2 = "2.0*x2-1.0"
					// partial derivative dg3dx3 = "2.0*x3"
					// partial derivative dg3dx4 = "-1.0"	
			
			// f:fe1
			// design vars: x1, x2, x3, x4
			// response vars: f
			// response f:fe1="x1^2-5.0*x1+x2^2-5.0*x2+2.0*x3^2-21.0*x3+x4^2+7.0*x4+50.0"
				//dfdxi:dfe1dxie1
					// partial derivative dfe1dx1e1 = "2.0*x1-5.0"
					// partial derivative dfe1dx2e1 = "2.0*x2-5.0"
					// partial derivative dfe1dx3e1 = "4.0*x3-21.0"
					// partial derivative dfe1dx4e1 = "2.0*x4+7.0"
					ServiceEvaluator dfe1dx1e1 = expression("dfe1dx1e1", "2.0*x1-5.0", args(designVars(sm, "x1")));
					ServiceEvaluator dfe1dx2e1 = expression("dfe1dx2e1", "2.0*x2-5.0", args(designVars(sm, "x2")));
					ServiceEvaluator dfe1dx3e1 = expression("dfe1dx3e1", "4.0*x3-21.0", args(designVars(sm, "x3")));
					ServiceEvaluator dfe1dx4e1 = expression("dfe1dx4e1", "2.0*x4+7.0", args(designVars(sm, "x4")));
					List<ServiceEvaluator> fe1g1 = list(dfe1dx1e1, dfe1dx2e1, dfe1dx3e1, dfe1dx4e1);
					
					//sm.setGradientEvaluators("f", "fe1", "fe1g1",  fe1g1);
					evaluator(sm, "f", "fe1", "fe1g1",  evaluators(dfe1dx1e1, dfe1dx2e1, dfe1dx3e1, dfe1dx4e1));
							
			// link variable
					ServiceEvaluator dxl1edx4e = expression("dxl1edx4e", "7", args(designVars(sm, "x4")));
					sm.setGradientEvaluators("xl1",  "xl1e", "xl1eg", list(dxl1edx4e));
							
			//dfdxi:dfe1dxie2
					// partial derivative dfe1dx1e2 = (fe1(x1+deltax1) -fe1(x1))/deltax1
					// partial derivative dfe1dx2e2 = (fe1(x2+deltax2) -fe1(x1))/deltax2
					// partial derivative dfe1dx3e2 = (fe1(x3+deltax3) -fe1(x1))/deltax3
					// partial derivative dfe1dx4e2 = (fe1(x4+deltax4) -fe1(x1))/deltax4
					
					ServiceEvaluator dfe1dx1e2 = new FiniteDifferenceEvaluator("dfe1dx1e2", outputVar(sm, "f").getEvaluator("fe1"),0.1, "x1");
					ServiceEvaluator dfe1dx2e2 = new FiniteDifferenceEvaluator("dfe1dx2e2", outputVar(sm, "f").getEvaluator("fe1"),0.1, "x2");
					ServiceEvaluator dfe1dx3e2 = new FiniteDifferenceEvaluator("dfe1dx3e2", outputVar(sm, "f").getEvaluator("fe1"),0.1, "x3");
					ServiceEvaluator dfe1dx4e2 = new FiniteDifferenceEvaluator("dfe1dx4e2", outputVar(sm, "f").getEvaluator("fe1"),0.1, "x4");
					List<ServiceEvaluator> fe1g2 = list(dfe1dx1e2, dfe1dx2e2, dfe1dx3e2, dfe1dx4e2);	
					sm.setGradientEvaluators("f", "fe1", "fe1g2",  fe1g2);
					
			// f:fe2
			// design vars: x1, x2, x3, x4
			// linked vars: xl1="7.0*x4"
			// parameters: p1=50.
			// response vars: f, f1, f2, f3, f4,
			// response f:fe2="f1+f3+f4-21.0*x3+xl1+p1"
				// dfdxi:dfe2dxie1
					// partial derivative dfe2df1e1="1.0"
					// partial derivative dfe2df3e1="1.0"
					// partial derivative dfe2df4e1="1.0"
					// partial derivative dfe2dx3e1="-21.0"
					// partial derivative dfe2dxl1e1="1.0"
					ServiceEvaluator dfe2df1e1 = evaluator("dfe2df1e1", "1.0");
					ServiceEvaluator dfe2df3e1 = evaluator("dfe2df3e1", "1.0");
					ServiceEvaluator dfe2df4e1 = evaluator("dfe2df4e1", "1.0");
					ServiceEvaluator dfe2dx3e1 = evaluator("dfe2dx3e1", "-21.0");
					ServiceEvaluator dfe2dxl1e1 = evaluator("dfe2dxl1e1", "1.0");
					List<ServiceEvaluator> fe2g1 = list(dfe2df1e1, dfe2df3e1, dfe2df4e1, dfe2dx3e1, dfe2dxl1e1);	
					sm.setGradientEvaluators("f", "fe2", "fe2g1",  fe2g1);		
			// dfdxi:dfe2dxie2
					// partial derivative dfe2df1e2 = (fe2(f1+deltaf1) -fe2(f1))/deltaf1
					// partial derivative dfe2df3e2 = (fe2(f3+deltaf3) -fe2(f3))/deltaf3
					// partial derivative dfe2df4e2 = (fe2(f4+deltaf4) -fe2(f4))/deltaf4
					// partial derivative dfe2dx3e2 = (fe2(x3+deltax3) -fe2(x3))/deltax3
					// partial derivative dfe2dxl1e2 = (fe2(xl1+deltaxl1) -fe2(x1l))/deltaxl1
					ServiceEvaluator dfe2df1e2 = new FiniteDifferenceEvaluator("dfe2df1e2", sm.getOutputVar("f").getEvaluator("fe2"), 0.1, "f1");
					ServiceEvaluator dfe2df3e2 = new FiniteDifferenceEvaluator("dfe2df3e2", sm.getOutputVar("f").getEvaluator("fe2"), 0.1, "f3");
					ServiceEvaluator dfe2df4e2 = new FiniteDifferenceEvaluator("dfe2df4e2", sm.getOutputVar("f").getEvaluator("fe2"), 0.1, "f4");
					ServiceEvaluator dfe2dx3e2 = new FiniteDifferenceEvaluator("dfe2dx3e2", sm.getOutputVar("f").getEvaluator("fe2"), 0.1, "x3");
					ServiceEvaluator dfe2dxl1e2 = new FiniteDifferenceEvaluator("dfe2dxl1e2", sm.getOutputVar("f").getEvaluator("fe2"), 0.1, "xl1");
					List<ServiceEvaluator> fe2g2 = list(dfe2df1e2, dfe2df3e2, dfe2df4e2, dfe2dx3e2, dfe2dxl1e2 );	
					sm.setGradientEvaluators("f", "fe2", "fe2g2",  fe2g2);
			
			
				// response f1="x1^2+x2^2"
					// partial derivative df1/dx1 ="2.0*x1"
					// partial derivative df1/dx2="2.0*x2"
					ServiceEvaluator df1edx1e1 = expression("df1edx1e1", "2.0*x1",args(designVars(sm, "x1")));
					ServiceEvaluator df1edx2e1 = expression("df1edx2e1", "2.0*x2",args(designVars(sm, "x2")));
					List<ServiceEvaluator> f1eg1 = list(df1edx1e1, df1edx2e1);	
					sm.setGradientEvaluators("f1", "f1e", "f1eg1",  f1eg1);
				// response f2="x1+x2"
					// partial derivative df2/dx1="1.0"
					// partial derivative df2/dx2="1.0"
					ServiceEvaluator df2edx1e1 = evaluator("df2edx2e1","1.0");
					ServiceEvaluator df2edx2e1 = evaluator("df2edx2e1", "1.0");
					List<ServiceEvaluator> f2eg1 = list(df2edx1e1, df2edx2e1);	
					sm.setGradientEvaluators("f2", "f2e", "f2eg1",  f2eg1);	
				// response f3="-5.0*f2"
					// partial derivative df3/df2="-5.0"
					ServiceEvaluator df3edf2e1 = evaluator("df3edf2e1","-5.0");
					List<ServiceEvaluator> f3eg1 = list(df3edf2e1);	
					sm.setGradientEvaluators("f3", "f3e", "f3eg1",  f3eg1);
				// response f4:f4e=2.0*x3^2+x4^2
					// df4dxie1
						// partial derivative df4/dx3:df4edx1e1="4.0*x3"
						// partial derivative df4/dx4:df4edx4e1="2.0*x4"
						ServiceEvaluator df4edx3e1 = expression("df4edx3e1","4.0*x3",args(designVars(sm, "x3")));
						ServiceEvaluator df4edx4e1 = expression("df4edx4e1", "2.0*x4",args(designVars(sm, "x4")));
						List<ServiceEvaluator> f4eg1 = list(df4edx3e1, df4edx4e1);	
						sm.setGradientEvaluators("f4", "f4e", "f4eg1",  f4eg1);
					// df4dxie2
						// partial derivative df4/dx3:df4edx3e2=(f4e(x3+deltax3) -f4e(x3))/deltax3
						// partial derivative df4/dx4:df4edx4e2=(f4e(x4+deltax4) -f4e(x4))/deltax4
					    ServiceEvaluator df4edx3e2 = new FiniteDifferenceEvaluator("df4edx3e2", outputVar(sm, "f4").getEvaluator("f4e"), 0.1, "x3");
					    ServiceEvaluator df4edx4e2 = new FiniteDifferenceEvaluator("df4edx4e2", outputVar(sm, "f4").getEvaluator("f4e"), 0.1, "x4");
						List<ServiceEvaluator> f4eg2 = list(df4edx3e2, df4edx4e2);	
						sm.setGradientEvaluators("f4", "f4e", "f4eg2",  f4eg2);
						
			//g1: g1e1
					// response g1:g1e1="x1^2+x1+x2^2-x2+x3^2+x3+x4^2-x4-8.0" 
					// dg1e1dxie1
						// derivativeResponse dg1dx1:dg1e1dx1 = "2.0*x1+1.0"
						// derivativeResponse dg1dx2:dg1e1dx2 = "2.0*x2-1.0"
						// derivativeResponse dg1dx3:dg1e1dx3 = "2.0*x3+1.0"
						// derivativeResponse dg1dx4:dg1e1dx4 = "2.0*x4-1.0"
						ServiceEvaluator dg1e1dx1e1 = expression("dg1e1dx1e1", "2.0*x1+1",args(sm.getDesignVars("x1")));
						ServiceEvaluator dg1e1dx2e1 = expression("dg1e1dx2e1", "2.0*x2-1.0",args(sm.getDesignVars("x2")));
						ServiceEvaluator dg1e1dx3e1 = expression("dg1e1dx3e1", "2.0*x3+1.0",args(sm.getDesignVars("x3")));
						ServiceEvaluator dg1e1dx4e1 = expression("dg1e1dx4e1", "2.0*x4-1.0",args(sm.getDesignVars("x4")));
						List<ServiceEvaluator> g1e1g1 = list(dg1e1dx1e1, dg1e1dx2e1, dg1e1dx3e1, dg1e1dx4e1);	
						sm.setGradientEvaluators("g1", "g1e1", "g1e1g1",  g1e1g1);
					// dg1e1dxie2	
						ServiceEvaluator dg1e1dx1e2 = new FiniteDifferenceEvaluator("dg1e1dx1e2", outputVar(sm, "g1").getEvaluator("g1e1"), 0.1, "x1");
						ServiceEvaluator dg1e1dx2e2 = new FiniteDifferenceEvaluator("dg1e1dx2e2", outputVar(sm, "g1").getEvaluator("g1e1"), 0.1, "x2");
						ServiceEvaluator dg1e1dx3e2 = new FiniteDifferenceEvaluator("dg1e1dx3e2", outputVar(sm, "g1").getEvaluator("g1e1"), 0.1, "x3");
						ServiceEvaluator dg1e1dx4e2 = new FiniteDifferenceEvaluator("dg1e1dx4e2", outputVar(sm, "g1").getEvaluator("g1e1"), 0.1, "x4");
						List<ServiceEvaluator> g1e1g2 = list(dg1e1dx1e2, dg1e1dx2e2, dg1e1dx3e2, dg1e1dx4e2);	
						sm.setGradientEvaluators("g1", "g1e1", "g1e1g2",  g1e1g2);
						
						
		  //g1: g1e2
			// response g1:g1e2="f1+f2-2.0*x2+x3^2+x3+x4^2-x4-8.0" 
			// partial derivative dg1/df1="1.0"
			// partial derivative dg1/df2="1.0"
			// partial derivative dg1/dx2="-2.0"
			// partial derivative dg1/dx3=2.0*x3+1.0"
			// partial derivative dg1/dx4=2.0*x4-1.0"	
			// dg1e2dxie1
			ServiceEvaluator dg1e2df1e1 = evaluator("dg1e2df1e1", "1.0");
			ServiceEvaluator dg1e2df2e1 = evaluator("dg1e2df2e1", "1.0");
			ServiceEvaluator dg1e2dx2e1 = evaluator("dg1e2dx2e1", "-2.0");
			ServiceEvaluator dg1e2dx3e1 = expression("dg1e2dx3e1", "2.0*x3+1.0", args(designVars(sm, "x3")));
			ServiceEvaluator dg1e2dx4e1 = expression("dg1e2dx4e1", "2.0*x4-1.0", args(designVars(sm, "x4")));
			List<ServiceEvaluator> g1e2g1 = list(dg1e2df1e1, dg1e2df2e1, dg1e2dx2e1, dg1e2dx3e1, dg1e2dx4e1);	
			sm.setGradientEvaluators("g1", "g1e2", "g1e2g1",  g1e2g1);
			// dg1e2dxie2
			ServiceEvaluator dg1e2df1e2 = new FiniteDifferenceEvaluator("dg1e2df1e2", outputVar(sm, "g1").getEvaluator("g1e2"), 0.1, "f1");
			ServiceEvaluator dg1e2df2e2 = new FiniteDifferenceEvaluator("dg1e2df2e2", outputVar(sm, "g1").getEvaluator("g1e2"), 0.1, "f2");
			ServiceEvaluator dg1e2dx2e2 = new FiniteDifferenceEvaluator("dg1e2dx2e2", outputVar(sm, "g1").getEvaluator("g1e2"), 0.1, "x2");
			ServiceEvaluator dg1e2dx3e2 = new FiniteDifferenceEvaluator("dg1e2dx3e2", outputVar(sm, "g1").getEvaluator("g1e2"), 0.1, "x3");
			ServiceEvaluator dg1e2dx4e2 = new FiniteDifferenceEvaluator("dg1e2dx4e2", outputVar(sm, "g1").getEvaluator("g1e2"), 0.1, "x4");
			List<ServiceEvaluator> g1e2g2 = list(dg1e2df1e2, dg1e2df2e2, dg1e2dx2e2, dg1e2dx3e2, dg1e2dx4e2);	
			sm.setGradientEvaluators("g1", "g1e2", "g1e2g2",  g1e2g2);
			
			
			// response g2="x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"
			// partial derivative dg2dx1 = "2.0*x1-1.0"
			// partial derivative dg2dx2 = "4.0*x2"
			// partial derivative dg2dx3 = "2.0*x3"
			// partial derivative dg2dx4 = "4.0*x4-1.0"
			ServiceEvaluator dg2edx1e1 = expression("dg2edx1e1", "2.0*x1-1",args(designVars(sm, "x1")));
			ServiceEvaluator dg2edx2e1 = expression("dg2edx2e1", "4.0*x2", args(designVars(sm, "x2")));
			ServiceEvaluator dg2edx3e1 = expression("dg2edx3e1", "2.0*x3", args(designVars(sm, "x3")));
			ServiceEvaluator dg2edx4e1 = expression("dg2edx4e1", "4.0*x4-1.0", args(designVars(sm, "x4")));
			List<ServiceEvaluator> g2eg1 = list(dg2edx1e1, dg2edx2e1, dg2edx3e1, dg2edx4e1);	
			sm.setGradientEvaluators("g2", "g2e", "g2eg1",  g2eg1);

			
			// response g3="2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"
			// partial derivative dg3dx1 = "4.0*x1+2.0"
			// partial derivative dg3dx2 = "2.0*x2-1.0"
			// partial derivative dg3dx3 = "2.0*x3"
			// partial derivative dg3dx4 = "-1.0"
			ServiceEvaluator dg3edx1e1 = expression("dg3edx1e1", "4.0*x1+2.0", args(designVars(sm, "x1")));
			ServiceEvaluator dg3edx2e1 = expression("dg3edx2e1", "2.0*x2-1.0", args(designVars(sm, "x2")));
			ServiceEvaluator dg3edx3e1 = expression("dg3edx3e1", "2.0*x3", args(designVars(sm, "x3")));
			ServiceEvaluator dg3edx4e1 = evaluator("dg3edx4e1", "-1.0");
			List<ServiceEvaluator> g3eg1 = list(dg3edx1e1, dg3edx2e1, dg3edx3e1, dg3edx4e1);	
			sm.setGradientEvaluators("g3", "g3e", "g3eg1",  g3eg1);
			return sm;
			
		}

	private static void doMultifidelityParametricAnalysis() throws EvaluationException, IOException, ContextException {
		
			// test ParametricModel
			ParametricModel am = getMultiFidelityParametricModel();
			am.setInvarinatVarValues(entry("p1", 50.0));
			am.evaluate();
			am.writeOutputTableToFile();
			logger.info("\n\nParametricAnalysis input table: " + am.getParametricTable());
			logger.info("\n\nParametricAnalysis fe2, g1e2 parametric table: " + am.getOutTable());
			
			am.getOutputVar("f").selectEvaluator("fe1");
			am.getOutputVar("g1").selectEvaluator("g1e1");
			am.evaluate();
			am.writeOutputTableToFile();
			logger.info("\n\nParametricAnalysis input table: " + am.getParametricTable());
			logger.info("\n\nParametricAnalysis fe1, g1e1 parametric table: " + am.getOutTable());
		}
		
	private static ParametricModel getMultiFidelityParametricModel() throws ContextException, IOException, EvaluationException {
		int designVarCount = 4;
		int responseVarCount = 4;

		ParametricModel am = parametricModel("Parametric Analysis", 
			designVars("x", designVarCount), 
			linkedVars("xl1"),
			parameterVars("p1"),
			//parameterVars(set("p1", 50.0)),
			outputVars("f"),realization("f", evaluation("fe1"), evaluation("fe2")),
			outputVars("f",4),
			realization("f1",evaluation("f1e")),realization("f2",evaluation("f2e")),realization("f3",evaluation("f3e")),realization("f4",evaluation("f4e")),
			outputVars("g", responseVarCount-1),
			realization("g1", evaluation("g1e1"), evaluation("g1e2")), realization("g2", evaluation("g2e")),realization("g3", evaluation("g3e")) ,
			parametricTable(input("../data/rosenSuzuki1in.data"),
							output("../data/rosenSuzuki1out.data", ", ")));					
			//configureMultiFidelityAnalysisModel(am);  FIX IT
			
			return am;
		}

	private static void doMultiFidelityResponseAnalysis() throws ContextException, EvaluationException, RemoteException {
		ResponseModel rm = getMultiFidelityResponseModel();
		rm.setInvarinatVarValues(entry("p1", 50.0));
		rm.setDesignVarValues(entry("x1", 2.0), entry("x2", 3.0), entry("x3", 4.0), entry("x4", 5.0));
		logger.info("\n\nresponse model: " + rm);
		
		// get all responses for the given values of xi
		logger.info("\n\nresponses: " + rm.getResponses());
		
		// examine the responseVar f
		Var f = rm.getOutputVar("f");
		ExpressionEvaluator ee1 = (ExpressionEvaluator)f.getEvaluator();
		logger.info("\n\nexpression:\n" + ee1.getName() + "/" + ee1.getExpression());
		logger.info("\n\nresponse:\n" + ee1.getName() + "/" + f.getValue());	
		
		f.selectEvaluator("fe1");
		ExpressionEvaluator ee2 = (ExpressionEvaluator)f.getEvaluator();
		logger.info("\n\nexpression:\n" + ee2.getName() + "/" + ee2.getExpression());
		logger.info("\n\nresponse:\n" + ee2.getName() + "/" + f.getValue());
		
		Var g1 = rm.getOutputVar("g1");
		ExpressionEvaluator gee1 = (ExpressionEvaluator)g1.getEvaluator();
		logger.info("\n\nexpression:\n" + gee1.getName() + "/" + gee1.getExpression());
		logger.info("\n\nresponse:\n" + gee1.getName() + "/" + g1.getValue());
		
		g1.selectEvaluator("g1e1");
		ExpressionEvaluator gee2 = (ExpressionEvaluator)g1.getEvaluator();
		logger.info("\n\nexpression:\n" + gee2.getName() + "/" + gee2.getExpression());
		logger.info("\n\nresponse:\n" + gee2.getName() + "/" + g1.getValue());

	}

	private static ResponseModel getMultiFidelityResponseModel() throws ContextException, RemoteException, EvaluationException {
		int designVarCount = 4;
		int responseVarCount = 4;
			
		ResponseModel am = responseModel("Response Analysis", 
			designVars("x", designVarCount), 
			linkedVars("xl1"),
			parameterVars("p1"),
			outputVars("f"),realization("f", evaluation("fe1"), evaluation("fe2")),
			outputVars("f",4),
			realization("f1",evaluation("f1e")),realization("f2",evaluation("f2e")),realization("f3",evaluation("f3e")),realization("f4",evaluation("f4e")),
			outputVars("g", responseVarCount-1),
			realization("g1", evaluation("g1e1"), evaluation("g1e2")), realization("g2", evaluation("g2e")),realization("g3", evaluation("g3e")) );
			
			//configureMultiFidelityAnalysisModel(am); FIX IT
			
			return am;
	}

	private static Configurator getMultiFidelityAnalysisConfigurator(VarModel am)
		throws ContextException, EvaluationException, RemoteException {
		// multifidelity is for f and g1. Each will have two evaluators: fe1, fe2 and g1e1, g1e2
			
		// fe1, g1e1, g2, g3, x1, x2, x3, x4		
		// design vars: x1, x2, x3, x4
		// response vars: f, g1, g2, g3
		// response fe1:f="x1^2-5.0*x1+x2^2-5.0*x2+2.0*x3^2-21.0*x3+x4^2+7.0*x4+50.0"
		// response g1e1:g1="x1^2+x1+x2^2-x2+x3^2+x3+x4^2-x4-8.0" 
		// response g2="x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"
		// response g3="2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"
	
		// configure response vars
//		Evaluator fe1 = evaluator("fe1", "x1^2-5.0*x1+x2^2-5.0*x2+2.0*x3^2-21.0*x3+x4^2+7.0*x4+50.0");
//		model.setResponseEvaluator("f", fe1);
//		fe1.addargs( model.getDesignVars("x1", "x2", "x3", "x4"));
//	
//		Evaluator g1e1 = evaluator("g1e1", "x1^2+x1+x2^2-x2+x3^2+x3+x4^2-x4-8.0");
//		model.setResponseEvaluator("g1", g1e1);
//		g1e1.addargs(model.getDesignVars("x1", "x2", "x3", "x4"));	
//	
//		Evaluator g2e = evaluator("g2e", "x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0");
//		model.setResponseEvaluator("g2", g2e);
//		g2e.addargs(model.getDesignVars("x1", "x2", "x3", "x4"));	
//	
//		Evaluator g3e = evaluator("g3e", "2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0");
//		model.setResponseEvaluator("g3", g3e);
//		g3e.addargs(model.getDesignVars("x1", "x2", "x3", "x4"));	
	return new Configurator((Configurable)am) {
		   
		public boolean configure(Object... configs) throws ConfigurationException, RemoteException {
		VarModel	model = (VarModel)configurable;
		
		try {
		var(model, "f", "fe1", evaluator("fe1", "x1^2-5.0*x1+x2^2-5.0*x2+2.0*x3^2-21.0*x3+x4^2+7.0*x4+50.0"),
					args("x1", "x2", "x3", "x4"));
		var(model, "g1", evaluator("g1e1", "x1^2+x1+x2^2-x2+x3^2+x3+x4^2-x4-8.0"),
				args("x1", "x2", "x3", "x4"));
		var(model, "g2", evaluator("g2e", "x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"),
				args("x1", "x2", "x3", "x4"));
		var(model, "g3", evaluator("g3e", "2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"),
				args("x1", "x2", "x3", "x4"));
		
		
		// fe2, g1e2, , f1, f2, f3, f4, g2, g3, x1, x2, x3, x4
		// Function of Functions ResponseModel with a linked variable and a parameter
		// design vars: x1, x2, x3, x4,
		// linked vars: xl1="7.0*x4"
		// parameters: p1=50.
		// response vars: f, f1, f2, f3, f4, g1, g2, g3
		// response f="f1+f3+f4-21.0*x3+xl1+p1"
		// response f1="x1^2+x2^2"
		// response f2="x1+x2"
		// response f3="-5.0*f2"
		// response f4=2.0*x3^2+x4^2
		// response g1="f1+f2-2.0*x2+x3^2+x3+x4^2-x4-8.0" 
		// response g2="x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"
		// response g3="2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"

		// configure linked vars
//		Evaluator xl1e = new JepEvaluator("xl1e", "7*x4");
//		xl1e.addDependent(model.getDesignVar("x4"));
//		model.setLinkedEvaluator("xl1", xl1e);
	
		var(model, "xl1", evaluator("xl1e", "7*x4"), args("x4"));
		
		// configure response vars (add .01 to fe2 and g1e2 so a difference can be seen between evaluators fe1,fe2 and g1e1, g1e2)
//		Evaluator fe2 = evaluator("fe2", "f1+f3+f4-21.0*x3+xl1+p1+.01");
//		model.setResponseEvaluator("f", fe2);
//		fe2.addargs(model.getDesignVars("x3"), model.getResponseVars("f1","f3","f4"));
//		fe2.addargs(model.getLinkedVar("xl1"), model.getParameterVar("p1"));
	
		var(model, "f", "fe2", expression("fe2", "f1+f3+f4-21.0*x3+xl1+p1",
				args(designVars(model, "x3"), outputVars(model, "f1", "f3",
						"f4"), linkedVars(model, "xl1"), parameterVars(model, "p1"))));
	
//		Evaluator f1e = evaluator("f1e", "x1^2+x2^2");
//		model.setResponseEvaluator("f1", f1e);
//		f1e.addargs(model.getDesignVars("x1","x2"));	
	
		var(model, "f1", evaluator("f1e", "x1^2+x2^2"), args("x1","x2"));
		
//		Evaluator f2e = evaluator("f2e","x1+x2");
//		model.setResponseEvaluator("f2", f2e);
//		f2e.addargs(model.getDesignVars("x1","x2"));
	
		var(model, "f2", evaluator("f2e","x1+x2"), args("x1","x2"));
		
//		Evaluator f3e = evaluator("f3e", "-5.0*f2");
//		model.setResponseEvaluator("f3", f3e);
//		f3e.addDependent(model.getResponseVar("f2"));
	
		var(model, "f3", evaluator("f3e", "-5.0*f2"), args("f2"));

		
//		Evaluator f4e = evaluator("f4e", "2.0*x3^2+x4^2");
//		model.setResponseEvaluator("f4", f4e);
//		f4e.addargs( model.getDesignVars("x3","x4"));
	
		var(model, "f4", evaluator("f4e", "2.0*x3^2+x4^2"), args("x3","x4"));

//		Evaluator g1e2 = evaluator("g1e2", "f1+f2-2.0*x2+x3^2+x3+x4^2-x4-8.0");
//		model.setResponseEvaluator("g1", g1e2);
//		g1e2.addargs(model.getResponseVars("f1", "f2"), model.getDesignVars("x2","x3","x4"));
	
		var(model, "g1", evaluator("g1e2", "f1+f2-2.0*x2+x3^2+x3+x4^2-x4-8.0"), 
					args("f1", "f2", "x2","x3","x4"));
		} catch (Exception e) {
			throw new ConfigurationException();
		}
		configurable = (Configurable)model;
		return true;
		}
	};
		// no need to configure g2 & g3 again. 
	}

	public static void doFunctionOfFunctionFileParametricAnalysis() throws 
		ParException, EvaluationException, ConfigurationException, ContextException, 
		ExertionException, SignatureException, IOException {
		// test ParametricModel
		ParametricModel am = getFunctionOfFunctionFileParametricModel();
		am.setInvarinatVarValues(entry("p1", 50.0));
		am.evaluate();
		am.writeOutputTableToFile();
		logger.info("\n\nParametricAnalysis input table: " + am.getParametricTable());
		logger.info("\n\nParametricAnalysis parametric table: " + am.getOutTable());
	}

	private static ParametricModel getFunctionOfFunctionFileParametricModel() throws ContextException, IOException, EvaluationException {
		
		// Function of Functions ResponseModel with a linked variable and a parameter
		// design vars: x1, x2, x3, x4,
		// linked vars: xl1="7.0*x4"
		// parameters: p1=50.
		// response vars: f, f1, f2, f3, f4, g1, g2, g3
		// response f="f1+f3+f4-21.0*x3+xl1+p1"
			// response f1="x1^2+x2^2"
			// response f2="x1+x2"
			// response f3="-5.0*f2"
			// response f4=2.0*x3^2+x4^2
		// response g1="f1+f2-2.0*x2+x3^2+x3+x4^2-x4-8.0" 
		// response g2="x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"
		// response g3="2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"

		int designVarCount = 4;

		ParametricModel am = parametricModel("Parametric Analysis", 
				designVars("x", designVarCount), 
				linkedVars("xl1"),
				parameterVars("p1"),
				outputVars("f"),
				outputVars("f",4),
				outputVars("g", 3),
				parametricTable(input("../data/rosenSuzuki1in.data"),
				output("../data/rosenSuzuki1FFout.data", ", ")) );

		configureFunctionOfFunctionAnalysisModel(am);

		logger.info("literal context: " + am);
		return am;
	}




	
	public static void doFileParametricAnalysis() throws 
	ParException, EvaluationException, ConfigurationException, ContextException, 
	ExertionException, SignatureException, IOException {
		// test ParametricModel
		ParametricModel am = getFileParametricModel();
		
		am.evaluate();
		am.writeOutputTableToFile();
		logger.info("\n\nParametricAnalysis input table: " + am.getParametricTable());
		logger.info("\n\nParametricAnalysis parametric table: " + am.getOutTable());
		
		am.writeOutputTableToFile("rosenSuzuki1.table");
	}
	
	public static void doUrlParametricAnalysis() throws 
	ParException, EvaluationException, ConfigurationException, ContextException, 
	ExertionException, SignatureException, IOException {
		// test ParametricModel
		ParametricModel am = getUrlParametricModel();
		
		am.evaluate();
		am.writeOutputTableToURL();
		logger.info("\n\nParametricAnalysis input table: " + am.getParametricTable());
		logger.info("\n\nParametricAnalysis parametric table: " + am.getOutTable());
	}
	
	public static void doOptimization() throws ParException, RemoteException,
	EvaluationException, ConfigurationException, ContextException, ExertionException, SignatureException, FilterException {
		
		OptimizationModel om = getOptimizationModel();
		om.setDesignVarValues(entry("x1", 2.0), entry("x2", 3.0), entry("x3", 4.0), entry("x4", 5.0));
		
		VarList objectiveVars = om.getObjectiveVars();
		logger.info("\n objective vars:\n" + objectiveVars.getNames());
		
		Var fo = om.getObjectiveVar("fo");
		logger.info("\n objective fo value:\n" + fo.getValue());
		
		VarList constraintVars = om.getConstraintVars();
		logger.info("\n constraint vars:\n" + constraintVars.getNames());
		
		Var g1c = om.getConstraintVar("g1c");
		logger.info("\n constraint g1c response:\n" + g1c.getResponse());
		logger.info("\n constraint g1c filter:\n" + g1c.getFilter());
		logger.info("\n constraint g1c realtion:\n" + g1c.getRelation());
		logger.info("\n constraint g1c allowable:\n" + g1c.getAllowable());
		logger.info("\n constraint g1c value:\n" + g1c.getValue());
	}
	
	public static void doSensitivitiesAnalysis() throws ParException, RemoteException,
	EvaluationException, ConfigurationException, ContextException, ExertionException, SignatureException {
		
		ResponseModel sm = getSensitivitiesModel();
		sm.setDesignVarValues(entry("x1", 2.0), entry("x2", 3.0), entry("x3", 4.0), entry("x4", 5.0));	
		//logger.info("\n\ndoSensitivityAnalysis out: " + sm.getSensitivities());
		
		Var f = sm.getOutputVar("f");
		
		logger.info("\n current function value:\n" + f.getValue());
		
		logger.info("----------------- partial derivative f wrt x1 fe|feg1: " + f.getPartialDerivative("x1", "feg1"));
		logger.info("----------------- partial derivative f wrt x2 fe|feg1: " + f.getPartialDerivative("x2", "feg1"));
		logger.info("----------------- partial derivative f wrt x3 fe|feg1: " + f.getPartialDerivative("x3", "feg1"));	
		logger.info("----------------- partial derivative f wrt x4 fe|feg1: " + f.getPartialDerivative("x4", "feg1"));
		logger.info("*-*-*-*-*-*-*-*-* partial gradient   f for    fe|feg1: " + f.getPartialDerivativeTable( "feg1"));
		logger.info("*+*+*+*+*+*+*+*+* total gradient     f for    fe|feg1: " + f.getTotalDerivativeTable( "feg1"));
		
		Var g1 = sm.getOutputVar("g1");
		Var g2 = sm.getOutputVar("g2");
		Var g3 = sm.getOutputVar("g3");
		logger.info("*+*+*+*+*+*+*+*+* total gradient     g1 for    g1e|g1eg1: " + g1.getTotalDerivativeTable( "g1eg1"));
		logger.info("*+*+*+*+*+*+*+*+* total gradient     g2 for    g2e|g2eg1: " + g2.getTotalDerivativeTable( "g2eg1"));
		logger.info("*+*+*+*+*+*+*+*+* total gradient     g3 for    g3e|g3eg1: " + g3.getTotalDerivativeTable( "g3eg1"));
		
		//logger.info("\n\ndoSensitivityAnalysis out: " + sm.getSensitivities());
	}
	
	public static void doFunctionOfFunctionSensitivityAnalysis() throws ParException, RemoteException,
	EvaluationException, ConfigurationException, ContextException, ExertionException, SignatureException {
		
		ResponseModel sm = getFunctionOfFunctionSensitivitiesModel();
		
		//sm.setDesignVarValues(v("x1", 2.0), v("x2", 3), v("x3", 4), v("x4", 5));
		sm.setDesignVarValues(entry("x1", 2.0), entry("x2", 3.0), entry("x3", 4.0), entry("x4", 5.0));
		sm.setInvarinatVarValues(entry("p1", 50.0));
		//logger.info("\n\ndoSensitivityAnalysis out: " + sm.getSensitivities());
		
		Var f = sm.getOutputVar("f");
		Var f1 = sm.getOutputVar("f1");
		Var f2 = sm.getOutputVar("f2");
		Var f3 = sm.getOutputVar("f3");
		Var f4 = sm.getOutputVar("f4");
		Var xl1 = sm.getVar("xl1");
		Var x3 = sm.getVar("x3");
		
//		logger.info("\n current f1 function value: " + f1.getValue());
//		logger.info("\n current f2 function value: " + f2.getValue());
//		logger.info("\n current f3 function value: " + f3.getValue());
//		logger.info("\n current f4 function value: " + f4.getValue());
//		logger.info("\n current xl1 function value: " + xl1.getValue());
//		
//		logger.info("\n current function f: " + f);
//		logger.info("\n current f function value: " + f.getValue());
		
		logger.info("*-*-*-*-*-*-*-*-* partial gradients  for    RozenSuzuki: " + sm.getPartialDerivativeTables());
		logger.info("*+*+*+*+*+*+*+*+* total gradients  for    RozenSuzuki: " + sm.getTotalDerivativeTables());
		
//		logger.info("----------------- total derivative f wrt x3    fe|feg1: " + sm.getPartialDerivative("f", "x3", "feg1"));
//		logger.info("----------------- total derivative f wrt x3    fe|feg1: " + sm.getTotalDerivative("f", "x3", "feg1"));
//		logger.info("----------------- total derivative f wrt f1    fe|feg1: " + sm.getTotalDerivative("f", "f1", "feg1"));
		
//		logger.info("----------------- partial derivative f wrt f1 fe|feg1: " + f.getPartialDerivative("f1", "feg1"));
//		logger.info("----------------- partial derivative f wrt f3 fe|feg1: " + f.getPartialDerivative("f3", "feg1"));
//		logger.info("----------------- partial derivative f wrt f4 fe|feg1: " + f.getPartialDerivative("f4", "feg1"));	
//		logger.info("----------------- partial derivative f wrt x3 fe|feg1: " + f.getPartialDerivative("x3", "feg1"));
//		logger.info("----------------- partial derivative f wrt xl1 fe|feg1: " + f.getPartialDerivative("xl1", "feg1"));
		
		//logger.info("----------------- partial gradient f   fe|feg1: " + sm.getGradient("f", "f1eg1"));
		//logger.info("----------------- total derivative f wrt x1    fe|feg1: " + sm.getTotalDerivative("f", "x1", "feg1"));
		//logger.info("----------------- total derivative f1 wrt x1    f1e|f1eg1: " + sm.getTotalDerivative("f1", "x1", "f1eg1"));
		//logger.info("----------------- total derivative f3 wrt x1    f3e|f3eg1: " + sm.getTotalDerivative("f3", "x1", "f3eg1"));
		//logger.info("----------------- total derivative f4 wrt x1    f4e|f4eg1: " + sm.getTotalDerivative("f4", "x1", "f4eg1"));
		//logger.info("----------------- total derivative x3 wrt x1    x3e|x3eg1: " + sm.getTotalDerivative("x3", "x1", "x3eg1"));
		//logger.info("----------------- total derivative xl1 wrt x1    xl1e|xl1eg1: " + sm.getTotalDerivative("xl1", "x1", "xl1eg1"));
		//logger.info("----------------- total derivative p1 wrt x1    p1e|p1eg1: " + sm.getTotalDerivative("p1", "x1", "p1eg1"));
		
		//logger.info("----------------- total derivative f wrt x2    fe|feg1: " + sm.getTotalDerivative("f", "x2", "feg1"));
		//logger.info("----------------- total derivative f1 wrt x2    f1e|f1eg1: " + sm.getTotalDerivative("f1", "x2", "f1eg1"));
		//logger.info("----------------- total derivative f3 wrt x2    f3e|f3eg1: " + sm.getTotalDerivative("f3", "x2", "f3eg1"));
		//logger.info("----------------- total derivative f4 wrt x2    f4e|f4eg1: " + sm.getTotalDerivative("f4", "x2", "f4eg1"));
		//logger.info("----------------- total derivative x3 wrt x2    x3e|x3eg1: " + sm.getTotalDerivative("x3", "x2", "x3eg1"));
		//logger.info("----------------- total derivative xl1 wrt x2   xl1e|xl1eg1: " + sm.getTotalDerivative("xl1", "x2", "xl1eg1"));
		//logger.info("----------------- total derivative p1 wrt x2    p1e|p1eg1: " + sm.getTotalDerivative("p1", "x2", "p1eg1"));
		
//		logger.info("----------------- total derivative f1 wrt f3    fe|feg1: " + sm.getTotalDerivative("f", "f3", "feg1"));
		
//		logger.info("----------------- total derivative f3 wrt x1    f3e|f3eg1: " + sm.getTotalDerivative("f3", "x1", "f3eg1"));
		
//		logger.info("----------------- total derivative f wrt x1    fe|feg1: " + sm.getTotalDerivative("f", "x1", "feg1"));
//		logger.info("----------------- total derivative f wrt x2    fe|feg1: " + sm.getTotalDerivative("f", "x2", "feg1"));
// 		logger.info("----------------- total derivative f wrt x3    fe|feg1: " + sm.getTotalDerivative("f", "x3", "feg1"));
//		logger.info("----------------- total derivative f wrt x4    fe|feg1: " + sm.getTotalDerivative("f", "x4", "feg1"));
		
//		logger.info("*-*-*-*-*-*-*-*-* partial gradient   f for    fe|feg1: " + sm.getPartialGradient("f", "feg1"));
//		logger.info("*-*-*-*-*-*-*-*-* partial gradients   f for    f: " + sm.getPartialGradients("f"));
		//logger.info("*-*-*-*-*-*-*-*-* partial gradients  for    RozenSuzuki: " + sm.getPartialGradients());
		
//		logger.info("*+*+*+*+*+*+*+*+* total gradient     f for    fe|feg1: " + sm.getTotalGradient("f", "feg1"));
//		logger.info("*+*+*+*+*+*+*+*+* total gradients   f for    f: " + sm.getTotalGradients("f"));
		//logger.info("*+*+*+*+*+*+*+*+* total gradients  for    RozenSuzuki: " + sm.getTotalGradients());

		//logger.info("*+*+*+*+*+*+*+*+* gradient     f for    fe|feg1: " + sm.getGradient("f", "feg1"));
//		logger.info("*+*+*+*+*+*+*+*+* gradient     f wrt x1   fe|feg1: " + sm.getGradient("f", "feg1", "x1"));
//		logger.info("*+*+*+*+*+*+*+*+* gradient     f wrt x2   fe|feg1: " + sm.getGradient("f", "feg1", "x2"));
//		logger.info("*+*+*+*+*+*+*+*+* gradient     f wrt x3   fe|feg1: " + sm.getGradient("f", "feg1", "x3"));
//		logger.info("*+*+*+*+*+*+*+*+* gradient     f wrt x4   fe|feg1: " + sm.getGradient("f", "feg1", "x4"));
		
		//logger.info("*+*+*+*+*+*+*+*+* gradient     f1 for    f1e|f1eg1: " + sm.getGradient("f", "f1eg1", "x1"));
		//logger.info("*+*+*+*+*+*+*+*+* gradient     f1 for    f1e|f1eg1: " + sm.getGradient("f3", "f1eg1", "x2"));
		
		//logger.info("*+*+*+*+*+*+*+*+* gradient     f for    fe|feg1: " + sm.getGradient("f", "feg1", "x4"));
		
//		logger.info("*+*+*+*+*+*+*+*+* total gradient     f for    fe|feg1: " + sm.getTotalGradient("f", "feg1"));
//		logger.info("*+*+*+*+*+*+*+*+* gradient     f for    fe|feg1: " + sm.getGradient("f", "feg1"));

//		logger.info("");
//		logger.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//		logger.info("\n current f1 function value:\n" + f1.getValue());
//		logger.info("----------------- partial derivative f1 wrt x1 f1e|f1eg1: " + f1.getPartialDerivative("x1", "f1eg1"));
//		logger.info("----------------- partial derivative f1 wrt x2 f1e|f1eg1: " + f1.getPartialDerivative("x2", "f1eg1"));
//		logger.info("----------------- partial gradient f1  for     f1e|f1eg1: " + f1.getPartialGradient("f1eg1"));
//		logger.info("----------------- total   gradient f1  for     f1e|f1eg1: " + f1.getTotalGradient("f1eg1"));
//		logger.info("");
//		logger.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//		logger.info("\n current f2 function value:\n" + f2.getValue());
//		logger.info("----------------- partial derivative f2 wrt x1 f2e|f2eg1: " + f2.getPartialDerivative("x1", "f2eg1"));
//		logger.info("----------------- partial derivative f2 wrt x2 f2e|f2eg1: " + f2.getPartialDerivative("x2", "f2eg1"));
//		logger.info("----------------- partial gradient f2  for     f2e|f2eg1: " + f2.getPartialGradient("f2eg1"));
//		logger.info("----------------- total   gradient f2  for     f2e|f2eg1: " + f2.getTotalGradient("f2eg1"));
//		logger.info("");
//		logger.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//		logger.info("\n current f3 function value:\n" + f3.getValue());
//		logger.info("----------------- partial derivative f3 wrt f2 f3e|f3eg1: " + f3.getPartialDerivative("f2", "f3eg1"));
//		logger.info("----------------- partial gradient f3  for     f3e|f3eg1: " + f3.getPartialGradient("f3eg1"));
//		logger.info("----------------- total   gradient f3  for     f3e|f3eg1: " + f3.getTotalGradient("f3eg1")); // prints 0.0 , should be -5.0
//		// next two lines throw exception
//		//logger.info("----------------- total derivative f3  wrt  x1 f3e|f3eg1: " + f3.getTotalDerivative("x1","f3eg1"));
//		//logger.info("----------------- total derivative f3  wrt  x2 f3e|f3eg1: " + f3.getTotalDerivative("x2","f3eg1"));
//		logger.info("");
//		logger.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//		logger.info("\n current f4 function value:\n" + f4.getValue());
//		logger.info("----------------- partial derivative f4 wrt x3 f4e|f4eg1: " + f4.getPartialDerivative("x3", "f4eg1"));
//		logger.info("----------------- partial derivative f4 wrt x4 f4e|f4eg1: " + f4.getPartialDerivative("x4", "f4eg1"));
//		logger.info("----------------- partial  gradient f4  for    f3e|f3eg1: " + f4.getPartialGradient("f4eg1"));
//		logger.info("----------------- total    gradient f4  for    f3e|f3eg1: " + f4.getTotalGradient("f4eg1"));
//		logger.info("");
//		logger.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//
//		
//		Var g1 = sm.getResponseVar("g1");
//		Var g2 = sm.getResponseVar("g2");
//		Var g3 = sm.getResponseVar("g3");
//		
//		logger.info("\n current g1 function value:\n" + g1.getValue());
//		logger.info("----------------- partial derivative g1 wrt f1 g1e|g1eg1: " + g1.getPartialDerivative("f1", "g1eg1"));
//		logger.info("----------------- partial derivative g1 wrt f2 g1e|g1eg1: " + g1.getPartialDerivative("f2", "g1eg1"));
//		logger.info("----------------- partial derivative g1 wrt x2 g1e|g1eg1: " + g1.getPartialDerivative("x2", "g1eg1"));
//		logger.info("----------------- partial derivative g1 wrt x3 g1e|g1eg1: " + g1.getPartialDerivative("x3", "g1eg1"));
//		logger.info("----------------- partial derivative g1 wrt x4 g1e|g1eg1: " + g1.getPartialDerivative("x4", "g1eg1"));
//		//logger.info("----------------- total derivative g1 wrt x1 g1e|g1eg1: " + g1.getTotalDerivative("x1", "g1eg1"));
//		logger.info("----------------- total derivative g1 wrt x2 g1e|g1eg1: " + g1.getTotalDerivative("x2", "g1eg1"));
//		logger.info("*+*+*+*+*+*+*+*+* partial gradient   g1 for    g1e|g1eg1: " + g1.getPartialGradient( "g1eg1"));
//		logger.info("*+*+*+*+*+*+*+*+* total gradient     g1 for    g1e|g1eg1: " + g1.getTotalGradient( "g1eg1"));
//		logger.info("");
//		logger.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//		logger.info("\n current g2 function value:\n" + g2.getValue());
//		logger.info("*+*+*+*+*+*+*+*+* total gradient     g2 for    g2e|g2eg1: " + g2.getTotalGradient( "g2eg1"));
//		logger.info("");
//		logger.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//		logger.info("\n current g3 function value:\n" + g3.getValue());
//		logger.info("*+*+*+*+*+*+*+*+* total gradient     g3 for    g3e|g3eg1: " + g3.getTotalGradient( "g3eg1"));
//		
		//logger.info("\n\ndoSensitivityAnalysis out: " + sm.getSensitivities());
	}

	private static ResponseModel getFunctionOfFunctionSensitivitiesModel() throws ContextException, RemoteException, EvaluationException {
		// Function of Functions ResponseModel with a linked variable and a parameter
		// design vars: x1, x2, x3, x4,
			// linked vars: xl1="7.0*x4"
		// parameters: p1=50.
		// response vars: f, f1, f2, f3, f4, g1, g2, g3
		// response f="f1+f3+f4-21.0*x3+xl1+p1"
				// partial derivative df/df1="1.0"
				// partial derivative df/df3="1.0"
				// partial derivative df/df4="1.0"
				// partial derivative df/dx3="-21.0"
				// partial derivative df/dxl1="1.0"
			// response f1="x1^2+x2^2"
				// partial derivative df1/dx1 ="2.0*x1"
				// partial derivative df1/dx2="2.0*x2"
			// response f2="x1+x2"
				// partial derivative df2/dx1="1.0"
				// partial derivative df2/dx2="1.0"
			// response f3="-5.0*f2"
				// partial derivative df3/df2="-5.0"
			// response f4=2.0*x3^2+x4^2
				// partial derivative df4/dx3="4.0*x3"
				// partial derivative df4/dx4="2.0*x4"
		// response g1="f1+f2-2.0*x2+x3^2+x3+x4^2-x4-8.0" 
				// partial derivative dg1/df1="1.0"
				// partial derivative dg1/df2="1.0"
				// partial derivative dg1/dx2="-2.0"
				// partial derivative dg1/dx3=2.0*x3+1.0"
				// partial derivative dg1/dx4=2.0*x4-1.0"
		// response g2="x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"
				// partial derivative dg2dx1 = "2.0*x1-1.0"
				// partial derivative dg2dx2 = "4.0*x2"
				// partial derivative dg2dx3 = "2.0*x3"
				// partial derivative dg2dx4 = "4.0*x4-1.0"
		// response g3="2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"
				// partial derivative dg3dx1 = "4.0*x1+2.0"
				// partial derivative dg3dx2 = "2.0*x2-1.0"
				// partial derivative dg3dx3 = "2.0*x3"
				// partial derivative dg3dx4 = "-1.0"	
		
		int designVarCount = 4;

		ResponseModel sm = responseModel("Sensitivity Analysis", 
			designVars("x", designVarCount), 
			linkedVars("xl1"),
				realization("xl1", differentiation(wrt("x4"))),
			parameterVars("p1"),
			outputVars("f"),
				realization("f", differentiation(wrt("f1", "f3", "f4", "x3", "xl1"))),
			outputVars("f",4),
				realization("f1", differentiation(wrt("x1", "x2"))),
				realization("f2", differentiation(wrt("x1", "x2"))),
				realization("f3", differentiation(wrt("f2"))),
				realization("f4", differentiation(wrt("x3", "x4"))),
			outputVars("g", 3),
				realization("g1", differentiation(wrt("f1", "f2", "x2", "x3", "x4"))),
				realization("g2", differentiation(wrt("x1", "x2", "x3", "x4"))),	
				realization("g3", differentiation(wrt("x1", "x2", "x3", "x4"))));
		
		configureFunctionOfFunctionAnalysisModel(sm);
		configureFunctionOfFunctionSensitivityModel(sm);
		
		return sm;
	}

	private static ResponseModel configureFunctionOfFunctionSensitivityModel(
			ResponseModel sm) throws ContextException {
		// Function of Functions ResponseModel with a linked variable and a parameter
		// design vars: x1, x2, x3, x4,
			// linked vars: xl1="7.0*x4"
		// parameters: p1=50.
		// response vars: f, f1, f2, f3, f4, g1, g2, g3
		// response f="f1+f3+f4-21.0*x3+xl1+p1"
				// partial derivative df/df1="1.0"
				// partial derivative df/df3="1.0"
				// partial derivative df/df4="1.0"
				// partial derivative df/dx3="-21.0"
				// partial derivative df/dxl1="1.0"
		// response f1="x1^2+x2^2"
				// partial derivative df1/dx1 ="2.0*x1"
				// partial derivative df1/dx2="2.0*x2"
		// response f2="x1+x2"
				// partial derivative df2/dx1="1.0"
				// partial derivative df2/dx2="1.0"
		// response f3="-5.0*f2"
				// partial derivative df3/df2="-5.0"
		// response f4=2.0*x3^2+x4^2
				// partial derivative df4/dx3="4.0*x3"
				// partial derivative df4/dx4="2.0*x4"
		// response g1="f1+f2-2.0*x2+x3^2+x3+x4^2-x4-8.0" 
				// partial derivative dg1/df1="1.0"
				// partial derivative dg1/df2="1.0"
				// partial derivative dg1/dx2="-2.0"
				// partial derivative dg1/dx3=2.0*x3+1.0"
				// partial derivative dg1/dx4=2.0*x4-1.0"
		// response g2="x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"
				// partial derivative dg2dx1 = "2.0*x1-1.0"
				// partial derivative dg2dx2 = "4.0*x2"
				// partial derivative dg2dx3 = "2.0*x3"
				// partial derivative dg2dx4 = "4.0*x4-1.0"
		// response g3="2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"
				// partial derivative dg3dx1 = "4.0*x1+2.0"
				// partial derivative dg3dx2 = "2.0*x2-1.0"
				// partial derivative dg3dx3 = "2.0*x3"
				// partial derivative dg3dx4 = "-1.0"	
		
		
		// link variable
		ServiceEvaluator dfedxl1e = expression("dfedxl1e", "7",args(sm.getDesignVars("x4")));
		sm.setGradientEvaluators("xl1",  "xl1e", "xl1eg1", list(dfedxl1e));
	
		// response f="f1+f3+f4-21.0*x3+xl1+p1"
		// partial derivative df/df1="1.0"
		// partial derivative df/df3="1.0"
		// partial derivative df/df4="1.0"
		// partial derivative df/dx3="-21.0"
		// partial derivative df/dxl1="1.0"
		ServiceEvaluator dfedf1e1 = evaluator("dfedf1e1", "1.0");
		ServiceEvaluator dfedf3e1 = evaluator("dfedf3e1", "1.0");
		ServiceEvaluator dfedf4e1 = evaluator("dfedf4e1", "1.0");
		ServiceEvaluator dfedx3e1 = evaluator("dfedx3e1", "-21.0");
		ServiceEvaluator dfedxl1e1 = evaluator("dfedxl1e1", "1.0");
		List<ServiceEvaluator> feg1 = list(dfedf1e1, dfedf3e1, dfedf4e1, dfedx3e1, dfedxl1e1);	
		sm.setGradientEvaluators("f", "fe", "feg1",  feg1);
		
		// response f1="x1^2+x2^2"
		// partial derivative df1/dx1 ="2.0*x1"
		// partial derivative df1/dx2="2.0*x2"
		ServiceEvaluator df1edx1e1 = expression("df1edx1e1", "2.0*x1",args(sm.getDesignVars("x1")));
		ServiceEvaluator df1edx2e1 = expression("df1edx2e1", "2.0*x2",args(sm.getDesignVars("x2")));
		List<ServiceEvaluator> f1eg1 = list(df1edx1e1, df1edx2e1);	
		sm.setGradientEvaluators("f1", "f1e", "f1eg1",  f1eg1);
				
		// response f2="x1+x2"
		// partial derivative df2/dx1="1.0"
		// partial derivative df2/dx2="1.0"
		ServiceEvaluator df2edx1e1 = evaluator("df2edx2e1","1.0");
		ServiceEvaluator df2edx2e1 = evaluator("df2edx2e1", "1.0");
		List<ServiceEvaluator> f2eg1 = list(df2edx1e1, df2edx2e1);	
		sm.setGradientEvaluators("f2", "f2e", "f2eg1",  f2eg1);
	
			
		// response f3="-5.0*f2"
		// partial derivative df3/df2="-5.0"
		ServiceEvaluator df3edf2e1 = evaluator("df3edf2e1","-5.0");
		List<ServiceEvaluator> f3eg1 = list(df3edf2e1);	
		sm.setGradientEvaluators("f3", "f3e", "f3eg1",  f3eg1);
			
			
		// response f4=2.0*x3^2+x4^2
		// partial derivative df4/dx3="4.0*x3"
		// partial derivative df4/dx4="2.0*x4"
		ServiceEvaluator df4edx3e1 = expression("df4edx3e1","4.0*x3",args(sm.getDesignVars("x3")));
		ServiceEvaluator df4edx4e1 = expression("df4edx4e1", "2.0*x4",args(sm.getDesignVars("x4")));
		List<ServiceEvaluator> f4eg1 = list(df4edx3e1, df4edx4e1);	
		sm.setGradientEvaluators("f4", "f4e", "f4eg1",  f4eg1);

		// response g1="f1+f2-2.0*x2+x3^2+x3+x4^2-x4-8.0" 
		// partial derivative dg1/df1="1.0"
		// partial derivative dg1/df2="1.0"
		// partial derivative dg1/dx2="-2.0"
		// partial derivative dg1/dx3=2.0*x3+1.0"
		// partial derivative dg1/dx4=2.0*x4-1.0"			
		ServiceEvaluator dg1edf1e1 = evaluator("dg1edf1e1", "1.0");
		ServiceEvaluator dg1edf2e1 = evaluator("dg1edf2e1", "1.0");
		ServiceEvaluator dg1edx2e1 = evaluator("dg1edx2e1", "-2.0");
		ServiceEvaluator dg1edx3e1 = expression("dg1edx3e1", "2.0*x3+1.0",args(sm.getDesignVars("x3")));
		ServiceEvaluator dg1edx4e1 = expression("dg1edx4e1", "2.0*x4-1.0",args(sm.getDesignVars("x4")));
		List<ServiceEvaluator> g1eg1 = list(dg1edf1e1, dg1edf2e1, dg1edx2e1, dg1edx3e1, dg1edx4e1);	
		sm.setGradientEvaluators("g1", "g1e", "g1eg1",  g1eg1);

		// response g2="x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"
		// partial derivative dg2dx1 = "2.0*x1-1.0"
		// partial derivative dg2dx2 = "4.0*x2"
		// partial derivative dg2dx3 = "2.0*x3"
		// partial derivative dg2dx4 = "4.0*x4-1.0"
		ServiceEvaluator dg2edx1e1 = expression("dg2edx1e1", "2.0*x1-1",args(sm.getDesignVars("x1")));
		ServiceEvaluator dg2edx2e1 = expression("dg2edx2e1", "4.0*x2",args(sm.getDesignVars("x2")));
		ServiceEvaluator dg2edx3e1 = expression("dg2edx3e1", "2.0*x3",args(sm.getDesignVars("x3")));
		ServiceEvaluator dg2edx4e1 = expression("dg2edx4e1", "4.0*x4-1.0",args(sm.getDesignVars("x4")));
		List<ServiceEvaluator> g2eg1 = list(dg2edx1e1, dg2edx2e1, dg2edx3e1, dg2edx4e1);	
		sm.setGradientEvaluators("g2", "g2e", "g2eg1",  g2eg1);
	
		// response g3="2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"
		// partial derivative dg3dx1 = "4.0*x1+2.0"
		// partial derivative dg3dx2 = "2.0*x2-1.0"
		// partial derivative dg3dx3 = "2.0*x3"
		// partial derivative dg3dx4 = "-1.0"
		ServiceEvaluator dg3edx1e1 = expression("dg3edx1e1", "4.0*x1+2.0",args(sm.getDesignVars("x1")));
		ServiceEvaluator dg3edx2e1 = expression("dg3edx2e1", "2.0*x2-1.0",args(sm.getDesignVars("x2")));
		ServiceEvaluator dg3edx3e1 = expression("dg3edx3e1", "2.0*x3",args(sm.getDesignVars("x3")));
		ServiceEvaluator dg3edx4e1 = evaluator("dg3edx4e1", "-1.0");
		List<ServiceEvaluator> g3eg1 = list(dg3edx1e1, dg3edx2e1, dg3edx3e1, dg3edx4e1);	
		sm.setGradientEvaluators("g3", "g3e", "g3eg1",  g3eg1);
		return sm;
	}

	public static void doFunctionOfFunctionResponseAnalysis() throws ParException, RemoteException,
	EvaluationException, ConfigurationException, ContextException, ExertionException, SignatureException {
		// test ResponseModel
		ResponseModel rm = getFunctionOfFunctionResponseModel();
		rm.setInvarinatVarValues(entry("p1", 50.0));
		rm.setDesignVarValues(entry("x1", 2.0), entry("x2", 3.0), entry("x3", 4.0), entry("x4", 5.0));
		logger.info("\n\nresponse model: " + rm);
		
		// get all responses for the given values of xi
		logger.info("\n\nresponses: " + rm.getResponses());
		
		// examine the responseVar F
		Var f = rm.getOutputVar("f");
		ExpressionEvaluator ee = (ExpressionEvaluator)f.getEvaluator();
		logger.info("\n\nexpression:\n" + ee.getName() + "/" + ee.getExpression());
		logger.info("\n\nresponse:\n" + ee.getName() + "/" + f.getValue());
		
		
	}

	
	public static ResponseModel getFunctionOfFunctionResponseModel() throws ParException,
	RemoteException, EvaluationException, ConfigurationException,
	ContextException, ExertionException, SignatureException {

		// Function of Functions ResponseModel with a linked variable and a parameter
		// design vars: x1, x2, x3, x4,
			// linked vars: xl1="7.0*x4"
		// parameters: p1=50.
		// response vars: f, f1, f2, f3, f4, g1, g2, g3
		// response f="f1+f3+f4-21.0*x3+xl1+p1"
			// response f1="x1^2+x2^2"
			// response f2="x1+x2"
			// response f3="-5.0*f2"
			// response f4=2.0*x3^2+x4^2
		// response g1="f1+f2-2.0*x2+x3^2+x3+x4^2-x4-8.0" 
		// response g2="x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"
		// response g3="2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"

		int designVarCount = 4;

		ResponseModel rm = responseModel("Response Analysis", 
			designVars("x", designVarCount), 
			linkedVars("xl1"),
			parameterVars("p1"),
			outputVars("f"),realization("f", evaluation("fe")),
			outputVars("f", 4),
			realization("f1", evaluation("f1e")),
			realization("f2", evaluation("f2e")),
			realization("f3", evaluation("f3e")),
			realization("f4", evaluation("f4e")) ,
			outputVars("g", 3),
			realization("g1", evaluation("g1e")),
			realization("g2", evaluation("g2e")),
			realization("g3", evaluation("g3e"))) ;
		
		configureFunctionOfFunctionAnalysisModel(rm);
		logger.info("response model: " + rm);

		return rm;
}
	/**
	 * @param am
	 * @throws RemoteException 
	 * @throws ContextException 
	 */
	private static VarModel configureFunctionOfFunctionAnalysisModel(
			VarModel model) throws RemoteException, ContextException {
		// Function of Functions ResponseModel with a linked variable and a parameter
		// design vars: x1, x2, x3, x4,
			// linked vars: xl1="7.0*x4"
		// parameters: p1=50.
		// response vars: f, f1, f2, f3, f4, g1, g2, g3
		// response f="f1+f3+f4-21.0*x3+xl1+p1"
			// response f1="x1^2+x2^2"
			// response f2="x1+x2"
			// response f3="-5.0*f2"
			// response f4="2.0*x3^2+x4^2"
		// response g1="f1+f2-2.0*x2+x3^2+x3+x4^2-x4-8.0" 
		// response g2="x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"
		// response g3="2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"
		// configure linked vars
		ServiceEvaluator xl1e = new JepEvaluator("xl1e", "7*x4");
		xl1e.addArg(model.getDesignVar("x4"));
		model.setLinkedEvaluator("xl1", xl1e);
		
		// configure response vars
		ServiceEvaluator fe = evaluator("fe", "f1+f3+f4-21.0*x3+xl1+p1");
		model.setResponseEvaluator("f", fe);
		//fe.addargs(model.getLinkedVars("xl1"), model.getParameterVar("p1"),model.getDesignVar("x3"),model.getResponseVars("f1","f3","f4"));
		fe.addArgs(model.getDesignVars("x3"),model.getOutputVars("f1","f3","f4"));
		fe.addArgs(model.getLinkedVar("xl1"));
		fe.addArgs(model.getInvariantVar("p1"));
		
		ServiceEvaluator f1e = evaluator("f1e", "x1^2+x2^2");
		model.setResponseEvaluator("f1", f1e);
		f1e.addArgs(model.getDesignVars("x1","x2"));	
		
		ServiceEvaluator f2e = evaluator("f2e","x1+x2");
		model.setResponseEvaluator("f2", f2e);
		f2e.addArgs(model.getDesignVars("x1","x2"));
		
		ServiceEvaluator f3e = evaluator("f3e", "-5.0*f2");
		model.setResponseEvaluator("f3", f3e);
		f3e.addArg(model.getOutputVar("f2"));
		
		ServiceEvaluator f4e = evaluator("f4e", "2.0*x3^2+x4^2");
		model.setResponseEvaluator("f4", f4e);
		f4e.addArgs( model.getDesignVars("x3","x4"));

		ServiceEvaluator g1e = evaluator("g1e", "f1+f2-2.0*x2+x3^2+x3+x4^2-x4-8.0");
		model.setResponseEvaluator("g1", g1e);
		g1e.addArgs(model.getOutputVars("f1", "f2"), model.getDesignVars("x2","x3","x4"));
		
		ServiceEvaluator g2e = evaluator("g2e", "x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0");
		model.setResponseEvaluator("g2", g2e);
		g2e.addArgs( model.getDesignVars("x1","x2","x3","x4"));
		
		ServiceEvaluator g3e = evaluator("g3e", "2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0");
		model.setResponseEvaluator("g3", g3e);
		g3e.addArgs( model.getDesignVars("x1","x2","x3","x4"));
		
		return model;
	}


	

	
	public static ParametricModel getFileParametricModel()
			throws ParException, EvaluationException,
			ConfigurationException, ContextException, ExertionException,
			SignatureException, IOException {
		
		// ParametricModel
		// design vars: x1, x2, x3, x4
		// response vars: f, g1, g2, g3
		// response f:fe="x1^2-5.0*x1+x2^2-5.0*x2+2.0*x3^2-21.0*x3+x4^2+7.0*x4+50.0"
		// response g1:g1e="x1^2+x1+x2^2-x2+x3^2+x3+x4^2-x4-8.0" 
		// response g2:g2e="x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"
		// response g3:g3e="2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"
		
		// need a  method that looks like this
		// ParametricModel am = parametricModel("Parametric Analysis", responseModelThatIsAlreadyConfigured,ParametricTable(),output()); 
		int designVarCount = 4;
		int responseVarCount = 4;
		ParametricModel pm = parametricModel("Parametric Analysis", 
			designVars("x", designVarCount), 
			outputVars("f"),
			outputVars("g", responseVarCount-1),
			parametricTable(input("../data/rosenSuzuki1in.data"),
			output("../data/rosenSuzuki1out.data", ", ")));

		configureAnalysisModel(pm);

		logger.info("parametric model: " + pm);
		return pm;
	}
	
	public static ParametricModel getUrlParametricModel()
			throws ParException, EvaluationException,
			ConfigurationException, ContextException, ExertionException,
			SignatureException, IOException {
		// ParametricModel
		// design vars: x1, x2, x3, x4
		// response vars: f, g1, g2, g3
		// response f:fe="x1^2-5.0*x1+x2^2-5.0*x2+2.0*x3^2-21.0*x3+x4^2+7.0*x4+50.0"
		// response g1:g1e="x1^2+x1+x2^2-x2+x3^2+x3+x4^2-x4-8.0" 
		// response g2:g2e="x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"
		// response g3:g3e="2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"
		
		// need a  method that looks like this
		// ParametricModel am = parametricModel("Parametric Analysis", responseModelThatIsAlreadyConfigured,ParametricTable(),output()); 
		int designVarCount = 4;
		int responseVarCount = 4;
		ParametricModel pm = parametricModel("Parametric Analysis", 
			designVars("x", designVarCount), 
			outputVars("f"),
			outputVars("g", responseVarCount-1),
			parametricTable(input("http://127.0.0.1:9000/rosenSuzuki1in.data"), 
			output("http://127.0.0.1:9000/rosenSuzuki1out.data")));

		configureAnalysisModel(pm);

		logger.info("parametric model: " + pm);
		return pm;
	}

	public static ResponseModel getSensitivitiesModel() throws ParException,
			RemoteException, EvaluationException, ConfigurationException,
			ContextException, ExertionException, SignatureException {
		
		// Senstivity Analysis Model
		// design vars: x1, x2, x3, x4
		// response vars: f, g1, g2, g3
				
		// response f:fe="x1^2-5.0*x1+x2^2-5.0*x2+2.0*x3^2-21.0*x3+x4^2+7.0*x4+50.0"
			// derivativeResponse dfdx1 = "2.0*x1-5.0"
			// derivativeResponse dfdx2 = "2.0*x2-5.0"
			// derivativeResponse dfdx3 = "4.0*x3-21.0"
			// derivativeResponse dfdx4 = "2.0*x4+7.0"
		// response g1:g1e="x1^2+x1+x2^2-x2+x3^2+x3+x4^2-x4-8.0" 
			// derivativeResponse dg1dx1 = "2.0*x1+1.0"
			// derivativeResponse dg1dx2 = "2.0*x2-1.0"
			// derivativeResponse dg1dx3 = "2.0*x3+1.0"
			// derivativeResponse dg1dx4 = "2.0*x4-1.0"
		// response g2:g2e="x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"
			// derivativeResponse dg2dx1 = "2.0*x1-1.0"
			// derivativeResponse dg2dx2 = "4.0*x2"
			// derivativeResponse dg2dx3 = "2.0*x3"
			// derivativeResponse dG2dx4 = "4.0*x4-1.0"
		// response g3:g3e="2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"
			// derivativeResponse dg3dx1 = "4.0*x1+2.0"
			// derivativeResponse dg3dx2 = "2.0*x2-1.0"
			// derivativeResponse dg3dx3 = "2.0*x3"
			// derivativeResponse dg3dx4 = "-1.0"
		
		int designVarCount = 4;
		int responseVarCount = 4;
		
		ResponseModel sm = responseModel("Sensitivity Analysis", 
			designVars("x", designVarCount), 
			outputVars("f"),
			outputVars("g", responseVarCount-1),
			realization("f", differentiation(wrt("x1", "x2", "x3", "x4"))),
			realization("g1", differentiation(wrt("x1", "x2", "x3", "x4"))),
			realization("g2", differentiation(wrt("x1", "x2", "x3", "x4"))),	
			realization("g3", differentiation(wrt("x1", "x2", "x3", "x4"))));
		
		configureAnalysisModel(sm);
		configureSensitivityModel(sm);

		logger.info("sensitivity model: " + sm);
		return sm;
	}
	
	private static ResponseModel configureSensitivityModel(ResponseModel model) 
		throws RemoteException, ContextException, EvaluationException {
		
		// Senstivity Analysis Model
		// design vars: x1, x2, x3, x4
		// response vars: f, g1, g2, g3
				
		// response f:fe="x1^2-5.0*x1+x2^2-5.0*x2+2.0*x3^2-21.0*x3+x4^2+7.0*x4+50.0"
			// derivativeResponse dfdx1 = "2.0*x1-5.0"
			// derivativeResponse dfdx2 = "2.0*x2-5.0"
			// derivativeResponse dfdx3 = "4.0*x3-21.0"
			// derivativeResponse dfdx4 = "2.0*x4+7.0"
		// response g1:g1e="x1^2+x1+x2^2-x2+x3^2+x3+x4^2-x4-8.0" 
			// derivativeResponse dg1dx1 = "2.0*x1+1.0"
			// derivativeResponse dg1dx2 = "2.0*x2-1.0"
			// derivativeResponse dg1dx3 = "2.0*x3+1.0"
			// derivativeResponse dg1dx4 = "2.0*x4-1.0"
		// response g2:g2e="x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"
			// derivativeResponse dg2dx1 = "2.0*x1-1.0"
			// derivativeResponse dg2dx2 = "4.0*x2"
			// derivativeResponse dg2dx3 = "2.0*x3"
			// derivativeResponse dG2dx4 = "4.0*x4-1.0"
		// response g3:g3e="2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"
			// derivativeResponse dg3dx1 = "4.0*x1+2.0"
			// derivativeResponse dg3dx2 = "2.0*x2-1.0"
			// derivativeResponse dg3dx3 = "2.0*x3"
			// derivativeResponse dg3dx4 = "-1.0"
		
		// Configure the AnalysisModel - configures the design vars, linked vars, and the response vars
		ResponseModel sm = (ResponseModel)configureAnalysisModel(model);
		
		// configure the sensitivity model
		
		// response f:fe="x1^2-5.0*x1+x2^2-5.0*x2+2.0*x3^2-21.0*x3+x4^2+7.0*x4+50.0"
		// derivativeResponse dfdx1:dfedx1 = "2.0*x1-5.0"
		// derivativeResponse dfdx2:dfedx2 = "2.0*x2-5.0"
		// derivativeResponse dfdx3:dfedx3 = "4.0*x3-21.0"
		// derivativeResponse dfdx4:dfedx4 = "2.0*x4+7.0"
		ServiceEvaluator dfedx1e1 = expression("dfedx1e1", "2.0*x1-5.0",args(sm.getDesignVars("x1")));
		ServiceEvaluator dfedx2e1 = expression("dfedx2e1", "2.0*x2-5.0",args(sm.getDesignVars("x2")));
		ServiceEvaluator dfedx3e1 = expression("dfedx3e1", "4.0*x3-21.0",args(sm.getDesignVars("x3")));
		ServiceEvaluator dfedx4e1 = expression("dfedx4e1", "2.0*x4+7.0",args(sm.getDesignVars("x4")));
		List<ServiceEvaluator> feg1 = list(dfedx1e1, dfedx2e1, dfedx3e1, dfedx4e1);	
		sm.setGradientEvaluators("f", "fe", "feg1",  feg1);
		
		// response g1:g1e="x1^2+x1+x2^2-x2+x3^2+x3+x4^2-x4-8.0" 
		// derivativeResponse dg1dx1:dg1edx1 = "2.0*x1+1.0"
		// derivativeResponse dg1dx2:dg1edx2 = "2.0*x2-1.0"
		// derivativeResponse dg1dx3:dg1edx3 = "2.0*x3+1.0"
		// derivativeResponse dg1dx4:dg1edx4 = "2.0*x4-1.0"
		ServiceEvaluator dg1edx1e1 = expression("dg1edx1e1", "2.0*x1+1",args(sm.getDesignVars("x1")));
		ServiceEvaluator dg1edx2e1 = expression("dg1edx2e1", "2.0*x2-1.0",args(sm.getDesignVars("x2")));
		ServiceEvaluator dg1edx3e1 = expression("dg1edx3e1", "2.0*x3+1.0",args(sm.getDesignVars("x3")));
		ServiceEvaluator dg1edx4e1 = expression("dg1edx4e1", "2.0*x4-1.0",args(sm.getDesignVars("x4")));
		List<ServiceEvaluator> g1eg1 = list(dg1edx1e1, dg1edx2e1, dg1edx3e1, dg1edx4e1);	
		sm.setGradientEvaluators("g1", "g1e", "g1eg1",  g1eg1);

		// response g2:g2e="x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"
		// derivativeResponse dg2dx1:dg2edx1 = "2.0*x1-1.0"
		// derivativeResponse dg2dx2:dg2edx2 = "4.0*x2"
		// derivativeResponse dg2dx3:dg2edx3 = "2.0*x3"
		// derivativeResponse dg2dx4:dg2edx4 = "4.0*x4-1.0"
		ServiceEvaluator dg2edx1e1 = expression("dg2edx1e1", "2.0*x1-1",args(sm.getDesignVars("x1")));
		ServiceEvaluator dg2edx2e1 = expression("dg2edx2e1", "4.0*x2",args(sm.getDesignVars("x2")));
		ServiceEvaluator dg2edx3e1 = expression("dg2edx3e1", "2.0*x3",args(sm.getDesignVars("x3")));
		ServiceEvaluator dg2edx4e1 = expression("dg2edx4e1", "4.0*x4-1.0",args(sm.getDesignVars("x4")));
		List<ServiceEvaluator> g2eg1 = list(dg2edx1e1, dg2edx2e1, dg2edx3e1, dg2edx4e1);	
		sm.setGradientEvaluators("g2", "g2e", "g2eg1",  g2eg1);

		
		// response g3:g3e="2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"
		// derivativeResponse dg3dx1:dg3edx1 = "4.0*x1+2.0"
		// derivativeResponse dg3dx2:dg3edx2 = "2.0*x2-1.0"
		// derivativeResponse dg3dx3:dg3edx3 = "2.0*x3"
		// derivativeResponse dg3dx4:dg3edx4 = "-1.0"
		ServiceEvaluator dg3edx1e1 = expression("dg3edx1e1", "4.0*x1+2.0",args(sm.getDesignVars("x1")));
		ServiceEvaluator dg3edx2e1 = expression("dg3edx2e1", "2.0*x2-1.0",args(sm.getDesignVars("x2")));
		ServiceEvaluator dg3edx3e1 = expression("dg3edx3e1", "2.0*x3",args(sm.getDesignVars("x3")));
		ServiceEvaluator dg3edx4e1 = evaluator("dg3edx4e1", "-1.0");
		List<ServiceEvaluator> g3eg1 = list(dg3edx1e1, dg3edx2e1, dg3edx3e1, dg3edx4e1);	
		sm.setGradientEvaluators("g3", "g3e", "g3eg1",  g3eg1);
		
		return sm;
	}
	
	public static OptimizationModel getOptimizationModel() throws ParException,
			EvaluationException, ConfigurationException, ContextException,
			ExertionException, SignatureException, RemoteException {
		// Optimization Model
		// design vars: x1, x2, x3, x4
		// response vars: f, g1, g2, g3
		// response
		// f:fe="x1^2-5.0*x1+x2^2-5.0*x2+2.0*x3^2-21.0*x3+x4^2+7.0*x4+50.0"
		// response g1:g1e="x1^2+x1+x2^2-x2+x3^2+x3+x4^2-x4-8.0"
		// response g2:g2e="x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"
		// response g3:g3e="2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"

		// need a method that looks like this
		// ParametricModel am = parametricModel("Parametric Analysis",
		// responseModelThatIsAlreadyConfigured,ParametricTable(),output());
		int designVarCount = 4;
		int responseVarCount = 4;
		OptimizationModel om = optimizationModel("DesignExploration Model", 
				//designVars("x", designVarCount), 
				designVars(names(loop(designVarCount), "x")), 
				outputVars("f"), 
				outputVars("g",responseVarCount - 1),
				objectiveVars(var("fo", "f", Target.min )),
				constraintVars(var("g1c", "g1", Relation.gt, 50.0), 
							   var("g2c", "g2", Relation.lt, 0.0), 
							   var("g3c","g3", Relation.lt, 0.0)));

		configureAnalysisModel(om);
		configureSensitivityModel(om);
		om.initialize();
		
		logger.info("optimization model: " + om);
		return om;
	}
}
