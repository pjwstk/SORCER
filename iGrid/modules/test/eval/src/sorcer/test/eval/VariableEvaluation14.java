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

import static sorcer.co.operator.list;
import static sorcer.co.operator.names;
import static sorcer.co.operator.duo;
import static sorcer.co.operator.values;
import static sorcer.vo.operator.args;
import static sorcer.vo.operator.outputVars;
import static sorcer.vo.operator.designVars;
import static sorcer.vo.operator.differentiation;
import static sorcer.vo.operator.evaluation;
import static sorcer.vo.operator.evaluator;
import static sorcer.vo.operator.expression;
import static sorcer.vo.operator.fdEvaluator;
import static sorcer.vo.operator.gradient;
import static sorcer.vo.operator.input;
import static sorcer.vo.operator.linkedVars;
import static sorcer.vo.operator.output;
import static sorcer.vo.operator.invariantVars;
import static sorcer.vo.operator.parametricModel;
import static sorcer.vo.operator.parametricTable;
import static sorcer.vo.operator.realization;
import static sorcer.vo.operator.responseModel;
import static sorcer.vo.operator.var;
import static sorcer.vo.operator.wrt;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Logger;

import net.jini.config.ConfigurationException;
import sorcer.core.context.model.var.ParametricModel;
import sorcer.core.context.model.var.ResponseModel;
import sorcer.core.context.model.var.VarModel;
import sorcer.service.ContextException;
import sorcer.service.EvaluationException;
import sorcer.service.ExertionException;
import sorcer.service.SignatureException;
import sorcer.util.Log;
import sorcer.vfe.ServiceEvaluator;
import sorcer.vfe.Var;
import sorcer.vfe.VarException;
import sorcer.vfe.evaluator.ExpressionEvaluator;
import sorcer.vfe.evaluator.FiniteDifferenceEvaluator;
import sorcer.vfe.evaluator.JepEvaluator;

/**
 * Example on how to create a ContextModel for numerical analysis
 */
@SuppressWarnings("unchecked")
public class VariableEvaluation14 {

	private static Logger logger = Log.getTestLog();

	public static void main(String[] args) throws Exception {
		int test = new Integer(args[0]);
		switch (test) {
			case 1: doResponseAnalysis(); break;
			case 2: doParametricAnalysis(); break;
			case 3: doFileParametricAnalysis(); break;
			case 4: doUrlParametricAnalysis(); break;
			case 5: doSensitivitiesAnalysis(); break;
		}
	}
	
	public static void doResponseAnalysis() throws ParException, RemoteException,
	EvaluationException, ConfigurationException, ContextException, ExertionException, SignatureException {
		// test ResponseModel
		ResponseModel rm = getResponseModel();
	
		rm.setInvarinatVarValues(duo("p1", 5));
		
		rm.setDesignVarValues(duo("x2", 2), duo("x3", 3), duo("x4", 4), duo("x5", 5));
		logger.info("\n\nresponse model: " + rm);

		Var y1 = rm.getOutputVar("y1");
		
		y1.selectEvaluator("y1e1");
		ExpressionEvaluator ee = (ExpressionEvaluator)y1.getEvaluator();
		logger.info("\n\nexpression:\n" + ee.getName() + "/" + ee.getExpression());
		logger.info("\n\nresponse:\n" + ee.getName() + "/" + y1.getValue());
		logger.info("\n\nresponses: " + rm.getResponses());
		
		y1.selectEvaluator("y1e2");
		ee = (ExpressionEvaluator)y1.getEvaluator();
		logger.info("\n\nexpression:\n" + ee.getName() + "/" + ee.getExpression());
		logger.info("\n\nresponse:\n" + ee.getName() + "/" + y1.getValue());
		logger.info("\n\nresponses: " + rm.getResponses());
		logger.info("\n\nresponses: " + rm.getResponses("y3", "y4"));
	}

