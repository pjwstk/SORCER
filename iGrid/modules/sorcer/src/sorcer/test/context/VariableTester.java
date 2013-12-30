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

package sorcer.test.context;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import sorcer.core.RemoteEvaluation;
import sorcer.core.context.node.ContextNode;
import sorcer.core.provider.eval.EvaluationProvider;
import sorcer.service.Context;
import sorcer.service.EvaluationException;
import sorcer.service.ServiceEvaluation;
import sorcer.util.Log;
import sorcer.util.Sorcer;
import sorcer.vfe.Distribution;
import sorcer.vfe.ServiceEvaluator;
import sorcer.vfe.Var;
import sorcer.vfe.VarException;
import sorcer.vfe.Variability.Type;
import sorcer.vfe.evaluator.ExertionEvaluator;
import sorcer.vfe.evaluator.ExpressionEvaluator;
import sorcer.vfe.evaluator.JepEvaluator;
import sorcer.vfe.evaluator.MethodEvaluator;
import engineering.core.design.CadDescription;
import engineering.core.design.CaeConstants;
import engineering.core.design.Dependency;
import engineering.core.design.IDragVLObjective;
import engineering.core.design.Objective;
import engineering.core.design.RealDesignVariable;
import engineering.core.design.ResponseVariable;
import engineering.core.reliability.GaussianDistribution;
import engineering.core.reliability.UnknownDistribution;
import engineering.provider.avus.AvusOutput;

/**
 * Example how use the Variables and Evaluators classes
 */
public class VariableTester {

	private static Logger logger = Log.getTestLog();

