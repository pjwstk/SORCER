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

package sorcer.test.jep;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.nfunk.jep.JEP;

import sorcer.core.context.ServiceContext;
import sorcer.service.Context;
import sorcer.service.ContextException;

/**
 * 
 */
public class ContextCondition {

	String expression;

	JEP parser;

	Map<String, String> mapReference = new HashMap<String, String>();

	public ContextCondition() {
		parser = new JEP();
	}

	public void setVariable(String variableName, String contextPath) {
		mapReference.put(variableName, contextPath);
	}

	public void setBooleanExpression(String expression) {
		this.expression = expression;
	}

	public Object eval(Context context) {

		for (Iterator iter = mapReference.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			String varName = (String) entry.getKey();
			String path = (String) entry.getValue();
			Object value;
			try {
				value = context.getValue(path);

				System.out.println("key: " + varName + "\tpath: " + path
						+ "\tvalue: " + value);

				parser.addVariable(varName, value);
			} catch (ContextException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Evaluating expression: " + expression);
		parser.parseExpression(expression);

		return parser.getValue();
	}

	public boolean isTrue(Context context) throws RemoteException {
		Object result = null;

		result = this.eval(context);

		System.out.println("Result value: " + result);

		if (result.toString().equals("0.0") || result.toString().equals("1.0"))
			return result.toString().equals("1.0");
		else
			throw new RemoteException(
					"Boolean expression is not a valid condition.");
	}

	public static void main(String[] arg) {
		ContextCondition test = new ContextCondition();

		Context context = new ServiceContext();

		try {
			context.putValue("in/value/1", 10);

			context.putValue("in/value/2", 20);
			context.putValue("in/value/3", 30);
			context.putValue("in/value/4", 40);
			context.putValue("in/value/5", 50);
			context.putValue("in/value/6", 60);
		} catch (ContextException e) {
			e.printStackTrace();
		}
		
		test.setVariable("x", "in/value/1");
		test.setVariable("y", "in/value/2");
		test.setVariable("z", "in/value/3");
		test.setVariable("i", "in/value/4");
		test.setVariable("j", "in/value/5");
		test.setVariable("k", "in/value/6");

		test.setBooleanExpression("(x + y) <= k");

		try {
			System.out.println("isTrue(): " + test.isTrue(context));
		} catch (RemoteException re) {
			System.out.println("problem with condition");
			re.printStackTrace();
		}
	}

}
