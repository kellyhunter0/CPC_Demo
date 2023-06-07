import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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

				Individual.setConstraints(RandomBasil.getBasil());
				//DriverFactory.setSeed();
				GA.run(true,1);
				
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
