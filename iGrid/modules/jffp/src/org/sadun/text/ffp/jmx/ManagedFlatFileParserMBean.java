/*
 * Created on Jan 19, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.sadun.text.ffp.jmx;

import javax.management.MBeanException;

/**
 *
 * @author <a href="mailto:cristianosadunTAKETHISAWAY@hotmail.com">Cristiano Sadun</a>
 * @version 1.0
 */
public interface ManagedFlatFileParserMBean {
	
	public String listFormats() throws MBeanException;
	public void addFormat(String formatString) throws MBeanException;
	public String getFormatURL() throws MBeanException;
	public void setFormatURL(String url) throws MBeanException;

}
