/*
 * Copyright 2009 the original author or authors.
 * Copyright 2009 SorcerSoft.org.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sorcer.test.eval;

import java.io.File;
import java.io.IOException;
import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import net.jini.config.ConfigurationException;
import sorcer.service.EvaluationException;
import sorcer.util.Log;
import sorcer.util.Sorcer;
import sorcer.vfe.FilterException;
import sorcer.vfe.Var;
import sorcer.vfe.VarException;
import sorcer.vfe.filter.FileFilter;
import sorcer.vfe.filter.LineImageFilter;
import sorcer.vfe.filter.LineSplitFilter;
import sorcer.vfe.filter.RegexGroupFilter;
import sorcer.vfe.filter.RegexTokenFilter;
import sorcer.vfe.filter.TableReader;

/**
 * Example on how to use the filed-oriented filters: 
 * LineImageFilter, FileFilter/RegexGroupFilter, FileFilter/RegexTokenFilter, 
 */
public class VariableEvaluation9 {

	private static Logger logger = Log.getTestLog();

	public static void main(String[] args) throws Exception {
		int test = new Integer(args[0]);
		switch (test) {
		case 1: test1(); break;
		case 2: test2(); break;
		case 3: test3(); break;
		case 4: test4(); break;
		case 5: test5(); break;
		case 6: test6(); break;
		}
	}

	public static void test1() throws ParException, EvaluationException,
			ConfigurationException, FilterException, IOException {
		logger.info("\n\t>>>>>>>>>>>>>>>>> test1 create a LineImageFilter");

		//line 3: abcdef gh "@@ @@ @@ @@@" - ab cd ef gh line 3
		//line 14: DESVAR, 2, 2.0  - "DESVAR, ## , ####" - DESVAR, 2 , 2.0
		
		LineImageFilter lif = new  LineImageFilter(new File(new File("..")
				.getCanonicalPath()
				+ File.separator + "data" + File.separator + "paramTest.dat"),
				"DESVAR, ## , ####", 4, 14);
	
		Var x1 = new Var("x1");
		x1.setFilter(lif);
		
		System.out.println("x1 value:\n" + x1.getValue());
	}
	
	public static void test2() throws ParException, EvaluationException,
			ConfigurationException, FilterException, IOException {
		logger.info("\n\t>>>>>>>>>>>>>>>>> test2 create a FileFilter/RegexGroupFilter");

		// line 3: abcdef gh "@@ @@ @@ @@@" - ab cd ef gh line 3
		// line 14: DESVAR, 2, 2.0 - "DESVAR, ## , ####" - DESVAR, 2 , 2.0

		FileFilter ff = new FileFilter(new File(new File("..")
				.getCanonicalPath()
				+ File.separator + "data" + File.separator + "paramTest.dat"));
		//"DESVAR,\\s(\\d+),\\s(\\d+.\\d*)"
		//DESVAR, 0, 0.0
		//DESVAR, 1, 1.0
		//DESVAR, 2, 2.0
		//DESVAR, 3, 3.0
		
		RegexGroupFilter rgf = new RegexGroupFilter("DESVAR,\\s(\\d+),\\s(\\d+.\\d*)", 2, 14);
		ff.setTextFilter(rgf);
		
		Var x1 = new Var("x1");
		x1.setFilter(ff);

		System.out.println("x1 value:\n" + x1.getValue());
	}

	public static void test3() throws ParException, EvaluationException,
			ConfigurationException, FilterException, IOException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test3 create a FileFilter/RegexTokenFilter");

		// line 3: abcdef gh "@@ @@ @@ @@@" - ab cd ef gh line 3
		// line 14: DESVAR, 2, 2.0 - "DESVAR, ## , ####" - DESVAR, 2 , 2.0

		FileFilter ff = new FileFilter(new File(new File("..")
				.getCanonicalPath()
				+ File.separator + "data" + File.separator + "paramTest.dat"));
		// "\\d+\\.{0,1}\\d*" - matches: [2, 2.0]
		// DESVAR, 0, 0.0
		// DESVAR, 1, 1.0
		// DESVAR, 2, 2.0
		// DESVAR, 3, 3.0

		RegexTokenFilter rtf = new RegexTokenFilter(
				"\\d+\\.{0,1}\\d*", 1, 14);
		ff.setTextFilter(rtf);

		Var x1 = new Var("x1");
		x1.setFilter(ff);

		System.out.println("x1 value:\n" + x1.getValue());
	}
	
	public static void test4() throws ParException, EvaluationException,
			ConfigurationException, FilterException, IOException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test4 create a FileFilter/LineSplitFilter");

		// line 3: abcdef gh "@@ @@ @@ @@@" - ab cd ef gh line 3
		// line 14: DESVAR, 2, 2.0 - "DESVAR, ## , ####" - DESVAR, 2 , 2.0

		FileFilter ff = new FileFilter(new File(new File("..")
				.getCanonicalPath()
				+ File.separator + "data" + File.separator + "paramTest.dat"));
		// "\\d+\\.{0,1}\\d*" - matches: [DESVAR, 2, 2.0]
		// DESVAR, 0, 0.0
		// DESVAR, 1, 1.0
		// DESVAR, 2, 2.0
		// DESVAR, 3, 3.0

		LineSplitFilter lsf = new LineSplitFilter(",\\s", 2, 14);
		ff.setTextFilter(lsf);

		Var x1 = new Var("x1");
		x1.setFilter(ff);

		System.out.println("x1 value:\n" + x1.getValue());
	}
	
	public static void test5() throws ParException, EvaluationException,
			ConfigurationException, FilterException, IOException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test5 create a FileFilter/LineSplitFilter");

		TableReader tr = new TableReader(new File(new File("..")
				.getCanonicalPath()
				+ File.separator + "data" + File.separator + "tableData.dat"), " ");

		System.out.println("table from file:\n" + tr.getValue());
	}
	
	public static void test6() throws ParException, EvaluationException,
			ConfigurationException, FilterException, IOException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test5 create a FileFilter/LineSplitFilter");

		TableReader tr = new TableReader(new File(new File("..")
				.getCanonicalPath()
				+ File.separator + "data" + File.separator + "tableData.dat"),
				" ");

		System.out.println("row1 from file:\n" + tr.nextRow());
		System.out.println("row2 from file:\n" + tr.nextRow());
		System.out.println("row3 from file:\n" + tr.nextRow());
	}
}
