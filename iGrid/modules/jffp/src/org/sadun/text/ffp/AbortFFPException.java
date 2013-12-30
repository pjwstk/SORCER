/*
 * Created on Jan 1, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp;

/**
 * An exception of this class can be raised by an {@link FlatFileParser.AdvancedListener}
 * to signal problems at parsing start, termination or when using a {@link org.sadun.text.ffp.DispatcherListener}.
 * 
 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano Sadun</a>
 * @version 1.0
 */
public class AbortFFPException extends FFPParseException {

	/**
	 * @param msg
	 */
	public AbortFFPException(String msg) {
		super(msg);
	}
	
	public AbortFFPException(String msg, Throwable t) {
		super(msg,t);
	}

}
