import static sorcer.vo.operator.args;
import static sorcer.vo.operator.designVars;
import static sorcer.vo.operator.evaluator;
import static sorcer.vo.operator.linkedVars;
import static sorcer.vo.operator.parameterVars;
import static sorcer.vo.operator.responseVars;
import static sorcer.vo.operator.var;
import sorcer.core.context.model.AnalysisModel;
import sorcer.service.Configurable;
import sorcer.service.ConfigurationException;
import sorcer.service.Configurator;

new Configurator(configurable) {
			
	public Configurable configure() throws ConfigurationException {
		AnalysisModel	model = (AnalysisModel)configurable;
				
		try {
			var(model, "f", "fe1", evaluator("fe1", "x1^2-5.0*x1+x2^2-5.0*x2+2.0*x3^2-21.0*x3+x4^2+7.0*x4+50.0"),
				args("x1", "x2", "x3", "x4"));
			var(model, "g1", evaluator("g1e1", "x1^2+x1+x2^2-x2+x3^2+x3+x4^2-x4-8.0"),
				args("x1", "x2", "x3", "x4"));
			var(model, "g2", evaluator("g2e", "x1^2-x1+2.0*x2^2+x3^2+2.0*x4^2-x4-10.0"),
				args("x1", "x2", "x3", "x4"));
			var(model, "g3", evaluator("g3e", "2.0*x1^2+2.0*x1+x2^2-x2+x3^2-x4-5.0"),
				args("x1", "x2", "x3", "x4"));
			var(model, "xl1", evaluator("xl1e", "7*x4"), args("x4"));
			var(model, "f", "fe2", evaluator("fe2", "f1+f3+f4-21.0*x3+xl1+p1",
				args(designVars(model, "x3"), responseVars(model, "f1", "f3",
					"f4"), linkedVars(model, "xl1"), parameterVars(model, "p1"))));
			var(model, "f1", evaluator("f1e", "x1^2+x2^2"), args("x1","x2"));
			var(model, "f2", evaluator("f2e","x1+x2"), args("x1","x2"));
			var(model, "f3", evaluator("f3e", "-5.0*f2"), args("f2"));
			var(model, "f4", evaluator("f4e", "2.0*x3^2+x4^2"), args("x3","x4"));
			var(model, "g1", evaluator("g1e2", "f1+f2-2.0*x2+x3^2+x3+x4^2-x4-8.0"),
				args("f1", "f2", "x2","x3","x4"));
		} catch (Exception e) {
			throw new ConfigurationException();
		}
		return model;
	}
	
};
