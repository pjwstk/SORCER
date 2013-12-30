/*
 * Created on Jan 19, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp.test;

import junit.framework.TestCase;

import org.sadun.text.ffp.FFPParseException;
import org.sadun.text.ffp.LineFormat;

/**
 *
 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano Sadun</a>
 * @version 1.0
 */
public class LineFormatTest extends TestCase {

	private LineFormat format;
	
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(LineFormatTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		format = new LineFormat();
		format.declareLineImage(1, "##### /[a,bb #####");
		format.declareLineImage(2, "#####");
		format.setLineSeparator("\r\n");
        
	}
	
	public void testParse() throws FFPParseException {
		String [] values = format.parse("12345a 67890\r\n54321", true);
		assertEquals("12345", values[0]);
		assertEquals("a", values[1]);
		assertEquals("67890", values[2]);
		assertEquals("54321", values[3]);
	}
	
	public void testParseToXML() throws FFPParseException {
		String xml = format.parseToXml("12345a 67890\r\n54321",true);
		}
}
