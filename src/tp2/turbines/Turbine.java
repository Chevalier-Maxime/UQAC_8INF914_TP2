package tp2.turbines;

import java.util.HashMap;

import tp2.elevation.Elevation;
import tp2.exceptions.DebitNotSetException;
import tp2.exceptions.PuissanceNotSetException;

/**
 * Cette classe représente une turbine et s'occupe de remplir le tableau de production 
 * associé à la turbine.
 * Il faut définir la méthode rendement (qui calcul le rendement de la turbine) pour définir une turbine.
 * @author cheva
 *
 */
public abstract class Turbine {
	
	/**
	 * Permet de définir la discretisation du débit dans les calculs
	 */
	public final static int DISCRETISATION = 5;

	/**
	 * Définit si la turbine est active
	 */
	private boolean active;
	
	/**
	 * Débit maximum de la turbine
	 */
	private double maxM3;
	
	/**
	 * Puissance produite par la turbine
	 */
	private Double puissanceGenere;
	
	/**
	 * Débit utilisé par la turbine
	 */
	private Double debitUtilise;
	
	/**
	 * Nombre de colonnes dans {@code tab}
	 */
	private int tailleTabColonne;

	/**
	 * Nombre de lignes dans {@code tab}
	 */
	private int tailleTabLigne;
	
	/**
	 * Tableau permettant de calculer la puissance 
	 * en fonction des débits disponibles et alloués
	 */
	private Double[][] tab;
	
	/**
	 * Stocke la meilleure allocation de débit pour la turbine à partir d'un débit donné
	 * <Débit total disponible, Débit à allouer à la turbine>
	 */
	private HashMap<Double, Double> bestAllcationAtDebit = new HashMap();
	
	/**
	 * Stocke la meilleure puissance produite (totale, avec les turbines précédentes) pour la turbine à partir d'un débit donné
	 * <Débit total disponible, Puissance produite par turbine + turbines précédentes>
	 */
	private HashMap<Double, Double> bestProductionAtDebit = new HashMap();
	
	
	/**
	 * Constructeur
	 * @param active 		Activation initiale
	 * @param maxDebitM3	Debit turbiné maximum pour la turbine
	 */
	public Turbine(boolean active, int maxDebitM3) {
		this.active = active;
		this.maxM3 = maxDebitM3;
		this.tailleTabColonne = maxDebitM3/DISCRETISATION + 1;
		//this.tab = new Double[tailleTab][tailleTab];
		puissanceGenere = 0.0;
		debitUtilise = 0.0;
		//TODO initialiser tab à 0 ? ou faire un if dans le for et mettre à 0 quand >debitDisponible
	}
	
	/**
	 * Accesseur en lecture
	 * @return	{@code active}
	 */
	public boolean getActive() {
		return active;
	}
	
	/**
	 * Accesseur en lecture
	 * @return	Le débit max de la turbine {@code maxM3}
	 */
	public double getDebitMax() {
		return maxM3;
	}
	
	/**
	 * Accesseur en lecture
	 * @return	La matrice de puissance en fonction du bébit disponible/attribué {@code tab}
	 */
	public Double[][] getTab(){
		return tab;
	}
	
	/**
	 * Accesseur en lecture
	 * @return La puissance générée par la turbine
	 * @throws PuissanceNotSetException Si la puissance n'est pas définie
	 */
	public double getPuissanceGeneree() throws PuissanceNotSetException {
		if(puissanceGenere == null) throw new PuissanceNotSetException();
		return this.puissanceGenere;
	}
	
	/**
	 * Accesseur en lecture
	 * @return Le débit utilisé par la turbine
	 * @throws DebitNotSetException Si le débit n'est pas définie
	 */
	public double getDebitUtilise() throws DebitNotSetException {
		if(debitUtilise == null) throw new DebitNotSetException();
		return this.debitUtilise;
	}
	
