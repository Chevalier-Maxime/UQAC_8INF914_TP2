package tp2.turbines;

import java.util.HashMap;

import tp2.elevation.Elevation;
import tp2.exceptions.DebitNotSetException;
import tp2.exceptions.PuissanceNotSetException;

public abstract class Turbine {
	
	public final static int DISCRETISATION = 5;

	private boolean active;
	private double maxM3;
	private Double puissanceGenere;
	private Double debitUtilise;
	
	private int tailleTab;
	private Double[][] tab;
	private HashMap<Double, Double> bestAllcationAtDebit = new HashMap();
	private HashMap<Double, Double> bestProductionAtDebit = new HashMap();
	
	public Turbine(boolean active, int maxDebitM3) {
		this.active = active;
		this.maxM3 = maxDebitM3;
		this.tailleTab = maxDebitM3/DISCRETISATION + 1;
		this.tab = new Double[tailleTab][tailleTab];
		puissanceGenere = 0.0;
		debitUtilise = 0.0;
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
	public double getPuissanceGeneree() throws PuissanceNotSetException {
		if(puissanceGenere == null) throw new PuissanceNotSetException();
		return this.puissanceGenere;
	}
	
	public double getDebitUtilise() throws DebitNotSetException {
		if(debitUtilise == null) throw new DebitNotSetException();
		return this.debitUtilise;
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
			setBestValues();
			return;
		}
		
		
		for(int debitRestant = 0; /*debitRestant <= debitDisponible && */debitRestant <= getDebitMax(); debitRestant+=DISCRETISATION) {
			if(debitRestant <= debitDisponible) {
				tab[debitRestant/DISCRETISATION][0] = 0.0 + t.getBestProductionAtDebit(debitDisponible);
				for(int debitAAllouer = DISCRETISATION; debitAAllouer <= debitRestant; debitAAllouer+=DISCRETISATION) {
					hChute = Elevation.hauteurChute(elevationAmont, debitDisponible, debitAAllouer);
					tab[debitRestant/DISCRETISATION][debitAAllouer/DISCRETISATION] = rendement(hChute, debitAAllouer) 
							+ t.getBestProductionAtDebit(debitDisponible - debitAAllouer);
				}
				
			}else {
				tab[debitRestant/DISCRETISATION][0] = 0.0;
				for(int debitAAllouer = DISCRETISATION; debitAAllouer <= debitRestant; debitAAllouer+=DISCRETISATION) {
					tab[debitRestant/DISCRETISATION][debitAAllouer/DISCRETISATION] = 0.0;
				}
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
			setBestValues();
			return;
		}
		
		for(int debitRestant = 0; /*debitRestant <= debitDisponible && */ debitRestant <= getDebitMax(); debitRestant+=DISCRETISATION) {
			if(debitRestant <= debitDisponible) {
				tab[debitRestant/DISCRETISATION][0] = 0.0;
				for(int debitAAllouer = DISCRETISATION; debitAAllouer <= debitRestant; debitAAllouer+=DISCRETISATION) {
					hChute = Elevation.hauteurChute(elevationAmont, debitDisponible, debitAAllouer);
					tab[debitRestant/DISCRETISATION][debitAAllouer/DISCRETISATION] = rendement(hChute, debitAAllouer);
				}
			}else {
				tab[debitRestant/DISCRETISATION][0] = 0.0;
				for(int debitAAllouer = DISCRETISATION; debitAAllouer <= debitRestant; debitAAllouer+=DISCRETISATION) {
					tab[debitRestant/DISCRETISATION][debitAAllouer/DISCRETISATION] = 0.0;
				}
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
		double saveDebit = debit;
		
		if(debit > getDebitMax()) {
			debit = getDebitMax();
		}else {
			debit = debit - debit%DISCRETISATION;
		}
		//double debitArrange = 
		return bestAllcationAtDebit.get(debit);
	}
	
	public double getBestProductionAtDebit(double debit) {
		if(debit > getDebitMax()) {
			debit = getDebitMax();
		}else {
			debit = debit - debit%DISCRETISATION;
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
	
	public String toString() {
		return "Debit : " + this.debitUtilise + " Puissance : " + this.puissanceGenere;
	}

	public void setDebitUtilise(double debit) {
		this.debitUtilise = debit;
		
	}

	public void setPuissanceGeneree(double production) {
		this.puissanceGenere = production;
	}
}
