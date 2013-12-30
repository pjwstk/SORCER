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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Exec {
	public static void main(String[] args) throws IOException {

		if (args.length == 0) {
			System.out.println("no program specified to be executed");
			System.exit(1);
		}

		System.out.println(doIt(args));
	}

	public static String doIt(Object command) throws IOException {
		DataInputStream is = null;
		try {
			Runtime rt = Runtime.getRuntime();
			Process p;
			if (command instanceof String)
				p = rt.exec((String) command);
			else
				p = rt.exec((String[]) command);

			String line;
			is = new DataInputStream(
					new BufferedInputStream(p.getInputStream()));

			StringBuffer sb = new StringBuffer();
			while ((line = is.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			return sb.toString();
		} catch (Exception e) {
			System.out.println("Error: Exec>>doIt" + e);
		} finally {
			if (is != null)
				is.close();
		}
		return null;
	}
}
