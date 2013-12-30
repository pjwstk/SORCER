/*
 * Copyright 2010 the original author or authors.
 * Copyright 2010 SorcerSoft.org.
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

import java.io.IOException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.logging.Logger;

import engineering.core.design.CadDescription;
import engineering.core.design.CaeConstants;
import engineering.core.design.RealDesignVariable;
import engineering.core.reliability.GaussianDistribution;

import sorcer.core.context.ArrayContext;
import sorcer.core.context.ServiceContext;
import sorcer.core.context.node.ContextNode;
import sorcer.core.context.node.ContextNodeException;
import sorcer.core.context.tuple.eval.AspectVariable;
import sorcer.core.context.tuple.eval.BasicVariable;
import sorcer.core.context.tuple.eval.Evaluator;
import sorcer.core.context.tuple.eval.Expression;
import sorcer.core.context.tuple.eval.FieldEvaluator;
import sorcer.core.context.tuple.eval.JEPEvaluation;
import sorcer.core.context.tuple.eval.JepEvaluator;
import sorcer.core.context.tuple.eval.MethodEvaluator;
import sorcer.core.context.tuple.eval.Variable;
import sorcer.core.context.tuple.eval.VariableException;
import sorcer.core.context.tuple.eval.VariableSetEvaluator;
import sorcer.core.context.tuple.eval.Evaluator.Type;
import sorcer.core.exertion.NetJob;
import sorcer.core.exertion.NetTask;
import sorcer.core.provider.eval.ServiceEvaluationTask;
import sorcer.core.signature.NetSignature;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.Evaluation;
import sorcer.service.EvaluationException;
import sorcer.service.Exertion;
import sorcer.service.SignatureException;
import sorcer.service.Task;
import sorcer.util.Sorcer;
import sorcer.util.Log;

/**
 * Example how use the Variables and Evaluators classes
 */
public class VariableEvaluation {

	private static Logger logger = Log.getTestLog();

	/**
	 * @param args
	 * @throws ParException
	 */
	public static void main(String[] args) throws Exception {
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());
		logger.info("provider.lookup.accessor: "
				+ Sorcer.getProperty("provider.lookup.accessor"));
		
