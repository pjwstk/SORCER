/*
 * Created on Jan 17, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp;

/**
 * An exception raised when parsing a {@link org.sadun.text.ffp.LineFormat} and 
 * an expected field cannot be found since the line is too short.
 * 
 * @author Cristiano Sadun
 */
public class FormatOutOfRangeException extends FieldRelatedFFPParseException {

	private CharSequence line;
	private LineFormat format;
	

	/**
	 * @param msg
	 */
	FormatOutOfRangeException(CharSequence line, LineFormat format, LineFormat.FieldInfo info) {
		super("Expected "
		+ info
		+ ", out of range in line '"
		+ line
		+ "' ("
		+ line.length()
		+ " characters long)", info);
		this.line=line;
		this.format=format;
	}

	/**
	 * @return
	 */
	public LineFormat getFormat() {
		return format;
	}

	/**
	 * @return
	 */
	public String getLine() {
		return line.toString();
	}

	
}
