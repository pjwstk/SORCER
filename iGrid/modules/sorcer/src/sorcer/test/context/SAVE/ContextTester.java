package sorcer.test.context;

import java.net.URL;
import java.util.Enumeration;

import jgapp.util.Util;
import sorcer.core.SorcerConstants;
import sorcer.core.context.node.ContextNode;
import sorcer.service.Context;


public class ContextTester implements SorcerConstants {

    public static void main(String[] args) {
	ContextTester test = new ContextTester();
  	Context[] context = new Context[2];

	try{
	    context[0] = test.getContext();
	    System.out.println("\n***INPUT CONTEXT***\n"+context[0]);
	    System.out.println("\ncntx.getSubcontext(\"/test/AURORA Strategy\"):");
	    //>>>>>>>>>>>>>>>
	    ((Context)context[0]).setCPasRoot();
	    //<<<<<<<<<<<<<<<<<
	    context[1] = context[0].getSubcontext("/test/AURORA Strategy");
	    System.out.println("\n***RETURNED CONTEXT***\n"+context[1]);

	    System.out.println("\nsubCntxt.getMetadefinitions():");
	    Enumeration en = context[1].getMetadefinitions();
	    while (en.hasMoreElements())
		System.out.println("  "+en.nextElement());

	    System.out.println("\ncntx.getSubcontextWithAssoc(\"author|J.B. Good\"):");
	    context[1] = context[0].getSubcontextWithAssoc("author|J.B. Good");
	    System.out.println("\n***RETURNED CONTEXT***\n"+context[1]);

	    System.out.println("\nreturnedCntxt.getAssociations():");
	    Enumeration enu = context[1].getAssociations();
	    while (enu.hasMoreElements())
		System.out.println("  "+enu.nextElement());
	    
	}catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}

	try{
	    System.out.println("\ncntx.getSubcontextWithMetaAssoc(\"ft|autoshank|binary|solid\"):");
	    System.out.println("\n***RETURNED CONTEXT***\n"+context[0].getSubcontextWithMetaAssoc("ft|autoshank|binary|solid"));

	}catch (Exception e) {
	    e.printStackTrace();
	}

