package sorcer.arithmetic;

import sorcer.arithmetic.Adder;
import sorcer.arithmetic.Divider;
import sorcer.arithmetic.Multiplier;
import sorcer.arithmetic.Subtractor;

public interface Arithmetic extends Adder, Subtractor, Divider, Multiplier {
	
	public final String ADD = "add";

	public final String SUBTRACT = "subtract";

	public final String MULTIPLY = "multiply";

	public final String DIVIDE = "divide";
	
	public final String AVERAGE = "average";
}