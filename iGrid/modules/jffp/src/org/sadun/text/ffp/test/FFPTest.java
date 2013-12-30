/*
 * Created on Jan 19, 2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp.test;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

import org.sadun.text.ffp.AbortFFPException;
import org.sadun.text.ffp.ConstantFoundInLineCondition;
import org.sadun.text.ffp.FFPParseException;
import org.sadun.text.ffp.FlatFileParser;
import org.sadun.text.ffp.LineFormat;
import org.sadun.text.ffp.Type;

/**
 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano
 *         Sadun</a>
 * @version 1.0
 */
public class FFPTest extends TestCase {

	/**
	 * A listener which expects a line of data for a given format, tries to match expected values to parsed values,
	 * and throws a RuntimeException if there's no match
	 *
	 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano Sadun</a>
	 * @version 1.0
	 */
	class FFPTestListener implements FlatFileParser.Listener {

		private LineFormat expectedFormat;
		private String[] expectedData;
		private int expectedLogicalLine;
		
		FFPTestListener(LineFormat expectedFormat, String[] expectedData, int expectedLogicalLine) {
			this.expectedFormat=expectedFormat;
			this.expectedData=expectedData;
			this.expectedLogicalLine=expectedLogicalLine;
		}

		public void lineParsed(
			LineFormat format,
			int logicalLineCount,
			int physicalLineCount,
			String[] values) {
			if (expectedFormat.equals(format))
				if (expectedLogicalLine == logicalLineCount) {
					assertEquals(values.length, test00Data.length);
					for (int i = 0; i < values.length; i++) {
						assertEquals(values[i], test00Data[i]);
					}
				} else
					throw new RuntimeException(
						"Unexpected logical line #" + logicalLineCount);
		}
	}

	private FlatFileParser ffp;
	protected LineFormat test00format; // TEST00 format
	protected String[] test00Data; // Array of TEST00 data
	protected String test00;
	// Line to parse, composed by concatenating betfor00Data elements

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(FFPTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		ffp = new FlatFileParser();
		setupBetFor00(ffp);
	}

	protected void setupBetFor00(FlatFileParser ffp) {
		test00format = new LineFormat("test format 00");
		test00format.defineNextField("header", 40);
		test00format.defineNextField(
			"transaction code",
			48,
			Type.CONSTANT,
			"TEST0000");
		test00format.defineNextField("id", 59, Type.NUMERIC);
		test00format.defineNextField("division", 70, Type.ALFA);
		test00format.defineNextField("control", 74, Type.ALFA);
		test00format.defineNextField("reserved1", 80, Type.ALFA);
		test00format.defineNextField("production date", 84, Type.NUMERIC);
		test00format.defineNextField("password", 94, Type.ALFA);
		test00format.defineNextField(
			"version",
			104,
			Type.CONSTANT,
			"VERSION002");
		test00format.defineNextField("new password", 114, Type.ALFA);
		test00format.defineNextField("operator number", 125, Type.ALFA);
		test00format.defineNextField("crypt:segment-user", 126, Type.ALFA);
		test00format.defineNextField("crypt:segment-date", 132, Type.NUMERIC);
		test00format.defineNextField("crypt:del-keyring", 152, Type.NUMERIC);
		test00format.defineNextField("crypt:segment-how", 153, Type.ALFA);
		test00format.defineNextField("reserved2", 295, Type.ALFA);
		test00format.defineNextField("kreb:segmentuser", 296, Type.ALFA);
		test00format.defineNextField("reserved3", 320, Type.ALFA);

		test00Data =
			new String[] {
				"0123456789012345678901234567890123456789",
				"TEST0000",
				"00000000001",
				"00000000002",
				"5555",
				"XXXXXX",
				"0131",
				"    passwd",
				"VERSION002",
				"xxxxxxxxxx",
				"  OPERATOR1",
				"A",
				"013104",
				"12345678901234567890",
				"h",
				"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
				"a",
				"XXXXXXXXXXXXXXXXXXXXXXXX" };

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < test00Data.length; i++) {
			sb.append(test00Data[i]);
		}
		test00 = sb.toString();
		
		
	 	ffp.declare(
				new ConstantFoundInLineCondition(test00format),
				test00format);
		/*
		ffp.declare(
			new MatchingRegexpInLineCondition("TEST*", test00format, 2),
			test00format);
		*/
	}

	
	public void testParseLine() throws IOException, FFPParseException {
		ffp.addListener(new FFPTestListener(test00format, test00Data, 1));
		ffp.parse(new StringReader(test00));
	}
	
	public void testParseLineNoMatch() throws IOException, FFPParseException {
		ffp.setFailOnNoMatchingConditions(false);
		final int [] unmatched=new int[1];
		ffp.addListener(new FlatFileParser.UnmatchedLineListener() {
			public void lineParsed(LineFormat format,int logicalLinecount,int physicalLineCount,String[] values) {
				// Do nothing
			}
			public void noMatchingCondition(
				int physicalLineCount,
				String line) {
				unmatched[0]++;
			}
		});
		ffp.parse(new StringReader("UNMATCHING LINE"));
		assertEquals(1, unmatched[0]);
	}
	
	public void testParseLineNoFail() throws IOException, FFPParseException {
		ffp.setFailOnLineParsingError(false);
		System.out.println("Note: a warning should be correctly issued now");
		ffp.parse(new StringReader(test00+System.getProperty("line.separator")+"UNMATCHING LINE"));
	}
		
	public void testParseAsXML() throws IOException, FFPParseException { 
		ffp.addListener(new FlatFileParser.Listener() { 
			public void  lineParsed(LineFormat format, int p, int l, String[] values) {
			  System.out.println(format.getName());
			  System.out.println(format.formatValuesAsXML(values)); 
			}
		 });
		ffp.parse(new StringReader(test00));
	}
	
	public void testDispatch() {
	    ffp.addDispatch(test00format, new FlatFileParser.Listener() {
	        public void lineParsed(LineFormat format, int logicalLinecount,
	                int physicalLineCount, String[] values)
	                throws AbortFFPException {
	            System.out.println("Event dispatched to specialized listener");
	        } 
	    });
	}

}

	
	  
	  
	  
	 


