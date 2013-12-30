import static sorcer.eo.operator.exert;
import static sorcer.eo.operator.op;
import static sorcer.eo.operator.signature;
import static sorcer.eo.operator.task;
import static sorcer.vo.operator.varInfo;
import static sorcer.vo.operator.varsInfo;

import java.io.File;

import sorcer.core.context.model.Exploration;
import sorcer.core.context.model.ExploreContext;
import sorcer.core.context.model.Explorer;
import sorcer.core.context.model.Modeling;
import sorcer.core.context.model.Optimization;
import sorcer.explorer.rs.RosenSuzukiDispatcher;
import sorcer.model.rs.RosenSuzukiModelCreator;
import sorcer.service.Exertion;
import sorcer.service.Task;
import sorcer.service.Signature.Type;
import sorcer.vfe.util.VarInfoList;
import engineering.optimization.conmin.provider.ConminOptimizerJNA;
import engineering.optimization.conmin.provider.ConminStrategy;

ExploreContext exploreContext = new ExploreContext("Rosen-Suzuki");
VarInfoList designInfo = varsInfo(varInfo("x1", 1.0d), varInfo("x2", 1.0d), varInfo("x3", 1.0d), varInfo("x4", 1.0d));
exploreContext.setDesignVarsInfo(designInfo);
ConminStrategy cmnStrategy=new ConminStrategy(new File(System.getProperty("conmin.strategy.file")));
exploreContext.setObjectivesInfo(null);
exploreContext.setConstraintsInfo(null);
exploreContext.setObjectivesGradientInfo(null);
exploreContext.setConstraintsGradientInfo(null);
exploreContext.setOptimizerStrategy(cmnStrategy);

// specify the explorer dispatched
exploreContext.setDispatcherSignature(signature(null, RosenSuzukiDispatcher.class));

// use service providers as specified by signatures
exploreContext.setModelSignature(signature("register", Modeling.class));
exploreContext.setOptimizerSignature(signature("register", Optimization.class));
Task optiTask = task("opti", op("explore", Exploration.class), exploreContext);

