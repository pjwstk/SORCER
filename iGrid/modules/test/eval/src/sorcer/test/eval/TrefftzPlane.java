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

import static java.lang.System.out;
import static sorcer.co.operator.entry;
import static sorcer.co.operator.list;
import static sorcer.co.operator.loop;
import static sorcer.co.operator.map;
import static sorcer.co.operator.names;
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
import static sorcer.vo.operator.fdEvaluator;
import static sorcer.vo.operator.gradient;
import static sorcer.vo.operator.linkedVars;
import static sorcer.vo.operator.objectiveVars;
import static sorcer.vo.operator.optimizationModel;
import static sorcer.vo.operator.realization;
import static sorcer.vo.operator.soaEvaluator;
import static sorcer.vo.operator.var;
import static sorcer.vo.operator.vars;
import static sorcer.vo.operator.wrt;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import net.jini.config.ConfigurationException;
import sorcer.core.SorcerConstants;
import sorcer.core.context.ServiceContext;
import sorcer.core.context.model.opti.OptimizationModel;
import sorcer.core.context.model.var.FidelityInfo;
import sorcer.core.context.model.var.Realization;
import sorcer.core.context.model.var.ResponseModel;
import sorcer.core.context.model.var.VarModel;
import sorcer.core.context.node.ContextNode;
import sorcer.core.exertion.NetTask;
import sorcer.core.signature.NetSignature;
import sorcer.service.ContextException;
import sorcer.service.EvaluationException;
import sorcer.service.ExertionException;
import sorcer.service.Signature;
import sorcer.service.SignatureException;
import sorcer.service.Task;
import sorcer.util.Log;
import sorcer.util.Sorcer;
import sorcer.vfe.ServiceEvaluator;
import sorcer.vfe.Filter;
import sorcer.vfe.Var;
import sorcer.vfe.VarException;
import sorcer.vfe.VarInfo.Relation;
import sorcer.vfe.VarInfo.Target;
import sorcer.vfe.evaluator.MethodEvaluator;
import sorcer.vfe.evaluator.SoaEvaluator;
import sorcer.vfe.filter.BasicFileFilter;
import sorcer.vfe.filter.BasicFileFilter.BasicPattern;
import sorcer.vfe.filter.ContextFilter;
import sorcer.vfe.filter.ListFilter;
import sorcer.vfe.filter.ObjectFieldFilter;
import sorcer.vfe.filter.PatternFilter.Pattern;
import sorcer.vfe.util.VarList;

/**
 * Example on how to create a model for AVUS-based application
 */
@SuppressWarnings("unchecked")
public class TrefftzPlane implements SorcerConstants, AvusConstants {

	private static Logger logger = Log.getTestLog();
	private static Properties properties;
	
	public static void main(String[] args) throws Exception {
		out.println(">>>>>>>>>>> Using requestor.properties: " + System.getProperty("requestor.properties"));
		properties = Sorcer.loadProperties(System.getProperty("requestor.properties"));
		int test = new Integer(args[0]);
		switch (test) {
			case 1: doResponseAnalysis(); break;
			case 2: doOptimization(); break;
		}
	}

	public static void doResponseAnalysis() throws Exception {

		OptimizationModel om = getOptimizationModel();
		configureOptimizationModel(om);
		configureSensitivities(om);
		logger.info("\n\nInduced Drag model: " + om);
		
		System.out.println("avus model design vars: " + om.getDesignVars().getNames());
		System.out.println("avus model response vars: " + om.getOutputVars().getNames());
		System.out.println("avus model objective vars: " + om.getObjectiveVars().getNames());
		System.out.println("avus model constraint vars: " + om.getConstraintVars().getNames());
		Realization DIr = om.getVar("DI").getRealization();
		System.out.println("Induced Drag model DI : " + DIr);
		List<FidelityInfo> DIes = DIr.getEvaluations();
		for (FidelityInfo e : DIes)
			System.out.println("\nDI evaluation: " + e);
		
		Realization Lpus9 = om.getVar("Lpus9").getRealization();
		System.out.println("Induced Drag model Lpus9 : " + Lpus9);
		List<FidelityInfo> Lpus9es = Lpus9.getEvaluations();
		for (FidelityInfo e : Lpus9es)
			System.out.println("\nLpus9 evaluation: " + e);
	}

