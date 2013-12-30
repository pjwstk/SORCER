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

public class Refresh extends Component {

	public Refresh(String url) {
		delay = 0;
		this.url = url;
	}

	public Refresh(String url, int delay) {
		this.url = url;
		this.delay = delay;
	}

	public void print(java.io.PrintWriter pw) {
		pw.println("<meta http-equiv=\"Refresh\" content=\"" + delay + ";url="
				+ url + "\">");
	}

	private String url;
	private int delay = 0;
}
