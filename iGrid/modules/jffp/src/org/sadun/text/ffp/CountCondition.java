/*
 * Created on Jan 17, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.sadun.text.ffp.FlatFileParser.Condition;
import org.sadun.text.ffp.FlatFileParser.LineReader;

/**
 * A condition which compares the numer of physical/logical lines read so far with a given
 * number (pivot number), checking for a specific simple numeric relationship.
 * 
 * @author Cristiano Sadun
 */
public class CountCondition implements Condition {
	
	/**
	 * Constant to indicate that comparisons will occur with
	 * physical lines count.
	 */
	public static final int PHYSICAL_LINES = 0;
	
	/**
	 * Constant to indicate that comparisons will occur with
	 * logical lines count.
	 */
	public static final int LOGICAL_LINES = 1;
	
	/**
	 * Relation constant
	 */
	public static final int LOWER = 0;
	
	/**
	 * Relation constant
	 */
	public static final int LOWER_OR_EQUAL = 1;
	
	/**
	 * Relation constant
	 */	
	public static final int EQUAL = 2;
	
	/**
	 * Relation constant
	 */	
	public static final int GREATER_OR_EQUAL = 4;
	
	/**
	 * Relation constant
	 */	
	public static final int GREATER = 8;
	
	/**
	 * Relation constant
	 */	
	public static final int ODD = 16;
	
	/**
	 * Relation constant
	 */	
	public static final int EVEN = 32;
	
	private int n;
	private int lineType;
	private int relationType;

	/**
	 * Create a condition of the given type (using one of the type constants),
	 * referring to either logical or physical lines, and pivoted on the given
	 * number.
	 * 
	 * @param n the number of the condition
	 * @param lineType one of {@link #PHYSICAL_LINES} or {@link #LOGICAL_LINES}
	 * @param relationType one of {@link #LOWER}, {@link #LOWER_OR_EQUAL}, {@link #EQUAL}, 
	 *             {@link #GREATER}, {@link #GREATER_OR_EQUAL}, {@link #ODD} or {@link #EVEN}. 
	 */
	public CountCondition(int pivotNumber, int lineType, int relationType) {
		assert pivotNumber > 0;
		assert relationType == LOWER || relationType == LOWER_OR_EQUAL || relationType == EQUAL
		|| relationType == GREATER || relationType == GREATER_OR_EQUAL || relationType == ODD ||
		relationType == EVEN;
		assert lineType == PHYSICAL_LINES || lineType == LOGICAL_LINES;
		this.n=pivotNumber;
		this.lineType=lineType;
		this.relationType=relationType;
	}
	
	/**
	 * Create a condition of the given type (using one of the type constants),
	 * referring to the logical lines, and pivoted on the given number.
	 * 
	 * @param n the number of the condition
	 * @param relationType one of {@link #LOWER}, {@link #LOWER_OR_EQUAL}, {@link #EQUAL}, 
	 *             {@link #GREATER}, {@link #GREATER_OR_EQUAL}, {@link #ODD} or {@link #EVEN}. 
	 */
	public CountCondition(int pivotNumber, int relationType) {
		this(pivotNumber, LOGICAL_LINES, relationType);
	}

	/**
     * The condition holds if the physical/logical line count (as defined at construction)
     * has the specified {@link #getRelationType() relation type} with the {@link #getPivotNumber() pivot
     * number}.
     * <p>
     * For example, if the line type is {@link #LOGICAL_LINES}, the relation type is {@link #LOWER} and 
     * the pivot number is <tt>5</tt>, the condition will hold for the first 4 logical lines.
     * 
     * @param logicalLineCount the logical lines read so far
	 * @param physicalLineCount the physical lines read so far
	 * @param reader an object allowing to read lines on the file
	 * @return <b>true</b> if the conditions hold, <b>false</b> otherwise. 
	 */
	public boolean holds(
		int logicalLineCount,
		int physicalLineCount,
		LineReader reader) {
			int lc = (lineType==PHYSICAL_LINES) ? physicalLineCount : logicalLineCount;
			switch (relationType) {
				case LOWER: return lc < n;
				case LOWER_OR_EQUAL: return lc <= n;
				case EQUAL: return lc == n;
				case GREATER_OR_EQUAL: return lc >= n;
				case GREATER: return lc >n;
				case ODD: return lc % 2 == 1;
				case EVEN: return lc % 2 == 0;
				default:
					throw new RuntimeException("Invalid relation type code");
			}
	}

	/**
	 * Return the line type used by this condition (one of {@link #LOGICAL_LINES} or {@link #PHYSICAL_LINES}).
	 * @return the line type used by this condition (one of {@link #LOGICAL_LINES} or {@link #PHYSICAL_LINES}).
	 */
	public int getLineType() {
		return lineType;
	}

	/**
	 * Return the relation type used by this condition (one of {@link #LOWER}, {@link #LOWER_OR_EQUAL}, {@link #EQUAL}, 
	 *             {@link #GREATER}, {@link #GREATER_OR_EQUAL}, {@link #ODD} or {@link #EVEN}).
	 * @return the relation type used by this condition (one of {@link #LOWER}, {@link #LOWER_OR_EQUAL}, {@link #EQUAL}, 
	 *             {@link #GREATER}, {@link #GREATER_OR_EQUAL}, {@link #ODD} or {@link #EVEN}).
	 */
	public int getRelationType() {
		return relationType;
	}

	/**
	 * Return the number to use for comparisons (pivot number). 
	 * @return the number to use for comparisons (pivot number).
	 */
	public int getPivotNumber() {
		return n;
	}
	
	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.print(lineType==PHYSICAL_LINES ? "physical" : "logical");
		pw.print(" line count ");
		switch (relationType) {
			case LOWER: pw.print("is lower than"); break;
			case LOWER_OR_EQUAL: pw.print("is lower or equal to"); break;
			case EQUAL: pw.print("is equal to"); break;
			case GREATER_OR_EQUAL: pw.print("is greater or equal to"); break;
			case GREATER: pw.print("is greater than"); break;
			case ODD: pw.print("is odd"); break;
			case EVEN: pw.print("is even"); break;
			default:
				throw new RuntimeException("Invalid relation type code");
		}
		if (relationType != EVEN && relationType!=ODD)
			pw.print(" "+n);
		return sw.toString();
		
	}

}
