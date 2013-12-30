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

import java.net.URLEncoder;
import java.util.Hashtable;

/**
 * Utilities for HTTP aware applications.
 */
public class W3Utils {
	/**
	 * Returns an encoding of its String argument which is suitable for
	 * inclusion in an HTTP request string, ie blanks are turned into '+' and
	 * non-alphanumeric characters are encoded in the form "%XX" where 'X' is a
	 * hex character. More precisely <em>reserved</em> and <em>unsafe</em>
	 * characters are encoded. (See the HTTP spec.)
	 */
	public static String urlencode(String str) {
		return URLEncoder.encode(str);
	}

	/**
	 * Returns true if the argument qualifies as an HTTP "token", i.e. if it
	 * contains no control characters or tspecials.
	 */
	public static boolean isToken(String s) {
		int len, ch;

		if (s == null || (len = s.length()) == 0)
			return false;

		for (int i = 0; i < len; i++) {
			ch = (int) s.charAt(i);
			if (isTSpecial(ch) || isControl(ch))
				return false;
		}
		return true;
	}

	/**
	 * Returns true if the specified character is an HTTP "tspecial" character.
	 */
	public static boolean isTSpecial(int ch) {
		switch (ch) {
		case '(':
		case ')':
		case '<':
		case '>':
		case '@':
		case ',':
		case ';':
		case ':':
		case '\\':
		case '"':
		case '/':
		case '[':
		case ']':
		case '?':
		case '=':
		case '{':
		case '}':
		case ' ':
		case '\t':
			return true;
		default:
			return false;
		}
	}

	/**
	 * Returns true if the specified character is an HTTP "reserved" character.
	 */
	public static boolean isReserved(int ch) {
		switch (ch) {
		case ';':
		case '/':
		case '?':
		case ':':
		case '@':
		case '&':
		case '=':
		case '+':
			return true;
		default:
			return false;
		}
	}

	/**
	 * Returns true if the specified character is an HTTP "unsafe" character.
	 */
	public static boolean isUnsafe(int ch) {
		switch (ch) {
		case ' ':
		case '"':
		case '#':
		case '%':
		case '<':
		case '>':
			return true;
		default:
			return isControl(ch);
		}
	}

	/**
	 * Returns true if the specified character is a US-ASCII control character,
	 * i.e. if it's ascii value is less than or equal to 31 or equal to 127
	 * (DEL).
	 */
	public static boolean isControl(int ch) {
		return (ch <= 31 || ch == 127);
	}

	/**
	 * Returns a String which equals the argument except that occurrences of '"'
	 * are expanded into its quoted-printable escaped equivalent sequence, '\"'.
	 * If the argument contains no double-quotes the original String is
	 * returned.
	 */
	protected static String quoted(String s) {
		if (s == null || s.indexOf('"') < 0)
			return s;

		StringBuffer sb = new StringBuffer(s.length() + 5);
		int i, start = 0;
		while ((i = s.indexOf('"', start)) > -1) {
			sb.append(s.substring(start, i));
			sb.append("\\\"");
			start = i + 1;
		}
		sb.append(s.substring(start));
		return sb.toString();
	}

	/**
	 * The opposite function of urlencode().
	 */
	public static String urldecode(String str) {
		int len = str.length();
		StringBuffer newstr = new StringBuffer(len);

		for (int i = 0; i < len; i++) {
			if (str.charAt(i) == '+') {
				newstr.append(' ');
			} else if (str.charAt(i) == '%') {
				newstr.append(dd2c(str.charAt(i + 1), str.charAt(i + 2)));
				i += 2;
			} else {
				newstr.append(str.charAt(i));
			}
		}
		return newstr.toString();
	}

	/**
	 * Encode String to make it suitable as a Cookie value.
	 * 
	 * @param s
	 *            the string to be encoded
	 * @return s with occurrences of semi-colon, comma, and white-space
	 *         characters replaced by their %XX URL encoding equivalents.
	 */
	public static String encodeCookie(String s) {
		if (s == null)
			return null;
		int i, l = s.length();
		StringBuffer sb = new StringBuffer();

		for (i = 0; i < l; i++) {
			switch (s.charAt(i)) {
			case ';':
				sb.append("%3B");
				continue;
			case ',':
				sb.append("%2C");
				continue;
			case ' ':
				sb.append("%20");
				continue;
			case '\t':
				sb.append("%09");
				continue;
			case '\n':
				sb.append("%0A");
				continue;
			case '\r':
				sb.append("%0D");
				continue;
			case '=':
				sb.append("%3D");
				continue;
			case '%':
				sb.append("%25");
				continue;
			}
			sb.append(s.charAt(i));
		}
		return sb.toString();
	}

	public static String decodeCookie(String s) {
		return W3Utils.urldecode(s);
	}

	/**
	 * Transform a char such as '&' to URL encoded form such as %25.
	 */
	public static String c2dd(char ch) {
		String s = Integer.toString(ch, 16);
		if (s.length() > 1)
			return '%' + s;
		else
			return "%0" + s;
	}

	/**
	 * Transform two hex digits to corresponding char.
	 */
	public static char dd2c(char d1, char d2) {
		return (char) (Character.digit(d1, 16) * 16 + Character.digit(d2, 16));
	}

