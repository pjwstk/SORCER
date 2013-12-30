/*
 * Created on Jan 17, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.sadun.util.RotationalCharBuffer;

/**
 * A class which reads readers inexpensively and allows to push back read lines.
 * <p>
 * A {@link #mark()} operation ensures that if {@link #reset()} is invoked, the reader 
 * is rewind to the marked position. 
 * <p>
 * This implementation supports multiple nested mark() operations. 
 * 
 * @author Cristiano Sadun
 */
class LinePushbackReader implements FlatFileParser.LineReader {

	private Reader reader;
	private String lineSeparator;
	private List buffer = new ArrayList();

	// A stack of markedLines lists
	private Stack markedLineBuffers = new Stack();

	//private List markedLines = new ArrayList();
	//private boolean marking=false;

	private boolean atEOF = false;

	/**
	 * Create a pushback reader consuming characters from the given reader 
	 * and using the given line separator sequence.
	 * 
	 * @param reader the reader providing characters
	 * @param lineSeparator the line separator sequence
	 */
	public LinePushbackReader(Reader reader, String lineSeparator) {
		this.reader = reader;
		this.lineSeparator = lineSeparator;
	}
	
	/**
	 * Create a pushback reader consuming characters from the given reader.
	 * 
	 * @param reader the reader providing characters
	 */
	public LinePushbackReader(Reader reader) {
		this(reader, System.getProperty("line.separator"));
	}

	/**
	 * Read one line.
	 * 
	 * @return the next line of text, or <b>null</b> if no such line exist.
	 * @exception IOException if an I/O exception occurs while reading the line
	 */
	public synchronized String readLine() throws IOException {
		// if we have lines in the buffer, we read them.
		// if we're marking, the lines are also copied in the marked lines buffers, wherever they come from
		String line;
		if (buffer.size() > 0)
			line = (String) buffer.remove(0);
		else
			line = readLineFromReader();

		if (line == null) // No more lines, we're at physical EOF
			return null;

		if (isMarking()) {
			addMarkedLines(line);
		}
		
		return line;
	}

	private void printState() {
		System.out.println(">============================");
		if (isMarking()) {
			System.out.println(">Marking");
			int c=1;
			for (Iterator i = markedLineBuffers.iterator(); i.hasNext();) {
				System.out.println(">Stack frame "+(c++));
				List markedLines = (List) i.next();
				for(Iterator j=markedLines.iterator();j.hasNext();)
					System.out.println(">\t"+j.next());
				System.out.println(">--------------------");
			}
		}
		else System.out.println(">Not marking");
		System.out.println(">Buffer:");
		for(Iterator j=buffer.iterator();j.hasNext();)
			System.out.println(">\t"+j.next());
	}

	/**
	 * Add the given line to all the marking buffers
	 * @param line
	 */
	private void addMarkedLines(String line) {
		List markedLines = (List) markedLineBuffers.peek();
		markedLines.add(line);
	}

	/**
	 * Return <b>true</b> if the reader is marking, i.e. allows at least one {@link #reset()} operation. 
	 * @return <b>true</b> if the reader is marking, i.e. allows at least one {@link #reset()} operation.
	 */
	private boolean isMarking() {
		return markedLineBuffers.size() > 0;
	}

	/**
	 * Marks the current reading point. A matching {@link #reset()} will re-point the reader to this point.
	 * <p>
	 * Multiple marks are allowed - the corresponding {@link #reset()} will unstack each mark in turn.
	 */
	public synchronized void mark() {
		// Create and add a new marked lines buffer to the stack
		markedLineBuffers.push(new ArrayList());
		//System.out.println("MARK");
	}

	/**
	 * Resets the reading point to the last {@link #mark() mark}. If no marks exist, an IllegalStateException
	 * is thrown.
	 * <p>
	 * @exception IllegalStateException if no marks have been previously set
	 */
	public synchronized void reset() {
		if (!isMarking())
			throw new IllegalStateException("Not marking");
		// Add all the marked lines in the topmost list to the buffer
		List markedLines = (List) markedLineBuffers.pop();
		markedLines.addAll(buffer);
		// Switch marked lines and buffer to avoid creating new objects
		List tmp = buffer;
		buffer = markedLines;
		markedLines = tmp;
		markedLines.clear();
		//System.out.println("RESET");
	}

	/**
	 * Closes the reader.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		reader.close();
	}

	/**
	 * Return whether or not there are more lines to read.
	 * @return whether or not there are more lines to read.
	 */
	public boolean atEOF() {
		return buffer.size() == 0 && atEOF;
	}

	private String readLineFromReader() throws IOException {
		int c = 0;
		StringWriter sw = new StringWriter();
		RotationalCharBuffer cb =
			new RotationalCharBuffer(lineSeparator.length());
		while ((c = reader.read()) != -1) {
			cb.addToRight((char) c);
			sw.write(c);
			
			if (cb.equals(lineSeparator)) { // Line's finished
				String line = sw.toString();
				line =
					line.substring(0, line.length() - lineSeparator.length());
				return line;
			}
		}
		atEOF = true;
		String result = sw.toString();
		if (atEOF && result.length() == 0)
			return null;
		return result;
	}

	/* TEST 
	public static void main(String args[]) throws IOException {
		StringReader sr =
			new StringReader("Hello\r\nWorld\r\nThis\r\nis\r\nme!");
		LinePushbackReader reader = new LinePushbackReader(sr, "\r\n");

		reader.mark();

		String line;
		int c = 0;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			if (++c == 1)
				reader.mark();
		}
		reader.reset();
		System.out.println("AFTER RESET 1: " + reader.readLine());
		reader.mark();
		System.out.println("AFTER MARK: " + reader.readLine());
		reader.reset();
		System.out.println("AFTER RESET 2: " + reader.readLine());
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}

		reader.reset();

		System.out.println("----");
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}

		reader.close();

	}
	*/

}