	public static void doParametricAnalysis() throws ParException, EvaluationException, 
	ConfigurationException, ContextException, ExertionException, SignatureException, IOException {
		// test ParametricModel
		ParametricModel am = getParametricModel();
		Var y1 = am.getOutputVar("y1");
		ExpressionEvaluator ee = (ExpressionEvaluator)y1.getEvaluator();
		logger.info("\n\nexpression:\n" + ee.getName() + "/" + ee.getExpression());
		y1.selectEvaluator("y1e1");
		am.evaluate();
		am.writeOutputTableToFile();
		logger.info("\n\nParametricAnalysis input table: " + am.getParametricTable());
		logger.info("\n\nParametricAnalysis parametric table: " + am.getOutTable());
		
		y1.selectEvaluator("y1e2");
		ee = (ExpressionEvaluator)y1.getEvaluator();
		logger.info("\n\nexpression:\n" + ee.getName() + "/" + ee.getExpression());
		am.evaluate();
		logger.info("\n\nParametricAnalysis input table: " + am.getParametricTable());
		logger.info("\n\nParametricAnalysis parametric table: " + am.getOutTable());
	}
	
	public static void doFileParametricAnalysis() throws 
	ParException, EvaluationException, ConfigurationException, ContextException, 
	ExertionException, SignatureException, IOException {
		// test ParametricModel
		ParametricModel am = getFileParametricModel();
		Var y1 = am.getOutputVar("y1");
		ExpressionEvaluator ee = (ExpressionEvaluator)y1.getEvaluator();
		logger.info("\n\nexpression:\n" + ee.getName() + "/" + ee.getExpression());
		y1.selectEvaluator("y1e1");
		am.evaluate();
		am.writeOutputTableToFile();
		logger.info("\n\nParametricAnalysis input table: " + am.getParametricTable());
		logger.info("\n\nParametricAnalysis parametric table: " + am.getOutTable());
		
		y1.selectEvaluator("y1e2");
		ee = (ExpressionEvaluator)y1.getEvaluator();
		logger.info("\n\nexpression:\n" + ee.getName() + "/" + ee.getExpression());
		am.evaluate();
		am.writeOutputTableToFile("y1e2.table");
		logger.info("\n\nParametricAnalysis input table: " + am.getParametricTable());
		logger.info("\n\nParametricAnalysis parametric table: " + am.getOutTable());
	}
	
	public static void doUrlParametricAnalysis() throws 
	ParException, EvaluationException, ConfigurationException, ContextException, 
	ExertionException, SignatureException, IOException {
		// test ParametricModel
		ParametricModel am = getUrlParametricModel();
		Var y1 = am.getOutputVar("y1");
		ExpressionEvaluator ee = (ExpressionEvaluator)y1.getEvaluator();
		logger.info("\n\nexpression:\n" + ee.getName() + "/" + ee.getExpression());
		y1.selectEvaluator("y1e1");
		am.evaluate();
		am.writeOutputTableToURL();
		//logger.info("\n\nParametricAnalysis input table: " + am.getInTable());
		//logger.info("\n\nParametricAnalysis parametric table: " + am.getOutTable());
		
		//y1.setEvaluation("y1e2");
		//am.writeOutputTableToUrl("http://127.0.0.1/data/y1e2.table");
		//ee = (ExpressionEvaluator)y1.getEvaluator();
		//logger.info("\n\nexpression:\n" + ee.getName() + "/" + ee.getExpression());
		//am.evaluate();
		//am.writeOutputTableToUrl();
		//logger.info("\n\nParametricAnalysis input table: " + am.getInTable());
		//logger.info("\n\nParametricAnalysis parametric table: " + am.getOutTable());
	}
	
