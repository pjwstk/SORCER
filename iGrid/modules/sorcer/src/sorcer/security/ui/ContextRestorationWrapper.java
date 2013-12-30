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

package sorcer.security.ui;

import java.awt.event.ActionListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import net.jini.security.Security;
import net.jini.security.SecurityContext;

public class ContextRestorationWrapper {
	// Utilty calss, don't allow creation of instances
	private ContextRestorationWrapper() {
	}

	public static ActionListener wrap(ActionListener l) {
		return (ActionListener) wrap(l, ActionListener.class);
	}

	private static Object wrap(Object obj, Class iface) {
		return Proxy.newProxyInstance(obj.getClass().getClassLoader(),
				new Class[] { iface }, new Handler(obj));
	}

	private static class Handler implements InvocationHandler {
		private final Object obj;

		private final SecurityContext ctx;

		private final ClassLoader ccl;

		Handler(Object obj) {
			this.obj = obj;
			ctx = Security.getContext();
			ccl = Thread.currentThread().getContextClassLoader();
		}

		@SuppressWarnings("unchecked")
		public Object invoke(Object proxy, final Method method,
				final Object[] args) throws Throwable {
			if (method.getDeclaringClass() == Object.class) {
				if ("equals".equals(method.getName()))
					return Boolean.valueOf(proxy == args[0]);
				else if ("hashCode".equals(method.getName()))
					return new Integer(System.identityHashCode(proxy));
			}

			try {
				PrivilegedExceptionAction pea = ctx
						.wrap(new PrivilegedExceptionAction() {
							public Object run() throws Exception {
								final Thread t = Thread.currentThread();
								final ClassLoader occl = t
										.getContextClassLoader();
								try {
									t.setContextClassLoader(ccl);
									try {
										return method.invoke(obj, args);
									} catch (final InvocationTargetException e) {
										final Throwable tt = e.getCause();
										if (tt instanceof Error)
											throw (Error) tt;
										throw (Exception) tt;
									}
								} finally {
									t.setContextClassLoader(occl);
								}
							}
						});
				return AccessController.doPrivileged(pea, ctx
						.getAccessControlContext());
			} catch (PrivilegedActionException e) {
				throw e.getCause();
			}
		}
	}
}
