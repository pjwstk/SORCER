/* ************************************************************************
 *
 * Name:        AvusOutput.java
 *
 * Created:     R.M. Kolonay 7 March 2007
 *
 * Revision history:
 *
 *
 * Air Force Research Lab - Air Vehicle Directorate
 * *************************************************************************/

package sorcer.test.eval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Serializable;
import java.util.Vector;

import org.lsmp.djep.xjep.function.Sum;
import org.nfunk.jep.JEP;

import sorcer.core.context.ServiceContext;
import sorcer.core.context.node.ContextNode;


/**
 * Implementation of the AvusOutput Class
 * 
 * @author R.M. Kolonay
 * @version %I%, %G%
 * @since JDK1.4
 */

public class AvusOutput implements Serializable {

	// Keywords used in the TreffzPlane File

	public static final String integratedforcesS = "INTEGRATED FORCE AND MOMENT RESULTS";

	public static final String forcesallpatchesS = "FORCES AND MOMENTS DUE TO ALL PATCHES WITH FORCE ACCOUNTING";

	public static final String pressureS = "PRESSURE";

	public static final String frictionS = "FRICTION";

	public static final String totalS = "TOTAL";

	public static final String clS = "CL";

	public static final String cdS = "CD";

	public static final String cyS = "CY";

	public static final String cmpitchS = "CM PITCH";

	public static final String cmyawS = "CM YAW";

	public static final String cmrollS = "CM ROLL";

	public static final String windaxisS = "WIND-AXIS SYSTEM";

	public static final String stabilityaxisS = "STABILITY-AXIS SYSTEM";

	public static final String bodyaxisS = "BODY-AXIS SYSTEM";

	public static final String forcesduetopatchS = "FORCES AND MOMENTS DUE TO PATCH";

	public static final String one = "1";
		
	public static final String unitssystem = "UNITS SYSTEM";
	
	public static final String referenceconditions = "REFERENCE CONDITIONS";

	public static final String initialconditions = " INITIAL CONDITIONS";
	
	public static final String diagnosticinfo = "DIAGNOSTIC INFORMATION";

	public static final String coefficientswrt = "COEFFICIENTS WITH RESPECT TO THE";
	
	public static final String computationtimings = "COMPUTATIONAL TIMINGS";
	
	public static final String referencearea = "REFERENCE AREA";
	public static final String gasConst = "Gas Const.";
	public static final String gamma = "Gamma";
	public static final String gravity = "Gravity";
	public static final String density = "Density";
	public static final String press = "Pressure";
	public static final String temp = "Temperature";
	public static final String soundspeed ="Sound Speed";
	public static final String enthalpy = "Enthalpy";
	public static final String machnumber="Mach Number";
	public static final String alpha = "Alpha";
	public static final String beta = "Beta";
	public static final String renumber = "Re/L Number";
	public static final String dynamicPressure ="Dyn. Press.";

	// fields
	// file names
	private double gasConstRef;
	private double gammaRef;
	private double densityRef;
	private double gravityRef;
	private double pressureRef;
	private double tempRef;
	private double soundspeedRef;
	private double enthalpyRef;
	private double machRef;
	private double alphaRef;
	private double betaRef;
	private double relnumberRef;
	private double dynPressRef;
	private double areaRef;
	
	private double gasConstInit;
	private double gammaInit;
	private double densityInit;
	private double pressureInit;
	private double tempInit;
	private double soundspeedInit;
	private double enthalpyInit;
	private double machInit;
	private double alphaInit;
	private double betaInit;
	private double relnumberInit;
	
	private double windaxisCl;

	private double windaxisCd;

	private double windaxisCy;

	private double windaxisCmP;

	private double windaxisCmY;

	private double windaxisCmR;


	private double uVel;

	private double vVel;

	private double wVel;

	private double pressure;

	private double temperature;

	private double rGasConst;

	private double trefftzPlaneXloc;

	private double cDi;

	private double cDi2;

	private Vector windAxisCLpus = new Vector();
	private Vector windAxisLpus = new Vector();
	
	private Vector windAxisCDpus = new Vector();
	private Vector windAxisDpus = new Vector();
	
	private Vector windAxisCYpus = new Vector();