	public static OptimizationModel getOptimizationModel() throws ParException,
			RemoteException, EvaluationException, ConfigurationException,
			ContextException, ExertionException, SignatureException {

		OptimizationModel omodel = optimizationModel("Induced Drag", 
			designVars(vars(loop("i",1,20),"beta$i$", 0.0, -10.0, 10.0)), 
			linkedVars(names(loop("i",21,40),"betal$i$")),
			designVars(var("alpha", 5.0, -5.0, 10.0)),
			designVars("mach", "gamma", "pstatic"),

			outputVars(var("DI",
				realization(
					evaluation("DIExacte", 
						differentiation(wrt(names(loop("i",1,20),"beta$i$"), "alpha","q"), gradient("DIExacteg1"))), 
				    evaluation("DISOAe", 
				        differentiation(wrt(names(loop("i",1,20),"beta$i$"), "alpha","q"), gradient("DISOAeg1"))), 
				    evaluation("DIMOAe", 
				    	differentiation(wrt(names(loop("i",1,20),"beta$i$"), "alpha","q"), gradient("DIMOAeg1"))),
				    evaluation("DIKrige",
				      	differentiation(wrt(names(loop("i",1,20),"beta$i$"), "alpha","q"), gradient("DIKrigeg1")))))),

			outputVars(var("LT",
				realization(
					evaluation("LTExacte", 
						differentiation(wrt(names(loop("i",1,20),"beta$i$"), "alpha","q"), gradient("LTExacteg1"))), 
					evaluation("LTSOAe", 
				        differentiation(wrt(names(loop("i",1,20),"beta$i$"), "alpha","q"), gradient("LTSOAeg1"))), 
				    evaluation("LTMOAe", 
				        differentiation(wrt(names(loop("i",1,20),"beta$i$"), "alpha","q"), gradient("LTMOAeg1")))))),
				  
			outputVars(loop("i",1,20),"Lpus$i$",
				realization( 
				    evaluation("Lpus$i$Exacte", 
				         differentiation(wrt(names(loop("k",1,20),"beta$k$"), "alpha"), gradient("Lpus$i$Exacteg1"))),     
				    evaluation("Lpus$i$SOAe", 
				         differentiation(wrt(names(loop("k",1,20),"beta$k$"), "alpha"), gradient("Lpus$i$SOAeg1"))))),     

			outputVars(var("q",
				realization(evaluation("qExact", differentiation(wrt("mach","gamma","pstatic"), gradient("qExactg1")))))),

			objectiveVars(var("DIo", "DI", Target.min )),
			constraintVars(var("LTc", "LT", Relation.eq, 1000.0)));
		
			System.out.println("Induced Drag optimization model: " + omodel);
		
		return omodel;

	}
	
