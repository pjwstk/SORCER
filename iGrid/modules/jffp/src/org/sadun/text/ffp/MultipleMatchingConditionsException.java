/*
 * Created on Jan 16, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp;

import java.io.StringWriter;

import org.sadun.util.IndentedPrintWriter;

/**
 * 
 * @author Cristiano Sadun
 */
public class MultipleMatchingConditionsException extends FFPParseException {

	/**
	 * @param msg
	 */
	public MultipleMatchingConditionsException(FlatFileParser.Condition [] conditions, String line) {
		super(makeMsg(conditions, line));
	}
	
	private static String makeMsg(FlatFileParser.Condition [] conditions, String line) {
		StringWriter sw = new StringWriter();
		IndentedPrintWriter pw = new IndentedPrintWriter(sw);
		
		pw.println("Multiple conditions match line '"+line+"'");
		pw.incIndentation(3);
		for(int i=0;i<conditions.length;i++)
		pw.println(conditions[i]);
		return sw.toString();
	}

}
