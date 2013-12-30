/*
 * Created on Jan 17, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp;

/**
 * An exception raised when a line is parsed correctly by a {@link org.sadun.text.ffp.LineFormat}
 * but not all its characters are consumed when the parsing is terminated.
 * 
 * @see {@link org.sadun.text.ffp.LineFormat#isFailOnTrailingChars()}.
 * @author Cristiano Sadun
 */
public class TrailingCharactersException extends FFPParseException {

	private CharSequence line;
	private LineFormat format;
	private CharSequence trailing;

	/**
	 * @param msg
	 */
	public TrailingCharactersException(
		CharSequence line,
		LineFormat format,
		CharSequence trailing) {
		super(
			"The line '"
				+ line
				+ "' does not completely match the format '"
				+ format.getName()
				+ "' (trailing character"
				+ (trailing.length() > 1 ? "s" : "")
				+ ": '"
				+ trailing
				+ "')");
		this.line=line;
		this.format=format;
		this.trailing=trailing;
	}

	/**
	 * Return the {@link LineFormat} which has parsed the line. 
	 * @return the {@link LineFormat} which has parsed the line.
	 */
	public LineFormat getFormat() {
		return format;
	}

	/**
	 * Return the line which has trailing characters.
	 * @return the line which has trailing characters.
	 */
	public String getLine() {
		return line.toString();
	}

	/**
	 * Return the trailing sequence.
	 * @return the trailing sequence.
	 */
	public CharSequence getTrailing() {
		return trailing;
	}

}
