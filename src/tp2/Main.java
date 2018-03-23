package tp2;

import tp2.turbines.Turbine;
import tp2.turbines.Turbine1;

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
	}

}