	public static void doSensitivitiesAnalysis() throws ParException, RemoteException,
	EvaluationException, ConfigurationException, ContextException, ExertionException, SignatureException {
		
		ResponseModel sm = getSensitivitiesModel();
		
		sm.setInvarinatVarValues(duo("p1", 5));
		sm.setDesignVarValues(duo("x2", 2), duo("x3", 3), duo("x4", 4), duo("x5", 5));
			
		Var y1 = sm.getOutputVar("y1");
		y1.selectEvaluator("y1e1");
		ServiceEvaluator y1e1 = (ExpressionEvaluator)y1.getEvaluator();
		logger.info("\ny1e:\n" + y1e1);
		logger.info("\ny1 value:\n" + y1.getValue());
		//logger.info("\n\nderivative table y1e1: " + y1e1.getDerivativeEvaluator());
		logger.info("----------------- partial derivative y1 wrt x2|y1e1|y1e1g1: " + sm.getPartialDerivative("y1", "x2", "y1e1g1"));
		logger.info("----------------- partial derivative y1 wrt x2|y1e1|y1e1g2: " + y1.getPartialDerivative("x2", "y1e1g2"));	

//		logger.info("+++++++++++++++++ total derivative y1 wrt x2|y1e1|y1e1g1: " + sm.getTotalDerivative("y1", "x2", "y1e1g1"));
//		logger.info("+++++++++++++++++ total derivative y1 wrt x2|y1e1|y1e1g2: " + y1.getTotalDerivative("x2", "y1e1g2"));
		
		logger.info("*-*-*-*-*-*-*-*-* partial gradient y1 for y1e1|y1e1g1: " + sm.getPartialDerivativeTable("y1", "y1e1g1"));
		logger.info("*-*-*-*-*-*-*-*-* partial gradient y1 for y1e1|y1e1g2: " + y1.getPartialDerivativeTable("y1e1g2"));
		
		logger.info("*+*+*+*+*+*+*+*+* total gradient y1 for y1e1|y1e1g1: " + sm.getTotalDerivativeTable("y1", "y1e1g1"));
		logger.info("*+*+*+*+*+*+*+*+* total gradient y1 for y1e1|y1e1g2: " + y1.getTotalDerivativeTable("y1e1g2"));
						
		y1.selectEvaluator("y1e2");
		ServiceEvaluator y1e2 = (ExpressionEvaluator)y1.getEvaluator();
		logger.info("\ny1e2:\n" + y1e2);
		logger.info("\ny1 value:\n" + y1.getValue());
		//logger.info("\n\nderivative table y1e2: " + y1e2.getDerivativeEvaluator());
		
		logger.info("----------------- partial derivative y1 wrt x2|y1e2|y1e2g1: " + sm.getPartialDerivative("y1", "x2", "y1e2g1"));
		logger.info("----------------- partial derivative y1 wrt x2|y1e2|y1e2g2: " + y1.getPartialDerivative("x2", "y1e2g2"));

//		logger.info("+++++++++++++++++ total derivative y1 wrt x2|y1e2|y1e2g1: " + sm.getTotalDerivative("y1", "x2", "y1e2g1"));
//		logger.info("+++++++++++++++++ total derivative y1 wrt x2|y1e2|y1e2g2: " + y1.getTotalDerivative("x2", "y1e2g2"));
		
		logger.info("*-*-*-*-*-*-*-*-* partial gradient y1 for y1e2|y1e2g1: " + sm.getPartialDerivativeTable("y1", "y1e2g1"));
		logger.info("*-*-*-*-*-*-*-*-* partial gradient y1 for y1e2|y1e2g2: " + y1.getPartialDerivativeTable( "y1e2g2"));
		
		logger.info("*+*+*+*+*+*+*+*+* total gradient y1 for y1e2|y1e2g1: " + sm.getTotalDerivativeTable("y1", "y1e2g1"));
		logger.info("*+*+*+*+*+*+*+*+* total gradient y1 for y1e2|y1e2g2: " + y1.getTotalDerivativeTable("y1e2g2"));
		
		logger.info("----------------- partial fof gradient for y1e2|y1e2g2: " + sm.getPartialDerivativeTables());
		logger.info("*+*+*+*+*+*+*+*+* total fof gradient for y1e2|y1e2g2: " + sm.getTotalDerivativeTables());
	}
	
