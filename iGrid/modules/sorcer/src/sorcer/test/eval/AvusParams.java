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

package sorcer.test.eval;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.Vector;

import sorcer.core.SorcerConstants;
import sorcer.core.context.node.ContextNode;
import sorcer.core.context.node.ContextNodeException;
import sorcer.core.context.tuple.eval.AspectVariable;
import sorcer.core.context.tuple.eval.Evaluator;
import sorcer.core.context.tuple.eval.Variable;
import sorcer.core.context.tuple.eval.VariableException;
import sorcer.util.Sorcer;
import engineering.core.design.CadDescription;
import engineering.core.design.CaeConstants;
import engineering.core.design.DesignUtil;
import engineering.core.design.DesignVariable;
import engineering.core.reliability.GaussianDistribution;

public class AvusParams implements SorcerConstants {

	private Properties properties = new Properties();

	final String engRespVariables = Sorcer
			.getProperty("engineering.design.responsevariables");

	final String engRealDVariables = Sorcer
			.getProperty("engineering.design.realdesignvariables");

	final String avusBCTxt = Sorcer.getProperty("engineering.avus.bcTxt");

	ContextNode[] cNs;

	public AvusParams() {

		loadProperties("requestor.properties");
	}

	public static void main(String[] args) throws Exception {
		AvusParams av = new AvusParams();
		ContextNode[] cns = av.getContextNodes();
		System.out.println("cns data = " + cns[0].getData());
		
		DesignVariable[] dvs = (DesignVariable[]) cns[0].getData();
		
		Evaluator eval =  (Evaluator)dvs[0].getEvaluator();
		System.out.println("dv evaluator = " + dvs[0].getEvaluator());
		
		System.out.println("dv value = " + dvs[0].getValue());
		dvs[0].setValue(.159);
		System.out.println("dv value after set = " + dvs[0].getValue());
		dvs[0].incrementValue();
		System.out.println("RRRRRRRRRRRRR dv value after increment= " + dvs[0].getValue());
		dvs[0].decrementValue();
		System.out.println("RRRRRRRRRRRRRR dv value after decrement= " + dvs[0].getValue());
		
		
		dvs[3].setValue("911.00");
		dvs[4].setValue("912.00");
		
		System.out.println("dv name = " + dvs[5].getName()+ " dv Value before set = "+dvs[5].getValue());
		dvs[5].setValue("\"/home/kolonarm/replaced\"");
		System.out.println("dv name = " + dvs[5].getName()+ " dv Value after set = "+dvs[5].getValue());
		
		System.out.println("dv name = " + dvs[6].getName()+ " dv Value before set = "+dvs[6].getValue());
		//dvs[6].setValue(512.0);
		//System.out.println("dv name = " + dvs[6].getName()+ " dv Value after set = "+dvs[6].getValue());
		dvs[6].incrementValue();
		System.out.println("dv name = " + dvs[6].getName()+ " dv Value after increment = "+dvs[6].getValue());
		dvs[6].decrementValue();
		System.out.println("dv name = " + dvs[6].getName()+ " dv Value after decrement = "+dvs[6].getValue());
		
		System.out.println("dv name = " + dvs[7].getName()+ " dv Value before set = "+dvs[7].getValue());
		//dvs[7].setValue(39.5);
		//System.out.println("dv name = " + dvs[7].getName()+ " dv Value after set = "+dvs[7].getValue());
		dvs[7].incrementValue();
		System.out.println("dv name = " + dvs[7].getName()+ " dv Value after increment = "+dvs[7].getValue());
		dvs[7].decrementValue();
		System.out.println("dv name = " + dvs[7].getName()+ " dv Value after decrement = "+dvs[7].getValue());
		
		System.out.println("dv name = " + dvs[8].getName()+ " dv Value  = "+dvs[8].getValue());
		dvs[8].setValue(9.18);
		System.out.println("dv name = " + dvs[8].getName()+ " dv Value after set = "+dvs[8].getValue());
	}