	/**
	 * Parses a string of the form "a=b&c=d&e=f" ie a '&' separated list of
	 * name-value pairs. Each element in the list is assumed to be urlencoded
	 * and this method will decode it using urldecode().
	 * 
	 * @param query
	 *            urlencoded string of form "a=b&c=d&e=f"
	 * @param h
	 *            hashtable to put name-value pairs into
	 * @return h
	 */
	public static Hashtable parseQueryString(String query, Hashtable h) {
		return decodeNmValPairs(query, '=', '&', null, h);
	}

	/**
	 * Parses a string of the form "a=b; c=d; e=f" ie a ';' separated list of
	 * name-value pairs (with allowed white-spaces after ';').
	 * 
	 * @param query
	 *            string of form "a=b; c=d; e=f"
	 * @param h
	 *            hashtable to put name-value pairs into
	 * @return h
	 */
	public static Hashtable parseCookieString(String query, Hashtable h) {
		return decodeNmValPairs(query, '=', ';', " \t", h);
	}

	/**
	 * Parses a string of the form "a=b c=d e=f" ie a white-space separated list
	 * of name-value pairs.
	 * 
	 * @param query
	 *            string of form "a=b c=d e=f"
	 * @param h
	 *            hashtable to put name-value pairs into
	 * @return h
	 */
	public static Hashtable parseNameValuePairs(String query, Hashtable h) {
		return parseNmValPairs(query, '=', ' ', " \t", h);
	}

	/**
	 * Parses a string of the form "aXbYcXdYeXf" of name-value pairs where X is
	 * specified by <i>sep1</i>, Y is specified by <i>sep2</i> and additional
	 * white-space caracters after Y are specified in the string <i>ws</i>.
	 * <p>
	 * 
	 * Both key and value are URL decoded.
	 */
	public static Hashtable decodeNmValPairs(String query, int sep1, int sep2,
			String ws, Hashtable h) {
		try {
			String name, val;
			int i1 = 0, i2 = 0;
			int l = query.length();

			while (i2 < l) {
				i1 = query.indexOf(sep1, i2);
				if (i1 == -1)
					return h;
				name = urldecode(query.substring(i2, i1));
				i1++;

				i2 = query.indexOf(sep2, i1);
				if (i2 == -1) {
					val = urldecode(query.substring(i1));
					i2 = l;
				} else {
					val = urldecode(query.substring(i1, i2));
				}

				i2++;
				// go past whitespaces:
				while (ws != null && i2 < l
						&& ws.indexOf(query.charAt(i2)) > -1)
					i2++;
				// System.out.println(name + " = " + val);
				h.put(name, val);
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			// continue and return what we did parse...
		}

		return h;
	}

	/**
	 * Parses a string of the form "aXbYcXdYeXf" of name-value pairs where X is
	 * specified by <i>sep1</i>, Y is specified by <i>sep2</i> and additional
	 * white-space caracters after Y are specified in the string <i>ws</i>.
	 * <p>
	 * 
	 * If both the first and the last character of a value is '"' they are
	 * stripped off.
	 */
	public static Hashtable parseNmValPairs(String query, int sep1, int sep2,
			String ws, Hashtable h) {
		try {
			String name, val;
			int i1 = 0, i2 = 0;
			int l = query.length();

			while (i2 < l) {
				i1 = query.indexOf(sep1, i2);
				if (i1 == -1)
					return h;
				name = query.substring(i2, i1);
				i1++;

				if (query.charAt(i1) == '"') {
					i2 = query.indexOf('"', i1 + 1);
					val = query.substring(i1 + 1, i2);
					i2++;
				} else {
					i2 = query.indexOf(sep2, i1);

					if (i2 == -1) {
						val = query.substring(i1);
						i2 = l;
					} else {
						val = query.substring(i1, i2);
					}
				}
				i2++;
				// go past whitespaces:
				while (ws != null && i2 < l
						&& ws.indexOf(query.charAt(i2)) > -1)
					i2++;
				// System.out.println(name + " = " + val);
				h.put(name, val);
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			// continue and return what we did parse...
		}

		return h;
	}

	// /*
	public static void main(String args[]) {
		// String s = "aa=bb cc=\"dd xx yy\" ee=ff";
		String s = "aa=bb cc=dd+%21%3d+XX >ee=ff";
		System.out.println("decode " + s + " ==> "
				+ decodeNmValPairs(s, '=', ' ', " \t", new Hashtable()));
		System.out.println("parse " + s + " ==> "
				+ parseNmValPairs(s, '=', ' ', " \t", new Hashtable()));
	}
	// */
	/*
	 * // test these routines: public static void main(String args[]) { String
	 * raw = "this is a % raw = string!";
	 * 
	 * System.out.println("RAW string                 : " + raw);
	 * System.out.println("encoded RAW string         : " + urlencode(raw));
	 * System.out.println("decoded encoded RAW string : " +
	 * urldecode(urlencode(raw)));
	 * 
	 * Hashtable h = new Hashtable();
	 * parseQueryString("query=SELECT+*%0D%0AFROM+tbl&foo=bar", h);
	 * System.out.println(h); parseQueryString("aa=bb&cc=dd&ee=ff", h);
	 * parseQueryString("%21a=%3dkri%26&nam=%25val", h);parseQueryString(
	 * "hostname=vecak&portno=5155&class=datarep&category=2html&prefix=.system.uids"
	 * , h); }
	 */
}
