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

package sorcer.util;

/**
 * The CallbackModel interface must be implemented by classes that do not use
 * Model but want to callback. The reason for this is the JDK 1.0.2
 * implementation of of the Dialog class that results in a non-blocking behavior
 * of a modal Dialog (see http://java.sun.com/products/JDK/1.0.2/AWTbugs.html
 * for more details). CallbackModal allows the dialog call a method of the
 * object that created and displayed the dialog when the dialog is disposed.
 */

public interface CallbackModel {
	/**
	 * Called by the Dialog when it is disposed
	 * 
	 * @param from
	 *            the calling dialog
	 * @param what
	 *            the result of the user interaction
	 */

	public void changed(Object aspect, Object arg);
}
