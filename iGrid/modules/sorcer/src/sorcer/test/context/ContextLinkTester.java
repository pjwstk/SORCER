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

import sorcer.core.context.ContextLink;
import sorcer.core.context.ServiceContext;
import sorcer.service.Context;
import sorcer.service.ContextException;

/**
 * Example how use the ContextLink class
 * 
 * @author Michael Alger
 */
public class ContextLinkTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ContextLinkTester.test1();
		ContextLinkTester.test2();
		ContextLinkTester.test3();
		ContextLinkTester.test4();
	}

	/**
	 * Example how to staticly attached a external data node (from another
	 * context) to the main context by explicitly creating ContextLink
	 */
	public static void test1() {
		Context mainContext = new ServiceContext("main");
		Context leafContext = new ServiceContext("leaf");
		ContextLink contextLink = null;

		try {
			// insert a test node
			leafContext.putValue("leaf/message/test",
					"Hello from the leafContext! (test1)");

			// insert the context, and the offset.
			// offset is the path of the data node in the leaf context which
			// you wish to attach to the main context
			contextLink = new ContextLink(leafContext, "leaf/message/test");

			// insert the contextLink to the main context on a designated path
			mainContext.putValue("in/context/1", contextLink);

			// retreive the data node in the leaf context automatically
			// using the offset (path) assigned
			System.out.println("Link Context node: "
					+ mainContext.getValue("in/context/1"));

		} catch (ContextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}// test1

	/**
	 * Example how to staticly attached a external data node (from another
	 * context) to the main context by using the putLink() method
	 */
	public static void test2() {
		Context mainContext = new ServiceContext("main");
		Context leafContext = new ServiceContext("leaf");

		try {
			// insert a test node
			leafContext.putValue("leaf/message/test",
					"Hello from the leafContext! (test2)");

			mainContext.putLink("in/context/2", leafContext,
					"leaf/message/test");

			System.out.println("Link Context node: "
					+ mainContext.getValue("in/context/2"));

		} catch (ContextException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Example how to use the ContextList in a dynamic fashion
	 */
	public static void test3() {
		Context mainContext = new ServiceContext("main");
		Context leafContext = new ServiceContext("leaf");
		ContextLink contextLink = null;

		try {
			// insert a test node
			leafContext.putValue("attachedNode/message/test",
					"Hello! from the leafContext! (test3)");
			leafContext.putValue("attachedNode/message/test1",
					"Hi! from the leafContext! (test3)");

			// insert the context, and the offset.
			// offset is the path of the data node in the leaf context which
			// you wish to attach to the main context
			contextLink = new ContextLink(leafContext, "attachedNode");

			// insert the contextLink to the main context on a designated path
			mainContext.putValue("in/context/1", contextLink);

			// retreive the data node in the leaf context automatically
			// using the offset (path) assigned
			System.out
					.println("Link Context node: "
							+ mainContext
									.getValue("in/context/1/attachedNode/message/test1"));
			
			mainContext.putValue("in/context/1/attachedNode/message/test2", "new node(test3)");
			
			System.out
			.println("Link Context node: "
					+ leafContext
							.getValue("attachedNode/message/test2"));

		} catch (ContextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Another example how to use the ContextList in a dynamic fashion using
	 * putLink() method
	 */
	public static void test4() {
		Context mainContext = new ServiceContext("main");
		Context leafContext = new ServiceContext("leaf");

		try {
			// insert a test node
			leafContext.putValue("leafContext/message/test",
					"Hello from the leafContext! (test4)");

			mainContext.putLink("in/context/2", leafContext, "leafContext");

			System.out.println("Link Context node: "
					+ mainContext
							.getValue("in/context/2/leafContext/message/test"));

		} catch (ContextException e) {
			e.printStackTrace();
		}
	}
}
