package tp2;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import tp2.exceptions.DebitNotSetException;
import tp2.turbines.*;


/**
 * Application principale
 * @author cheva
 *
 */
public class Main {
	
	/**
	 * Liste des turbines
	 */
	private ArrayList<Turbine> turbines = new ArrayList();
	
	/**
	 * D�bit non turbin�
	 */
	private double reste;
	
	/**
	 * Ajoute la turbine � la liste
	 * @param t	La turbine � ajouter
	 */
	public void addTurbine(Turbine t) {
		turbines.add(t);
	}
	
	/**
	 * Active la turbine � l'index d�sign� dans la liste
	 * @param index
	 */
	public void activateTurbine(int index) {
		if(index < turbines.size())
			turbines.get(index).setActive(true);
	}
	
	/**
	 * Desactive  la turbine � l'index d�sign� dans la liste
	 * @param index
	 */
	public void desactivateTurbine(int index) {
		if(index < turbines.size())
			turbines.get(index).setActive(false);
	}
	
	/**
	 * Defini le d�bit turbin� maximum possible pour la turbine � l'index design�
	 * @param index			L'index de la turbine � modifier
	 * @param debitMaxM3	Le d�bit maximum
	 */
	public void setDebitMaxTurbine(int index, double debitMaxM3) {
		if(index < turbines.size())
			turbines.get(index).setDebitMax(debitMaxM3);
	}
	
	/**
	 * Boucle principale de l'algorithme. Effectue la r�curtion initiale pour d�finir les tableaux de 
	 * chaque turbine.
	 * @param elevationAmont			Elevation amont
	 * @param debitARepartir			Qtot � r�partir
	 * @throws DebitNotSetException		Exception si une des turbines n'as pas son debit � la fin
	 */
	public void recursion(double elevationAmont, double debitARepartir) throws DebitNotSetException {
		reinitialiser();
		
		//On cherche la premi�re turbine acitv�e
		int premiereTurbine = 0;
		while(premiereTurbine < turbines.size() && !turbines.get(premiereTurbine).getActive()) premiereTurbine++;
		if(premiereTurbine >= turbines.size()) {
			this.reste = debitARepartir;
			return;
		}
		
		//On initialise le traitement
		Turbine initiale = turbines.get(premiereTurbine);
		initiale.remplirTableau(debitARepartir, elevationAmont);
		
		//On fait la r�curstion
		int derniereTurbine = premiereTurbine;
		for(int i = premiereTurbine+1; i < turbines.size() ; i ++) {
			//On passe les turbines inactives
			if(!turbines.get(i).getActive()) {
				continue;
			}
			turbines.get(i).remplirTableau(debitARepartir, elevationAmont, turbines.get(derniereTurbine));
			derniereTurbine = i;
		}
		
		//On lance la r�cursion arri�re pour trouver les valeurs
		recursionArriere(debitARepartir);
	}
	
	/**
	 * Permet de r�initialiser toutes les turbines
	 */
	private void reinitialiser() {
		for (Turbine turbine : turbines) {
			turbine.reinit();
		}
	}

	/**
	 * Retrouve les meilleurs valeurs via la r�cursion arri�re
	 * @param debit						Qtot � turbiner
	 * @throws DebitNotSetException		Si une des turbines n'a pas son d�bit de set
	 */
	private void recursionArriere(double debit) throws DebitNotSetException {
		double allocation, productionPrecedente, production = 0;
		
		//On passe les turbines inactives
		int premiereTurbine = turbines.size() -1;
		while(!turbines.get(premiereTurbine).getActive()) premiereTurbine--;
		
		//On fait la r�cursion arri�re
		int turbinePrecedente;
		for(int i = premiereTurbine; i >=0 ; i--) {
			//On passe les trubines inactives
			if(turbines.get(i).getActive()) {
				allocation = turbines.get(i).getBestAllocationAtDebit(debit);
				debit = debit - allocation;
				turbines.get(i).setDebitUtilise(allocation);
				
				//Permet de traiter diff�rement la derni�re turbine
				if(i != 0) {
					//On passe les turbines incatives
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
	
	/**
	 * Affichage textuel des r�sultats
	 */
	public void resultats() {
		for (Turbine turbine : turbines) {
			System.out.println(turbine);
		}
		System.out.println("Restes : " + reste);
	}

	public static void main(String[] args) throws IOException {
		
		// Read XSL file
        FileInputStream inputStream = new FileInputStream(new File("partie2.xlsx"));
 
        // Get the workbook instance for XLS file
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
 
        // Get first sheet from the workbook
        XSSFSheet sheet = workbook.getSheetAt(0);
 
        // Get iterator to all the rows in current sheet
        Iterator<Row> rowIterator = sheet.iterator();
        
        Double elevationAmont;
        Double debitARepartir;
        
        while (rowIterator.hasNext()) {
        	Main application = new Main();
        	Row row = rowIterator.next();
            row.getCell(0);
            //pour t1 
            if( row.getCell(2).getNumericCellValue() != 0) {
            	Turbine t1 = new Turbine1(true, 165);
            	application.addTurbine(t1);
            }else {
            	Turbine t1 = new Turbine1(false, 165);
            	application.addTurbine(t1);
            }
          //pour t2 
            if( row.getCell(3).getNumericCellValue() != 0) {
            	Turbine t2 = new Turbine2(true, 165);
            	application.addTurbine(t2);
            }else {
            	Turbine t2 = new Turbine2(false, 165);
            	application.addTurbine(t2);
            }
          //pour t3 
            if( row.getCell(4).getNumericCellValue() != 0) {
            	Turbine t3 = new Turbine3(true, 165); 
            	application.addTurbine(t3);
            }else {
            	Turbine t3 = new Turbine3(false, 165);
            	application.addTurbine(t3);
            }
          //pour t4 
            if( row.getCell(5).getNumericCellValue() != 0) {
            	Turbine t4 = new Turbine4(true, 165);
            	application.addTurbine(t4);
            }else {
            	Turbine t4 = new Turbine4(false, 165);
            	application.addTurbine(t4);
            }
          //pour t5 
            if( row.getCell(6).getNumericCellValue() != 0) {
            	Turbine t5 = new Turbine5(true, 165);
            	application.addTurbine(t5);
            }else {
            	Turbine t5 = new Turbine5(false, 165);
            	application.addTurbine(t5);
            }
            elevationAmont =  row.getCell(1).getNumericCellValue();
            debitARepartir = row.getCell(0).getNumericCellValue();
            
            try {
				application.recursion(elevationAmont, debitARepartir);
				application.resultats();
				
			} catch (DebitNotSetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            
        }
            

		// TODO Auto-generated method stub

		//Main application = new Main();
		//Turbine t1 = new Turbine1(true, 165);
		//Turbine t2 = new Turbine2(true, 165);
		//Turbine t3 = new Turbine3(true, 165);
		//Turbine t4 = new Turbine4(false, 165);
		//Turbine t5 = new Turbine5(true, 165);
		
		//application.addTurbine(t1);
		//application.addTurbine(t2);
		//application.addTurbine(t3);
		//application.addTurbine(t4);
		//application.addTurbine(t5);
		
		//try {
		//	application.recursion(172.11, 549.958);
		//	application.resultats();
			
		//	application.recursion(172.11, 600);
		//	application.resultats();
			
		//} catch (DebitNotSetException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		
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
	
	
	/**
	 * Affiche le tableau de donn�es de la turbine t
	 * @param t		La turbine dont on veut afficher le tableau
	 */
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
