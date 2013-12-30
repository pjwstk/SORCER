/*
 * Created on Jan 16, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp;

/**
 *
 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano Sadun</a>
 * @version 1.0
 */
public class InvalidPhysicalLineCountException extends FFPParseException {

	/**
	 * @param s
	 * @param i
	 * @param j
	 */
	public InvalidPhysicalLineCountException(String s, int found, int expected) {
		
		super("Invalid physical line count: expected "+expected+", found "+found+" in \""+s+"\"");
	}

}