	/**
	 * Rempli le tableau {@code tab} en fonction du débit disponible, de l'élévation amont
	 * et de la turbine précédente.
	 * @param debitDisponible 	Débit disponible maximum pour le turbinage
	 * @param elevationAmont 	Elevation Amont
	 * @param t					Turbine précédente (qui doit avoir été traitée)
	 */
	public void remplirTableau(double debitDisponible, double elevationAmont, Turbine t) {
		//Defintion de la taille tu tableau en fonction du débit à répartir.
		tailleTabLigne = ((Double) (Math.floor((debitDisponible/DISCRETISATION)) + 1)).intValue();
		this.tab = new Double[tailleTabLigne][tailleTabColonne];
		
		//Bornes
		if(/*debitDisponible == 0 || */this.active == false) {
			double debitRestant = debitDisponible;
			for(int i = 0 ; i < tailleTabLigne; i++) {
				for(int j = 0 ; j < tailleTabColonne; j++) {
					tab[i][j] = 0.0 + t.getBestProductionAtDebit(debitDisponible);
				}
			}
			setBestValues();
			return;
		}
		
		//Boucle de calcul avec rendement des turbines précédentes
		double hChute;
		for(int debitRestant = 0;debitRestant <= debitDisponible; debitRestant+=DISCRETISATION) {
				tab[debitRestant/DISCRETISATION][0] = 0.0 + t.getBestProductionAtDebit(debitRestant);
				for(int debitAAllouer = DISCRETISATION; debitAAllouer <= debitRestant ; debitAAllouer+=DISCRETISATION) {
					if(debitAAllouer <= getDebitMax()) {
						hChute = Elevation.hauteurChute(elevationAmont, debitDisponible, debitAAllouer);
						tab[debitRestant/DISCRETISATION][debitAAllouer/DISCRETISATION] = rendement(hChute, debitAAllouer) + t.getBestProductionAtDebit(debitRestant - debitAAllouer);
					}else {
						hChute = Elevation.hauteurChute(elevationAmont, debitDisponible, getDebitMax());
						tab[debitRestant/DISCRETISATION][((Double)getDebitMax()).intValue()/DISCRETISATION] = rendement(hChute, getDebitMax()) + t.getBestProductionAtDebit(debitRestant - getDebitMax());
					}
				}
			
			
		}
		setBestValues();
	}
	
	/**
	 * Rempli le tableau {@code tab} en fonction du débit disponible et de l'élévation amont.
	 * Il n'y a pas de turbines précédente, c'est pour l'initialisation.
	 * @param debitDisponible 	Débit disponible maximum pour le turbinage
	 * @param elevationAmont 	Elevation Amont
	 */
	public void remplirTableau(double debitDisponible, double elevationAmont) {
		//Defintion de la taille tu tableau en fonction du débit à répartir.
		tailleTabLigne = ((Double) (Math.floor((debitDisponible/DISCRETISATION)) + 1)).intValue();
		this.tab = new Double[tailleTabLigne][tailleTabColonne];
		
		//Bornes
		if(debitDisponible == 0 || this.active == false) {
			for(int i = 0 ; i < tailleTabLigne; i++) {
				for(int j = 0 ; j < tailleTabColonne; j++) {
					tab[i][j] = 0.0;
				}
			}
			setBestValues();
			return;
		}
		
		//Boucle de calcul
		double hChute;
		for(int debitRestant = 0; debitRestant <= debitDisponible; debitRestant+=DISCRETISATION) {
				tab[debitRestant/DISCRETISATION][0] = 0.0;
				for(int debitAAllouer = DISCRETISATION; debitAAllouer <= debitRestant ; debitAAllouer+=DISCRETISATION) {
					if(debitAAllouer <= getDebitMax()) {
						hChute = Elevation.hauteurChute(elevationAmont, debitDisponible, debitAAllouer);
						tab[debitRestant/DISCRETISATION][debitAAllouer/DISCRETISATION] = rendement(hChute, debitAAllouer);
					}else {
						hChute = Elevation.hauteurChute(elevationAmont, debitDisponible, getDebitMax());
						tab[debitRestant/DISCRETISATION][((Double)getDebitMax()).intValue()/DISCRETISATION] = rendement(hChute, getDebitMax());
					}
				}
		}
		
		setBestValues();
	}
	
