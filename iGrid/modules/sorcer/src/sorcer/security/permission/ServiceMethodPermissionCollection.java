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

package sorcer.security.permission;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.Hashtable;

public class ServiceMethodPermissionCollection extends PermissionCollection {
	private Hashtable permissions;
	private boolean addedAdmin;
	private int adminMask;

	ServiceMethodPermissionCollection() {
		permissions = new Hashtable();
		addedAdmin = false;
	}

	public void add(Permission p) {
		if (!(p instanceof ServiceMethodPermission))
			throw new IllegalArgumentException("Wrong permission type");
		ServiceMethodPermission fmp = (ServiceMethodPermission) p;
		String name = fmp.getName();
		ServiceMethodPermission other = (ServiceMethodPermission) permissions
				.get(name);
		if (other != null)
			fmp = merge(fmp, other);
		if (name.equals("*")) {
			addedAdmin = true;
			adminMask = fmp.mask;
		}
		permissions.put(name, fmp);
	}

	public Enumeration elements() {
		return permissions.elements();
	}

	public boolean implies(Permission p) {
		if (!(p instanceof ServiceMethodPermission))
			return false;
		ServiceMethodPermission fmp = (ServiceMethodPermission) p;
		if (addedAdmin && (adminMask & fmp.mask) != 0)
			return true;
		Permission inTable = (Permission) permissions.get(fmp.getName());
		if (inTable == null)
			return false;
		return inTable.implies(fmp);
	}

	private ServiceMethodPermission merge(ServiceMethodPermission a,
			ServiceMethodPermission b) {
		String aAction = a.getActions();
		if (aAction.equals(""))
			return b;
		String bAction = b.getActions();
		if (bAction.equals(""))
			return a;
		return new ServiceMethodPermission(a.getName(), aAction + "," + bAction);
	}
}
