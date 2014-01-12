/*
 * Copyright 2012 the original author or authors.
 * Copyright 2012 SorcerSoft.org.
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

import sorcer.service.EvaluationException;
import sorcer.vfe.Var;


/**
 * @author Mike Sobolewski
 */
public class Rectangle {

	private double length;
	
	private double width;
	
	private Var<Double> lengthVar;
	
	private Var<Double> widthVar;
	
	private ParametricRect rect = new ParametricRectImpl();
	
	public Rectangle() {
	}
	
	public Rectangle(double length, double width) {
		this.length = length;
		this.width = width;
	}
	
	public Rectangle(Var<Double> lengthVar, Var<Double> widthVar) {
		this.lengthVar = lengthVar;
		this.widthVar = widthVar;
	}
	
	public double getArea() throws RemoteException, EvaluationException {
		if (lengthVar != null && widthVar != null)
			return rect.area(lengthVar.getValue(), widthVar.getValue());
		else
			return rect.area(length, width);
	}
	
	public double getAspect() throws RemoteException {
		return rect.aspect(length, width);
	}
	
	public double getPrimeter() throws RemoteException {
		return rect.perimeter(length, width);
	}

}