	private static OptimizationModel configureOptimizationModel(
			OptimizationModel omodel) throws Exception {
		
	// configure the design variables
		// first configure the filters for the betai, alpha, Mach, gamma, and pstatic
		// create the patterns for the filters of beta vars
		Pattern beta1p = new BasicPattern("beta1", "File", "Double", 192, 6, " ");
		Pattern beta2p = new BasicPattern("beta2", "File", "Double", 200, 6, " " );
		Pattern beta3p = new BasicPattern("beta3", "File", "Double", 208, 6, " " );
		Pattern beta4p = new BasicPattern("beta4", "File", "Double", 216, 6, " " );
		Pattern beta5p = new BasicPattern("beta5", "File", "Double", 224, 6, " " );
		Pattern beta6p = new BasicPattern("beta6", "File", "Double", 232, 6, " " );
		Pattern beta7p = new BasicPattern("beta7", "File", "Double", 240, 6, " " );
		Pattern beta8p = new BasicPattern("beta8", "File", "Double", 248, 6, " " );
		Pattern beta9p = new BasicPattern("beta9", "File", "Double", 256, 6, " " );
		Pattern beta10p = new BasicPattern("beta10", "File", "Double", 264, 6, " " );
		Pattern beta11p = new BasicPattern("beta11", "File", "Double", 272, 6, " " );
		Pattern beta12p = new BasicPattern("beta12", "File", "Double", 280, 6, " " );
		Pattern beta13p = new BasicPattern("beta13", "File", "Double", 288, 6, " " );
		Pattern beta14p = new BasicPattern("beta14", "File", "Double", 296, 6, " " );
		Pattern beta15p = new BasicPattern("beta15", "File", "Double", 304, 6, " " );
		Pattern beta16p = new BasicPattern("beta16", "File", "Double", 312, 6, " " );
		Pattern beta17p = new BasicPattern("beta17", "File", "Double", 320, 6, " " );
		Pattern beta18p = new BasicPattern("beta18", "File", "Double", 328, 6, " " );
		Pattern beta19p = new BasicPattern("beta19", "File", "Double", 336, 6, " " );
		Pattern beta20p = new BasicPattern("beta20", "File", "Double", 344, 6, " " );
		
//		List<Pattern> bpl = list(beta1p, beta2p, beta3p, beta4p, beta5p, beta6p, beta7p,
//				beta8p, beta9p, beta10p, beta11p, beta12p, beta13p, beta14p, beta15p, 
//				beta16p, beta17p, beta18p, beta19p, beta20p);
		
		Map<String, Pattern> bpm = map(entry("beta1", beta1p), entry("beta2" ,beta2p), entry("beta3", beta3p), 
				entry("beta4", beta4p), entry("beta5", beta5p), entry("beta6", beta6p), entry("beta7", beta7p),
				entry("beta8", beta8p), entry("beta9", beta9p), entry("beta10", beta10p), entry("beta11", beta11p), 
				entry("beta12", beta12p), entry("beta13",beta13p), entry("beta14", beta14p), entry("beta15", beta15p), 
				entry("beta16", beta16p), entry("beta17", beta17p), entry("beta18", beta18p), 
				entry("beta19", beta19p), entry("beta20", beta20p));
		
		//URL betaURL = model.getProperty("property");	
		URL betaURL = null;
//		List<Filter> bfl = list();
//		for (Object bp : bpl)
//			bfl.add(new BasicFileFilter(betaURL, (BasicPattern) bp));

		Map<String,Filter> bfm = map();
		for (String bp : bpm.keySet())
			bfm.put(bp, new BasicFileFilter(betaURL, (BasicPattern) bpm.get(bp)));
		
		//configure the betai design variables
		//vars(omodel, names(loop("i",1,20), "beta$i$"), bfl);
		vars(omodel, names(loop("i",1,20), "beta$i$"), bfm);
		

	    Pattern alphap = new BasicPattern("alpha", "File", "Double", 98, 2, " ");
		Pattern machp = new BasicPattern("mach", "File", "Double", 198, 3, " "); 
		Pattern gammap = new BasicPattern("gamma", "File", "Double", 298, 4, " ");
		Pattern pstaticp = new BasicPattern("pstatic", "File", "Double", 398, 4, " ");

		//URL avusjobURL = model.getPropert("property");
		URL avusjobURL = null;
		
		BasicFileFilter abff = new BasicFileFilter(avusjobURL, alphap);
		BasicFileFilter machbff = new BasicFileFilter(avusjobURL, machp);
		BasicFileFilter gammabff = new BasicFileFilter(avusjobURL, gammap);
		BasicFileFilter pstaticbff = new BasicFileFilter(avusjobURL,pstaticp);

		// design variables configuration for alpha, mach, gamma, and pstatic
		vars(omodel, list("alpha", "mach", "gamma", "pstatic"), list(abff, machbff, gammabff, pstaticbff)) ;
		
		// configure linked variables. linking betal21 beta1 all the way to betal40 to beta20 	
		VarList betaList = vars(vars(omodel, loop("i", 21, 40), "betal$i$"), 
				evaluators(omodel, loop(list("i:21", "k:1"), 20), "betal$i$","1.0*beta$k$", args("beta$k$")));
		
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> variables in betaList: " + betaList);

	// configure the evaluators
	// LpusiExacte
		// configure the evaluator for the lpusi. This is a exertion evaluator with three filters.
		// exertion evaluator (avus task)
		Task avusTask = getAvusTask();
		ServiceEvaluator avusEvaluator = avusTask.getEvaluator();
		// construct the filter pipeline for filtering a single lpusi. ContextFilter,ObjectFilter,ListFilteri

		// construct the ContextFilter to get the object from the task contextf
		ContextFilter contextFilter = new ContextFilter("avusTaskContext", "out/value/AVUS/AVUSOUTPUT");
		// construct the ObjectFilter to extract the windAxisLpus which holds an array Lpusi
		ObjectFieldFilter objFilter = new ObjectFieldFilter("windAxisLpus");
		//construct 20 List Filters and use them to complete the pipeline of filters  
		List<Filter> plFlpusi = new ArrayList<Filter>(20);
		for (int i = 0; i<plFlpusi.size(); i++)
				plFlpusi.add(new Filter("Lpusi"+i,contextFilter, objFilter, new ListFilter(i)));
		
		// configure the Lpusi
		VarList Lpusvl = vars((VarModel)omodel, names(loop("i",1,20),"Lpusi$i$"), 
				avusEvaluator, plFlpusi, args(names(loop("k",1,20),"beta$k$"), list("alpha")));
		
	// dLpusiExactedBeta&alpha
		// configure the Derivative Evaluators for Lpusi
		// Since the evaluator is always the same for all Lpusi we get the first one and use that for all FD evaluators
		ServiceEvaluator lpusiEval = outputVar(omodel,"Lpus1").getEvaluator("Lpus1Exacte");
		List<ServiceEvaluator> fdEvals = new ArrayList<ServiceEvaluator>(21);
		for (int i=1; i<=fdEvals.size(); i++){
			String wrtName = "beta"+i;
			fdEvals.add(fdEvaluator("dLpusidBetaie1",lpusiEval,0.1,wrtName));
			if (i==fdEvals.size())fdEvals.add(fdEvaluator("dLpusidBetaie1",lpusiEval,0.1,"alpha"));
		}
		// add the list (gradient vector) to each Lpusi. 
				for (int j=1; j<=20; j++){
					String varName = "Lpus"+j;
					String varEvalName = "Lpus"+j+"Exacte";
					String varGradEvalName = varEvalName+"g1";
					omodel.setGradientEvaluators(varName, varEvalName, varGradEvalName,  fdEvals);
		}
// Configure the LpusiSOAe Evaluator.
				// The SOA evaluator requires f0, gradf0, and x0
				// to compute f0, gradf0 use the LpusiExacte, x0 are the current values for betai and alpha

				// get the x0 for SOA (it is the same for all Lpusi)
				double[] x0 = new double[21];
				for (int i = 0; i<20; i++)x0[i]=(Double)omodel.getInputValue("beta"+i);
				x0[21]=(Double)omodel.getVarValue("alpha");
				
				
				// get the gradf0 for SOA for each Lpusi
				
				// create a SOA Evaluator for each Lpusi and add it to the model. Also create the Lpusi Gradient Evaluators as well.
				double[] gradf0d = new double[20];
				for (int k=0; k<20; k++){
					
					// get the f0 for this  Lpusi SOA
					double lpusi0 = (Double)omodel.getResponseValue("Lpusi"+k,"Lpus"+k+"Exacte");
					
					// get the gradf0 for this Lpusi SOA
					 Double[] gradf0 = (Double[])omodel.getDesignDerivativeTable("Lpus"+k,"Lpus"+k+"Exacteg1").getValues("0").toArray();
					 for (int i =0; i<gradf0.length; i++)gradf0d[i]=gradf0[i];
					 
					 SoaEvaluator eval = soaEvaluator(x0, lpusi0, gradf0d);
					 var(omodel,"Lpus"+k,"Lpus"+k+"SOAe",eval,args(names(loop("k",1,20),"beta$k$"), list("alpha")));
					
					 // gradient Evaluators for this LpusiSOAe
					 List<ServiceEvaluator> dLpusiSOAedBetajg1 = new ArrayList<ServiceEvaluator>(21);
					 for (int j = 0; j<=20; j++){
						 dLpusiSOAedBetajg1.add(evaluator("dLpus"+k+"SOAedBeta"+j+"g1", gradf0[j].toString()));
					 }
					 // add the list of gradient evaluators to the model
					 omodel.setGradientEvaluators("Lpus"+k,"Lpus"+k+"SOAe","dLpus"+k+"SOAedBetajg1",dLpusiSOAedBetajg1);
				}
				// if one wishes to update a SOA with a new expansion point the below syntax is used.
				//update(eval, 10.0, new double[] { 2.0, 3.0, 4.0 }, new double[] { 2.0, 3.0, 40 });

		// Configure the q response Variable
				var(omodel,"q","qExacte", evaluator("qExact","0.5*gamma*pstatic*mach^2"),args("gamma","pstatic","mach"));
		// Configure the q response Variable gradients
				ServiceEvaluator dqdgammag1 = expression("dqdgammag1","0.5*pstatic*mach^2",args(designVars(omodel,"pstatic","mach")));
				ServiceEvaluator dqdpstaticg1 = expression("dqdpstaticg1","0.5*gamma*mach^2",args(designVars(omodel,"gamma","mach")));
				ServiceEvaluator dqdmachg1 = expression("dqdmachg1","gamma*pstatic*mach",args(designVars(omodel,"pstatic","gamma","mach")));
				List<ServiceEvaluator> qg1 = list(dqdgammag1, dqdpstaticg1, dqdmachg1);	
				omodel.setGradientEvaluators("q", "gExacte", "qExacteg1",  qg1);
				
		// Configure the DI response Variable. Recall that there are 4 Evaluators for DI. DIExacte, DISOAe, DIMOAe, and DIKrige. 

			// DIExacte Configuration - MethodEvaluator
				
				// first construct the InducedDrag object
				// Construct the iDrag Evaluator
				Double[] yiA = new Double[] { 0.5, 1.5, 2.5, 3.5, 4.5, 5.5, 6.5, 7.5, 8.5,
						9.5, 10.5, 11.5, 12.5, 13.5, 14.5, 15.5, 16.5, 17.5, 18.5, 19.5 };
				InducedDrag idragObj = new InducedDrag("avusIdrag", yiA);
				MethodEvaluator iDragMethodEval = new MethodEvaluator("iDragEvaluator", idragObj, "evaluateIDrag");
				Lpusvl.add(omodel.getVar("q"));
				iDragMethodEval.setVars(Lpusvl);
				
				var(omodel, "DI","DIExacte",iDragMethodEval, args(names(loop("k",1,20),"Lpus$k$"), list("q")));
				
			// dDIExactedLpusig1 Configuration - MethodEvaluator used by all 
				MethodEvaluator iDragSensitivityMethodEval = new MethodEvaluator("iDragSensitivitiesEvaluator", idragObj, "evaluateIDragSensitivities");
				iDragMethodEval.setVars(Lpusvl);
				
				// evaluateIDragSensitivities retuns a Double[]. First n are wrt Lpusi, last entry is wrt q. Need a list filter.
				List<Filter> dDIdLpusFilter = new ArrayList<Filter>(21);
				for (int i = 0; i<Lpusvl.size()+1; i++){
						dDIdLpusFilter.add(new Filter( new ListFilter(i)));
				}
				
				// configure the sensitivity evaluators
				List<ServiceEvaluator> dDIdLpusEval = new ArrayList<ServiceEvaluator>(21);
				for (int i = 0; i<Lpusvl.size(); i++){
					// problem. Why are the entries in the gradient vector evaluators instead of variables? They should be Variables. Since an evaluator may not
					// reduce to a single scalar that is necessary for a derivative calculation.
					// in this case I need to filter the result of this method evaluator iDragSensitivityMethodEval since it returns a vector.
						dDIdLpusEval.add(iDragSensitivityMethodEval);
				}		
					omodel.setGradientEvaluators("DI", "DIExacte", "dDIExactedLpusiqg1",  dDIdLpusEval);
		
					
		// DISOAe Configuration
					// The SOA evaluator requires f0, gradf0, and x0
					// to compute f0, gradf0 use the DIExacte, x0 are the current values for Lpusi and q

					// get the x0 for SOA
					double[] lpusi0 = new double[21];
					for (int i = 0; i<20; i++)lpusi0[i]=(Double)omodel.getResponseValue("Lpusi"+i,"Lpus"+i+"Exacte");
					lpusi0[21]=(Double)omodel.getResponseValue("q","qExacte");
					
					
					// get the gradf0 for SOA for each DI
					
					// create a SOA Evaluator for DI and add it to the model. Also create the dDISOA Gradient Evaluators as well.
					
						
						// get the f0 for this  Lpusi SOA
						double DI0 = (Double)omodel.getResponseValue("DI","DIExacte");
						
						// get the gradf0 for the DI for the SOA
						
						 Double[] gradDI0 = (Double[])omodel.getDesignDerivativeTable("DI","dDIExactedLpusiqg1").getValues("0").toArray();
						 double[] gradDI0d = new double[gradDI0.length];
						 for (int i =0; i<gradDI0.length; i++)gradf0d[i]=gradDI0[i];
						 SoaEvaluator DISOAe = soaEvaluator(lpusi0, DI0, gradDI0d);
						 var(omodel,"DI","DISOAe",DISOAe,args(names(loop("k",1,20),"Lpusi$k$"), list("q")));
						
						 // gradient Evaluators for this DISOAe
						 List<ServiceEvaluator> dDISOAedLpusiqg1 = new ArrayList<ServiceEvaluator>(21);
						 for (int j = 0; j<=20; j++){
							 dDISOAedLpusiqg1.add(evaluator("dDISOAedLpusiqg1", gradDI0[j].toString()));
						 }
						 // add the list of gradient evaluators to the model
						 omodel.setGradientEvaluators("DI","DISOAe","dDISOAedLpusiqg1",dDISOAedLpusiqg1);
						 
		// DIMOAe Configuration		
						 // this approximation uses the LpusiSOAe in the DIExact Evaluator instead of the LpusiExacte
							// first construct the InducedDrag object
							// Construct the iDrag Evaluator
							
						 // need to be able to select the evaluator when args are responseVars not independent vars. Because in this case the arg is Lpusi with the LpusiSOAe
						 // not the LpusiExacte.
							var(omodel, "DI","DIMOAe",iDragMethodEval, args(names(loop("k",1,20),"Lpus$k$"), list("q")));
							
						// dDIExactedLpusig1 Configuration - MethodEvaluator used by all 
	//						MethodEvaluator iDragSensitivityMethodEval = new MethodEvaluator("iDragSensitivitiesEvaluator", idragObj, "evaluateIDragSensitivities");
							iDragMethodEval.setVars(Lpusvl);
							
							// evaluateIDragSensitivities retuns a Double[]. First n are wrt Lpusi, last entry is wrt q. Need a list filter.
	//						List<Filter> dDIdLpusFilter = new ArrayList<Filter>(21);
							for (int i = 0; i<Lpusvl.size()+1; i++){
									dDIdLpusFilter.add(new Filter( new ListFilter(i)));
							}
							
							// configure the sensitivity evaluators
	//						List<Evaluator> dDIdLpusEval = new ArrayList<Evaluator>(21);
							for (int i = 0; i<Lpusvl.size(); i++){
								// problem. Why are the entries in the gradient vector evaluators instead of variables? They should be Variables. Since an evaluator may not
								// reduce to a single scalar that is necessary for a derivative calculation.
								// in this case I need to filter the result of this method evaluator iDragSensitivityMethodEval since it returns a vector.
									dDIdLpusEval.add(iDragSensitivityMethodEval);
							}		
								omodel.setGradientEvaluators("DI", "DIMOA", "dDIMOAedLpusiqg1",  dDIdLpusEval);
								
		// Configure Total Lift using a Groovy Evaluator
							// LTExacte
								// LTExacte = Sum LpusiExacte
								// dLTExatedLpusig1 = 1.0
							// LTMOA
								// LTMOAe = Sum LpusiSOA
								// dLTMOAdLpusig1 = 1.0
		return null;
	}
	