		// logger.info("provider.lookup.accessor: "
		// + Env.getProperty("provider.lookup.accessor"));
		 //VariableEvaluation.test1();
		// VariableEvaluation.test2();
		// VariableEvaluation.test3();
		// VariableEvaluation.test4();
		// VariableEvaluation.test5();
		// VariableEvaluation.test6();
		// VariableEvaluation.test7();
		// VariableEvaluation.test8();
		// VariableEvaluation.test9();
		// VariableEvaluation.test10();
		// VariableEvaluation.test11();
		// VariableEvaluation.test12();
		// VariableEvaluation.test13();
		//VariableEvaluation.test14();
		// VariableEvaluation.test15();
		//VariableEvaluation.test16();
		 //VariableEvaluation.test17();
		 VariableEvaluation.test18();
	}

	public static void test1() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\ttest1 Using Basic Variables and JEP Evaluator");

		// expression e1 = x1 + x2

		Variable e1 = new BasicVariable("e1", new JepEvaluator("x1 + x2"));
		logger.info("Variable e1: " + e1);
		Evaluator evalr_e1 = (Evaluator) e1.getEvaluator();
		logger.info("evalr_e1 = " + evalr_e1.describe());
		evalr_e1.addArg(new BasicVariable("x1", 1));
		evalr_e1.addArg(new BasicVariable("x2", 2));
		logger.info("setup evalr_e1 = " + evalr_e1.describe());

		logger.info("first: e1 value = " + e1.getValue());
		logger.info("second: e1 value = " + e1.getValue());

	}

	public static void test2() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest2 Combining Multiple Expressions");

		// expression x1 = y1 + y2
		Variable x1 = new BasicVariable("x1", new JepEvaluator("y1 + y2"));
		logger.info("Variable x1: " + x1);
		Evaluator evalr_x1 = (Evaluator) x1.getEvaluator();
		logger.info("evalr_x1 = " + evalr_x1.describe());
		Variable y1 = new BasicVariable("y1", 11.0);
		evalr_x1.addArg(y1);
		evalr_x1.addArg(new BasicVariable("y2", 12.0));
		logger.info("setup evalr_x1 = " + evalr_x1.describe());
		logger.info(">>>>>>>>>>>>>>> x1 value = " + x1.getValue());

		// expression x2 = z1 + z2
		Variable x2 = new BasicVariable("x2", new JepEvaluator("z1 + z2"));
		logger.info("Variable x2: " + x2);
		Evaluator evalr_x2 = (Evaluator) x2.getEvaluator();
		logger.info("evalr_x2 = " + evalr_x1.describe());
		evalr_x2.addArg(new BasicVariable("z1", 21.0));
		evalr_x2.addArg(new BasicVariable("z2", 22.0));
		logger.info("setup evalr_x2 = " + evalr_x2.describe());
		logger.info(">>>>>>>>>>>>>>> x2 value = " + x2.getValue());

		// expression e1 = x1 + x2
		Variable e1 = new BasicVariable("e1", new JepEvaluator("x1 + x2"));
		logger.info("Variable e1: " + e1);
		Evaluator evalr_e1 = (Evaluator) e1.getEvaluator();
		logger.info("evalr_e1 = " + evalr_e1.describe());
		evalr_e1.addArg(x1);
		evalr_e1.addArg(x2);

		logger.info("setup evalr_e1 = " + evalr_e1.describe());
		logger.info(">>>>>>>>>>>>>>> e1 value = " + e1.getValue());

		y1.setValue(5);
		
		logger.info("1st>>>>>>>>>>>>>>> e1 value = " + e1.getValue());

		logger.info("2nd>>>>>>>>>>>>>>> e1 value = " + e1.getValue());

	}

	public static void test3() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest3 Expressions with an Exertion and Method Evaluator");

		// expression R1 = x5 + x6 + x7
		Expression expr = new Expression("x5 + x6 + x7", EvaluatorType.JEP);
		Variable x5 = new BasicVariable("x5", 1.0);
		Variable x6 = new BasicVariable("x6", 1.0);
		Variable x7 = new BasicVariable("x7", 1.0);
		expr.addArg(x5).addArg(x6).addArg(x7);
		Task task = new ServiceEvaluationTask("expr", JEPEvaluation.class.getName(),
				"evaluate", expr);
		Variable R1 = new AspectVariable("R1", ServiceEvaluationTask.RESULT_PATH, task);
		logger.info(">>>>>>>>>>>>>>> R1 value = " + R1.getValue());
		// no network execution should be the next time
		logger.info(">>>>>>>>>>>>>>> R1 value = " + R1.getValue());
		// network execution this time
		x5.setValue(10.0);
		logger.info(">>>>>>>>>>>>>>> R1 value = " + R1.getValue());

		// expression R2 = -5x1 - 5x2
		// class name specified
		MethodEvaluator R2e = new MethodEvaluator(
				"sorcer.test.context.ResponseVariable2", "calculateVars");
		// an instance of the class given, it can be used after getValue is
		// called to access the object state.
		// ResponseVariable2 target = new ResponseVariable2();
		// MethodEvaluator R2e = new MethodEvaluator(target);
		Variable x1 = new BasicVariable("x1", 1.0);
		Variable x2 = new BasicVariable("x2", 1.0);
		R2e.addArg(x1).addArg(x2);
		// Java way
		// R2e.setMethod("calculate", new Class[] { double.class, double.class
		// },
		// new Object[] { 1.0, 1.0 });
		Variable R2 = new AspectVariable("R2", R2e);
		logger.info("setup R2e = " + R2e.describe());
		logger.info(">>>>>>>>>>>>>>> R2 value = " + R2.getValue());
		// no evaluate execution the next time
		logger.info(">>>>>>>>>>>>>>> R2 x1: " + x1.getValue());
		logger.info(">>>>>>>>>>>>>>> R2 x2: " + x2.getValue());
		logger.info(">>>>>>>>>>>>>>> R2 value = " + R2.getValue());
		// evaluate execution this time
		x1.setValue(10.0);
		logger.info(">>>>>>>>>>>>>>> R2 value = " + R2.getValue());
		// if target is the instance of method class
		// logger.info(">>>>>>>>>>>>>>> target R2 value = " + target.getR2());

		// expression R3 = -23x3 + 7x4 with JepEvaluator
		Variable R3 = new BasicVariable("R3", new JepEvaluator("-23*x3 + 7*x4"));
		Evaluator R3e = (Evaluator) R3.getEvaluator();
		Variable x3 = new BasicVariable("x3", 1.0);
		R3e.addArg(x3);
		Variable x4 = new BasicVariable("x4", 1.0);
		R3e.addArg(x4);
		logger.info("setup R3e = " + R3e.describe());
		logger.info(">>>>>>>>>>>>>>> R3 value = " + R3.getValue());
		// no evaluate execution the next time
		logger.info(">>>>>>>>>>>>>>> R3 value = " + R3.getValue());
		// evaluate execution this time
		x3.setValue(10.0);
		logger.info(">>>>>>>>>>>>>>> R3 value = " + R3.getValue());

		// expression R = R1 + R2 + R3
		Variable R = new BasicVariable("R", new JepEvaluator("R1 + R2 + R3"));
		Evaluator Re = (Evaluator) R.getEvaluator();
		logger.info("Re = " + Re.describe());
		Re.addArg(R1);
		Re.addArg(R2);
		Re.addArg(R3);
		logger.info("setup Re = " + Re.describe());
		logger.info(">>>>>>>>>>>>>>> R value = " + R.getValue());

	}

	public static void test4() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest4 Service Task Evalution");

		// expression R1 = x5 + x6 + x7
		Expression expr = new Expression("x5 + x6 + x7");
		Variable x5 = new BasicVariable("x5", 1.0);
		Variable x6 = new BasicVariable("x6", 1.0);
		Variable x7 = new BasicVariable("x7", 1.0);
		expr.addArg(x5).addArg(x6).addArg(x7);
		
		Exertion etask = new ServiceEvaluationTask("expr", JEPEvaluation.class
				.getName(), "evaluate", expr);
		Variable etv = new AspectVariable("etv", ServiceEvaluationTask.RESULT_PATH,
				etask);

		//logger.info("first >>>>>>>>>>>>>>> exertion variable = " + etv.getValue());
		
		logger.info("second >>>>>>>>>>>>>>> exertion task = " + ((Evaluation)etask).getValue());
		
	}

	public static void test5() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest5 Service Task Evalution with Updated Depenedent Value");

		// expression R1 = x5 + x6 + x7
		Expression expr = new Expression("x5 + x6 + x7", EvaluatorType.JEP);
		Variable x5 = new BasicVariable("x5", 1.0);
		Variable x6 = new BasicVariable("x6", 1.0);
		Variable x7 = new BasicVariable("x7", 1.0);
		expr.addArg(x5).addArg(x6).addArg(x7);
		Task task = new ServiceEvaluationTask("expr", JEPEvaluation.class.getName(),
				"evaluate", expr);
		Variable R1 = new AspectVariable("R1", ServiceEvaluationTask.RESULT_PATH, task);
		logger.info(">>>>>>>>>>>>>>> R1 value = " + R1.getValue());
		// no network execution the next time
		logger.info(">>>>>>>>>>>>>>> R1 value = " + R1.getValue());
		// network execution this time
		x5.setValue(10.0);
		logger.info(">>>>>>>>>>>>>>> R1 value = " + R1.getValue());
	}

	public static void test6() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest6 Changing vales in an expression - observer/observable case");

		// expression R1 = x5 + x6 + x7
		Expression expr = new Expression("x5 + x6 + x7",EvaluatorType .JEP);
		Variable x5 = new BasicVariable("x5", 1.0);
		Variable x6 = new BasicVariable("x6", 1.0);
		Variable x7 = new BasicVariable("x7", 1.0);
		expr.addArg(x5).addArg(x6).addArg(x7);
		Variable R1 = new BasicVariable("R1", expr);
		logger.info("\n\tsetup expr = " + expr.describe());
		logger.info("1st >>>>>>>>>>>>>>> expr value = " + R1.getValue());
		// no network execution the next time
		logger.info("2nd >>>>>>>>>>>>>>> expr value = " + R1.getValue());
		// network execution this time
		x5.setValue(10.0);
		logger.info("x5 changed >>>>>>>>>>>>>>> expr value = " + R1.getValue());
	}

	public static void test7() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest7 Expression Evaluator Example");

		// expression R1 = x5 + x6 + x7
		Expression expr = new Expression("R1", "x5 + x6 + x7");
		expr.addVar("x5", 1.0);
		expr.addVar("x6", 1.0);
		expr.addVar("x7", 1.0);
		Variable R1 = new BasicVariable("R1", expr);
		logger.info("\n\tsetup expr = " + expr.describe());
		logger.info("1st >>>>>>>>>>>>>>> expr value = " + R1.getValue());
		// no network execution the next time
		logger.info("2nd >>>>>>>>>>>>>>> expr value = " + R1.getValue());
		// network execution this time
		Variable x5 = expr.getVarInfo("x5");
		x5.setValue(10.0);
		logger.info("x5 changed >>>>>>>>>>>>>>> expr value = " + R1.getValue());
		logger.info(">>>>>>>>>>>>>>> R1 value = " + R1.getValue());
	}

	public static void test8() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest8 Context Evauator Example");

		// expression R1 = x5 + x6 + x7
		Expression expr1 = new Expression("R1", "x5 + x6 + x7");
		expr1.addVar("x5", 1.0);
		expr1.addVar("x6", 1.0);
		expr1.addVar("x7", 1.0);
		Variable R1 = new BasicVariable("R1", expr1);

		// expression R2 = y1 + y2
		Expression expr2 = new Expression("R2", "y1 * y2");
		Variable y1 = new BasicVariable("y1", 2.0);
		expr2.addArg(y1);
		expr2.addVar("y2", 3.0);
		Variable R2 = new BasicVariable("R2", expr2);

		logger.info("\n\tsetup expr1 = " + expr1.describe());
		logger.info("1st >>>>>>>>>>>>>>> expr value = " + R1.getValue());

		logger.info("\n\tsetup expr2 = " + expr2.describe());
		logger.info("2nd >>>>>>>>>>>>>>> expr value = " + R2.getValue());

		y1.setValue(5.0);
		logger.info("1st again >>>>>>>>>>>>>>> expr value = " + R1.getValue());
		logger.info("2nd again >>>>>>>>>>>>>>> expr value = " + R2.getValue());

		ServiceContext cxt = new ServiceContext();
		try {
			cxt.putValue("expression/R1", R1);
			cxt.putValue("expression/R2", R2);
		} catch (ContextException e) {
			e.printStackTrace();
		}
		
		// testing context as the Evaluation
		cxt.setEventInfo("expression/R1");
		logger.info(" >>>>>>>>>>>>>>> service context value = " + cxt.getValue());
				
		Variable X1 = new AspectVariable("X1", "expression/R1", cxt);
		Variable X2 = new AspectVariable("X2", "expression/R2", cxt);

		logger
				.info("X1 first >>>>>>>>>>>>>>> node R1 value = "
						+ X1.getValue());
		logger
				.info("X2 first >>>>>>>>>>>>>>> node R2 value = "
						+ X2.getValue());

		y1.setValue(40.0);
		logger.info("X1 second >>>>>>>>>>>>>>> node R1 value = "
				+ X1.getValue());
		logger.info("X2 second >>>>>>>>>>>>>>> node R2 value = "
				+ X2.getValue());

		logger.info("X1 again second >>>>>>>>>>>>>>> node R1 value = "
				+ X1.getValue());
		logger.info("X2 again second >>>>>>>>>>>>>>> node R2 value = "
				+ X2.getValue());

	}

	public static void test9() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest9 Method Evaluator Example");
		// expression R2 = -5x1 - 5x2 defined in the class
		// "sorcer.test.eval.ResponseVariable2
		//
		// Variable R2 = new BasicVariable("R2", "calculateVars",
		// "sorcer.test.eval.ResponseVariable2");
		// MethodEvaluator me = new MethodEvaluator(
		// "sorcer.test.eval.ResponseVariable2", "calculateVars");
		//
		// an instance of the class given, it can be used after getValue is
		// called to access the object state.
		// ResponseVariable2 target = new ResponseVariable2();
		// MethodEvaluator R2e = new MethodEvaluator(target);

		Variable R2 = new AspectVariable("R2", "calculateVars",
				"sorcer.test.eval.ResponseVariable2");
		Variable x1 = new BasicVariable("x1", 1.0);
		Variable x2 = new BasicVariable("x2", 1.0);
		Evaluator me = (Evaluator) R2.getEvaluator();
		me.addArg(x1).addArg(x2);
		// Java way
		// me.setMethod("calculate", new Class[] { double.class, double.class
		// },
		// new Object[] { 1.0, 1.0 });
		// Variable R2 = new BasicVariable("R2", me);

		logger.info("setup me = " + me.describe());
		logger.info(">>>>>>>>>>>>>>> R2 value = " + R2.getValue());
		// no evaluate execution the next time
		logger.info(">>>>>>>>>>>>>>> R2 x1: " + x1.getValue());
		logger.info(">>>>>>>>>>>>>>> R2 x2: " + x2.getValue());
		logger.info(">>>>>>>>>>>>>>> R2 value = " + R2.getValue());
		// evaluate execution this time
		x1.setValue(10.0);
		logger.info(">>>>>>>>>>>>>>> R2 value = " + R2.getValue());

		// if target is the instance of method class
		// logger.info(">>>>>>>>>>>>>>> target R2 value = " + target.getR2());
	}

	public static void test10() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest10 Method Evaluator Simpler Syntax Example");

		Variable R2 = new AspectVariable("R2", "calculateVars",
				"sorcer.test.eval.ResponseVariable2");
		R2.addArg(new BasicVariable("x1", 1.0)).addArg(
				new BasicVariable("x2", 1.0));

		logger.info(">>>>>>>>>>>>>>> R2 value = " + R2.getValue());
	}

	public static void test11() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest9 Java Class Field Evaluator Example");

		Variable R2 = new AspectVariable("R2", "calculateVars",
				"sorcer.test.eval.ResponseVariable2");
		Variable x1 = new BasicVariable("x1", 1.0);
		Variable x2 = new BasicVariable("x2", 1.0);
		Evaluator me = (Evaluator) R2.getEvaluator();
		me.addArg(x1).addArg(x2);

		logger.info("setup me = " + me.describe());
		logger.info(">>>>>>>>>>>>>>> R2 value = " + R2.getValue());

		Variable R3 = new BasicVariable("R3", "name", new FieldEvaluator(
				(MethodEvaluator) me));
		logger.info(">>>>>>>>>>>>>>> R3 field value \"name\" = "
				+ R3.getValue());

		R3.setValue("Sobolewski");
		logger.info(">>>>>>>>>>>>>>> R3 field value \"name\" = "
				+ R3.getValue());

		Variable R4 = new AspectVariable("R4", "calculateVars",
				new MethodEvaluator(R3.getEvaluator()));
		logger.info(">>>>>>>>>>>>>>> R4 method \"calculateVars\" = "
				+ R4.getValue());
	}

	public static void test12() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest10 Method Evaluator Simpler Syntax Example");

		Variable R2 = new AspectVariable("R2", "calculateVars",
				"sorcer.test.eval.ResponseVariable2");
		R2.addArg(new BasicVariable("x1", 1.0)).addArg(
				new BasicVariable("x2", 1.0));

		logger.info(">>>>>>>>>>>>>>> R2 value = " + R2.getValue());
	}

	public static void test13() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest13 Service Context Evaluator Example");
		
		// expression R1 = x5 + x6 + x7
		Expression expr1 = new Expression("R1", "x5 + x6 + x7");
		expr1.addVar("x5", 1.0);
		expr1.addVar("x6", 1.0);
		expr1.addVar("x7", 1.0);
		Variable R1 = new BasicVariable("R1", expr1);
		
		// expression R2 = y1 + y2
		Expression expr2 = new Expression("R2", "y1 * y2");
		Variable y1 = new BasicVariable("y1", 2.0);
		expr2.addArg(y1);
		expr2.addVar("y2", 3.0);
		Variable R2 = new BasicVariable("R2", expr2);
		
		// context evaluator
		Context cxt = new ServiceContext();
		try {
			cxt.putValue("expression/R1", R1);
			cxt.putValue("expression/R2", R2);
		} catch (ContextException e) {
			e.printStackTrace();
		}
		Variable X1 = new AspectVariable("X1", "expression/R1", cxt);
		Variable X2 = new AspectVariable("X2", "expression/R2", cxt);
		
		
		logger.info(">>>>>>>>>>>>>>> X1 = " + X1.getValue());
		logger.info(">>>>>>>>>>>>>>> X2 = " + X2.getValue());
	}
	
	public static void test14() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest11 Variable set Evaluator Example");
		
		// method evaluator "calculateVars" -5x1 - 5x2 defined in the class 
		// sorcer.test.eval.ResponseVariable2
		Variable R0 = new AspectVariable("R0", "calculateVars",
				"sorcer.test.eval.ResponseVariable2");
		R0.addArg(new BasicVariable("x1", 1.0)).addArg(
				new BasicVariable("x2", 1.0));
		//logger.info("::::::::::::::: R0 = " + R0.getValue());
		
		// expression R1 = x5 + x6 + x7
		Expression expr1 = new Expression("R1", "x5 + x6 + x7");
		expr1.addVar("x5", 1.0);
		expr1.addVar("x6", 1.0);
		expr1.addVar("x7", 1.0);
		Variable R1 = new BasicVariable("R1", expr1);
		//logger.info("::::::::::::::: R1 = " + R1.getValue());
		
		// expression R2 = y1 + y2
		Expression expr2 = new Expression("R2", "y1 * y2");
		Variable y1 = new BasicVariable("y1", 2.0);
		expr2.addArg(y1);
		expr2.addVar("y2", 3.0);
		Variable R2 = new BasicVariable("R2", expr2);
		//logger.info("::::::::::::::: R2 = " + R2.getValue());
		
		Variable R3 = new BasicVariable("R3", expr2);
		//logger.info("::::::::::::::: R3 = " + R3.getValue());
		
		// master expression
		Variable master = new AspectVariable("master", new Expression("master",
				"R0 + R1 + R2 + R3"));
		master.addArg(R0).addArg(R1).addArg(R2).addArg(R3);
