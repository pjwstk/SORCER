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
import java.util.ArrayList;
import java.util.List;


public class Differentiation implements Serializable {
	
	String evaluatorName;
	List<String> wrtNames = new ArrayList<String>();
	List<Gradient> gradients = new ArrayList<Gradient>();

	public Differentiation() {
		// do nothing
	}
	
	public Differentiation(Differentiation derivation) {
		evaluatorName = derivation.evaluatorName;
		gradients = new ArrayList<Gradient>(derivation.gradients.size());
		for (Gradient g : derivation.gradients)
			gradients.add(new Gradient(g));
		wrtNames = new ArrayList<String>(derivation.wrtNames.size());
		wrtNames.addAll(derivation.wrtNames);;
	}
	
	public Differentiation(String evaluator, String gradient, List<String> wrt) {
		evaluatorName = evaluator;
		gradients.add(new Gradient(gradient, wrt));
		wrtNames.addAll(wrt);;
	}
	
	public String getEvaluatorName() {
		return evaluatorName;
	}

	public void setEvaluatorName(String varName) {
		this.evaluatorName = varName;
	}

	public List<String> getDependentVarNames() {
		return wrtNames;
	}

	public void setDependentVarNames(List<String> dependentVarNames) {
		this.wrtNames = dependentVarNames;
	}

	public List<String> addDepenedntVar(String name) {
		wrtNames.add(name);
		return wrtNames;
	}

	public List<Gradient> getGradients() {
		return gradients;
	}

	public Gradient getGradient(String evaluationName) {
		for (Gradient g: gradients) {
			if (g.getEvaluationName().equals(evaluationName)) {
				return g;
			}
		}
		return null;
	}

	public void setGradients(List<Gradient> gradients) {
		this.gradients = gradients;
	}

	public List<Gradient> addGradient(Gradient gradient) {
		gradients.add(gradient);
		return gradients;
	}

	@Override
	public String toString() {
		return "Differentiation: " + evaluatorName + ":" + wrtNames + ":" + gradients;
	}
}