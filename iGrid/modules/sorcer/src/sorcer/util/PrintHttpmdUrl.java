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

import java.io.File;
import java.util.Scanner;

import net.jini.url.httpmd.HttpmdUtil;

public class PrintHttpmdUrl {

	public static void main(String[] args) {

		// get codbase from the user
		Scanner in = new Scanner(System.in);
		System.out.println("Enter codebase: ");
		String codebase = in.nextLine();

		if (codebase.length() == 0) {
			System.out.println("no codebase provided");
			return;
		}

		String dir = System.getProperty("iGrid.home") + File.separator + "lib";
		System.out.println("codebase=" + codebase);
		int i = codebase.indexOf(":");
		codebase = "httpmd" + codebase.substring(i);

		System.out.println("jar dir=" + dir);
		try {
			System.out.println(HttpmdUtil.computeDigestCodebase(dir, codebase
					+ ";md5=0"));

		} catch (Exception e) {
			System.out.println(codebase);
		}
	}
}
