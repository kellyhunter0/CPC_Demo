import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;

public class Individual {
	private Random rnd = new Random();
	private ArrayList<Gene> chromosome;
	private ArrayList<TrainingSlot> free;
	private boolean modified = true;
	private int fitness;
	private int expired=0;
	private int lates=0;
	private int groupImbalanced=0;
	private int lowConstraintViolations=0;
	private int mediumConstraintViolations=0;
	private int highConstraintViolations=0;
	private int[][][]participants = new int[ProblemParameters.WEEKS][][];
	private static ArrayList<CustomConstraint> customConstraints;
	private Player player = new Player();
	private Profile profile;
	
	

	private static int[] targets=null;//Targets from each group

	//	public static void loadConstraints() {
	//		//		constraintsPresent = new ArrayList<String>();
	//		//		constraintsNotPresent = new ArrayList<String>();
	//		customConstraints = new ArrayList<CustomConstraint>();
	//		String fileName = "./constraints.txt";
	//
	//
	//		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
	//			String line;
	//			int lineNo=1;
	//			while ((line = br.readLine()) != null) {
	//				//process line
	//				CustomConstraint cp=null;
	//				try {
	//					cp = Parser.parse(line);
	//
	//				}catch(Exception e) {
	//					System.out.println("Error reading constraints \nLine no " + lineNo +" "+ e.getMessage());
	//					System.exit(-1);
	//				}
	//				if (cp != null) {
	//					customConstraints.add(cp);
	//					//					char ch = line.charAt(0);
	//					//					line = line.substring(1);
	//					//					if (ch=='+') {
	//					//						constraintsPresent.add(line);
	//					//					}
	//					//					if (ch=='-') {
	//					//						constraintsNotPresent.add(line);
	//					//					}
	//				}
	//				lineNo++;
	//
	//			}
	//		} catch (FileNotFoundException e) {
	//			e.printStackTrace();
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		}
	//
	//	}
	public static void setConstraints(ArrayList<String> text) {
		//		constraintsPresent = new ArrayList<String>();
		//		constraintsNotPresent = new ArrayList<String>();
		customConstraints = new ArrayList<CustomConstraint>();


		int lineNo=1;
		for (String line : text) {
			//process line
			CustomConstraint cp=null;
			try {
				cp = Parser.parse(line);

			}catch(Exception e) {
				System.out.println("Error reading constraints \nLine no " + lineNo +" "+ e.getMessage());
				System.exit(-1);
			}
			if (cp != null) {
				customConstraints.add(cp);
			}
			lineNo++;
		}
	}
	public Individual() { // O(g * d), where g is the number of groups, and d is the number of drivers
		init();

		if (targets==null) {
			int tDrivers = DriverFactory.getDriverList().size();
			targets = new int[Group.values().length];
			for(Group g :Group.values()) {
				int count=0;
				for (Driver d : DriverFactory.getDriverList()) {
					if (d.group()==g)					
						count++;
				}
				float t =  (count/(float)tDrivers)*ProblemParameters.TRAINING_PLACES;
				targets[g.ordinal()] = Math.round(t);
				targets[g.ordinal()]++;
				//				System.out.println("Target " + g.name() + " "+targets[g.ordinal()]);
			}

		}

	}
	public int fitness() { // O(x * d * g), where x is the number of participants, g is the number of groups, and d is the number of days
		//		try {
		//			this.verify();
		//		} catch (Exception e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//			System.exit(-1);
		//		}

		if (modified) {
			ProblemParameters.EVALS++;
			modified = false;
			fitness =0;
			lates=0;
			expired=0;
			groupImbalanced=0;

			//Count participants from each group.
			for (int x=0;x < participants.length; x++ ) {
				for (int d=0; d < 7; d++) {
					for (int g =0; g < Group.values().length; g++ )
						participants[x][d][g] = 0;
				}
			}


			for (Gene g : chromosome) {
				
				if (checkLate(g)) {
					fitness = fitness + ProblemParameters.PENALTY_LATE_SHIFT;
					lates++;
				}
				//Check for training after expiry
				if (checkExpiry(g)) {
					fitness = fitness + ProblemParameters.PENALTY_EXPIRED_LICENSE;
					expired++;
				}else {
				//Encourage training as far before expiry as possible
					if (g.driver.finalYear())
						fitness = fitness + (g.slot.getWeek()/g.driver.getExpiresWeek())*ProblemParameters.PENALTY_ADVANCE_FINAL_WEEK;
				}
				//Log participation
				participants[g.slot.getWeek()][g.slot.getDay()][g.driver.group().ordinal()]++;

			}
			//Check for imbalance in participants
			checkImbalance();
			checkCustomConstraints();
		}
		return fitness;
	}
	
	private void checkCustomConstraints(Gene g, String intermediate) {
		
		String arrayListSource = "";
		boolean isFound = false;
		boolean violation = false;
		profile = new Profile();
		profile.player = player;
		
		for (CustomConstraint cp : customConstraints) {
			
			String[] res =  intermediate.split(":");
			String driver = res[2]; // driver ID
			String group = res[4]; // group name
			String[] temp = cp.getSource().split(" "); // allows checks to be made to see if it is a group or driver violation
					// find the intermediate string and see if there is a constraint that has been met
					Matcher matcher = cp.getPattern().matcher(intermediate);
					if (matcher.find()) { // finds a constraint that has been met
						arrayListSource = "Found " + g.driver.getID() + "," + g.driver.group().name() + "," + cp.getSource();
						System.out.println("\t\t Found " + arrayListSource ); // print out the source
						profile.player.addMetConstraints(arrayListSource); // add to list
						profile.driver = g.driver; // assign driver to a profile
						profile.slot = g.slot; // assign their slot to a profile
						isFound = true; // finds a constraint that has been met
						violation = false; // no violations found
					}
			
					else {
						 String playerVariables = "";
						if( cp.getSource().contains(g.driver.group().name())&& !cp.getSource().contains(g.driver.getID())) { // group only violations 
							arrayListSource = "" + g.driver.getID() + "," + g.driver.group().name() + "," + cp.getSource();
							profile.slot = g.slot;
							profile.driver = g.driver;
							Strategy strategy = profile.player.setStrategy(cp);
							playerVariables = g.slot.getWeek() + "," + g.slot.getDay() + "," + g.driver.getID() + "," + g.driver.group() + "," + cp.getSource() + "," +strategy;
							profile.player.addToPlayerVariables(playerVariables);
							profile.player.addToPlayerList(player);
							profile.player.addViolations("violation " + arrayListSource);
							System.out.println("\t Group Violation " + g.driver.getID() +" "+ g.driver.group().name() + cp.getSource());
							isFound = false;
							violation = true;
						}
						else if ((cp.getSource().contains(g.driver.getID()) && !cp.getSource().contains(g.driver.group().name()))) { // driver only violations
							arrayListSource = "" + g.driver.getID() + "," + g.driver.group().name() + "," + cp.getSource();
							profile.slot = g.slot;
							profile.driver = g.driver;
							//violations.add(arrayListSource);
							profile.player.setStrategy(cp);
							Strategy strategy = profile.player.setStrategy(cp);
							playerVariables = g.slot.getWeek() + "," + g.slot.getDay() + "," + g.driver.getID() + "," + g.driver.group() + "," + cp.getSource() + "," +strategy;
							profile.player.addToPlayerVariables(playerVariables);
							profile.player.addViolations("violation " + arrayListSource);
							profile.player.addToPlayerList(player);
							System.out.println("\t Driver Violation " +  g.driver.getID() + " " + g.driver.group().name() + cp.getSource());
							violation = true;
							isFound = false;
						}
						if(group.equals(temp[1]) && !driver.equals(temp[1])) {

							//System.out.println("\t No driver clash"+ ","+ g.slot.getWeek() + "," +g.slot.getDay() +"," + g.driver.getID() +"," +g.driver.group().name() + "," +g.driver.getDuty(g.slot.getWeek())+","+g.driver.getExpiresWeek() + ","+g.driver.finalYear());
						}
						else if (!group.equals(temp[1]) && driver.equals(temp[1])) {

							//System.out.println("\t No group clash"+ ","+ g.slot.getWeek() + "," +g.slot.getDay() +"," + g.driver.getID() +"," +g.driver.group().name() + "," +g.driver.getDuty(g.slot.getWeek())+","+g.driver.getExpiresWeek() + ","+g.driver.finalYear());
						}
					}
				}
		// no clash - there are no driver or group violations if they have no conflicts with the training week given
				if(isFound == false  && violation == false) {
					profile.slot = g.slot;
					profile.driver = g.driver;
					//profile.player.add
					profile.player.addOpenToSwap("free " + g.slot.getWeek() + "," +g.slot.getDay() +"," + g.driver.getID() +"," +g.driver.group().name() + "," +g.driver.getDuty(g.slot.getWeek())+","+g.driver.getExpiresWeek() + ","+g.driver.finalYear());
					Strategy strategy = profile.player.setStrategyNoConstraints();
					String playerVariables = g.slot.getWeek() + "," + g.slot.getDay() + "," + g.driver.getID() + "," + g.driver.group() + "," + "No Priority" + "," +strategy;
					profile.player.addToPlayerVariables(playerVariables);
					profile.player.addToPlayerList(player);
					//System.out.println("\t No clash"+ ","+ g.slot.getWeek() + "," +g.slot.getDay() +"," +g.driver.getID() +"," +g.driver.group().name() + "," +g.driver.getDuty(g.slot.getWeek())+","+g.driver.getExpiresWeek() + ","+g.driver.finalYear());
				//System.out.println(driver + " "+ group + " " + cp.getSource());
			}


			}
				

		
	
	
	private void checkCustomConstraints() {

		String[] intermediate = this.getIntermediate();
		for (CustomConstraint cp : customConstraints) {
				if (!cp.mustAppear()) {
				boolean f = false;

				for (String i : intermediate) {
					Matcher matcher = cp.getPattern().matcher(i);
					if (matcher.find()) {
						f = true;
					}
					else {
						String[] intermediateSplit = i.split(":");
						String id = intermediateSplit[2];
						String group = intermediateSplit[4];
						if(cp.getPriority() == ConstraintPriority.high && (cp.getSource().contains(id)|| cp.getSource().contains(group))) {
							highConstraintViolations++;
						}
						else if (cp.getPriority() == ConstraintPriority.medium && (cp.getSource().contains(id)|| cp.getSource().contains(group))) {
							mediumConstraintViolations++;
						}
						else if (cp.getPriority() == ConstraintPriority.low && (cp.getSource().contains(id)|| cp.getSource().contains(group))) {
							lowConstraintViolations++;
						}
						f = false;
					}
				}
				if(!f) {
				int p=0;		
					if (cp.getPriority() == ConstraintPriority.high) {
						p = ProblemParameters.PENALTY_HIGH_CONSTRAINT_VIOLATION * highConstraintViolations;
						fitness = fitness + p;
					}
					else if (cp.getPriority() == ConstraintPriority.medium ) {
						p = ProblemParameters.PENALTY_MEDIUM_CONSTRAINT_VIOLATION * mediumConstraintViolations;
						fitness = fitness + p;
					}
					else if (cp.getPriority() == ConstraintPriority.low ) {
						p = ProblemParameters.PENALTY_LOW_CONSTRAINT_VIOLATION  * lowConstraintViolations;
						fitness = fitness + p;
					}
					else {
					fitness = fitness + p;
					}
				}
			}else {
				boolean found = false;
				for (String i : intermediate) {
					Matcher matcher = cp.getPattern().matcher(i);
					if (matcher.find()) {
						found = true;
					}
					else {
						String[] intermediateSplit = i.split(":");
						String id = intermediateSplit[2];
						String group = intermediateSplit[4];
						if(cp.getPriority() == ConstraintPriority.high && (cp.getSource().contains(id)|| cp.getSource().contains(group))) {
							highConstraintViolations++;
						}
						else if (cp.getPriority() == ConstraintPriority.medium && (cp.getSource().contains(id)|| cp.getSource().contains(group))) {
							mediumConstraintViolations++;
						}
						else if (cp.getPriority() == ConstraintPriority.low && (cp.getSource().contains(id)|| cp.getSource().contains(group))) {
							lowConstraintViolations++;
						}
						found = false;
					}

				}
				
				if(!found) {
					int p=0;
					if (cp.getPriority() == ConstraintPriority.high) {
						p = ProblemParameters.PENALTY_HIGH_CONSTRAINT_VIOLATION * highConstraintViolations;
						fitness = fitness + p;
					}
					else if (cp.getPriority() == ConstraintPriority.medium ) {
						p = ProblemParameters.PENALTY_MEDIUM_CONSTRAINT_VIOLATION  * mediumConstraintViolations;
						fitness = fitness + p;
					}
					else if (cp.getPriority() == ConstraintPriority.low ) {
						p = ProblemParameters.PENALTY_LOW_CONSTRAINT_VIOLATION * lowConstraintViolations;
						fitness = fitness + p;						
					}
					else {
						fitness = fitness + p;
					}
				}
			}
		}
	}
	
	private void checkImbalance() { // O(p * d * g), where g is the number of groups, and d is the number of drivers
		for (int[][] p : participants) {
			for (int d=0; d < 7; d++) {
				for (Group g : Group.values()) {
					if (p[d][g.ordinal()]>targets[g.ordinal()]) {
						int over = p[d][g.ordinal()]-targets[g.ordinal()];
						groupImbalanced=groupImbalanced+over;
						fitness = fitness +(over *ProblemParameters.PENALTY_GROUP_IMBALANCE);
					}
				}
			}
		}
	}

	private boolean checkLate(Gene g) {
		if (g.driver.getDuty(g.slot.getWeek())==1) {
			return true;
		}
		return false;
	}

	public boolean checkExpiry(Gene g) {
		int week = g.slot.getWeek();
		if (g.driver.finalYear()) {
			if(week >  g.driver.getExpiresWeek()) {
				return true;
			}
		}
		return false;

	}

	public void initParticipants() {
		participants = new int[ProblemParameters.WEEKS][][];
		for (int x=0;x < participants.length; x++ ) {
			participants[x] = new int[7][];
			for (int d=0; d < 7; d++) {
				participants[x][d] = new int[Group.values().length];
			}
		}

	}
	public void init() {

		modified = true;
		initParticipants();
		//Random init
		free = new ArrayList<TrainingSlot>();
		chromosome = new ArrayList<Gene>();

		try {
			free.addAll(SlotFactory.getSlotSet() ) ;

			//Now create genes
			for (Driver d : DriverFactory.getDriverList()) {
				Gene g = new Gene();
				g.driver = d;
				g.slot = getRndUnalloc(d);
				chromosome.add(g);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		intermediateBuffer = new String[chromosome.size()];
	}

	private TrainingSlot getRndUnalloc(Driver d) {
		//Bias towards early shift & unexpired

		int idx = rnd.nextInt(free.size());

		for (int tries =0; tries <=150; tries++) {
			TrainingSlot s = free.get(idx);
			int week = s.getWeek();
			if (d.finalYear()) {
				if (week <= d.getExpiresWeek())
					break;
			}else {

				int duty = d.getDuty(week);
				if (duty==2) {
					break;
				}
			}
			idx = rnd.nextInt(free.size());
		}
		return free.remove(idx);
	}

	public void mutate() {
		modified = true;
		//mutate
		if (rnd.nextBoolean()) {
			//Sawp
			Gene g1 = chromosome.get(rnd.nextInt(chromosome.size()));


			//Bias towards slot issues
			if (rnd.nextBoolean()) {
				int t=0;
				while ((g1.driver.getDuty(g1.slot.getWeek())==1) &&( t <11)){
					g1 = chromosome.get(rnd.nextInt(chromosome.size()));
					t++;
				}
			}

			Gene g2 = chromosome.get(rnd.nextInt(chromosome.size()));
			TrainingSlot tmp = g1.slot;
			g1.slot = g2.slot;
			g2.slot = tmp;
		}else {
			//Swap to unalloc
			Gene g1 = chromosome.get(rnd.nextInt(chromosome.size()));
			TrainingSlot tmp = free.remove(rnd.nextInt(free.size()));
			free.add(g1.slot);
			g1.slot = tmp;
		}
	}

	public Individual (Individual other) {
		initParticipants();
		//Copy constructor
		this.chromosome = new ArrayList<Gene>();
		for (Gene og : other.chromosome) {
			Gene ng = new Gene();
			ng.driver = og.driver;
			ng.slot = og.slot;
			this.chromosome.add(ng);
		}

		this.free = new ArrayList<TrainingSlot>();
		for (TrainingSlot s : other.free)
			this.free.add(s);

		modified = true;
		intermediateBuffer = new String[chromosome.size()];
	}


	public Individual(Individual p1, Individual p2) throws Exception {
		initParticipants();
		modified = true;
		//Crossover constructor
		free = new ArrayList<TrainingSlot>();
		free.addAll(SlotFactory.getSlotSet()) ;
		chromosome = new ArrayList<Gene>();

		for (int count =0; count <  p1.chromosome.size(); count ++) {
			Gene g  = new Gene();
			Gene p1g = p1.chromosome.get(count);
			Gene p2g = p2.chromosome.get(count);
			g.driver = p1g.driver;
			if (rnd.nextBoolean()) {
				//Add p1
				TrainingSlot s = p1g.slot;
				if (free.contains(s)) {
					g.slot = s;
					free.remove(s);
				}else {
					s = p2g.slot;
					if (free.contains(s)) {
						g.slot = s;
						free.remove(s);
					}else {
						s = free.remove(0);
						g.slot=s;
					}
				}
			}else{
				//Add p2
				TrainingSlot s = p2g.slot;
				if (free.contains(s)) {
					g.slot = s;
					free.remove(s);
				}else {
					s = p1g.slot;
					if (free.contains(s)) {
						g.slot = s;
						free.remove(s);
					}else {
						s = free.remove(0);
						g.slot=s;
					}
				}
			}
			chromosome.add(g);
		}
		intermediateBuffer = new String[chromosome.size()];
	}

	public void printSummary() {
		if (modified) {
			this.fitness();
		};
		//...
		System.out.println("CPC Plan");
		System.out.println("Issues :");
		System.out.println("CPC on lates shift: \t" +lates); 
		System.out.println("License expired before training: \t"+expired);
		System.out.println("Imbalanced group places: \t"+groupImbalanced);
		System.out.println("Low Constraint violations: \t"+lowConstraintViolations);
		System.out.println("Medium Constraint violations: \t"+mediumConstraintViolations);
		System.out.println("High Constraint violations: \t"+highConstraintViolations);

	}

	public String findIntermediate(String[] intermediate, Gene g) {
		String drvId = g.driver.getID();
		for (String s : intermediate) {
			if (s.contains(drvId))
				return s;
		}
		return null;
	}
	public void printSol() {
		//found = false;
		String[] intermediate = this.getIntermediate();
		
		System.out.println("Plan by week / day ");
		System.out.println("Week, Day, Driver ID, Group, Duty, Expiry Week, Final Year" );
		//Print by week
		for (int week =0; week < ProblemParameters.WEEKS; week++) {
			for (int day =0; day < 7; day ++) {
				for (Gene g : chromosome) {
					if ((g.slot.getWeek() == week) && (g.slot.getDay()==day)) {
						
						System.out.print(week + "," +day +"," + g.driver.getID() +"," +g.driver.group().name() + "," +g.driver.getDuty(week)+","+g.driver.getExpiresWeek() + ","+g.driver.finalYear() );
						if (this.checkExpiry(g))
								System.out.print("\tLICENSE EXPIRED!");

						if (this.checkLate(g))
							System.out.print("\tON LATE DUTY!");
						
						System.out.println();
						String inter = findIntermediate(intermediate,g);

						
						//Now check custom constraints
						this.checkCustomConstraints(g, inter);
						//player.getFullPlayerVariables(g);
						
						
					 }
				}
			}
		}

		
		// Initial list of constraints set
		for(CustomConstraint cp : customConstraints) {
			System.out.println("Custom Constraint: " + cp.getSource() );

		}
		profile.player = player;

		
	// drivers with no violations or restrictions
		for(String a : profile.player.getOpenToSwap()) {
			System.out.println( a);
		}
	
	// drivers and groups with met constraints. 
		for(String driver : profile.player.getMetConstraints()) {
			System.out.println(driver);
		}
	// violations for each driver and group
		for(String group : profile.player.getViolations()) {
			System.out.println(group);
		}
	// get full player variables after assessing the issue with GA output
		for(String s : profile.player.getFullPlayerVariables()) {
			System.out.println(s); 
		}


	}
	private String[] intermediateBuffer;

	public String[] getIntermediate() {
		//Print by week
		int c=0;
		for (Gene g : chromosome) {
			String w =""+ g.slot.getWeek();
			if (w.length()<2) w ="0"+w;

			String xw = ""+g.driver.getExpiresWeek();
			if (xw.length()<2) xw ="0"+xw;
			int fy=0;
			if (g.driver.finalYear()) fy=1;

			//intermediateBuffer[c] =  (":WK:"+w + ":DY:" +g.slot.getDay() +":ID:" + g.driver.getID() +":GR:" +g.driver.group().name() + ":DT:" +g.driver.getDuty(g.slot.getWeek())+":XW:"+xw + ":FY:"+fy );	
			intermediateBuffer[c] =  (
					":ID:" + g.driver.getID() +
					":GR:" +g.driver.group().name() + 
					":WK:"+w + 
					":DY:" +g.slot.getDay() +
					":DT:" +g.driver.getDuty(g.slot.getWeek())+
					":XW:"+xw + 
					":FY:"+fy );	

			c++;
		}
		//		String buffer = "";
		//		for (String s : intermediateBuffer) {
		//			//			buffer = buffer + s;
		//		}
		return intermediateBuffer;
	}



	private void verify() throws Exception {
		/* Verify that the chromsome contains a valid solution */
		ArrayList<TrainingSlot> slots = SlotFactory.getSlotSet();


		for (Gene g : chromosome) {
			if (g.driver == null)
				throw new Exception("Null driver in gene");

			if (g.slot == null)
				throw new Exception("Null slot in gene");


			if (slots.contains(g.slot)) {
				slots.remove(slots.indexOf(g.slot));
			}else {
				throw new Exception("Duplicated slot ");
			}
		}

		for (TrainingSlot s : free) {
			if (slots.contains(s)) {
				slots.remove(slots.indexOf(s));
			}else {
				throw new Exception("Duplicate unallocated slot");
			}
		}
		if (slots.size() !=0) {
			throw new Exception("Extra slots left");
		}
	}
	public String stats() {
		return  "fit," +fitness +",lates,"+lates +",expired,"+expired +",imbalance,"+groupImbalanced +",low," +lowConstraintViolations +",med," +mediumConstraintViolations+",high," +highConstraintViolations;
	}
}
