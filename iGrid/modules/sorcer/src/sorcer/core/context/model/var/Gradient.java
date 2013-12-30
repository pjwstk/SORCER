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

package sorcer.core.context.model.var;

import java.io.Serializable;
import java.util.List;

import sorcer.vfe.util.Wrt;


public class Gradient implements Serializable {

	static final long serialVersionUID = -7165041877787413968L;
	
	// name of the evaluator that depends on WRT
	private String evaluationName;

	// list of dependent variable names
	private Wrt wrt;

	public Gradient(String name) {
		evaluationName = name;
	}

	public Gradient(Gradient gradient) {
		evaluationName = gradient.evaluationName;
		wrt = new Wrt(gradient.wrt);
	}
	
	public Gradient(String gradient, List<String> wrtNames) {
		evaluationName = gradient;
		wrt = new Wrt(wrtNames);
	}
	/**
	 * <p>
	 * Returns the evaluation name for this gradient calculation.
	 * </p>
	 * 
	 * @return the evaluationName
	 */
	public String getEvaluationName() {
		return evaluationName;
	}

	/**
	 * <p>
	 *  Assigns the evaluation name for this gradient calculation.
	 * </p>
	 * 
	 * @param evaluationName
	 *            the evaluationName to set
	 */
	public void setEvaluationName(String evaluatoName) {
		this.evaluationName = evaluatoName;
	}

	/**
	 * <p>
	 * Returns with-respect-to var names o this gradient.
	 * </p>
	 * 
	 * @return the wrt
	 */
	public Wrt getWrt() {
		return wrt;
	}

	/**
	 * <p>
	 * Assigns with-respect-to var names o this gradient.
	 * </p>
	 * 
	 * @param wrt
	 *            the wrt to set
	 */
	public void setWrt(Wrt wrt) {
		this.wrt = wrt;
	}
	
	@Override
	public String toString() {
		return "Gradient: " + evaluationName + ":" + wrt;
	}
}
