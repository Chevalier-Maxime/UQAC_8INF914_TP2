package tp2.elevation;

public class Elevation {

	//TODO c'est peut être mieux de passer sur des float
	//si on a des pb de mémoire/tmps de traitement
	public static double calculElevation(int qtot) {
		double res = 0;
		
		double t1 = 8.639f * Math.pow(10, -13) * Math.pow(qtot, 4);
		double t2 = 3.189f * Math.pow(10, -9) * Math.pow(qtot, 3);
		double t3 = 3.257f * Math.pow(10, -6) * Math.pow(qtot, 2);
		double t4 = 2.168f * Math.pow(10, -3) * qtot;
		
		res = t1 - t2 + t3 + t4 + 137.5;
		
		return res;
	}
	
	public static double hauteurChute(int elevationAmont, int qtot, int debit) {
		double res = 0;
		
		res = elevationAmont - calculElevation(qtot) - (0.5*Math.pow(10, -5) * Math.pow(debit, 2));
		
		return res;
	}
	
}