	public static ResponseModel getResponseModel() throws ParException,
			RemoteException, EvaluationException, ConfigurationException,
			ContextException, ExertionException, SignatureException {

		// ResponseModel
		// parameter vars: p1 = 5
		// design vars: x2, x3, x4, x5
		// linked vars: xl1
		// response vars: y1, y2, y3, y4, y5
		// response y1/y1e1 ="xl1 + 2*x2^2 + 3*x3^2 + 5*x4 + 4*x2^3"
		// linked var xl1: "3 * x3"
		// response y2="x2^2", dy2dx2: "2*x2"
		// response y3="2*y2" dy3dy2: "2"
		// response y4="xl1 + 3*x3^2", dy4dxl1: "1", dy4dx3: "6*x3"
		// response y5="p1*x4", dy5dx4: p1
		// response y1/y1e2 = "y3 + y4 + y5 + 4*x2^3", dy6dy3="1", dy6dy4="1", dy6dy5="1", dy6dx2="12*x2^2"
		
		int designVarCount = 4;
		int responseVarCount = 5;
		int linkedVarCount = 1;
		ResponseModel am = responseModel("Response Analysis", 
				parameterVars("p1"), 
				designVars("x", designVarCount, 2), 
				linkedVars("xl", linkedVarCount), 
				outputVars("y", responseVarCount),
				realization("y1", evaluation("y1e1"), evaluation("y1e2")));
				
		configureAnalysisModel(am);
		logger.info("literal context: " + am);
		
		return am;
	}
	
	private static VarModel configureAnalysisModel(VarModel model)
			throws ContextException, EvaluationException, RemoteException {
		// setup evaluators and filters for model vars

		// ResponseModel
		// parameter vars: p1 = 5
		// design vars: x2, x3, x4, x5
		// linked vars: xl1
		// response vars: y1, y2, y3, y4, y5
		// response y1/y1e1 ="xl1 + 2*x2^2 + 3*x3^2 + 5*x4 + 4*x2^3"
		// linked var xl1: "3 * x3"
		// response y2="x2^2", dy2dx2: "2*x2"
		// response y3="2*y2" dy3dy2: "2"
		// response y4="xl1 + 3*x3^2", dy4dxl1: "1", dy4dx3: "6*x3"
		// response y5="5*x4", dy5dx4: "5"
		// response y1/y1e2 = "y3 + y4 + y5 + 4*x2^3", dy6dy3="1", dy6dy4="1", dy6dy5="1", dy6dx2="12*x2^2"
		
		// configure linked vars
		ServiceEvaluator xl1e = new JepEvaluator("xl1e", "3*x3");
		xl1e.addArgs(designVars(model, "x3"));
		model.setLinkedEvaluator("xl1", xl1e);
		
		// configure response vars
//		Evaluator y1e1 = expression("y1e1", "xl1 + 2*x2^2 + 3*x3^2 + 5*x4 +4*x2^3");
//		model.setResponseEvaluator("y1", y1e1);
//		y1e1.addDependents(model.getLinkedVars("xl1"), model.getDesignVars("x2", "x3", "x4"));
		
		var(model, "y1", "y1e1", expression("xl1 + 2*x2^2 + 3*x3^2 + 5*x4 +4*x2^3", 
				args(linkedVars(model, "xl1"), designVars(model, "x2", "x3", "x4"))));
		
		ServiceEvaluator y2e = evaluator("y2e", "x2^2");
		model.setResponseEvaluator("y2", y2e);
		y2e.addArg(model.getDesignVar("x2"));	
		
		ServiceEvaluator y3e = evaluator("y3e","2*y2");
		model.setResponseEvaluator("y3", y3e);
		y3e.addArgs(outputVars(model, "y2"));
		
		ServiceEvaluator y4e = evaluator("y4e", "xl1 + 3*x3^2");
		model.setResponseEvaluator("y4", y4e);
		y4e.addArgs(linkedVars(model, "xl1"), designVars(model, "x3"));
		
		ServiceEvaluator y5e = evaluator("y5e", "p1*x4");
		model.setResponseEvaluator("y5", y5e);
		y5e.addArgs(parameterVars(model, "p1"), designVars(model, "x4"));
		
//		Evaluator y1e2 = expression("y1e2", "y3 + y4 + y5 + 4*x2^3");
//		model.setResponseEvaluator("y1", y1e2);
//		y1e2.addDependents(model.getResponseVars("y3", "y4", "y5"), model.getDesignVars("x2"));
	
		ServiceEvaluator y1e2 = evaluator("y1e2", "y3 + y4 + y5 + 4*x2^3");
		var(model, "y1", y1e2, list("y3", "y4", "y5", "x2"));
		
//		Evaluator y1e2 = expression("y1e2", "y3 + y4 + y5 + 4*x2^3");
//		composition(model, "y1", y1e2, 
//				dependents(list("y3", "y4", "y5", "x2")));
				       
		return model;
	}
	
