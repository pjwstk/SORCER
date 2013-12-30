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

public abstract class Button extends Component {
	public Button() {
		value = new Text("Button");
		name = null;
	}

	public Button(String value) {
		this.value = new Text(value);
		name = null;
	}

	public Button(String value, String name) {
		this(value, name, null);
	}

	public Button(String value, String name, String onclick) {
		this.value = new Text(value);
		this.name = name;
		this.onclick = onclick;
	}

	public void print(java.io.PrintWriter pw) {
		pw.print("<input type=" + getType());
		if (name != null)
			pw.print(" name=\"" + name + "\"");

		if (onclick != null)
			pw.print(" onclick=\"" + onclick + "\"");

		if (this instanceof ImageButton) {
			// value holds the image src
			pw.print(" src=\"");
			value.print(pw);
			pw.print("\"");
		} else if (value != null) {
			pw.print(" value=\"");
			value.print(pw);
			pw.print("\"");
		}

		if (getClass() == Checkbox.class) {
			Checkbox chb = (Checkbox) this;
			if (chb.isChecked())
				pw.print(" checked");
		} else if (getClass() == RadioButton.class) {
			RadioButton rb = (RadioButton) this;
			if (rb.isChecked())
				pw.print(" checked");
		}
		pw.println(">");
	}

	public String getValue() {
		return value.getText();
	}

	public void setValue(String value) {
		this.value = new Text(value);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOnclick(String method) {
		onclick = method;
	}

	public abstract String getType();

	private Text value;
	private String name;
	private String onclick;
}
