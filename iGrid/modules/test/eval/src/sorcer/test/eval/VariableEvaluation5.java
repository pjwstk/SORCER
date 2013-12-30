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
import java.net.MalformedURLException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.config.ConfigurationException;
import sorcer.service.EvaluationException;
import sorcer.util.Log;
import sorcer.util.Sorcer;
import sorcer.vfe.FilterException;
import sorcer.vfe.Var;
import sorcer.vfe.VarException;
import sorcer.vfe.evaluator.JepEvaluator;
import sorcer.vfe.filter.BasicFileFilter;
import sorcer.vfe.filter.FileFilter;
import sorcer.vfe.filter.BasicFileFilter.BasicPattern;
import sorcer.vfe.filter.BasicFileFilter.Format;
import sorcer.vfe.filter.PatternFilter.FormattedPattern;
import sorcer.vfe.filter.PatternFilter.Pattern;

/**
 * Example on how to use the BasicFileFilter
 */
public class VariableEvaluation5 {

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

	public static void test1() throws ParException, RemoteException,
			EvaluationException, ConfigurationException, MalformedURLException, FilterException {
		logger.info("\n\t>>>>>>>>>>>>>>>>> test1 create a BasicFileFilter");

		Pattern cs1p = new BasicPattern("cs1", "File", "Double", 2, 1, " ");
		Pattern cs2p = new BasicPattern("cs2", "File", "Double", 4, 2, " ");
		Pattern cs3p = new BasicPattern("cs3", "File", "Double", 6, 4, " ");
		Pattern kw1 = new BasicPattern("kw1", "Keyword Filter3", "Double", "[bracket keyword]");
		Pattern kw2 = new BasicPattern("kw2", Format.KEYWORD3, "Double", "[QDP]");
		
		// Filter for keyword, field, delimiter
		Pattern kwwdel = new BasicPattern("kwwdel", Format.KEYWORD1, "String", "home_dir", 2, "=");
		
		// Filter for keyword, field, delimiter
		Pattern kwwdel3 = new BasicPattern("kwwdel3", Format.KEYWORD1, "Double", "DESVAR, 3", 3, ",");
		
		// Filter for keyword, field, delimiter, sub field, sub delimiter used to expose a value in a record structured as follows
		//set sweep 45.0;
		// here the keyword is "set sweep" the first delimiter is " "(space) and the field based on (space) delimiter is 3 that produces
		// 45.0; as the item. Need to strip off the ";" this is done by specifying the subfield of 1 and the subdelimiter as ";".
		Pattern kwwdel4 = new BasicPattern("kwwdel4", Format.KEYWORD2, "Double", "set sweep", 3, " ", 1, ";");
		
		// Filter for keyword, linesAfter, field, delimiter - Enables exposing a field in a record n lines after the key word is found.
        //GPWG
        //35.0   26.0   
        //19.0   22.0
        //0.0   11.0    45.0   120.0
		// here the keyword is "GPWG" the linesAfter is "3" the filed is "4", the delimiter is " "(space). exposing "120."
		FormattedPattern kwwdel5 = new BasicPattern("kwwdel5", Format.KEYWORD4, "Double", "GPWG", 3, 4, " ");
		//kwwdel5.setFormatSpecifier("%10.3f");
		
		FileFilter f = null;
		try {
			f = new BasicFileFilter(new File(new File("..").getCanonicalPath()
			+ File.separator + "data" + File.separator + "paramTest.dat"), cs1p, cs2p, cs3p, kw1, kw2, kwwdel, kwwdel3, kwwdel4, kwwdel5);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Var x1 = new Var("x1");
		f.setPattern(kw1);
		x1.setFilter(f);

		f.setPattern(cs1p);
		System.out.println("cs1p pattern value = "+ x1.getValue());
		
		f.setPattern(cs2p);
		System.out.println("cs2p pattern value = "+ x1.getValue());
		
		f.setPattern(cs3p);
		System.out.println("cs3p pattern value = "+ x1.getValue());
		
		f.setPattern(kw1);
		System.out.println("kw1 pattern value = "+ x1.getValue());
		
		f.setPattern(kw2);
		System.out.println("kw2 pattern value = "+ x1.getValue());
		
		f.setPattern(kwwdel);
		System.out.println("kwwdel pattern value = "+ x1.getValue());
		
		f.setPattern(kwwdel3);
		System.out.println("kwwdel3 pattern value = "+ x1.getValue());
		
		f.setPattern(kwwdel4);
		System.out.println("kwwdel4 pattern value = "+ x1.getValue());
		
		f.setPattern(kwwdel5, "%10.3f");
		//f.setPattern(kwwdel5);
		System.out.println("kwwdel5 pattern value = "+ x1.getValue());
		x1.setValue(122.555555);
		System.out.println("kwwdel5 value for  122.555555 = "+ x1.getValue());
	}

	public static void test2() throws ParException, RemoteException,
			EvaluationException, ConfigurationException, MalformedURLException,
			FilterException {
		logger.info("\n\t>>>>>>>>>>>>>>>>> test2 create a BasicFileFilter");

		Pattern cs1p = new BasicPattern("cs1", "File", "Double", 2, 1, " ");
		Pattern cs2p = new BasicPattern("cs2", "File", "Double", 4, 2, " ");
		Pattern cs3p = new BasicPattern("cs3", "File", "Double", 6, 4, " ");
		Pattern kw1 = new BasicPattern("kw1", "Keyword Filter3", "Double",
				"[bracket keyword]");
		Pattern kw2 = new BasicPattern("kw2", Format.KEYWORD3, "Double",
				"[QDP]");

		// Filter for keyword, field, delimiter
		Pattern kwwdel = new BasicPattern("kwwdel", Format.KEYWORD1, "String",
				"home_dir", 2, "=");

		// Filter for keyword, field, delimiter
		Pattern kwwdel3 = new BasicPattern("kwwdel3", Format.KEYWORD1,
				"Double", "DESVAR, 3", 3, ",");

		// Filter for keyword, field, delimiter, sub field, sub delimiter used
		// to expose a value in a record structured as follows
		// set sweep 45.0;
		// here the keyword is "set sweep" the first delimiter is " "(space) and
		// the field based on (space) delimiter is 3 that produces
		// 45.0; as the item. Need to strip off the ";" this is done by
		// specifying the subfield of 1 and the subdelimiter as ";".
		Pattern kwwdel4 = new BasicPattern("kwwdel4", Format.KEYWORD2,
				"Double", "set sweep", 3, " ", 1, ";");

		// Filter for keyword, linesAfter, field, delimiter - Enables exposing a
		// field in a record n lines after the key word is found.
		// GPWG
		// 35.0 26.0
		// 19.0 22.0
		// 0.0 11.0 45.0 120.0
		// here the keyword is "GPWG" the linesAfter is "3" the filed is "4",
		// the delimiter is " "(space). exposing "120."
		Pattern kwwdel5 = new BasicPattern("kwwdel5", Format.KEYWORD4,
				"Double", "GPWG", 3, 4, " ");

		FileFilter f = null;
		try {
			f = new BasicFileFilter(new File(new File("..").getCanonicalPath()
					+ File.separator + "data" + File.separator
					+ "paramTest.dat"), cs1p, cs2p, cs3p, kw1, kw2, kwwdel,
					kwwdel3, kwwdel4, kwwdel5);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Var x2 = new Var("x2");
		x2.setFilter(f, cs1p);
		System.out.println("cs1p pattern value = " + x2.getValue());

		Var x3 = new Var("x3");
		x3.setFilter(f, cs2p);
		System.out.println("cs2p pattern value = " + x3.getValue());

		Var x4 = new Var("x4");
		x4.setFilter(f, cs3p);
		System.out.println("cs3p pattern value = " + x4.getValue());

		Var x5 = new Var("x5");
		x5.setFilter(f, kw1);
		System.out.println("kw1 pattern value = " + x5.getValue());

		Var x6 = new Var("x6");
		x6.setFilter(f, kw2);
		System.out.println("kw2 pattern value = " + x6.getValue());

		Var x7 = new Var("x7");
		x7.setFilter(f, kwwdel);
		System.out.println("kwwdel pattern value = " + x7.getValue());

		Var x8 = new Var("x8");
		x8.setFilter(f, kwwdel3);
		System.out.println("kwwdel3 pattern value = " + x8.getValue());

		Var x9 = new Var("x9");
		x9.setFilter(f, kwwdel4);
		System.out.println("kwwdel4 pattern value = " + x9.getValue());

		Var x10 = new Var("x10");
		x10.setFilter(f, kwwdel5);
		System.out.println("kwwdel5 pattern value = " + x10.getValue());
	}

	public static void test3() throws ParException, RemoteException,
			EvaluationException, ConfigurationException, MalformedURLException,
			FilterException {
		logger.info("\n\t>>>>>>>>>>>>>>>>> test3 create a BasicFileFilter with multiple patterns and resetting variable vaules");

		Pattern cs1p = new BasicPattern("cs1", "File", "Double", 2, 1, " ");
		Pattern cs2p = new BasicPattern("cs2", "File", "Double", 4, 2, " ");
		Pattern cs3p = new BasicPattern("cs3", "File", "Double", 6, 4, " ");
		Pattern kw1 = new BasicPattern("kw1", "Keyword Filter3", "Double",
				"[bracket keyword]");
		Pattern kw2 = new BasicPattern("kw2", Format.KEYWORD3, "Double",
				"[QDP]");

		// Filter for keyword, field, delimiter
		Pattern kwwdel = new BasicPattern("kwwdel", Format.KEYWORD1, "String",
				"home_dir", 2, "=");

		// Filter for keyword, field, delimiter
		Pattern kwwdel3 = new BasicPattern("kwwdel3", Format.KEYWORD1,
				"Double", "DESVAR, 3", 3, ",");

		// Filter for keyword, field, delimiter, sub field, sub delimiter used
		// to expose a value in a record structured as follows
		// set sweep 45.0;
		// here the keyword is "set sweep" the first delimiter is " "(space) and
		// the field based on (space) delimiter is 3 that produces
		// 45.0; as the item. Need to strip off the ";" this is done by
		// specifying the subfield of 1 and the subdelimiter as ";".
		Pattern kwwdel4 = new BasicPattern("kwwdel4", Format.KEYWORD2,
				"Double", "set sweep", 3, " ", 1, ";");

		// Filter for keyword, linesAfter, field, delimiter - Enables exposing a
		// field in a record n lines after the key word is found.
		// GPWG
		// 35.0 26.0
		// 19.0 22.0
		// 0.0 11.0 45.0 120.0
		// here the keyword is "GPWG" the linesAfter is "3" the filed is "4",
		// the delimiter is " "(space). exposing "120."
		Pattern kwwdel5 = new BasicPattern("kwwdel5", Format.KEYWORD4,
				"Double", "GPWG", 3, 4, " ");

		FileFilter f = null;
		try {
			f = new BasicFileFilter(new File(new File("..").getCanonicalPath()
					+ File.separator + "data" + File.separator
					+ "paramTest.dat"), cs1p, cs2p, cs3p, kw1, kw2, kwwdel,
					kwwdel3, kwwdel4, kwwdel5);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Var x2 = new Var("x2");
		x2.setFilter(f, cs1p);
		System.out.println("cs1p pattern value: " + x2.getValue());

		Var x3 = new Var("x3");
		x3.setFilter(f, cs2p);
		System.out.println("cs2p pattern value: " + x3.getValue());
		
		x2.setValue(22.6);
		System.out.println("cs1p pattern reset value to: " + x2.getValue());
		
		Var x4 = new Var("x4");
		x4.setPersister(f.setPattern(cs3p));
		x4.setEvaluator(new JepEvaluator("x4lv", "x2 + 2"));
		x4.addArg(x2);
		System.out.println("x4 value: " + x4.getValue());
	}

}
