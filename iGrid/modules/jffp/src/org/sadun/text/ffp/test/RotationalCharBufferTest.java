/*
 * Created on Jan 19, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp.test;

import junit.framework.TestCase;

import org.sadun.util.RotationalCharBuffer;

/**
 *
 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano Sadun</a>
 * @version 1.0
 */
public class RotationalCharBufferTest extends TestCase {

	private RotationalCharBuffer rcb;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		rcb=new RotationalCharBuffer(3);
	}
	
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(RotationalCharBufferTest.class);
	}
	
	public void testIncomplete() {
		rcb.addToRight("X");
		assertEquals(rcb.toString(), "X");
	}
	
	public void testAddRight() {
		rcb.addToRight("h");
		rcb.addToRight("e");
		rcb.addToRight("l");
		rcb.addToRight("l");
		rcb.addToRight("o");
		//System.out.println(rcb.toString());
		assertEquals("llo", rcb.toString());
	}
	
	public void testAddLeft() {
		rcb.addToLeft("o");
		rcb.addToLeft("l");
		rcb.addToLeft("l");
		rcb.addToLeft("e");
		rcb.addToLeft("h");
		System.out.println(rcb.toString());
		assertEquals("hel", rcb.toString());
	}
	
}
