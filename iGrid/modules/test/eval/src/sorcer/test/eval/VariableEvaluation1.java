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
import static sorcer.core.context.model.var.Node.children;
import static sorcer.core.context.model.var.Node.node;
import static sorcer.eo.operator.classInstance;
import static sorcer.eo.operator.evaluate;
import static sorcer.eo.operator.instance;
import static sorcer.eo.operator.instance;
import static sorcer.eo.operator.provider;
import static sorcer.eo.operator.providers;
import static sorcer.eo.operator.sig;
import static sorcer.vfe.operator.model;
import static sorcer.vfe.operator.submodels;
import static sorcer.vo.operator.constraintVars;
import static sorcer.vo.operator.outputVars;
import static sorcer.vo.operator.designVars;
import static sorcer.vo.operator.differentiation;
import static sorcer.vo.operator.evaluation;
import static sorcer.vo.operator.evaluator;
import static sorcer.vo.operator.gradient;
import static sorcer.vo.operator.groovy;
import static sorcer.vo.operator.inputVars;
import static sorcer.vo.operator.objectiveVars;
import static sorcer.vo.operator.optimizationModel;
import static sorcer.vo.operator.realization;
import static sorcer.vo.operator.realizations;
import static sorcer.vo.operator.responseModel;
import static sorcer.vo.operator.soaEvaluator;
import static sorcer.vo.operator.update;
import static sorcer.vo.operator.var;
import static sorcer.vo.operator.vars;
import static sorcer.vo.operator.wrt;

import java.io.File;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import sorcer.arithmetic.provider.AdderImpl;
import sorcer.arithmetic.provider.Multiplier;
import sorcer.arithmetic.provider.MultiplierImpl;
import sorcer.core.context.model.opti.OptimizationModel;
import sorcer.core.context.model.var.FidelityInfo;
import sorcer.core.context.model.var.Node;
import sorcer.core.context.model.var.Noder;
import sorcer.core.context.model.var.Realization;
import sorcer.core.context.model.var.ResponseModel;
import sorcer.service.ContextException;
import sorcer.service.EvaluationException;
import sorcer.service.SignatureException;
import sorcer.util.Log;
import sorcer.util.SorcerUtil;
import sorcer.util.exec.ExecUtils.CmdResult;
import sorcer.vfe.ServiceEvaluator;
import sorcer.vfe.Filter;
import sorcer.vfe.FilterException;
import sorcer.vfe.Var;
import sorcer.vfe.VarException;
import sorcer.vfe.VarInfo;
import sorcer.vfe.VarInfo.Relation;
import sorcer.vfe.VarInfo.Target;
import sorcer.vfe.evaluator.CmdEvaluator;
import sorcer.vfe.evaluator.ExpressionEvaluator;
import sorcer.vfe.evaluator.JepEvaluator;
import sorcer.vfe.evaluator.SoaEvaluator;
import sorcer.vfe.filter.ArrayFilter;
import sorcer.vfe.filter.ContextFilter;
import sorcer.vfe.filter.ListFilter;
import sorcer.vfe.filter.MapFilter;
import sorcer.vfe.filter.MapKeyFilter;
import sorcer.vfe.filter.ObjectFieldFilter;
import sorcer.vfe.util.VarList;

/**
 * Example how use the Variables and JepEvaluator with various generic filters.
 * and multiple filters
 */
public class VariableEvaluation1 {

	private static Logger logger = Log.getTestLog();

	public static void main(String[] args) throws Exception {
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());
//		logger.info("provider.lookup.accessor: "
//				+ Sorcer.getProperty("provider.lookup.accessor"));

