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

package sorcer.model.geometry;

import java.rmi.RemoteException;

import sorcer.core.provider.ServiceTasker;
import sorcer.service.Context;
import sorcer.service.ContextException;

import com.sun.jini.start.LifeCycle;

/**
 * @author Mike Sobolewski
 */
@SuppressWarnings("rawtypes")
public class ContextualRectTasker extends ServiceTasker implements RemoteContextualRect {
	
	/**
	 * @param args
	 * @param lifeCycle
	 * @throws Exception
	 */
	public ContextualRectTasker(String[] args, LifeCycle lifeCycle) throws Exception {
		super(args, lifeCycle);
	}

	/* (non-Javadoc)
	 * @see sorcer.model.geometry.Rect#area(sorcer.service.Context)
	 */
	@Override
	public Context area(Context context) throws RemoteException, ContextException {
		double x = (Double)context.getValue("arg/x");
		double y = (Double)context.getValue("arg/y");
		context.putValue("area", x * y);
		return context;
	}

	/* (non-Javadoc)
	 * @see sorcer.model.geometry.Rect#aspect(sorcer.service.Context)
	 */
	@Override
	public Context aspect(Context context) throws RemoteException, ContextException {
		double x = (Double)context.getValue("arg/x");
		double y = (Double)context.getValue("arg/y");
		context.putValue("aspect", y / x);
		return context;
	}

	/* (non-Javadoc)
	 * @see sorcer.model.geometry.Rect#perimeter(sorcer.service.Context)
	 */
	@Override
	public Context perimeter(Context context) throws RemoteException, ContextException {
		double x = (Double)context.getValue("arg/x");
		double y = (Double)context.getValue("arg/y");
		context.putValue("perimeter", 2 * x + 2 * y);
		return context;
	}

}
