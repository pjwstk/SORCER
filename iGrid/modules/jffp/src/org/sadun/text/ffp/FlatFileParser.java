/*
 * Created on Jan 16, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A parser for flat files.
 * <p>
 * This parser employs {@link org.sadun.text.ffp.LineFormat} objects to specify the format of the flat file.
 * The {@link #FlatFileParser(LineFormat)} constructor is useful when the flat file is simple, each logical line
 * having identical format. Together with {@link org.sadun.text.ffp.LineFormat#fromImage(String)} allows
 * to declare the parser and the format in one line of code:
 * <p>   
 * <pre>
 *  FlatFileParser ffp = new FlatFileParser(LineFormat.fromImage("##### type1 @@@ @@@"));
 * </pre>
 * will parse flat files with a number in position 0-5, the constant <tt>type1</tt> at position 5-10, and two 
 * alphanumeric fields of three characters each (see {@link org.sadun.text.ffp.LineFormat} for image syntax).
 * <p>
 * <p>
 * Alternatively, if a flat file contains lines with different formats, the corresponding 
 * {@link org.sadun.text.ffp.LineFormat} objects can be associated to a {@link FlatFileFormat.Condition 
 * condition} by using {@link #declare(Condition, LineFormat)}.
 * <p>
 * Parsing will occur by registering a {@link Listener} (or associating a specific listener to each line format
 * by using {@link #addDispatch(LineFormat, Listener) addDispatch} and invoking one of the 
 * {@link #parse(Reader) parse()} methods:
 * <pre>
 *  ffp.addListener(new EchoListener());
 *  ffp.parse(new File("myfile.txt"));
 * </pre>
 * <p>
 * For each line, the parser will examine the conditions and use the line format which matches. Only one {@link FlatFileFormat.Condition
 * condition} is allowed to match for each line, and it's programmer responsability to ensure that the given
 * conditions do not overlap. 
 * 
 * @version 2.0
 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano Sadun</a>
 */
public class FlatFileParser {

	/**
	 * An object implementing this interface can decide whether or not the physical line currently available
	 * for reading match or not a certain condition, in the {@link FlatFileParser.Condition#holds(int, int, LineReader)} method. 
	 * A Condition is usually associated to a {@link LineFormat} in a {@link FlatFileParser} object.
	 * <p>
	 * The Condition object can read as many physical lines as required from the file before deciding
	 * whether or not the condition holds. It can check things like whether the current line number is lower than
	 * a certain number, or if two lines are available and the second contains a certain constant at 
	 * a certain position, etc. 
	 * <p>
	 * For example, if a file has ten lines of header with a certain format,
	 * followed by any number of lines with a second format, code like the following is
	 * a possible Condition identifying header lines.
	 * <p>
	 * <pre>
	 *  public boolean holds(int logicalLineCount, int physicalLineCount, LineReader reader) throws IOException {
	 *   // Check if we are in the file header
	 *   return logicalLineCount < 10;
	 *  }
	 * </pre>
	 * <p>
	 * Some implementations are provided by the library for common types of conditions.
	 * 
	 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano Sadun</a>
	 */
	public interface Condition {
		
		/**
		 * Return true if a desired logical condition holds.
		 * The condition can regard the next lines available in the file (accessible by the <tt>reader</tt> parameter)
		 * or the number of physical or logical lines read so far.
		 * <p>
		 * Note that the implementation can use the <tt>reader</tt> object to read as many lines as necessary
		 * to verify if a certain condition holds - but should return <b>false</b> unless an IOException
		 * due to real i/o problems (and not, for example, that not enough lines exist on the file and therefore EOF 
		 * is reached) is raised. 
		 * 
		 * @param logicalLineCount the logical lines read so far
		 * @param physicalLineCount the physical lines read so far
		 * @param reader an object allowing to read lines on the file
		 * @return <b>true</b> if the conditions hold, <b>false</b> otherwise.
		 * @throws IOException if a I/O problem arises when reading lines
		 */
		public boolean holds(int logicalLineCount, int physicalLineCount, LineReader reader) throws IOException;
		
