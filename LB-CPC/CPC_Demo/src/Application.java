import java.io.BufferedReader;

import static java.lang.System.exit;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Application {

	public static void main(String[] args) {
		
		String[] options = {"1- Run GA",
			                "2- Run BRD",
			                "0- Exit",
};
		
		try {
			String dataset = args[0];
			
			if (args[0].contains("seed"))
				ProblemParameters.DATASET_SEED = Integer.parseInt(args[0].split("=")[1]);
			
			if (args[0].contains("file"))
				ProblemParameters.DATASET_FILE = args[0].split("=")[1];
			
			DriverFactory.toCSV();
			int[] constraints = {4};


			for (int c : constraints) {
				ProblemParameters.CUSTOMCONSTRAINTS=c;
				//ArrayList<CustomConstraint> customConstraints = new ArrayList<CustomConstraint>();				
				//DriverFactory.setSeed();
				
				
		        Scanner scanner = new Scanner(System.in);
		        int option = 1;
		        while (option!=0){
		            printMenu(options);
			            try {
			                option = scanner.nextInt();
			                switch (option){
		                    case 1: 
		                    	runGAOption();option=0; 
		                    break;
		                    case 2: 
		                    	runBRDOption();option=0; 
		                    break;
		                    case 0: 
		                    	exit(0);
		                }
		            }
		            catch (InputMismatchException ex){
		                System.out.println("Please enter an integer value between 1 and " + options.length);
		                scanner.next();
		            }
		            catch (Exception ex){
		                System.out.println("An unexpected error happened. Please try again");
		                scanner.next();
		            }
		        }

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
   public static void printMenu(String[] options){
        for (String option : options){
            System.out.println(option);
        }
        System.out.print("Choose your option : ");
   }
	   
	// Options
   private static void runGAOption() {
	        System.out.println("Running GA");
	    	Individual.setConstraints(RandomBasil.getBasil());
	    	Player.setGA(true);
	    	try {
				GA.run(true,10);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
   }
   private static void runBRDOption() {
	        System.out.println("Running BRD");
	    	Player.setGA(false);
	    	if(Player.getGA() == false) {
	    	Player.setConstraints(RandomBasil.getBasil());

				BRD.runBRD(true, 10);

	   	}
	}
	    private static void option3() {
	        System.out.println("Thanks for choosing option 3");
	    }
	
	public static ArrayList<String> readConstraints() {

		ArrayList<String> res = new ArrayList<String>();
		String fileName = "./constraints.txt";


		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = br.readLine()) != null) {
				res.add(line);


			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;

	}
}
