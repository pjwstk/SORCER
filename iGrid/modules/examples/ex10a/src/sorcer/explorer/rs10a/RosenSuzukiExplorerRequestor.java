package sorcer.explorer.rs10a;

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
import java.io.PrintWriter;
import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import sorcer.core.context.model.explore.ExploreContext;
import sorcer.core.context.model.explore.Explorer;
import sorcer.model.rs.RosenSuzukiModelBuilder;
import sorcer.service.Exertion;
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
		ExploreContext exploreContext = new ExploreContext("Rosen-Suzuki");
		exploreContext.setType(Opti.MIN);
		//VarInfoList parameterInfo = varsInfo(varInfo("p1", 60.0));
		//modelContext.setParametersInfo(parameterInfo);
		VarInfoList designInfo = varsInfo(varInfo("x1", 1.0), varInfo("x2", 1.0), varInfo("x3", 1.0), varInfo("x4", 1.0));
		exploreContext.setInputVarsInfo(designInfo);
		ConminStrategy cmnStrategy = new ConminStrategy(new File(System.getProperty("conmin.strategy.file")));
		exploreContext.setOptimizerStrategy(cmnStrategy);
		exploreContext.setModelBuilder(sig("createModel", RosenSuzukiModelBuilder.class));
		//exploreContext.setResponsesInfo(null);
		exploreContext.setObjectiveVarsInfo(null);
		exploreContext.setConstraintVarsInfo(null);
		exploreContext.setObjectivesGradientInfo(null);
		exploreContext.setConstraintsGradientInfo(null);
		
		//exploreContext.setOptimizerState(cmnState);
		// specify the explorer dispatched
		exploreContext.setDispatcherSignature(sig(RosenSuzukiDispatcher.class));
		
		Task opti = null;
		ExploreContext optiContext =  null;
		if (isIntra) {
			// optimizer and model are initialized by by Explorer using corresponding signatures
			exploreContext.setModelSignature(sig("createModel", RosenSuzukiModelBuilder.class));
			exploreContext.setOptimizerSignature(sig(ConminOptimizerJNA.class));
			opti = task("opti intra", sig("explore", Explorer.class), exploreContext);
			opti = exert(opti);
			optiContext = (ExploreContext)opti.getContext();
		}
		else {
			// service providers specified by signatures
			exploreContext.setModelSignature(sig("register", OptimizationModeling.class, "Rosen-Suzuki Model"));
			exploreContext.setOptimizerSignature(sig("register", Optimization.class, "Rosen-Suzuki Optimizer"));
			opti = task("opti", sig("explore", Exploration.class, "Rosen-Suzuki Explorer"), exploreContext);
			opti = exert(opti);
			optiContext = (ExploreContext)opti.getContext();
		}
		logger.info(">>>>>>>>>>>>> exceptions: " + opti.getExceptions());
		logger.info(">>>>>>>>>>>>> exploration results: " + ((ConminState)optiContext.getOptimizerState()));
		
		// output for JUnit test
		File out = new File(System.getenv("IGRID_HOME") + "/modules/sorcer/test/rs/data/opti-test.out");
		PrintWriter pw = new PrintWriter(out);
		pw.println(((ConminState)optiContext.getOptimizerState()).getOBJ());
		pw.close();
	}

	private static void eolInterExplore() throws Exception {
		Task optiTask = optiTask(sig("explore", Exploration.class, "Rosen-Suzuki Explorer"),
				context(initialDesign(input("x1", 1.0), input("x2", 1.0), input("x3", 1.0), input("x4", 1.0)),
						par("optimizer/strategy", new ConminStrategy(new File(System.getProperty("conmin.strategy.file")))),
						//needs serialized builder classes, builder is set in the model itself
//						par("model/builder", sig("createModel", RosenSuzukiModelBuilder.class)),
						result("exploration/results")),
				strategy(Opti.MIN,
						dispatcher(sig(RosenSuzukiDispatcher.class)),
						model(sig("register", OptimizationModeling.class, "Rosen-Suzuki Model")),
						optimizer(sig("register", Optimization.class, "Rosen-Suzuki Optimizer"))));
		
		Object out = value(optiTask);
		logger.info(">>>>>>>>>>>>> exploration results: " + out);
	}
	
	private static void eolIntraExplore() throws Exception {
		Task optiTask = optiTask(sig("explore", Explorer.class),
				context(initialDesign(input("x1", 1.0), input("x2", 1.0), input("x3", 1.0), input("x4", 1.0)),
						par("optimizer/strategy", new ConminStrategy(new File(System.getProperty("conmin.strategy.file")))),
						result("exploration/results")),
				strategy(Opti.MIN, 
						dispatcher(sig(RosenSuzukiDispatcher.class)),
						model(sig("createModel", RosenSuzukiModelBuilder.class)),
						optimizer(sig(ConminOptimizerJNA.class))));						

		Object out = value(optiTask);
		logger.info(">>>>>>>>>>>>> exploration results: " + out);
	}
}
