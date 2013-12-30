/*
 * Created on Jan 20, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sadun.text.ffp.FlatFileParser.Condition;

/**
 * A condition that checks a regular expression in a physical line.
 * 
 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano Sadun</a>
 * @version 1.0
 */
public class MatchingRegexpInLineCondition implements Condition {
	
	private int physicalLine;
	private Pattern pattern;
	private int startPosition;

	/**
	 * Create a condition which will look for the given constant at the given position
	 * of the given physical line in the file.
	 * 
	 * @param physicalLine the physical line in which the constant is to be looked for
	 * @param regexp the regular expression to match
	 * @param startPosition the position in the physical line where to look for the constant
	 */
	public MatchingRegexpInLineCondition(int physicalLine, String regexp, int startPosition) {
		this.physicalLine=physicalLine;
		this.pattern=Pattern.compile(regexp);
		this.startPosition=startPosition;
	}

	/**
	 * Create a condition which will look for the given regexp at the given position
	 * of the next line in the file.
	 * 
	 * @param regexp the regular expression to match
	 * @param startPosition the position in the line where to look for the regexp
	 */
	public MatchingRegexpInLineCondition(String regexp, int startPosition) {
		this(1, regexp, startPosition);
	}
	
	/**
	 * Create a condition which will look at the value of the nth field of the given physical line in
	 * the given {@link LineFormat} object. 
	 * <p>
	 * The field must have type {@link Type#CONSTANT}.
	 * <p>
	 * This constructor is useful when a {@link LineFormat} has already been defined with
	 * the regexp value to be looked for by this condition.
	 *  
	 * @param format the {@link LineFormat} from which to get the regexp value
	 * @param regexp the regular expression to match  
	 * @param physicalLine the physical line of the field (starting with 1)
	 * @param n the number of the field (starting with 1)
	 */
	public MatchingRegexpInLineCondition(int physicalLine, String regexp, LineFormat format, int n) {
		this(physicalLine, regexp, format.getField(physicalLine, n).getStart());
	}
	
	/**
	 * Create a condition which will look at the value of the nth field of the given physical line in
	 * the given {@link LineFormat} object. 
	 * <p>
	 * The field must have type {@link Type#CONSTANT}.
	 * <p>
	 * This constructor is useful when a {@link LineFormat} has already been defined with
	 * the regexp value to be looked for by this condition.
	 *  
	 * @param format the {@link LineFormat} from which to get the regexp value
	 * @param regexp the regular expression to match  
	 * @param physicalLine the physical line of the field (starting with 1)
	 * @param n the number of the field (starting with 1)
	 */
	public MatchingRegexpInLineCondition(String regexp, LineFormat format, int n) {
		this(1, regexp, format.getField(1, n).getStart());
	}
	
	/**
	 * Attempt to match the regular expression and line position provided at construction to the physical line.
     *
	 * @see org.sadun.text.ffp.FlatFileParser.Condition#holds(int, int, org.sadun.text.ffp.FlatFileParser.LineReader)
	 */
	public boolean holds(
		int logicalLineCount,
		int physicalLineCount,
		FlatFileParser.LineReader reader) throws IOException {
		// Read as many physical lines as needed
		String line=readPhysicalLines(reader, physicalLine);
		if (line==null) return false;
		if (startPosition >= line.length()) return false;
		Matcher matcher=pattern.matcher(line);
		return matcher.find(startPosition);
	}

	/**
	 * Read n physical lines and returns the n-th.
	 * <p>
	 * If no enough lines exist, null is returned.
	 * 
	 * @param reader
	 * @param n
	 * @return
	 */
	protected String readPhysicalLines(FlatFileParser.LineReader reader, int n) throws IOException {
		String line=null;
		for(int i=0;i<n;i++) {
			line=reader.readLine();
			if (line==null) return null;
		}
		return line;
	}
	
	public String toString() {
		return "when characters starting at ["+startPosition+"] in the physical line "+physicalLine+" match the regular expression '"+pattern.pattern()+"'"; 	
	}


}
