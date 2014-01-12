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

import sorcer.service.Context;
import sorcer.service.ContextException;

/**
 * @author Mike Sobolewski
 */
@SuppressWarnings("rawtypes")
public class ContextualCircleImpl implements ContextualCircle {

	/*
	 * (non-Javadoc)
	 * 
	 * @see sorcer.model.geometry.ContextualCircle#area(sorcer.service.Context)
	 */
	@Override
	public Context area(Context context) throws RemoteException,
			ContextException {
		double radius = (Double) context.getValue("arg/radius");
		context.putValue("area", Math.PI * Math.pow(radius, 2));
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * sorcer.model.geometry.ContextualCircle#circumference(sorcer.service.Context
	 * )
	 */
	@Override
	public Context circumference(Context context) throws RemoteException,
			ContextException {
		double radius = (Double) context.getValue("arg/radius");
		double area = 2 * Math.PI * radius;
		context.putValue("circumference", area);
		if (context.getReturnPath() != null)
			context.setReturnValue(area);
		return context;
	}
}
