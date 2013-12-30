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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

import sorcer.util.ByteArray;

/**
 * Class providing simple textual substitution of "variable" elements of a text
 * file. Instances of this class represents text files. The file is loaded when
 * the class is instantiated and held in memory along with this object.
 * Subsequent invocations of <a href="#print">print()</a> will evaluate the file
 * content in the context of the set of name-value pairs defined in the
 * specified Dictionary. The result is the file content with certain parts
 * textually substituted by the results of invoking toString() on values of the
 * Dictionary.
 * 
 * <p>
 * Example text in HTML file:
 * 
 * <pre>
 * &lt;title&gt;
 *   <b>&lt;template key</b>="date"<b>&gt;</b>
 *     <b>&lt;def&gt;</b>Message of the day (<b>%s</b>)
 *     <b>&lt;alt&gt;</b>No message today
 *   <b>&lt;/template&gt;</b>
 * &lt;/title&gt;
 * </pre>
 * 
 * The text between <b>template</b> tags will never appear verbatim on the wire.
 * Instead the print() method will substitute the string representation of the
 * object corresponding to <b>key</b> into the place(s) of the string "%s" in
 * the <b>def</b> string and send this. If no such object exists, the <b>alt</b>
 * part will be send instead.
 * <p>
 * Both the <b>def</b> and <b>alt</b> parts are optional. The corresponding
 * defaults are "%s" and the empty string respectively. The <b>key</b> parameter
 * is mandatory.
 * <p>
 * 
 * A service generating content dynamically can now load the HTML template from
 * a file and just fill in the blanks...
 * 
 * <pre>
 * HtmlTemplate template = new HtmlTemplate(&quot;template.html&quot;);
 * Hashtable args = new Hashtable();
 * args.put(&quot;date&quot;, new Date());
 * template.print(System.out, args);
 * </pre>
 * 
 * <p>
 * Class HtmlTemplate recognizes the commonly used special case where the
 * <b>def</b> part is "%s" and the <b>alt</b> part is the empty string. In this
 * case the template declaration can be specified as a one-element tag:
 * 
 * <pre>
 *   <b>&lt;subst key</b>=name<b>&gt;</b>
 * </pre>
 * 
 * <b>NB:</b> <b>template</b> tags cannot be nested.
 * 
 * @author Anders Kristensen
 */
public class HtmlTemplate {

	byte[] buf; // contents of file
	int len; // length of file
	byte[] varStartPtrn; // byte array for "<template "
	byte[] substStartPtrn; // byte array for "<subst "
	byte[] varEndPtrn; // byte array for "</template>"
	byte[] defPtrn; // byte array for "<def>"
	byte[] altPtrn; // byte array for "<alt>"
	byte[] varPtrn; // byte array for "%s"
	static final int subst_len = 7; // length of substStartPtrn
	static final int vsp_len = 10; // length of varStartPtrn
	static final int vep_len = 11; // length of varEndPtrn
	static final int dp_len = 5; // length of defPtrn
	static final int ap_len = 5; // length of altPtrn
	static final int vp_len = 2; // length of varPtrn
	Vector vars; // ordered list of SubstRec's

	public HtmlTemplate(String filename) throws IOException {
		buf = ByteArray.loadFromFile(filename);
		init();
	}

	public HtmlTemplate(File file) throws IOException {
		buf = ByteArray.loadFromFile(file);
		init();
	}

	private void init() {
		len = buf.length;
		vars = new Vector();
		substStartPtrn = ByteArray.getBytes("<subst ");
		varStartPtrn = ByteArray.getBytes("<template ");
		varEndPtrn = ByteArray.getBytes("</template>");
		defPtrn = ByteArray.getBytes("<def>");
		altPtrn = ByteArray.getBytes("<alt>");
		varPtrn = ByteArray.getBytes("%s");

		parseHtml();
	}

	/** Finds the variable parts of the file content. */
	void parseHtml() {
		int i, i1, i2;
		SubstRec rec;

		for (i = 0; i < len;) {
			i1 = ByteArray.findBytes(buf, i, len - i, varStartPtrn);
			i2 = ByteArray.findBytes(buf, i, len - i, substStartPtrn);
			if (i1 > -1 && (i1 < i2 || i2 == -1))
				rec = newTemplateRec(buf, i1);
			else if (i2 > -1)
				rec = newSubstRec(buf, i2);
			else
				return;

			if (rec == null)
				return;

			// System.out.println(" : " + rec);
			vars.addElement(rec);
			i = rec.off + rec.len;
		}
	}

	// Returns index of ending '>'.
	// Note that a '>' within double quoted string doesn't count
	public static int findGT(byte[] buf, int off) {
		try {
			int i;
			for (i = off; buf[i] != '>'; i++) {
				if (buf[i] == '"') // go past next '"'
					while (buf[++i] != '"')
						; // TODO: check for escaped '"'
			}
			return i;
		} catch (Exception e) {
			return -1;
		}
	}

