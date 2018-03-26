package tp2.turbines;

import tp2.elevation.Elevation;

/**
 * Représentation de la turbine 3 des Chutes du Diable
 * @author cheva
 *
 */
public class Turbine3 extends Turbine {

	public Turbine3(boolean active, int maxDebitM3) {
		super(active, maxDebitM3);
	}

	@Override
	protected Double rendement(double hChute, double debit) {
		//Normalisation des données :
		//TODO absolument pas sur qu'on doivent faire un truc dans le genre
		hChute = (hChute - 31.97) / 1.588;
		debit = (debit - 123.5) / 51.05;
		
		double res = 0;
		
		double t2 = 1.77 * hChute;
		double t3 = 14.88* debit;
		double t4 = 0.9327 * hChute * debit;
		double t5 = 3.237 * Math.pow(debit, 2);
		double t6 = 0.08316 * hChute * Math.pow(debit, 2);
		double t7 = 1.478 * Math.pow(debit, 3);
		
		res = 34.02 + t2 + t3 + t4 - t5 + t6 - t7;
		
		return res;
		//return 10001.0;
	}
	
	
	public String toString() {
		return "Turbine 3 "+ super.toString();
	}

}
