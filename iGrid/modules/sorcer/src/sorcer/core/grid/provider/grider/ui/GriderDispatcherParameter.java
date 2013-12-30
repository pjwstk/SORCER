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

package sorcer.core.grid.provider.grider.ui;

import java.util.Vector;

public class GriderDispatcherParameter {
	public GriderDispatcherParameter() {
		in = new Vector();
		out = new Vector();
		arg = new Vector();
	}

	public void addIn(String t) {
		in.add(t);
	}

	public void addOut(String t) {
		out.add(t);
	}

	public void addArg(String t) {
		arg.add(t);
	}

	public void clear() {
		in.clear();
		out.clear();
		arg.clear();
	}

	public Vector getIn() {
		return in;
	}

	public Vector getOut() {
		return out;
	}

	public Vector getArg() {
		return arg;
	}

	public void setEqual(GriderDispatcherParameter p) {
		setIn(p.in);
		setOut(p.out);
		setArg(p.arg);
	}

	public void removeIn(int i) {
		in.remove(i);
	}

	public void removeOut(int i) {
		out.remove(i);
	}

	public void removeArg(int i) {
		arg.remove(i);
	}

	public void setIn(Vector t) {
		in = t;
	}

	public void setOut(Vector t) {
		out = t;
	}

	public void setArg(Vector t) {
		arg = t;
	}

	public String view() {
		StringBuilder v = new StringBuilder();
		v.append("Input: \n");
		for (int i = 0; i < in.size(); ++i)
			v.append("   " + in.elementAt(i) + "\n");
		v.append("Arguments: \n");
		for (int i = 0; i < arg.size(); ++i)
			v.append("   " + arg.elementAt(i) + "\n");
		v.append("Output: \n");
		for (int i = 0; i < out.size(); ++i)
			v.append("   " + out.elementAt(i) + "\n");
		return v.toString();
	}

	private Vector in;
	private Vector out;
	private Vector arg;
}