	private Vector windAxisCMPpus = new Vector();

	private Vector windAxisCMYpus = new Vector();

	private Vector windAxisCMRpus = new Vector();

	private Vector bodyAxisCLpus = new Vector();

	private Vector bodyAxisCDpus = new Vector();

	private Vector bodyAxisCYpus = new Vector();

	private Vector bodyAxisCMPpus = new Vector();

	private Vector bodyAxisCMYpus = new Vector();

	private Vector bodyAxisCMRpus = new Vector();

	private Vector stabilityAxisCLpus = new Vector();

	private Vector stabilityAxisCDpus = new Vector();

	private Vector stabilityAxisCYpus = new Vector();

	private Vector stabilityAxisCMPpus = new Vector();

	private Vector stabilityAxisCMYpus = new Vector();

	private Vector stabilityAxisCMRpus = new Vector();

	private ContextNode trefftzContextNode;

	public AvusOutput() {

	}

	public AvusOutput(File avusOutputFile) throws IOException {

		// populate fields
		populateFields(avusOutputFile);
	}

	public void populateFields(File avusOutputFile) throws IOException {
		InputStream iStream = (InputStream) (new FileInputStream(avusOutputFile));
		LineNumberReader reader = new LineNumberReader(new BufferedReader(
				new InputStreamReader(iStream)));
		// populate fields
		populateFields(reader);
		dimensionalizeFields();
	}


	private Double fieldParser(String field1)
	{
		int spcidx = field1.indexOf(" ");
		String valstr = null;
		if(spcidx != -1)
		{
			valstr = field1.substring(0, spcidx).trim();
		}
		else
		{
			valstr = field1;
		}
			
		return (new Double(valstr).doubleValue());
	}
	
