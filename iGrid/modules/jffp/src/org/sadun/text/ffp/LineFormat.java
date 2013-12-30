/*
 * Created on Jan 16, 2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.sadun.util.IndentedPrintWriter;

/**
 * An ojbect of this class holds details of a specific logical line format and
 * can parse it from core memory.
 * A logical line can be composed by several consecutive physical lines,
 * separated by a {@link #getLineSeparator() line separator} character sequence.
 * <p>
 * A LineFormat is built by declaring the expected fields and used by invoking the
 * {@link #parse(String)} method.
 * Three ways are available to declare the expected fields:
 * <p>
 * <ul>
 * <li> One of the {@link #defineField(int, int, int, Type, String) defineField()} overloads.
 * <p>
 * The overload with most parameters allows to specify the physical line containing the field, 
 * a name for the field, a {@link org.sadun.text.ffp.Type type}, its zero-based start and end indexes (end excluded)
 * and a possible <b>image</b> - a denotation that must be matched by the field value once parsed.
 * <p>
 * <table border width=60% align=center>
 * <tr><td align=center><b>Type</b></td><td align=center><b>Image</b></td></tr>
 * <tr><td>{@link org.sadun.text.ffp.Type#CONSTANT}</td><td><font color=red>required</font>, the constant value</td></tr>
 * <tr><td>{@link org.sadun.text.ffp.Type#ALFA}</td><td><font color=navy>optional</font>, <b>null</b> or a regular expression which must be matched by the value</td></tr>
 * <tr><td>{@link org.sadun.text.ffp.Type#NUMERIC}</td><td><font color=navy>optional</font>, <b>null</b> or a <tt>NumberFormat</tt> string which will be used to parse the value</td></tr>
 * <tr><td>{@link org.sadun.text.ffp.Type#UNDEFINED}</td><td>ignored</td></tr>
 * </table>
 * <p>
 * For example,
 * <pre>
 *  format.defineField(0, 5, Type.NUMERIC);
 *  format.defineField(5, 7, Type.CONSTANT, "aa");
 *  format.defineField(7, 12, Type.NUMERIC);
 * </pre>
 * <p>
 * <li>One of the {@link #defineNextField(int, int, Type, String) defineNextField()} overloads.
 * <p>
 * {@link #defineNextField(int, int, Type, String)} behaves exactly as 
 * {@link #defineField(int, int, int, Type, String)}, but the start index is implicit:
 * the LineFormat keeps track a "current start" index which is updated with each definition.
 * <p>
 * For example,
 * <pre>
 *  format.defineNextField(5, Type.NUMERIC);
 *  format.defineNextField(7, Type.CONSTANT, "aa");
 *  format.defineNextField(12, Type.NUMERIC);
 * </pre>
 * <p>
 * <li> A line image, by using a {@link #declareLineImage(int, String) declareLineImage()} overload.
 * Line images use sequences of characters (separated by spaces) to denote types:
 * <p>
 * <table border width=60% align=center>
 * <tr><td align=center><b>Symbol</b></td><td align=center><b>Type</b></td></tr>
 * <tr><td>@</td><td>{@link org.sadun.text.ffp.Type#ALFA}</td></tr>
 * <tr><td>#</td><td>{@link org.sadun.text.ffp.Type#NUMERIC}</td></tr>
 * <tr><td>any character</td><td>{@link org.sadun.text.ffp.Type#CONSTANT}</td></tr>
 * <tr><td>/b</td><td>blank in {@link org.sadun.text.ffp.Type#CONSTANT}</td></tr>
 * <tr><td>//</td><td>forward slash in {@link org.sadun.text.ffp.Type#CONSTANT}</td></tr>
 * </table>
 * <p>
 * For example,
 * <pre>
 *  format.declareLineImage("##### aa #####");
 * </pre>
 * </ul>
 * <p>
 * For each method, overloads which accept a physical line number as parameter or not are provided:
 * if the parameter is absent, the physical line number is intended 1 (the common case of parsing
 * a single line).
 * <p>
 * A LineFormat can be {@link #createCopy() copied} in whichever moment, and the resulting copy
 * is independent from the original object. This allows to minimize the amount of declarations
 * necessary for creating LineFormats for similar lines.
 * <p>
 * When {@link #parse(String)} is invoked, the object attempts to parse the passed text
 * according to the format definition. If the parsing fails, a {@link org.sadun.text.ffp.FFPParseException}
 * subexception is thrown. If the line is parsed correctly but some characters are still existing before
 * the line separator:
 * <ul>
 * <li> if {@link #isFailOnTrailingChars()} is <b>true</b> a {@link org.sadun.text.ffp.TrailingCharactersException}
 * is thrown;
 * <li> else, the remaining characters are ignored, but a warning is logged on the 
 * {@link org.sadun.text.ffp.FlatFileParser#LOGGER_CHANNEL_NAME} channel.
 * </ul>
 * <p>
 * {@link #isFailOnTrailingChars()} is initialized on construction depending on the value
 * of the public static field {@link #defaultFailOnTrailingChars}, which is by default set to <b>true</b> (i.e.
 * indicating that an exception should be raised on trailing characters).
 * 
 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano
 *         Sadun</a>
 * @version 1.0
 */
public class LineFormat implements Cloneable {

	private interface ImageParser {
		public boolean hasNext();
		public FieldInfo nextField() throws ImageParseException;
        public String[] parseConstantSet(String constantSetImage) throws ImageParseException;;
		
	}

	class DefaultImageParser implements ImageParser {

	    private StringTokenizer st;
		private int currentStart = 0;
		private int currentField = 0;
		private int physicalLineNumber;

		public DefaultImageParser(int physicalLineNumber, String image) {
			this.physicalLineNumber=physicalLineNumber;
			this.st = new StringTokenizer(image, " ");
		}

		public boolean hasNext() {
			return st.hasMoreTokens();
		}

