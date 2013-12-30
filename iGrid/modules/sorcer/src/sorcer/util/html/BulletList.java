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

public class BulletList extends Container {
	public static final int SQUARE = 1;
	public static final int CIRCLE = 2;
	public static final int DISC = 3;

	public BulletList() {
	}

	public BulletList(Component c) {
		add(c);
	}

	public void print(java.io.PrintWriter pw) {
		pw.print("<ul");
		if (type > 0) {
			pw.print(" type=");
			switch (type) {
			case SQUARE:
				pw.print("square");
				break;
			case CIRCLE:
				pw.print("circle");
				break;
			case DISC:
				pw.print("disc");
				break;
			}
		}
		pw.println(">");

		super.print(pw);
		pw.println("</ul>");
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	private int type = 0;
}
