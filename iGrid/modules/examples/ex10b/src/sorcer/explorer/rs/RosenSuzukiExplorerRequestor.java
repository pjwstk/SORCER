package sorcer.explorer.rs;

import static sorcer.eo.operator.context;
import static sorcer.eo.operator.dispatcher;
import static sorcer.eo.operator.exert;
import static sorcer.eo.operator.initialDesign;
import static sorcer.eo.operator.input;
import static sorcer.eo.operator.model;
import static sorcer.eo.operator.optiTask;
import static sorcer.eo.operator.optimizer;
import static sorcer.eo.operator.result;
import static sorcer.eo.operator.sig;
import static sorcer.eo.operator.strategy;
import static sorcer.eo.operator.task;
import static sorcer.eo.operator.value;
import static sorcer.po.operator.par;
import static sorcer.vo.operator.varInfo;
import static sorcer.vo.operator.varsInfo;

import java.io.File;
import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import sorcer.core.context.model.explore.DispatcherStrategy;
import sorcer.core.context.model.explore.ExploreContext;
import sorcer.core.context.model.explore.Explorer;
import sorcer.model.rs.RosenSuzukiMultiFidelityModelBuilder;
import sorcer.service.Strategy.Opti;
import sorcer.service.Task;
import sorcer.util.Log;
import sorcer.vfe.Exploration;
import sorcer.vfe.Optimization;
import sorcer.vfe.OptimizationModeling;
import sorcer.vfe.util.VarInfoList;
import engineering.optimization.conmin.provider.ConminOptimizerJNA;
import engineering.optimization.conmin.provider.ConminState;
import engineering.optimization.conmin.provider.ConminStrategy;


public class RosenSuzukiExplorerRequestor {

	private static Logger logger = Log.getTestLog();
	private static boolean isIntra = false;
	private static boolean isEOL = false;

	public static void main(String[] args) throws Exception {
		System.setSecurityManager(new RMISecurityManager());
		logger.info("is intraprocess: " + args[0] + " DSL: " + args[1]);
		isIntra = "intra".equals(args[0]);
		isEOL = "eol".equals(args[1]);
		if (isEOL) {
			if (isIntra)
				eolIntraExplore();
			else
				eolInterExplore();
		} else
			explore();
	}
	
	private static void explore() throws Exception {
		//int ndv = 4;
		//int ncon = 3;
		//0.34363963270405057 x2 = 0.3790520940478045 x3 = 1.9306729315827758 x4 = -1.207981204830921
		ExploreContext exploreContext = new ExploreContext("Rosen-Suzuki");
		//VarInfoList designInfo = varsInfo(varInfo("x1", 0.34363963270405057), varInfo("x2", 0.3790520940478045), varInfo("x3", 1.9306729315827758), varInfo("x4", -1.207981204830921));
		VarInfoList designInfo = varsInfo(varInfo("x1", 1.0), varInfo("x2", 1.0), varInfo("x3", 1.0), varInfo("x4", 1.0));
		//VarInfoList designInfo = varsInfo(varInfo("x1", 0.0), varInfo("x2", 1.0), varInfo("x3", 2.0), varInfo("x4", -1.0));
		exploreContext.setDesignVarsInfo(designInfo);
		ConminStrategy cmnStrategy=new ConminStrategy(new File(System.getProperty("conmin.strategy.file")));
		DispatcherStrategy dispStrategy = new DispatcherStrategy(new File(System.getProperty("dispatcher.strategy.file")));
		//ConminStrategy cmnStrategy=new ConminStrategy(new File("/Users/sobol/workspace/iGrid-OS.1.1/modules/engineering/optimization/conmin/data/conminrosenSuzukiMin.dat"));
		// now done in initializeSearchContext in Dispatcher
		//ConminState cmnState = new ConminState(ndv,ncon);
		//List parameterInfo = list(varInfo("p1", 60.0));
		//modelContext.setParametersInfo(parameterInfo);
		//exploreContext.setResponsesInfo(null);
		exploreContext.setObjectiveVarsInfo(null);
		exploreContext.setConstraintVarsInfo(null);
		exploreContext.setObjectivesGradientInfo(null);
		exploreContext.setConstraintsGradientInfo(null);
		exploreContext.setOptimizerStrategy(cmnStrategy);
		exploreContext.setDispatcherStrategy(dispStrategy);
		//exploreContext.setOptimizerState(cmnState);
		// specify the explorer dispatched
		exploreContext.setDispatcherSignature(sig(RosenSuzukiDispatcherMF.class));
		// model listener for the model updates/configuration
		//exploreContext.setModelListenerSignature(sig(RosenSuzukiModelManager.class));

		Task opti = null;
		ExploreContext optiContext = null;
		if (isIntra) {
			// use local service providers as specified by class-based signatures
			exploreContext.setModelSignature(sig("createModel", RosenSuzukiMultiFidelityModelBuilder.class));
			exploreContext.setOptimizerSignature(sig(ConminOptimizerJNA.class));
			opti = task("opti intra", sig("explore", Explorer.class), exploreContext);
			opti = exert(opti);
			optiContext = (ExploreContext)opti.getContext();
		} else {
			// use remote service providers as specified by service-type signatures
			exploreContext.setModelSignature(sig("register", OptimizationModeling.class, "Rosen-Suzuki MF-Model"));
			exploreContext.setOptimizerSignature(sig("register", Optimization.class));
			opti = task("opti inter", sig("explore", Exploration.class), exploreContext);
			opti = exert(opti);
			optiContext = (ExploreContext)opti.getContext();

		}
		logger.info(">>>>>>>>>>>>> exceptions: " + opti.getExceptions());
		logger.info(">>>>>>>>>>>>> exploration results: " + ((ConminState)optiContext.getOptimizerState()));
	}

	private static void eolInterExplore() throws Exception {
		Task optiTask = optiTask(sig("explore", Exploration.class, "Rosen-Suzuki Explorer"),
				context(initialDesign(input("x1", 1.0), input("x2", 1.0), input("x3", 1.0), input("x4", 1.0)),
						par("optimizer/strategy", new ConminStrategy(new File(System.getProperty("conmin.strategy.file")))),
						par("dispatcher/strategy", new DispatcherStrategy(new File(System.getProperty("dispatcher.strategy.file")))),
						result("exploration/results")),
						strategy(Opti.MIN,
								dispatcher(sig(RosenSuzukiDispatcherMF.class)),
								model(sig("register", OptimizationModeling.class, "Rosen-Suzuki MF-Model")),
								optimizer(sig("register", Optimization.class, "Rosen-Suzuki Optimizer"))));

		Object out = value(optiTask);
		logger.info(">>>>>>>>>>>>> exploration results: " + out);
	}

	private static void eolIntraExplore() throws Exception {
		Task optiTask = optiTask(sig("explore", Explorer.class),
				context(initialDesign(input("x1", 1.0), input("x2", 1.0), input("x3", 1.0), input("x4", 1.0)),
						par("optimizer/strategy", new ConminStrategy(new File(System.getProperty("conmin.strategy.file")))),
						par("dispatcher/strategy", new DispatcherStrategy(new File(System.getProperty("dispatcher.strategy.file")))),
						result("exploration/results")),
						strategy(Opti.MIN, 
								dispatcher(sig(RosenSuzukiDispatcherMF.class)),
								model(sig("createModel", RosenSuzukiMultiFidelityModelBuilder.class)),
								optimizer(sig(ConminOptimizerJNA.class))));

		Object out = value(optiTask);
		logger.info(">>>>>>>>>>>>> exploration results: " + out);
	}
}
