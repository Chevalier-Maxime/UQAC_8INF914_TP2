package tp2.turbines;

import tp2.elevation.Elevation;

/**
 * Représentation de la turbine 4 des Chutes du Diable
 * @author cheva
 *
 */
public class Turbine4 extends Turbine {

	public Turbine4(boolean active, int maxDebitM3) {
		super(active, maxDebitM3);
	}

	@Override
	protected Double rendement(double hChute, double debit) {
		//Normalisation des données :
		//TODO absolument pas sur qu'on doivent faire un truc dans le genre
		hChute = (hChute - 31.97) / 1.588;
		debit = (debit - 121.9) / 55;
		
		double res = 0;
		
		double t2 = 1.799 * hChute;
		double t3 = 18.65 * debit;
		double t4 = 1.139 * hChute * debit;
		double t5 = 4.391 * Math.pow(debit, 2);
		double t6 = 0.1478 * hChute * Math.pow(debit, 2);
		double t7 = 2.582 * Math.pow(debit, 3);
		
		res = 34.8 + t2 + t3 + t4 - t5 + t6 - t7;
		
		return res;
	}
	
	
	public String toString() {
		return "Turbine 4 "+ super.toString();
	}

}
