/*
 * Copyright 2009 the original author or authors.
 * Copyright 2009 SorcerSoft.org.
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
package sorcer.core.context;

import java.rmi.RemoteException;

import sorcer.service.Context;

public class ContextManagementImpl 
{
	// used in sorcer.core.exertion.ContextTask
	final static String CONTEXT_REQUEST_PATH = "request/context";
	
	/**
	 * Returns a default service context by the owning provider.
	 * 
	 * @return a default context
	 */
	public Context getContext() throws RemoteException
	{
		return new ServiceContext();
	}

	/**
	 * Returns a service context for a method named <code>selector</code> in an
	 * interface <code>interfaceName</code>.
	 * 
	 * @param interfaceName
	 * @param selector
	 * @return a service context
	 */
	public Context getContext(String interfaceName, String selector) throws RemoteException
	{
		return new ServiceContext();
		
	}

	
	/**
	 * Returns a service context for a method named <code>selector</code> in an
	 * interface <code>interfaceName</code> as specified in the <code>context</code>.
	 * 
	 * @param context
	 * @return a service context
	 */
	public Context getContext(Context context) throws RemoteException
	{
		return new ServiceContext();
	}

	
	/**
	 * Sets a service context for a method named <code>selector</code> in an
	 * interface <code>interfaceName</code>.
	 * 
	 * @param interfaceName
	 * @param selector
	 * @return service context
	 */
	public Context setContext(String interfaceName, String selector) throws RemoteException
	{
		return new ServiceContext();
	}

/*	public Context getMethodContext(String interfaceName) throws RemoteException
	{
		//Context context = new ServiceContext("laboratory/name");
		//try {
			/*context.setAttribute("person|first|middle|lastname");
			context.setSubject("laboratory/name", "SORCER");*/
		
			//context.putValue("university/home", "TTU");
			/*context.putValue("university/department/name", "CS");
			context.putValue("university/department/room/number", "C20B");
			context.putValue("university/department/room/phone/number",
					"806-742-1194");
			context.putValue("university/department/room/phone/ext", "237");
			context.putValue("director/email", "sobol@cs.ttu.edu");*/
		//} catch (ContextException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
	/*	System.out.println("hello");
		Context context=new ServiceContext();
		try 
		{
			FileInputStream fis=new FileInputStream("context.cxnt");
			ObjectInputStream in=new ObjectInputStream(fis);
			try {
				context=(Context)in.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			in.close();
		
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return context;
	}
	public boolean saveMethodContext(String interfaceName, String methodName,Context theContext) throws RemoteException
	{
		
		System.out.println("saving context");	
		try 
			{
				FileOutputStream fos=new FileOutputStream("context.cxnt");
				ObjectOutputStream out=new ObjectOutputStream(fos);
				out.writeObject(theContext);
				out.close();
			
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
	
		
		
		return true;
	}
	public String[] currentContextList(String interfaceName) throws RemoteException
	{
		return new String[0];
	}
	public boolean deleteContext(String interfaceName, String methodName) throws RemoteException
	{
		return true;
	}*/
}
