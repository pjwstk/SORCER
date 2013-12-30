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

import java.net.URI;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class ServerUtil {

	public static Hashtable getParameters(URI uri) {
		String s = uri.getQuery();
		if (s == null)
			return null;
		s = s.concat("&");

		Hashtable ht = new Hashtable();
		StringTokenizer st = new StringTokenizer(s, "'&''='", false);

		while (st.hasMoreTokens())
			ht.put(st.nextToken(), st.nextToken());

		return ht;
	}

	public static URI getURI(String protocol, String authority, String path,
			Hashtable params) {
		StringBuffer query = new StringBuffer();
		if (query != null) {
			String key;
			for (Enumeration e = params.keys(); e.hasMoreElements();)
				query.append(key = (String) e.nextElement()).append("=")
						.append((String) params.get(key)).append("&");
		}

		if (query.length() > 0)
			query.deleteCharAt(query.length() - 1);

		try {
			return new URI(protocol, authority, path, query.toString(), null);
		} catch (Exception e) {
			return null;
		}
	}
}