	private static ResponseModel configureSensitivities(ResponseModel model) 
		throws RemoteException, ContextException, EvaluationException {
		
		return null;
	}
	
	public static OptimizationModel doOptimization() throws Exception {
		OptimizationModel  omodel = getOptimizationModel();
		configureOptimizationModel(omodel);
		
		Var<?> beta11 = omodel.getVar("beta11");
		//System.out.println("Var beta11: " + beta11.getFilter());
		//System.out.println("Var beta11: " + beta11);
		
		//System.out.println("Induced Drag configured optimization model: " + omodel);

		return omodel;
	}
	private static Task getAvusTask() throws Exception {
		// construct the ProviderContext
		ServiceContext avusContext = getAvusContext();
		// construct Method and then Task for the Avus job
		String providerName = properties.getProperty("requestor.provider.name", "AvusProvider");
		Signature avusMethod = new NetSignature("executeAvus", sorcer.test.eval.AvusRemoteInterface.class, providerName);
		NetTask avusTask = new NetTask("run Avus", avusMethod);
		avusTask.setContext(avusContext);
	
		return avusTask;
	}
	private static ServiceContext getAvusContext() throws Exception {
		// get the context nodes
		ContextNode[] avusNodes = getAvusContextNodes();
		// construct the context
		ServiceContext context = new ServiceContext("AvusContext","AvusContext");
		if (avusNodes[1] != null)
			context.putValue( IN_VALUE + CPS + "AVUS/BCFILE",avusNodes[1],AVUS_BC_TXT);
		if (avusNodes[2] != null)
			context.putValue( IN_VALUE + CPS + "AVUS/JOBFILE", avusNodes[2], avusJob);
		if (avusNodes[3] != null)
			context.putValue( IN_VALUE + CPS + "AVUS/RESTARTFILE", avusNodes[3],avusJob);
		if (avusNodes[4] != null)
			context.putValue( IN_VALUE + CPS + "AVUS/GRIDFILE",avusNodes[4], avusGrid);
		if (avusNodes[5] != null)
			context.putValue( IN_VALUE + CPS + "AVUS/OLDRESTARTFILE",avusNodes[5], avusOldRestart);
		if (avusNodes[6] != null)
			context.putValue( IN_VALUE + CPS + "AVUS/TAPFILE",avusNodes[6], avusTap);
		if (avusNodes[7] != null)
			context.putValue( IN_VALUE + CPS+ "AVUS/TRIPFILE", avusNodes[7], avusTrip);
		
		// create and add the AvusOutput object to context
		AvusOutput avusOut;
		File avusOutputFile = new File(properties.getProperty("requestor.avusbaselineoutput"));
			avusOut = new AvusOutput(avusOutputFile);
			out.println("avusOut windaxisCl = "+avusOut.getWindaxisCl());
			out.println("avusOutput = "+avusOutput);
			context.putValue( OUT_VALUE + CPS+ "AVUS/AVUSOUTPUT",avusOut, avusOutput);
			
		return context;
	}
	private static ContextNode[] getAvusContextNodes() throws Exception {
		ContextNode fCN1 = null; //  bcinputfile	
		ContextNode fCN2 = null; // bcinputfil gen2 format 
		ContextNode fCN3 = null; // jobinputfile
		ContextNode fCN4 = null; // jobrestartfile
		ContextNode fCN5 = null; // gridinputfile
		ContextNode fCN6 = null; // oldrestartinputfile
		ContextNode fCN7 = null; // tapinputfile
		ContextNode fCN8 = null; // tripinputfile
	
		// Create context nodes associated with Files needed to run AVUS
		String basedataURL = Sorcer.getDataServerUrl();
		String avusPath = properties.getProperty("requestor.avus.datapath");
		String dataURL = basedataURL + "/" + avusPath;
		out.println("dataURL = " + dataURL);
	
		// File Context Node for BCinputFile
		String bcInputFileName = properties.getProperty("requestor.bcinfile");
		if (bcInputFileName != null) {
			File bcInputFile = new File(bcInputFileName);
			fCN1 = createURLNode("AvusBCData", bcInputFile, dataURL);
			out.println(">>>BCDATA url " + dataURL);
		}
		// File Context Node for BCinputFile Gen2 format
		String bcGen2InputFileName = properties.getProperty("requestor.bcgen2infile");
		if (bcGen2InputFileName != null) {
			File bcGen2InputFile = new File(bcGen2InputFileName);
			fCN2 = createURLNode("AvusGen2BCData", bcGen2InputFile, dataURL);
		}
		// File Context Node for JobinputFile
		String jobInputFileName = properties.getProperty("requestor.jobinfile");
		if (jobInputFileName != null) {
			File jobInputFile = new File(jobInputFileName);
			fCN3 = createURLNode("AvusJobData", jobInputFile, dataURL);
		}
		out.println("restartfilejob = "+properties.getProperty("requestor.jobrestartfile"));
		String jobRestartFileName = properties.getProperty("requestor.jobrestartfile");
		if (jobRestartFileName != null) {
			File jobRestartFile = new File(jobRestartFileName);
			fCN4 = createURLNode("AvusRestartJobData", jobRestartFile,dataURL);
		}
		// File Context Node for gridinputFile
		String gridInputFileName = properties.getProperty("requestor.gridinfile");
		if (gridInputFileName != null) {
			File gridInputFile = new File(gridInputFileName);
			fCN5 = createURLNode("AvusGridData", gridInputFile, dataURL);
		}
		// File Context Node for oldrestartinputFile
		String oldrestartInputFileName = properties
			.getProperty("requestor.oldrestartinfile");
		if (oldrestartInputFileName != null) {
			File oldrestartInputFile = new File(oldrestartInputFileName);
			fCN6 = createURLNode("AvusOldRestartData", oldrestartInputFile,dataURL);
		}
		// File Context Node for tapinputFile
		String tapInputFileName = properties.getProperty("requestor.tapinfile");
		if (tapInputFileName != null) {
			File tapInputFile = new File(tapInputFileName);
			fCN7 = createURLNode("AvusTapData", tapInputFile, dataURL);
		}
		// File Context Node for tripinputFile
		String tripInputFileName = properties.getProperty("requestor.tripinfile");
		if (tripInputFileName != null) {
			File tripInputFile = new File(tripInputFileName);
			fCN8 = createURLNode("AvusTripData", tripInputFile, dataURL);
		} 
		return new ContextNode[] { fCN1, fCN2, fCN3, fCN4, fCN5, fCN6, fCN7, fCN8};		
	}
	
	public static ContextNode createURLNode(String nodeName, File fileName, String base) {
		URL dataURL = null;
		try {
			dataURL = new URL(base + "/" + fileName);
		} catch (MalformedURLException e) {
			out.println("Cannot create URLNode with filename = "
					+ fileName + " base = " + base);
			e.printStackTrace();
		}
		return new ContextNode(nodeName, dataURL);
	}
}