	/**
	 * @param args
	 * @throws ParException
	 */
	public static void main(String[] args) throws Exception {
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());
	//	logger.info("provider.lookup.accessor: "
		//		+ Env.getProperty("provider.lookup.accessor"));
		// VariableTester.test1();
		// VariableTester.test2();
		// VariableTester.test3();
		// VariableTester.test4();
		// VariableTester.test5();
		// VariableTester.test6();
		// VariableTester.test7();
		// VariableTester.test8();
		// VariableTester.test9();
		//VariableTester.test10();
		//VariableTester.test11();
		//VariableTester.test12();
		//VariableTester.test13();
		VariableTester.test14();
	}

	public static void test1() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\ttest1");

		// expression e1 = x1 + x2

		Var e1 = new Var("e1");
		e1.setEvaluator(new JepEvaluator("x1 + x2"));
		logger.info("Variable e1: " + e1);
		ServiceEvaluator evalr_e1 = (ServiceEvaluator) e1.getEvaluator();
		logger.info("evalr_e1 = " + evalr_e1.describe());
		evalr_e1.addArg(new Var("x1", 1));
		evalr_e1.addArg(new Var("x2", 2));
		logger.info("setup evalr_e1 = " + evalr_e1.describe());

		logger.info("first: e1 value = " + e1.getValue());
		logger.info("second: e1 value = " + e1.getValue());

	}

	public static void test2() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest2");

		// expression x1 = y1 + y2
		Var x1 = new Var("x1", new JepEvaluator("y1 + y2"));
		e1.getEvaluator(new JepEvaluator("y1 + y2"));
		logger.info("Variable x1: " + x1);
		ServiceEvaluator evalr_x1 = (ServiceEvaluator) x1.getEvaluator();
		logger.info("evalr_x1 = " + evalr_x1.describe());
		Var y1 = new Var("y1", 11.0);
		evalr_x1.addArg(y1);
		evalr_x1.addArg(new Var("y2", 12.0));
		logger.info("setup evalr_x1 = " + evalr_x1.describe());
		logger.info(">>>>>>>>>>>>>>> x1 value = " + x1.getValue());

		// expression x2 = z1 + z2
		Var x2 = new Var("x2", new JepEvaluator("z1 + z2"));
		x2.notifySelectEvaluation(new JepEvaluator("z1 + z2"));
		logger.info("Variable x2: " + x2);
		ServiceEvaluator evalr_x2 = (ServiceEvaluator) x2.getEvaluator();
		logger.info("evalr_x2 = " + evalr_x1.describe());
		evalr_x2.addArg(new Var("z1", 21.0));
		evalr_x2.addArg(new Var("z2", 22.0));
		logger.info("setup evalr_x2 = " + evalr_x2.describe());
		logger.info(">>>>>>>>>>>>>>> x2 value = " + x2.getValue());

		// expression e1 = x1 + x2
		Var e1 = new AspectVariable("e1", new JepEvaluator("x1 + x2"));
		e1.notifySelectEvaluation(new JepEvaluator("x1 + x2"));
		logger.info("Variable e1: " + e1);
		ServiceEvaluator evalr_e1 = (ServiceEvaluator) e1.getEvaluator();
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
		logger.info("\n\n\ttest3");

		// expression R1 = x5 + x6 + x7
		ExpressionEvaluator expr = new ExpressionEvaluator("x5 + x6 + x7", Type.JEP);
		Var x5 = new Var("x5", 1.0);
		Var x6 = new Var("x6", 1.0);
		Var x7 = new Var("x7", 1.0);
		expr.addArg(x5).addArg(x6).addArg(x7);
		ExertionEvaluator R1e = new ExertionEvaluator("R1", expr);
		Var R1 = new AspectVariable("R1", R1e);
		logger.info("setup R1e = " + R1e.describe());
		logger.info(">>>>>>>>>>>>>>> R1 value = " + R1.getValue());
		// no network execution the next time
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
		Var x1 = new AspectVariable("x1", 1.0);
		Var x2 = new AspectVariable("x2", 1.0);
		R2e.addArg(x1).addArg(x2);
		// Java way
		// R2e.setMethod("calculate", new Class[] { double.class, double.class
		// },
		// new Object[] { 1.0, 1.0 });
		Var R2 = new AspectVariable("R2", R2e);
		logger.info("setup R2e = " + R2e.describe());
		logger.info(">>>>>>>>>>>>>>> R2 value = " + R2.getValue());
		// no evaluate execution the next time
		logger.info(">>>>>>>>>>>>>>> R2 x1: " + x1.getValue());
		logger.info(">>>>>>>>>>>>>>> R2 x2: " + x2.getValue());
		logger.info(">>>>>>>>>>>>>>> R2 value = " + R2.getValue());
		// evaluate execution this time
		x1.setValue(10.0);
		logger.info(">>>>>>>>>>>>>>> R2 value = " + R2.getValue());
		// if target is the instance of methd class
		// logger.info(">>>>>>>>>>>>>>> target R2 value = " + target.getR2());

		// expression R3 = -23x3 + 7x4 with JepEvaluator
		Var R3 = new AspectVariable("R3", new JepEvaluator("-23*x3 + 7*x4"));
		ServiceEvaluator R3e = (ServiceEvaluator) R3.getEvaluator();
		Var x3 = new AspectVariable("x3", 1.0);
		R3e.addArg(x3);
		Var x4 = new AspectVariable("x4", 1.0);
		R3e.addArg(x4);
		logger.info("setup R3e = " + R3e.describe());
		logger.info(">>>>>>>>>>>>>>> R3 value = " + R3.getValue());
		// no evaluate execution the next time
		logger.info(">>>>>>>>>>>>>>> R3 value = " + R3.getValue());
		// evaluate execution this time
		x3.setValue(10.0);
		logger.info(">>>>>>>>>>>>>>> R3 value = " + R3.getValue());

		// expression R = R1 + R2 + R3
		Var R = new AspectVariable("R", new JepEvaluator("R1 + R2 + R3"));
		ServiceEvaluator Re = (ServiceEvaluator) R.getEvaluator();
		logger.info("Re = " + Re.describe());
		Re.addArg(R1);
		Re.addArg(R2);
		Re.addArg(R3);
		logger.info("setup Re = " + Re.describe());
		logger.info(">>>>>>>>>>>>>>> R value = " + R.getValue());

	}

	public static void test4() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest4");

		// expression R1 = x5 + x6 + x7
		ExpressionEvaluator expr = new ExpressionEvaluator("x5 + x6 + x7");
		Var x5 = new AspectVariable("x5", 1.0);
		Var x6 = new AspectVariable("x6", 1.0);
		Var x7 = new AspectVariable("x7", 1.0);
		expr.addArg(x5).addArg(x6).addArg(x7);
		ExertionEvaluator R1e = new ExertionEvaluator("R1", expr);
		Context context = R1e.getExertion().getContext();
		logger.info(">>>>>>>>>>>>>>> evaluator context = " + context);
		RemoteEvaluation provider = new EvaluationProvider();
		context = ((ServiceEvaluation) provider).evaluate(context);
		logger.info(">>>>>>>>>>>>>>> R1 context = " + context);

		x5.setValue(10);
		context = ((ServiceEvaluation) provider).evaluate(context);
		logger.info("2nd>>>>>>>>>>>>>>> R1 context = " + context);

	}

	public static void test5() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest5");

		// expression R1 = x5 + x6 + x7
		ExpressionEvaluator expr = new ExpressionEvaluator("x5 + x6 + x7", Type.JEP);
		Var x5 = new AspectVariable("x5", 1.0);
		Var x6 = new AspectVariable("x6", 1.0);
		Var x7 = new AspectVariable("x7", 1.0);
		expr.addArg(x5).addArg(x6).addArg(x7);
		ExertionEvaluator R1e = new ExertionEvaluator("R1", expr);
		Var R1 = new AspectVariable("R1", R1e);
		logger.info("setup R1e = " + R1e.describe());
		logger.info(">>>>>>>>>>>>>>> R1 value = " + R1.getValue());
		// no network execution the next time
		logger.info(">>>>>>>>>>>>>>> R1 value = " + R1.getValue());
		// network execution this time
		x5.setValue(10.0);
		logger.info(">>>>>>>>>>>>>>> R1 value = " + R1.getValue());
	}

	public static void test6() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest6");

		// expression R1 = x5 + x6 + x7
		ExpressionEvaluator expr = new ExpressionEvaluator("x5 + x6 + x7", Type.JEP);
		Var x5 = new AspectVariable("x5", 1.0);
		Var x6 = new AspectVariable("x6", 1.0);
		Var x7 = new AspectVariable("x7", 1.0);
		expr.addArg(x5).addArg(x6).addArg(x7);
		logger.info("\n\tsetup expr = " + expr.describe());
		logger.info("1st >>>>>>>>>>>>>>> expr value = " + expr.getValue());
		// no network execution the next time
		logger.info("2nd >>>>>>>>>>>>>>> expr value = " + expr.getValue());
		// network execution this time
		x5.setValue(10.0);
		logger.info("x5 changed >>>>>>>>>>>>>>> expr value = "
				+ expr.getValue());
	}

	public static void test7() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest7");

		// expression R1 = x5 + x6 + x7
		ExpressionEvaluator expr = new ExpressionEvaluator("R1", "x5 + x6 + x7");
		expr.addVar("x5", 1.0);
		expr.addVar("x6", 1.0);
		expr.addVar("x7", 1.0);
		logger.info("\n\tsetup expr = " + expr.describe());
		logger.info("1st >>>>>>>>>>>>>>> expr value = " + expr.getValue());
		// no network execution the next time
		logger.info("2nd >>>>>>>>>>>>>>> expr value = " + expr.getValue());
		// network execution this time
		Var x5 = expr.getVariable("x5");
		x5.setValue(10.0);
		logger.info("x5 changed >>>>>>>>>>>>>>> expr value = "
				+ expr.getValue());
		Var R1 = expr.getVariable();
		logger.info(">>>>>>>>>>>>>>> R1 value = " + R1.getValue());

		Var Rx = new AspectVariable("Rx", expr);
		logger.info(">>>>>>>>>>>>>>> Rx value = " + Rx.getValue());
	}

	public static void test8() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest8");
		MethodEvaluator R2e = new MethodEvaluator(
				"sorcer.test.context.ResponseVariable2", "calculateVars");
		Var x1 = new AspectVariable("x1", 1.0);
		Var x2 = new AspectVariable("x2", 1.0);
		R2e.addArg(x1).addArg(x2);
		Var R2 = new AspectVariable("R2", R2e);
		logger.info("1st >>>>>>>>>>>>>>> R2 value = " + R2.getValue());
		// evaluate execution this time
		x1.setValue(10.0);
		logger.info("2nd >>>>>>>>>>>>>>> R2 value = " + R2.getValue());

		// field name: String name in ResponseVariable2
		R2.setEventInfo("name");
		logger.info("3nd >>>>>>>>>>>>>>> name field= " + R2.getValue());

		R2.setValue("Sobolewski");
		logger.info("4nd >>>>>>>>>>>>>>> name field= " + R2.getValue());

		// field name: double R2 in ResponseVariable2
		R2.setEventInfo("R2");
		logger.info("5nd >>>>>>>>>>>>>>> R2 field= " + R2.getValue());

		R2.setValue(100.0);
		logger.info("6nd >>>>>>>>>>>>>>> R2 field= " + R2.getValue());
	}

	public static void test9() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest9");
		ExpressionEvaluator expr = new ExpressionEvaluator("x5 + x6 + x7", Evaluator.ServiceEvaluator.JEP);
		Var x5 = new Var("x5", 1.0);
		Var x6 = new Var("x6", 1.0);
		Var x7 = new Var("x7", 1.0);
		expr.addArg(x5).addArg(x6).addArg(x7);

		String item1 = "obj1";
		String item2 = "obj2";
		ContextNode objCN = new ContextNode("JEP node", expr);

		Vector iFilter = new Vector();
		iFilter.addElement("JEP");
		objCN.addItem(item1, iFilter);
		objCN.addItem(item2, iFilter);

		Var objV = null;
		Objective obj = null;

		UnknownDistribution unkndist = new UnknownDistribution(
				new Double("0.0"));
		CadDescription cD = new CadDescription(CaeConstants.SHAPE);
		System.out.println("creating basic variable ");
		objV = new Var(item1, item1, objCN, Variable.Var, true,
				cD, unkndist);
		System.out.println("after creating basic variable ");
		// System.out.println("objV BasicVariable aspect = " +
		// ((BasicVariable)objV).getAspect());
		// System.out.println("objV BasicVariable value = " + objV.getValue());
		obj = new Objective(item1, true, objV, Distribution.REALIZATION);
		System.out.println("before get Objective value ");
		System.out.println("Objective Value = " + obj.getValue());
		System.out.println("after get Objective value ");
		System.out.println("Objective Name = " + obj.getName());

		logger.info("1st >>>>>>>>>>>>>>> R2 value = " + objV.getValue());
		// evaluate execution this time
		x5.setValue(10.0);
		logger.info("2nd >>>>>>>>>>>>>>> R2 value = " + objV.getValue());

		// field name: String name in ResponseVariable2
		logger.info("3nd >>>>>>>>>>>>>>> name field= " + objV.getValue());
	}

	public static void test10() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest10");

		// expression R2 = -5x1 - 5x2
		ResponseVariable2 rv = new ResponseVariable2();
		MethodEvaluator R2e = new MethodEvaluator(rv);
		// Java way with formal parameters used
		R2e.setSignature("calculate", new Class[] { double.class, double.class },
				new Object[] { 1.0, 1.0 });
		Var R2 = new AspectVariable("R2", R2e);
		logger.info(">>>>>>>>>>>>>>> R2 value = " + R2.getValue());
	}

	public static void test11() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest11");

		// expression R2 = -5x1 - 5x2
		ResponseVariable2 rv = new ResponseVariable2();
		MethodEvaluator R2e = new MethodEvaluator(rv);
		// Java way for the invocation without formal parameters
		// R2e.setMethod("calculate", new Class[] {}, new Object[] {});
		// or another way with no foraml parameters for the invoked method
		R2e.setSelector("calculate");
		Var R2 = new AspectVariable("R2", R2e);
		logger.info(">>>>>>>>>>>>>>> R2 value = " + R2.getValue());
	}

	public static void test12() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\n\ttest12");

		// expression R2 = -5x1 - 5x2
		// A method invocation with formal parameters being Variables
		ResponseVariable2 rv = new ResponseVariable2();
		MethodEvaluator R2e = new MethodEvaluator(rv);
		R2e.setSelector("calculateVars");
		Var x1 = new AspectVariable("x1", 1.0);
		Var x2 = new AspectVariable("x2", 1.0);
		R2e.addArg(x1).addArg(x2);
		Var R2 = new AspectVariable("R2", R2e);
		logger.info(">>>>>>>>>>>>>>> R2 value = " + R2.getValue());
	}
	public static void test13() throws ParException, RemoteException,
	EvaluationException {
		// Set and get of a Variable that references a dataItem in a contextNode and hence file.
		String basedataURL = "http://dna.flight.wpafb.af.mil:9000/";
		String avusPath = "app/avus/goland/naca0006/mp3alpha5p0";
		//String basedataURL = "http://localhost:9009/";
		//String avusPath = "testdata";
		String dataURL = basedataURL + avusPath;
		System.out.println("dataURL = " + dataURL);
		// File Context Node for BCinputFile
		String bcInputFileName = "avusBC.cbc";
		try {
			URL dataURLF = new URL(dataURL + "/" + bcInputFileName);
			
			// create the context node that holds the URL to the file
			ContextNode FCN1= new ContextNode("AvusBCData", dataURLF, "engineering.avus.bcTxt");
			
			// create the data item within the context node.		
				// First create the file filter 
				Vector filter = new Vector();
				String filterName = "cs1";
				filter.addElement("File");
				filter.addElement("Double");
				//line
				filter.addElement(new Integer("149"));
				//field
				filter.addElement(new Integer("8"));
				//delimeter
				filter.addElement(" ");
				
				// add the item to the contextnode
				FCN1.addItem(filterName, filter);
				System.out.println("Context Node = "+FCN1);
				// create the basic variable
				GaussianDistribution gD = new GaussianDistribution(
						new Double("0.0"), new Double("0.0"), new Double("0.1"));
				CadDescription cD = new CadDescription(CaeConstants.SHAPE);
				AspectVariable bVar = new AspectVariable("cs1", FCN1,
						Variable.Var, true, cD, gD);
//				BasicVariable bVar = new BasicVariable("cs1", filterName, FCN1,
//						Variable.DOUBLE, true, cD,  gD, new Double("0.0"));
				
				// Should now be able to get and set the variable value. 
				// A get should obtain the value from the file
				// and a set should write a new value to the file. 
				// These methods should be calling the getItemValue and setItemValue
				// in ContextNode. 
				// get Value (file holds value = 5.15 ) this is what should be returned.
				bVar.setEventInfo(filterName);
				System.out.println("ValueMap = "+bVar.getValueMap());
				System.out.println("Basic Variable aspect = "+bVar.getEventInfo());
				System.out.println(" Basic Variable Value = "+bVar.getValue());
				
				// set the Value
				bVar.setValue(4.14159);
				System.out.println(" Basic Variable Value after set Value = "+bVar.getValue());
				// avusBC.cbc file should now contain the value 3.14159 (you must check the file) Doing a getValue does not
				// confirm that the file has been updated.
				
				// Now need to create a RealDesignVariable and associate it with the BasicVariable created above
				// Not sure how to do this. In the past a RealDesignVariable was created then I would use the .addDependency 
				// method in Variable and pass the associated RealDesignVariable as an argument.
//				RealDesignVariable rDV = new RealDesignVariable(bVar.getName(),(Double)bVar.getValue(), new Double("1.0"),
//																new Double("1.0"),new Double("1.0"));
				
				//Check the sematincs of parameters: upperBound, lowerBound, characteristicValue
				// or the last 1.0 is step size?
				RealDesignVariable rDV = new RealDesignVariable("cs2", bVar, 1.0, 1.0, 1.0);
				//by default the aspect is inherited from bVar, but can be changed to another item name
				//rDV.setAspect(filterName);
				System.out.println(" Real Design Variable Value = " + rDV.getValue());
				rDV.setValue(11.14159);
				System.out.println(" Real Design Value after set Value = " + rDV.getValue());
				
				// make the evaluator (ContextNode) of rDV associated with a local copy of a file in Env.getDataPath()
				System.out.println("Evaluator Type "+rDV.getEvaluator().getClass().getName());
				((ContextNode)rDV.getEvaluator()).getLocalFileCopyIn(Sorcer.getCanonicalDataDir());
				rDV.setValue(15.14159);
				System.out.println(" Real Design Variable Value 1 from local file = " + rDV.getValue());
				
				System.out.println(" Real Design Variable Value 2 from local file = " + rDV.getValue());
				
				System.out.println(" Real Design Variable Value 3 from local file = " + rDV.getValue());
				
				rDV.setValue(16.14159);
				
				System.out.println(" Real Design Variable Value 4 from local file = " + rDV.getValue());
				
				System.out.println(" Real Design Variable Value 5 from local file = " + rDV.getValue());
				
				System.out.println(" Real Design Variable Value 6 from local file = " + rDV.getValue());	
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}		
		public static void test14() throws ParException, RemoteException,
		EvaluationException {
			// Set and get of a Variable that references a dataItem in a contextNode and hence file.
			String basedataURL = "http://dna.flight.wpafb.af.mil:9000/";
			String avusPath = "app/avus/goland/naca0006/mp3alpha5p0";
			//String basedataURL = "http://localhost:9009/";
			//String avusPath = "testdata";
			String dataURL = basedataURL + avusPath;
			System.out.println("dataURL = " + dataURL);
			// File Context Node for BCinputFile
				File avusOutputFile = new File(
				"/home/kolonarm/workspace/iGrid-06/data/avus/goland/naca0006/AvusOutput.out");
				AvusOutput avusOut;
				File avusOutputFile2 = new File(
				"/home/kolonarm/workspace/iGrid-06/data/avus/goland/naca0006/AvusOutputMp85.out");
				AvusOutput avusOut2;
				try {
					avusOut2 = new AvusOutput(avusOutputFile2);
					avusOut = new AvusOutput(avusOutputFile);
					MethodEvaluator[] evaluatorLpus = new MethodEvaluator[20];
					Var[] varLpus = new Var[20];
					ResponseVariable[] lpusRespVar = new ResponseVariable[20];
					for (int i=0; i< 20; i++){
				 evaluatorLpus[i] = new MethodEvaluator(
						avusOut);
				 evaluatorLpus[i].setSignature("getWindAxisLpus", new Class[]{ Integer.class}, new Object[]{i}); 
				 varLpus[i]  = new AspectVariable("Lpus"+i, evaluatorLpus[i]);
				  lpusRespVar[i] = new ResponseVariable("Lpus"+i,
							varLpus[i], Distribution.REALIZATION);
					System.out.println(">>>>>>>>>> LpusiRespVar = "+lpusRespVar[i].getName()+", "+lpusRespVar[i].getValue());
					}
					Objective objIDragVL = new IDragVLObjective("IDragVL", true,
							lpusRespVar[0].getVariable(), Distribution.REALIZATION,
							lpusRespVar, lpusRespVar, null, null);
					// construct the method evaluator to expose the IDrag
					MethodEvaluator iDragVLME = new MethodEvaluator(
							objIDragVL);
					 iDragVLME.setSelector("getValue"); 
					 Var varIDragVL  = new AspectVariable("iDragVL", iDragVLME);
					  ResponseVariable iDragVLRV = new ResponseVariable("iDragVL",
								varIDragVL, Distribution.REALIZATION);
					  System.out.println(">>>>>>>>>> iDragVLRV = "+iDragVLRV.getName()+", "+iDragVLRV.getValue());
					  ServiceEvaluator eval = (ServiceEvaluator)iDragVLRV.getEvaluator();
					  if (eval instanceof MethodEvaluator){
						  Object targ = ((MethodEvaluator)eval).getTarget();
						  if (targ instanceof Dependency){
							  System.out.println(" getResponseVariable on targ "+((Dependency)targ).getRespnseVariable());
							  System.out.println(" getResponseVariable on targ1 "+(((Dependency)targ).getRespnseVariable()).getClass().getName() );
							 List<ResponseVariable>  respV = ((Dependency)targ).getResponseVaribales();
							 ResponseVariable rsp = respV.get(0);
							 System.out.println("respv0 = "+rsp);
							 System.out.println("size = "+respV.size());
							 for (int j=0; j<respV.size();j++){
								 ServiceEvaluator evali = (ServiceEvaluator)respV.get(j).getEvaluator();
								 System.out.println("evalj name = "+evali.getClass().getName());
								 if (evali instanceof MethodEvaluator){
									 Object targi = ((MethodEvaluator)evali).getTarget();
									 System.out.println("targi name = "+targi.getClass().getName());
									 if (targi instanceof AvusOutput){
										 System.out.println("found AvusOutput!"+j);
										 if (j==5){
											 System.out.println("old value = "+respV.get(j).getValue());
											 ((MethodEvaluator)evali).setTarget(avusOut2);
										 System.out.println("new value = "+respV.get(j).getValue());
										 }
									 }
								 }
							 }
						  }
					  }
				// create the context node that holds the URL to the file
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
	}
}
