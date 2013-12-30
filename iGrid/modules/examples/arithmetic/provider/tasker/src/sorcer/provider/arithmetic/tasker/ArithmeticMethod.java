/* 
 Copyright (C) 2002 Texas Tech University. All rights reserved.
 
 This software is the confidential and proprietary information of 
 Texas Tech University. ("Confidential Information").  You shall not
 disclose such Confidential Information and shall use it only in
 accordance with the terms of the license agreement you entered into
 with Texas Tech University.

 $Source: /usr/local/sobolemw/cvs/iGrid-07.cvsrep/iGrid/modules/examples/arithmetic/provider/tasker/src/sorcer/provider/arithmetic/tasker/ArithmeticMethod.java,v $
 */
package sorcer.provider.arithmetic.tasker;

import java.rmi.RemoteException;

import sorcer.arithmetic.Adder;
import sorcer.arithmetic.Arithmometer;
import sorcer.arithmetic.Divider;
import sorcer.arithmetic.Multiplier;
import sorcer.arithmetic.Subtractor;
import sorcer.core.context.ArrayContext;
import sorcer.core.signature.NetSignature;
import sorcer.service.Context;

/**
 * The ArithmeticMethod is intended to be used with Tasker providers to execute
 * service tasks of type {@link ArithmeticTask}. These tasks with {@link ArrayContext}
 * are executed by internal arithmometer functions overidding the same functions of a Tasker
 * provider or any remote Arithmetic service provider.
 */
public class ArithmeticMethod extends NetSignature {

	Arithmometer aritmo = new Arithmometer();
	/**
	 * Default Constructor
	 */
	public ArithmeticMethod(String selector) {
		super(selector, "sorcer.core.Tasker", NULL);
	}

	/**
	 * Implements the {@link Adder} interface.
	 * 
	 * @param context
	 *            input context for this operation
	 * @return an output service context
	 * @throws RemoteException
	 */
	public Context add(Context context) throws RemoteException {
		return aritmo.add(context);
	}

	/**
	 * Implements the {@link Subtractor} interface.
	 * 
	 * @param context
	 *            input context for this operation
	 * @return an output service context
	 * @throws RemoteException
	 */
	public Context subtract(Context context)
			throws RemoteException {
		return aritmo.subtract(context);
	}

	/**
	 * Implements the {@link Multiplier} interface.
	 * 
	 * @param context
	 *            input context for this operation
	 * @return an output service context
	 * @throws RemoteException
	 */
	public Context multiply(Context context)
			throws RemoteException {
		return aritmo.multiply(context);
	}

	/**
	 * Implements the {@link Divider} interface.
	 * 
	 * @param context
	 *            input context for this operation
	 * @return an output service context
	 * @throws RemoteExceptionO
	 */
	public Context divide(Context context) throws RemoteException {
		return aritmo.divide(context);
	}

}