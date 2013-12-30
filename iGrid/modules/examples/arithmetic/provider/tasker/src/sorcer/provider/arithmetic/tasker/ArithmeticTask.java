/* 
 Copyright (C) 2002 Texas Tech University. All rights reserved.
 
 This software is the confidential and proprietary information of 
 Texas Tech University. ("Confidential Information").  You shall not
 disclose such Confidential Information and shall use it only in
 accordance with the terms of the license agreement you entered into
 with Texas Tech University.

 $Source: /usr/local/sobolemw/cvs/iGrid-07.cvsrep/iGrid/modules/examples/arithmetic/provider/tasker/src/sorcer/provider/arithmetic/tasker/ArithmeticTask.java,v $
 */
package sorcer.provider.arithmetic.tasker;

import sorcer.core.exertion.NetTask;

public class ArithmeticTask extends NetTask {

	public ArithmeticTask(String selector) {
		super("Arithmetic task",
				"execute artithmetic function by own arithmometer",
				new ArithmeticMethod(selector));
	}
}