/*
 * Copyright 2010 the original author or authors.
 * Copyright 2010 SorcerSoft.org.
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

package sorcer.test.context;

import java.rmi.RMISecurityManager;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Logger;

import sorcer.arithmetic.Arithmetic;
import sorcer.core.SorcerConstants;
import sorcer.core.context.ArrayContext;
import sorcer.core.context.Contexts;
import sorcer.core.context.ServiceContext;
import sorcer.core.context.node.ContextNode;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.util.Log;
import sorcer.util.Sorcer;
import sorcer.util.SorcerUtil;

/**
 * A set of use cases for testing the behavior of the {@link IntexedContext}
 * class and the {@link Context} API as well.
 * <ul>
 * <li>The 'array' use case tests the {@link IntexedContext} class.
 * <li>The 'arithmetic' use case test input and output parameters for the
 * {@link Arithmetic} interface
 * <li>The 'attributes' use case test usage of data attributes and associations
 * </ul>
 */
public class IndexedContextTester implements SorcerConstants {

	private static Logger logger = Log.getTestLog();

	private ArrayContext ca;

	public IndexedContextTester() {
		// init test data if needed here
		ca = new ArrayContext("Arithmetic");
	}

	public static void main(String[] args) throws ContextException {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}
		// Test the context array
		logger.entering(IndexedContextTester.class.getName(), "main");
		IndexedContextTester tester = new IndexedContextTester();
		tester.testEnv();

		// get test type for the user
		Scanner in = new Scanner(System.in);
		System.out.println("Enter test type: ");
		String testType = in.nextLine();

