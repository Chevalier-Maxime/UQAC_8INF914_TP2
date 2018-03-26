package tp2.turbines;

import tp2.elevation.Elevation;

/**
 * Représentation de la turbine 5 des Chutes du Diable
 * @author cheva
 *
 */
public class Turbine5 extends Turbine {

	public Turbine5(boolean active, int maxDebitM3) {
		super(active, maxDebitM3);
	}

	@Override
	protected Double rendement(double hChute, double debit) {
		//Normalisation des données :
		//TODO absolument pas sur qu'on doivent faire un truc dans le genre
		hChute = (hChute - 31.98) / 1.588;
		debit = (debit - 113.4) / 53.7;
		
		double res = 0;
		
		double t2 = 1.866 * hChute;
		double t3 = 15.75 * debit;
		double t4 = 1.01 * hChute * debit;
		double t5 = 2.666 * Math.pow(debit, 2);
		double t6 = 0.05991 * hChute * Math.pow(debit, 2);
		double t7 = 1.335 * Math.pow(debit, 3);
		
		res = 32.57 + t2 + t3 + t4 - t5 + t6 - t7;
		
		return res;
	}
	
	
	public String toString() {
		return "Turbine 5 "+ super.toString();
	}

}