	try {
	    System.out.println("\ncntx.getSorcerNodesWithMetaAssoc(\"ft|autoshank|binary|solid\"):");
	    SorcerNode[] fNodes = context[0].getSorcerNodesWithMetaAssoc("ft|autoshank|binary|solid");
	    for (int i=0; i<fNodes.length; i++)
		System.out.println(fNodes[i]);

	}catch (Exception e) {
	    e.printStackTrace();
	}   
//  	// test getSorcerNodeWithFT
//  	System.out.println("\ngetSorcerNodeWithFT("+
//  			   SorcerNode.Sorcer_FPI_STRAT+"):\n" + 
//  			   context[0].
//  			   getSorcerNodeWithFT(SorcerNode.Sorcer_FPI_STRAT));

//  	//   there is no data node of this type:
//  	System.out.println("\ngetSorcerNodeWithFT("+
//  			   SorcerNode.Sorcer_AUTOSHANK_PARAMETERS+"):\n" + 
//  			   context[0].
//  			   getSorcerNodeWithFT(SorcerNode.Sorcer_AUTOSHANK_PARAMETERS));

//  	// test getAllSorcerNodesWithFT
//  	SorcerNode[] nodes = context[0].getAllSorcerNodesWithFT(SorcerNode.Sorcer_FPI_STRAT);
//  	int len = java.lang.reflect.Array.getLength(nodes);
//  	System.out.println("\ngetAllSorcerNodesWithFT("+
//  			   SorcerNode.Sorcer_FPI_STRAT+"):");
//  	System.out.println(nodes);	
//  	for (int i = 0; i < len; i++){
//  	    System.out.println("   " + nodes[i]);
//  	    System.out.println("\nSorcerNode.printContent():");
//  	    nodes[i].printContent();
//  	    System.out.println("");
//  	}

//  	//   there is no data node of this type:
//  	nodes = context[0].getAllSorcerNodesWithFT(SorcerNode.Sorcer_AUTOSHANK_PARAMETERS);
//  	if (nodes == null) len = 0;
//  	else len = java.lang.reflect.Array.getLength(nodes);
	    
//  	System.out.println("\ngetAllSorcerNodesWithFT("+
//  			   SorcerNode.Sorcer_AUTOSHANK_PARAMETERS+"):");
//  	System.out.println(nodes);	
//  	for (int i = 0; i < len; i++)
//  	    System.out.println("   " + nodes[i]);

//  	// test getSubcontextWithFT
//  	System.out.println("\ngetSubcontextWithFT("+
//  			   SorcerNode.Sorcer_FPI_STRAT+"):");
//  	System.out.println(context[0].getSubcontextWithFT(SorcerNode.Sorcer_FPI_STRAT,"abc"));

    }

    public Context getContext() throws Exception {

	Util.isDebugged = true;
	String testString;
	HashtableContext cntxt = new HashtableContext();
	
	Util.debug("\n***CURRENT CONTEXT***\n"+cntxt);
	cntxt.setName("Test");
	cntxt.setRootName("test");
	//>>>>>>>>>>>>>>>>>>>
	cntxt.setCPasRoot();
	//<<<<<<<<<<<<<<<<<<<
	cntxt.setAttribute("author");

  	testString = "Testing no-arg constructor";
	HashtableContext cntxt1 = new HashtableContext();	
	cntxt1.setRootName("test");
	//>>>>>>>>>>>>>>>>>>>
	cntxt1.setCPasRoot();
	//<<<<<<<<<<<<<<<<<<<
	SorcerNode DN00 = null;
	try{
	    DN00 = new SorcerNode("Autoshank solid file"
			       , new URL("http://Sorcer.crd.ge.com/Sorcer"
					 +"/data/someAutoshankFile"));
	    cntxt1.putValue("/test/geom/shank/OhSuzanna", DN00, "ft|autoshank|binary|solid");
	}catch (Exception e) {
	    System.out.println(testString+": FAILED");	    
	    System.out.println("\n***CURRENT CONTEXT***\n"+cntxt1);
	    e.printStackTrace();
	    System.exit(1);
	}
  	System.out.println(testString+": PASSED");

	SorcerNode DN0 = null;
  	testString = "Testing single arg constructor";
	try{
	    DN0 = new SorcerNode("Autoshank solid file"
			       , new URL("http://Sorcer.crd.ge.com/Sorcer"
					 +"/data/someAutoshankFile"));
	    cntxt.putValue("/test/geom/shank/", DN0, "ft|autoshank|binary|solid");
	    //cntxt.putValue("/test/geom/shank", DN0);
	    cntxt.addAttributeValue("/test/geom/shank", "author|J.B. Good");
	
	}catch (Exception e) {
	    System.out.println(testString+": FAILED");	    
	    System.out.println("\n***CURRENT CONTEXT***\n"+cntxt);
	    e.printStackTrace();
	    System.exit(1);
	}
  	System.out.println(testString+": PASSED");

	testString = "Testing getSubcontext";
	Context[] subCntxt = new Context[6];
	try {
	    String rN = "SubContext";

	    subCntxt[0]= cntxt.getSubcontext("geom/shank");
	    subCntxt[0].putValue(IN_PATH+"/Cycle","OUT/Output");
	    subCntxt[0].setRootName(rN);
	    Util.debug("******subcontext********\n"+subCntxt[0]);
	    
	    subCntxt[1]= cntxt.getSubcontext("geom/shank");
	    subCntxt[1].setRootName(rN);
	    ((HashtableContext)subCntxt[1]).setCPasRoot();
	    subCntxt[1].putValue(IN_PATH+"/Cycle","OUT/Output");

	    subCntxt[2]= cntxt.getSubcontext("geom/shank");
	    subCntxt[2].setRootName(rN);
	    ((HashtableContext)subCntxt[2]).setCPasRoot();
	    subCntxt[2].putValue(rN+CPS+IN_PATH+"/Cycle","OUT/Output");

	    subCntxt[3]= cntxt.getSubcontext("geom/shank");
	    subCntxt[3].setRootName(rN);
	    subCntxt[3].putValue(IN_PATH+"/Cycle","OUT/Output");

	    subCntxt[4]= cntxt.getSubcontext("geom/shank");
	    subCntxt[4].setRootName(rN);
	    ((HashtableContext)subCntxt[4]).setCPasRoot();
	    subCntxt[4].putValue(IN_PATH+"/Cycle","OUT/Output");
	    
	    if ( !(subCntxt[0].equals(subCntxt[1]) &&
		   subCntxt[0].equals(subCntxt[2]) &&
		   subCntxt[0].equals(subCntxt[3]) &&
		   subCntxt[0].equals(subCntxt[4])) )
		throw new Exception("subCntxt testing failure\n"+
				    "*****subcntxt 0*****\n"+subCntxt[0]+
				    "*****subcntxt 1*****\n"+subCntxt[1]+
				    "*****subcntxt 2*****\n"+subCntxt[2]+
				    "*****subcntxt 3*****\n"+subCntxt[3]+
				    "*****subcntxt 4*****\n"+subCntxt[4]);

	    subCntxt[5]= cntxt.getSubcontext("test");
	    
	    if ( !(subCntxt[5].equals(cntxt)))
		throw new Exception("subCntxt testing failure\n"+
				    "*****subcntxt *****\n"+subCntxt[5]+
				    "*****orig Cntxt*****\n"+cntxt);
	}catch (Exception e) {
	    System.out.println(testString+": FAILED");	    
	    e.printStackTrace();
	    System.exit(1);
	}
  	System.out.println(testString+": PASSED");
	

  	testString = "Testing relative context paths";
	SorcerNode DN1 = null;
	try{
	    cntxt.putEmptyValue("test/geom");
	    cntxt.setCP("geom");
	    Util.debug("Current context path:"+cntxt.getCP()+"\n");
	    DN1 = new SorcerNode("UG part file"
			       , new URL("http://Sorcer.crd.ge.com/Sorcer"
					 +"/data/someUG.prt"));
	    cntxt.putValue("airfoil", DN1);
	    cntxt.addAttributeValue("airfoil", "ft|autoshank|binary|solid");
	    cntxt.addAttributeValue("airfoil", "author|Evil Catbert");

	    // let's try getting subcontexts again
	    String rN = "SubContext";

	    subCntxt[0]= cntxt.getSubcontext("shank");
	    subCntxt[0].putValue(IN_PATH+"/Cycle","OUT/Output");
	    subCntxt[0].setRootName(rN);

	    subCntxt[1]= cntxt.getSubcontext("shank");
	    subCntxt[1].setRootName(rN);
	    ((HashtableContext)subCntxt[1]).setCPasRoot();
	    subCntxt[1].putValue(IN_PATH+"/Cycle","OUT/Output");

	    subCntxt[2]= cntxt.getSubcontext("shank");
	    subCntxt[2].setRootName(rN);
	    ((HashtableContext)subCntxt[2]).setCPasRoot();
	    subCntxt[2].putValue(rN+CPS+IN_PATH+"/Cycle","OUT/Output");

	    subCntxt[3]= cntxt.getSubcontext("shank");
	    subCntxt[3].setRootName(rN);
	    subCntxt[3].putValue(IN_PATH+"/Cycle","OUT/Output");

	    subCntxt[4]= cntxt.getSubcontext("shank");
	    subCntxt[4].setRootName(rN);
	    ((HashtableContext)subCntxt[4]).setCPasRoot();
	    subCntxt[4].putValue(IN_PATH+"/Cycle","OUT/Output");
	    
	    if ( !(subCntxt[0].equals(subCntxt[1]) &&
		   subCntxt[0].equals(subCntxt[2]) &&
		   subCntxt[0].equals(subCntxt[3]) &&
		   subCntxt[0].equals(subCntxt[4])) )
		throw new Exception("subCntxt testing failure\n"+
				    "*****subcntxt 0*****\n"+subCntxt[0]+
				    "*****subcntxt 1*****\n"+subCntxt[1]+
				    "*****subcntxt 2*****\n"+subCntxt[2]+
				    "*****subcntxt 3*****\n"+subCntxt[3]+
				    "*****subcntxt 4*****\n"+subCntxt[4]);

	    subCntxt[5]= cntxt.getSubcontext("test");
	    
	    if ( !(subCntxt[5].equals(cntxt)))
		throw new Exception("subCntxt testing failure\n"+
				    "*****subcntxt *****\n"+subCntxt[5]+
				    "*****orig Cntxt*****\n"+cntxt);


	}catch (Exception e) {
	    System.out.println(testString+": FAILED");	    
	    System.out.println("\n***CURRENT CONTEXT***\n"+cntxt);	    
	    e.printStackTrace();
	    System.exit(1);
	}
  	System.out.println(testString+": PASSED");



	ContextNode DN2 = null;
	try{
	    DN2 = new ContextNode("AURORA Strategy File"
			       , new URL("http://Sorcer.crd.ge.com/Sorcer"
					 +"/data/strat.txt"));
	    cntxt.putValue("test/AURORA Strategy", DN2, "ft|fpi|text|strat");
	}catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}

  	Util.debug("Current context path:"+cntxt.getCP()+"\n");
	Util.debug("\n***CONTEXT***\n"+cntxt);


	System.out.println("\ncntxt.getMetadefinitions():");
	Enumeration enu = cntxt.getMetadefinitions();
	while (enu.hasMoreElements())
	    System.out.println("  "+enu.nextElement());

	System.out.println("\ncntxt.getPaths():");
	enu = cntxt.contextPaths();
	while (enu.hasMoreElements())
	    System.out.println("  "+enu.nextElement());

	System.out.println("\ncntxt.getMetavalue(\"/test/geom/airfoil\",\"ft\"):");
	System.out.println("  "+cntxt.getMetavalue("/test/geom/airfoil","ft"));	
	System.out.println("\ncntxt.getMetavalue(\"/test/geom/airfoil\",\"author\"):");
	System.out.println("  "+cntxt.getMetavalue("/test/geom/airfoil","author"));	

	System.out.println("\ncntxt.getAttributes():");
	enu = cntxt.getAttributes();
	while (enu.hasMoreElements())
	    System.out.println("  "+enu.nextElement());

	System.out.println("\ncntxt.getMetaattributes():");	
	enu = cntxt.compositeAttributes();
	while (enu.hasMoreElements())
	    System.out.println("  "+enu.nextElement());

	System.out.println("\ncntxt.getAttributeValue(\"/test/geom/airfoil\",\"ft\"):");
	System.out.println("  "+cntxt.getAttributeValue("/test/geom/airfoil","ft"));

	System.out.println("\ncntxt.getAttributeValue(\"/test/geom/airfoil\",\"author\"):");
	System.out.println("  "+cntxt.getAttributeValue("/test/geom/airfoil","author"));

	System.out.println("\ncntxt.getAttributeValue(\"/test/geom/airfoil\",\"format\"):");
	System.out.println("  "+cntxt.getAttributeValue("/test/geom/airfoil","format"));

	System.out.println("\ncntxt.getAttributeValues(\"/test/geom/airfoil\"):");
	enu = cntxt.getAttributeValues("/test/geom/airfoil");
	while (enu.hasMoreElements())
	    System.out.println("  "+enu.nextElement());

	System.out.println("\ncntxt.getAttributes(\"/test/geom/airfoil\"):");
	enu = cntxt.getAttributes("/test/geom/airfoil");
	while (enu.hasMoreElements())
	    System.out.println("  "+enu.nextElement());

	System.out.println("\ncntxt.getAttributeValues():");
	enu = cntxt.getAttributeValues();
	while (enu.hasMoreElements())
	    System.out.println("  "+enu.nextElement());
	    
	System.out.println("\ncntxt.getAssociations():");
	enu = cntxt.getAssociations();
	while (enu.hasMoreElements())
	    System.out.println("  "+enu.nextElement());

	enu = cntxt.getAssociations("/test/geom/airfoil");
	System.out.println("\ncntxt.getAssociations(\"/test/geom/airfoil\"):");
	while (enu.hasMoreElements())
	    System.out.println("  "+enu.nextElement());

	System.out.println("\ncntxt.getMetaAssociations():");
	enu = cntxt.getMetaAssociations();
	while (enu.hasMoreElements())
	    System.out.println("  "+enu.nextElement());
	    
	System.out.println("\ncntxt.getMetaAssociations(\"/test/geom/airfoil\"):");
	enu = cntxt.getMetaAssociations("/test/geom/airfoil");
	while (enu.hasMoreElements())
	    System.out.println("  "+enu.nextElement());
	    
	System.out.println("\ncntxt.getAttributeValues(\"/test/geom/\"):");
	enu = cntxt.getAttributeValues("/test/geom/");
	while (enu.hasMoreElements())
	    System.out.println("  "+enu.nextElement());

	System.out.println("\ncntxt.metavalues():");
	System.out.println("  "+cntxt.metavalues());

	return (Context)cntxt;
    }
}


