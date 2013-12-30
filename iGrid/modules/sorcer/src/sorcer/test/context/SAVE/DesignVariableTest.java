package fiper.test.service;

import fiper.service.*;
import java.util.*;
import java.net.*;
import java.io.*;
import fiper.service.reliability.*;

public class DesignVariableTest {

    public static final void main(String[] args) {

	// create DataNode and dataItems
	DataNode dN1=DesignVariableTest.createDataNodeAndDataItems();

	// read and write to DataNode
	try {
	    System.out.println("Main: Reading DataNodeValues...");
	    
	    double dco=Double.parseDouble((String)dN1.getPatternValue("DCO"));
	    double dca=Double.parseDouble((String)dN1.getPatternValue("DCA"));

	    System.out.println("Main: Read DCO from DataNode: "+dco);
	    System.out.println("Main: Read DCA from DataNode: "+dca);

	    System.out.println("Main: dN1.isDataSettable(): "+dN1.isDataSettable());
	    System.out.println("Main: Attempting to set values to: "
			       +(-1.0)+" and "+(-2.0));

	    dN1.setValue("DCO",(new Double(-1.0)).toString());
	    Thread.sleep(1000);
	    dN1.setValue("DCA",(new Double(-2.0)).toString());
	    Thread.sleep(1000);
	    
	    System.out.println("Main: Getting DCO :"+dN1.getPatternValue("DCO"));
	    System.out.println("Main: Getting DCA :"+dN1.getPatternValue("DCA"));

	} catch (Exception e) {
	    e.printStackTrace();
	}

	//variable
	System.out.println("Main: Creating GaussianDistribution (99.123,100,3.33)<=>"
			   +"(realization, mean, std)...");

	Double mean=new Double(100.0);
	Double std=new Double(3.33);
	Double realization=new Double(99.123);

	GaussianDistribution g1=new GaussianDistribution(realization, mean, std);
	try {
	    System.out.println("Main: g1.getDistParam(\"MEAN\"):"
			       + g1.getDistParam(FiperDistribution.MEAN));
	    System.out.println("Main: g1.getDistParam(\"STANDARD_DEVIATION\"):"
			       + g1.getDistParam(FiperDistribution.STANDARD_DEVIATION));
	    System.out.println("Main: g1.getDistParam(\"REALIZATION\"):"
			       + g1.getDistParam(FiperDistribution.REALIZATION));
	    
	    System.out.println("Main: Creating Variable with name \"v1\", DataNode "
			       +"dN1, dataItem \"DCO\", and the GaussianDistribution...");
	} catch (Exception e) {
	    e.printStackTrace();
	}

	AspectVariable v1=null;
	try {
	    v1=new AspectVariable("v1", dN1, "DCO", FiperVariable.DOUBLE, g1);

	    System.out.println("Main: v1.getValue(FiperDistribution.MEAN): "
			   +v1.getValue(FiperDistribution.MEAN));
	    
	    System.out.println("Main: v1.getValue(FiperDistribution.STANDARD_DEVIATION): "
			       +v1.getValue(FiperDistribution.STANDARD_DEVIATION));
	    
	    System.out.println("Main: v1.getValue(FiperDistribution.REALIZATION): "
			       +v1.getValue(FiperDistribution.REALIZATION));

	} catch (Exception e) {
	    e.printStackTrace();
	}
	    
	// design varaibles
	System.out.println("Main: Creating rDV1...");
	RealDesignVariable rDV1=new RealDesignVariable("rDV1",
						       new Double("1.0"), 
						       new Double("10.0"), 
						       new Double("-10.0"), 
						       new Double("0.123456"));
	
	System.out.println("Main: rDV1.getValue(): "+rDV1.getValue());
	System.out.println("Main: rDV1.DesignVariableType(): "
			   +rDV1.getDesignVariableType());
	System.out.println("Main: rDV1.getElementClass(): "+rDV1.getValueType());
	System.out.println("Main: rDV1.getValue(): "+rDV1.getValue());
	System.out.println("Main: rDV1.isElementMathAsUsual(): "
			   +rDV1.isElementMathAsUsual());
	System.out.println("Main: rDV1.getCharacteristicValue(): "
			   +rDV1.getCharacteristicValue());
	System.out.println("Main: rDV1.getLowerBound(): "+rDV1.getLowerBound());
	System.out.println("Main: rDV1.getUpperBound(): "+rDV1.getUpperBound());

	System.out.println("Main: Setting rDV1 to 44.4...");
	try {
	    rDV1.setValue(new Double(44.4));
	    System.out.println("Main: rDV1.getValue(): "+rDV1.getValue());
	} catch (Exception e) {
	    e.printStackTrace();
	}
	
	try {
	    System.out.println("\nMain: v1.addDependency(): Linking rDV1 with"
			       +"MEAN of v1...(one-to-one)...");
	    v1.addDependency(FiperDistribution.MEAN, rDV1);
	    System.out.println("Main: Done linking rDV1 to MEAN of v1.");
	} catch (Exception e) {
	    e.printStackTrace();
	}

	//trying the model-view-controller
	System.out.println("Main: Trying the model-view-controller...");
	System.out.println("Main: Setting rDV1 to 123.45...");
	try {
	    rDV1.setValue(new Double(123.45));
	} catch (Exception e) {
	    e.printStackTrace();
	}
	System.out.println("Main: Done setting rDV1.");
	System.out.println("Main: rDV1.getValue(): "+rDV1.getValue());
	try {
	    System.out.println("Main: v1.getValue(FiperDistribution.MEAN): "
			       +v1.getValue(FiperDistribution.MEAN));
	} catch (Exception e) {
	    e.printStackTrace();
	}


	// real dv2
	RealDesignVariable rDV2=new RealDesignVariable("rDV2",
						       new Double("21.0"), 
						       new Double("210.0"), 
						       new Double("-210.0"), 
						       new Double("20.123456"));
	try {
	    System.out.println("\n\nMain: Setting rDV1 to 111.111 ...");
	    rDV1.setValue(new Double(111.111));
	    System.out.println("Main: Setting rDV2 to 222.222 ...");
	    rDV2.setValue(new Double(222.222));
	    System.out.println("Main: DataNode.getItemValue(\"DCO\"):"
			       +dN1.getPatternValue("DCO"));
	    System.out.println("Main: Adding dependency of rDV2 on REALIZATION of v1"
			       +"...");
	    Observable[] myOA={(Observable)rDV1, (Observable)rDV2};
	    System.out.println("Main: javaExpression: REALIZATION="
			       +"Math.pow(fv0,2)+2*fv1.");
	    v1.addDependency(FiperDistribution.REALIZATION, myOA, 
			     "Math.pow(fv0,2)+2*fv1");
	    System.out.println("Main: Done adding dependency.");

	    System.out.println("Main: DataNode.getItemValue(\"DCO\"):"
			       +dN1.getPatternValue("DCO"));
	    System.out.println("Main: rDV2.getValue() :"+rDV2.getValue());
	    System.out.println("Main: Setting rDV2 from 21.0 to 31.0...");
	    rDV2.setValue(new Double(31.0));
	    System.out.println("Main: Done setting rDV2\nrDV2.getValue(): "
			       +rDV2.getValue());
	    System.out.println("Main: v1.getValue(REALIZATION): "
			       +v1.getValue(FiperDistribution.REALIZATION));
	    System.out.println("Main: DataNode.getItemValue(\"DCO\"):"
			       +dN1.getPatternValue("DCO"));

	    
	} catch (Exception e) {
	    e.printStackTrace();
	}

	
    }
    
    public static DataNode createDataNodeAndDataItems() {
	DataNode dN1=null;
	try {
	    dN1=new DataNode("Test DataNode", new URL("http://caleb.crd.ge.com:"
						      +"8080/fiper/data/service/genericDataNodeData.txt")
			     , DataNode.TYPE_IN, DataNode.FIPER_UNKNOWN);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	
	Vector itemVect1=new Vector();
	itemVect1.addElement("File");
	itemVect1.addElement("Double");
	itemVect1.addElement(new Integer(2));
	itemVect1.addElement(new Integer(2));
	itemVect1.addElement(";");
	dN1.addItem("DCO",itemVect1);

	Vector itemVect2=new Vector();
	itemVect2.addElement("File");
	itemVect2.addElement("Double");
	itemVect2.addElement(new Integer(4));
	itemVect2.addElement(new Integer(2));
	itemVect2.addElement(";");
	dN1.addItem("DCA",itemVect2);
	return dN1;
    }
}