	public void populateFields(LineNumberReader reader)
			throws NumberFormatException, IOException {
		//
		// populate the fields

		String record;
		int i = 0;
		while ((record = reader.readLine()) != null) {

			// convert the record to a string first
			record = record.trim();
			if (record.startsWith(referencearea)){
				String[] fields = record.split(":", 2);
				this.areaRef = fieldParser(fields[1].trim());
			}
			if (record.equalsIgnoreCase(referenceconditions))
			{
				boolean readblock = true;
				while (readblock) 
				{
					record = reader.readLine().trim();
					String[] fields = record.split(":", 2);
					if(fields[0].trim().equalsIgnoreCase(gasConst))
						this.gasConstRef=fieldParser(fields[1].trim());
					if(fields[0].trim().equalsIgnoreCase(gamma)) 
						this.gammaRef=fieldParser(fields[1].trim());	
					if(fields[0].trim().equalsIgnoreCase(gravity)) 
						this.gravityRef=fieldParser(fields[1].trim());
					if(fields[0].trim().equalsIgnoreCase(density)) 
						this.densityRef=fieldParser(fields[1].trim());
					if(fields[0].trim().equalsIgnoreCase(press)) 
						this.pressureRef=fieldParser(fields[1].trim());
					if(fields[0].trim().equalsIgnoreCase(temp)) 
						this.tempRef=fieldParser(fields[1].trim());
					if(fields[0].trim().equalsIgnoreCase(soundspeed)) 
						this.soundspeedRef=fieldParser(fields[1].trim());
					if(fields[0].trim().equalsIgnoreCase(enthalpy)) 
						this.enthalpyRef=fieldParser(fields[1].trim());
					if(fields[0].trim().equalsIgnoreCase(machnumber)) 
						this.machRef=fieldParser(fields[1].trim());
					if(fields[0].trim().equalsIgnoreCase(alpha)) 
						this.alphaRef=fieldParser(fields[1].trim());
					if(fields[0].trim().equalsIgnoreCase(beta)) 
						this.betaRef=fieldParser(fields[1].trim());
					if(fields[0].trim().equalsIgnoreCase(renumber)) 
						this.relnumberRef=fieldParser(fields[1].trim());
					if(fields[0].trim().equalsIgnoreCase(dynamicPressure))
					{
						this.dynPressRef=fieldParser(fields[1].trim());
						// assuming Dyn. Press. last record in the block
						readblock = false;
					}
				}			
			}
				if (record.equalsIgnoreCase(initialconditions)){
					boolean readblock = true;
					while (readblock) {
						record = reader.readLine().trim();
						String[] fields = record.split(":", 2);
						if(fields[0].trim().equalsIgnoreCase(density)) 
							this.densityInit=fieldParser(fields[1].trim());	
						if(fields[0].trim().equalsIgnoreCase(press)) 
							this.pressureInit=fieldParser(fields[1].trim());	
						if(fields[0].trim().equalsIgnoreCase(temp)) 
							this.tempInit=fieldParser(fields[1].trim());
						if(fields[0].trim().equalsIgnoreCase(soundspeed)) 
							this.soundspeedInit=fieldParser(fields[1].trim());
						if(fields[0].trim().equalsIgnoreCase(enthalpy)) 
							this.enthalpyInit=fieldParser(fields[1].trim());
						if(fields[0].trim().equalsIgnoreCase(machnumber)) 
							this.machInit=fieldParser(fields[1].trim());
						if(fields[0].trim().equalsIgnoreCase(alpha)) 
							this.alphaInit=fieldParser(fields[1].trim());
						if(fields[0].trim().equalsIgnoreCase(beta)) {
							this.betaInit=fieldParser(fields[1].trim());
//							 assuming beta last record in the block
							readblock = false;
						}	
					}
				}
			if (record.equalsIgnoreCase(forcesallpatchesS)) 
			{
				boolean readblock = true;
				int j = i;
				int tt = 0;
				while (readblock) 
				{
					record = reader.readLine().trim();
					// System.out.println("block record = "+record);
					String[] fields = record.split("\\s", 2);
					if (fields[0].trim().equalsIgnoreCase(("TOTAL"))) 
					{
						String[] fields2 = fields[1].trim().split("\\s", 2);
						// System.out.println("fields"+fields[0]+">"+fields[1].trim()+"<");
						// System.out.println("fields2<"+fields2[0]+">"+fields2[3]+"<"+fields2[4]+">");
						// System.out.println("fields2a<"+fields2[0]+">"+fields2[1]+"<"+fields2[2]+">");
						if (tt == 0) 
						{
							windaxisCl = new Double(fields2[0].trim()).doubleValue();
							String[] fields3 = fields2[1].trim().split("\\s", 2);
							windaxisCd = new Double(fields3[0].trim()).doubleValue();
							String[] fields4 = fields3[1].trim().split("\\s", 2);
							windaxisCy = new Double(fields4[0].trim()).doubleValue();
							tt = 1;
						} 
						else if (tt == 1) 
						{
							windaxisCmP = new Double(fields2[0].trim()).doubleValue();
							String[] fields3 = fields2[1].trim().split("\\s", 2);
							windaxisCmY = new Double(fields3[0].trim()).doubleValue();
							String[] fields4 = fields3[1].trim().split("\\s", 2);
							windaxisCmR = new Double(fields4[0].trim()).doubleValue();
							tt = 0;
							readblock = false;
						}
					}
					j++;
					// System.out.println(readblock);
				}
			}
			if (record.startsWith(forcesduetopatchS)) {
				//System.out.println("record = "+record);
				
					 String[] panelid = record.split("[\\sA-Za-z:\\s]*");
					 //System.out.println(panelid.length);
					//System.out.println("panelid1 "+panelid[0]+">"+panelid[1]+"<"+panelid.length);
					//String[] numid = panelid[1].split("[0-9]",1);
					//System.out.println("numsplit length = "+numid.length);
				if (panelid.length >0 ) {
					panelid = record.split("[\\sA-Za-z:\\s]*",2);
					//System.out.println("panelid2 "+panelid[0]+">"+panelid[1]+"<");
					int cpanelid = (new Integer(panelid[1].trim())).intValue();
					//System.out.println ("Found panel with id "+cpanelid);
					boolean readpatchblock = true;
					while (readpatchblock) {
						record = reader.readLine().trim();
						if (record.equalsIgnoreCase(windaxisS)) {
							//System.out.println("wind axis record = "+record);
							boolean readwindaxisblock = true;
							while (readwindaxisblock) {
								record = reader.readLine().trim();
								if (record.startsWith("CL")) {
									boolean readclcdcyblock = true;

									while (readclcdcyblock) {
										record = reader.readLine().trim();
										String[] fields = record.split("\\s+",
												4);
										if (fields[0].equalsIgnoreCase(totalS)) {
											//System.out.println("fields>"+fields[0]+"<>"+fields[1]+"<>"+fields[2]+"<");
										//	System.out.println("cpanelid"+cpanelid);
//											 check to see if an entry for this panel is in the vector
											try{
												windAxisCLpus.get(cpanelid - 1);
											}
											catch (ArrayIndexOutOfBoundsException aiob){
												windAxisCLpus.add(cpanelid-1, new Double(0.0));
											}
											double clpus = ((Double) windAxisCLpus
													.get(cpanelid - 1))
													.doubleValue()
													+ new Double(fields[1]
															.trim())
															.doubleValue();
											//System.out.println("i "+cpanelid+" clpus = "+fields[1].trim());
											windAxisCLpus.set(cpanelid - 1,
													new Double(clpus));
											//System.out.println("panel 1 = "+windAxisCLpus.get(0));
											try{
												windAxisCDpus.get(cpanelid - 1);
											}
											catch (ArrayIndexOutOfBoundsException aiob){
												windAxisCDpus.add(cpanelid-1, new Double(0.0));
											}
											double cdpus = ((Double) windAxisCDpus
													.elementAt(cpanelid - 1))
													.doubleValue()
													+ new Double(fields[2]
															.trim())
															.doubleValue();
											windAxisCDpus.add(cpanelid - 1,
													new Double(cdpus));
											try{
												windAxisCYpus.get(cpanelid - 1);
											}
											catch (ArrayIndexOutOfBoundsException aiob){
												windAxisCYpus.add(cpanelid-1, new Double(0.0));
											}
											double cypus = ((Double) windAxisCYpus
													.elementAt(cpanelid - 1))
													.doubleValue()
													+ new Double(fields[3]
															.trim())
															.doubleValue();
											windAxisCYpus.add(cpanelid - 1,
													new Double(cypus));
										}
										if (record.startsWith("CM"))
											readclcdcyblock = false;
									}

								}
								if (record.startsWith("CM")) {
									boolean readcmblock = true;
									while (readcmblock) {
										record = reader.readLine().trim();
										String[] fields = record.split("\\s+",
												4);
										if (fields[0].equalsIgnoreCase(totalS)) {
											try{
												windAxisCMPpus.get(cpanelid - 1);
											}
											catch (ArrayIndexOutOfBoundsException aiob){
												windAxisCMPpus.add(cpanelid-1, new Double(0.0));
											}
											double cmppus = ((Double) windAxisCMPpus
													.elementAt(cpanelid - 1))
													.doubleValue()
													+ new Double(fields[1]
															.trim())
															.doubleValue();
											windAxisCMPpus.add(cpanelid - 1,
													new Double(cmppus));
											try{
												windAxisCMYpus.get(cpanelid - 1);
											}
											catch (ArrayIndexOutOfBoundsException aiob){
												windAxisCMYpus.add(cpanelid-1, new Double(0.0));
											}
											double cmypus = ((Double) windAxisCMYpus
													.elementAt(cpanelid - 1))
													.doubleValue()
													+ new Double(fields[2]
															.trim())
															.doubleValue();
											windAxisCMYpus.add(cpanelid - 1,
													new Double(cmypus));
											try{
												windAxisCMRpus.get(cpanelid - 1);
											}
											catch (ArrayIndexOutOfBoundsException aiob){
												windAxisCMRpus.add(cpanelid-1, new Double(0.0));
											}
											double cmrpus = ((Double) windAxisCMRpus
													.elementAt(cpanelid - 1))
													.doubleValue()
													+ new Double(fields[3]
															.trim())
															.doubleValue();
											windAxisCMRpus.add(cpanelid - 1,
													new Double(cmrpus));
										}
										if (record.equalsIgnoreCase(coefficientswrt)){
											readwindaxisblock=false;
											readcmblock = false;
										}	
									}
								}
							}
						}
						//System.out.println("length of Vector  = "+windAxisCLpus.size());
						if (record.equalsIgnoreCase(diagnosticinfo) 
								|| record.equalsIgnoreCase(computationtimings)
								|| record.equalsIgnoreCase(stabilityaxisS))readpatchblock = false;
					}
				}
			}
		}
	}
	