//		logger.info("::::::::::::::: master = " + master.getValue());
//		y1.setValue(5.0);
//		logger.info("::::::::::::::: master = " + master.getValue());
		
		// variable set evaluator
		VariableSetEvaluator vse = new VariableSetEvaluator("vse");
		vse.addVariable(R0).addVariable(R1).addVariable(R2).addVariable(R3).addVariable(master);
		AspectVariable set = new AspectVariable("set", "master", vse);
		logger.info("::::::::::::::: SET value = " + set.getValue());
		
		y1.setValue(5.0);
		logger.info("::::::::::::::: again SET value = " + set.getValue());
	}
	
	public static void test15() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest15 Variable set Evaluator Example");
		// expression R1 = x5 + x6 + x7
		Expression expr1 = new Expression("R1", "x5 + x6 + x7");
		expr1.addVar("x5", 1.0);
		expr1.addVar("x6", 1.0);
		expr1.addVar("x7", 1.0);
		Variable R1 = new AspectVariable("R1", expr1);
		logger.info("::::::::::::::: R1 = " + R1.getValue());

		// expression R2 = y1 + y2
		Expression expr2 = new Expression("R2", "y1 * y2");
		Variable y1 = new BasicVariable("y1", 2.0);
		expr2.addArg(y1);
		expr2.addVar("y2", 3.0);
		Variable R2 = new AspectVariable("R2", expr2);
		logger.info("::::::::::::::: R2 = " + R2.getValue());

		Variable R3 = new AspectVariable("R3", expr2);
		logger.info("::::::::::::::: R3 = " + R3.getValue());
	}
	
	public static void test16() throws ParException, RemoteException,
			EvaluationException, ContextException, SignatureException {
		logger.info("\n\n\ttest16 Exertion/Artihtmetic Task Evaluator Example");
		
		NetSignature signature = new NetSignature("multiply",
		"sorcer.arithmetic.ArithmeticRemote");
		NetTask task = new NetTask("arithmethic-multiply", signature);

		ArrayContext context = new ArrayContext("arithmetic");
		context.iv(1, 10.0);
		context.ivc(1, "arg1");
		context.iv(2, 50.0);
		context.ov(3, 0.0);
		context.ovc(3, "result for multiplying values 1 and 2");
		task.setContext(context, context.ovp(3));
		
		logger.info("::::::::::::::: task value = " + task.getValue());

//		Variable etv = new AspectVariable("etv",context.ovp(3),
//				task);
//		logger.info("::::::::::::::: task value = " + etv.getValue());
	}
	
	public static void test17() throws ParException, RemoteException,
			EvaluationException, ContextException, SignatureException {

		NetSignature signature1 = new NetSignature("add",
				"sorcer.arithmetic.ArithmeticRemote");
		NetTask task1 = new NetTask("arithmethic-add", signature1);
		ArrayContext context1 = new ArrayContext("arithmetic");
		context1.iv(1, 20.0);
		context1.ivc(1, "arg1");
		context1.iv(2, 80.0);
		context1.ivc(2, "arg2");
		context1.ov(3, 0.0);
		context1.ovc(3, "result for adding arg1 and arg2");
		task1.setContext(context1);

		NetSignature signature2 = new NetSignature("multiply",
				"sorcer.arithmetic.ArithmeticRemote");
		NetTask task2 = new NetTask("arithmethic-multiply", signature2);
		ArrayContext context2 = new ArrayContext("arithmetic");
		context2.iv(1, 10.0);
		context2.ivc(1, "arg1");
		context2.iv(2, 50.0);
		context2.ov(3, 0.0);
		context2.ovc(3, "result for multiplying values 1 and 2");
		task2.setContext(context2);

		NetSignature signature3 = new NetSignature("subtract",
				"sorcer.arithmetic.ArithmeticRemote");
		NetTask task3 = new NetTask("arithmethic-subtract", signature3);
		ArrayContext context3 = new ArrayContext("arithmetic");
		context3.iv(1, 0.0);
		context3.ivc(1, "arg1: result of task 2");
		context3.iv(2, 0.0);
		context3.ivc(2, "arg2: result of task 1");
		context3.ov(3, 0.0);
		context3.ovc(3, "result for subtacting arg1 and arg2");
		task3.setContext(context3);

		NetJob job = new NetJob("Arithmetic");
		job.addExertion(task1);
		job.addExertion(task2);
		job.addExertion(task3);
		
		// map the result of second task as the first argument of task three
		logger.info("context 2: " + task2.getContext());
		logger.info("context 3: " + task3.getContext());
		task2.getContext().map(ArrayContext.ovp(3), ArrayContext.ivp(1),
				task3.getContext());
		
		// map the result of the first task as the second argument of task three
		task1.getContext().map(ArrayContext.ovp(3), ArrayContext.ivp(2),
				task3.getContext());
		job.setEventInfo(context3.ovc(3));

		logger.info("::::::::::::::: jab value = " + job.getValue());
	}
	
	public static void test18() throws ParException, RemoteException,
	EvaluationException, ContextException {
// test creating RealDesignVarible as itemData in a context node where the item data uses a "Method" filter
		ResponseVariable2 rv = new ResponseVariable2(); 
		ContextNode rvCn = new ContextNode("rvObject",rv);
		Vector filter = new Vector();
		String filterName = "dv";
		filter.addElement("Method");
		filter.addElement("Double");
		// assuming the following structure "methodName(Type1 arg1, Type2
		// arg2, ...)"
		filter.addElement("setDv(java.lang.Double value)");
		filter.addElement("getDv()");
		rvCn.addItem(filterName, filter);
		GaussianDistribution gD = new GaussianDistribution(
				new Double("0.0"), new Double("0.0"), new Double("0.1"));
		CadDescription cD = new CadDescription(CaeConstants.SHAPE);
		AspectVariable dvav = new AspectVariable("dv", "dv",rvCn,
				Var.DOUBLE, true, cD, gD);
		RealDesignVariable rdv = new RealDesignVariable(dvav.getName(), dvav, 10000.0, -10000.0, 1.0);	
		System.out.println("RDV value before set = "+rdv.getValue());
		System.out.println("rv2 getvalue before set = "+rv.getDv());
		rdv.setValue(3.14159);
		System.out.println("RDV getvalue after set= "+rdv.getValue());
		System.out.println("rv2 getvalue after set= "+rv.getDv());
	}
}
