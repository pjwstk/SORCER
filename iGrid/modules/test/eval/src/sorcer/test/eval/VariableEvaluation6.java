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
import sorcer.vfe.filter.GrepFilter;
import sorcer.vfe.filter.RegexFirstMatchFilter;
import sorcer.vfe.filter.RegexGroupFilter;
import sorcer.vfe.filter.RegexReaderFilter;
import sorcer.vfe.filter.TextFilter;

/**
 * Example on how to use the GrepFilter
 */
public class VariableEvaluation6 {

	private static Logger logger = Log.getTestLog();

	public static void main(String[] args) throws Exception {
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());
		logger.info("provider.lookup.accessor: "
				+ Sorcer.getProperty("provider.lookup.accessor"));

		int test = new Integer(args[0]);
		switch (test) {
		case 1: test1(); break;
		case 2: test2(); break;
		case 3: test3(); break;
		}
	}

	public static void test1() throws ParException, EvaluationException,
			ConfigurationException, FilterException, IOException {
		logger.info("\n\t>>>>>>>>>>>>>>>>> test1 create a GrepFilter");

		FileFilter gf = new GrepFilter(new File(new File("..")
				.getCanonicalPath()
				+ File.separator + "data" + File.separator + "paramTest.dat"),
				"DESVAR");

		//System.out.println("GrepFilter: " + gf.info());

		Var x1 = new Var("x1");
		x1.setFilter(gf);
		
		System.out.println("x1 value:\n" + x1.getValue());

	}

	
	public static void test2() throws ParException, EvaluationException,
			ConfigurationException, FilterException, IOException {
		logger.info("\n\t>>>>>>>>>>>>>>>>> test2 create a RegexReaderFilter");
	
		FileFilter gf = new RegexReaderFilter(new File(new File("..")
				.getCanonicalPath()
				+ File.separator + "data" + File.separator + "paramTest.dat"),
				"DESVAR");

		// System.out.println("GrepFilter: " + gf.info());

		Var x1 = new Var("x1");
		x1.setFilter(gf);

		System.out.println("x1 value:\n" + x1.getValue());

	}
	
	
	public static void test3() throws ParException, EvaluationException,
			ConfigurationException, FilterException, IOException {
		logger.info("\n\t>>>>>>>>>>>>>>>>> test3 create a pipe of RegexReaderFilter, RegexGroupFilter, and RegexSingletonFilter");

		FileFilter gf = new RegexReaderFilter(new File(new File("..")
				.getCanonicalPath()
				+ File.separator + "data" + File.separator + "paramTest.dat"),
				"DESVAR");

		TextFilter rgf = new RegexGroupFilter("\\s\\d,", 3);
//		rgf.setPattern("\\s\\d,");
//		rgf.setLineNumber(3);
		
		TextFilter sgf = new RegexFirstMatchFilter("\\d");
//		sgf.setPattern("\\d");
		
		gf.addFilter(rgf);
		gf.addFilter(sgf);

		Var x1 = new Var("x1");
		x1.setFilter(gf);

		System.out.println("x1 value:\n" + x1.getValue());

	}
}
