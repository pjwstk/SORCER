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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class Requestors {
	private final static Logger logger = Log.getTestLog();
	final static String DEFAULT_PROPERTIES_FILENAME = "requestor.properties";
	final static String DEAFULT_CONFGG_DIR = "configs";

	private Requestors() {
		// utility class
	}

	public static void loadProperties() {
		loadProperties(DEFAULT_PROPERTIES_FILENAME);
	}

	public static Properties loadProperties(String filename) {
		logger.info("iGrid.home: " + System.getProperty("iGrid.home"));
		Properties props = Sorcer.getEnvProperties();
		logger.info("Loading properties in filename: " + filename);
		Properties properties = new Properties();
		String choppedFilename = null;

		try {
			File pf = new File("tmp");
			String path = pf.getCanonicalPath();
			// We assume that run scripts are in bin directory
			int pos = path.lastIndexOf("bin");
			choppedFilename = path.substring(0, pos);

			pf = new File(choppedFilename + DEAFULT_CONFGG_DIR
					+ File.separatorChar + filename);
			InputStream is = (InputStream) (new FileInputStream(pf));

			if (is != null) {
				logger.info("Requestor properties in: " + choppedFilename
						+ DEAFULT_CONFGG_DIR + File.separatorChar + filename);
				properties.load(is);
			} else {
				logger
						.info("Not able to open stream on properties "
								+ filename);
				return null;
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			logger.info("Not able to create service requestor properties in: "
					+ choppedFilename + DEAFULT_CONFGG_DIR + File.separatorChar
					+ filename);
		}
		Sorcer.appendProperties(properties);
		logProperties(properties, "Requestor properties");
		logProperties(props, "SORCER Environment properties");

		return properties;
	}

	/**
	 * Log all identified provider's properties as key=value pairs.
	 */
	public static void logProperties(Properties properties, String msg) {

		final StringBuilder builder = new StringBuilder();
		builder.append(msg + ": [");
		boolean first = true;
		for (Entry<Object, Object> e : properties.entrySet()) {
			if (!first) {
				builder.append(", ");
			} else {
				first = false;
			}
			builder.append(e.getKey() + "=" + e.getValue());
		}
		builder.append("]");
		logger.info(builder.toString());
	}

}