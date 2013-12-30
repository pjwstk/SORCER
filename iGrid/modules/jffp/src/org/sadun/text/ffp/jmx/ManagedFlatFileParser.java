/*
 * Created on Jan 19, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp.jmx;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.management.MBeanException;

import org.sadun.text.ffp.FFPFactory;
import org.sadun.text.ffp.FFPStreamParseException;
import org.sadun.text.ffp.FlatFileParser;
import org.sadun.text.ffp.ImageParseException;
import org.sadun.text.ffp.LineFormat;
import org.sadun.util.IndentedPrintWriter;



/**
 *
 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano Sadun</a>
 * @version 1.0
 */
public class ManagedFlatFileParser implements ManagedFlatFileParserMBean {

	private static class EmbeddedFFP extends FlatFileParser {
		
		private EmbeddedFFP() {
			super();
		}
		
		private EmbeddedFFP(FlatFileParser ffp) {
			super();
			setAutoTrimMode(ffp.isAutoTrimMode());
			setFailOnLineParsingError(ffp.isFailOnLineParsingError());
			setFailOnNoMatchingConditions(ffp.isFailOnNoMatchingConditions());
			setFastMatchMode(ffp.isFastMatchMode());
			setLineSeparator(ffp.getLineSeparator());
		}
		public Map getFieldsByCondition() { return super.getFieldsByCondition(); }
	}
	
	protected EmbeddedFFP ffp = new EmbeddedFFP();
	private String formatsURL;
	
	private FFPFactory ffpFactory = new FFPFactory();
	
	
	/* (non-Javadoc)
	 * @see org.sadun.text.ffp.jmx.ManagedFlatFileParserMBean#slistFormats()
	 */
	public String slistFormats(boolean compactDescription) throws MBeanException {
		StringWriter sw=new StringWriter();
		IndentedPrintWriter pw = new IndentedPrintWriter(sw);
		
		if (ffp.getFieldsByCondition().keySet().size()==0)
			return "No line formats installed";
		
		for (Iterator i=ffp.getFieldsByCondition().keySet().iterator();i.hasNext();) {
			FlatFileParser.Condition condition = (FlatFileParser.Condition)i.next();
			pw.print(condition+" use ");
			if (compactDescription) {
				pw.println("line format '"+((LineFormat)ffp.getFieldsByCondition().get(condition)).getName()+"'");
			} else {
				pw.incIndentation(5);
				pw.println(ffp.getFieldsByCondition().get(condition));
				pw.decIndentation(5);
				pw.println();
			}
		}
		return sw.toString();
	}
	
	public String listFormats() throws MBeanException {
		return slistFormats(false);
	}
	
	/* (non-Javadoc)
	 * @see org.sadun.text.ffp.jmx.ManagedFlatFileParserMBean#addFormat(java.lang.String)
	 */
	public void addFormat(String formatImage) throws MBeanException {
		try {
			ffp.declare(formatImage);
		} catch (ImageParseException e) {
			throw new MBeanException(e);
		}
	}
	
	public String getFormatURL() {
		if (formatsURL==null || "".equals(formatsURL.trim())) return "";
		return formatsURL;
	}
	
	/* (non-Javadoc)
	 * @see org.sadun.text.ffp.jmx.ManagedFlatFileParserMBean#setFormatURL(java.lang.String)
	 */
	public synchronized void setFormatURL(String url) throws MBeanException {
		if (url==null || "".equals(url.trim())) {
			if (formatsURL==null) return;
			else { 
				ffp=new EmbeddedFFP();
				return;
			}
		}
		
		try {
			ffp=new EmbeddedFFP(ffpFactory.createFFP(new URL(url)));
		} catch (FFPStreamParseException e) {
			throw new MBeanException(e);
		} catch (MalformedURLException e) {
			throw new MBeanException(e);
		} catch (IOException e) {
			throw new MBeanException(e);
		}
		
	}
	
	
}
