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

import com.sun.jini.start.LifeCycle;

/**
 * @author Mike Sobolewski
 */
public class ParametricRectTasker extends ServiceTasker implements RemoteParametricRect {
	
	/**
	 * @param args
	 * @param lifeCycle
	 * @throws Exception
	 */
	public ParametricRectTasker(String[] args, LifeCycle lifeCycle) throws Exception {
		super(args, lifeCycle);
	}
	
	/* (non-Javadoc)
	 * @see sorcer.model.geometry.Rectangle#area(double, double)
	 */
	@Override
	public double area(double x, double y) throws RemoteException {
		return  x * y;
	}

	/* (non-Javadoc)
	 * @see sorcer.model.geometry.Rectangle#aspect(double, double)
	 */
	@Override
	public double aspect(double x, double y) throws RemoteException {
		return  y / x;
	}

	/* (non-Javadoc)
	 * @see sorcer.model.geometry.Rectangle#perimeter(double, double)
	 */
	@Override
	public double perimeter(double x, double y) throws RemoteException {
		return 2 * x + 2 * y;
	}

}