		logger.info("testing: " + testType);
		tester.run(testType);
	}

	private void run(String test) throws ContextException {
		try {
			if (test.equals("array")) {

				ca.v(10, 100);
				ca.vc(10, "not input or either output");

				ca.iv(1, 20);
				ca.ivc(1, "initial value is 20");
				ca.ivd(1, "input values 1 and 2 to be added");

				ca.iv(2, 50);
				ca.ivc(2, "initial value is 50");

				ca.iv(3, 60);
				logger.info("max index: \n" + ca.getMaxIndex());
				logger.info("size: \n" + ca.size());

				ca.iv(4, 70);
				ca.ivd(4, "input values 3 and 4 to be multiplied");

				ca.ov(5, 320);
				ca.ovc(5, "output for operation on values 1 and 2");

				ca.ov(6, 350);
				ca.ovc(6, "output for operation on values 3 and 4");

				ca.v(7, new ContextNode("service node"));
				ca.vc(7, "with service node");

				logger.info(">>>>>>>>>>input path: \n" + ca.inPaths());
				logger.info(">>>>>>>>>>sorted input path: \n"
						+ Arrays.toString(ca.getSortedInPaths()));

				logger.info("(toString) context array: \n" + ca);

				logger.info("getAllContextNodes: \n"
						+ Contexts.getContextNodes(ca));

				logger.info(ArrayContext.IN_ + ", getValuesStartsWith: \n"
						+ Contexts.getValuesStartsWith(ca, ArrayContext.IN_));

				logger.info(ArrayContext.IN_ + ", getKeysStartsWith: \n"
						+ Contexts.getKeysStartsWith(ca, ArrayContext.IN_));

				logger.info("getInputPathsMap: \n"
						+ Contexts.getInPathsMap(ca));

				logger.info("getInputPathsMap: \n"
						+ Contexts.getOutPathsMap(ca));

				logger.info("formted output: \n"
						+ Contexts.getFormattedOut(ca, false));
			} else if (test.equals("arithmetic")) {
				ca.iv(1, 10);
				ca.iv(2, 20);
				ca.ivd(4, "input values 3 and 4 to be multiplied");
				ca.iv(3, 40);
				ca.iv(4, 30);
				ca.ivd(4, "input values 3 and 4 to be subtracted");
				ca.ov(5, 320);
				ca
						.ovc(5,
								"output for sum of results iv(1)xiv(2) + (iv(3)-iv(4))");
				logger.info(">>>>> initial context: \n" + ca);

				List inpaths = Contexts.getInPaths(ca);
				logger.info("listInputPaths: \n" + inpaths);
				List outpaths = Contexts.getOutPaths(ca);
				logger.info("listOutputPaths: \n" + outpaths);

				logger.info("(get index of inpaths.get(0): \n"
						+ ca.getIndex((String) inpaths.get(0)));
				logger.info("(get index of inpaths.get(2): \n"
						+ ca.getIndex((String) inpaths.get(2)));

				int result = 0;
				for (Object path : outpaths)
					result += (Integer) ca.getValue((String) path);
				logger.info("result: \n" + result);

				result = 0;
				for (Object path : inpaths)
					result += (Integer) ca.getValue((String) path);
				logger.info("result: \n" + result);

				if (outpaths.size() == 1) {
					ca.putValue((String) outpaths.get(0), result);
					ca.putValue((String) outpaths.get(0) + SorcerConstants.CPS
							+ ArrayContext.COMMENT, "executed by localhost");
				}

				logger.info(">>>>> resulting context: \n" + ca);
			} else if (test.equals("attributes")) {
				// define this context attributes
				ca.setAttribute("arg");
				ca.setAttribute("result");
				ca.setAttribute("op|selector|arg1|arg2");

				// tag data nodes with associations
				// do not use the same singleton attributes
				// as component attributes in metaattributes
				// for example "arg" and "op|arg|arg"
				// instead "arg" and "op|arg1|arg2" as illustrated below.
				ca.iv(1, 10);
				ca.mark(ca.getPath("iv", 1), "arg|1");
				ca.iv(2, 20);
				ca.mark(ca.getPath("iv", 2), "arg|2");
				ca.ov(3, 0);
				ca.mark(ca.getPath("ov", 3), "op|add|1|2");
				ca.iv(4, 30);
				ca.mark(ca.getPath("iv", 4), "arg|3");
				ca.iv(5, 40);
				ca.mark(ca.getPath("iv", 5), "arg|4");
				ca.ov(6, 0);
				ca.mark(ca.getPath("ov", 6), "op|subtract|3|4");

				logger.info(">>>>> initial context: \n" + ca);

				List inpaths = Contexts.getInPaths(ca);
				logger.info("listInputPaths: \n" + inpaths);
				List outpaths = Contexts.getOutPaths(ca);
				logger.info("listOutputPaths: \n" + outpaths);

				// test associations in the ca service context
				logger.info("getMarkedPaths \"arg|1\": \n"
						+ Arrays.toString(Contexts.getMarkedPaths(ca,
								"arg|1")));
				logger.info("getMarkedPaths \"arg|2\": \n"
						+ Arrays.toString(Contexts.getMarkedPaths(ca,
								"arg|2")));
				logger.info("getMarkedPaths \"arg|4\": \n"
						+ Arrays.toString(Contexts.getMarkedPaths(ca,
								"arg|3")));
				logger.info("getMarkedPaths \"arg|5\": \n"
						+ Arrays.toString(Contexts.getMarkedPaths(ca,
								"arg|4")));

				logger.info("getMarkedPaths \"op|add|1|2\": \n"
						+ Arrays.toString(Contexts.getMarkedPaths(ca,
								"op|add|1|2")));
				logger.info("getMarkedPaths \"op|add|3|4\": \n"
						+ Arrays.toString(Contexts.getMarkedPaths(ca,
								"op|subtract|3|4")));

				logger.info(">>>>> resulting context: \n" + ca);
			}
			/*
			 * } else if (test.equals("function")) { Map vars = new HashMap();
			 * vars.put("x1", 10); vars.put("x2", 5); FuncContext fc = new
			 * FuncContext("x1 + 2 * x2 > 10", vars); //FuncContext fc = new
			 * FuncContext("x1 + 2 * x2", vars); logger.info(">>>>> input
			 * context: " + fc); logger.info(">>>>> result: \n" +
			 * fc.evaluate()); logger.info(">>>>> output context: " + fc); }
			 */
			else if (test.equals("lab")) {
				Context context = new ServiceContext("laboratory/name");
				context.setAttribute("person|first|middle|lastname");
				context.setSubject("laboratory/name", "SORCER");
				
				context.putValue("university", "TTU");
				context.putValue("university/department/name", "CS");
				context.putValue("university/department/room/number", "C20B");
				context.putValue("university/department/room/phone/number",
						"806-742-1194");
				context.putValue("university/department/room/phone/ext", "237");
				context.putValue("director/email", "sobol@cs.ttu.edu");
				context.mark("director", "person|Mike|W|Sobolewski");

				String univ, lastname, person, email;
				univ = (String) context.getValue("university");
				logger.info("univ: " + univ);
				email = (String) context.getValue("director/email");
				logger.info("email: " + email);
				lastname = context.getAttributeValue("director", "lastname");
				logger.info("lastname: " + lastname);
				person = context.getAttributeValue("director", "person");
				logger.info("person: " + person);
				Enumeration personPaths = context.markedPaths("person|Mike|W|Sobolewski");
				logger
						.info("personPaths: \n"
								+ Arrays.toString(SorcerUtil.makeArray(personPaths)));
			} else if (test.equals("positioning")) {
				ServiceContext context = new ServiceContext("array");
				String root = "arithmetic";
				Contexts.putInValueAt(context, root + "[0]" + CPS + VALUE, 20, 1);
				Contexts.putInValueAt(context, root + "[1]" + CPS + VALUE, 80, 2);
				// We know that the output is gonna be placed in this path
				context.putOutValue(root + "[3]" + CPS + VALUE, 0);
				
				List inputPaths = Contexts.getInPaths(context);
				List outPaths = Contexts.getOutPaths(context);
				for (int i = 0; i < inputPaths.size(); i++) {

					logger.info("in value " + i + ". "
							+ context.getValue((String) inputPaths.get(i)));

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void testEnv() {
		logger.info("iGrid.home: " + System.getProperty("iGrid.home"));
		Properties props = Sorcer.getEnvProperties();
		logger.info("SORCER Environment configurtion\n" + props);

		logger.info("data.root.dir: "
				+ Sorcer.getProperty("data.root.dir"));

		logger.info("provider.data.dir: "
				+ Sorcer.getProperty("provider.data.dir"));

		logger.info("provider data server: " + Sorcer.getDataServerUrl());
	}
}