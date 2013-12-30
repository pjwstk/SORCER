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

import sorcer.service.Arg;
import sorcer.vfe.util.Wrt;

public class FidelityInfo implements Serializable, Arg {

	static final long serialVersionUID = 7519269701188244414L;

	protected String varName;

	protected String fidelityName;

	protected String evalutorName;

	protected String filterName;

	// selected gradient for this fidelity
	protected String gradientName;

	protected Wrt wrt;

	protected Differentiation differentiation;

	public FidelityInfo() {
		// fidelityName undefined
	}

	public FidelityInfo(String fidelityName) {
		this.fidelityName = fidelityName;
	}

	public FidelityInfo(String varName, String fidelityName) {
		this.varName = varName;
		this.fidelityName = fidelityName;
	}

	public FidelityInfo(String varName, String fidelityName,
			String gradientName) {
		this.varName = varName;
		this.fidelityName = fidelityName;
		this.gradientName = gradientName;
	}

	public FidelityInfo(String varName, Wrt wrt, String fidelityName,
			String gradientName) {
		this(varName, fidelityName, gradientName);
		this.wrt = wrt;
	}

	public FidelityInfo(String varName, String fidelityName,
			String evaluatorName, String filterName) {
		this(varName, fidelityName, evaluatorName, filterName, null);
	}

	public FidelityInfo(String varName, String fidelityName,
			String evaluatorName, String filterName, String gradientName) {
		this.varName = varName;
		this.fidelityName = fidelityName;
		this.evalutorName = evaluatorName;
		this.filterName = filterName;
		this.gradientName = gradientName;
	}

	public FidelityInfo(FidelityInfo eval) {
		varName = eval.varName;
		fidelityName = eval.fidelityName;
		evalutorName = eval.evalutorName;
		filterName = eval.filterName;
		gradientName = eval.gradientName;
		if (eval.differentiation != null)
			differentiation = new Differentiation(eval.differentiation);
	}

	public String getName() {
		return fidelityName;
	}

	public void setName(String fidelityName) {
		this.fidelityName = fidelityName;
	}

	public String getEvaluatorName() {
		if (evalutorName == null)
			return fidelityName;
		else
			return evalutorName;
	}

	public String getFilterName() {
		return filterName;
	}

	public void setEvaluatorName(String name) {
		evalutorName = name;
		if (differentiation != null)
			differentiation.evaluatorName = name;
	}

	public void setFilterName(String name) {
		filterName = name;
	}

	/**
	 * <p>
	 * Returns the with-respect-to var name.
	 * </p>
	 * 
	 * @return the wrt name
	 */
	public Wrt getWrt() {
		return wrt;
	}

	/**
	 * <p>
	 * Assigns the with-respect-to var name.
	 * </p>
	 * 
	 * @param wrt
	 *            the wrt name to set
	 */
	public void setWrt(Wrt wrt) {
		this.wrt = wrt;
	}

	public void setWrt(String wrt) {
		this.wrt = new Wrt(wrt);
	}

	/**
	 * <p>
	 * Returns the gradient name (a column name) in the corresponding derivative
	 * evaluator. Each column specifies a list of derivative evaluators
	 * associated with this name.
	 * </p>
	 * 
	 * @return the gradientName
	 */
	public String getGradientName() {
		return gradientName;
	}

	/**
	 * <p>
	 * Sets the name (a column name in) the corresponding derivative evaluator.
	 * Each column specifies a list of derivative evaluators associated with
	 * this name.
	 * </p>
	 * 
	 * @param gradientName
	 *            the gradientName to set
	 */
	public void setGradientName(String gradientName) {
		this.gradientName = gradientName;
	}

	/**
	 * <p>
	 * Returns a variable name for this fidelity.
	 * </p>
	 * 
	 * @return the varName
	 */
	public String getVarName() {
		return varName;
	}

	/**
	 * <p>
	 * Assigns a variable name for this fidelity.
	 * </p>
	 * 
	 * @param varName
	 *            the varName to set
	 */
	public void setVarName(String varName) {
		this.varName = varName;
	}

	/**
	 * <p>
	 * Returns a derivative evaluator declaration for this variable realization.
	 * </p>
	 * 
	 * none
	 * 
	 * @return the derivative
	 */
	public Differentiation getDifferentiation() {
		return differentiation;
	}

	/**
	 * <p>
	 * Assigns a derivative evaluator declaration for this variable realization
	 * </p>
	 * 
	 * @param differentiation
	 *            the differentiation to set
	 */
	public void setDifferentiation(Differentiation differentiation) {
		this.differentiation = differentiation;
	}

	@Override
	public String toString() {
		return "Fidelity: " + fidelityName
				+ (varName == null ? "" : ":" + varName) + ":" + evalutorName
				+ ":" + filterName + ", " + differentiation;
	}

}
