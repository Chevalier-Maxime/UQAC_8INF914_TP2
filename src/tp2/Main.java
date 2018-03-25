package tp2;

import java.util.ArrayList;

import tp2.exceptions.DebitNotSetException;
import tp2.turbines.*;

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
		//TODO reinitialiser les trubines
		int premiereTurbine = 0;
		while(premiereTurbine < turbines.size() && !turbines.get(premiereTurbine).getActive()) premiereTurbine++;
		if(premiereTurbine >= turbines.size()) {
			this.reste = debitARepartir;
			return;
		}
		Turbine initiale = turbines.get(premiereTurbine);
		initiale.remplirTableau(debitARepartir, elevationAmont);
		
		int derniereTurbine = premiereTurbine;
		for(int i = premiereTurbine+1; i < turbines.size() ; i ++) {
			if(!turbines.get(i).getActive()) {
				continue;
			}
			turbines.get(i).remplirTableau(debitARepartir, elevationAmont, turbines.get(derniereTurbine));
			derniereTurbine = i;
		}
		recursionArriere(debitARepartir);
	}
	
	
	private void recursionArriere(double debit) throws DebitNotSetException {
		double allocation, productionPrecedente, production = 0;
		
		int premiereTurbine = turbines.size() -1;
		while(!turbines.get(premiereTurbine).getActive()) premiereTurbine--;
		int turbinePrecedente;
		for(int i = premiereTurbine; i >=0 ; i--) {
			if(turbines.get(i).getActive()) {
				
				
				
				allocation = turbines.get(i).getBestAllocationAtDebit(debit);
				debit = debit - allocation;
				turbines.get(i).setDebitUtilise(allocation);
				
				if(i != 0) {
					turbinePrecedente = i-1;
					while(turbinePrecedente>= 0 && !turbines.get(turbinePrecedente).getActive()) turbinePrecedente --;
					
					if(turbinePrecedente >= 0)
						production = turbines.get(i).getBestProductionAtDebit(allocation + debit) - turbines.get(turbinePrecedente).getBestProductionAtDebit(debit);
					else {
						production = turbines.get(i).getBestProductionAtDebit(allocation + debit);
					}
				}else {
					production = turbines.get(i).getBestProductionAtDebit(allocation + debit);
				}
				
				turbines.get(i).setPuissanceGeneree(production);
			}
		}
		
		this.reste = debit;
		
		
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
		Turbine t1 = new Turbine1(true, 165);
		Turbine t2 = new Turbine2(true, 165);
		Turbine t3 = new Turbine3(true, 165);
		Turbine t4 = new Turbine4(false, 165);
		Turbine t5 = new Turbine5(true, 165);
		
		application.addTurbine(t1);
		application.addTurbine(t2);
		application.addTurbine(t3);
		application.addTurbine(t4);
		application.addTurbine(t5);
		
		try {
			application.recursion(172.11, 549.958);
			application.resultats();
		} catch (DebitNotSetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		printTab(t1);
//		printTab(t2);
//		printTab(t3);
		
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
	
	public static void printTab(Turbine t) {
		if(!t.getActive()) return;
		Double[][] test2 = t.getTab();
		
		for(int i = 0 ; i < test2.length ; i++) {
			for(int j = 0 ; j < test2[i].length ; j++) {
				System.out.print(test2[i][j] + "   ");
			}
			System.out.println("");
		}
		System.out.println("Fin");
		
	}
	
	

}
