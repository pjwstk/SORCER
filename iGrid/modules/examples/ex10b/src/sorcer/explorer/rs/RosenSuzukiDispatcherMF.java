package sorcer.explorer.rs;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.jini.core.event.UnknownEventException;
import sorcer.core.context.model.explore.ContextEvent;
import sorcer.core.context.model.explore.DispatchException;
import sorcer.core.context.model.explore.DispatcherStrategy;
import sorcer.core.context.model.explore.ExploreContext;
import sorcer.core.context.model.explore.ExploreDispatcher;
import sorcer.core.context.model.explore.ResponseContext;
import sorcer.core.context.model.explore.ResponseContext.EventInfo;
import sorcer.core.context.model.explore.Update;
import sorcer.core.context.model.opti.SearchContext;
import sorcer.core.context.model.var.FidelityInfo;
import sorcer.service.ContextException;
import sorcer.service.Exec;
import sorcer.service.SignatureException;
import sorcer.util.SorcerUtil;
import sorcer.vfe.VarInfo;
import sorcer.vfe.util.TableList;
import sorcer.vfe.util.VarInfoList;
import engineering.optimization.conmin.provider.ConminState;

@SuppressWarnings("rawtypes")
public class RosenSuzukiDispatcherMF extends ExploreDispatcher {
	static final long serialVersionUID = 8604617506815165509L;
	private int numOptiIter = 0;
	private int numGlobalIter = 0;
	private boolean appxUsed = false;
	private boolean maxCmnIter = false;
	private VarInfo[] nm1ObjVarsInfo ;
	private VarInfo[] nm1ConVarsInfo ;
	private VarInfo[] nm1RdvVarsInfo ;
	
	public RosenSuzukiDispatcherMF() {

	}

	public RosenSuzukiDispatcherMF(ExploreContext context)
			throws RemoteException, UnknownEventException, ContextException,
			SignatureException {
		super(context);
	}
	
