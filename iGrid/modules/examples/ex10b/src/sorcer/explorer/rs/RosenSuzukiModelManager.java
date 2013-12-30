package sorcer.explorer.rs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sorcer.core.context.model.explore.ContextEvent;
import sorcer.core.context.model.explore.ExploreDispatcher;
import sorcer.core.context.model.explore.ResponseContext;
import sorcer.core.context.model.explore.ModelManager;
import sorcer.core.context.model.explore.Update;
import sorcer.core.context.model.explore.ContextEvent.Type;
import sorcer.core.context.model.explore.ResponseContext.EventInfo;
import sorcer.core.context.model.var.FidelityInfo;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.EvaluationException;
import sorcer.vfe.util.TableList;
import sorcer.vfe.util.VarInfoList;

/**
 * @author raymondkolonay
 *
 */
public class RosenSuzukiModelManager extends ModelManager{
	int selCalls = 0;
	int evalCalls = 0;
	ResponseContext wMC ;
	
	public RosenSuzukiModelManager(ExploreDispatcher dispatcher) {
		super(dispatcher, false);
	}
	
	public RosenSuzukiModelManager(ExploreDispatcher dispatcher, boolean isThreaded) {
		super(dispatcher, isThreaded);
	}
	/* (non-Javadoc)
	 * @see sorcer.core.context.model.explore.ModelManager#updateModel(sorcer.service.Context, sorcer.core.context.model.explore.ContextEvent.Type)
	 */
	@Override
	public Context updateModel(Context mc, Type type)
			throws EvaluationException {
		ResponseContext context = (ResponseContext) mc;
		ResponseContext xMC = new ResponseContext();
		EventInfo eInfo = context.getEventInfo();
		try {
			if (context.getEventInfo().getCause().equals(ContextEvent.Type.MODEL_SETUP)) {
				xMC.setEventInfo(eInfo);
				xMC.getEventInfo().setCause(Type.MODEL_EVALUATION);
				evalCalls = 0;
				logger.info(" >>>>>>>>>>>>>>>> Returning from Type.MODEL_SETUP with cause:"+xMC.getEventInfo().getCause());
				return xMC;
			}
			
			else if (context.getEventInfo().getCause().equals(ContextEvent.Type.MODEL_RECONFIG)) {
//				context.getEventInfo().setCause(ContextEvent.Type.MODEL_UPDATE);
//				context.setConfigurationEvaluations(new Update(eval, array(
//						new Object(), "reconfig")));
				logger.info(" ContextEvent Type.MODEL_RECONFIG NOT IMPLEMENTED");
				return xMC;

			} else if (context.getEventInfo().getCause().equals(ContextEvent.Type.MODEL_UPDATE)) {
				
				// update the SOA evaluators 
				xMC = getUpdateEvals(wMC, (ResponseContext)mc, new String[]{"SOA"}, new String[]{"fSOAeg1"}, new String[]{"SOA","SOA","SOA"},
						new String[]{"g1SOAeg1","g2SOAeg1","g3SOAeg1"});
//				
				logger.info(" >>>>>>>>>>>>>>>> Updating Evalution  <<<<<<<<<<<<<<<<<");
				xMC.setEventInfo(eInfo);
				xMC.getEventInfo().setCause(ContextEvent.Type.MODEL_SELECTION);
				//xMC = (ModelContext)model.updateEvaluation(xMC);				
				return xMC;
			} else if (context.getEventInfo().getCause().equals(ContextEvent.Type.MODEL_SELECTION)) {	
				if (selCalls == 0){
					// this entry assumes that the previous event was MODEL_EVALUATION, hence mc should have varInfo information populated.
					this.wMC = (ResponseContext)mc;
					VarInfoList objVarsInfoW = ((VarInfoList)wMC.getObjectiveVarsInfo());
					logger.info(" >>>>>>>>> Var Name & Value <<<<<<<<<<<<<<<<<"+objVarsInfoW.get(0).getName()+" ,"+objVarsInfoW.get(0).getValue());
					logger.info(" >>>>>>>>> Gradient Name <<<<<<<<<<<<<<<<<"+objVarsInfoW.get(0).getGradientName());
					logger.info(" >>>>>>>>> Gradient Var Names <<<<<<<<<<<<<<<<<"+objVarsInfoW.get(0).getGradientVarNames());
					// set the model context to perform "Exact" for obj, cons and grads.
					logger.info(" >>>>>>>>>>>>>>>> Get Exact Evaluators Selection Context <<<<<<<<<<<<<<<<<");
					
					xMC = updateModelContext((ResponseContext)mc, true, false, new String[]{"Exact"}, new String[]{"fExacteg1"}, new String[]{"Exact","Exact","Exact"},
							new String[]{"g1Exacteg1","g2Exacteg1","g3Exacteg1"}, false);
					xMC.setEventInfo(eInfo);
					xMC.getEventInfo().setCause(ContextEvent.Type.MODEL_EVALUATION);	
					selCalls++;
					logger.info(" >>>>>>>>>>>>>>>> Returning from Exact Type.MODEL_SELECTION with cause:"+((ResponseContext)mc).getEventInfo().getCause());
					//evalCalls = 0;
					selCalls++;
					return xMC;
				}	
				else if (selCalls > 0){
					// this entry assumes that the previous event was MODEL_UPDATE, hence mc will be empty. Hence to get the VarInfo information use this.wMC
//					// Select SOA evaluators
					logger.info(" >>>>>>>>>>>>>>>> Get SOA Evaluators Selection Context <<<<<<<<<<<<<<<<<");
					xMC = updateModelContext(this.wMC, true, false, new String[]{"SOA"}, new String[]{"fSOAeg1"}, new String[]{"SOA","SOA","SOA"},
							new String[]{"g1SOAeg1","g2SOAeg1","g3SOAeg1"}, false);
					xMC.setEventInfo(eInfo);
					xMC.getEventInfo().setCause(ContextEvent.Type.MODEL_EVALUATION);
					logger.info(" >>>>>>>>>>>>>>>> Returning from SOA Type.MODEL_SELECTION with cause:"+((ResponseContext)mc).getEventInfo().getCause());
    				evalCalls = -1;
    				return xMC;
				}
				
			} else if (context.getEventInfo().getCause().equals(ContextEvent.Type.MODEL_EVALUATION)) {
				
				if (evalCalls == 0){
					// Get the Derivative Variable names for later updating the SOA Gradient Derivative Variables	
					logger.info(" >>>>>>>>>>>>>>>> Getting SOADerivVarNamesContext  <<<<<<<<<<<<<<<<<");
					xMC = getSOADerivVarNamesContext(eInfo);
					xMC.getEventInfo().setCause(Type.MODEL_SELECTION);
					evalCalls++;
					logger.info(" >>>>>>>>>>>>>>>> Returning from Type.MODEL_EVALUATION with cause:"+((ResponseContext)mc).getEventInfo().getCause());
					return xMC;
				} else if
					(evalCalls == 1){
					
					// Evaluate the "Exact" model to obtain the necessary information for updating the approximate problem.
					logger.info(" >>>>>>>>>>>>>>>> Get Exact Evaluation Context <<<<<<<<<<<<<<<<<");
					xMC = new ResponseContext();			
					xMC.setDesignVarsInfo(wMC.getDesignVarsInfo()); //wMC should contain the variable values from the converged SOA Opti
					xMC.setOutputVarsInfo(null);
					xMC.setObjectiveVarsInfo(null);
					xMC.setConstraintVarsInfo(null);
					xMC.setObjectivesGradientInfo(null);
					xMC.setConstraintsGradientInfo(null);
					xMC.setEventInfo(eInfo);
					xMC.getEventInfo().setCause(ContextEvent.Type.MODEL_UPDATE);
					logger.info(" >>>>>>>>>>>>>>>> Exact Evalution <<<<<<<<<<<<<<<<<");
					return xMC;
					//xMC = (ModelContext)model.evaluate(xMC);
				} else if
					(evalCalls == -1){
					logger.info(" >>>>>>>>>>>>>>>> Get SOA Evaluation Context <<<<<<<<<<<<<<<<<");
					// Evaluate the SOA model after completing model update
					xMC = new ResponseContext();			
					xMC.setDesignVarsInfo(wMC.getDesignVarsInfo()); //wMC should contain the variable values from the converged SOA Opti
					xMC.setOutputVarsInfo(null);
					xMC.setObjectiveVarsInfo(null);
					xMC.setConstraintVarsInfo(null);
					xMC.setObjectivesGradientInfo(null);
					xMC.setConstraintsGradientInfo(null);
					xMC.setEventInfo(eInfo);
					xMC.getEventInfo().setCause(ContextEvent.Type.ARRANGED);
					logger.info(" >>>>>>>>>>>>>>>> SOA Evalution <<<<<<<<<<<<<<<<<");
					//xMC = (ModelContext)model.evaluate(xMC);
					evalCalls = 0;
					selCalls = 0;
					return xMC;
				}
				
			}
		} catch (ContextException e) {
			e.printStackTrace();
		}
		return xMC;
	}
	/**
	 * @param mc
	 * @return
	 * @throws ContextException 
	 */
	private ResponseContext getSOADerivVarNamesContext( EventInfo eInfo) throws ContextException {
		ResponseContext xMC = new ResponseContext();
		xMC.setEventInfo(eInfo);
		xMC.setDesignVarsInfo(this.searchContext.getDesignVarsInfo());
		xMC.setWithObjectiveGradientVars(true);
		xMC.setWithConstraintGradientsVars(true);
		xMC.setObjectiveVarsInfo(null);
		xMC.setConstraintVarsInfo(null);
		return xMC;
	}

