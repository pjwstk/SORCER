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
import sorcer.vfe.filter.RegexFirstMatchFilter;
import sorcer.vfe.filter.RegexReplaceFilter;
import sorcer.vfe.filter.TextFilter;

/**
 * Example on how to use TextFilters: RegexReplaceFilter
 */
public class VariableEvaluation8 {

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
		}
	}

	public static void test1() throws ParException, EvaluationException,
			ConfigurationException, FilterException, IOException {
		logger.info("\n\t>>>>>>>>>>>>>>>>> test1 create a RegexReplaceFilter");
	
		String contents =
		"#This is a test of keyword,variable in field" + '\n' +
		"DESVAR, 0, 0.0" + '\n' +
		"DESVAR, 1, 1.0" + '\n' +
		"DESVAR, 2, 2.0" + '\n' +
		"DESVAR, 3, 3.0";
		
		TextFilter rgf = new RegexReplaceFilter(contents, "DESVAR", "VAR");

		System.out.println("RegexReplaceFilter: " + rgf.info());

		Var x1 = new Var("x1");
		x1.setFilter(rgf);
		
		System.out.println("x1 value:\n" + x1.getValue());

	}

	
	public static void test2() throws ParException, EvaluationException,
			ConfigurationException, FilterException, IOException {
		logger.info("\n\t>>>>>>>>>>>>>>>>> test1 create a RegexReplaceFilter");
		
		String contents =
			"#This is a test of keyword,variable in field" + '\n' +
			"DESVAR, 0, 0.0" + '\n' +
			"DESVAR, 1, 1.0" + '\n' +
			"DESVAR, 2, 2.0" + '\n' +
			"DESVAR, 3, 3.0";
			
			TextFilter rgf = new RegexReplaceFilter(contents, "1,", "10,");

			System.out.println("RegexReplaceFilter: " + rgf.info());

			Var x1 = new Var("x1");
			x1.setFilter(rgf);
			
			System.out.println("x1 value:\n" + x1.getValue());

	}
}