	public ContextNode[] getContextNodes() throws NumberFormatException,
			ParException, ContextNodeException, IOException {

		// the first is the bcinputfile
		ContextNode fCN1 = null;

		// Create context nodes associated with Files needed to run AVUS
		String basedataURL = properties.getProperty("requestor.dataURL");
		String avusPath = properties.getProperty("requestor.avus.datapath");
		String dataURL = basedataURL + avusPath;
		System.out.println("dataURL = " + dataURL);
		// File Context Node for BCinputFile
		// File Context Node for BCinputFile
		String bcGen2InputFileName = properties
				.getProperty("requestor.paramtestdat");
		if (bcGen2InputFileName != null) {
			File bcGen2InputFile = new File(bcGen2InputFileName);
			fCN1 = createURLNode("AvusGen2BCData", bcGen2InputFile, dataURL,
					avusBCTxt);
			System.out.println(">>>BCDATA url " + dataURL);
		}

		// DesignVariable Context Node
		// This requires the addtion of filters to file context node, creation
		// of variables,
		// and finally designvariables

		AspectVariable[] depVarsGen2 = null;
		DesignVariable[] sDVGen2 = null;
		if (fCN1 != null) {
			AspectVariable[] indVarsGen2 = createIndepVars(fCN1);

			System.out.println("indVarsGen2 = " + indVarsGen2);
			sDVGen2 = DesignUtil.createSorcerDesignVariables(indVarsGen2);
			
			System.out.println("indVarsGen2 length= " + sDVGen2.length);
			double sSV = 1.0;
			try {
				for (int i = 0; i < sDVGen2.length; i++) {
					sDVGen2[i].setStepSize(new Double(sSV));
					System.out.println("dv name = "+sDVGen2[i].getName()+" dv value = " + sDVGen2[i].getValue());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ContextNode cnV = new ContextNode("Avus DV", sDVGen2, engRealDVariables);
		return new ContextNode[] { cnV };
	}

	public ContextNode createURLNode(String nodeName, File fileName,
			String base, String mimeType) {
		URL dataURL = null;
		try {
			dataURL = new URL(base + "/" + fileName);
			// dataURL = new URL(base + fileName);
		} catch (MalformedURLException e) {
			System.out.println("Cannot create URLNode with filename = "
					+ fileName + " base = " + base);
			e.printStackTrace();
		}
		return new ContextNode(nodeName, dataURL, mimeType);
	}

	public void loadProperties(String filename) {
		System.out.println("load:filename=" + filename);
		try {
			// check the class resource
			InputStream is = this.getClass().getResourceAsStream(filename);
			// check local resource
			if (is == null)
				is = (InputStream) (new FileInputStream(new File(filename)));
			if (is != null) {
				properties.load(is);
			} else {
				System.err.println("Not able to open stream on properties "
						+ filename);
				System.err.println("Requestor runner class=" + this.getClass());
				return;
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.err.println("Not able to create service requestor "
					+ this.getClass() + " properties");
		}
		return;
	}

	private AspectVariable[] createIndepVars(ContextNode cN) throws ContextNodeException, IOException {

		AspectVariable[] indepVars = null;
		// expose the independent variables in the context
		// create filters (dataItems) for each control surface
		//
		// csi
		//
		createFilter("cs1", "File", "Double", "2", "1", " ", cN);
		createFilter("cs2", "File", "Double", "4", "2", " ", cN);
		createFilter("cs3", "File", "Double", "6", "4", " ", cN);
		// create  bracket keyword filters
		Vector filter = new Vector();
		String filterName = "kw1";
		filter.addElement("Keyword Filter3");
		filter.addElement("Double");
		filter.addElement("[bracket keyword]");
		cN.addItem(filterName, filter);
		System.out.println("kw1 item value = "+cN.getItemValue("kw1"));
		// 
		Vector filter1 = new Vector();
		String filterName1 = "kw2";
		filter1.addElement("Keyword Filter3");
		filter1.addElement("Double");
		filter1.addElement("[QDP]");
		cN.addItem(filterName1, filter1);
		System.out.println("kw2 item value = "+cN.getItemValue("kw2"));
		
		// Filter for keyword, field, delimiter
		Vector filter2 = new Vector();
		String filterName2 = "kwwdel";
		filter2.addElement("Keyword Filter");
		filter2.addElement("String");
		filter2.addElement("home_dir");
		filter2.addElement(2);
		filter2.addElement("=");
		cN.addItem(filterName2, filter2);
		System.out.println("kwwdel item value = "+cN.getItemValue("kwwdel"));
		
		// Filter for keyword, field, delimiter
		Vector filter3 = new Vector();
		String filterName3 = "kwwdel3";
		filter3.addElement("Keyword Filter");
		filter3.addElement("Double");
		filter3.addElement("DESVAR, 3");
		filter3.addElement(3);
		filter3.addElement(",");
		cN.addItem(filterName3, filter3);
		System.out.println("kwwdel3 item value = "+cN.getItemValue("kwwdel3"));
		
		// Filter for keyword, field, delimiter, sub field, sub delimiter used to expose a value in a record structured as follows
		//set sweep 45.0;
		// here the keyword is "set sweep" the first delimiter is " "(space) and the field based on (space) delimiter is 3 that produces
		// 45.0; as the item. Need to strip off the ";" this is done by specifying the subfield of 1 and the subdelimiter as ";".
		Vector filter4 = new Vector();
		String filterName4 = "kwwdel4";
		filter4.addElement("Keyword Filter2");
		filter4.addElement("Double");
		filter4.addElement("set sweep");
		filter4.addElement(3);
		filter4.addElement(" ");
		filter4.addElement(1);
		filter4.addElement(";");
		cN.addItem(filterName4, filter4);
		System.out.println("kwwdel4 item value = "+cN.getItemValue("kwwdel4"));
		
		// Filter for keyword, linesAfter, field, delimiter - Enables exposing a field in a record n lines after the key word is found.
        //GPWG
        //35.0   26.0   
        //19.0   22.0
        //0.0   11.0    45.0   120.0
		// here the keyword is "GPWG" the linesAfter is "3" the filed is "4", the delimiter is " "(space). exposing "120."
		Vector filter5 = new Vector();
		String filterName5 = "kwwdel5";
		filter5.addElement("Keyword Filter4");
		filter5.addElement("Double");
		filter5.addElement("GPWG");
		filter5.addElement(3);
		filter5.addElement(4);
		filter5.addElement(" ");
		
		cN.addItem(filterName5, filter5);
		System.out.println("kwwdel5 item value = "+cN.getItemValue("kwwdel5"));
		//
		// create variables
		//
		try {

			// create Variables for the csi
			// (note: csi is Gaussian with mean=0.0 and std=0.005)
			//
			System.out.println("csi FilterNode = " + cN);
			GaussianDistribution gD = new GaussianDistribution(
					new Double("0.0"), new Double("0.0"), new Double("0.1"));
			CadDescription cD = new CadDescription(CaeConstants.SHAPE);
			AspectVariable cs1Var = new AspectVariable("cs1", cN,
					Var.DOUBLE, true, cD, gD);
			cs1Var.setEventInfo("cs1");
			AspectVariable cs2Var = new AspectVariable("cs2", cN,
					Var.DOUBLE, true, cD, gD);
			cs2Var.setEventInfo("cs2");
			AspectVariable cs3Var = new AspectVariable("cs3", cN,
					Var.DOUBLE, true, cD, gD);
			cs3Var.setEventInfo("cs3");

			AspectVariable kw1Var = new AspectVariable("kw1", cN,
					Var.STRING, true, cD, gD);
			kw1Var.setEventInfo("kw1");		
			AspectVariable kw2Var = new AspectVariable("kw2", cN,
					Var.STRING, true, cD, gD);
			kw2Var.setEventInfo("kw2");
			
			AspectVariable kwwdelVar = new AspectVariable("kwwdel", cN,
					Var.STRING, true, cD, gD);
			kwwdelVar.setEventInfo("kwwdel");
			
			
			AspectVariable kwwdel3Var = new AspectVariable("kwwdel3", cN,
					Var.STRING, true, cD, gD);
			kwwdel3Var.setEventInfo("kwwdel3");
			
			AspectVariable kwwdel4Var = new AspectVariable("kwwdel4", cN,
					Var.STRING, true, cD, gD);
			kwwdel4Var.setEventInfo("kwwdel4");
			
			AspectVariable kwwdel5Var = new AspectVariable("kwwdel5", cN,
					Var.STRING, true, cD, gD);
			kwwdel5Var.setEventInfo("kwwdel5");
			
			indepVars = new AspectVariable[] { cs1Var, cs2Var, cs3Var, kw1Var, kw2Var, kwwdelVar, kwwdel3Var, 
					kwwdel4Var, kwwdel5Var};

		} catch (Exception e) {
			e.printStackTrace();
		}
		return indepVars;
	}

	private void createFilter(String name, String ftype, String dtype,
			String line, String field, String delimeter, ContextNode fN) {
		Vector filter = new Vector();
		String filterName = name;
		filter.addElement(ftype);
		filter.addElement(dtype);
		filter.addElement(new Integer(line));
		filter.addElement(new Integer(field));
		filter.addElement(delimeter);
		fN.addItem(filterName, filter);
	}
}
