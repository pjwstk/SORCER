package sorcer.greeter;

import java.util.logging.Logger;

import org.junit.runner.Result;

public class JUnitGreeterTester {
	private final static Logger logger = Logger.getLogger(JUnitGreeterTester.class.getName());
	
	public static void main(String[] args) {
		
		Result result = org.junit.runner.JUnitCore.runClasses(junit.sorcer.greeter.GreeterTest.class);
		logger.info("Time elapsed: " + result.getRunTime() + " ms");
		logger.info("Failures: " + result.getFailureCount());
	}
}