		/**
		 * Return a human-readable description of the condition.
		 * @return a human-readable description of the condition.
		 */
		public String toString();
	}

	/**
	 * Classes implementing this interface can be registered in a {@link FlatFileParser} 
	 * (using {@link FlatFileParser#addListener(Listener)}) to react to successfully parsed
	 * lines in the flat file.
	 * <p>
	 * The {@link LineFormat} is passed as well, for easier handling of mixed lines files.
	 * 
	 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano Sadun</a>
	 */
	public interface Listener {
		/**
		 * Invoked when a line is successfully parsed. The corresponding {@link LineFormat}
		 * is passed as well.
		 * 
		 * @param format the {@link LineFormat} object which has executed the parsing  
		 * @param values the values resulting from the parsing
		 */
		public void lineParsed(LineFormat format, int logicalLinecount, int physicalLineCount, String[] values) throws AbortFFPException;
	}
	
	/**
	 * Classes implementing this extension of {@link Listener} will receive more events
	 * on flat file parsing.
	 *
	 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano Sadun</a>
	 * @version 1.0
	 */
	public interface AdvancedListener extends Listener {
		
		/**
		 * Invoked when the parser has started 
		 */
		public void parsingStarted() throws AbortFFPException;
		
		/**
		 * Invoked when the parser has terminated 
		 * @param successful <b>true</b> if the parsing has been successful
		 */
		public void parsingTerminated(boolean successful)  throws AbortFFPException;
		
	}
	
	/**
	 * Classes implementing this extension of {@link Listener} will receive notification
	 * about lines where no condition matches on parsing, if the {@link FlatFileParser#isFailOnNoMatchingConditions() 
	 * failOnNoMatchingConditions} property is <b>false</b>. 
	 *
	 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano Sadun</a>
	 * @version 1.0
	 */
	public interface UnmatchedLineListener extends Listener {
		
		/**
		 * Invoked when the {@link FlatFileParser#isFailOnNoMatchingConditions() 
	     * failOnNoMatchingConditions} property is <b>false</b> and no matching conditions are found
	     * for a certain line.
	     *  
		 * @param physicalLineCount the physical line number
		 * @param line the line
		 */
		public void noMatchingCondition(int physicalLineCount, String line);
	}
	
	/**
	 * Objects implementing this interface allow to access the flat file during parsing, from outside the
	 * parser. This class is not intended for user implementation; rather, a proper implementation is created 
	 * and maintained by the parser, and passed as necessary to user-defined objects.
	 * 
	 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano Sadun</a>
	 */
	public interface LineReader {
		/**
		 * Read a physical line from the file.
		 * 
		 * <b>null</b> is returned in case no lines are available.
		 * @return a line or <B>null</b>
		 * @throws IOException if a I/O problem occurs when reading the file 
		 */
		public String readLine() throws IOException;
	}

	private Condition ALWAYS_HOLDING_CONDITION = new Condition() {
		public boolean holds(int logicalLineCount, int physicalLineCount, LineReader reader){
			return true;
		}
		
		public String toString() {
			return "always";
		}
	};

	private Map conditions = new HashMap();
	private String lineSeparator = System.getProperty("line.separator");
	private Set listeners = new HashSet();
	
	private boolean fastMatchMode=false;
	private boolean autoTrimMode=false;
	private boolean failOnNoMatchingConditions=true;
	private boolean failOnLineParsingError = defaultFailOnLineParsingError;
	private Condition [] lastHoldingCondition = new Condition[1];
	private Logger logger=Logger.getLogger(LOGGER_CHANNEL_NAME);
	
	private DispatcherListener internalDispatcher=new DispatcherListener();

	/**
	 * The logger channel used by this package.
	 * Its value is <tt>org.sadun.text.ffp</tt>.
	 */
	public static final String LOGGER_CHANNEL_NAME = "org.sadun.text.ffp";
	

