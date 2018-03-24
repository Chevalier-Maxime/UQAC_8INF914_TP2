package tp2.turbines;

import java.util.HashMap;

import tp2.elevation.Elevation;
import tp2.exceptions.PuissanceNotSetException;

public abstract class Turbine {
	
	public final static int DISCRETISATION = 5;

	private boolean active;
	private double maxM3;
	private Integer puissanceGenere;
	
	private int tailleTab;
	private Double[][] tab;
	private HashMap<Double, Double> bestAllcationAtDebit = new HashMap();
	private HashMap<Double, Double> bestProductionAtDebit = new HashMap();
	
	public Turbine(boolean active, int maxDebitM3) {
		this.active = active;
		this.maxM3 = maxDebitM3;
		this.tailleTab = maxDebitM3/DISCRETISATION + 1;
		this.tab = new Double[tailleTab][tailleTab];
		//TODO initialiser tab à 0 ? ou faire un if dans le for et mettre à 0 quand >debitDisponible
	}
	
	public boolean getActive() {
		return active;
	}
	
	public double getDebitMax() {
		return maxM3;
	}
	
	public Double[][] getTab(){
		return tab;
	}
	public int getPuissanceGeneree() throws PuissanceNotSetException {
		if(puissanceGenere == null) throw new PuissanceNotSetException();
		return this.puissanceGenere;
	}
	
	public void remplirTableau(double debitDisponible, double elevationAmont, Turbine t) {
		
		double hChute;
		//Bornes
		if(debitDisponible == 0 || this.active == false) {
			double debitRestant = debitDisponible;
			for(int i = 0 ; i < tailleTab; i++) {
				for(int j = 0 ; j < tailleTab; j++) {
					tab[i][j] = 0.0 + t.getBestProductionAtDebit(debitDisponible);
				}
			}
			return;
		}
		
		
		for(int debitRestant = 0; debitRestant <= debitDisponible && debitRestant <= getDebitMax(); debitRestant+=DISCRETISATION) {
			tab[debitRestant/DISCRETISATION][0] = 0.0;
			for(int debitAAllouer = DISCRETISATION; debitAAllouer <= debitRestant; debitAAllouer+=DISCRETISATION) {
				hChute = Elevation.hauteurChute(elevationAmont, debitDisponible, debitAAllouer);
				tab[debitRestant/DISCRETISATION][debitAAllouer/DISCRETISATION] = rendement(hChute, debitAAllouer) 
						+ t.getBestProductionAtDebit(debitDisponible - debitAAllouer);
			}
			
		}
		
		setBestValues();
	}
	
public void remplirTableau(double debitDisponible, double elevationAmont) {
		
		double hChute;
		//Bornes
		if(debitDisponible == 0 || this.active == false) {
			for(int i = 0 ; i < tailleTab; i++) {
				for(int j = 0 ; j < tailleTab; j++) {
					tab[i][j] = 0.0;
				}
			}
			return;
		}
		
		for(int debitRestant = 0; debitRestant <= debitDisponible && debitRestant <= getDebitMax(); debitRestant+=DISCRETISATION) {
			tab[debitRestant/DISCRETISATION][0] = 0.0;
			for(int debitAAllouer = DISCRETISATION; debitAAllouer <= debitRestant; debitAAllouer+=DISCRETISATION) {
				hChute = Elevation.hauteurChute(elevationAmont, debitDisponible, debitAAllouer);
				tab[debitRestant/DISCRETISATION][debitAAllouer/DISCRETISATION] = rendement(hChute, debitAAllouer);
			}
			
		}
		
		setBestValues();
	}
	
	private void setBestValues() {
		double bestAllocation;
		double bestProduction;
		
		for(int debitRestant = 0; debitRestant <= getDebitMax(); debitRestant+=DISCRETISATION) {
			bestProduction = tab[debitRestant/DISCRETISATION][0];
			bestAllocation = 0;
			for(int debitAAllouer = 0; debitAAllouer <= debitRestant; debitAAllouer+=DISCRETISATION) {
				if(tab[debitRestant/DISCRETISATION][debitAAllouer/DISCRETISATION] != null) {
					if(tab[debitRestant/DISCRETISATION][debitAAllouer/DISCRETISATION] > bestProduction) {
						bestProduction = tab[debitRestant/DISCRETISATION][debitAAllouer/DISCRETISATION];
						bestAllocation = debitAAllouer;
					}
				}
			}
			
			bestAllcationAtDebit.put((double) debitRestant, bestAllocation);
			bestProductionAtDebit.put((double) debitRestant, bestProduction);
		}
	}
	
	public double getBestAllocationAtDebit(double debit) {
		
		if(debit > getDebitMax()) {
			debit = Math.floor(getDebitMax()/DISCRETISATION);
		}else {
			debit = Math.floor(debit / DISCRETISATION);
		}
		return bestAllcationAtDebit.get(debit);
	}
	
	public double getBestProductionAtDebit(double debit) {
		if(debit > getDebitMax()) {
			debit = Math.floor(getDebitMax());
		}else {
			debit = Math.floor(debit);
		}
		return bestProductionAtDebit.get(debit);
	}

	protected abstract Double rendement(double hChute, double debit);

	public void setActive(boolean active) {
		// TODO Auto-generated method stub
		this.active = active;
	}

	public void setDebitMax(double debitMaxM3) {
		// TODO Auto-generated method stub
		this.maxM3 = debitMaxM3;
		
	}
}
