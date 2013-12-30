/*
 * Created on Jan 17, 2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp;

import java.io.IOException;
import java.util.Iterator;

import org.sadun.text.ffp.FlatFileParser.Condition;
import org.sadun.text.ffp.FlatFileParser.LineReader;

/**
 * A condition that looks for a matching constant into a specific physical
 * line.
 * 
 * @author Cristiano Sadun
 */
public class ConstantFoundInLineCondition implements Condition {

	private int physicalLine;
	private String constant;
	private int startPosition;

	/**
	 * Create a condition which will look for the given constant at the given
	 * position of the given physical line in the file.
	 * 
	 * @param physicalLine
	 *            the physical line in which the constant is to be looked for
	 * @param constant
	 *            the constant to look for
	 * @param startPosition
	 *            the position in the physical line where to look for the
	 *            constant
	 */
	public ConstantFoundInLineCondition(
		int physicalLine,
		String constant,
		int startPosition) {
		this.physicalLine = physicalLine;
		this.constant = constant;
		this.startPosition = startPosition;
	}

	/**
	 * Create a condition which will look for the given constant at the given
	 * position of the next line in the file.
	 * 
	 * @param constant
	 *            the constant to look for
	 * @param startPosition
	 *            the position in the line where to look for the constant
	 */
	public ConstantFoundInLineCondition(String constant, int startPosition) {
		this(1, constant, startPosition);
	}

	/**
	 * Create a condition which will look at the value of the nth field of the
	 * given physical line in the given {@link LineFormat}object.
	 * <p>
	 * The field must have type {@link Type#CONSTANT}.
	 * <p>
	 * This constructor is useful when a {@link LineFormat}has already been
	 * defined with the constant value to be looked for by this condition.
	 * 
	 * @param format
	 *            the {@link LineFormat}from which to get the constant value
	 * @param physicalLine
	 *            the physical line of the field (starting with 1)
	 * @param n
	 *            the number of the field (starting with 1)
	 */
	public ConstantFoundInLineCondition(
		int physicalLine,
		LineFormat format,
		int n) {
		this(
			1,
			format.getField(physicalLine, n).getImage(),
			format.getField(physicalLine, n).getStart());
	}

	/**
	 * Create a condition which will look at the value of the nth field in the
	 * given {@link LineFormat}object.
	 * <p>
	 * The field must have type {@link Type#CONSTANT}.
	 * <p>
	 * This constructor is useful when a {@link LineFormat}has already been
	 * defined with the constant value to be looked for by this condition.
	 * 
	 * @param format
	 *            the {@link LineFormat}from which to get the constant value
	 * @param n
	 *            the number of the field (starting with 1)
	 */
	public ConstantFoundInLineCondition(LineFormat format, int n) {
		this(1, format, n);
	}

	/**
	 * Create a condition which will look at the value of the first constant
	 * field in the given physical line of the given {@link LineFormat}object.
	 * <p>
	 * The format must have at least one field defined as type
	 * {@link Type#CONSTANT}.
	 * <p>
	 * This constructor is useful when a {@link LineFormat}has already been
	 * defined with the constant value to be looked for by this condition.
	 * 
	 * @param format
	 *            the {@link LineFormat}from which to get the constant value
	 * @param physicalLine
	 *            the physical line of the field (starting with 1)
	 * @param n
	 *            the number of the field (starting with 1)
	 * @exception IllegalArgumentException
	 *                if the given format does not have any
	 *                {@link Type#CONSTANT constant}field.
	 */
	public ConstantFoundInLineCondition(int physicalLine, LineFormat format) {
		this(physicalLine, format, findFirstConstantFieldNumber(format));
	}

	/**
	 * Create a condition which will look at the value of the first constant
	 * field of in the given {@link LineFormat}object.
	 * <p>
	 * The format must have at least one field defined as type
	 * {@link Type#CONSTANT}.
	 * <p>
	 * This constructor is useful when a {@link LineFormat}has already been
	 * defined with the constant value to be looked for by this condition.
	 * 
	 * @param format
	 *            the {@link LineFormat}from which to get the constant value
	 * @param n
	 *            the number of the field (starting with 1)
	 * @exception IllegalArgumentException
	 *                if the given format does not have any
	 *                {@link Type#CONSTANT constant}field.
	 */
	public ConstantFoundInLineCondition(LineFormat format) {
		this(1, format);
	}

	private static int findFirstConstantFieldNumber(LineFormat format) {
		int count = 1;
		for (Iterator i = format.iterator(); i.hasNext();) {
			LineFormat.FieldInfo info = (LineFormat.FieldInfo) i.next();
			if (info.getType() == Type.CONSTANT)
				return count;
			count++;
		}
		throw new IllegalArgumentException("No fields of Type.CONSTANT type in the given format");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sadun.text.ffp.FlatFileParser.Condition#holds(int, int,
	 *      org.sadun.text.ffp.FlatFileParser.LineReader)
	 */
	public boolean holds(
		int logicalLineCount,
		int physicalLineCount,
		LineReader reader)
		throws IOException {
		// Read as many physical lines as needed
		String line = readPhysicalLines(reader, physicalLine);
		if (line == null)
			return false;
		try {
			String c =
				LineFormat.extract(
					line,
					startPosition,
					startPosition + constant.length());
			return c.equals(constant);
		} catch (IllegalRangeException e) {
			return false;
		}

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
	protected String readPhysicalLines(LineReader reader, int n)
		throws IOException {
		String line = null;
		for (int i = 0; i < n; i++) {
			line = reader.readLine();
			if (line == null)
				return null;
		}
		return line;
	}

	public String toString() {
		return "when characters at ["
			+ startPosition
			+ "-"
			+ (startPosition + constant.length())
			+ "] in the physical line "
			+ physicalLine
			+ " are exactly '"
			+ constant
			+ "'";
	}

}
