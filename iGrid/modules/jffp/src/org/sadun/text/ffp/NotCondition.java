/*
 * Created on Jan 17, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp;

import java.io.IOException;

import org.sadun.text.ffp.FlatFileParser.Condition;
import org.sadun.text.ffp.FlatFileParser.LineReader;

/**
 * A {@link org.sadun.text.ffp.FlatFileParser.Condition} that holds if and only if the
 * condition passed at construction does <i>not</i> hold.
 * 
 * @author Cristiano Sadun
 */
public class NotCondition implements Condition {
	
	private Condition condition;
	
	/**
	 * Create a condition that negates the given one.
	 * @param condition the condition to negate.
	 */
	public NotCondition(Condition condition) {
		this.condition=condition;
	}

	/** 
	 * The condition given at construction is evaluated: if it fails, this condition holds; otherwise not.
	 * 
	 * @see org.sadun.text.ffp.FlatFileParser.Condition#holds(int, int, org.sadun.text.ffp.FlatFileParser.LineReader)
	 */
	public boolean holds(
		int logicalLineCount,
		int physicalLineCount,
		LineReader reader)
		throws IOException {
		return ! condition.holds(logicalLineCount,physicalLineCount, reader);
	}
}
