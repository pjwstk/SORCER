/*
 * Created on Jan 16, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp;

/**
 * 
 * @author Cristiano Sadun
 */
public class NoMatchingConditionException extends FFPParseException {

	/**
	 * @param msg
	 */
	public NoMatchingConditionException(int physicalLineCount, String line) {
		super("No matching condition for line "+physicalLineCount+", after '"+line+"'");
	}

}
