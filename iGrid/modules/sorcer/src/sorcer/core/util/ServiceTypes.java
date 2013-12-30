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

package sorcer.core.util;

import java.util.Hashtable;

import sorcer.core.SorcerConstants;
import sorcer.service.Context;

public class ServiceTypes implements SorcerConstants {
	private Hashtable appNames, formats, modifiers;

	public ServiceTypes() {
		// do nothing
	}

	public String getAppNameCd(String name) {
		return (String) appNames.get(name);
	}

	public String getFormatCd(String format) {
		return (String) formats.get(format);
	}

	public String getModifierCd(String modifier) {
		return (String) appNames.get(modifier);
	}

	public String getCdType(String name, String format, String modifier) {
		StringBuffer sb = new StringBuffer();
		sb.append(getAppNameCd(name)).append(CPS).append(getFormatCd(format))
				.append(CPS).append(getModifierCd(modifier));

		return sb.toString();
	}

	public String getCdTypePath(String name, String format, String modifier,
			int index) {
		StringBuffer sb = new StringBuffer();
		sb.append(Context.DATA_NODE_TYPE).append(CPS).append(
				getAppNameCd(name)).append(CPS).append(getFormatCd(format))
				.append(CPS).append(getModifierCd(modifier)).append(CPS)
				.append(IND).append(index);

		return sb.toString();
	}

	public Hashtable getAppName() {
		return appNames;
	}

	public void setAppNames() {
		this.appNames = appNames;
	}

	public Hashtable getFormats() {
		return formats;
	}

	public void setFormats(Hashtable formats) {
		this.formats = formats;
	}

	public Hashtable getModifiers() {
		return modifiers;
	}

	public void setModifiers(Hashtable modifiers) {
		this.modifiers = modifiers;
	}
}
