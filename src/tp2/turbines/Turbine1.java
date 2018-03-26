package tp2.turbines;

import tp2.elevation.Elevation;

/**
 * Représentation de la turbine 1 des Chutes du Diable
 * @author cheva
 *
 */
public class Turbine1 extends Turbine {

	public Turbine1(boolean active, int maxDebitM3) {
		super(active, maxDebitM3);
	}

	@Override
	protected Double rendement(double hChute, double debit) {
		//Normalisation des données :
		//TODO absolument pas sur qu'on doivent faire un truc dans le genre
		hChute = (hChute - 31.97) / 1.589;
		debit = (debit - 118.5) / 55.03;
		
		double res = 0;
		
		double t2 = 1.659 * hChute;
		double t3 = 15.17 * debit;
		double t4 = 6.038 * Math.pow(10, -3) * Math.pow(hChute, 2);
		double t5 = 0.8589 * hChute * debit;
		double t6 = 2.71 * Math.pow(debit, 2);
		double t7 = 2.805 * Math.pow(10, -3) * Math.pow(hChute, 2) * debit;
		double t8 = 0.04096 * hChute * Math.pow(debit, 2);
		double t9 = 1.309 * Math.pow(debit, 3);
		
		res = 32.15 + t2 + t3 - t4 + t5 - t6 - t7 + t8 - t9;
		
		return res;
		//return 101.0;
	}
	
	
	public String toString() {
		return "Turbine 1 "+ super.toString();
	}

}