	public double getMachRef(){
		return this.machRef;
	}
	
	public double getPressureRef(){
		return this.pressureRef;
	}
	
	public double getGammaRef(){
		return this.gammaRef;
	}
	
	public double getAreaRef(){
		return this.areaRef;
	}
	
	public double getWindaxisCl() {
		return windaxisCl;
	}
	
	public void setWindaxisCl(double value) {
		this.windaxisCl = value;
	}
	public Vector getWindAxisCDpus(){
		return windAxisCDpus;
	}
	public Vector getWindAxisCLpus(){
		return windAxisCLpus;
	}
	public void dimensionalizeFields (){
		double wALpus = 0.0;
		double scale = pressureRef*.5*gammaRef*Math.pow(machRef,2)*areaRef;
		//System.out.println("winAxisCLpus Dim size "+windAxisCLpus.size());
		for (int i=0; i<windAxisCLpus.size();i++){
			wALpus = ((Double)(windAxisCLpus.elementAt(i))).doubleValue()*scale;
			this.windAxisLpus.add(i,new Double(wALpus));
		}
		//System.out.println("winAxisLpus Dim size "+windAxisLpus.size());
	}
	public double getdynPressRef(){
		return this.dynPressRef;
	}
	public Vector getWindAxisLpus(){
		return windAxisLpus;
	}
	public Double[] getWindAxisLpusA(){
		int ln = windAxisLpus.size();
		Double[] da = new Double[ln];
		windAxisLpus.copyInto(da);
		return da;
	}
	
