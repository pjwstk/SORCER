/*
 * Created on Jan 17, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp;

import org.sadun.text.ffp.LineFormat.FieldInfo;

/**
 * An exception raised if a field value does not match its declared type.
 * 
 * @author Cristiano Sadun
 */
public class ValidationException extends FieldRelatedFFPParseException {

	/**
	 * @param info
	 * @param value
	 */
	public ValidationException(CharSequence line, FieldInfo info, String value) {
		super(	"In line '"+line+"', expected "+info.getType()+" field "
		+ info
		+ ", found value '"
		+ value
		+ "'", info);
	}


}