		/**
		 * Return the next field info.
		 * @return
		 */
		public FieldInfo nextField() throws ImageParseException {
			String fieldSpec = st.nextToken();
		
			Type type = null;
            boolean constantSet=false;
			StringBuffer image = new StringBuffer();
			// Special type must start with /
			if (fieldSpec.charAt(0) == '/') {
				if (fieldSpec.length() > 1 && fieldSpec.charAt(1) == '/') {
					// Do nothing, will be handled later
				} else {
					// Handle special type
					char c = fieldSpec.charAt(1);
					if (c == 'b' || c == 'B') {
						// Do nothing, will be handled later
					} else if (c=='[') {
                        // Do nothing, constant set separator
                    } else
						throw new ImageParseException(
							"Invalid type marker: " + fieldSpec);
				}
			}

			if (type == null) { // Type hasn't been established yet
				// Normal type
                
				for (int i = 0; i < fieldSpec.length(); i++) {
					char c = fieldSpec.charAt(i);
					// Allow for escape sequences in constant type
					if (c == '/' && fieldSpec.length() > i)
						if (type == null || type == Type.CONSTANT) {
							char c2 = fieldSpec.charAt(i + 1);
							// Is escape sequence /b or // ?
							if (c2 == 'b' || c2 == 'B')
								c2 = ' ';
							else if (c2 == '/') {
								// Do nothing, already ok	
							} if (c2 == '[') {
							    constantSet=true;
                                image=new StringBuffer(fieldSpec.substring(1));
                                type=Type.CONSTANTSET;
                                break;
                            } 
                            else {
								// else escape sequence char must match a type char
								Type escType = findType(c2);
								if (escType == Type.CONSTANT)
									throw new ImageParseException(
										"Unexpected '"
											+ c2
											+ "' after / in "
											+ fieldSpec);
							}
							image.append(c2);
							i++;
							continue;
						}
                    
                    
                    
					// Handle single-char image
					Type cType = findType(c);
					if (type != null && type != cType)
						throw new ImageParseException(
							"Invalid image: "
								+ fieldSpec
								+ ". Images mixes "
								+ type
								+ " and "
								+ cType
								+ " types (remember spaces must be used as field separators)");
					else
						type = cType;
					if (type == Type.CONSTANT)
						image.append(c);
				}
			}
			String img = image.toString();
			if ("".equals(img))
				img = null;
            
            int end;
            FieldInfo info;
            if (constantSet) {
                // Create constant set field
                String[] constants = parseConstantSet(img.substring(1));
                // Compute end as maxium length
                end = constants[0].length();
                boolean differentLenghts = false;
                for (int i = 1; i < constants.length; i++)
                    if (end < constants[i].length()) {
                        end = constants[i].length();
                        differentLenghts = true;
                    }
                int len=end;
                end += currentStart;
                
                if (differentLenghts) {
                    logger.warning("Constants of different lengths have been specified. The maximum length (" + len + ") will be used as field length and whitespace padding will be expected for the shorter constants. Use explicit blank markers (/b) in the image to avoid this message.");
                    // Pad if necessary    
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < constants.length; i++) {
                        sb.delete(0, sb.length());
                        for(int j=constants[i].length();j<len;j++) 
                            sb.append(' ');
                        constants[i]+=sb.toString();
                    }
                }
                info = new FieldInfo(mkNewFieldName(
                        FieldInfo.CONSTANT_SET_FIELD_DEFAULT_NAME, physicalLineNumber,
                        currentField),
                // FieldInfo.DEFAULT_NAME,
                        currentStart, end, type, constants);
            } else {

                end = currentStart
                        + ((type == Type.CONSTANT) ? img.length() : fieldSpec
                                .length());
                info = new FieldInfo(mkNewFieldName(
                        type == Type.CONSTANT ? FieldInfo.CONSTANT_FIELD_DEFAULT_NAME : FieldInfo.DEFAULT_NAME, physicalLineNumber,
                        currentField),
                // FieldInfo.DEFAULT_NAME,
                        currentStart, end, type, img);
            }
			
			currentStart = end;
			currentField++;
			return info;
		}

		private Type findType(char c) {
			switch (c) {
				case '@' :
					return Type.ALFA;
				case '#' :
					return Type.NUMERIC;
                case '[' :
                    return Type.CONSTANTSET;
				default :
					return Type.CONSTANT;
			}
		}
        
