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
import java.util.StringTokenizer;

public class ServiceMethodPermission extends Permission {

	protected int mask;
	static private int VIEW = 0x01;
	static private int UPDATE = 0x02;

	public ServiceMethodPermission(String name) {
		this(name, "view");
	}

	public ServiceMethodPermission(String name, String action) {
		super(name);
		if (action == null)
			action = "view";
		parse(action);
	}

	private void parse(String action) {
		StringTokenizer st = new StringTokenizer(action, ",\t ");

		mask = 0;
		while (st.hasMoreTokens()) {
			String tok = st.nextToken();
			if (tok.equals("view"))
				mask |= VIEW;
			else if (tok.equals("update"))
				mask |= UPDATE;
			else
				throw new IllegalArgumentException("Unknown action " + tok);
		}
	}

	public boolean implies(Permission permission) {
		if (!(permission instanceof ServiceMethodPermission))
			return false;

		ServiceMethodPermission p = (ServiceMethodPermission) permission;
		String name = getName();
		if (!name.equals("*") && !name.equals(p.getName()))
			return false;
		if ((mask & p.mask) != p.mask)
			return false;
		return true;
	}

	public boolean equals(Object o) {
		if (!(o instanceof ServiceMethodPermission))
			return false;

		ServiceMethodPermission p = (ServiceMethodPermission) o;
		return ((p.getName().equals(getName())) && (p.mask == mask));
	}

	public int hashCode() {
		return getName().hashCode() ^ mask;
	}

	public String getActions() {
		if (mask == 0)
			return "";
		else if (mask == VIEW)
			return "view";
		else if (mask == UPDATE)
			return "update";
		else if (mask == (VIEW | UPDATE))
			return "view, update";
		else
			throw new IllegalArgumentException("Unknown mask");
	}

	public PermissionCollection newPermissionsCollection() {
		return new ServiceMethodPermissionCollection();
	}

	public static void main(String args[]) {
		ServiceMethodPermission sobol = new ServiceMethodPermission("sobol",
				"ArithmeticProtocol,add,*");
		ServiceMethodPermission sobolAll = new ServiceMethodPermission("sobol",
				"ArithmeticProtocol,*,*");
		ServiceMethodPermission su = new ServiceMethodPermission("*",
				"ArithmeticProtocol,*,*");

		System.out.println("sobol -> sobolAll " + sobol.implies(sobolAll));
		System.out.println("sobolAll -> sobol " + sobolAll.implies(sobol));
		System.out.println("sobolAll -> su " + sobolAll.implies(su));
		System.out.println("su -> sobol " + su.implies(sobol));
	}
}