	public static ParametricModel getParametricModel() throws ParException,
			EvaluationException, ConfigurationException,
			ContextException, ExertionException, SignatureException, IOException {
		// ParametricModel
		// parameter vars: p1 = 5
		// design vars: x2, x3, x4, x5
		// linked vars: xl1
		// response vars: y1, y2, y3, y4, y5
		// response y1/y1e1 ="xl1 + 2*x2^2 + 3*x3^2 + 5*x4 + 4*x2^3"
		// linked var xl1: "3 * x3"
		// response y2="x2^2", dy2dx2: "2*x2"
		// response y3="2*y2" dy3dy2: "2"
		// response y4="xl1 + 3*x3^2", dy4dx1: "1", dy4dx3: "6*x3"
		// response y5="5*x4", dy5dx4: "5"
		// response y1/y1e2 = "y3 + y4 + y5 + 4*x2^3", dy6dy3="1", dy6dy4="1", dy6dy5="1", dy6dx2="12*x2^2"
		
		int designVarCount = 4;
		int responseVarCount = 5;
		int linkedVarCount = 1;
		ParametricModel am = parametricModel("Response Analysis", 
				parameterVars("p1"), 
				designVars("x", designVarCount, 2), 
				linkedVars("xl", linkedVarCount), 
				outputVars("y", responseVarCount),
				realization("y1", evaluation("y1e1"), evaluation("y1e2")),
				parametricTable(
					names("x2","x3","x4", "x5"), 
					values(1.1, 2.2, 3.3, 4.4),
					values(2.2, 4.2, 6.6, 8.8),
					values(4.4, 8.8, 12.1, 16.1),
					output("../data/out.data", ", ")));
				
		configureAnalysisModel(am);
		
		logger.info("literal context: " + am);
		return am;
	}
	
	public static ParametricModel getFileParametricModel()
			throws ParException, EvaluationException,
			ConfigurationException, ContextException, ExertionException,
			SignatureException, IOException {
		// ParametricModel
		// parameter vars: p1 = 5
		// design vars: x2, x3, x4, x5
		// linked vars: xl1
		// response vars: y1, y2, y3, y4, y5
		// response y1/y1e1 ="xl1 + 2*x2^2 + 3*x3^2 + 5*x4 + 4*x2^3"
		// linked var xl1: "3 * x3"
		// response y2="x2^2", dy2dx2: "2*x2"
		// response y3="2*y2" dy3dy2: "2"
		// response y4="xl1 + 3*x3^2", dy4dxl1: "1", dy4dx3: "6*x3"
		// response y5="5*x4", dy5dx4: "5"
		// response y1/y1e2 = "y3 + y4 + y5 + 4*x2^3", dy6dy3="1", dy6dy4="1",
		// dy6dy5="1", dy6dx2="12*x2^2"

		int designVarCount = 4;
		int responseVarCount = 5;
		int linkedVarCount = 1;
		ParametricModel am = parametricModel("Response Analysis", 
				parameterVars("p1"), 
				designVars("x", designVarCount, 2), 
				linkedVars("xl", linkedVarCount),
				outputVars("y", responseVarCount), 
				realization("y1", evaluation("y1e1"), evaluation("y1e2")), 
				parametricTable(input("../data/in.data"), output("../data/out.data")));

		configureAnalysisModel(am);

		logger.info("literal context: " + am);
		return am;
	}
	
