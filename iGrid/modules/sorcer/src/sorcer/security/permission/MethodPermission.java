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

import java.io.Serializable;

import javax.security.auth.Subject;

import net.jini.security.AccessPermission;

public class MethodPermission extends AccessPermission implements Serializable {
	Subject subject = null;
	String methodname;

	public MethodPermission(String method) {
		super(method);
		System.out.println(this.getClass()
				+ "::>> -------- Inside MethodPermission Constructor----"
				+ method);
		methodname = method;
		/*
		 * try{ AccessControlContext context = AccessController.getContext();
		 * subject = Subject.getSubject(context);
		 * System.out.println(this.getClass()+"::>>> SUBJECT=="+subject);
		 * }catch(Exception ex){System.out.println(this.getClass()+
		 * "::>>>Error in getting current Subject"); ex.printStackTrace(); }
		 */

	}
	/*
	 * public boolean implies(Permission permission){ MethodPermission mp =
	 * (MethodPermission) permission;
	 * System.out.println(this.getClass()+"::>>> ---- Inside implies method ----"
	 * +methodname+mp.getName()); if(methodname.equals(mp.getName())){
	 * System.out.println(this+"::>> ------ Returning TRUE ----"); return true;
	 * }else{ System.out.println(this+"::>> ------ Returning FALSE ----");
	 * return true; }
	 * 
	 * if(mp.subject==null){
	 * System.out.println(this+"::>> ------- Subject was null returning false ----"
	 * ); return false; }else{
	 * System.out.println(this+"::>>> ------ Subject was not null... continuing ---"
	 * ); Principal principal =
	 * (X500Principal)subject.getPrincipals(X500Principal.class);
	 * if(("".equals(principal.getName()))){
	 * System.out.println(this+"::>> ------ Returning TRUE ----"); return true;
	 * }else{ System.out.println(this+"::>> ------ Returning FALSE ----");
	 * return false; } } }
	 */

}
