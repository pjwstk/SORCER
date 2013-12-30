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

package sorcer.security.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class HTTPSClient {

	public static void main(String[] args) throws Exception {
		System.setProperty("java.protocol.handler.pkgs",
				"com.sun.net.ssl.internal.www.protocol");

		// Here's the default URL
		String urlString = "https://www.verisign.com/";

		// If an argument has been passed, use it
		// as the url to attach to.
		if (args.length > 0) {
			urlString = args[0];
		}

		URL url = new URL(urlString);
		BufferedReader in = new BufferedReader(new InputStreamReader(url
				.openStream()));

		String line;
		while ((line = in.readLine()) != null) {
			System.out.println(line);
		}
		in.close();
	}
}
