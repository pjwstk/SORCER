/*
 * Created on Feb 2, 2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.sadun.text.ffp.FlatFileParser.Condition;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class creates {@link org.sadun.text.ffp.FlatFileParser}objects by
 * loading conditions and line format definitions from properly formatted
 * stream, URL or file.
 * 
 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano
 *         Sadun</a>
 * @version 1.0
 */
public class FFPFactory {

	/**
	 * This interface allows users to define their own syntax for
	 * {@link FlatFileParser.Condition conditions}and
	 * {@link LineFormat line format}specifications.
	 * 
	 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">
	 *         Cristiano Sadun</a>
	 * @version 1.0
	 */
	public static interface FFPStreamParser {
		/**
		 * Interpret the stream and extract conditions and associated line
		 * formats, storing them. Return an array of parsed conditions.
		 * 
		 * @param is
		 *            the stream from which to read.
		 * @return an array of parsed
		 *         {@link FlatFileParser.Condition conditions}.
		 * @exception ParseException
		 *                if an error occurs during parsing
		 */
		public FlatFileParser.Condition[] parseStream(InputStream is)
			throws IOException, FFPStreamParseException;

		/**
		 * Given a condition, return the associated line format.
		 * 
		 * @param condition
		 *            the key {@link FlatFileParser.Condition}
		 * @return the associated {@link LineFormat}.
		 */
		public LineFormat getLineFormat(FlatFileParser.Condition condition);

		/**
		 * Reset the parser so that a new parsing can occur.
		 */
		public void reset();
	}

