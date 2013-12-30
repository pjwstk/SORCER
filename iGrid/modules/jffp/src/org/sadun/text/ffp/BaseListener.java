/*
 * Created on Jan 17, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sadun.text.ffp.FlatFileParser.Listener;

/**
 * A base implementation of {@link org.sadun.text.ffp.FlatFileParser.Listener} which
 * activates only when on a specific {@link LineFormat line format} name (or a specific regular expression match)
 * is successfully parsed.
 * <p>
 * Multiple listeners can be registered at a {@link org.sadun.text.ffp.FlatFileParser}
 * each reacting to a specific {@link LineFormat line format} name.
 * <p>
 * When {@link #matched(String, String[])} is executed, the method compares the received format name
 * to the string or regular expression passed at construction and if they correspond, invokes 
 * {@link #onFormatName(String, String[])}.
 * <p>
 * The net result is that the reaction code does not need to perform checks on the format name,
 * since it's invoked only if a match is verified. For example,  
 * <p>
 * <pre>
 *  FlatFileParser ffp = new FlatFileParser();
 * 
 *  ..set up two LineFormat objects, one named "header", the other "data"..
 * 
 *  ffp.addListener(new BaseListener("header") {
 *   protected void onFormatName(String formatName, String[] values) {
 *     System.out.println("HEADER line");
 *   }
 *  });
 *  ffp.addListener(new BaseListener("data") {
 *   protected void onFormatName(String formatName, String[] values) {
 *     System.out.println("DATA line");
 *   }
 *  });
 * </pre>
 * 
 * @author Cristiano Sadun
 */
public abstract class BaseListener implements Listener {

	private String formatName;
	private Pattern pattern;

	/**
	 * Create a listener which matches exactly the given line format name.
	 * @param formatName the name of the desired {@link LineFormat}
	 */
	protected BaseListener(String formatName) {
		this(formatName, false);
	}

	/**
	 * Create a listener which matches the given line format name or the given regular expression.
	 * 
	 * @param formatName the format name or regular expression
	 * @param isRegExp <b>true</b> if the given formatName is to be treated as a regular expression
	 */
	protected BaseListener(String formatName, boolean isRegExp) {
		this.formatName=formatName;
		if (isRegExp) pattern=Pattern.compile(formatName); 
	}

	/**
	 * This implementation checks wether the received format name matches the 
	 * name or regular expression provided at construction, and invokes {@link #onFormatName(String, String[])}
	 * if so.
	 */
	public final void lineParsed(LineFormat format, int logicalLineCount, int physicalLineCount, String[] values) throws AbortFFPException {
		boolean exec=false;
		String formatName=format.getName();
		if (pattern!=null) {
			Matcher matcher = pattern.matcher(formatName);
			if (matcher.matches()) exec=true;
		} else {
			if (formatName.equals(this.formatName)) exec=true;
		}
		if (exec) onFormatName(format, logicalLineCount, physicalLineCount, values);
	}

	/**
	 * Invoked when a line whose format name matches the name or regexp provided at construction is found.
	 *  
	 * @param formatName the received format name
	 * @param values the parsed values
	 */
	protected abstract void onFormatName(LineFormat format, int logicalLineCount, int physicalLineCount, String[] values) throws AbortFFPException;
	
}
