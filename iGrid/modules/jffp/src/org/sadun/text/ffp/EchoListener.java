/*
 * Created on Jan 17, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp;

import java.io.PrintStream;

import org.sadun.util.ObjectLister;

/**
 * A {@link org.sadun.text.ffp.FlatFileParser.Listener} which
 * echoes the parsed data when a {@link org.sadun.text.ffp.LineFormat} whose is found whose name
 * matches a regular expression (provided at construction). For example,
 * <p>
 * <pre>
 *  FlatFileParser ffp = new FlatFileParser();
 * 
 *  ..set up three LineFormat objects, one named "header", the second "data1" and the third "data2"
 * 
 *  ffp.addListener(new DumpListener("data*"));
 * </pre>
 *
 * @author Cristiano Sadun
 */
public class EchoListener extends BaseListener {
	
	private PrintStream out;
	
	protected final void onFormatName(LineFormat format, int logicalLineCount, int physicalLineCount, String[] values) {
		out.println("Line "+logicalLineCount+": "+ObjectLister.getInstance().list(values));
	}

	/**
	 * Create an EchoListener which echoes lines whose format name matches the given regular expression
	 * on the given stream.
	 * 
	 * @param regexp the regular expression that the parsed line format's name must match
	 * @param out the stream to echo to
	 */
	public EchoListener(String regexp, PrintStream out) {
		super(regexp, true);
		this.out=out;
	}

	/**
	 * Create an EchoListener which echoes lines whose format name matches the given regular expression
	 * on System.out.
	 * 
	 * @param regexp the regular expression that the parsed line format's name must match
	 * @param out the stream to echo to
	 */	
	public EchoListener(String regexp) {
		this(regexp, System.out);
	}
	
	/**
	 * Create an EchoListener which echoes every line on the given stream.
	 * 
	 * @param out the stream to echo to 
	 */
	public EchoListener(PrintStream out) {
		this(".*", out);
	}

	/**
	 * Create an EchoListener which echoes every line on System out.
	 */
	public EchoListener() {
		this(".*");
	}
	

}
