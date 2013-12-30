/*
 * Created on Jan 17, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.sadun.text.ffp.FlatFileParser.Condition;
import org.sadun.text.ffp.FlatFileParser.LineReader;

/**
 * A {@link org.sadun.text.ffp.FlatFileParser.Condition} that holds if and only if one the
 * conditions it groups hold.
 * 
 * @author Cristiano Sadun
 */
public class OrCondition implements Condition {
	
	private Condition[] conditions;

	/**
	 * Create an OrCondition based on two conditions
	 * @param c1 the first condition
	 * @param c2 the second condition
	 */
	public OrCondition(Condition c1, Condition c2) {
		this(new Condition[] { c1, c2} );
	}
	
	/**
	 * Create an OrCondition based on an array of conditions 
	 * @param conditions the array of conditions. 
	 */
	public OrCondition(Condition [] conditions) {
		assert conditions.length > 1;
		this.conditions=conditions;
	}

	/**
	 * The conditions are evaluated in the same order given at construction and if one holds, the 
	 * method returns <b>true</b>.
	 * 
	 * @return <b>false</b> if none of the conditions given at construction hold
	 * @see org.sadun.text.ffp.FlatFileParser.Condition#holds(int, int, org.sadun.text.ffp.FlatFileParser.LineReader)
	 */
	public boolean holds(
		int logicalLineCount,
		int physicalLineCount,
		LineReader reader)
		throws IOException {
		for(int i=0;i<conditions.length;i++) {
			((LinePushbackReader)reader).mark();
			boolean holds = conditions[i].holds(logicalLineCount, physicalLineCount, reader);
			((LinePushbackReader)reader).reset();
			if (!holds) return true;
		}
		return false;
	}
	
	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.print(conditions[0]);
		for(int i=1;i<conditions.length;i++) {
			pw.print(" or ");
			pw.print(conditions[i]);
		}
		return sw.toString();
	}

}
