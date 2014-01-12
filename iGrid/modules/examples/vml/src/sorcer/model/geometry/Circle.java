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
public class Circle {

	private double radius;

	private Var<Double> radiusVar;

	private ParametricCircle circle = new ParametricCircleImpl();

	public Circle() {
	}

	public Circle(double radius) {
		this.radius = radius;
	}
	
	public Circle(Var<Double> radiusVar) {
		this.radiusVar = radiusVar;
	}

	public double getArea() throws RemoteException, EvaluationException {
		if (radiusVar != null)
			return circle.area(radiusVar.getValue());
		else
			return circle.area(radius);
	}

	public double getCircumference() throws RemoteException,
			EvaluationException {
		if (radiusVar != null)
			return circle.circumference(radiusVar.getValue());
		else
			return circle.circumference(radius);
	}
}