	private static class XMLFFPStreamParser
		extends DefaultHandler
		implements FFPStreamParser {

		private Map cMap;
		private SAXParserFactory spf;
		private SAXParser saxParser;

		public XMLFFPStreamParser() {
			reset();
			spf = SAXParserFactory.newInstance();
			spf.setValidating(false);
			spf.setNamespaceAware(true);
			try {
				saxParser = spf.newSAXParser();
			} catch (ParserConfigurationException e) {
				throw new RuntimeException(
					"Cannot initialize default FFP Factory parser",
					e);
			} catch (SAXException e) {
				throw new RuntimeException(
					"Cannot initialize default FFP Factory parser",
					e);
			}
		}

		public FlatFileParser.Condition[] parseStream(InputStream is)
			throws IOException, FFPStreamParseException {
			BufferedInputStream bis;
			if (is instanceof BufferedInputStream)
				bis = (BufferedInputStream) is;
			else
				bis = new BufferedInputStream(is);

			try {
				saxParser.parse(bis, this);
				FlatFileParser.Condition[] result =
					new FlatFileParser.Condition[cMap.keySet().size()];
				cMap.keySet().toArray(result);
				return result;
			} catch (SAXException e) {
				FFPStreamParseException ex;
				if ((ex=locateFFPStreamParseException(e))!=null) 
					throw ex;
				else
					throw new FFPStreamParseException("Could not parse", e);
			}
		}

		/**
		 * @param e
		 * @return
		 */
		private FFPStreamParseException locateFFPStreamParseException(SAXException e) {
			Throwable t = e;
			while (t!=null) {
				if (t instanceof FFPStreamParseException) return (FFPStreamParseException)t;
				if (t instanceof SAXException) t=((SAXException)t).getException();
				else t=t.getCause();
			}
			return null;
		}

		public LineFormat getLineFormat(Condition condition) {
			LineFormat format = (LineFormat) cMap.get(condition);
			if (format == null)
				throw new IllegalArgumentException(
					"No line format associated to " + condition);
			return format;
		}

		public void reset() {
			cMap = new HashMap();
		}

		private StringBuffer buf = new StringBuffer();
		private Attributes currAttributes;
		private String currElement;
		private String defaultPackage = FFPFactory.class.getPackage().getName();

		private Map lineFormatsByName = new HashMap();

		public void startElement(
			String uri,
			String localName,
			String qName,
			Attributes attributes)
			throws SAXException {
			if ("line-format".equals(localName)
				|| "associate".equals(localName)
				|| "definitions".equals(localName)) {
				currElement = localName;
				currAttributes = attributes;
			} else
				throw new SAXException("Invalid element: " + qName);
		}

		public void characters(char[] ch, int start, int length) {
			buf.append(ch, start, length);
		}

		private static final String FFP_NAMESPACE = "http://www.sadun.org/ffp";

		public void endElement(String uri, String localName, String qName)
			throws SAXException {
			if (!localName.equals(currElement))
				throw new SAXException(
					new FFPStreamParseException("Invalid state: nested element within currElement"));
			if ("line-format".equals(localName)) {

				for (int i = 0; i < currAttributes.getLength(); i++)
					System.out.println(
						currAttributes.getURI(i)
							+ " "
							+ currAttributes.getLocalName(i));

				String name = currAttributes.getValue(FFP_NAMESPACE, "name");
				if (name == null)
					throw new SAXException(
						new FFPStreamParseException("Missing mandatory 'name' attribute in ffp:line-format element"));
				if (lineFormatsByName.containsKey(name))
					throw new SAXException(
						new FFPStreamParseException(
							"Duplicate line format name '" + name + "'"));
				LineFormat format = new LineFormat(name);
				try {
					format.declareLineImage(buf.toString());
				} catch (ImageParseException e) {
					throw new SAXException(
						new FFPStreamParseException(
							"Invalid line format image",
							e));
				}
				lineFormatsByName.put(name, format);

			} else if ("associate".equals(localName)) {
				String to = currAttributes.getValue(FFP_NAMESPACE, "to");
				if (to == null)
					throw new SAXException(
						new FFPStreamParseException("Missing mandatory 'to' attribute in ffp:associate element"));
				String packageName =
					currAttributes.getValue(FFP_NAMESPACE, "package");
				if (packageName == null)
					packageName = "default";
				String condition =
					currAttributes.getValue(FFP_NAMESPACE, "condition");
				if (condition == null)
					throw new SAXException(
						new FFPStreamParseException("Missing mandatory 'condition' attribute in ffp:associate element"));
				// TODO Build an instance of condition and associate it to the
				// line format in cMap
			}
			buf.delete(0, buf.length());
		}
	}

	private FFPStreamParser parser;

	public FFPFactory() {
		this(new XMLFFPStreamParser());
	}

	public FFPFactory(FFPStreamParser parser) {
		assert parser != null;
		this.parser = parser;
	}

	/**
	 * Create a single {@link FlatFileParser}reading the given stream.
	 * 
	 * @param is
	 *            the input stream containing the condition/format definitions
	 * @return a {@link FlatFileParser}initialized accordingly.
	 * @throws ParseException
	 *             if a problem occurs parsing the stream
	 * @throws IOException
	 *             if a problem occurs reading or closing the stream.
	 */
	public FlatFileParser createFFP(InputStream is)
		throws FFPStreamParseException, IOException {
		synchronized (parser) {
			parser.reset();
			FlatFileParser ffp = new FlatFileParser();
			FlatFileParser.Condition[] conds = parser.parseStream(is);
			for (int i = 0; i < conds.length; i++) {
				ffp.declare(conds[i], parser.getLineFormat(conds[i]));
			}
			is.close();
			return ffp;
		}
	}

	public FlatFileParser createFFP(URL url)
		throws FFPStreamParseException, IOException {
		return createFFP(url.openStream());
	}

	public static void main(String args[]) throws Exception {
		FFPFactory factory = new FFPFactory();
		String resName = "test/FFPFactoryTest.xml";
		InputStream is = factory.getClass().getResourceAsStream(resName);
		if (is == null)
			throw new RuntimeException(resName + " not found");
		factory.createFFP(is);
	}

}