	public static ParametricModel getUrlParametricModel()
			throws ParException, EvaluationException,
			ConfigurationException, ContextException, ExertionException,
			SignatureException, IOException {
		
		// ParametricModel
		// parameter vars: p1 = 5
		// design vars: x2, x3, x4, x5
		// linked vars: xl1
		// response vars: y1, y2, y3, y4, y5
		// response y1/y1e1 ="xl1 + 2*x2^2 + 3*x3^2 + 5*x4 + 4*x2^3"
		// linked var xl1: "3 * x3"
		// response y2="x2^2", dy2dx2: "2*x2"
		// response y3="2*y2" dy3dy2: "2"
		// response y4="xl1 + 3*x3^2", dy4dxl1: "1", dy4dx3: "6*x3"
		// response y5="5*x4", dy5dx4: "5"
		// response y1/y1e2 = "y3 + y4 + y5 + 4*x2^3", dy6dy3="1", dy6dy4="1",
		// dy6dy5="1", dy6dx2="12*x2^2"

		int designVarCount = 4;
		int responseVarCount = 5;
		int linkedVarCount = 1;
		ParametricModel am = parametricModel("Response Analysis", 
				parameterVars("p1"), 
				designVars("x", designVarCount, 2), 
				linkedVars("xl", linkedVarCount),
				outputVars("y", responseVarCount), 
				realization("y1", evaluation("y1e1"), evaluation("y1e2")),
				parametricTable(input("http://172.17.199.129:50000/in.data"), output("http://172.17.199.129:50000/out.data")));

		configureAnalysisModel(am);

		logger.info("literal context: " + am);
		return am;
	}

	public static ResponseModel getSensitivitiesModel() throws ParException,
			RemoteException, EvaluationException, ConfigurationException,
			ContextException, ExertionException, SignatureException {

		// Analysis Model
		// parameter vars: p1 = 5
		// design vars: x2, x3, x4, x5
		// linked vars: xl1 (x3)
		// response vars: y1 (xl1, x2, x3, x4) (y3, y4, y5, x2), y2 (x2), y3 (y2), y4 (x1, x3), y5 (x4)
		// response' y1/y1e1="xl1 + 2*x2^2 + 3*x3^2 + 5*x4 + 4*x2^3" 
		// response'' y1/y1e2: "y3 + y4 + y5 + 4*x2^3", dy6dy3="1", dy6dy4="1", dy6dy5="1", dy6dx2="12*x2^2"
		// linked var xl1: "3 * x3" dxl1dx3: 3 
		// response y2="x2^2", dy2dx2: "2*x2"
		// response y3="2*y2" dy3dy2: "2"
		// response y4="x1 + 3*x3^2", dy4dxl1: "1", dy4dx3: "6*x3"
		// response y5="5*x4", dy5dx4: "5"
		
		int designVarCount = 4;
		int responseVarCount = 5;
		int linkedVarCount = 1;
		
		ResponseModel sm = responseModel("Sensitivity Analysis", 
			parameterVars("p1"), 
			designVars("x", designVarCount, 2), 
			linkedVars("xl", linkedVarCount), 
			outputVars("y", responseVarCount),
			realization("y1", evaluation("y1e1"), evaluation("y1e2"), 
				differentiation("y1e1", wrt("xl1", "x2", "x3", "x4"), gradient("y1e1g1"), gradient("y1e1g2")),
				differentiation("y1e2", wrt("y3", "y4", "y5", "x2"), gradient("y1e2g1"), gradient("y1e2g2"))),
			realization("y2", evaluation("y2e"), differentiation("y2e", wrt("x2"))),
			realization("y3", evaluation("y3e"), differentiation("y3e", wrt("y2"))),	
			realization("y4", evaluation("y4e"), differentiation("y4e", wrt("xl1", "x3"))),
			realization("y5", evaluation("y5e"), differentiation("y5e", wrt("x4"))),
			realization("xl1", evaluation("xl1e"), differentiation("xl1e", wrt("x3"))));
		
		configureAnalysisModel(sm);
		configureSensitivityModel(sm);

		logger.info("sensitivity model: " + sm);
		return sm;
	}
	
