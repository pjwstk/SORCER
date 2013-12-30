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

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RMISecurityManager;
import java.util.Properties;
import java.util.logging.Logger;

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
import sorcer.vfe.filter.PatternFilter.Pattern;

/**
 * Example how use Variables with BasicFileFilter
 */
public class VariableEvaluation16 {

	private static Logger logger = Log.getTestLog();

	public static void main(String[] args) throws Exception {
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());
		Properties props = Sorcer.getEnvProperties();
		logger.info("SORCER Environment configurtion:\n" + props);

		int test = new Integer(args[0]);
        switch (test) {
            case 1:  test1(); break;
            case 2:  test2(); break;
            case 3:  test3(); break;
//            case 4:  test4(); break;
//            case 5:  test5(); break;
//            case 6:  test6(); break;
//            case 7:  test7(); break;
        }
	}

	public static void test1() throws ParException, EvaluationException,
			FilterException, MalformedURLException, IOException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test1 Using FileFilter - set Variable value over URL");

		Pattern cs1p = new BasicPattern("cs1", "File", "Double", 2, 1, " ");
		Pattern cs2p = new BasicPattern("cs2", "File", "Double", 4, 2, " ");

		FileFilter f = null;
		String websterUrl = Sorcer.getWebsterUrl();

		try {
			f = new BasicFileFilter(new URL(websterUrl + "/paramTest.dat"),
					cs1p, cs2p);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Var x2 = new Var("x2");
		x2.setFilter(f, cs1p);
		System.out.println("cs1p pattern value: " + x2.getValue());

		Var x3 = new Var("x3");
		x3.setFilter(f, cs2p);
		System.out.println("cs2p pattern value: " + x3.getValue());

		x2.setValue(14.6);
		System.out.println("cs1p pattern reset value to: " + x2.getValue());

		Var x4 = new Var("x4");
		x4.setEvaluator(new JepEvaluator("x4lv", "x2 + 2"));
		x4.addArg(x2);
		System.out.println("x4 value: " + x4.getValue());

		Var x5 = new Var("x5");
		x5.setFilter(f, cs1p);
		System.out.println("x5 value: " + x5.getValue());
	}

	
	public static void test2() throws ParException, EvaluationException,
			FilterException, MalformedURLException, IOException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test1 Using FileFilter - file handlend via URLs");

		Pattern cs1p = new BasicPattern("cs1", "File", "Double", 2, 1, " ");
		Pattern cs2p = new BasicPattern("cs2", "File", "Double", 4, 2, " ");

		FileFilter f = null;
		String websterUrl = Sorcer.getWebsterUrl();

		try {
			f = new BasicFileFilter(new URL(websterUrl + "/paramTest.dat"),
					cs1p, cs2p);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Var x2 = new Var("x2");
		x2.setFilter(f, cs1p);
		System.out.println("cs1p pattern value: " + x2.getValue());

		Var x3 = new Var("x3");
		x3.setFilter(f, cs2p);
		System.out.println("cs2p pattern value: " + x3.getValue());

		Var x4 = new Var("x4");
		x4.setEvaluator(new JepEvaluator("x4lv", "x2 + 2"));
		x4.addArg(x2);
		System.out.println("x4 value: " + x4.getValue());

		// read file from the file filter URL
		InputStream is = f.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while ((line = reader.readLine()) != null) {
			out.println(line);
		}
		
		// read copy downloaded from the filter URL
		out.println("------------------------------------------------------------");
		File copy = new File("copy.dat");
		f.downloadFile(copy);
		
		reader = new BufferedReader(new FileReader(copy));
		while ((line = reader.readLine()) != null) {
			out.println(line);
		}
	}
	
	public static void test3() throws ParException, EvaluationException,
			FilterException, MalformedURLException, IOException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test3 Using FileFilter with setValueLocally");

		Pattern cs1p = new BasicPattern("cs1", "File", "Double", 2, 1, " ");
		Pattern cs2p = new BasicPattern("cs2", "File", "Double", 4, 2, " ");

		FileFilter f = null;
		String websterUrl = Sorcer.getWebsterUrl();

		try {
			f = new BasicFileFilter("test3", new URL(websterUrl + "/paramTest.dat"),
					cs1p, cs2p);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Var x2 = new Var("x2");
		x2.setFilter(f, cs1p);
		System.out.println("cs1p pattern original value: " + x2.getValue());
		x2.setValueLocally(10.0);
		System.out.println("cs1p pattern local value: " + x2.getValue());     
		
		Var x3 = new Var("x3");
		x3.setFilter(f, cs2p);
		System.out.println("cs2p pattern original value: " + x3.getValue());
		x3.setValueLocally(20.0);
		System.out.println("cs2p pattern local value: " + x3.getValue());
	}
}
