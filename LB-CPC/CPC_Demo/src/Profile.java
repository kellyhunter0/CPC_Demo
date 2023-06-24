import java.util.ArrayList;
import java.util.Random;

public class Profile {

	public Player player;
	public Driver driver;
	public TrainingSlot slot;
	private Random r;
	
	public Profile() {
		
	}
	public Profile(int week, int day, int no, String id, Group g, Strategy s) {
		// TODO Auto-generated constructor stub
		ArrayList<Driver> driverList = new ArrayList<Driver>();
		boolean finalYear = false;
		r = new Random();
		if(slot == null && driver == null) {
			try {
				slot = new TrainingSlot(week, day, no);
				DriverFactory.newDrivers(ProblemParameters.POP_SIZE, g);
				driverList = DriverFactory.getDriverList();
				for(Driver d : driverList) {
					int[] duties = new int[ProblemParameters.WEEKS];
					for(int i=0;i<duties.length;i++) {
						duties[i] = r.nextInt(2)+1;
					}
					//duties = 
					driver = new Driver(id, week, finalYear, d.group(), duties);
					//String st = driver.toCSV();
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
		week = slot.getWeek();
		day = slot.getDay();
		no = slot.getNo();
		id = driver.getID();
		g = driver.group();
		s = Strategy.SWAP_FREE; // default
		}
	}
}
