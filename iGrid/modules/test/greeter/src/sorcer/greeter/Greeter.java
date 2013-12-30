package sorcer.greeter;

import java.util.logging.Logger;

public class Greeter {
	private final static Logger logger = Logger.getLogger(Greeter.class.getName());
	
	public String sayHello() {
		String message = "Hello SORCER!";
		logger.exiting(this.getClass().getName(), "sayHello", message);
		return message;
	}
}
