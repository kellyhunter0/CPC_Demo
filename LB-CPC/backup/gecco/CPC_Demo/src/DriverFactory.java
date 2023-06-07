import java.util.ArrayList;
import java.util.Random;

public class DriverFactory {
	private static ArrayList<Driver> drivers;
	private static int id;
	private static Random r = null;//Seeded rng

//	public static void setSeed(int seed) {
//		r = new Random(86);
//	}
//	
	public static ArrayList<Driver> getDriverList(){
		if (drivers ==null)
			initialise();
		
		return drivers;
	}
	
	private static void initialise() {
		r = new Random(ProblemParameters.DATASET_SEED);
		//Create a random list of  drivers
//		System.out.println("Driver List");
//		System.out.println("ID, ExpiresWeek,FinalYear,group");
		drivers = new ArrayList<Driver>();
		int id=0;
		newDrivers(50, Group.OFF);
		newDrivers(24, Group.LMC);
		newDrivers(750, Group.CEN);
		newDrivers(400, Group.MAR);
		newDrivers(450, Group.LON);
		newDrivers(140, Group.LCB);
		newDrivers(140, Group.ECB);
		newDrivers(60, Group.EBT);
		
	}
	
	private static void newDrivers(int q, Group g) {
		for (int x=0; x < q; x++) {
			int week = r.nextInt(ProblemParameters.WEEKS-1)+1;
			int[] duties = new int[ProblemParameters.WEEKS];
			for (int d =0; d <duties.length; d++)
				duties[d] = r.nextInt(2)+1; //1-2
			
			boolean finalyear= false;
			if (r.nextInt(5)==0)
				finalyear = true;
			
 			try {
				Driver d = new Driver(""+id,/*ID*/
						week, /*Expiry week*/
						finalyear,/*True if license expires this year */
						g,/*staff group */
						duties
						);
				drivers.add(d);
//				System.out.println(d);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			id++;
		}
	}
}
