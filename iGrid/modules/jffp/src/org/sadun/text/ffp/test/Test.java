/*
 * Created on Jan 17, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.sadun.text.ffp.AndCondition;
import org.sadun.text.ffp.ConstantLineCondition;
import org.sadun.text.ffp.CountCondition;
import org.sadun.text.ffp.EchoListener;
import org.sadun.text.ffp.FlatFileParser;
import org.sadun.text.ffp.LineFormat;
import org.sadun.text.ffp.NotCondition;

/**
 * A test class which reads a mixed-line layout: a one-line header, followed by an arbirary number
 * of data lines, followed by a single line containing EOF.
 *   
 * @author Cristiano Sadun
 */
public class Test {

	public static void main(String[] args) throws Exception {
		
		FlatFileParser parser = new FlatFileParser(LineFormat.fromImage("##### XX @@@@@@@@@@"));
		parser.addListener(new EchoListener());
		parser.parse(new File("testfile0.txt"));
		System.exit(0);

		// Create a parser
		FlatFileParser.defaultFailOnLineParsingError=false;
		FlatFileParser ffp = new FlatFileParser();
		
		// There are many ways to indicated the conditions attached to 
		// each line format. Here, we start by declaring a condition
		// which holds when the line is identical to "EOF"...
		FlatFileParser.Condition eofCondition =
			new ConstantLineCondition("EOF");
			
		// ..and we set as a condition for a data line to have
		// a count greater than 1 (the first line is a header)
		// and not matching the previous condition. 
		FlatFileParser.Condition dataLineCondition =
			new AndCondition(
				new CountCondition(1, CountCondition.GREATER),
				new NotCondition(eofCondition));
				
		// Set to ignore trailing chars by default
		LineFormat.defaultFailOnTrailingChars=false;

		// We declare the data line format, associated to its condition
		ffp.declare(
			dataLineCondition,
			LineFormat.fromImage(
				"data",
				"@@@@@@@@@@@@@@@@@@@@ @@@@@@@@@@@@@@@ #### #### /bT1"));
		
		// Same for the EOF line		
		ffp.declare(eofCondition, LineFormat.fromImage("terminator", "EOF"));

		// The condition for the header line is simply to be the first, so
		// we declare it on the spot, together with the header format 
		ffp.declare(
			new CountCondition(1, CountCondition.EQUAL),
			LineFormat.fromImage("header", "@@@@@ ## - ## - ####"));

		// We add a listener which just echoes the parsed data
		ffp.addListener(new EchoListener("data*"));

		// Finally, open the file, parse and close it.
		BufferedReader reader =
			new BufferedReader(new FileReader("testfile.txt"));
		ffp.parse(reader);
		reader.close();
	}
}
