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

import static sorcer.co.operator.list;
import static sorcer.vo.operator.args;
import static sorcer.vo.operator.constraintVars;
import static sorcer.vo.operator.evaluator;
import static sorcer.vo.operator.expression;
import static sorcer.vo.operator.groovy;
import static sorcer.vo.operator.inputVars;
import static sorcer.vo.operator.objectiveVars;
import static sorcer.vo.operator.optimizationModel;
import static sorcer.vo.operator.outputVars;
import static sorcer.vo.operator.parametricModel;
import static sorcer.vo.operator.parametricTable;
import static sorcer.vo.operator.responseModel;
import static sorcer.vo.operator.responseTable;
import static sorcer.vo.operator.setEvaluator;
import static sorcer.vo.operator.table;
import static sorcer.vo.operator.var;
import static sorcer.vo.operator.vars;

import java.util.List;
import java.util.logging.Logger;

import sorcer.core.context.model.opti.OptimizationModel;
import sorcer.core.context.model.var.ParametricModel;
import sorcer.core.context.model.var.ResponseModel;
import sorcer.service.ContextException;
import sorcer.service.EvaluationException;
import sorcer.util.Sorcer;
import sorcer.vfe.ServiceEvaluator;
import sorcer.vfe.Var;
import sorcer.vfe.VarInfo.Relation;
import sorcer.vfe.VarInfo.Target;
import sorcer.vfe.Variability.Type;
import sorcer.vfe.util.VarList;

/**
 * @author Mike Sobolewski and Noah Hafner <nmh+aero@nomh.org>
 *
 * Simple hello model example using geometry for a rectangle
 */
@SuppressWarnings("rawtypes")
public class RectModelBuilder {

	private final static Logger logger = Logger
			.getLogger(RectModelBuilder.class.getName());
	
	/**
	 * Example models: response model
	 * this is the simplest model, scalar input and outputs
	 */
	public static ResponseModel getResponseModel() throws ContextException {
		ResponseModel model = responseModel(
				inputVars("x","y"),
				outputVars(var("area"), var("aspect"), var("perimeter")));
		
		configureModel(model);
		return model;

	}
	
	/**
	 * Example models: parametric model
	 * this model builds on the response model and can calculate output(s) 
	 *   from multiply valued input(s)
	 */
	@SuppressWarnings("unchecked")
	public static ParametricModel getParametricModel() throws ContextException {
		String inURL = Sorcer.getWebsterUrl() + "/geometry/parametric_datafile.txt";
		String outURL = Sorcer.getWebsterUrl() + "/geometry/response_datafile.txt";
		
		ParametricModel model = parametricModel(
				RectModelBuilder.getResponseModel(),
				table(
					parametricTable(inURL, ", "),
					responseTable(outURL, ", ")));
		
		return model;
	}

	@SuppressWarnings("unchecked")
	public static OptimizationModel getOptimizationModel()
			throws ContextException {
		OptimizationModel model = optimizationModel(
				RectModelBuilder.getResponseModel(), 
				objectiveVars(var("fo", "area", Target.min )),
				outputVars("g", 4),
				constraintVars(var("g1c", "g1", Relation.lt, 0.0), 
					var("g2c", "g2", Relation.lt, 0.0), 
					var("g3c","g3", Relation.lt, 0.0),
					var("g4c","g4", Relation.lt, 0.0)));		

		configureOptiModel(model);
		configureSensitivityModel(model);
		model.initialize();
		return model;
	}

	/**
	 * Example of model configuration
	 * This configuration function works with multiple types of models
	 * this is the mapping of input(s)->output(s)
	 */
	public static void configureModel(ResponseModel model) throws ContextException {
		// independent vars
		Var x = var(model, "x", 2.0);
		Var y = var(model, "y", 3.0);
		
		// dependent vars
		Var area = var(model, "area");
		setEvaluator(area, groovy("areae", "x * y", vars(x, y)));

		Var aspect = var(model, "aspect");
		setEvaluator(aspect, groovy("y / x", vars(x, y)));
				
		Var perimeter = var(model,"perimeter"); 
		setEvaluator(perimeter, groovy("2 * x + 2 * y", vars(x, y)));
	}
	
