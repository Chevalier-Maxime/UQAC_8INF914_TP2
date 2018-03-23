package tp2.turbines;

import tp2.elevation.Elevation;
import tp2.exceptions.PuissanceNotSetException;

public abstract class Turbine {
	
	public final static int DISCRETISATION = 5;

	private boolean active;
	private int maxM3;
	private Integer puissanceGenere;
	
	private int tailleTab;
	private Double[][] tab;
	
	public Turbine(boolean active, int maxDebitM3) {
		this.active = active;
		this.maxM3 = maxDebitM3;
		this.tailleTab = maxDebitM3/DISCRETISATION;
		this.tab = new Double[tailleTab][tailleTab];
		//TODO initialiser tab à 0 ? ou faire un if dans le for et mettre à 0 quand >debitDisponible
	}
	
	public boolean getActive() {
		return active;
	}
	
	public int getDebitMax() {
		return maxM3;
	}
	
	public Double[][] getTab(){
		return tab;
	}
	public int getPuissanceGeneree() throws PuissanceNotSetException {
		if(puissanceGenere == null) throw new PuissanceNotSetException();
		return this.puissanceGenere;
	}
	
	public void remplirTableau(int debitDisponible, int elevationAmont) {
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
		
		//TODO ajouter des bornes pour aller plus vite
		//j = debit restant à attribuer
		//i = débit à allouer à la turbine
		for(int j = 0; j < getDebitMax() ; j+=DISCRETISATION) {
			if(j <= debitDisponible) {
				hChute = Elevation.hauteurChute(elevationAmont, debitDisponible, j);
				for(int i = 0; i < getDebitMax() ; i+=DISCRETISATION) {
					tab[i/DISCRETISATION][j/DISCRETISATION] = rendement(hChute, i);
				}
			}else {
				//On ne peux pas donner plus de débit à la turbine parce qu'il y en a plus
				for(int i = 0; i < getDebitMax() ; i+=DISCRETISATION) {
					tab[i/DISCRETISATION][j/DISCRETISATION] = tab[i/DISCRETISATION][(j-DISCRETISATION)/DISCRETISATION];
				}
			}
			
		}
	}

	protected abstract Double rendement(double hChute, double debit);
}
