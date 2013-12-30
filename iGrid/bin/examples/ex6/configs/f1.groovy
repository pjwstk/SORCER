import static sorcer.eo.operator.context;
import static sorcer.eo.operator.input;
import static sorcer.eo.operator.job;
import static sorcer.eo.operator.op;
import static sorcer.eo.operator.output;
import static sorcer.eo.operator.path;
import static sorcer.eo.operator.pipe;
import static sorcer.eo.operator.task;
import sorcer.arithmetic.provider.Adder;
import sorcer.arithmetic.provider.Multiplier;
import sorcer.arithmetic.provider.Subtractor;
import sorcer.service.Job;
import sorcer.service.Task;

String arg = "arg", result = "result";
String x1 = "x1", x2 = "x2", y = "y";

Task f3 = task("f3", op("subtract", Subtractor.class),
   context("subtract", input(path(arg, x1), null), input(path(arg, x2), null),
	  output(path(result, y), null)));

Task f4 = task("f4", op("multiply", Multiplier.class),
		   context("multiply", input(path(arg, x1), 10.0d), input(path(arg, x2), 50.0d),
			  output(path(result, y), null)));

Task f5 = task("f5", op("add", Adder.class),
   context("add", input(path(arg, x1), 20.0d), input(path(arg, x2), 80.0d),
	  output(path(result, y), null)));

// Function Composition f3(f4(x1, x2), f5(x1, x2), f3(x1, x2))
Job f1 = job("f1", f4, f5, f3,
   pipe(output(f4, path(result, y)), input(f3, path(arg, x1))),
   pipe(output(f5, path(result, y)), input(f3, path(arg, x2))));

return f1;