	private static ResponseModel configureSensitivityModel(ResponseModel model) 
		throws RemoteException, ContextException, EvaluationException {
		
		// Analysis Model
		// parameter vars: p1 = 5
		// design vars: x2, x3, x4, x5
		// linked vars: xl1 (x3), "dxl1dx3"="3.0"
		// response vars: y1, y2 (x2), y3 (y2), y4 (xl1, x3), y5 (x4)
		// response'  y1:y1e1="xl1 + 2*x2^2 + 3*x3^2 + 5*x4 + 4*x2^3" (xl1, x2, x3, x4)
		// response'' y1:y1e2: "y3 + y4 + y5 + 4*x2^3" (y3, y4, y5, x2)
		// linked var xl1: "3 * x3" dxl1dx3: 3 
		// response y2="x2^2", dy2dx2: "2*x2"
		// response y3="2*y2" dy3dy2: "2"
		// response y4="x1 + 3*x3^2", dy4dxl1: "1", dy4dx3: "6*x3"
		// response y5="5*x4", dy5dx4: "5"
		
		// Configure the AnalysisModel - configures the design vars, linked vars, and the response vars
		ResponseModel sm = (ResponseModel)configureAnalysisModel(model);
		
		// y1': y1:y1e1="xl1 + 2*x2^2 + 3*x3^2 + 5*x4 + 4*x2^3", dy1dx1="1", dy1dx2=4.0*x2+12.x2^2", dy1dx3="6.0*x3", dy1dx4="5.0"
		// gradient y1e1:y1e1g1 wrt("xl1", "x2", "x3", "x4")
		ServiceEvaluator dy1e1dxl1e1 = evaluator("dy1e1dxl1e1", "1.0");
		ServiceEvaluator dy1e1dx2e1 = expression("dy1e1dx2e1", "4.0*x2+12*x2^2", 
				args(sm.getDesignVars("x2")));
		ServiceEvaluator dy1e1dx3e1 = expression("dy1e1dx3e1", "6.0*x3", 
				args(sm.getDesignVars("x3")));
		ServiceEvaluator dy1e1dx4e1 = new JepEvaluator("dy1e1dx4e1", "5.0");
		List<ServiceEvaluator> y1e1g1 = list(dy1e1dxl1e1, dy1e1dx2e1, dy1e1dx3e1, dy1e1dx4e1);	
		sm.setGradientEvaluators("y1", "y1e1", "y1e1g1",  y1e1g1);
		
		// gradient y1e1/y1e1g2 wrt("xl1", "x2", "x3", "x4")
		ServiceEvaluator dy1e1dxl1e2 = fdEvaluator("dy1e1dxl1e2", sm
				.getOutputVar("y1").getEvaluator("y1e1"), "xl1");
		ServiceEvaluator vdy1e1dx2e2 = fdEvaluator("vdy1e1dx2e2", sm
				.getOutputVar("y1").getEvaluator("y1e1"), "x2");
		ServiceEvaluator dy1e1dx3e2 = fdEvaluator("dy1e1dx3e2", sm
				.getOutputVar("y1").getEvaluator("y1e1"), "x3");
		ServiceEvaluator dy1e1dx4e2 = fdEvaluator("dy1e1dx4e2", sm
				.getOutputVar("y1").getEvaluator("y1e1"), "x4");
		List<ServiceEvaluator> y1e1g2 = list(dy1e1dxl1e2, vdy1e1dx2e2, dy1e1dx3e2, dy1e1dx4e2);
		sm.setGradientEvaluators("y1", "y1e1", "y1e1g2", y1e1g2);

		// y1'': y1:y1e2: "y3 + y4 + y5 + 4*x2^3", dy1dy3="1", dy6dy4="1", dy1dy5="1", dy1dx2="12*x2^2"
		// gradient y1e2:y1e1g1 wrt("y3", "y4", "y5", "x2")
		ServiceEvaluator dy1e2dy3e1 = evaluator("dy1e2dy3e1", "1.0");
		ServiceEvaluator dy1e2dy4e1 = evaluator("dy1e2dy4e1", "1.0");
		ServiceEvaluator dy1e2dy5e1 = evaluator("dy1e2dy5e1", "1.0");
		ServiceEvaluator dy1e2dx2e1 = expression("dy1e2dx2e1", "12.*x2^2",
				args(sm.getDesignVars("x2")));
		List<ServiceEvaluator> y1e2g1 = list(dy1e2dy3e1, dy1e2dy4e1, dy1e2dy5e1, dy1e2dx2e1);
		sm.setGradientEvaluators("y1", "y1e2", "y1e2g1", y1e2g1);

		// gradient y1e2:y1e1g2 wrt(""y3", "y4", "y5", "x2")
		ServiceEvaluator dy1e2dy3e2 = new FiniteDifferenceEvaluator("dy1e2dy3e2", sm
				.getOutputVar("y1").getEvaluator("y1e2"), "y3");
		ServiceEvaluator dy1e2dy4e2 = new FiniteDifferenceEvaluator("dy1e2dy4e2", sm
				.getOutputVar("y1").getEvaluator("y1e2"), "y4");
		ServiceEvaluator dy1e2dy5e2 = new FiniteDifferenceEvaluator("dy1e2dy5e2", sm
				.getOutputVar("y1").getEvaluator("y1e2"), "y5");
		ServiceEvaluator dy1e2dx2e2 = new FiniteDifferenceEvaluator("dy1e2dx2e2", sm
				.getOutputVar("y1").getEvaluator("y1e2"), "x2");
		List<ServiceEvaluator> y1e2g2 = list(dy1e2dy3e2, dy1e2dy4e2, dy1e2dy5e2, dy1e2dx2e2);
		sm.setGradientEvaluators("y1", "y1e2", "y1e2g2", y1e2g2);

		// gradient xl1e1:xl1eg wrt("x3")	
		ServiceEvaluator dxl1dx3 = new JepEvaluator("dxl1dx3", "3.0");
		List<ServiceEvaluator> xl1e = list(dxl1dx3);
		sm.setGradientEvaluators("xl1", "xl1e", "xl1e",  xl1e);
		
		// gradient y2e:y2eg wrt("x2")	
		ServiceEvaluator dy2dx2e = new JepEvaluator("dy2dx2e", "2.*x2");
		dy2dx2e.addArg(sm.getDesignVar("x2"));
		List<ServiceEvaluator> y2e = list(dy2dx2e);
		sm.setGradientEvaluators("y2", "y2e", "y2e",  y2e);
		
		// gradient y3e:y3eg wrt("y2")	
		ServiceEvaluator dy3dy2e = new JepEvaluator("dy3dy2e", "2.0");
		List<ServiceEvaluator> y3e = list(dy3dy2e);
		sm.setGradientEvaluators("y3", "y3e", "y3e",  y3e);

		// gradient y4e:y4eg wrt("x1", "x3")	
		ServiceEvaluator dy4dxl1e = new JepEvaluator("dy4dxl1e", "1.0");
		ServiceEvaluator dy4dx3e = new JepEvaluator("dy4dxl1e", "6.0*x3");
		dy4dx3e.addArg(sm.getDesignVar("x3"));
		List<ServiceEvaluator> y4e1 = list(dy4dxl1e, dy4dx3e);
		sm.setGradientEvaluators("y4", "y4e", "y4e",  y4e1);

		// gradient y5e:y5eg wrt("x4")	
		ServiceEvaluator dy5dx4e = new JepEvaluator("dy5dx4e", "5.0");
		List<ServiceEvaluator> y5e = list(dy5dx4e);
		sm.setGradientEvaluators("y5", "y5e", "y5e",  y5e);

		Var y3 = sm.getOutputVar("y3");
		
		return sm;
	}
}