        public String[] parseConstantSet(String spec) throws ImageParseException {
            assert spec != null;
            StringTokenizer st = new StringTokenizer(spec,",");
            String cs [] = new String[st.countTokens()];
            int i=0;
            while(st.hasMoreTokens()) {
                String token = st.nextToken();
                StringBuffer sb = new StringBuffer();
                for(int j=0;j<token.length();j++)
                    if (token.charAt(j)=='/') {
                        if (j==token.length()-1) throw new ImageParseException("Unterminated / sequence in "+spec);
                        if (token.charAt(j+1)=='B' || token.charAt(j+1)=='b') {
                            sb.append(' ');
                            j++;
                        } else throw new ImageParseException("Unexpected character '"+token.charAt(j+1)+" after / in sequence in "+spec);
                    } else sb.append(token.charAt(j));
                cs[i++]=sb.toString();
            }
            return cs;
        }
	}

	/**
	 * A class describing a field.
	 *
	 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano Sadun</a>
	 * @version 1.0
	 */
    public class FieldInfo implements Cloneable {

		public static final String DEFAULT_NAME = "field";
        public static final String CONSTANT_FIELD_DEFAULT_NAME = "const";
        public static final String CONSTANT_SET_FIELD_DEFAULT_NAME = "constset";

		private String name;
		private int start;
		private int end;
		private Type type = Type.UNDEFINED;
		private String image;
        private Set constantsSet;

		private FieldInfo(String name, int start, int end, Type type, String image) {
			assert type != null;
            
			this.name = name;
			this.start = start;
			this.end = end;
			this.type = type;
			this.image = image;
			if ((type == Type.CONSTANT) && image == null)
				throw new IllegalArgumentException("Programming error: constant/constant set fields need an image specification");
		}
        
        private FieldInfo(String name, int start, int end, Type type, String [] images) {
            this(name, start,end,type, (String)null);
            constantsSet = new HashSet(Arrays.asList(images));
        }
        
		/**
		 * Surface copying, since there's no nonconstant reference members
		 * @return
		 */
		FieldInfo createCopy() {
			try {
				return (FieldInfo) clone();
			} catch (CloneNotSupportedException e) {
				throw new Error("This is not supposed to happen");
			}
		}

		boolean intersects(FieldInfo f2) {
			return (f2.start < start && f2.end > start)
				|| (f2.start >= start && f2.end <= end)
				|| (f2.start <= end && f2.end > end);

		}

		public boolean equals(Object obj) {
			if (obj instanceof FieldInfo) {
				return ((FieldInfo) obj).start == start
					&& ((FieldInfo) obj).end == end;
			}
			return false;
		}

		public String toString() {
			return name
				+ " (from position "
				+ start
				+ " to position"
				+ end
				+ ", length "
				+ (end - start)
				+ ", type "
				+ type 
                +(constantsSet != null ? ", possible values '"+listConstantSet()+"'" : 
				image == null ? "" : ", image '" + image + "'")
				+ ")";
		}

		private String listConstantSet() {
            StringBuffer sb = new StringBuffer();
            for(Iterator i = constantsSet.iterator();i.hasNext();) {
                sb.append(i.next());
                if (i.hasNext()) sb.append(", ");
            }
            return sb.toString();
        }

        /**
		 * @return Returns the end.
		 */
		public int getEnd() {
			return end;
		}

		/**
		 * @return Returns the start.
		 */
		public int getStart() {
			return start;
		}

		/**
		 * @return Returns the type.
		 */
		public Type getType() {
			return type;
		}

		/**
		 * @return Returns the image.
		 */
		public String getImage() {
			return image;
		}

		/**
		 * @return
		 */
		public String getName() {
			return name;
		}
        public Set getConstantSet() {
            return constantsSet;
        }

	}

	/**
	 * This member controls the default value of the {@link #isFailOnTrailingChars()
	 * failOnTrailingChars} property. It is set to <b>true</b> when the class is
	 * loaded. 
	 */
	public static boolean defaultFailOnTrailingChars = true;

	private String name;
	private String lineSeparator = System.getProperty("line.separator");
	// Map Integer -> SortedSet(FieldInfo) (physical line number -> fields)
	private Map fieldsByLine = new HashMap();
	private int currentStart = 0;
	private int currentPhysicalLine = 1;
	private boolean failOnTrailingChars;
	
	private Logger logger=Logger.getLogger("org.sadun.text.ffp");

	/**
	 * Create a LineFormat with a name, for reading a logical line.
	 * <p>
	 * A logical line may be composed by multiple physical lines separated by a
	 * {@link #getLineSeparator() line separator}.
	 */
	public LineFormat(String name) {
		this.name = name;
		this.failOnTrailingChars = defaultFailOnTrailingChars;
	}

	/**
	 * Create a LineFormat, for reading a logical line.
	 * <p>
	 * A logical line may be composed by multiple physical lines separated by a
	 * {@link #getLineSeparator() line separator}.
	 */
	public LineFormat() {
		this("default");
	}

	/**
	 * Return the name of this line format.
	 * @return the name of this line format.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the line separator. By default, the line separator is the value of the
	 * <tt>line.separator</tt> system property.
	 * @return Returns the lineSeparator.
	 */
	public String getLineSeparator() {
		return lineSeparator;
	}

	/**
	 * Set the line separator. By default, the line separator is the value of the
	 * <tt>line.separator</tt> system property.
	 * @param lineSeparator
	 *            The lineSeparator to set.
	 */
	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}

	/**
	 * Return the physical lines count at the moment of call.
	 * <p>
	 * Note that if {@link #defineField(int, int, int)}is invoked declaring a
	 * field into a greater physical line, the count returned by this method
	 * will adjust accordingly.
	 * 
	 * @return the physical lines count at the moment of call.
	 */
	public int getPhysicalLinesCount() {
		return currentPhysicalLine;
	}

	/**
	 * An utility method to extracts a portion of a physical line.
	 * 
	 * @param line
	 *            the character array containing the physical line
	 * @param start
	 *            the start index
	 * @param end
	 *            the end index
	 * @return the String comprised between start and end (excluded).
	 * @exception IllegalRangeException
	 *                if the given range is not valid
	 */
	public static String extract(CharSequence line, int start, int end)
		throws IllegalRangeException {
		StringBuffer sb = new StringBuffer();
		if (start < 0 || end > line.length())
			throw new IllegalRangeException(line, start, end);
		for (int i = start; i < end; i++)
			sb.append(line.charAt(i));
		return sb.toString();
	}
	
	/**
	 * Define fields in the given physical line using a positions array. 
	 * As many fields as elements in the array are defined. Each element must be a two-integer array
	 * containing start and end position of the field (excluded).
	 * @param positions as many fields as elements in the array are
	 *                  defined. Each element of the array must be a two-integer array
	 *                  containing start and end position of the field (excluded).
	 */
	public void defineFields(int physicalLine, int [][] positions) {
		for(int i=0;i<positions.length;i++) {
			if (positions[i].length!=2) 
				throw new IllegalArgumentException("Programming error: the #"+i+" element in the passed array has length different than 2.");
			defineField(physicalLine, positions[i][0], positions[i][1]);
		}
	}
	
	/**
	 * Define fields using a positions array. As many fields as elements in the array are
	 * defined. Each element must be a two-integer array
	 * containing start and end position of the field (excluded).
	 * @param positions as many fields as elements in the array are
	 *                  defined. Each element of the array must be a two-integer array
	 *                  containing start and end position of the field (excluded).
	 */
	public void defineFields(int [][] positions) {
		defineFields(1, positions);
	}

	/**
	 * Define a field as being in the given physical line and at the given
	 * start-end position. The end position is not included, so a positional
	 * field at [0, 10] inclusive will have start=0 and end=11;
	 * <p>
	 * Physical lines indexes start at 1. Start and end indexes start at 0.
	 * <p>
	 * Note: if the given physical line is greater than the current physical
	 * line, the latter is increased; the current start position is advanced to
	 * the given end position (for usage with
	 * {@link #defineNextField(int, int)}).
	 * 
	 * @param physicalLine
	 *            the physical line index (starting from 1)
	 * @param start
	 *            the start index in the line
	 * @param end
	 *            the end index in the line
	 */
	public void defineField(int physicalLine, int start, int end) {
		defineField(physicalLine, start, end, Type.UNDEFINED);
	}

	/**
	 * Define a field as being in the given physical line and at the given
	 * start-end position. The end position is not included, so a positional
	 * field at [0, 10] inclusive will have start=0 and end=11;
	 * <p>
	 * Physical lines indexes start at 1. Start and end indexes start at 0.
	 * <p>
	 * Note: if the given physical line is greater than the current physical
	 * line, the latter is increased; the current start position is advanced to
	 * the given end position (for usage with
	 * {@link #defineNextField(int, int)}).
	 * 
	 * @param physicalLine
	 *            the physical line index (starting from 1)
	 * @param start
	 *            the start index in the line
	 * @param end
	 *            the end index in the line
	 * @param type
	 *            the expected type of the parameter
	 */
	public void defineField(int physicalLine, int start, int end, Type type) {
		defineField(physicalLine, mkNewFieldName(FieldInfo.DEFAULT_NAME), start, end, type);
	}
	
	private String mkNewFieldName(String base) {
		return mkNewFieldName(base, -1);
	}
	
	private String mkNewFieldName(String base, int currentField) {
		return mkNewFieldName(base, -1, currentField);
	}
	
	private String mkNewFieldName(String base, int physicalLine, int currentField) {
		int line=this.currentPhysicalLine;
		if (physicalLine!=-1) line=physicalLine;
		SortedSet s = ((SortedSet)this.fieldsByLine.get(new Integer(currentPhysicalLine+1)));
		int fieldCount=0;
		if (currentField>0) fieldCount=currentField;
		else {
			if (s!=null) fieldCount=s.size();
		}
		return base+"_"+line+"_"+(fieldCount+1);
	}

	/**
	 * Define a field as being in the given physical line and at the given
	 * start-end position. The end position is not included, so a positional
	 * field at [0, 10] inclusive will have start=0 and end=11;
	 * <p>
	 * Physical lines indexes start at 1. Start and end indexes start at 0.
	 * <p>
	 * Note: if the given physical line is greater than the current physical
	 * line, the latter is increased; the current start position is advanced to
	 * the given end position (for usage with
	 * {@link #defineNextField(int, int)}).
	 * 
	 * @param physicalLine
	 *            the physical line index (starting from 1)
	 * @param start
	 *            the start index in the line
	 * @param end
	 *            the end index in the line
	 * @param type
	 *            the expected type of the parameter
	 * @param image the image of the element 
	 */
	public void defineField(
		int physicalLine,
		int start,
		int end,
		Type type,
		String image) {
		defineField(
			physicalLine,
			mkNewFieldName(FieldInfo.DEFAULT_NAME),
			start,
			end,
			type,
			image);
	}

	/**
	 * Define a field as being in the given physical line and at the given
	 * start-end position. The end position is not included, so a positional
	 * field at [0, 10] inclusive will have start=0 and end=11;
	 * <p>
	 * Physical lines indexes start at 1. Start and end indexes start at 0.
	 * <p>
	 * Note: if the given physical line is greater than the current physical
	 * line, the latter is increased; the current start position is advanced to
	 * the given end position (for usage with
	 * {@link #defineNextField(int, int)}).
	 * 
	 * @param physicalLine
	 *            the physical line index (starting from 1)
	 * @param name the name of the field
	 * @param start
	 *            the start index in the line
	 * @param end
	 *            the end index in the line
	 */
	public void defineField(
		int physicalLine,
		String name,
		int start,
		int end) {
		defineField(physicalLine, name, start, end, Type.UNDEFINED);
	}

	/**
	 * Define a field as being in the given physical line and at the given
	 * start-end position. The end position is not included, so a positional
	 * field at [0, 10] inclusive will have start=0 and end=11;
	 * <p>
	 * Physical lines indexes start at 1. Start and end indexes start at 0.
	 * <p>
	 * Note: if the given physical line is greater than the current physical
	 * line, the latter is increased; the current start position is advanced to
	 * the given end position (for usage with
	 * {@link #defineNextField(int, int)}).
	 * 
	 * @param physicalLine
	 *            the physical line index (starting from 1)
	 * @param name the name of the field
	 * @param start
	 *            the start index in the line
	 * @param end
	 *            the end index in the line
	 * @param type
	 *            the expected type of the parameter
	 */
	public void defineField(
		int physicalLine,
		String name,
		int start,
		int end,
		Type type) {
		defineField(physicalLine, name, start, end, type, null);
	}

	/**
	 * Define a field as being in the given physical line and at the given
	 * start-end position. The end position is not included, so a positional
	 * field at [0, 10] inclusive will have start=0 and end=11;
	 * <p>
	 * Physical lines indexes start at 1. Start and end indexes start at 0.
	 * <p>
	 * Note: if the given physical line is greater than the current physical
	 * line, the latter is increased; the current start position is advanced to
	 * the given end position (for usage with
	 * {@link #defineNextField(int, int)}).
	 * <p>
	 * This is the method allowing the most complete field definition.
	 * 
	 * @param physicalLine the physical line index (starting from 1)
	 * @param name the name of the field
	 * @param start the start index in the line
	 * @param end the end index in the line
	 * @param type the expected type of the parameter
	 * @param image the image of the element 
	 */
	public void defineField(
		int physicalLine,
		String name,
		int start,
		int end,
		Type type,
		String image) {
		if (image!=null) validateImage(start, end, type, image);
		addFieldInfo(
			physicalLine,
			new FieldInfo(name, start, end, type, image));
		currentStart = end;
	}

	/**
	 * @param type
	 * @param image
	 */
	private void validateImage(int start, int end, Type type, String image) {
		if (type==Type.CONSTANT) {
			if (end-start != image.length()) 
				throw new IllegalArgumentException(
						"Field length as computed from end ("
						+end
						+") - start("
						+start
						+") = "
						+(end-start)
						+" does not match constant '"+image+"' length ("
						+image.length()+")");
		}
			
		
	}

	private void addFieldInfo(int physicalLine, FieldInfo info) {
		if (physicalLine > currentPhysicalLine)
			currentPhysicalLine = physicalLine;
		final Integer pl = new Integer(physicalLine);
		SortedSet l = (SortedSet) fieldsByLine.get(pl);
		if (l == null) {
			l = new TreeSet(new Comparator() {
				public int compare(Object o1, Object o2) {
					FieldInfo f1 = (FieldInfo) o1;
					FieldInfo f2 = (FieldInfo) o2;
					// fields must not intersect
					if (f1.intersects(f2))
						throw new FieldDefinitionException(
							"Programming error: the fields "
								+ f1
								+ " and "
								+ f2
								+ " intersect");
					return f1.start - f2.start;

				}
			});
			fieldsByLine.put(pl, l);
		}
		l.add(info);

	}

	/**
	 * Define a field as being at the given start-end position. The end
	 * position is not included, so a positional field at [0, 10] inclusive
	 * will have start=0 and end=11;
	 * <p>
	 * Note: the current start position is advanced to the given end position
	 * (for usage with {@link #defineNextField(int, int)}).
	 * 
	 * @param start the start index in the line
	 * @param end the end index in the line
	 */
	public void defineField(int start, int end) {
		defineField(1, start, end);
	}

	/**
	 * Define a field as being at the given start-end position and having the given type. The end
	 * position is not included, so a positional field at [0, 10] inclusive
	 * will have start=0 and end=11;
	 * <p>
	 * Note: the current start position is advanced to the given end position
	 * (for usage with {@link #defineNextField(int, int)}).
	 * 
	 * @param start the start index in the line
	 * @param end the end index in the line
	 * @param type the expected type of the parameter
	 */
	public void defineField(int start, int end, Type type) {
		defineField(1, start, end, type);
	}

	/**
	 * Define a field as being at the given start-end position and having the given name. The end
	 * position is not included, so a positional field at [0, 10] inclusive
	 * will have start=0 and end=11;
	 * <p>
	 * Note: the current start position is advanced to the given end position
	 * (for usage with {@link #defineNextField(int, int)}).
	 *
	 * @param name the name of the field
	 * @param start the start index in the line
	 * @param end the end index in the line
	 */
	public void defineField(String name, int start, int end) {
		defineField(1, name, start, end);
	}

	/**
	 * Define a field as being at the given start-end position and having the given name and type. The end
	 * position is not included, so a positional field at [0, 10] inclusive
	 * will have start=0 and end=11;
	 * <p>
	 * Note: the current start position is advanced to the given end position
	 * (for usage with {@link #defineNextField(int, int)}).
	 *
	 * @param name the name of the field
	 * @param start the start index in the line
	 * @param end the end index in the line
	 * @param type the expected type of the parameter
	 */
	public void defineField(String name, int start, int end, Type type) {
		defineField(1, name, start, end, type);
	}
	
	/**
	 * Define consecutive fields in the given physical line using a positions array. 
	 * As many fields as elements in the array are defined. Each element indicates the end
	 * position of the field (excluded).
	 * 
	 * @param positions as many fields as elements in the array are
	 *                  defined. Each element indicates the end
	 *                   position of the field (excluded).
	 */
	public void defineNextFields(int physicalLine, int [] positions) {
		for(int i=0;i<positions.length;i++) {
			defineNextField(physicalLine, positions[i]);
		}
	}	
	
	/**
	 * Define consecutive fields using a positions array. 
	 * As many fields as elements in the array are defined. Each element indicates the end
	 * position of the field (excluded).
	 * 
	 * @param positions as many fields as elements in the array are
	 *                  defined. Each element indicates the end
	 *                   position of the field (excluded).
	 */
	public void defineNextFields(int [] positions) {
		defineNextFields(1, positions);
	}	
	

	/**
	 * Define a field as being in the given physical line, from the current
	 * start position to the given end position (excluded).
	 * 
	 * @param physicalLine the physical line index (starting from 1)
	 * @param end the end position (excluded).
	 */
	public void defineNextField(int physicalLine, int end) {
		defineNextField(physicalLine, end, Type.UNDEFINED);
	}

	/**
	 * Define a field as being in the given physical line, having the given type and being 
	 * located from the current start position to the given end position (excluded).
	 * 
	 * @param physicalLine the physical line index (starting from 1)
	 * @param end the end position (excluded).
	 * @param type the expected type of the parameter
	 */
	public void defineNextField(int physicalLine, int end, Type type) {
		defineNextField(physicalLine, mkNewFieldName(FieldInfo.DEFAULT_NAME), end, type);
	}

	/**
	 * Define a field as being in the given physical line, having the given type and image and being 
	 * located from the current start position to the given end position (excluded).
	 * 
	 * @param physicalLine the physical line index (starting from 1)
	 * @param end the end position (excluded).
	 * @param type the expected type of the parameter
	 * @param image the image of the parameter
	 */
	public void defineNextField(
		int physicalLine,
		int end,
		Type type,
		String image) {
		defineNextField(
			physicalLine,
			mkNewFieldName(FieldInfo.DEFAULT_NAME),
			end,
			type,
			image);
	}

	/**
	 * Define a field as being in the given physical line, having the given name and being 
	 * located from the current start position to the given end position (excluded).
	 * 
	 * @param name the name of the field
	 * @param physicalLine the physical line index (starting from 1)
	 * @param end the end position (excluded).
	 */
	public void defineNextField(int physicalLine, String name, int end) {
		defineNextField(physicalLine, name, end, Type.UNDEFINED);

	}

	/**
	 * Define a field as being in the given physical line, having the given name and type and being 
	 * located from the current start position to the given end position (excluded).
	 * 
	 * @param name the name of the field
	 * @param physicalLine the physical line index (starting from 1)
	 * @param end the end position (excluded).
	 * @param type the expected type of the parameter
	 */
	public void defineNextField(
		int physicalLine,
		String name,
		int end,
		Type type) {
		defineField(physicalLine, name, currentStart, end, type);
	}

	/**
	 * Define a field as being in the given physical line, having the given name, type and image and being 
	 * located from the current start position to the given end position (excluded).
	 * 
	 * @param name the name of the field
	 * @param physicalLine the physical line index (starting from 1)
	 * @param end the end position (excluded).
	 * @param type the expected type of the parameter
	 * @param image the image of the parameter
	 */
	public void defineNextField(
		int physicalLine,
		String name,
		int end,
		Type type,
		String image) {
		if (physicalLine < currentPhysicalLine)
			throw new IllegalArgumentException("FFP programming error: setFieldInfo() invoked with physical line lower than current physical line");
		if (physicalLine > currentPhysicalLine)
			currentStart = 0;
		defineField(physicalLine, name, currentStart, end, type, image);
	}

	/**
	 * Define a field as being located from the current
	 * start position to the given end position (excluded).
	 * 
	 * @param end the end position (excluded).
	 */
	public void defineNextField(int end) {
		defineNextField(1, end);
	}

	/**
	 * Define a field as having the given name and being 
	 * located from the current start position to the given end position (excluded).
	 * 
	 * @param name the name of the field
	 * @param end the end position (excluded).
	 */
	public void defineNextField(String name, int end) {
		defineNextField(1, name, end);
	}
	
	/**
	 * Define a field as having the given name and being 
	 * located from the current start position to the given end position (excluded),
	 * and given type
	 * 
	 * @param name the name of the field
	 * @param end the end position (excluded)
	 * @param type the expected type of the parameter
	 */
	public void defineNextField(String name, int end, Type type) {
		defineNextField(1, name, end, type);
	}
	
	/**
	 * Define a field as having the given name and being 
	 * located from the current start position to the given end position (excluded),
	 * and given type and image
	 * 
	 * @param name the name of the field
	 * @param end the end position (excluded).
	 * @param type the expected type of the parameter
	 * @param image the image of the parameter
	 */
	public void defineNextField(String name, int end, Type type, String image) {
		defineNextField(1, name, end, type, image);
	}
	
	/**
	 * Define a field as having the given type and being 
	 * located from the current start position to the given end position (excluded).
	 * @param end the end position (excluded).* 
	 * @param type the expected type of the parameter
	 */
	public void defineNextField(int end, Type type) {
		defineNextField(1, end, type);
	}
	
	/**
	 * Define a field as having the given type and image and being 
	 * located from the current start position to the given end position (excluded).
	 * 
	 * @param end the end position (excluded).* 
	 * @param type the expected type of the parameter
	 * @param image the image of the parameter
	 */
	public void defineNextField(int end, Type type, String image) {
		defineNextField(1, end, type, image);
	}

	/**
	 * Create an independent copy of the format.
	 * <p>
	 * The copy so obtained can be modifed independetly from the original, for example to
	 * parse two slightly different formats.
	 * 
	 * @return an independent copy of the format.
	 */
	public LineFormat createCopy() {
		LineFormat format;
		try {
			format = (LineFormat) clone();
		} catch (CloneNotSupportedException e) {
			throw new Error("This is not supposed to happen");
		}
		format.fieldsByLine = new HashMap();
		for (Iterator i = fieldsByLine.keySet().iterator(); i.hasNext();) {
			Object key = i.next();
			FieldInfo info = (FieldInfo) fieldsByLine.get(key);
			format.fieldsByLine.put(key, info.createCopy());
		}
		return format;
	}

	/**
	 * Parse the given line basing on the format. If {@link #isFailOnTrailingChars()} is <b>true</b>,
	 * the match must be total. Else, trailing characters are silently ignored.
	 * 
	 * @param s a string containing the line's physical lines. The last line
	 *            may or may not have a
	 *            {@link #getLineSeparator() line separator}.
	 * @return an array containing the string representation of all the fields
	 *         in the line.
	 * @exception FFPParseException if a problem occurs when parsing
	 */
	public String[] parse(String s) throws FFPParseException {
		return parse(s, false);	
	}
	
	/**
	 * Parse the given line basing on the format. If {@link #isFailOnTrailingChars()} is <b>true</b>,
	 * the match must be total. Else, trailing characters are silently ignored.
	 * <p>
	 * Basing on the value of the <tt>autoTrim</tt> parameter, values are automatically
	 * trimmed before being returned.
	 * 
	 * @param s a string containing the line's physical lines. The last line
	 *            may or may not have a
	 *            {@link #getLineSeparator() line separator}.
	 * @param autoTrim if <b>true</b> strings are trimmed before being returned
	 * @return an array containing the string representation of all the fields
	 *         in the line.
	 * @exception FFPParseException if a problem occurs when parsing
	 */
	public String[] parse(String s, boolean autoTrim) throws FFPParseException {
		// Locate the line separators and split into physical lines without
		// separators
		List l = new ArrayList();
		int i, currIndex = 0;
		do {
			i = s.indexOf(lineSeparator, currIndex);
			// i==-1 at the last line
			CharSequence line =
				s.subSequence(currIndex, (i == -1) ? s.length() : i);
			if (line.length() > 0) {
				l.add(line);
				currIndex = i + lineSeparator.length();
			}
		} while (i != -1);

		if (l.size() != getPhysicalLinesCount())
			throw new InvalidPhysicalLineCountException(
				s,
				l.size(),
				getPhysicalLinesCount());

		List result = new ArrayList();

		for (i = 0; i < l.size(); i++) {
			CharSequence line = (CharSequence) l.get(i);
			// Get the values for ith line
			SortedSet fields = (SortedSet) fieldsByLine.get(new Integer(i + 1));
			for (Iterator j = fields.iterator(); j.hasNext();) {
				FieldInfo info = (FieldInfo) j.next();
				if (!j.hasNext()) { // last element
					if (line.length() > info.end) {
						TrailingCharactersException exc = new TrailingCharactersException(
						line,
						this,
						line.subSequence(info.end, line.length())); 
						if (failOnTrailingChars) throw exc;
						else logger.warning(exc.getMessage());
					}
				}
				try {
					String value = extract(line, info.start, info.end);
					validateValue(line, info, value);
					if (autoTrim) result.add(value.trim());
					else result.add(value);
				} catch (IllegalRangeException e) {
					throw new FormatOutOfRangeException(line, this, info);
				}
			}
		}

		String[] resultArray = new String[result.size()];
		result.toArray(resultArray);
		return resultArray;
	}
	
	/**
	 * An utility method, which parses a line, and returns its XML fragment representation.
	 * 
	 * @param s a string containing the line's physical lines. The last line
	 *            may or may not have a
	 *            {@link #getLineSeparator() line separator}.
	 * @return an array containing the string representation of all the fields
	 *         in the line.
	 * @exception FFPParseException if a problem occurs when parsing
	 */
	public String parseToXml(String s) throws FFPParseException {
		return parseToXml(s, false);
	}
	
	/**
	 * An utility method, which parses a line, and returns its XML fragment representation.
	 * 
	 * @param s a string containing the line's physical lines. The last line
	 *            may or may not have a
	 *            {@link #getLineSeparator() line separator}.
	 * @param autoTrim if <b>true</b> strings are trimmed before being returned
	 * @return an array containing the string representation of all the fields
	 *         in the line.
	 * @exception FFPParseException if a problem occurs when parsing
	 */
	public String parseToXml(String s, boolean autoTrim) throws FFPParseException {
		return parseToXml0(-1, s, autoTrim);
	}
	
	// If lineCount==-1, ignore it, else add it as an attribute to <line>
	String parseToXml0(int lineCount, String s, boolean autoTrim) throws FFPParseException {
		String [] values=parse(s, autoTrim);
		return formatValuesAsXML(lineCount, values);
	}
	
	/**
	 * An utility method to format a set of values to an XML format, according to
	 * this {@link LineFormat}.
	 * 
	 * @param the line number to add as a parameter to the &lt;line&gt; tag.
	 * @param values the values to format to XML
	 * @return an XML fragment containing the values 
	 */	
	public String formatValuesAsXML(int lineCount, String [] values) {
		StringWriter sw=new StringWriter();
		IndentedPrintWriter pw = new IndentedPrintWriter(sw);
		pw.print("<line");
		if(lineCount!=-1) pw.print(" number=\""+lineCount+"\"");
		pw.println(">");
		pw.incIndentation(2);
		printFieldValuesXML(pw, values);
		pw.decIndentation(2);
		pw.println("</line>");
		return sw.toString();
	}
	
	/**
	 * An utility method to format a set of values to an XML format, according to
	 * this {@link LineFormat}.
	 * 
	 * @param values the values to format to XML
	 * @return an XML fragment containing the values 
	 */
	public String formatValuesAsXML(String [] values) {
		return formatValuesAsXML(-1, values);
	}
	
	void printFieldValuesXML(PrintWriter pw, String[] values) {
		int count=0;
		for(int i=0;i<getPhysicalLinesCount();i++) {
			SortedSet fields = (SortedSet) fieldsByLine.get(new Integer(i + 1));
			int fc=1;
			for (Iterator j = fields.iterator(); j.hasNext();) {
				FieldInfo info = (FieldInfo) j.next();
				pw.print("<field name=\""+info.getName()+"\" line=\""+(i+1)+"\" number=\""+fc+"\">");
				pw.print(values[count++]);
				pw.println("</field>");
				fc++;
			}
		}
	}
	
	
		
	

	// Typecheck on the given value denotation
	private void validateValue(CharSequence line, FieldInfo info, String value)
		throws ValidationException {
		if (info.getType() == Type.CONSTANT) {
			if (!value.equals(info.getImage()))
				throw new ValidationException(line, info, value);
		} else if (info.getType() == Type.CONSTANTSET) {
            if (!info.getConstantSet().contains(value))
                throw new ValidationException(line, info, value);
        } else if (info.getType() == Type.NUMERIC) {
			if (info.getImage()!=null) {
				NumberFormat nFormat = new DecimalFormat(info.getImage());
				try {
					nFormat.parse(value);
				} catch (ParseException e1) {
					throw new ValidationException(line, info,value);
				} 
			} else
			try {
				Double.parseDouble(value);
			} catch (NumberFormatException e) {
				throw new ValidationException(line, info,value);
			}
		}
	}

	/**
	 * Return a human-readable description of the format.
	 * @return a human-readable description of the format.
	 */
	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println("line format '" + name + "':");
		for (int i = 1; i <= getPhysicalLinesCount(); i++) {
			SortedSet fields = (SortedSet) fieldsByLine.get(new Integer(i));
			for (Iterator j = fields.iterator(); j.hasNext();)
				pw.println(j.next());
		}
		return sw.toString();
	}

	/**
	 * This method allows to declare quickly a line format by providing an image.
	 * <p>
	 * The image symbols indicate the type of the field; fields are separated by
	 * a space.
	 * <p>
	 * <ul>
	 * <li><tt>@</tt> indicates an alphanumeric character
	 * <li><tt>#</tt> indicates a number
	 * <li><tt>alphanumeric</tt> indicates a constant
	 * </ul>
	 *
	 * @param image
	 */
	public void declareLineImage(String image) throws ImageParseException {
		declareLineImage(1, image);
	}
	/**
	 * This method allows to declare quickly a line format by providing an image.
	 * <p>
	 * The image symbols indicate the type of the field; fields are separated by
	 * a space.
	 * <p>
	 * <ul>
	 * <li><tt>@</tt> indicates an alphanumeric character
	 * <li><tt>#</tt> indicates a number
	 * <li><tt>alphanumeric</tt> indicates a constant
	 * </ul> 
	 * 
	 * @param physicalLine
	 * @param image
	 */
	public void declareLineImage(int physicalLine, String image)
		throws ImageParseException {
		ImageParser ip = createImageParser(physicalLine, image);
		while (ip.hasNext()) {
			addFieldInfo(physicalLine, ip.nextField());
		}
	}

	/**
	 * In future, allows to define the ImageParser to use externally.
	 */
	protected ImageParser createImageParser(int physicalLine, String image) {
		return new DefaultImageParser(physicalLine, image);
	}

	/**
	 * Allows to create a LineFormat object directly from an image.
	 * (see {@link #declareLineImage(String)}).
	 * 
	 * @param image the format image
	 * @return a LineFormat object to parse that image.
	 * @throws ImageParseException if the image syntax is incorrect.
	 */
	public static LineFormat fromImage(String image)
		throws ImageParseException {
		LineFormat format = new LineFormat();
		format.declareLineImage(image);
		return format;
	}

	/**
	 * Allows to create a named LineFormat object directly from an image.
	 * (see {@link #declareLineImage(String)}).
	 * 
	 * @param the line format name
	 * @param image the format image
	 * @return a LineFormat object to parse that image.
	 * @throws ImageParseException if the image syntax is incorrect.
	 */
	public static LineFormat fromImage(String name, String image)
		throws ImageParseException {
		LineFormat format = new LineFormat(name);
		format.declareLineImage(image);
		return format;
	}

	/**
	 * Return whether or not parsing must fails if a line is parsed correctly but
	 * not all its characters are consumed.
	 * @return <b>true</b> if a line is parsed correctly but
	 *         not all its characters are consumed, <b>false</b> otherwise.
	 */
	public boolean isFailOnTrailingChars() {
		return failOnTrailingChars;
	}

	/**
	 * Set whether or not parsing must fails if a line is parsed correctly but
	 * not all its characters are consumed.
	 * <p>
	 * The initial value is controlled by the {@link #defaultFailOnTrailingChars} 
	 * static member.
	 * 
	 * @param failOnTrailingChars <b>true</b> if a line which parsed correctly, but
	 *         whose characters are not totally consumed must make the parser fail.
	 */
	public void setFailOnTrailingChars(boolean failOnTrailingChars) {
		this.failOnTrailingChars = failOnTrailingChars;
	}

	
	private FieldInfo cachedGetField=null;
	private int [] cachedGetFieldData= new int [2];
	FieldInfo getField(int physicalLine, int n) {
		if (cachedGetField!=null && cachedGetFieldData[0]==physicalLine && cachedGetFieldData[0]==n) return cachedGetField;
		SortedSet set =getFieldsSetForPhysicalLine(physicalLine);
		int count=0;
		FieldInfo info=null;
		for(Iterator i=set.iterator();i.hasNext() && count++ < n;info=(FieldInfo)i.next());
		if (info==null || count!=n+1) throw new IllegalArgumentException("This format has only "+count+" fields in line "+physicalLine+", field number "+n+" is out of range.");
		cachedGetFieldData[0]=physicalLine;
		cachedGetFieldData[1]=n;
		cachedGetField=info;
		return info;
	}
	
	private SortedSet getFieldsSetForPhysicalLine(int physicalLine) {
		SortedSet set = (SortedSet)fieldsByLine.get(new Integer(physicalLine));
		if (set==null) 
			if (physicalLine==0) throw new IllegalArgumentException("Physical line indexes start from 1, not 0");
			else throw new IllegalArgumentException("This format has only "+getPhysicalLinesCount()+" physical lines, no line "+physicalLine+" present");
		return set;
	}
	
	/**
	 * Return the number of fields in the logical line.
	 * @return the number of fields in the logical line.
	 */
	public int getFieldsCount() {
		int c=0;
		for(int i=0;i<getPhysicalLinesCount();i++) {
			c+=getFieldsCount(i);
		}
		return c;
	}

	/**
	 * Return the number of fields in the physical line.
	 * @return the number of fields in the physical line.
	 */
	public int getFieldsCount(int physicalLine) {
		SortedSet set = getFieldsSetForPhysicalLine(physicalLine);
		return set.size();
	}	
	
	/**
	 * Return an iterator on this format's {@link LineFormat.FieldInfo} set.
	 * @return an iterator on this format's {@link LineFormat.FieldInfo} set.
	 */
	public Iterator iterator() {
		return new FieldsIterator();
	}
	
	private class FieldsIterator implements Iterator {
		
		int currentPhysicalLine;
		int currentFieldNumber;
		Iterator currentIterator;
		
		private FieldsIterator() {
			setAtLineStart(1);
		}
		
		private void setAtLineStart(int n) {
			SortedSet set = (SortedSet)fieldsByLine.get(new Integer(n));
			if (set==null) currentIterator=null;
			else {
				currentIterator=set.iterator();
				currentFieldNumber=1;
				currentPhysicalLine=n;
			}
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
		  	return currentIterator != null && currentIterator.hasNext();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public Object next() {
			if (currentIterator == null) throw new NoSuchElementException();
			Object next=currentIterator.next();
			if (! currentIterator.hasNext()) // go to next line
				setAtLineStart(++currentPhysicalLine);
			return next;
		}
	}
	
	/**
	 * Return a string constant translated to the image syntax recognized by this class.
	 * @param constant the constant to translate
	 * @return the translated constant
	 */
	public static String makeConstantImage(String s) {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<s.length();i++) {
			char c=s.charAt(i);
			switch(c) {
				case '/': sb.append("//"); break;
				case ' ': sb.append("/b"); break;
				case '#': sb.append("/#"); break;
				case '@': sb.append("/@"); break;
				default:
					sb.append(c);
			}
		}
		return sb.toString();
	}

	/*
	 * TEST METHOD
	 * 
	public static void main(String args[]) throws Exception {
		LineFormat format = new LineFormat();
		
		//format.defineNextField(5, Type.NUMERIC);
		//format.defineNextField(7, Type.CONSTANT, "aa");
		//format.defineNextField(12, Type.NUMERIC);
		//format.defineNextField(2, 5);
		
		format.declareLineImage(1, "##### aa #####");
		format.declareLineImage(2, "#####");
		format.setLineSeparator("\r\n");
	
		System.out.println(format);
	
		ObjectLister.getInstance().println(
			format.parse("12345aa67890\r\n5432"));
	}*/

}
