import static sorcer.eo.operator.context;
import static sorcer.eo.operator.input;
import static sorcer.eo.operator.op;
import static sorcer.eo.operator.output;
import static sorcer.eo.operator.path;
import static sorcer.eo.operator.task;
import sorcer.arithmetic.provider.Adder;
import sorcer.service.Task;

Task f5 = task("f5", op("add", Adder.class),
   context("add", input(path("arg", "x1"), 20.0d), input(path("arg", "x2"), 80.0d),
	  output(path("result", "y"), null)));
