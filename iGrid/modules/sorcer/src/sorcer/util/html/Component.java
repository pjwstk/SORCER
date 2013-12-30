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

package sorcer.util.html;

public abstract class Component {
	/**
	 * Queries whether a component is a container or not
	 * 
	 * @return true if a component is a container
	 * @see Container
	 */
	public boolean isContainer() {
		return false;
	}

	/**
	 * Print this component on a java.io.PrintWriter This method must be
	 * overridden by every component
	 * 
	 * @param pw
	 *            the java.io.PrintWriter object to which output will go
	 */
	abstract public void print(java.io.PrintWriter pw);

}
