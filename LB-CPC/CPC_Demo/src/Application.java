import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Application {

	public static void main(String[] args) {
		
		try {
			String dataset = args[0];
			
			if (args[0].contains("seed"))
				ProblemParameters.DATASET_SEED = Integer.parseInt(args[0].split("=")[1]);
			
			if (args[0].contains("file"))
				ProblemParameters.DATASET_FILE = args[0].split("=")[1];
			
			DriverFactory.toCSV();
			int[] constraints = {10};


			for (int c : constraints) {
				ProblemParameters.CUSTOMCONSTRAINTS=c;
				//			ArrayList<String> constraints = ;

				
				
				//DriverFactory.setSeed();
				
				System.out.println("Command Options: ");
				System.out.println("1: Run GA");
				System.out.println("2: Run BRD");
				System.out.println("?: Display");
				System.out.println("0: Quit");
				try (Scanner scan = new Scanner(System.in)) 
				{
					String choice = scan.nextLine();
					do {
					switch (choice){
					    case "1":
					    	Individual.setConstraints(RandomBasil.getBasil());
					    	Player.setGA(true);
					    	GA.run(true,1);
					    	choice = "0";
					        break;
					    case "2":
					    	
					    	Player.setGA(false);
					    	if(Player.getGA() == false) {
					    	Player.setConstraints(RandomBasil.getBasil());
					    	BRD.runBRD(true, 1);
					    	}
					    	choice = "0";
					    	//BRD.improveSol();
					        break;
					  
					    case "?":
					            System.out.println("Command Options: ");
								System.out.println("1: Run GA");
								System.out.println("2: Run BRD");
								System.out.println("?: Display");
								System.out.println("0: Quit");
					            break;
					        }  }while (!choice.equals("0"));
					}

				}
				
				
				
			

		} catch (Exception e) {
			e.printStackTrace();
		}
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