	protected ResponseContext updateModelContext(SearchContext sContext, boolean evalObjCons, boolean evalObjConsGrad, 
			String[] objsEvaluationName, String[] objsGradEvaluationName, String[] consEvaluationName, String[] consGradEvaluationName, 
			boolean updateSOA)
	throws ContextException {
		
		List<FidelityInfo> objEvaluationsForGradients = new ArrayList();
		List<FidelityInfo> conEvaluationsForGradients = new ArrayList();
		List<FidelityInfo> objConEvaluations = new ArrayList();

		//extract info from ModelContext
		VarInfoList objVarsInfo = ((VarInfoList)sContext.getObjectiveVarsInfo()); 
		VarInfoList conVarsInfo = ((VarInfoList)sContext.getConstraintVarsInfo()); 		
		
		for (int i = 0; i<objVarsInfo.size(); i++)objConEvaluations.add(new FidelityInfo(objVarsInfo.get(i).getInnerVarInfo().getName(), objsEvaluationName[i], objsGradEvaluationName[i]));
		for (int i = 0; i<conVarsInfo.size(); i++)objConEvaluations.add(new FidelityInfo(conVarsInfo.get(i).getInnerVarInfo().getName(), consEvaluationName[i], consGradEvaluationName[i]));
		FidelityInfo[] ea = new FidelityInfo[objConEvaluations.size()];
		objConEvaluations.toArray(ea);
//		for (int i = 0; i< ea.length ;i++)logger.info(">>>>>>>>> evaluation i = "+ea[i].toString());
		sContext.setSelectEvaluations(ea);
		sContext.setObjectiveVarsInfo(null);
		sContext.setConstraintVarsInfo(null);
		sContext.setObjectivesGradientInfo(null);
		sContext.setConstraintsGradientInfo(null);
//		if (!evalObjCons){
//			sContext.clearConstraintsInfo();
//			sContext.clearObjectivesInfo();
//		}
//		if (evalObjCons){
//			;
//			for (int i = 0; i<objVarsInfo.size(); i++)objConEvaluations.add(new Evaluation(objVarsInfo.get(i).getInnerVarInfo().getName(), objsEvaluationName[i], objsGradEvaluationName[i]));
//			for (int i = 0; i<conVarsInfo.size(); i++)objConEvaluations.add(new Evaluation(conVarsInfo.get(i).getInnerVarInfo().getName(), consEvaluationName[i], consGradEvaluationName[i]));
//			sContext.setSelectEvaluations((Evaluation[])objConEvaluations.toArray());
//	}
//		if (!evalObjConsGrad){
//			sContext.clearObjectiveGradientsInfo();
//			sContext.clearConstraintGradientsInfo();
//		}
//		if (evalObjConsGrad){
//			for (int i = 0; i<objVarsInfo.size(); i++)objEvaluationsForGradients.add(new Evaluation( objVarsInfo.get(i).getName(),objsGradEvaluationName[i]));
//			for (int i = 0; i<conVarsInfo.size(); i++)conEvaluationsForGradients.add(new Evaluation( conVarsInfo.get(i).getName(),consGradEvaluationName[i]));
//			sContext.setObjectivesGradientInfo( objEvaluationsForGradients);
//			sContext.setConstraintsGradientInfo(conEvaluationsForGradients);
//		}

//		logger.info("##################### state: "+ getState());
		return (ResponseContext) sContext;
}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * sorcer.core.context.model.ExplorerDispatcher#initializeSearchContext(
	 * sorcer.core.context.model.SearchContext)
	 */
	@Override
	protected SearchContext initializeSearchContext(SearchContext searchContext) {
		try {
			logger.info("##################### initialzeSearchContext");
			 nm1ObjVarsInfo = ((VarInfoList) searchContext
					.getObjectiveVarsInfo()).toArray();
			 nm1ConVarsInfo = ((VarInfoList) searchContext
					.getConstraintVarsInfo()).toArray();
			 nm1RdvVarsInfo = ((VarInfoList) searchContext
					.getDesignVarsInfo()).toArray();
			 
			ConminState conminState = new ConminState(nm1ObjVarsInfo[0], nm1RdvVarsInfo,
					nm1ConVarsInfo);
			optimizerState = conminState;
			searchContext.setOptimizerState(conminState);
		} catch (Throwable e) {
			logger.info("WARNING: " + SorcerUtil.stackTraceToString(e));
		}

		return searchContext;
	}

	@Override 
	 protected ResponseContext updateEvaluationContext(
			 ContextEvent contextEvent) throws DispatchException {
		SearchContext searchContext = (SearchContext) contextEvent.getContext();
//		dispatchSearchContext = searchContext;
//		setState(searchContext.getStatus());
//		if (state == ExecState.DONE || state == ExecState.RETURNED) {
//			return;
//		}
		try {
//			// save then clear the optimizerStrategy
//			if (dispatchSearchContext.getOptimizerStrategy() == null)
//				new Throwable().printStackTrace();
//			
//			optimizerStrategy = searchContext.getOptimizerStrategy();
//			searchContext.setOptimizerStrategy(null);
//			optimizerState = searchContext.getOptimizerState();
//			searchContext.setOptimizerState(null);
			
			appxUsed = approxUsed(searchContext);
			maxCmnIter = maxConminIter(searchContext);
			DispatcherStrategy ds = ((ExploreContext)dispatchSearchContext).getDispatcherStrategy();
			int maxDispatcherIter = ds.getMaxDispatcherIter();
			logger.info(" Dispatcher state = "+ getState());
			logger.info(" appxUsed = "+ appxUsed);
			if (getState() == Exec.FAILED  ) return null;
			//if (state == ExecState.DONE  )return;
			if ((state == Exec.DONE || maxCmnIter) && !appxUsed){
				logger.info(" >>>>>>>>>>>>>>>> Exact Problem has Terminated  <<<<<<<<<<<<<<<<<");
				logger.info("Optistate = "+state+" Dispatcher state = " +getState()+" ,ExecState.DONE = "+Exec.DONE+", appxUsed = "+appxUsed+", maxCmnIter = "+ maxCmnIter );
				setState(Exec.DONE);
				return null; 
			}
			if ((state == Exec.DONE || maxCmnIter) && appxUsed){
				// approximate problem converged or max calls to opti using approximate problem
				// need to perform an "Exact" evaluation of all objectives and all constraints 
				logger.info(" >>>>>>>>>>>>>>>> Aproximate Problem has Terminated - Updating Approximate Model <<<<<<<<<<<<<<<<<");
				logger.info("Optistate = "+state+" Dispatcher state = " +getState()+" ,ExecState.DONE = "+Exec.DONE+", appxUsed = "+appxUsed+", maxCmnIter = "+ maxCmnIter );

				numGlobalIter++;
				logger.info(" numGlobalIter = " + numGlobalIter
						+ ", maxDispatcherIter = " + maxDispatcherIter);
				if (numGlobalIter > maxDispatcherIter){
					setState(Exec.DONE);
					return null;
				}
					//modelManager.stateChanged(contextEvent);

				// Get the Derivative Variable names for later updating the SOA Gradient Derivative Variables
				ResponseContext wMC = new ResponseContext();	
				wMC.setDesignVarsInfo(searchContext.getDesignVarsInfo());
//				wMC.setWithResponseGradienVars(true);
				wMC.setWithObjectiveGradientVars(true);
				wMC.setWithConstraintGradientsVars(true);
				wMC.setObjectiveVarsInfo(null);
				wMC.setConstraintVarsInfo(null);
				
				wMC = (ResponseContext)model.evaluate(wMC);
				logger.info(" >>>>>>>>>>>>>>>> Context Containing current Gradient Var Names <<<<<<<<<<<<<<<<<"+wMC);
				VarInfoList objVarsInfoW = ((VarInfoList)wMC.getObjectiveVarsInfo());
				logger.info(" >>>>>>>>> Gradient Name <<<<<<<<<<<<<<<<<"+objVarsInfoW.get(0).getGradientName());
				logger.info(" >>>>>>>>> Gradient Var Names <<<<<<<<<<<<<<<<<"+objVarsInfoW.get(0).getGradientVarNames());
				//ModelContext modelContext = updateModelContext(searchContext);
				
				// set the model context to perform "Exact" for obj, cons and grads.
				ResponseContext modelContext = updateModelContext(searchContext, true, false, new String[]{"Exact"}, new String[]{"fExacteg1"}, new String[]{"Exact","Exact","Exact"},
						new String[]{"g1Exacteg1","g2Exacteg1","g3Exacteg1"}, false);
				logger.info(" >>>>>>>>>>>>>>>> Changing to Exact Evaluators <<<<<<<<<<<<<<<<<");
				model.selectEvaluation(modelContext);
				logger.info(" >>>>>>>>>>>>>>>> Returned from Calling Model Selection Direct <<<<<<<<<<<<<<<<<");
				numOptiIter = 0;

				// logger.info(">>>>>>>>>>>>>>>>>>>> SC Obj Evaluator Name = "+sc.getObjectivesInfo().get(0).getInnerVarInfo().getEvaluatorName());
				// return;
				try{	
				// Evaluate the "Exact" model to obtain the necessary information for updating the approximate problem.
				ResponseContext xMC = new ResponseContext();
//				
				xMC.setDesignVarsInfo(searchContext.getDesignVarsInfo());
				xMC.setOutputVarsInfo(null);
				xMC.setObjectiveVarsInfo(null);
				xMC.setConstraintVarsInfo(null);
				xMC.setObjectivesGradientInfo(null);
				xMC.setConstraintsGradientInfo(null);
//				
				logger.info(" >>>>>>>>>>>>>>>> Exact Evalution with Direct Evaluate Method <<<<<<<<<<<<<<<<<");
				xMC = (ResponseContext)model.evaluate(xMC);
				logger.info("XXXXXXXXX 1 xMc is with gradient vars = "+xMC.isWithResponseGradientVars());
				logger.info(" >>>>>>>>>>>>>>>> Return Exact Evalution with Direct Evaluate Method <<<<<<<<<<<<<<<<<"+xMC);
				
//				// update the SOA evaluators 
				xMC = getUpdateEvals(wMC, xMC, new String[]{"SOA"}, new String[]{"fSOAeg1"}, new String[]{"SOA","SOA","SOA"},
						new String[]{"g1SOAeg1","g2SOAeg1","g3SOAeg1"});
//				
				logger.info(" >>>>>>>>>>>>>>>>Calling Update Evalution with Direct updateEvaluation Method <<<<<<<<<<<<<<<<<");
				
				xMC = (ResponseContext)model.updateEvaluation(xMC);
				logger.info(" >>>>>>>>>>>>>>>> After Update Evalution with Direct updateEvaluation Method <<<<<<<<<<<<<<<<<");
//				
//				// Select SOA evaluators
				logger.info(" >>>>>>>>>>>>>>>> update Model context to Select SOA <<<<<<<<<<<<<<<<<");
				 modelContext = updateModelContext(searchContext, true, false, new String[]{"SOA"}, new String[]{"fSOAeg1"}, new String[]{"SOA","SOA","SOA"},
						new String[]{"g1SOAeg1","g2SOAeg1","g3ESOAeg1"}, false);
//				 
//				logger.info(" >>>>>>>>>>>>>>>> Changing to SOA Evaluators with Direct selectEvaluation<<<<<<<<<<<<<<<<<");
				model.selectEvaluation(modelContext);
				
				xMC = new ResponseContext();
//				
				xMC.setDesignVarsInfo(searchContext.getDesignVarsInfo());
				xMC.setObjectiveVarsInfo(null);	
				xMC.setConstraintVarsInfo(null);
				xMC.setObjectivesGradientInfo(null);
				xMC.setConstraintsGradientInfo(null);
				xMC = (ResponseContext)model.evaluate(xMC);
				//logger.info(" >>>>>>>>>>>>>>>> Return SOA Evalution with Direct Evaluate Method <<<<<<<<<<<<<<<<<"+xMC);
				VarInfoList objVarsInfo = ((VarInfoList)xMC.getObjectiveVarsInfo()); 
				VarInfoList conVarsInfo = ((VarInfoList)xMC.getConstraintVarsInfo()); 
				
				logger.info(" >>>>>>>>>>>>>>>> Return SOA Obj value =  <<<<<<<<<<<<<<<<<"+objVarsInfo.get(0).getValue());
				logger.info(" >>>>>>>>>>>>>>>> Return SOA g1 value =  <<<<<<<<<<<<<<<<<"+conVarsInfo.get(0).getLhValue());
				logger.info(" >>>>>>>>>>>>>>>> Return SOA g2 value =  <<<<<<<<<<<<<<<<<"+conVarsInfo.get(1).getLhValue());
				logger.info(" >>>>>>>>>>>>>>>> Return SOA g3 value =  <<<<<<<<<<<<<<<<<"+conVarsInfo.get(2).getLhValue());
				Map gm = getGradFG0(xMC);
				Set<String> keys = gm.keySet();
				Iterator it = keys.iterator();
				while (it.hasNext() ){
					String name = (String)it.next();
					Double[] grd = (Double[])gm.get(name);
					logger.info(" grad var name = "+name+" gradValues "+Arrays.toString(grd));
				}
				
				}
				catch (Exception e) {
					logger.throwing(getClass().getName(), "updateEvaluationContext", e);
					System.exit(1);
				} 
				//setState(ExecState.DONE);
				//return; 
				// Should now have new approximate problem
				// globalConvergence(searchContext);
				// calcMovlimits(searchContext );
			}
		} catch (Exception e) {
			throw new DispatchException(e);
		}
		return super.validateSearchContext(contextEvent);
	}
	
	private ResponseContext getUpdateEvals(ResponseContext wMC, ResponseContext xMC, String[] objsEvaluationName, String[] objsGradEvaluationName, 
			String[] consEvaluationName, String[] consGradEvaluationName) {

		List<FidelityInfo> updateEvals =  new ArrayList();
		List<FidelityInfo> objEvaluationsForGradients = new ArrayList();
		List<FidelityInfo> conEvaluationsForGradients = new ArrayList();
		List<FidelityInfo> objConEvaluations = new ArrayList();

		//extract info from ModelContext
		VarInfoList objVarsInfo;
		VarInfoList conVarsInfo;
		VarInfoList respVarsInfo;
		VarInfoList objVarsInfoW;
		VarInfoList conVarsInfoW;
		ResponseContext cntxt = new ResponseContext();
		try {
			objVarsInfo = ((VarInfoList)xMC.getObjectiveVarsInfo());
			objVarsInfoW = ((VarInfoList)wMC.getObjectiveVarsInfo());
			//respVarsInfo = ((VarInfoList)xMC.getResponsesInfo());
			conVarsInfo = ((VarInfoList)xMC.getConstraintVarsInfo());
			conVarsInfoW = ((VarInfoList)wMC.getConstraintVarsInfo());
			List<Update> upd = new ArrayList(); 

			Double[] x0 = getX0(xMC);
			Double[] fg0 = getFG0(xMC);
			Map gradFG0 = getGradFG0(xMC);			
			FidelityInfo eval = new FidelityInfo(objVarsInfo.get(0).getInnerVarInfo().getName(), objsEvaluationName[0], objsGradEvaluationName[0]);
			upd.add(new Update(eval, x0, fg0[0], gradFG0.get(objVarsInfo.get(0).getGradientName())));
			//List<String> objGradVars = objVarsInfo.get(0).getInnerVarInfo().getGradientVarNames();
			//logger.info("BBBBBBBBBBBB obj gradientVars names = "+objGradVars);
			List<String> objGradVars = objVarsInfoW.get(0).getGradientVarNames();
			logger.info("BBBBBBBBBBBB obj gradientVars names = "+objGradVars);
			logger.info("BBBBBBBBBBBB obj gradientVarsInfo gradientName = "+objVarsInfo.get(0).getGradientName());
			Double[] objGradVals = (Double[])gradFG0.get(objVarsInfo.get(0).getGradientName());
			logger.info("BBBBBBBBBBBB obj objGradVals = "+ objGradVals);
			logger.info("BBBBBBBBBBBB obj objGradVals elements= "+ Arrays.toString(objGradVals));

			for (int i = 0; i<objGradVars.size(); i++){
				String[] sgrd = new String[]{objGradVals[i].toString()};
				logger.info("OOOOOOOOOOOOOOOOOOOOOOO objGradientVarName:"+objGradVars.get(i)+" objGradvalue = "+objGradVals[i]);
				upd.add(new Update(objVarsInfo.get(0).getInnerVarInfo().getName(),objGradVars.get(i), sgrd ));
			}

			for (int i = 0; i<conVarsInfo.size(); i++){
				//logger.info("convarinfo 1 name  = "+conVarsInfo.get(i).getInnerVarInfo().getName());
				//logger.info("convarinfo 2 name  = "+conVarsInfo.get(i).getName());
				//logger.info("convarinfo 3 name  = "+consEvaluationName[i] + ","+consGradEvaluationName[i]);
				//List<String> coniGradVars = conVarsInfoW.get(i).getGradientVarNames();
				logger.info("JJJJJJJJJJJJJJJJ conVarinfo = "+conVarsInfo.get(i));
				//logger.info("BBBBBBBBBBBB con i gradientVars names = "+coniGradVars);
				FidelityInfo evalg = new FidelityInfo(conVarsInfo.get(i).getInnerVarInfo().getName(), consEvaluationName[i], consGradEvaluationName[i]);
				upd.add(new Update(evalg, x0, fg0[i+1], gradFG0.get(conVarsInfo.get(i).getGradientName())));

				List<String> conGradVars = conVarsInfoW.get(i).getGradientVarNames();
				Double[] conGradVals = (Double[])gradFG0.get(conVarsInfo.get(i).getGradientName());
				for (int j = 0; j<conGradVars.size(); j++){
					String[] sgrd = new String[]{conGradVals[j].toString()};
					logger.info("CCCCCCCCCCCCCCCCCCCC conGradientVarName:"+conGradVars.get(j)+" ConGradvalue = "+conGradVals[j]);
					upd.add(new Update(conVarsInfo.get(i).getInnerVarInfo().getName(),conGradVars.get(j), sgrd ));
				}
			}
			Update[] updA = new Update[upd.size()];
			for (int i=0; i<upd.size(); i++)
				updA[i]=upd.get(i);
			cntxt.setUpdateEvaluations(updA);
			//logger.info("MMMMMMMMMMMMMMMMMMMMMM update Evaluation 2 Context = "+cntxt);
		} catch (Exception e) {
			logger.throwing(getClass().getName(), "getUpdateEvals", e);
		} 
		return cntxt;
	}

	/**
	 * @param xMC 
	 * @return
	 * @throws ContextException 
	 */
	private Map getGradFG0(ResponseContext xMC) throws ContextException {
		Map gradTable = new HashMap();
		TableList objsGrads =(TableList)xMC.getObjectivesGradientValues();
		TableList conGrads = (TableList)xMC.getConstraintsGradientValues();
		
		String objName = objsGrads.getNames().get(0);
		VarInfoList desVarsInfo = ((VarInfoList)xMC.getDesignVarsInfo()); 
		
		Double[] gf = new Double[desVarsInfo.size()];
		for (int i = 0; i < desVarsInfo.size(); i++) {
			gf[i] = (Double) objsGrads.getValue(objName,desVarsInfo.get(i).getName());
			gradTable.put(objName, gf);
		}
		
		List<String> conNames = conGrads.getNames();
		for (int j=0; j<conNames.size(); j++){
			Double[] gjg = new Double[desVarsInfo.size()];
			for (int i = 0; i < desVarsInfo.size(); i++) {
				gjg[i] = (Double) conGrads.getValue(conNames.get(j),desVarsInfo.get(i).getName());
			}
			gradTable.put(conNames.get(j), gjg);
		}
		
//		Double[] fg = new Double[]{-4.999866456447779, -3.0351803419416337, -12.98637609250401, 4.999670304805452};
//		Double[] g1g = new Double[]{1.0001335435522205, 0.9648196580583661, 5.006811953747995, -3.0003296951945475};
//		Double[] g2g = new Double[]{-0.9998664564477794, 3.9296393161167322, 4.006811953747995, -5.000659390389095};
//	    Double[] g3g = new Double[]{2.000267087104441, 0.9648196580583661, 4.006811953747995, -1.0};
//	    gradTable.put("fgrad", gf);
//	    gradTable.put("g1grad", g1g);
//	    gradTable.put("g2grad", g2g);
//	    gradTable.put("g3grad", g3g);
		return gradTable;
	}

	/**
	 * @return
	 * @throws ContextException 
	 */
	private Double[] getFG0(ResponseContext xMC) throws ContextException {
		// TODO Auto-generated method stub
		VarInfoList objVarsInfo = ((VarInfoList)xMC.getObjectiveVarsInfo()); 
		VarInfoList conVarsInfo = ((VarInfoList)xMC.getConstraintVarsInfo()); 
		//logger.info(" LLLLLLLLLLLLLLLL length of fg0 = "+objVarsInfo.size()+" + "+conVarsInfo.size());
		Double[] fg0 = new Double[objVarsInfo.size()+conVarsInfo.size()];
		//Double[] fg0 = new Double[]{6.007667363788478, 3.220743935514747E-4, -1.0553488224610579, -0.0033468486151440047};
		for (int i = 0; i<objVarsInfo.size(); i++)fg0[i]=(Double)objVarsInfo.get(i).getValue();
		for (int j = 0; j<conVarsInfo.size(); j++)fg0[objVarsInfo.size()+j]=(Double)conVarsInfo.get(j).getLhValue();
		return fg0;
	}

	/**
	 * @param xMC 
	 * @return
	 * @throws ContextException 
	 */
	private Double[] getX0(ResponseContext xMC) throws ContextException {
		// TODO Auto-generated method stub
		VarInfoList desVarsInfo = ((VarInfoList)xMC.getDesignVarsInfo()); 
		Double[] x0 = new Double[desVarsInfo.size()];
		for (int i=0; i<x0.length; i++)x0[i]=(Double)desVarsInfo.get(i).getValue();
		//Double[] x0 = new Double[]{6.677177611029948E-5, 0.9824098290291831, 2.0034059768739976, -1.0001648475972738};
		return x0;
	}

	/**
	 * @param searchContext
	 * @return
	 * @throws ContextException 
	 */
	private boolean approxUsed(SearchContext sContext) throws ContextException {
		// TODO Auto-generated method stub
		appxUsed = false;
		
		//extract info from ModelContext
		VarInfoList objVarsInfo = ((VarInfoList)sContext.getObjectiveVarsInfo()); 
		VarInfoList conVarsInfo = ((VarInfoList)sContext.getConstraintVarsInfo()); 
//		GradientList objGrads = sContext.getObjectivesGradientValues();		
//		GradientList conGrads = sContext.getConstraintsGradientValues();
//		System.out.println(">>>>>>>>>>>>>>>>>>>> OBJ GradList = "+objGrads.toString());
//		System.out.println(">>>>>>>>>>>>>>>>>>>> conGrads GradList = "+conGrads);
//		System.out.println(">>>>>>>>>>>>>>>>>>>> OBJ EvaluatorName = "+objVarsInfo.get(0).getEvaluatorName());
//		logger.info(">>>>>>>>>>>>>>>>>>>> OBJ inner Var evaluator Name = "+objVarsInfo.get(0).getInnerVarInfo().getEvaluatorName());
//		logger.info(">>>>>>>>>>>>>>>>>>>> OBJ inner var gradientName "+objVarsInfo.get(0).getInnerVarInfo().getGradientName());
//		
//		logger.info(">>>>>>>>>>>>>>>>>>>> OBJ  Var evaluator Name = "+objVarsInfo.get(0).getEvaluatorName());
//		logger.info(">>>>>>>>>>>>>>>>>>>> OBJ  var gradientName "+objVarsInfo.get(0).getGradientName());
		
		for (int i=0; i<objVarsInfo.size(); i++) if (!objVarsInfo.get(0).getInnerVarInfo().getEvaluatorName().contains("Exact")) appxUsed =true;
		for (int i=0; i<conVarsInfo.size(); i++) if (!conVarsInfo.get(i).getInnerVarInfo().getEvaluatorName().contains("Exact")) appxUsed =true;		
		for (int i=0; i<objVarsInfo.size(); i++) if (!objVarsInfo.get(0).getInnerVarInfo().getGradientName().contains("Exact")) appxUsed =true;
		for (int i=0; i<conVarsInfo.size(); i++) if (!conVarsInfo.get(i).getInnerVarInfo().getGradientName().contains("Exact")) appxUsed =true;		

		
		return appxUsed;
	}
	
	@Override
	 protected SearchContext updateSearchContext(
			 ContextEvent contextEvent) throws DispatchException {
		ResponseContext modelContext = (ResponseContext)contextEvent.getContext();
		try {
			if ((getState() == Exec.DONE || maxCmnIter) && appxUsed) {
				// approximate problem converged or max calls to opti using
				// approximate problem reached
				// assuming that model returned with "Exact" evaluation of all
				// objectives and all constraints (this model update was
				// requested by processOptimizerRequest)
				// need to check for global convergence

				if (globalConvergence(modelContext))
					return null;

				// if not converged generate a new approximate problem(model)
				// and update the move limits
				// calcMovlimits(modelContext );

				// update differentiation
				// model.update()

				// Need to compute the "Exact" Gradients for Obj & Constraints
				// for the new SOA model. This can be done here or when the
				// update Evaluators occur
				// model.update()

				// need api to update evaluators. In this case I need to replace
				// the F(x0) and GradF(x0) in the SOA evaluators
				// model.update()

				// need to select the evaluations to SOA for Obj, Conts
			}
		} catch (Exception e) {
			logger.throwing(getClass().getName(), "postprocessSearchContext",e);
			setState(Exec.FAILED);
		}
		return super.updateSearchContext(contextEvent);

	}
	/**
	 * @param searchContext
	 * @return
	 * @throws ContextException 
	 */
	private boolean maxConminIter(SearchContext searchContext) throws ContextException {
		numOptiIter++;
		DispatcherStrategy ds = ((ExploreContext)dispatchSearchContext).getDispatcherStrategy();
		int maxOptiIter = ds.getMaxOptiIter();
//		System.out.println(" numoptiIter = "+numOptiIter+" maxOptiIter = "+maxOptiIter);
		if (numOptiIter > maxOptiIter) return true;
		return false;
	}

	/**
	 * @param searchContext
	 * @return
	 * @throws ContextException 
	 * @throws UnknownEventException 
	 * @throws RemoteException 
	 */
	private boolean globalConvergence(ResponseContext modelContext) throws ContextException, RemoteException, UnknownEventException {
		boolean converged = false;
		// requires storage of n-1 exact values of the objective, constraints, and design Variable values.
        // check absolute & percentage change of exact obj
		// check absolute & percentage change of dvars
		// check to see if any constraints are on the boundary
		
//		// set the n-1 values to the current exact
//		nm1ObjVarsInfo = ((VarInfoList) modelContext.getObjectivesInfo()).toArray();
//		nm1ConVarsInfo = ((VarInfoList) modelContext.getConstraintsInfo()).toArray();
//		nm1RdvVarsInfo = ((VarInfoList) modelContext.getDesignVarsInfo()).toArray();
		return converged;
	}
	
	private void calcMovlimits(ResponseContext context) throws ContextException {
		
		VarInfo[] rdvVarsInfo = ((VarInfoList)context.getDesignVarsInfo()).toArray(); 			
		DispatcherStrategy dStrat = (DispatcherStrategy)((ExploreContext)context).getDispatcherStrategy();
		double ximove = dStrat.getVarMoveLimit();
		// compute the movelimits 
		for (int i = 0; i < rdvVarsInfo.length; i++) {
			// if -0.5 < xi < 0.5 xllocal = -ximove, xulocal=ximove
			// System.out.println(">>>>>>>>> x length = "+x.length+" ximove
			// length = "+ximove.length);
			if (ximove != 0.0) {
				if ((Double)rdvVarsInfo[i].getValue() > -1.0 && (Double)rdvVarsInfo[i].getValue() < 1.0) {

					rdvVarsInfo[i].setLowerMoveLimit((Double)rdvVarsInfo[i].getValue() - ximove);
					rdvVarsInfo[i].setUpperMoveLimit((Double)rdvVarsInfo[i].getValue() + ximove);
				} else {
					if ((Double)rdvVarsInfo[i].getValue() < 0.0) {
						rdvVarsInfo[i].setLowerMoveLimit((Double)rdvVarsInfo[i].getValue() * ximove);
						rdvVarsInfo[i].setUpperMoveLimit( (Double)rdvVarsInfo[i].getValue() / ximove);
					} else {
						rdvVarsInfo[i].setLowerMoveLimit((Double)rdvVarsInfo[i].getValue() / ximove);
						rdvVarsInfo[i].setUpperMoveLimit((Double)rdvVarsInfo[i].getValue() * ximove);
					}
				}
			} 
		}
	}

}