	public Double[] getWindAxisCLpusA(){
		int ln = windAxisCLpus.size();
		Double[] da = new Double[ln];
		windAxisCLpus.copyInto(da);
		return da;
	}
	public double getWindaxisCd() {
		return windaxisCd;
	}

	public void setWindaxisCd(double value) {
		this.windaxisCd = value;
	}
	
	public double getWindaxisCmR() {
		return windaxisCmR;
	}
	
	public double getWindaxisCmRNorm(double refCmR)
	{
		return (windaxisCmR / refCmR);
	}
	
	public double getWindaxisCmRMinimize(double refCmR)
	{
		return (1.0 - (windaxisCmR / refCmR)); // [CmR >= refCmR]->  [(1.0 - CmR/refCmR) <= 0]
	}
	
	public void setWindaxisCmR(double value) {
		this.windaxisCmR = value;
	}
	
	public double getWindAxisCLpus(Integer index){
		return ((Double)windAxisCLpus.elementAt(index.intValue())).doubleValue();
	}
	public double getWindAxisLpus(Integer index){
		return ((Double)windAxisLpus.elementAt(index.intValue())).doubleValue();
	}
	
	public void setWindAxisCLpus(Integer index, double value){
		windAxisCLpus.set(index.intValue(), new Double(value));
	}
//	public void setWindAxisLpus(Integer index, double value){
//		windAxisLpus.set(index.intValue(), new Double(value));
//	}
	public void setWindAxisLpus(Integer index, Double value){
		windAxisLpus.set(index.intValue(), value);
	}
	//
//	public static void main(String args[]) throws IOException {
//		File avusOutputFile = new File(
//				"/home/kolonarm/workspace/iGrid/data/avus/goland/naca0006/mp85alpha5p0/AvusOutputMp85.out");
//		AvusOutput avusOut = new AvusOutput(avusOutputFile);
//		System.out.println("windaxisCl = " + avusOut.getWindaxisCl());
//		System.out.println("windaxisCd = " + avusOut.getWindaxisCd());
//		//
//		try {
//			/*
//			ContextNode cdNearFieldCn = new ContextNode("NearFieldCd", avusOut,
//					ServiceContext.DA_IN);
//			// create the Item in this Context Node
//			String itemName = "WindaxisCd";
//			String setMethodName = GenericUtil.getObjectMethodName("set"
//					+ itemName, avusOut);
//			String getMethodName = GenericUtil.getObjectMethodName("get"
//					+ itemName, avusOut);
//			Vector iFilter = new Vector();
//			iFilter.addElement("Method");
//			iFilter.addElement("double");
//			iFilter.addElement(setMethodName + "(java.lang.double value)");
//			iFilter.addElement(getMethodName + "()");
//
//			// add the item to the node
//			cdNearFieldCn.addItem(itemName, iFilter);
//			// create the variable for this item in the node
//			 */
//			UnknownDistribution unkndist = new UnknownDistribution(new Double(
//					"0.0"));
//			CadDescription cD = new CadDescription(CaeConstants.SHAPE);
//			/*
//			Variable cDiVar = new Variable("WindaxisCd", cdNearFieldCn,
//					"WindaxisCd", ContextVariable.DOUBLE, unkndist, true, cD);
//			ResponseVariable cDiRespVar = new ResponseVariable("WindaxisCd",
//					cDiVar, SorcerDistribution.REALIZATION);
//			System.out.println("Response variable value = "
//					+ cDiRespVar.getValue());
//					*/
//			//
//			ContextNode lpusCn = new ContextNode("LiftperUnitSpan", avusOut,
//					ServiceContext.DA_IN);
//			// create the Item in this Context Node
//			String itemName1 = "WindAxisCLpus";
//			String setMethodName1 = GenericUtil.getObjectMethodName("set"
//					+ itemName1, avusOut);
//			String getMethodName1 = GenericUtil.getObjectMethodName("get"
//					+ itemName1, avusOut);
//			Vector iFilter1 = new Vector();
//			iFilter1.addElement("Method");
//			iFilter1.addElement("double");
//			iFilter1.addElement(setMethodName1 + "(java.lang.Integer "+ new Integer(1)+ ", java.lang.double value)");
//			iFilter1.addElement(getMethodName1 + "(java.lang.Integer "+new Integer(1)+")");
//
//			// add the item to the node
//			lpusCn.addItem("lpus1", iFilter1);
//			// create the variable for this item in the node
//			AspectVariable lpusVar = new AspectVariable("lpus1", lpusCn, Var.DOUBLE, true, cD, unkndist);
//			ResponseVariable lpusRespVar = new ResponseVariable("lpus1",
//					lpusVar, Distribution.REALIZATION);
//			System.out.println("Response variable value lpus1 = "
//					+ lpusRespVar.getValue());
//			
//			JEP expJEP = new JEP();
//			expJEP.addStandardConstants();
//			expJEP.addStandardFunctions();
//			expJEP.addComplex();
//			expJEP.setAllowUndeclared(true);
//			expJEP.setAllowAssignment(true);
//			// Add all the design variables to the parser
//			
//			//Double da[] = new Double[]{1.0, 2.0, 3.0,4.0};
//			//System.out.println("sum = "+jsum.evaluate(da));
//			
//			Vector clpus = avusOut.getWindAxisCLpus();
//		expJEP.addVariable("clpus", 2.0);
//		expJEP.parseExpression("clpus^2");
//		System.out.println("Exp clpus^2 Value = "+expJEP.getValue());
//		Sum jsum = new Sum(expJEP);
//		//	System.out.println("cl^2 total = "+jsum.evaluate(avusOut.getWindAxisCLpusA()));
//			double cltotal = 0.0;
//			double root = ((Double)clpus.elementAt(0)).doubleValue();
//			double rootb =  0.0228867;
//			double b2   = Math.pow(20.,2.);
//			for (int i=0; i<clpus.size(); i++){
//				System.out.println(i+" "+clpus.elementAt(i));
//				cltotal=cltotal+((Double)clpus.elementAt(i)).doubleValue();
//			}
//			for (int i=0; i<clpus.size(); i++){
//				double yi2 = Math.pow((new Double(i)).doubleValue(), 2.0);
//				double elliptic = root*Math.pow((1.0-(yi2/b2)),0.5);
//				System.out.println(i+" "+elliptic);
//			}
//			for (int i=0; i<clpus.size(); i++){
//				double yi2 = Math.pow((new Double(i)).doubleValue(), 2.0);
//				double ellipticb = rootb*Math.pow((1.0-(yi2/b2)),0.5);
//				System.out.println(i+" "+ellipticb);
//			}
//			System.out.println("cl total = "+cltotal);
//		} catch (Exception ee) {
//			ee.printStackTrace();
//		}
//	}
}
