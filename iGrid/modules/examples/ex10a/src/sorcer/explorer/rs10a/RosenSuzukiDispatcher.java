package sorcer.explorer.rs10a;

import java.rmi.RemoteException;

import net.jini.core.event.UnknownEventException;
import sorcer.core.context.model.explore.ExploreContext;
import sorcer.core.context.model.explore.ExploreDispatcher;
import sorcer.core.context.model.opti.SearchContext;
import sorcer.service.ContextException;
import sorcer.service.SignatureException;
import sorcer.util.SorcerUtil;
import sorcer.vfe.VarInfo;
import sorcer.vfe.util.VarInfoList;
import engineering.optimization.conmin.provider.ConminState;


public class RosenSuzukiDispatcher extends ExploreDispatcher {
	
	static final long serialVersionUID = 8604617506815165509L;

	public RosenSuzukiDispatcher() {
		
	}
	
	public RosenSuzukiDispatcher(ExploreContext context) throws RemoteException, UnknownEventException, 
		ContextException, SignatureException {
		super(context);
	}

	/* (non-Javadoc)
	 * @see sorcer.core.context.model.ExplorerDispatcher#initializeSearchContext(sorcer.core.context.model.SearchContext)
	 */
	@Override
	protected SearchContext initializeSearchContext(SearchContext searchContext) {
		try {
			logger.info("##################### initialzeSearchContext");
			VarInfo<?>[] objVarsInfo = ((VarInfoList)searchContext.getObjectiveVarsInfo()).toArray();
			VarInfo<?>[] conVarsInfo = ((VarInfoList)searchContext.getConstraintVarsInfo()).toArray(); 
			VarInfo<?>[] rdvVarsInfo = ((VarInfoList)searchContext.getInputVarsInfo()).toArray(); 
			
			ConminState cmnState = new ConminState(objVarsInfo[0], rdvVarsInfo, conVarsInfo);
			optimizerState = cmnState;
		} catch (Throwable e) {
			//e.printStackTrace();
			logger.info("WARNING: " + SorcerUtil.stackTraceToString(e));
		} 
		
		return searchContext;
	}

}

