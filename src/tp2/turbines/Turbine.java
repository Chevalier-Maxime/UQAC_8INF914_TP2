package tp2.turbines;

import java.util.HashMap;

import tp2.elevation.Elevation;
import tp2.exceptions.DebitNotSetException;
import tp2.exceptions.PuissanceNotSetException;

/**
 * Cette classe repr�sente une turbine et s'occupe de remplir le tableau de production 
 * associ� � la turbine.
 * Il faut d�finir la m�thode rendement (qui calcul le rendement de la turbine) pour d�finir une turbine.
 * @author cheva
 *
 */
public abstract class Turbine {
	
	/**
	 * Permet de d�finir la discretisation du d�bit dans les calculs
	 */
	public final static int DISCRETISATION = 5;

	/**
	 * D�finit si la turbine est active
	 */
	private boolean active;
	
	/**
	 * D�bit maximum de la turbine
	 */
	private double maxM3;
	
	/**
	 * Puissance produite par la turbine
	 */
	private Double puissanceGenere;
	
	/**
	 * D�bit utilis� par la turbine
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
	 * en fonction des d�bits disponibles et allou�s
	 */
	private Double[][] tab;
	
	/**
	 * Stocke la meilleure allocation de d�bit pour la turbine � partir d'un d�bit donn�
	 * <D�bit total disponible, D�bit � allouer � la turbine>
	 */
	private HashMap<Double, Double> bestAllcationAtDebit = new HashMap();
	
	/**
	 * Stocke la meilleure puissance produite (totale, avec les turbines pr�c�dentes) pour la turbine � partir d'un d�bit donn�
	 * <D�bit total disponible, Puissance produite par turbine + turbines pr�c�dentes>
	 */
	private HashMap<Double, Double> bestProductionAtDebit = new HashMap();
	
	
	/**
	 * Constructeur
	 * @param active 		Activation initiale
	 * @param maxDebitM3	Debit turbin� maximum pour la turbine
	 */
	public Turbine(boolean active, int maxDebitM3) {
		this.active = active;
		this.maxM3 = maxDebitM3;
		this.tailleTabColonne = maxDebitM3/DISCRETISATION + 1;
		//this.tab = new Double[tailleTab][tailleTab];
		puissanceGenere = 0.0;
		debitUtilise = 0.0;
		//TODO initialiser tab � 0 ? ou faire un if dans le for et mettre � 0 quand >debitDisponible
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
	 * @return	Le d�bit max de la turbine {@code maxM3}
	 */
	public double getDebitMax() {
		return maxM3;
	}
	
	/**
	 * Accesseur en lecture
	 * @return	La matrice de puissance en fonction du b�bit disponible/attribu� {@code tab}
	 */
	public Double[][] getTab(){
		return tab;
	}
	
	/**
	 * Accesseur en lecture
	 * @return La puissance g�n�r�e par la turbine
	 * @throws PuissanceNotSetException Si la puissance n'est pas d�finie
	 */
	public double getPuissanceGeneree() throws PuissanceNotSetException {
		if(puissanceGenere == null) throw new PuissanceNotSetException();
		return this.puissanceGenere;
	}
	
	/**
	 * Accesseur en lecture
	 * @return Le d�bit utilis� par la turbine
	 * @throws DebitNotSetException Si le d�bit n'est pas d�finie
	 */
	public double getDebitUtilise() throws DebitNotSetException {
		if(debitUtilise == null) throw new DebitNotSetException();
		return this.debitUtilise;
	}
	
	/**
	 * Rempli le tableau {@code tab} en fonction du d�bit disponible, de l'�l�vation amont
	 * et de la turbine pr�c�dente.
	 * @param debitDisponible 	D�bit disponible maximum pour le turbinage
	 * @param elevationAmont 	Elevation Amont
	 * @param t					Turbine pr�c�dente (qui doit avoir �t� trait�e)
	 */
	public void remplirTableau(double debitDisponible, double elevationAmont, Turbine t) {
		//Defintion de la taille tu tableau en fonction du d�bit � r�partir.
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
		
		//Boucle de calcul avec rendement des turbines pr�c�dentes
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
	 * Rempli le tableau {@code tab} en fonction du d�bit disponible et de l'�l�vation amont.
	 * Il n'y a pas de turbines pr�c�dente, c'est pour l'initialisation.
	 * @param debitDisponible 	D�bit disponible maximum pour le turbinage
	 * @param elevationAmont 	Elevation Amont
	 */
	public void remplirTableau(double debitDisponible, double elevationAmont) {
		//Defintion de la taille tu tableau en fonction du d�bit � r�partir.
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
	 * Comme on regarde par ligne, on sauvegarde la premi�re meilleure valeure en cas d'�galit� ce qui permet de garder
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
	 * Recherche de la meilleure allocation de d�bit pour la turbine 
	 * en fonction du d�bit disponible entrant
	 * @param debit	Le d�bit maximum disponible
	 * @return		Le d�bit � allouer � la turbine pour une production maximum
	 */
	public double getBestAllocationAtDebit(double debit) {
		//Permet de convertir pour la discr�tisation
		debit = debit - debit%DISCRETISATION;
		return bestAllcationAtDebit.get(debit);
	}
	
	/**
	 * Recherche de la meilleure production d'electricit� pour la turbine 
	 * en fonction du d�bit disponible entrant
	 * @param debit	Le d�bit maximum disponible
	 * @return		La production de la turbine (+ la production des turbines pr�c�dentes)
	 */
	public double getBestProductionAtDebit(double debit) {
		//Permet de convertir pour la discr�tisation
		debit = debit - debit%DISCRETISATION;
		return bestProductionAtDebit.get(debit);
	}

	/**
	 * Calcul le rendement de la turbine, en fonction de la hauteure de chute 
	 * et du d�bit total turbin� � la centrale
	 * @param hChute	Hauteur de chute
	 * @param debit		D�bit total � turbiner (Qtot)
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
	 * Setter du d�bit maximum turbin� possible
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
	 * Setter du d�bit turbin� par la turbine
	 * @param debit
	 */
	public void setDebitUtilise(double debit) {
		this.debitUtilise = debit;
		
	}

	/**
	 * Setter de la puissance g�n�r� par la turbine
	 * @param production
	 */
	public void setPuissanceGeneree(double production) {
		this.puissanceGenere = production;
	}

	/**
	 * R�initialise les valeurs de la turbine
	 */
	public void reinit() {
		setDebitUtilise(0.0);
		setPuissanceGeneree(0.0);
		tab= null;
		tailleTabLigne = -1;
		
	}
}
