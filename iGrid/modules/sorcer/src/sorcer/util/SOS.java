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

public class SOS {

	/**
	 * General database prefix for the SORCER database schema.
	 */
	protected static String dbPrefix = "SOS";
	protected static String sepChar = ".";

	/** Port for a code sever (webster) */
	protected static int port = 0;

	private static boolean bootable = false;

	/**
	 * Default name 'sorcer.env' for a file defining global environment
	 * properties.
	 */
	public static String SORCER_ENV_FILENAME = "sorcer.env";

	/**
	 * Default name 'provider.properties' for a file defining provider
	 * properties.
	 */
	public static String PROVIDER_PROPERTIES_FILENAME = "provider.properties";

	/**
	 * Default name 'data.formats' for a file defining service context node
	 * types.
	 */
	protected static String CONTEXT_DATA_FORMATS = "data.formats";

	/**
	 * Default name 'servid.per' for a file storing a service registration ID.
	 */
	protected static String serviceIdFilename = "servid.per";

	/**
	 * <p>
	 * Return <code>true</code> is a SORCER {@link sorcer.provider.boot.Booter}
	 * is used, otherwise <code>false</code>,
	 * </p>
	 * 
	 * @return the bootable
	 */
	public static boolean isBootable() {
		return bootable;
	}

	/**
	 * <p>
	 * Assigns <code>true</code> by the {@link sorcer.provider.boot.Booter} when
	 * used.
	 * </p>
	 * 
	 * @param bootable
	 *            the bootable to set
	 */
	public static void setBootable(boolean bootable) {
		SOS.bootable = bootable;
	}

}