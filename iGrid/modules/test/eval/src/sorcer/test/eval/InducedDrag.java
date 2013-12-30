package sorcer.test.eval;
import java.io.Serializable;
import java.rmi.RemoteException;


import sorcer.service.EvaluationException;
import sorcer.vfe.Var;
import sorcer.vfe.util.VarList;

public class InducedDrag implements Serializable {


	private Double totalLift;
	private Double inducedDrag;
	private Double sqerr;
	Double[] yiA;
	Double[] swidth;
	Double[] lpus;
	Double qdp;
	//double[] yiA = new double[] { 0.5, 1.5, 2.5, 3.5, 4.5, 5.5, 6.5, 7.5, 8.5,
	//		9.5, 10.5, 11.5, 12.5, 13.5, 14.5, 15.5, 16.5, 17.5, 18.5, 19.5 };

	//double[] swidth = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
	//		1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };

	private double[] lastLpus = new double [100];
	private double[] lastEllipt = new double[100];
	private double[] lpusElldiff= new double[100];
	
	public InducedDrag(String name, Double[] yiA, Double[] lpus) {
		
		this.yiA=yiA;
		this.lpus=lpus;
		genSwidth(yiA);
	}
	
	public InducedDrag(String name, Double[] yiA) {	
		this.yiA=yiA;
		genSwidth(yiA);
	}

	public InducedDrag(String name, Double[] yiA, Double qdp) {
		
		this.yiA=yiA;
		this.qdp=qdp;
		genSwidth(yiA);
	}

	private void genSwidth(Double[] yiA) {
		this.swidth = new Double[yiA.length];
		Double yiLeft = 0.0;
		Double[] yiRight = new Double[yiA.length];
		for (int i =0; i<yiA.length; i++){
			if (i ==0){
				yiLeft = 0.0;
			}
			else {
				yiLeft = yiRight[i-1];
			}
		yiRight[i] = yiA[i]+ (yiA[i]-yiLeft);
		this.swidth[i] = yiRight[i]-yiLeft;
		}
	}
	
	public Double evaluateIDrag(VarList variables) throws RemoteException, EvaluationException {
		// load the response info into lpus
		lpus = new Double[variables.size()];
		// assume the first size -1 entries are LPUS
		for (int i=0; i< variables.size()-1; i++ ){
			Object o = variables.get(i).getValue();
			if (o instanceof Integer)
				this.lpus[i]=new Double((Integer)variables.get(i).getValue());
			else
				this.lpus[i]=(Double)variables.get(i).getValue();
		}
		// assume q is the last entry in the VarList
		this.qdp = (Double)variables.get(variables.size()).getValue();
		return evaluateIDrag(lpus, qdp);
	}

	public Double evaluateIDrag(Double[] lpus, Double[] yiA,
			Double[] swidth, Double q) {
		System.out.println(">>>>>>>>>>>>> swidthlength = " + swidth.length);
		this.yiA = yiA;
		this.swidth = swidth;
		this.lpus=lpus;
		return evaluateIDrag(lpus,q);
	}

