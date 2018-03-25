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
		reinitialiser();
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
	
	
	private void reinitialiser() {
		for (Turbine turbine : turbines) {
			turbine.reinit();
		}
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
