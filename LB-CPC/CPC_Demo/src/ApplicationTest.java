import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ApplicationTest {

	public static void main(String[] args) {
		try {
			ProblemParameters.DATASET_SEED = 135;

			ProblemParameters.CUSTOMCONSTRAINTS=10;
					//			ArrayList<String> constraints = ;

					Individual.setConstraints(RandomBasil.getBasil());
			//DriverFactory.setSeed();
			GA.run(true,1);


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