	public Double evaluateIDrag(Double[] respV, Double q) {
		//
		double sum = 0.0;
		double idrag = 0.0;
		double fksum = 0.0;
		double fk = 0.0;
		double fkp1 = 0.0;
		double b2 = Math.pow(20., 2.);
		// the q that ASTROS was run at for M=.3 was 133.42 psf
		// double qdp = 133.42;
		// need to submit the job to get an update set of Response Variables
		// Assume square error function has minor axis = respV[0].

		// The formulation requires that the lift per unit span be dimensional
		// ie lbs
		double ltot = 0.0;
		for (int j = 0; j < respV.length; j++) {
			// this is for M=.3 sea level 15997.0 = Po*.5*gamma*M^2*RefArea =
			// 2116.2*.5*1.4*.3^2*120
			// respV[j]=respV[j]*15997.0;
			ltot = ltot + respV[j];
		}
		this.totalLift = ltot;
		System.out.println(">>>>> ltot = " + ltot);

		try {
			double a = respV[0];
			// loop through and generate the sum

			// note: for the caclulation of induced drag, yi is the center of
			// the strip, yk is the rhs of the strip
			for (int i = 0; i < respV.length; i++) {
				fksum = 0.0;
				double yi = yiA[i];
				double lpusi = respV[i];
				double fi = lpusi;
				//double yi2 = Math.pow((new Double(i)).doubleValue(), 2.0);
				double yi2 = Math.pow(yi, 2.0);
				// calculate the elliptic distribution
				lastEllipt[i] = a * Math.pow((1.0 - (yi2 / b2)), 0.5);
				lpusElldiff[i] = lastEllipt[i] - lpusi;
				 lastLpus[i] = lpusi;
				//System.out.println(i + " ," + ellip + " ," + lpusi + " ,"
				//		+ diff);
				// compute the square error between an elliptic distribtion and
				// the actual lpus
				sum = sum
						+ Math.pow(a * Math.pow((1.0 - (yi2 / b2)), 0.5)
								- lpusi, 2.0);

				for (int k = 0; k < respV.length; k++) {
					double yk = yiA[k] + swidth[k] / 2.0;
					fk = respV[k] / swidth[k];
					if (k < respV.length - 1) {
						fkp1 = respV[k + 1] / swidth[k + 1];
					} else {
						fkp1 = 0.0;
						//fkp1 = fk/50.;
					}
					fksum = fksum + (fk - fkp1)
							* ((1.0 / (yi - yk)) - (1.0 / (yi + yk)));
					//System.out.println("yi = "+yi+" yk = "+yk+" Fk = "+fk+" Fkp1 = "+fkp1+" swidthk = "+swidth[k]);
					 //System.out.println("fksum = "+fk+","+fkp1+" , "+yi+", "+yk);

				}
				idrag = idrag - (1.0 / (Math.PI * 8.0 * q) * fi * fksum);
				//System.out.println("Fi = "+fi+" Fksum = "+fksum+" idrag =  "+idrag);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	//	System.out
		//		.println(">>>>>>>>>>>> ncall   Sqerr    iDrag <<<<<<<<<<<<<<< ");
		this.sqerr = sum;
		//System.out.println(ncall + " , " + sum + " , " + idrag);
		return new Double(idrag);

	}
	public Double[] evaluateIDragSensitivities(VarList variables) throws RemoteException, EvaluationException {
		// load the response info into lpus
		lpus = new Double[variables.size()];
		// assume the first size -1 entries are LPUS
		for (int i=0; i< variables.size()-1; i++ ){
			Object o = variables.get(i).getValue();
			if (o instanceof Integer)
				this.lpus[i]=new Double((Integer)variables.get(i).getValue());
			else
				this.lpus[i]=(Double)variables.get(i).getValue();
		}
		// assume q is the last entry in the VarList
		this.qdp = (Double)variables.get(variables.size()).getValue();
		return evaluateIDragSensitivities(lpus, qdp);
	}
	public Double[] evaluateIDragSensitivities(Double[] respV, Double q) {
		// place dDIDLpus1-n in the first n positions of the grads array
		Double[] grads = new Double[respV.length+1];
		for (int i = 0; i<respV.length; i++){
			grads[i]=evaluateIDragSensitivity(respV,q,i," ");
		}
		// place dDIDq in the last position of grads array
		grads[respV.length] = evaluateIDragSensitivity(respV,q,0,"q");
		
		return grads;
	}
	public Double evaluateIDragSensitivity(Double[] respV, Double q, int wrt, String varName) {
		Double dDiDq;
		// if varName = q 
		if (varName.equalsIgnoreCase("q")){
			dDiDq = (-1.0/q)*evaluateIDrag(respV, q);
			return dDiDq;
		}
		//
		// wrt is an index indicating which Lpusi the partial differentiation is wrt
		//
		double dDiDlpusi = 0.0;
		double fk = 0.0;
		double fkp1 = 0.0;

		// The formulation requires that the lift per unit span be dimensional
		// ie lbs
		
		// constant q/2pi
		double qpi = 1.0 / (Math.PI * 8.0 * q);
		
		try {
			// if  varname is q (dynamic pressure)
			// if varname Lpusi
			// potentially three terms that need to be computed for computing the partial derivative wrt lpus_j
			// dDiDlpusiA =-1/(8*pi*q)(deltayj)Sum(k=1,n)[(Lpusk-Lpusk+1)[1/(yj-yk)-1/(yj+yk)]  included if j=1 to j=n
			// dDiDlpusiB = 1/(8*pi*q)(deltayi)Sum(i=1,n)[(Lpusk-Lpusk+1)[1/(yi-y_(j-1))-1/(yi+y(j-1))]  included if j=2 to j=n-1
			// dDiDlpusiC = -1/(8*pi*q)Sum(i=1,n)[Lpusi*(deltayi)(Lpusk-Lpusk+1)[1/(yi-yj)-1/(yi+yj)]
			
			double dDiDlpusiA= 0.0;
			double dDiDlpusiB= 0.0;
			double dDiDlpusiC= 0.0;

				for (int k = 0; k < respV.length; k++) {
					double yk = yiA[k] + swidth[k] / 2.0;
					fk = respV[k] / swidth[k];
					if (k < respV.length - 1) {
						fkp1 = respV[k + 1] / swidth[k + 1];
					} else {
						fkp1 = 0.0;
						//fkp1 = fk/50.;
					}
					dDiDlpusiA = dDiDlpusiA - qpi*swidth[wrt]*(fk-fkp1)*( (1.0/(yiA[wrt]-yiA[k]))-(1.0/(yiA[wrt]+yiA[k])) );
					dDiDlpusiB = dDiDlpusiB - qpi*fk*swidth[k]*( (1.0/(yiA[k]-yiA[wrt]))-(1.0/(yiA[k]+yiA[wrt])) );
					if (wrt >2 && wrt < respV.length) dDiDlpusiC = dDiDlpusiC + qpi*fk*swidth[k]*( (1.0/(yiA[k]-yiA[wrt-1]))-(1.0/(yiA[k]+yiA[wrt-1])) );
					
					

				}
				dDiDlpusi = dDiDlpusiA+dDiDlpusiB+dDiDlpusiC;
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new Double(dDiDlpusi);

	}
	public double getInducedDrag() {
		return this.inducedDrag;
	}

	public double getTotalLift() {
		return this.totalLift;
	}
	public double[] getLastLpus() {
		return this.lastLpus;
	}
	public double[] getLastEllipt() {
		return this.lastEllipt;
	}

	public double getSqerr() {
		return this.sqerr;
	}     

}
