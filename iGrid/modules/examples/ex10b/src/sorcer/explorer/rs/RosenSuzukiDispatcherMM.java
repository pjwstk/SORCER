package sorcer.explorer.rs;

import static sorcer.eo.operator.sig;

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
import sorcer.core.context.model.explore.Update;
import sorcer.core.context.model.explore.ResponseContext.EventInfo;
import sorcer.core.context.model.opti.SearchContext;
import sorcer.core.context.model.var.FidelityInfo;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.Exec;
import sorcer.service.SignatureException;
import sorcer.util.SorcerUtil;
import sorcer.vfe.VarInfo;
import sorcer.vfe.util.TableList;
import sorcer.vfe.util.VarInfoList;
import engineering.optimization.conmin.provider.ConminState;
import engineering.optimization.conmin.provider.ConminStrategy;

public class RosenSuzukiDispatcherMM extends ExploreDispatcher {
	static final long serialVersionUID = 8604617506815165509L;
	private int numOptiIter = 0;
	private int numGlobalIter = 0;
	private boolean appxUsed = false;
	private boolean maxCmnIter = false;
	private VarInfo[] nm1ObjVarsInfo ;
	private VarInfo[] nm1ConVarsInfo ;
	private VarInfo[] nm1RdvVarsInfo ;
	
	public RosenSuzukiDispatcherMM() {

	}

	public RosenSuzukiDispatcherMM(ExploreContext context)
			throws RemoteException, UnknownEventException, ContextException,
			SignatureException {
		super(context);
	}

	/* (non-Javadoc)
	 * @see sorcer.core.context.model.explore.ExploreDispatcher#updateSearchContext(sorcer.core.context.model.explore.ModelContext)
	 */
	@Override
	protected ResponseContext validateSearchContext(ContextEvent contextEvent)
			throws DispatchException {
		ResponseContext modelContext = (ResponseContext)contextEvent.getContext();
		logger.info("##################### model context \n" + modelContext);
		if (modelContext.getEventInfo().getCause() != null){
			// Model has been updated, need to initialize the conmin state and set the strategy from the last searchContext
			if (modelContext.getEventInfo().getCause().equals(ContextEvent.Type.ARRANGED)) {
				try {
					ConminStrategy cmnStrategy = (ConminStrategy) dispatchSearchContext.getOptimizerStrategy();
					ConminState cmnState = (ConminState) dispatchSearchContext.getOptimizerState();
					cmnState.initState();
					SearchContext nsc = new SearchContext(modelContext);
					nsc.setOptimizerStrategy(cmnStrategy);
					nsc.setOptimizerState(cmnState);
					ExploreContext nec = new ExploreContext(nsc);
					if (dispatchSearchContext instanceof ExploreContext){
						if (((ExploreContext)dispatchSearchContext).getModelSignature() != null)
							nec.setModelSignature(((ExploreContext)dispatchSearchContext).getModelSignature());
						if (((ExploreContext)dispatchSearchContext).getOptimizerSignature() != null)
							nec.setOptimizerSignature(((ExploreContext)dispatchSearchContext).getOptimizerSignature());
						if (((ExploreContext)dispatchSearchContext).getDispatcherSignature() != null)
							nec.setDispatcherSignature(((ExploreContext)dispatchSearchContext).getDispatcherSignature());
						if (((ExploreContext)dispatchSearchContext).getDispatcherStrategy() != null)
							nec.setDispatcherStrategy(((ExploreContext)dispatchSearchContext).getDispatcherStrategy());
						if (((ExploreContext)dispatchSearchContext).getModelManagerSignature() != null)
							nec.setModelManagerSignature(((ExploreContext)dispatchSearchContext).getModelManagerSignature());
					}
					logger.info(" 3 RosenSuzukiExplorer# returning SearchContext: ########### "+ nec+" type = "+nec.getClass().getName());
					return nec;
				} catch (Exception e) {
					e.printStackTrace();
					throw new DispatchException(e);
				}
			}
		}
		return super.validateSearchContext(contextEvent);
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
			 
			ConminState cmnState = new ConminState(nm1ObjVarsInfo[0], nm1RdvVarsInfo,
					nm1ConVarsInfo);
			optimizerState = cmnState;
		} catch (Throwable e) {
			logger.info(SorcerUtil.stackTraceToString(e));
		}

		return searchContext;
	}

	@Override 
	 protected ResponseContext updateEvaluationContext(
			 ContextEvent contextEvent) throws DispatchException {
		try {
			appxUsed = approxUsed(dispatchSearchContext);
			maxCmnIter = maxConminIter(dispatchSearchContext);
			DispatcherStrategy ds = ((ExploreContext)dispatchSearchContext).getDispatcherStrategy();
			int maxDispatcherIter = ds.getMaxDispatcherIter();
			logger.info("NNNNNNNNNNNNN numGloblaIter = "+numGlobalIter+" maxDispatcherIter = "+maxDispatcherIter);
			if (numGlobalIter >= maxDispatcherIter){
				setState(Exec.DONE);
				return null;
			}
			logger.info(" Dispatcher state = "+ getState());
			logger.info(" appxUsed = "+ appxUsed);
			if (getState() == Exec.FAILED)
				return null;
			//if (searchContext.getEventInfo().getCause().equals(ContextEvent.Type.ARRANGED))optimizer.search(searchContext);
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

				// Generate a new Approximate Problem
				numOptiIter = 0;
				modelManager.stateChanged(contextEvent); 
				// Should now have new approximate problem
				// globalConvergence(searchContext);
				// calcMovlimits(searchContext );
			}
//			// normal return, just update Model context based on what was
//			// returned
		} catch (Exception e) {
			throw new DispatchException(e);
		}
		return super.updateEvaluationContext(contextEvent);
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
			Double[] objGradVals = (Double[])gradFG0.get(objVarsInfo.get(0).getGradientName());
			for (int i = 0; i<objGradVars.size(); i++){
				String[] sgrd = new String[]{objGradVals[i].toString()};
				logger.info("OOOOOOOOOOOOOOOOOOOOOOO objGradientVarName:"+objGradVars.get(i)+" objGradvalue = "+objGradVals[i]);
				upd.add(new Update(objVarsInfo.get(0).getInnerVarInfo().getName(),objGradVars.get(i), sgrd ));
			}

			for (int i = 0; i<conVarsInfo.size(); i++){
				//	logger.info("convarinfo 1 name  = "+conVarsInfo.get(i).getInnerVarInfo().getName());
				//	logger.info("convarinfo 2 name  = "+conVarsInfo.get(i).getName());
				//	logger.info("convarinfo 3 name  = "+consEvaluationName[i] + ","+consGradEvaluationName[i]);
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
			//		logger.info("MMMMMMMMMMMMMMMMMMMMMM update Evaluation 2 Context = "+cntxt);
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
	 protected SearchContext validateResponseContext(
			 ContextEvent contextEvent) throws DispatchException {
		ResponseContext modelContext = (ResponseContext) contextEvent.getContext();
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
				
				// if you want to terminate
					//setState(ExecState.DONE);
					//return;	
			}
		} catch (Exception e) {
			logger.throwing(getClass().getName(), "validateResponseContext",e);
			setState(Exec.FAILED);
			
		}
		return super.validateResponseContext(contextEvent);
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