	/**
	 * The default value used to intialize the {@link #isFailOnLineParsingError() failOnLineParsingError} 
	 * property when a parser instance is constructed.
	 */
	public static boolean defaultFailOnLineParsingError = true;

	/**
	 * Create a parser with no associated line formats.
	 * <p>
	 * Before running {@link #parse(Reader)}, at least one format must be added
	 * by invoking {@link #declare(Condition, LineFormat) declare()}.
	 */
	public FlatFileParser() {
		this(null);
	}

	/**
	 * Creates a parser which parses flat files whose all lines have the given format. 
	 * @param format
	 */
	public FlatFileParser(LineFormat format) {
		if (format != null) {
			declare(ALWAYS_HOLDING_CONDITION, format);
		}
	}

	/**
	 * Declare that a certain {@link LineFormat} is to be used under the given condition.
	 * 
	 * @param condition the {@link Condition} condition that must hold.
	 * @param format the {@link LineFormat} to use.
	 */
	public void declare(Condition condition, LineFormat format) {
		if (!lineSeparator.equals(format.getLineSeparator()))
			throw new IllegalArgumentException("The given format does not use the same line separator as the parser");
			
		if (conditions.keySet().contains(ALWAYS_HOLDING_CONDITION)) 
			throw new IllegalStateException("Programming error: line format already specified by either FlatFileParser(LineFormat) or declare(LineFormat). To handle mixed format files, please use another constructor and/or the declare(Condition, LineFormat) method to associate formats to conditions.");	
			
		conditions.put(condition, format);
		logger.fine("Line format "+format.getName()+" will match "+condition);
	}
	
	/**
	 * Declare that the only line format recognized by the parser be the one given.
	 * Further declarations (i.e. further invocations of any <tt>declare()</tt> overload) will fail.
	 * <p>
	 * Invoking this method after creating a parser with <tt>new {@link #FlatFileParser()}</tt> is 
	 * equivalent to use the constructor {@link #FlatFileParser(LineFormat)}.
	 * 
	 * @param format the {@link LineFormat} to use for each line in the flat file. 
	 */
	public void declare(LineFormat format) {
		declare(ALWAYS_HOLDING_CONDITION, format);
	}
	
	/**
	 * Declare that the only line format recognized by the parser be the one whose image is given.
	 * Further declarations (i.e. further invocations of any <tt>declare()</tt> overload) will fail.
	 * <p>
	 * Invoking this method after creating a parser with <tt>new {@link #FlatFileParser()}</tt> is 
	 * equivalent to use the constructor {@link #FlatFileParser(LineFormat)}.
	 * 
	 * @param formatImage the {@link LineFormat}'s image to use for each line in the flat file.
	 * @exception ImageParseException if the given image is not correct
	 * @see {@link LineFormat#fromImage(String)} 
	 */
	public void declare(String formatImage) throws ImageParseException {
		declare(ALWAYS_HOLDING_CONDITION, LineFormat.fromImage(formatImage)); 
		
	}
	
	/**
	 * Declare that a certain {@link LineFormat}, whose image is given, is to be used 
	 * under the given condition.
	 * 
	 * @param condition the {@link Condition} condition that must hold.
	 * @param formatImage the {@link LineFormat}'s image to use for each line in the flat file.
	 * @exception ImageParseException if the given image is not correct
	 * @see {@link LineFormat#fromImage(String)}
	 */
	public void declare(Condition condition, String formatImage) throws ImageParseException {
		declare(condition, LineFormat.fromImage(formatImage));
	}
	
