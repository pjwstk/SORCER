package junit.sorcer.examples;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
 
/**
 * JUnit Parameterized Test *
 */

import java.util.Arrays;
import java.util.Collection;
 
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
 
/**
 * JUnit Parameterized Test
 */
@RunWith(Parameterized.class)
public class SummingTest {
 
	 private int x1, x2;
 
	 public SummingTest(int a, int b) {
	    x1 = a;
		x2 = b;
	 }
 
	 @Parameters
	 public static Collection<Object[]> data() {
	   Object[][] data = new Object[][] { { 1, 5 }, { 2, 6 }, { 3, 7 }, { 4, 8 } };
	   return Arrays.asList(data);
	 }
 
	 @Test
	 public void summerTest() {
	   System.out.println("Parameterized Sum is: " + (x1 + x2));
	 }
}