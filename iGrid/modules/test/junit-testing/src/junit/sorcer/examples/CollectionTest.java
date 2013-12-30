package junit.sorcer.examples;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.runner.*;
import junit.framework.JUnit4TestAdapter;
    
/**
 * A sample test case, testing <code>java.util.ArrayList</code>.
 */
public class CollectionTest {
    
    private Collection<String> collection;

    @Before
    public void setUp() {
        collection = new ArrayList<String>();
    }

    @After
    public void tearDown() {
        collection.clear();
    }

    @Test
    public void testEmptyCollection() {
        assertTrue(collection.isEmpty());
    }

    @Test
    public void testOneItemCollection() {
        collection.add("itemA");
        assertEquals(1, collection.size());
    }
    
    /** For compatibility to run a suit with a TestRunner in JUnit 3 */
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(CollectionTest.class);
	}
	
	/** For compatibility to run a suit with a TestRunner in JUnit 3 */
	public static void testIt () {
		junit.textui.TestRunner.run (suite());
	}
	
    public static void main(String args[]) {
        org.junit.runner.JUnitCore.main("CollectionTest");
    }
    
    public static boolean wasSuccessful() {
        // use this invocation for programmatic testing
        Result result = org.junit.runner.JUnitCore.runClasses(CollectionTest.class);
        return result.wasSuccessful();
    }
}