	/**
	 * Parse the text from in the given file.
	 * <p>
	 * Notification of successful parsed lines is sent to any registered {@link FlatFileParser.Listener}
	 * ({@link FlatFileParser#addListener(Listener)} is used for registration).
	 * 
	 * @param file the file to parse
	 * @throws IOException if an I/O exception occurs 
	 * @throws FFPParseException if the text format cannot be parsed correctly
	 */
	public void parse(File file) throws IOException, FFPParseException {
		
		if (conditions.size()==0) throw new IllegalStateException("Programming error: no line formats have been declared. Please invoke declare() to declare a line format.");
		
		Reader reader=null;
		try {
			reader=new BufferedReader(new FileReader(file));
			parse(reader);
		} finally {
			if (reader!=null)
				reader.close();
		}
	}

	/**
	 * Parse the text from the given reader.
	 * <p>
	 * Notification of successful parsed lines is sent to any registered {@link FlatFileParser.Listener}
	 * ({@link FlatFileParser#addListener(Listener)} is used for registration).
	 * 
	 * @param sourceReader the reader to read the text from
	 * @throws IOException if an I/O exception occurs 
	 * @throws FFPParseException if the text format cannot be parsed correctly
	 */
	public void parse(Reader sourceReader) throws IOException, FFPParseException {
		
		int logicalLineCount=1;
		int physicalLineCount=1;
		
		LinePushbackReader reader=new LinePushbackReader(sourceReader, getLineSeparator());
		String line="file start";
		boolean moreLines=true; 
		boolean successful=false;
		
		notifyStart();
		
		try {
			do {
				
				StringBuffer logicalLine=new StringBuffer();
				
				Condition[] matchingConditions = findMatchingCondition(logicalLineCount, physicalLineCount, reader);
				
				if (matchingConditions.length == 0)
					if (reader.atEOF()) {
						moreLines=false;
						continue;
					} else {
						// If we're not supposed to fail on parsing error, or on no matching conditions, ignore
						if ((failOnNoMatchingConditions || failOnLineParsingError)) {
							throw new NoMatchingConditionException(physicalLineCount, line);
						} else { // Ignore
						    // Consume the line
						    line=reader.readLine();
						    physicalLineCount++;
							if (! failOnLineParsingError) logger.warning("No matching conditions for line '"+line+"'");
							notifyUnmatchedLine(physicalLineCount, line);
							continue;
						} 
					}
				if (matchingConditions.length > 1)
					if (failOnLineParsingError)
						throw new MultipleMatchingConditionsException(
							matchingConditions,
							line);
						
				if (reader.atEOF()) {
									moreLines=false;
									continue;
								}		
						
				LineFormat format =
					(LineFormat) conditions.get(matchingConditions[0]);
					
				// Read as many physical lines are required
				for(int i=0;i<format.getPhysicalLinesCount();i++) {
					logicalLine.append(reader.readLine());
					logicalLine.append(getLineSeparator());
				}
				
				line = logicalLine.toString();
					
				try { 
					notifyMatch(format, logicalLineCount, physicalLineCount, format.parse(line, autoTrimMode));
				} catch (FFPParseException e) {
					if (failOnLineParsingError) throw e;
					else logger.warning(e.getMessage());
				}
				logicalLineCount+=1;
				physicalLineCount+=format.getPhysicalLinesCount();
			} while (moreLines);
			successful=true;
		} finally {
			notifyTermination(successful);
			if (reader!=null) reader.close();	
		}
		
	}

	
	/**
	 * Add the given listener to the registered listeners set.
	 * <p>
	 * See also {@link #addDispatch(LineFormat, Listener)}.
	 * 
	 * @param l the listener to add
	 */
	public void addListener(Listener l) {
		listeners.add(l);
	}
	
	/**
	 * Associates the given LineFormat to the given listener, so that when
	 * a line is parsed by the given format, the event is sent to the given
	 * listener.
	 * <p>
	 * An internal object of {@link DispatcherListener} class is used.
	 * 
	 * @param format the line format on which the dispatch is to be based
	 * @param l the listener to associate
	 */
	public void addDispatch(LineFormat format, Listener l) {
	    // Check if the internal dispatcher has been added already
	    synchronized(this) {
	        boolean added=false;
	        for(Iterator i=listeners.iterator();i.hasNext();) {
	            Listener lst = (Listener)i.next();
	            if (lst==internalDispatcher) {
	                added=true;
	                break;
	            }
	        }
	        if (!added) listeners.add(internalDispatcher);
	    }
	    internalDispatcher.associateListener(format, l);
	}

