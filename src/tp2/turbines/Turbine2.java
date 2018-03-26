package tp2.turbines;

/**
 * Représentation de la turbine 2 des Chutes du Diable
 * @author cheva
 *
 */
public class Turbine2 extends Turbine {

	public Turbine2(boolean active, int maxDebitM3) {
		super(active, maxDebitM3);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Double rendement(double hChute, double debit) {
		//Normalisation des données :
		//TODO absolument pas sur qu'on doivent faire un truc dans le genre
		hChute = (hChute - 31.98) / 1.589;
		debit = (debit - 114.6) / 57.24;
		
		double res = 0;
		
		double t2 = 1.68 * hChute;
		double t3 = 13.65 * debit;
		double t4 = 6.624 * Math.pow(10, -14) * Math.pow(hChute, 2);
		double t5 = 0.8393 * hChute * debit;
		double t6 = 1.25 * Math.pow(debit, 2);
		
		
		res = 32.35 + t2 + t3 + t4 + t5 - t6;
		
		return res;
		//return 1001.0;
	}
	
	public String toString() {
		return "Turbine 2 "+ super.toString();
	}

}
