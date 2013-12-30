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

public class Selector extends Container {

	public Selector() {
		name = "Selector";
		isMultiple = false;
	}

	public Selector(String name) {
		this.name = new String(name);
		isMultiple = false;
	}

	public Selector(String name, boolean multiple) {
		this.name = new String(name);
		isMultiple = multiple;
	}

	public Selector(String name, int size) {
		this.name = new String(name);
		this.size = size;
	}

	public Selector(String name, int size, boolean multiple) {
		this.name = new String(name);
		this.size = size;
		isMultiple = multiple;
	}

	public void add(String text) {
		add(new SelectorItem(text));
	}

	public void add(String text, boolean selected) {
		add(new SelectorItem(text, selected));
	}

	public void print(java.io.PrintWriter pw) {
		pw.print("<select name=\"" + name + "\"");
		if (size > 1)
			pw.print(" size=" + size);
		if (isMultiple)
			pw.print(" multiple");
		pw.println(">");

		super.print(pw);

		pw.println("</select>");
	}

	public boolean isMultiple() {
		return isMultiple;
	}

	public void setMultiple(boolean bit) {
		isMultiple = bit;
	}

	public String getName() {
		return new String(name);
	}

	public void setName(String name) {
		this.name = new String(name);
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	private int size = 0;
	private boolean isMultiple;
	private String name;
}
