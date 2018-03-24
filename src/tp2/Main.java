package tp2;

import java.util.ArrayList;

import tp2.exceptions.DebitNotSetException;
import tp2.turbines.Turbine;
import tp2.turbines.Turbine1;
import tp2.turbines.Turbine2;

public class Main {
	
	private ArrayList<Turbine> turbines = new ArrayList();
	private double reste;
	
	public void addTurbine(Turbine t) {
		turbines.add(t);
	}
	
	public void activateTurbine(int index) {
		turbines.get(index).setActive(true);
	}
	
	public void desactivateTurbine(int index) {
		turbines.get(index).setActive(false);
	}
	
	public void setDebitMaxTurbine(int index, double debitMaxM3) {
		turbines.get(index).setDebitMax(debitMaxM3);
	}
	
	public void recursion(double elevationAmont, double debitARepartir) throws DebitNotSetException {
		Turbine initiale = turbines.get(0);
		initiale.remplirTableau(debitARepartir, elevationAmont);
		
		for(int i = 1; i < turbines.size() ; i ++) {
			turbines.get(i).remplirTableau(debitARepartir, elevationAmont, turbines.get(i-1));
		}
		
		recursionArriere(debitARepartir);
		
	}
	
	
	private void recursionArriere(double debit) throws DebitNotSetException {
		double allocation, production =0;
		for(int i = turbines.size()-1; i >0 ; i--) {
			allocation = turbines.get(i).getBestAllocationAtDebit(debit);// * Turbine.DISCRETISATION;
			debit = debit - allocation;
			turbines.get(i).setDebitUtilise(allocation);
		}
		
		double dernierDebit = turbines.get(0).getBestAllocationAtDebit(debit);
		turbines.get(0).setDebitUtilise(dernierDebit);
		
		this.reste = debit - dernierDebit ;
		
		
		for(int i = 0; i < turbines.size() ; i++) {
			production = turbines.get(i).getBestProductionAtDebit(turbines.get(i).getDebitUtilise()) - production;
			turbines.get(i).setPuissanceGeneree(production);
		}
		
	}
	
	public void resultats() {
		for (Turbine turbine : turbines) {
			System.out.println(turbine);
		}
		System.out.println("Restes : " + reste);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Main application = new Main();
		Turbine t1 = new Turbine1(true, 100);
		Turbine t2 = new Turbine2(true, 100);
		
		application.addTurbine(t1);
		application.addTurbine(t2);
		
		try {
			application.recursion(172, 300);
			application.resultats();
		} catch (DebitNotSetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
//		Double[][] test = t1.getTab();
//		
//		for(int i = 0 ; i < test.length ; i++) {
//			for(int j = 0 ; j < test[i].length ; j++) {
//				System.out.print(test[i][j] + "   ");
//			}
//			System.out.println("");
//		}
//		System.out.println("Fin");
//		
//		Double[][] test2 = t2.getTab();
//		
//		for(int i = 0 ; i < test2.length ; i++) {
//			for(int j = 0 ; j < test2[i].length ; j++) {
//				System.out.print(test2[i][j] + "   ");
//			}
//			System.out.println("");
//		}
//		System.out.println("Fin");
	}
	
	

}
