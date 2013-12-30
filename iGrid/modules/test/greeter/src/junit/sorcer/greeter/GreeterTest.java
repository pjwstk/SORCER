package junit.sorcer.greeter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import sorcer.greeter.Greeter;

/**
 * @author Mike Sobolewski
 */

public class GreeterTest {
	private final static Logger logger = Logger.getLogger(GreeterTest.class.getName());
	private Greeter greeter;

	@Before
	public void setUp() {
		greeter = new Greeter();
	}

	@Test
	public void testSayHello() {
		String hello = greeter.sayHello();
		logger.info("Greeting: " + hello);
		// JUnit assert
		assertEquals(hello, "Hello SORCER!");
		
		assertTrue("Wrong say hello", hello.equals("Hello SORCER!"));
		
		// Java assert
		//assert hello.equals("Hello SORCER!") : "Hello SORCER!";

	}

	/**
	 * Needed for compatibility with JUnit 3.x runner.
	 */
	public static junit.framework.Test suite() {
		return new junit.framework.JUnit4TestAdapter(GreeterTest.class);
	}

	/**
	 * Run the test directly via <code>main</code> that is much less important
	 * with the advent of IDE runners.
	 */
	public static void main(String args[]) {
		org.junit.runner.JUnitCore.main(GreeterTest.class.getName());

	}
}