	/**
	 * Rempli les collections {@code bestAllcationAtDebit} et {@code bestProductionAtDebit}
	 * par rapport au tableau {@code tab}. Pour chaque ligne du tableau, on regarde la production 
	 * maximum en fonction de l'attribution, et on sauvegarde les meilleurs valeurs.
	 * Comme on regarde par ligne, on sauvegarde la première meilleure valeure en cas d'égalité ce qui permet de garder
	 * de l'eau.
	 */
	private void setBestValues() {
		double bestAllocation;
		double bestProduction;	
		double debitMax = (tailleTabLigne-1) * DISCRETISATION + 1;
		int debit;
		
		//Boucle de recherche
		for(int debitRestant = 0; debitRestant <= debitMax; debitRestant+=DISCRETISATION) {
			bestProduction = tab[debitRestant/DISCRETISATION][0];
			bestAllocation = 0;
			for(int debitAAllouer = 0; debitAAllouer <= debitRestant; debitAAllouer+=DISCRETISATION) {
				debit = (int) ((debitAAllouer > getDebitMax()) ? getDebitMax() : debitAAllouer);
				if(tab[debitRestant/DISCRETISATION][debit/DISCRETISATION] != null) {
					
					if(tab[debitRestant/DISCRETISATION][debit/DISCRETISATION] > bestProduction) {
						bestProduction = tab[debitRestant/DISCRETISATION][debit/DISCRETISATION];
						bestAllocation = debitAAllouer;
					}
				}
			}
			
			bestAllcationAtDebit.put((double) debitRestant, bestAllocation);
			bestProductionAtDebit.put((double) debitRestant, bestProduction);
		}
		return;
	}
	
	/**
	 * Recherche de la meilleure allocation de débit pour la turbine 
	 * en fonction du débit disponible entrant
	 * @param debit	Le débit maximum disponible
	 * @return		Le débit à allouer à la turbine pour une production maximum
	 */
	public double getBestAllocationAtDebit(double debit) {
		//Permet de convertir pour la discrétisation
		debit = debit - debit%DISCRETISATION;
		return bestAllcationAtDebit.get(debit);
	}
	
	/**
	 * Recherche de la meilleure production d'electricité pour la turbine 
	 * en fonction du débit disponible entrant
	 * @param debit	Le débit maximum disponible
	 * @return		La production de la turbine (+ la production des turbines précédentes)
	 */
	public double getBestProductionAtDebit(double debit) {
		//Permet de convertir pour la discrétisation
		debit = debit - debit%DISCRETISATION;
		return bestProductionAtDebit.get(debit);
	}

	/**
	 * Calcul le rendement de la turbine, en fonction de la hauteure de chute 
	 * et du débit total turbiné à la centrale
	 * @param hChute	Hauteur de chute
	 * @param debit		Débit total à turbiner (Qtot)
	 * @return
	 */
	protected abstract Double rendement(double hChute, double debit);

	/**
	 * Active la turbine
	 * @param active	True si on active la turbine, false sinon
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Setter du débit maximum turbiné possible
	 * @param debitMaxM3
	 */
	public void setDebitMax(double debitMaxM3) {
		this.maxM3 = debitMaxM3;
		
	}
	
	/**
	 * Affichage textuel de la turbine
	 */
	public String toString() {
		return "Debit : " + this.debitUtilise + " Puissance : " + this.puissanceGenere;
	}

	/**
	 * Setter du débit turbiné par la turbine
	 * @param debit
	 */
	public void setDebitUtilise(double debit) {
		this.debitUtilise = debit;
		
	}

	/**
	 * Setter de la puissance généré par la turbine
	 * @param production
	 */
	public void setPuissanceGeneree(double production) {
		this.puissanceGenere = production;
	}

	/**
	 * Réinitialise les valeurs de la turbine
	 */
	public void reinit() {
		setDebitUtilise(0.0);
		setPuissanceGeneree(0.0);
		tab= null;
		tailleTabLigne = -1;
		
	}
}
