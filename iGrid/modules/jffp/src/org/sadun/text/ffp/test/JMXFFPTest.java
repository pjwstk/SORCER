/*
 * Created on Jan 20, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp.test;

import javax.management.MBeanException;

import org.sadun.text.ffp.FlatFileParser;
import org.sadun.text.ffp.jmx.ManagedFlatFileParser;

/**
 *
 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano Sadun</a>
 * @version 1.0
 */
public class JMXFFPTest extends FFPTest {
	
	private class TestableFFP extends ManagedFlatFileParser {
		FlatFileParser getFFP() { return ffp; }
	}
	
	TestableFFP mffp; 

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(JMXFFPTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		mffp=new TestableFFP();
		setupBetFor00(mffp.getFFP());
		
	}
	
	public void testSListFormats() throws MBeanException {
		System.out.println(mffp.slistFormats(true));
	}
	
	

}
