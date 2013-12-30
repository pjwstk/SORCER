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

public class Input extends Component {

	public Input() {
		name = "Input";
		size = 10;
		maxlength = 10;
	}

	public Input(String name) {
		this.name = new String(name);
		size = 10;
		maxlength = 10;
	}

	public Input(String name, String value) {
		this.name = name;
		if (value != null)
			this.value = value;
		size = 10;
		maxlength = 10;
	}

	public Input(String name, String value, String onBlur) {
		this.name = name;
		if (value != null)
			this.value = value;
		size = 10;
		maxlength = 10;
		this.onBlur = onBlur;
	}

	public Input(String name, int size, int maxlength) {
		this.name = name;
		this.size = size;
		this.maxlength = maxlength;
	}

	public Input(String name, String value, int size, int maxlength) {
		this.name = name;
		if (value != null)
			this.value = value;
		this.size = size;
		this.maxlength = maxlength;
	}

	public void print(java.io.PrintWriter pw) {
		String type = getType();

		pw.print("<input name=\"" + name + "\" size=" + size + " maxlength="
				+ maxlength);

		if (value != null)
			pw.print(" value=\"" + value + "\"");

		if (type != null)
			pw.print(" type=" + type);
		if (onBlur != null)
			pw.println("onBlur=\"" + onBlur + "\"");

		pw.println(">");
	}

	/**
	 * Set the visible input field length
	 * 
	 * @param length
	 *            the new field length
	 */

	public void setLength(int length) {
		size = length;
	}

	/**
	 * Get the visible input field length
	 * 
	 * @return the length
	 */

	public int getLength() {
		return size;
	}

	public void setMaxLength(int length) {
		maxlength = length;
	}

	public int getMaxLength() {
		return maxlength;
	}

	public String getType() {
		return null;
	}

	public void setName(String n) {
		name = new String(n);
	}

	public String getName() {
		return name;
	}

	public void setValue(String s) {
		value = new String(s);
	}

	public String getValue() {
		return value;
	}

	public void setonBlur(String s) {
		onBlur = s;
	}

	private String name;
	private String value = null;
	private int size;
	private int maxlength;
	private String onBlur;
}
