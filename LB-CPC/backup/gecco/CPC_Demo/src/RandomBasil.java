import java.util.ArrayList;
import java.util.Random;

public class RandomBasil {

	public static void main(String[] args) {
	}
	public static ArrayList<String> getBasil() {
		ArrayList<String> res = new ArrayList<String>();
		Random rnd = new Random();
		ArrayList<Driver> drivers = DriverFactory.getDriverList();
		
		ArrayList<Group> groups = new ArrayList<Group>();
		for (Group g : Group.values())
			groups.add(g);
		
		while(res.size()<ProblemParameters.CUSTOMCONSTRAINTS) {
			String statement = "";//"driver 226 must not be after week 23 with low priority";
			Driver d = null;
			if ((rnd.nextBoolean())||(groups.size()==0)) {
				d = drivers.get(rnd.nextInt(drivers.size()));
				statement = "driver " + d.getID();
			}
			else {
				//statement = "group " +"ebt";
				Group g = groups.remove(rnd.nextInt(groups.size()));
				statement = "group " +g.name();
			}

			String not ="";
			if (rnd.nextBoolean())
				not = "not";

			String op = "in";

			int o = rnd.nextInt(3);
			if (o==1)
				op ="in";
			if(o==2)
				op="before";
			if((o==0)) {
				op="after";
			}
			statement = statement +" must "+not+" be "+op;
			
			String week = "" +rnd.nextInt(ProblemParameters.WEEKS);
			if (week.length()<2)
				week = "0" + week;
			statement = statement +" week " +week;

			int p = rnd.nextInt(3);
			if (p==0)
				statement = statement +" with low priority";
			if (p==1)
				statement = statement +" with medium priority";
			if (p==2)
				statement = statement +" with high priority";

//			System.out.println(statement);
			
			try {
				CustomConstraint cp = Parser.parse(statement);
//				System.out.println("Compiled OK\n"+cp.getRegex());
				res.add(statement);
			} catch (Exception e) {

				// TODO Auto-generated catch block
			//	System.out.println(e.getMessage());
				

			}

		}
		return res;

	}

}
