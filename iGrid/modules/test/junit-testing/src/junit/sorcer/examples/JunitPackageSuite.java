package junit.sorcer.examples;

import junit.framework.Test;
import junit.framework.TestSuite;

public class JunitPackageSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for junit.sorcer");
		//$JUnit-BEGIN$
		suite.addTest(ListTest.suite());
		suite.addTest(SimpleTest.suite());
		suite.addTest(CollectionTest.suite());
		suite.addTest(CollectionAllTest.suite());
		//$JUnit-END$
		return suite;
	}

}
