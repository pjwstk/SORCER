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
public class IllegalRangeException extends RuntimeException {

	/**
	 * @param line
	 * @param start
	 * @param end
	 */
	public IllegalRangeException(CharSequence line, int start, int end) {
		super("Illegal range for line \""+line.toString()+"\": "+start+"-"+end);
	}

}