		int test = new Integer(args[0]);
        switch (test) {
            case 1:  test1(); break;
            case 2:  test2(); break;
            case 3:  test3(); break;
            case 4:  test4(); break;
            case 5:  test5(); break;
            case 6:  test6(); break;
            case 7:  test7(); break;
            case 8:  test8(); break;
            case 9:  test9(); break;
            case 10:  test10(); break;
            case 11:  test11(); break;
            case 12:  test12(); break;
            case 13:  test13(); break;
            case 14:  test14(); break;
            case 15:  test15(); break;
            case 16:  test16(); break;
            case 17:  test17(); break;
            case 18:  test18(); break;
            case 19:  test19(); break;
            case 20:  test20(); break;
            case 21:  test21(); break;
            case 22:  test22(); break;
            case 23:  test23(); break;
        }
	}

	
	public static void test1() throws ParException, RemoteException,
	EvaluationException {


		logger.info("\ntest1 using variables and jep evaluator (s. a. burton)");


		// independent variables
		//
		Var x1 = new Var("x1", 1.0);
		logger.info("x1 = " + x1.getValue());

		Var x2 = new Var("x2", 2.0);
		logger.info("x2 = " + x2.getValue());


		// dependent variable e1
		//
		Var e1 = new Var("e1");

		ServiceEvaluator jepEval1 = new JepEvaluator("x1 + x2");
		jepEval1.addArg(x1);
		jepEval1.addArg(x2);

		e1.setEvaluator(jepEval1);


		// test
		//
		logger.info("e1 = " + e1.getValue());
		x1.setValue(2.0);
		x2.setValue(3.0);
		logger.info("e1 = " + e1.getValue());

	}
	
	
	public static void test2() throws ParException, RemoteException,
	EvaluationException {

		logger.info("\ntest2 using variables, jep evaluator, and MappingFilter (s. a. burton)");

		// create independent variables
		//
		Var x1 = new Var("x1", 1.0);
		logger.info("x1 = " + x1.getValue());

		Var x2 = new Var("x2", 2.0);
		logger.info("x2 = " + x2.getValue());


		// create dependent variable that evals "x1 + x2" and maps a result of 3 to "done"
		// three steps: 
		//		0) create Var object
		//		1) add Evaluator object
		//		2) add Filter object
		//
		Var y1 = new Var("y1");


		// create Evaluator and add to Var object
		//
		ServiceEvaluator myEval = new JepEvaluator("x1 + x2");
		myEval.addArg(x1);
		myEval.addArg(x2);
		y1.setEvaluator(myEval);


		// create Filter object in three steps: 
		//		1) create Map object; and 
		//		2) create Filter object
		//		3) add Filter to Var
		//
		Map fm1 = new HashMap();
		fm1.put(3.0, "done");
		Filter f1 = new MapKeyFilter(fm1);
		y1.setFilter(f1);

		
		// print out y1 value
		//
		logger.info("y1: " + y1);
		logger.info("y1 = " + y1.getValue());

	}

	
	public static void test21() throws ParException, RemoteException,
			EvaluationException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test2 Using Variables and JEP Evaluator and MapFilter");

		Map fm1 = new HashMap();
		fm1.put("done", 3.0);
		fm1.put("exit", 4.0);
		fm1.put("start", 5.0);
		
		Filter f1 = new MapFilter("done");
		Object val = null;
		try {
			val = f1.doFilter(fm1);
		} catch (FilterException e) {
			e.printStackTrace();
		}
		
		logger.info("<<<<<<<<<<<<<<<<<<<< map filter out: " + val);
	}
	
	public static void test22() throws ParException, RemoteException,
			EvaluationException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test2 Using Variables and JEP Evaluator and MapFilter");

		// map result of 3 to "done"
		double[] nums = new double[3];;
		nums[0] = 0.3;
		nums[1] = 1.3;
		nums[2] = 2.3;

		Filter f1 = new ArrayFilter(1, 2);
		Object val = null;
		try {
			val = f1.doFilter(nums);
		} catch (FilterException e) {
			e.printStackTrace();
		}
		if (val.getClass().isArray())
			logger.info("<<<<<<<<<<<<<<<<<<<< array filter out: " + SorcerUtil.arrayToString(val));
		else
			logger.info("<<<<<<<<<<<<<<<<<<<< array filter out: " + val);
	}
	
	public static void test3() throws ParException, RemoteException,
			EvaluationException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test3 Using Variables and JEP Evaluator and MapFilter chain");

		// map result of 3 to "done"
		Map fm1 = new HashMap();
		fm1.put(3.0, "done");
		Filter f1 = new MapFilter(fm1);

		// now remap "done" to 10
		Map fm2 = new HashMap();
		fm2.put("done", 10);
		f1.addFilter(new MapFilter(fm2));
		logger.info("f1: " + f1);

		// expression e1 = x1 + x2
		Var e1 = new Var("e1");
		e1.setFilter(f1);
		e1.setEvaluator(new JepEvaluator("x1 + x2"));
		logger.info("e1: " + e1);

		ServiceEvaluator evalr_e1 = (ServiceEvaluator) e1.getEvaluator();
		logger.info("evalr_e1: " + evalr_e1.describe());

		evalr_e1.addArg(new Var("x1", 1));
		evalr_e1.addArg(new Var("x2", 2));
		logger.info("setup evalr_e1: " + evalr_e1.describe());

		logger.info("<<<<<<<<<<<<<<<<<<<< first: e1 value: " + e1.getValue());
		logger.info("<<<<<<<<<<<<<<<<<<<< second: e1 value: " + e1.getValue());
	}

	public static void test4() throws ParException, RemoteException,
			EvaluationException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test4 Using Variables and JEP Evaluator, MapFilter/MapFilter and ObjectFilter chain");

		// map result of 3 to "done"
		Map fm1 = new HashMap();
		fm1.put(3.0, "done");
		Filter f1 = new MapFilter(fm1);

		// now remap "done" to 10
		Map fm2 = new HashMap();
		fm2.put("done", new Response());

		// the second map filter
		f1.addFilter(new MapFilter(fm2));
		logger.info("f1: " + f1);

		// get "name" field = "Mike" in new Response()
		f1.addFilter(new ObjectFieldFilter("name"));
		logger.info("f1: " + f1);

		// expression e1 = x1 + x2
		Var e1 = new Var("e1");
		e1.setFilter(f1);
		e1.setEvaluator(new JepEvaluator("x1 + x2"));
		logger.info("e1: " + e1);

		ServiceEvaluator evalr_e1 = (ServiceEvaluator) e1.getEvaluator();
		logger.info("evalr_e1: " + evalr_e1.describe());

		evalr_e1.addArg(new Var("x1", 1));
		evalr_e1.addArg(new Var("x2", 2));
		logger.info("setup evalr_e1: " + evalr_e1.describe());

		logger.info("<<<<<<<<<<<<<<<<<<<< first: e1 value: " + e1.getValue());
		logger.info("<<<<<<<<<<<<<<<<<<<< second: e1 value: " + e1.getValue());
	}

	public static void test5() throws ParException, RemoteException,
			EvaluationException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test5 Using Variables and JEP Evaluator, MapFilter/MapFilter and ObjectFilter/ContextFilter chain");

		// map result of 3 to "done"
		Map fm1 = new HashMap();
		fm1.put(3.0, "done");
		Filter f1 = new MapKeyFilter(fm1);

		// now remap "done" to 10
		Map fm2 = new HashMap();
		fm2.put("done", new Response());

		// the second map filter
		f1.addFilter(new MapKeyFilter(fm2));
		logger.info("f1: " + f1);

		// get this time the "context" field in new Response()
		f1.addFilter(new ObjectFieldFilter("context"));
		logger.info("f1: " + f1);

		// ContextFiler for "person/last/name" get "Sobolewski"
		f1.addFilter(new ContextFilter("person/last/name"));
		logger.info("f1: " + f1);

		// expression e1 = x1 + x2
		Var e1 = new Var("e1");
		e1.setFilter(f1);
		e1.setEvaluator(new JepEvaluator("x1 + x2"));
		logger.info("e1: " + e1);

		ServiceEvaluator evalr_e1 = (ServiceEvaluator) e1.getEvaluator();
		logger.info("evalr_e1: " + evalr_e1.describe());

		evalr_e1.addArg(new Var("x1", 1));
		evalr_e1.addArg(new Var("x2", 2));
		logger.info("setup evalr_e1: " + evalr_e1.describe());

		
		logger.info("<<<<<<<<<<<<<<<<<<<< first: e1 value: " + e1.getValue());
		//logger.info("<<<<<<<<<<<<<<<<<<<< second: e1 value: " + e1.getValue());
	}

	public static void test6() throws ParException, RemoteException,
			EvaluationException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test6 Using Variables with the Object Filter only");
		
		Filter f1 = new ObjectFieldFilter("dv");
		f1.setTarget(new Response());
		logger.info("f1: " + f1);

		// expression e1 = x1 + x2
		Var e1 = new Var("e1");
		e1.setFilter(f1);
		logger.info("e1: " + e1);

		logger.info("<<<<<<<<<<<<<<<<<<<< first: e1 value: " + e1.getValue());
		logger.info("<<<<<<<<<<<<<<<<<<<< second: e1 value: " + e1.getValue());
	}
	
	public static void test7() throws ParException, RemoteException,
			EvaluationException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test7 Using Variables with the List Filter only");

		ListFilter f1 = new ListFilter("x", "y", "z");
		f1.setIndexes(1, 2);
		
		ListFilter f2 = new ListFilter(1, 2, 3);
		f2.setIndex(2);
		
		logger.info("f1: " + f1);
		logger.info("f2: " + f2);
		
		Var v1 = new Var("v1");
		v1.setFilter(f1);
		logger.info("v1: " + v1);

		Var v2 = new Var("v2");
		v2.setFilter(f2);
		logger.info("v1: " + v1);
		
		logger.info("<<<<<<<<<<<<<<<<<<<< first: v1 value: " + v1.getValue());
		logger.info("<<<<<<<<<<<<<<<<<<<< second: v2 value: " + v2.getValue());
	}
	
	
	public static void test8() throws ParException, RemoteException,
			EvaluationException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test8 Using Variables with the List Filter only");

		ListFilter f1 = new ListFilter("x", "y", list(4, list(7, 8, 9, 10), 6));
		f1.setIndex(2);

		ListFilter f2 = new ListFilter();
		f2.setIndex(1);

		ListFilter f3 = new ListFilter();
		f3.setIndex(2);
		
		ListFilter f4 = new ListFilter();
		f4.setIndex(3);
		
		Filter f5 = new Filter();
		f5.setPipeline(f1, f2, f3);
		
		Filter f6 = new Filter(f1, f2, f4);
		
		logger.info("f5: " + f5);
		logger.info("f6: " + f6);

		Var v1 = new Var("v1");
		v1.setFilter(f5);
		logger.info("v1: " + v1);

		Var v2 = new Var("v2");
		v2.setFilter(f6);
		logger.info("v1: " + v1);

		logger.info("<<<<<<<<<<<<<<<<<<<< first: v1 value: " + v1.getValue());
		logger.info("<<<<<<<<<<<<<<<<<<<< second: v2 value: " + v2.getValue());
	}
	
	public static void test9() throws ParException, RemoteException,
			EvaluationException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test1 Using Variables and Groovy Evaluator");

		// expression y = x1 + x2
		Var<?> y = new Var<Object>("y");
		//y.setEvaluator(new GroovyEvaluator("x1 + x2"));
		//y.setEvaluator(new ExpressionEvaluator("Groovy", "x1 + x2", Type.GROOVY));
		y.setEvaluator(groovy("x1 + x2"));
		logger.info("y: " + y);

		ExpressionEvaluator ye = (ExpressionEvaluator) y.getEvaluator();
		logger.info("ye: " + ye.describe());

		Var<Integer> x2 = new Var<Integer>("x2", 2);
		ye.addArg(new Var<Integer>("x1", 1));
		ye.addArg(x2);
		logger.info("setup ye: " + ye.describe());

		logger.info("<<<<<<<<<<<<<<<<<<<< first: y value: " + y.getValue());
		x2.setValue(10);
		logger.info("<<<<<<<<<<<<<<<<<<<< second: y value: " + y.getValue());
		
		ye.setExpression("x2");
		logger.info("<<<<<<<<<<<<<<<<<<<< third: y value: " + y.getValue());
		
		ye.setExpression("1");
		logger.info("<<<<<<<<<<<<<<<<<<<< fourth: y value: " + y.getValue());
		
		ye.setExpression("x1 >= x2");
		logger.info("<<<<<<<<<<<<<<<<<<<< fith: y value: " + y.getValue());
		
		ye.setExpression("new Date()");
		logger.info("<<<<<<<<<<<<<<<<<<<< sixth: y value: " + y.getValue());
		
		Var<Integer> foo = new Var<Integer>("foo", 2);
		ye.addArg(foo);
		ye.setExpression("println 'Hello World!'; x = 123; return foo * 10 + x");
		logger.info("<<<<<<<<<<<<<<<<<<<< seventh: y value: " + y.getValue());
		
		foo.setValue(100);
		StringBuilder sb = new StringBuilder();
		sb.append("import sorcer.vfe.Var;")
			.append("var = new Var('x2', 30);")
			.append("println 'foo: ' + foo;")
			.append("println var;")
			.append("x = 123;")
			.append("return foo * 10 + x + var.getValue()");
		
		ye.setExpression(sb.toString());
		logger.info("<<<<<<<<<<<<<<<<<<<< import Var: y value: " + y.getValue());
	}

	public static void test10() throws ParException, RemoteException,
	EvaluationException {
		ResponseModel rm = responseModel("test of pool",
				designVars(names(loop(1, 20), "beta")),
				designVars("alpha"),
				outputVars("y"),
				realizations (loop(1, 20), "betaA$i$var", 
						evaluation("betaA$i$e", differentiation("derivative$i$A", wrt("beta$i$wrt", "alpha"))), 
						evaluation("betaB$i$e", differentiation("derivative$i$B", wrt("beta$i$wrt", "alpha")))));
	
		System.out.println("loop model: " + rm);
	}
	
	public static void test11() throws ParException, RemoteException,
	EvaluationException {
	
		Node subTree1 = new Node('A');
		subTree1.getChildren().add(new Node('A'));
		subTree1.getChildren().add(new Node('A'));

		Node subTree2 = new Node('A');
		subTree2.getChildren().add(new Node('A'));
		subTree2.getChildren().add(new Node('C'));
		subTree2.getChildren().add(subTree1);

		Node subTree3 = new Node('B');
		subTree3.getChildren().add(subTree2);

		Node root1 = new Node('A');
		root1.getChildren().add(subTree3);
		
		Node n1 = node("A", children(node("A"), node("A")));
		Noder n2 = node("A", children(node("A"), node("C"), n1));
		Noder n3 = node("B", children(n2));
		Noder n = node("A", children(n3));
		
		Node n11 = node("A", children(node("B", children(node("A", children(node("A"), node("C"), node("A", children(node("A"), node("A")))))))));

		Node n21 = model("A", submodels(model("B", submodels(model("A", submodels(model("A"), model("C"), model("A", submodels(model("A"), model("A")))))))));
		//ModelProducer n31 = producer("A", subproducers(producer("B", subproducers(producer("A", subproducers(producer("A"), producer("C"), producer("A", subproducers(producer("A"), producer("A")))))))));
		
		Node root2 = new Node('A');
		root2.getChildren().add(new Node('A'));
		root2.getChildren().add(new Node('A'));
		
		Node s1 = node("A", children(node("A"), node("A")));

      System.out.println(">>>>>>>>>>>>>>>> n: " + n);
      System.out.println(">>>>>>>>>>>>>>>> s1: " + s1);
      System.out.println(">>>>>>>>>>>>>>>> n21: " + n21);
      System.out.println(">>>>>>>>>>>>>>>> n31: " + n21);
//      System.out.println(">>>>>>>>>>>>>>>> 1 matched subtree: " + Node.findSubtree(tree, node));
//      System.out.println(">>>>>>>>>>>>>>>> 2 matched subtree: " + Node.findSubtree(n, s1));
      //System.out.println(">>>>>>>>>>>>>>>> model matched subtree: " + Node.findSubtree(n31, s1));
	}
	
	
	public static void test12() throws RemoteException, ContextException {
		
		OptimizationModel om = optimizationModel("Induced Drag", 
			designVars(vars(loop(20), "beta", 0.0, -10.0, 10.0)), 
			designVars(var("alpha", 1.0, -5.0, 5.0)),
			designVars("v0", "rho" ),
			outputVars("DI"),
//			realization("DI", 
//				evaluation("DIeExact"), evaluation("DIeSOA"),
//				evaluation("DIeMOA"), evaluation("DIeKrig"),
//				derivative("DIeExact", wrt(names(loop(20), "beta"), "alpha"), gradient("DIeExactg1")),
//				derivative("DIeSOA", wrt(names(loop(20), "beta"), "alpha"), gradient("DIeSOAg1")),
//				derivative("DIeMOA", wrt(names(loop(20), "beta"), "alpha"), gradient("DIeMOAExactg1")),
//				derivative("DIeKrig", wrt(names(loop(20), "beta"), "alpha"), gradient("DIeKrigg1"))),
			realization("DI", 
					evaluation("DIeExact", 
						differentiation(wrt(names(loop(20), "beta"), "alpha"), gradient("DIeExactg1"))), 
					evaluation("DIeSOA", 
						differentiation(wrt(names(loop(20), "beta"), "alpha"), gradient("DIeSOAg1"))),
					evaluation("DIeMOA", 
						differentiation(wrt(names(loop(20), "beta"), "alpha"), gradient("DIeMOAExactg1"))), 
					evaluation("DIeKrig",	
						differentiation(wrt(names(loop(20), "beta"), "alpha"), gradient("DIeKrigg1")))),
			outputVars("Ltot"),
			realization("Ltot", differentiation(wrt(names(loop(20), "betai$i$"),"alpha"))),
			outputVars(names(loop(20), "Lpus")), 
			realization(loop(20), "Lpus$i$", 
					evaluation("Lpus$i$Exact", differentiation(wrt(names(loop(20), "beta"), "alpha"))), 
					evaluation("Lpus$i$SOA"), differentiation(wrt(names(loop(20), "beta"), "alpha"))), 
			outputVars("qdp"), 
			realization("qdp", evaluation("qdpExact"), evaluation("qdpSOA"), 
					differentiation("qdp", wrt("v0","rho"))),
			objectiveVars(var("DIo", "DI", Target.min)),
			constraintVars(var("Ltotc", "Ltot", Relation.eq, 1000.0)));

		System.out.println("avus model: " + om);
		
		System.out.println("avus model design vars: " + om.getDesignVars().getNames());
		System.out.println("avus model response vars: " + om.getOutputVars().getNames());
		System.out.println("avus model objective vars: " + om.getObjectiveVars().getNames());
		System.out.println("avus model constraint vars: " + om.getConstraintVars().getNames());
		Realization DIr = om.getVar("DI").getRealization();
		System.out.println("avus model DI : " + DIr);
		List<FidelityInfo> DIes = DIr.getEvaluations();
		for (FidelityInfo e : DIes)
			System.out.println("\navus DI evaluation: " + e);
	}
	
	@SuppressWarnings("unchecked")
	public static void test13() throws RemoteException, ContextException {
		Var y1 =var("y1");
		System.out.println("y1: " +y1);
		Var y2 =var("y2", 10.0);
		System.out.println("y2: " + y2);
		Var y3 =var("y3", 10.0, 5.0, 20.0);
		System.out.println("y3: " + y3);
		
		VarList yvl = vars(loop(5), "y", 10.0, 5.0, 20.0);
		System.out.println("yvl: " + yvl);
		
		VarList xvl =vars(loop(3, 9), "x", 10.0, 5.0, 20.0);
		System.out.println("xvl: " + xvl);
		
		VarList wordvl = vars(loop("i", 3, 9), "Lpus$i$SOA$ms$", 10.0, 5.0, 20.0);
		System.out.println("wordvl: " + wordvl);
	}
		
	
	@SuppressWarnings("unchecked")
	public static void test14() throws RemoteException, ContextException {
		
		VarList vars = vars(var("x1", 10.0), var("x2", 11.0), var("x3", 12.0));
		
		//SoaEvaluator(double valueF0, double[] calcPointF0, double[] gradientF0)

		SoaEvaluator eval = soaEvaluator(new double[] { 2.0, 3.0, 4.0 }, 20.0,  new double[] { 2.0, 3.0, 40 });
		System.out.println(evaluate(evaluator(soaEvaluator(new double[] { 2.0, 3.0, 4.0 }, 20.0, new double[] { 2.0, 3.0, 40 }), vars)));		
		update(eval, new double[] { 2.0, 3.0, 4.0 },  10.0, new double[] { 2.0, 3.0, 40 });
		System.out.println(evaluate(evaluator(eval, vars)));
	}
	

	public static void test15() throws RemoteException, ContextException {
		//List<VarInfo> varsInfo = vars(var("x1", 10.0), var("x2", 11.0), var("x3", 12.0));
		List<VarInfo> varsInfo = list(new VarInfo("x1", 10.0), new VarInfo("x2", 11));
		
		System.out.println(">>>>>>>>>>>>>>>> vars info: " + varsInfo);
		System.out.println(">>>>>>>>>>>>>>>> vars info 0: " +  varsInfo.get(0).getName() + ":" + varsInfo.get(0).getValue());
		System.out.println(">>>>>>>>>>>>>>>> vars info 1: " + varsInfo.get(1).getName()  + ":" + varsInfo.get(1).getValue());

	}
	
	public static void test16() throws RemoteException, ContextException, SignatureException {

		Object obj = instance(sig(null, AdderImpl.class));
		System.out.println(">>>>>>>>>>>>>>>> instance obj: " + obj);
		
		obj = instance(sig(null, AdderImpl.class));
		System.out.println(">>>>>>>>>>>>>>>> newInstance obj: " + obj);
		
		obj = classInstance(sig(null, AdderImpl.class));
		System.out.println(">>>>>>>>>>>>>>>> classInstance obj: " + obj);
		
		System.out.println(">>>>>>>>>>>>>>>> new MultiplierImpl: " + new MultiplierImpl());

		System.out.println(">>>>>>>>>>>>>>>> instance: " + instance(sig(null, MultiplierImpl.class)));

		System.out.println(">>>>>>>>>>>>>>>> provider: " + provider(sig("multiply", Multiplier.class)));

		System.out.println(">>>>>>>>>>>>>>>> providers: " + providers(sig("multiply", Multiplier.class)));
	}
	
	
	public static void test17() throws RemoteException {

		System.out.println(">>>>>>>>>>>>>>>> loop names: "
				+ names(loop("i", 1, 40), "Lpus$i$"));
	}

	public static void test18() throws RemoteException, EvaluationException {

		ServiceEvaluator e = new CmdEvaluator("ls -la");

		CmdResult result = (CmdResult) e.evaluate();

		System.out.println(">>>>>>>>>>>>>>>> CmdEvaluator ls -la status: "
				+ result.getExitValue());
		System.out.println(">>>>>>>>>>>>>>>> CmdEvaluator ls -la err: "
				+ result.getErr());
		System.out.println(">>>>>>>>>>>>>>>> CmdEvaluator ls -la out: "
				+ result.getOut());
	}

	public static void test19() throws RemoteException, EvaluationException {

		String[] cmdarray = new String[] { "ls", "-l", "-a" };
		ServiceEvaluator e = new CmdEvaluator(cmdarray);

		CmdResult result = (CmdResult) e.evaluate();

		System.out.println(">>>>>>>>>>>>>>>> CmdEvaluator ls -la status: "
				+ result.getExitValue());
		System.out.println(">>>>>>>>>>>>>>>> CmdEvaluator ls -la err: "
				+ result.getErr());
		System.out.println(">>>>>>>>>>>>>>>> CmdEvaluator ls -la out: "
				+ result.getOut());
	}

	public static void test20() throws RemoteException, EvaluationException {

		File logFile = new File("scriptLogg");
		File script = new File("test-script.sh");
		
		//Evaluator e = new CmdEvaluator("csh -cf", script, false, logFile);
			
		ServiceEvaluator e = new CmdEvaluator(new String[] {"csh", "-cf"} , script, true, logFile);
		
		CmdResult result = (CmdResult) e.evaluate();

		System.out.println(">>>>>>>>>>>>>>>> CmdEvaluator ls -la status: "
				+ result.getExitValue());
		System.out.println(">>>>>>>>>>>>>>>> CmdEvaluator ls -la err: "
				+ result.getErr());
		System.out.println(">>>>>>>>>>>>>>>> CmdEvaluator ls -la out: "
				+ result.getOut());
	}
	
	@SuppressWarnings("unchecked")
	public static void test23() throws ParException, EvaluationException  {
		VarList vl = inputVars(loop(6), "x");
		
		vl.getVar("x1").setEvaluator(groovy("x2 + x3", vl.selectVars("x2", "x3")));
		vl.getVar("x2").setEvaluator(groovy("x4 + x5", vl.selectVars("x4", "x5", "x6")));
		
//		vl.getVar("x1").setEvaluator(evaluator("x2 + x3", vl.selectVars("x2", "x3")));
//		vl.getVar("x2").setEvaluator(evaluator("x4 + x5", vl.selectVars("x4", "x5", "x6")));
		
		vl.setVarValues(entry("x3", 3.0), entry("x4", 4.0), entry("x5", 5.0), entry("x6", 6.0));
		
//		vl.getVar("x3").setValue(3.0);
//		vl.getVar("x4").setValue(4.0);
//		vl.getVar("x5").setValue(5.0);
//		vl.getVar("x6").setValue(6.0);

		System.out.println("x1: " + vl.getVar("x1"));
		System.out.println("x1 dependents: " + vl.getVar("x1").getArgs().getNames());
		System.out.println("x2: " + vl.getVar("x2"));
		System.out.println("x2 dependents: " + vl.getVar("x2").getArgs().getNames());
		System.out.println("x2 value: " + vl.getVar("x2").getValue());
		System.out.println("x3: " + vl.getVar("x3"));
		System.out.println("x4: " + vl.getVar("x4"));
		System.out.println("x5: " + vl.getVar("x5"));
		System.out.println("x5 dependents: " + vl.getVar("x5").getArgs().getNames());
		System.out.println("x6: " + vl.getVar("x6"));
		System.out.println("x6 dependents: " + vl.getVar("x6").getArgs().getNames());
		
		System.out.println("x5 indepenedent of x5 :" + vl.getVar("x5").getIndependentVar("x5"));
		System.out.println("x3 indepenedent of x1 :" + vl.getVar("x1").getIndependentVar("x3"));
		System.out.println("x5 indepenedent of x1 :" + vl.getVar("x1").getIndependentVar("x5"));
		System.out.println("x2 indepenedent of x1 :" + vl.getVar("x1").getIndependentVar("x2"));
	}
	
}
