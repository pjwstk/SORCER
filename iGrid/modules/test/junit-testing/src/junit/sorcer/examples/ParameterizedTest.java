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

@RunWith(value = Parameterized.class)
public class ParameterizedTest {
 
	 private int number;
 
	 public ParameterizedTest(Object number) {
		 if (number instanceof Integer)
			 this.number = (Integer)number;
		 else if (number instanceof String)
			 this.number = Integer.parseInt((String)number);
	 }
 
	 @Parameters
	 public static Collection<Object[]> data() {
	   Object[][] data = new Object[][] { { 1 }, { 2 }, { 3 }, { "10" }, { "11" } };
	   return Arrays.asList(data);
	 }
 
	 @Test
	 public void pushTest() {
	   System.out.println("Parameterized Number is : " + number);
	 }

}