	/**
	 * Remove the given listener from the registered listeners set.
	 * @param l the listener to remove
	 */
	public void removeListener(Listener l) {
		listeners.remove(l);
	}

	private void notifyMatch(LineFormat format, int logicalLinecount, int physicalLineCount, String[] values) throws AbortFFPException {
		for (Iterator i = listeners.iterator(); i.hasNext();) {
			Listener listener = (Listener) i.next();
			listener.lineParsed(format, logicalLinecount, physicalLineCount, values);
		}
	}

	private void notifyTermination(boolean successful) throws AbortFFPException {
		for (Iterator i = listeners.iterator(); i.hasNext();) {
			Listener listener = (Listener) i.next();
			if (listener instanceof AdvancedListener) 
				((AdvancedListener)listener).parsingTerminated(successful);
		}
	}
	
	private void notifyStart() throws AbortFFPException {
		for (Iterator i = listeners.iterator(); i.hasNext();) {
			Listener listener = (Listener) i.next();
			if (listener instanceof AdvancedListener) 
				((AdvancedListener)listener).parsingStarted();
		}
	}
	
	private void notifyUnmatchedLine(int physicalLineCount, String line) {
		for (Iterator i = listeners.iterator(); i.hasNext();) {
		Listener listener = (Listener) i.next();
		if (listener instanceof UnmatchedLineListener) 
			((UnmatchedLineListener)listener).noMatchingCondition(physicalLineCount, line);
		}
	}
	
	/**
	 * @param line
	 * @return
	 */
	private Condition[] findMatchingCondition(int logicalLineCount, int physicalLineCount, LinePushbackReader reader) throws IOException {
		
		if (reader.atEOF()) {
			logger.finer("At EOF, returning no matching conditions");
			return new Condition[0];
		}
		
		// Fast match mode - check if the last condition stil holds, if it does, use it with no further ado
		if (fastMatchMode && lastHoldingCondition[0] != null) {
			logger.finer("Fast match mode enabled, checking last holding condition '"+lastHoldingCondition[0]+"'..");
			reader.mark();
			if (lastHoldingCondition[0].holds(logicalLineCount, physicalLineCount, reader)) {
				reader.reset();
				logger.finer("Last holding condition holds, ignoring other conditions");
				return lastHoldingCondition;
			}
			logger.finer("Last holding condition does not hold anymore, continuining..");
			reader.reset();
		}
		
		// Normal mode: run all the conditions and collect the ones which match
		List l = new ArrayList();
		for(Iterator i = conditions.keySet().iterator();i.hasNext();) {
			Condition condition = (Condition)i.next();
			logger.finer("Verifying condition '"+condition+"'");			
			reader.mark();
			if (condition.holds(logicalLineCount, physicalLineCount, reader)) {
				logger.finer("Condition '"+condition+"' holds.");
				l.add(condition);
			}
			reader.reset();
		}
		Condition [] result = new Condition[l.size()];
		l.toArray(result);
		logger.finer("Total of "+result.length+" conditions holding");
		return result;
	}

	/**
	 * Return the line separator string used by this parser.
	 * @return the line separator string used by this parser.
	 */
	public String getLineSeparator() {
		return lineSeparator;
	}

