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

import static sorcer.co.operator.context;
import static sorcer.co.operator.entry;
import static sorcer.co.operator.list;
import static sorcer.co.operator.listContext;
import static sorcer.co.operator.map;
import static sorcer.co.operator.pair;
import static sorcer.co.operator.table;
import static sorcer.vo.operator.designModel;
import static sorcer.vo.operator.designVars;
import static sorcer.vo.operator.outputVars;
import static sorcer.vo.operator.vars;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.jini.config.ConfigurationException;
import sorcer.core.context.ListContext;
import sorcer.core.context.model.par.ParModel;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.EvaluationException;
import sorcer.util.Log;
import sorcer.util.Sorcer;
import sorcer.vfe.Var;
import sorcer.vfe.VarException;
import sorcer.vfe.evaluator.JepEvaluator;
import sorcer.vfe.filter.MapFilter;
import sorcer.vfe.util.Table;

/**
 * Example on how to create a ContextModel with context literals
 */
@SuppressWarnings("unchecked")
public class VariableEvaluation4 {

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
		case 4: test4(); break;
		case 5: test5(); break;
		}
	}

	public static void test1() throws ParException, RemoteException,
			EvaluationException, ConfigurationException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test1 esting static exports for literal collections");

		// anonymous, static constructor
		List asc = new ArrayList<String>() {
			{
				add("a");
				add("java");
				add("list");
			}
		};

		List<String> slist = list("a", "b", "c");
		logger.info("<<<<<<<<<<<<<<<<<<<< slist: " + slist);

		List<Integer> iList = list(1, 2, 3);
		logger.info("<<<<<<<<<<<<<<<<<<<< iList: " + iList);

		for (String elem : list("a", "java", "list")) {
			System.out.println(elem);
		}

		// use an o-Operator for declaring arbitrary tuples
		Map<String, String> smap = map(entry("Faust", "Goethe"), entry("Kosmos ",
				"Humboldt"), entry("Exertion", "SORCER"));
		logger.info("<<<<<<<<<<<<<<<<<<<< smap: " + smap);
	}
	
	
	public static void test2() throws ParException, RemoteException,
			EvaluationException, ConfigurationException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test2 testing static exports for literal Context");

		// use an x-Operator (tuple/product operator) for declaring arbitrary tuples
		try {
			Context cxt = context(entry("AFRL", "Kolonay"),
					entry("Cosmos ", "Humboldt"), 
					entry("Exertion", "SORCER"), 
					entry("num", 10));
			logger.info("literal context: " + cxt);
		} catch (ContextException e) {
			e.printStackTrace();
		}
	}
	
	public static void test3() throws ParException, RemoteException,
	EvaluationException, ConfigurationException {
		// use an x-Operator (tuple/product operator) for declaring a parametric model
		ParModel pm;
		try {
			Var v = new Var("x0");
			v.setFilter(new MapFilter(map(entry(3.0, "done"))));
			v.setEvaluator(new JepEvaluator("x1 + x2"));
			v.getEvaluator().addArg(new Var("x1", 1)).addArg(new Var("x2", 2));
			logger.info("v: " + v);

			pm = designModel(v, new Var("y1"), new Var("y2"), new Var("y3"));
			logger.info("parametric model: " + pm);
			
			Var y2 = pm.getVar("y2");
			logger.info("from parametric model y2: " + y2);
			logger.info("x0.getValue(): " + pm.getVar("x0").getValue());
		} catch (ContextException e) {
			e.printStackTrace();
		}
	}
	
	public static void test4() throws ParException, RemoteException,
	EvaluationException, ConfigurationException, ContextException {
		// use an ListContex
		ListContext cxt = new ListContext();
		cxt.add("Mike");
		cxt.add("Sobolewski");
		cxt.add(9204);
		cxt.add(new Var("x"));
		
		logger.info("list context: " + cxt);
		logger.info("list context elements: " + cxt.getElements());
		
		cxt.remove(1);
		logger.info("list context: " + cxt);
		logger.info("list context elements: " + cxt.getElements());
		
		cxt.set(1, "replaced");
		logger.info("list context: " + cxt);
		logger.info("list context elements: " + cxt.getElements());
		
		logger.info("get list second element: " + cxt.get(1));
		logger.info("set context second element: " + cxt.putValue("element[1]", "value replaced"));
		logger.info("get context second element: " + cxt.getValue("element[1]"));
		
		logger.info("get context size: " + cxt.size());
		
		// expend context by a collection
		List list = list("Irena", "Jarocka", "Warsaw");
		cxt.addAll(list);
		logger.info("list expended: " + cxt);
		
		ListContext context = listContext("Irena", "Jarocka", "Warsaw", 123);
		logger.info("list context literal: " + context);
	}
	
	public static void test5() throws ParException, RemoteException,
			EvaluationException, ConfigurationException, ContextException {
		// use variable lists and tables
		Var v = new Var("x", Var.Type.DESIGN);
		logger.info("v: " + v);

		ParModel varm = designModel(v, new Var("y1"), new Var("y2"), new Var("y3"));
		logger.info("variable model: " + varm);
		
		List vars = vars(Var.Type.DESIGN, "x", 12);
		logger.info("vars list: " + vars);
		
		List dVars = designVars("y", 12);
		logger.info("dVars list: " + dVars);

		List rVars = outputVars("z", 12);
		logger.info("rVars list: " + rVars);

		Table varTable = table(vars, dVars, rVars);
		logger.info("varTable: " + varTable);

		Context cxt = context(entry("AFRL", "Kolonay"), 
				entry("var", v), // variable
				entry("varm", varm), // variable model
				entry("vars", vars), // variable list
				entry("dVars", dVars), // typed variable list
				entry("rVars", rVars), // typed variable list
				entry("varTable", varTable)); // variable table
		logger.info("literal context: " + cxt);

	}
	
}
