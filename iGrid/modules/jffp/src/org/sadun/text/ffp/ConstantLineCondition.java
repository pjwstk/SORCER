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
 * A condition which holds only if the next line on the file is
 * identical to the a given constant.
 * 
 * @author Cristiano Sadun
 */
public class ConstantLineCondition implements Condition {

	private String constant;

	/**
	 * Create a condition which use the given constant.
	 * @param constant the constant to use
	 */
	public ConstantLineCondition(String constant) {
		this.constant=constant;	
	}
	
	/**
	 * Return <b>true</b> if there is one physical line available in the file,
	 * and it is identical to the constant provided at construction.
	 * 
 	 * @param logicalLineCount the logical lines read so far
	 * @param physicalLineCount the physical lines read so far
	 * @param reader an object allowing to read lines on the file
	 * @return <b>true</b> if the conditions hold, <b>false</b> otherwise.
	 * @throws IOException if a I/O problem arises when reading lines
	 */
	public boolean holds(
		int logicalLineCount,
		int physicalLineCount,
		LineReader reader)
		throws IOException {
		String line=reader.readLine();
		if (line==null) return false;
		return line.equals(constant);
	}
	/**
	 * Return the constant used by this condition.
	 * @return the constant used by this condition.
	 */
	public String getConstant() {
		return constant;
	}
	
	public String toString() {
		return "the line matches exactly '"+constant+"'";
	}

}
