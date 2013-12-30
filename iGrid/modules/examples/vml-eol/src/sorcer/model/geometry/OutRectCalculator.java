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

/**
 * @author Mike Sobolewski
 */

import static java.lang.System.out;

import java.rmi.RemoteException;

import sorcer.service.ContextException;

public class OutRectCalculator {

	private static ParametricRectImpl rect = new ParametricRectImpl();

	public static void main(String... args) throws ContextException,
			NumberFormatException, RemoteException {
		if (args.length != 3)
			System.err.println("Wrong number of arguments!");
		
		if (args.length == 3 && args[0].equals("area"))
			out.println("area="
					+ rect.area(Double.parseDouble(args[1]),
							Double.parseDouble(args[2])));
		else if (args.length == 3 && args[0].equals("aspect"))
			out.println("area="
					+ rect.aspect(Double.parseDouble(args[1]),
							Double.parseDouble(args[2])));
		else if (args.length == 3 && args[0].equals("perimeter"))
			out.println("area="
					+ rect.perimeter(Double.parseDouble(args[1]),
							Double.parseDouble(args[2])));
	}
}