	public static void configureOptiModel(OptimizationModel om) throws ContextException {
		om.getVar("x").setLowerBound(1.0);
		om.getVar("x").setUpperBound(150.);
		om.getVar("y").setLowerBound(1.0);
		om.getVar("y").setUpperBound(150.);
		om.getVar("x").addKind(Type.BOUNDED);
		om.getVar("y").addKind(Type.BOUNDED);
		ServiceEvaluator g1e = evaluator("g1e", "-2.0*x/399.0 - 2.0*y/399.0 + 1.0");
		om.setResponseEvaluator("g1", g1e);
		g1e.addArgs(om.getInputVars("x", "y"));	

		ServiceEvaluator g2e = evaluator("g2e", "2.0*x/401. + 2.0*y/401. - 1.0");
		om.setResponseEvaluator("g2", g2e);
		g2e.addArgs(om.getInputVars("x", "y"));	

		ServiceEvaluator g3e = evaluator("g3e", "-y/(x*0.99) + 1.0");
		om.setResponseEvaluator("g3", g3e);
		g3e.addArgs(om.getInputVars("x", "y"));	

		ServiceEvaluator g4e = evaluator("g4e", "y/(x*1.01) - 1.0");
		om.setResponseEvaluator("g4", g4e);
		g4e.addArgs(om.getInputVars("x", "y"));	
	}

	private static OptimizationModel configureSensitivityModel(OptimizationModel om) 
	throws ContextException, EvaluationException {
	// Sensitivity Model
	// design vars: x, y
	// response vars: area (a)
			
	// response area:ae="x * y"
		// derivativeResponse dadx = "y"
		// derivativeResponse dady = "x"
	// response g1:g1e="-2.0*x - 2.0*y + 399.0" 
		// derivativeResponse dg1dx = "-2.0"
		// derivativeResponse dg1dy = "-2.0"
	// response g2:g2e="3.0*x + 2.0*y - 411.0"
		// derivativeResponse dg2dx = "3.0"
		// derivativeResponse dg2dy = "2.0"
	// response g3:g3e="-y/x + 0.99"
		// derivativeResponse dg3dx1 = "y/x^2"
		// derivativeResponse dg3dx2 = "-1.0/x"
	// response g4:g4e="y/x - 1.01"
		// derivativeResponse dg4dx = "-y/x^2"
		// derivativeResponse dg4dy = "1.0/x"
	
	// configure the sensitivity model
	// response area:area="x * y"
	// derivativeResponse dadx = "y"
	// derivativeResponse dady = "x"
	ServiceEvaluator dadxv1 = expression("dadxe1", "y", args(om.getInputVars("y")));
	ServiceEvaluator dadyv1 = expression("dadye1", "x", args(om.getInputVars("x")));
	List<ServiceEvaluator> aeg1 = list(dadxv1, dadyv1);	
	om.setGradientEvaluators("area", "areae", "areaeg1",  aeg1);
	
	// response g1:g1e="-2.0*x - 2.0*y + 399.0" 
	// derivativeResponse dg1dx:dg1vdx = "-2.0"
	// derivativeResponse dg1dy:dg1vdy = "-2.0"
	Var dg1vdxv1 = var("dg1edx-pde", -2.0/399.0);
	Var dg1vdyv1 = var("dg1edy-pde", -2.0/399.0);
	VarList g1vg1 = vars(dg1vdxv1, dg1vdyv1);	
	om.setGradientVars("g1", "g1e", "g1eg1",  g1vg1);

	// response g2:g2e="2.0*x + 2.0*y - 401.0"
	// derivativeResponse dg2dx1:dg2vdx = "2"
	// derivativeResponse dg2dx2:dg2vdy = "2"
	Var dg2vdxv1 = var("dg2vdx-pde", 2.0/401.);
	Var dg2vdyv1 = var("dg2vdy-pde", 2.0/401.);
	VarList g2vg1 = vars(dg2vdxv1, dg2vdyv1);	
	om.setGradientVars("g2", "g2e", "g2eg1",  g2vg1);
	
	// response g3:g3e="-y/x + 0.99"
	// derivativeResponse dg3dx1:dg3edx1 = "y/x^2"
	// derivativeResponse dg3dx2:dg3edx2 = "-1.0/x"
	ServiceEvaluator dg3edxe1 = expression("dg3edxe1", "y/(x^2*.99)", args(om.getInputVars("x", "y")));
	ServiceEvaluator dg3edye1 = expression("dg3edye1", "-1.0/(x*.99)", args(om.getInputVars("x")));
	List<ServiceEvaluator> g3eg1 = list(dg3edxe1, dg3edye1);	
	om.setGradientEvaluators("g3", "g3e", "g3eg1",  g3eg1);
	
	// response g4:g4e="y/x - 1.01"
	// derivativeResponse dg4dx1:dg4edx1 = "-y/x^2"
	// derivativeResponse dg4dx2:dg4edx2 = "1.0/x"
	ServiceEvaluator dg4edxe1 = expression("dg4edxe1", "-y/(x^2*1.01)", args(om.getInputVars("x", "y")));
	ServiceEvaluator dg4edye1 = expression("dg4edx2e1", "1.0/(x*1.01)", args(om.getInputVars("x")));
	List<ServiceEvaluator> g4eg1 = list(dg4edxe1, dg4edye1);	
	om.setGradientEvaluators("g4", "g4e", "g4eg1",  g4eg1);
	
	return om;
	}
	
}