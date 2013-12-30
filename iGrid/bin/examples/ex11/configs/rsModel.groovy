import static sorcer.collection.operator.loop;
import static sorcer.collection.operator.names;
import static sorcer.vo.operator.designVars;
import static sorcer.vo.operator.differentiation;
import static sorcer.vo.operator.evaluation;
import static sorcer.vo.operator.gradient;
import static sorcer.vo.operator.linkedVars;
import static sorcer.vo.operator.parameterVars;
import static sorcer.vo.operator.realization;
import static sorcer.vo.operator.responseModel;
import static sorcer.vo.operator.responseVars;
import static sorcer.vo.operator.var;
import static sorcer.vo.operator.wrt;
import sorcer.core.context.model.ResponseModel;

int designVarCount = 4;

ResponseModel sm = responseModel("Rosen-Suzuki",
designVars("x", designVarCount),
linkedVars("xl1"),
parameterVars(var("p1", 50.0)),
responseVars("f"),
	realization("f", evaluation("fe1"), evaluation("fe2"),
		differentiation("fe1", wrt(names(loop(1, 4), "x")), gradient("fe1g1"), gradient("fe1g2")),
		differentiation("fe2", wrt("f1", "f3", "f4", "x3", "xl1"), gradient("fe2g1"), gradient("fe2g2"))),
responseVars("f",4),
	realization("f1", differentiation(wrt("x1", "x2"))),
	realization("f2", differentiation(wrt("x1", "x2"))),
	realization("f3", differentiation(wrt("f2"))),
	realization("f4", evaluation("f4e"),
		differentiation("f4e", wrt("x3", "x4"), gradient("f4eg1"), gradient("f4eg2"))),
	responseVars("g", 3),
	realization("g1", evaluation("g1e1"), evaluation("g1e2"),
		differentiation("g1e1", wrt(names(loop(4), "x")), gradient("g1e1g1"), gradient("g1e1g2")),
		differentiation("g1e2", wrt("f1", "f2", "x2", "x3", "x4"), gradient("g1e2g1"), gradient("g1e2g2"))),
	realization("g2", differentiation(wrt(names(loop(4), "x")))),
	realization("g3", differentiation(wrt("x1", "x2", "x3", "x4"))));

return sm;