	/**
	 * Set the line separator string used by this parser.
	 * The separator is also set on all the {@link LineFormat}s currently defined.
	 * @param lineSeparator the line separator string used by this parser.
	 */
	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
		for (Iterator i = conditions.keySet().iterator(); i.hasNext();) {
			LineFormat format = (LineFormat) conditions.get(i.next());
			format.setLineSeparator(lineSeparator);
		}
	}
	
	/**
	 * Return whether or not the parser should fail when a exception occurs parsing
	 * a line, or just emit a warning on the {@link FlatFileParser#LOGGER_CHANNEL_NAME}
	 * channel.
	 * 
	 * @return whether or not the parser should fail when a exception occurs parsing
	 * a line, or just emit a warning on the {@link FlatFileParser#LOGGER_CHANNEL_NAME}
	 * channel.
	 */
	public boolean isFailOnLineParsingError() {
		return failOnLineParsingError;
	}

	/**
	 * Set whether or not the parser should fail when a exception occurs parsing
	 * a line, or just emit a warning on the {@link FlatFileParser#LOGGER_CHANNEL_NAME}
	 * channel.
	 * 
	 * @param failOnLineParsingError if <b>true</b>, the {@link #parse(Reader)} methods
	 *        will fail if there's a parsing problem on a line.
	 */
	public void setFailOnLineParsingError(boolean failOnLineParsingError) {
		this.failOnLineParsingError = failOnLineParsingError;
	}

	/**
	 * Return the current state of the the fast match mode. Fast match mode speeds up
	 * mixed line parsing by using the {@link LineFormat} associated of the last holding 
	 * {@link Condition} if it still holds, avoiding to check all the conditions.
	 * 
	 * @return the current state of the the fast match mode.
	 */
	public boolean isFastMatchMode() {
		return fastMatchMode;
	}

	/**
	 * Set the current state of the the fast match mode. Fast match mode speeds up
	 * mixed line parsing by using the {@link LineFormat} associated of the last holding 
	 * {@link Condition} if it still holds, avoiding to check all the conditions.
	 * 
	 * @param fastMatchMode if <b>true</b>, enables the fastmatch mode, else disables it.
	 */
	public void setFastMatchMode(boolean fastMatchMode) {
		this.fastMatchMode = fastMatchMode;
	}

	/**
	 * Return the current state of the auto trim mode. In auto trim mode, strings are trimmed of trailing spaces
	 * before being notified to the listeners.
	 * 
	 * @return the current state of the the auto trim mode.
	 */
	public boolean isAutoTrimMode() {
		return autoTrimMode;
	}

	/**
	 * Set the auto trim mode. In auto trim mode, strings are trimmed of trailing spaces
	 * before being notified to the listeners.
	 * 
	 * @param autoTrimMode if <b>true</b>, enables the auto trim mode, else disables it.
	 */
	public void setAutoTrimMode(boolean autoTrimMode) {
		this.autoTrimMode = autoTrimMode;
	}
	
	/*
	public static void main(String args[]) throws Exception {
		FlatFileParser ffp = new FlatFileParser();
		ffp.logger.setLevel(Level.ALL);
		ffp.declare(new ConstantFoundInLineCondition(1, "aa", 5), LineFormat.fromImage("##### aa #####"));
		ffp.declare(new ConstantFoundInLineCondition(1, "bb", 5), LineFormat.fromImage("##### bb #####"));
		ffp.addListener(new Listener() {
			public void matched(String formatName, String[] values) {
				System.out.println(formatName);
				ObjectLister.getInstance().println(values);
			}
		});
		ffp.parse(new StringReader("12345aa67890\r\n54321aa09876"));
	}
	*/

	protected Map getFieldsByCondition() { return conditions; }

	/**
	 * Return whether the parser will fail or not if no conditions match a line.
	 * @return <b>true</b> if the parser will fail or not if no conditions match a line, otherwise <b>false</b>.
	 */
	public boolean isFailOnNoMatchingConditions() {
		return failOnNoMatchingConditions;
	}

	/**
	 * Set whether the parser will fail or not if no conditions match a line.
	 * @param failOnNoMatchingConditions <b>true</b> if the parser must fail if no conditions match a line, otherwise <b>false</b>.
	 */
	public void setFailOnNoMatchingConditions(boolean failOnNoMatchingConditions) {
		this.failOnNoMatchingConditions = failOnNoMatchingConditions;
	}

}
