package fiper.test.service;

import fiper.service.*;
import java.util.*;
import java.net.*;
import java.io.*;
import fiper.service.reliability.*;

public class RealDesignVariableTest {

    public static final void main(String[] args) {

	DataNode dN1=null;
	double dco=-5.0; 
	double dca=3.0;

	try {
	    //URL myUrl=new URL("http://caleb.crd.ge.com:8080/fiper/data"
	    //+"/service/genericDataNodeData.txt");
	    //System.out.println(myUrl.getFile());

	    dN1=new DataNode("Test DataNode", new URL("http://mars.crd.ge.com:"
						      +"8893/fiper/data/service"
						      +"/genericDataNodeData"
						      +".txt")
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

	try {
	    System.out.println("Main: Reading DataNodeValues...");
	    
	    double dco1=Double.parseDouble((String)dN1.getPatternValue("DCO"));
	    double dca1=Double.parseDouble((String)dN1.getPatternValue("DCA"));

	    System.out.println("Main: Read DCO from DataNode: "+dco1);
	    System.out.println("Main: Read DCA from DataNode: "+dca1);

	} catch (Exception e) {
	    e.printStackTrace();
	}
	
	// user input

	/*double dca2=0.0;
	  double dco2=0.0;
	  try {
	  InputStream is=System.in;
	  InputStreamReader isr=new InputStreamReader(is);
	  BufferedReader br=new BufferedReader(isr);
	  
	  System.out.println("Main: Input DCO:");
	  dco2=(new Double(br.readLine()).doubleValue());
	  System.out.println("Main: Input DCA:");
	  dca2=(new Double(br.readLine()).doubleValue());
	  System.out.println("Main: New DCO: "+dco2);
	  System.out.println("Main: New DCA: "+dca2);
	  
	  br.close();
	  isr.close();
	  is.close();
	  } catch (Exception e) {
	  e.printStackTrace();
	  }
	*/

	try {
	    System.out.println("Main: dN1.isDataSettable(): "
			       +dN1.isDataSettable());
	    System.out.println("Main: Attempting to set values: "
			       +(dco)+" and "+(dca));
	    //System.out.println("Main: Attempting to set values: "
	    //+dco2+" and "+dca2);

	    dN1.setValue("DCO",(new Double(dco)).toString());
	    Thread.sleep(100);

	    dN1.setValue("DCA",(new Double(dca)).toString());
	    Thread.sleep(100);
	    
	    //dN1.setItemValue("DCO",(new Double(dco2)).toString());
	    //dN1.setItemValue("DCA",(new Double(dca2)).toString());

	    System.out.println("Main: Getting DCO :"+dN1.getPatternValue("DCO"));
	    Thread.sleep(100);

	    System.out.println("Main: Getting DCA :"+dN1.getPatternValue("DCA"));
	    Thread.sleep(100);

	    //System.out.println("Main: Set DCO...reading DCO :"
	    //+dN1.getItemValue("DCO"));
	    //System.out.println("Main: Set DCA...reading DCA :"
	    //+dN1.getItemValue("DCA"));

	} catch (Exception e) {
	    e.printStackTrace();
	}

	//variable
	Double myD1=new Double(100.0);
	Double myD2=new Double(3.33);
	Double myD3=new Double(99.123);

	GaussianDistribution g1=new GaussianDistribution(myD3, myD1, myD2);

	AspectVariable v1=null;
	try {
	    v1=new AspectVariable("v1", dN1, "DCO", FiperVariable.DOUBLE, g1);
	    System.out.println("Main: v1.getValue(FiperDistribution.MEAN): "
			       +v1.getValue(FiperDistribution.MEAN));
	} catch (Exception e) {
	    e.printStackTrace();
	}

	// design varaibles
	RealDesignVariable rDV1=new RealDesignVariable("rDV1",
						       new Double("1.0"), 
						       new Double("10.0"), 
						       new Double("-10.0"), 
						       new Double("0.123456"));
	
	System.out.println("Main: getValue(): "+rDV1.getValue());
	System.out.println("Main: Setting rDV1 to 44.4...");
	try {
	    rDV1.setValue(new Double(44.4));
	} catch (Exception e) {
	    e.printStackTrace();
	}
	System.out.println("Main: getValue(): "+rDV1.getValue());
	System.out.println("Main: DesignVariableType(): "
			   +rDV1.getDesignVariableType());
	System.out.println("Main: getElementClassName(): "+rDV1.getValueType());
	System.out.println("Main: getValue(): "+rDV1.getValue());
	System.out.println("Main: isElementMathAsUsual(): "
			   +rDV1.isElementMathAsUsual());
	System.out.println("Main: getCharacteristicValue(): "
			   +rDV1.getCharacteristicValue());
	System.out.println("Main: getLowerBound(): "+rDV1.getLowerBound());
	System.out.println("Main: getUpperBound(): "+rDV1.getUpperBound());
	
	try {
	    System.out.println("\n\nMain: Going to invoke addDep.");
	    v1.addDependency(FiperDistribution.MEAN, rDV1);
	    System.out.println("Main: done");
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
	    System.out.println("\n\nMain: Setting rDV2 to 222.222 ...");
	    rDV2.setValue(new Double(222.222));
	    System.out.println("Main: DataNode.getItemValue(\"DCO\"):"
			       +dN1.getPatternValue("DCO"));

	    System.out.println("Main: Adding dependency of rDV2 "+
			       "on REALIZATION...");
	    Observable[] myOA={(Observable)rDV1, (Observable)rDV2};
	    System.out.println("Main: instance of FiperVariable: "
			       +(myOA[0] instanceof FiperVariable));
	    System.out.println("Main: instance of FiperVariable: "
			       +(myOA[1] instanceof FiperVariable));
	    v1.addDependency(FiperDistribution.REALIZATION, myOA
			     , "Math.pow(fv[0],2)+2*fv[1]");
	    System.out.println("Main: Done adding dependency.");

	    System.out.println("Main: DataNode.getItemValue(\"DCO\"):"
			       +dN1.getPatternValue("DCO"));
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
}