	public SubstRec newTemplateRec(byte[] buf, int i_ssp) {
		int i_sep, i_dp, i_ap, i_vp; // buf indices for *Ptrn byte arrays
		int i = 0;
		int l = len;
		Hashtable args;
		String key, def, alt;
		SubstRec rec;

		try {
			i_sep = ByteArray.findBytes(buf, i_ssp + vsp_len, len - i_ssp
					- vsp_len, varEndPtrn);
			i = findGT(buf, i_ssp + vsp_len);
			// System.out.println(">: " + i);
			args = W3Utils.parseNameValuePairs(new String(buf, 0, i_ssp
					+ vsp_len, i - i_ssp - vsp_len), new Hashtable());
			// System.out.println("Args: " + args);
			key = (String) args.get("key");
			i_dp = ByteArray.findBytes(buf, i, i_sep - i, defPtrn);
			i_ap = ByteArray.findBytes(buf, i, i_sep - i, altPtrn);
			if (i_dp > -1) {
				if (i_ap > -1) {
					def = new String(buf, 0, i_dp + dp_len, i_ap - i_dp
							- dp_len);
					alt = new String(buf, 0, i_ap + ap_len, i_sep - i_ap
							- ap_len);
				} else {
					def = new String(buf, 0, i_dp + dp_len, i_sep - i_dp
							- dp_len);
					alt = "";
				}
			} else {
				if (i_ap > -1) {
					def = "%s";
					alt = new String(buf, 0, i_ap + ap_len, i_sep - i_ap
							- ap_len);
				} else {
					def = "%s";
					alt = "";
				}
			}
			def = def.trim();
			alt = alt.trim();
			return new SubstRec(i_ssp, i_sep + vep_len - i_ssp, key, def, alt);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return null;
		}
	}

	public SubstRec newSubstRec(byte[] buf, int start) {
		Hashtable args;
		String key, def, alt;
		int end;

		try {
			end = findGT(buf, start + subst_len);
			args = W3Utils.parseNameValuePairs(new String(buf, 0, start
					+ subst_len, end - start - subst_len), new Hashtable());
			key = (String) args.get("key");
			if ((def = (String) args.get("def")) == null)
				def = "%s";
			if ((alt = (String) args.get("alt")) == null)
				alt = "";
			return new SubstRec(start, end - start + 1, key, def, alt);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return null;
		}
	}

	/** Write result of "reifying" this object with dict to the stream. */
	public void print(OutputStream out, Dictionary dict) throws IOException {
		int off = 0;
		int l = len;
		SubstRec rec;
		Object o;

		for (int j = 0;; j++) {
			if (j < vars.size()) {
				rec = (SubstRec) vars.elementAt(j);
				out.write(buf, off, rec.off - off);
				try {
					o = dict.get(rec.key);
				} catch (Exception e) {
					o = null;
				}
				if (o == null) {
					out.write(rec.alt, 0, rec.alt.length);
				} else {
					byte[] b = ByteArray.getBytes(o.toString());
					int i_vp, off2 = 0, l2 = rec.def.length;

					while ((i_vp = ByteArray.findBytes(rec.def, off2, l2,
							varPtrn)) > -1) {
						out.write(rec.def, off2, i_vp - off2);
						out.write(b, 0, b.length);
						off2 = i_vp + vp_len;
						l2 = rec.def.length - off2;
					}
					out.write(rec.def, off2, l2);
				}
				off = rec.off + rec.len;
			} else {
				out.write(buf, off, len - off);
				return;
			}
		}
	}

	public byte[] getContents() {
		return buf;
	}

	/** for debugging this class only */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("HtmlTemplate: ");
		for (int i = 0; i < vars.size(); i++)
			sb.append("  " + i + ": " + vars.elementAt(i));
		return sb.toString();
	}

	/*
	 * // for testing... public static void main(String[] args) throws
	 * IOException { HtmlTemplate varfile = new HtmlTemplate(args[0]); Hashtable
	 * h = new Hashtable(); h.put("title", "THIS IS MY TITLE"); h.put("cal",
	 * varfile.vars.elementAt(0)); h.put("date", "Thu 12 Sep");
	 * h.put("crt3-07:15", "Bruce Becker, 76345"); h.put("crt4-07:15",
	 * "Anders kristensen, 28164"); h.put("crt4-08:00",
	 * "Mathias Willerup, 28164"); System.out.println("Resolved file:");
	 * varfile.print(System.out, h); }
	 */
}

class SubstRec {

	int off;
	int len;
	String key;
	byte[] def;
	byte[] alt;

	public SubstRec(int off, int len, String key, String def, String alt) {
		this.off = off;
		this.len = len;
		this.key = key;
		this.def = ByteArray.getBytes(def);
		this.alt = ByteArray.getBytes(alt);
	}

	public String toString() {
		return "SubstRec(off=" + off + ", len=" + len + ", key=" + key
				+ ", def=" + new String(def, 0) + ", alt=" + new String(alt, 0)
				+ ")";
	}
}
