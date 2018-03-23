package tp2;

import tp2.turbines.Turbine;
import tp2.turbines.Turbine1;
import tp2.turbines.Turbine2;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Turbine t1 = new Turbine1(false, 180);
		t1.remplirTableau(150, 170);
		
		Double[][] test = t1.getTab();
		
		for(int i = 0 ; i < test.length ; i++) {
			for(int j = 0 ; j < test[i].length ; j++) {
				System.out.print(test[i][j] + "   ");
			}
			System.out.println("");
		}
		System.out.println("Fin");
		
		
		
		Turbine t2 = new Turbine2(true, 180);
		t2.remplirTableau(200, 170);
		
		Double[][] test2 = t2.getTab();
		
		for(int i = 0 ; i < test2.length ; i++) {
			for(int j = 0 ; j < test2[i].length ; j++) {
				System.out.print(test2[i][j] + "   ");
			}
			System.out.println("");
		}
		System.out.println("Fin");
	}

}
