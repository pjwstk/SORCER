/*
 * Created on Jan 16, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp;

/**
 * The main parsing-related exception. Any other FFP-related exception is
 * a subclass of this.
 * 
 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano Sadun</a>
 * @version 1.0
 */
public abstract class FFPParseException extends Exception {

	FFPParseException(String msg) {
		super(msg);
	}
	
	FFPParseException(String msg, Throwable t) {
		super(msg,t);
	}
}