	protected ResponseContext updateModelContext(ResponseContext sContext, boolean evalObjCons, boolean evalObjConsGrad, 
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
		ResponseContext xMC = new ResponseContext();
		xMC.setSelectEvaluations(ea);
		xMC.setObjectiveVarsInfo(null);
		xMC.setConstraintVarsInfo(null);
		xMC.setObjectivesGradientInfo(null);
		xMC.setConstraintsGradientInfo(null);
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
		return (ResponseContext) xMC;
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
				//			logger.info("convarinfo 1 name  = "+conVarsInfo.get(i).getInnerVarInfo().getName());
				//			logger.info("convarinfo 2 name  = "+conVarsInfo.get(i).getName());
				//			logger.info("convarinfo 3 name  = "+consEvaluationName[i] + ","+consGradEvaluationName[i]);
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
			// TODO Auto-generated catch block
			logger.throwing(getClass().getName(), "getUpdateEvals", e);
		} 
		return cntxt;
	}
	
	private Double[] getX0(ResponseContext xMC) throws ContextException {
		// TODO Auto-generated method stub
		VarInfoList desVarsInfo = ((VarInfoList)xMC.getDesignVarsInfo()); 
		Double[] x0 = new Double[desVarsInfo.size()];
		for (int i=0; i<x0.length; i++)x0[i]=(Double)desVarsInfo.get(i).getValue();
		//Double[] x0 = new Double[]{6.677177611029948E-5, 0.9824098290291831, 2.0034059768739976, -1.0001648475972738};
		return x0;
	}

	/**
	 * @param xMC 
	 * @return
	 * @throws ContextException 
	 */
	private Map getGradFG0(ResponseContext xMC) throws ContextException {
		// TODO Auto-generated method stub
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
		//logger.info("length of fg0 = "+objVarsInfo.size()+" + "+conVarsInfo.size());
		Double[] fg0 = new Double[objVarsInfo.size()+conVarsInfo.size()];
		//Double[] fg0 = new Double[]{6.007667363788478, 3.220743935514747E-4, -1.0553488224610579, -0.0033468486151440047};
		for (int i = 0; i<objVarsInfo.size(); i++)fg0[i]=(Double)objVarsInfo.get(i).getValue();
		for (int j = 0; j<conVarsInfo.size(); j++)fg0[objVarsInfo.size()+j]=(Double)conVarsInfo.get(j).getLhValue();
		return fg0;
	}

}
