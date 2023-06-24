import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Player {
	// Drivers who are happy with met constraints that can swap - this will be in the case of drivers who have constraints met but these are lower priority, and the swap could mean that a high priority constraint is met
	private ArrayList<String> metConstraints;
	// Drivers who have no constraints set and are open to swap
	private ArrayList<String> openToSwap;
	// drivers who have constraint violations
	private ArrayList<String> violations; // repair function
	private ArrayList<String> fullPlayerVariables; // repair function
	private ArrayList<Player> playersList;
	private ArrayList<Driver> driversList;
	private ArrayList<TrainingSlot> free;
	private static ArrayList<CustomConstraint> customConstraints;
	private ArrayList<Profile> playerProfile; // repair function
	private Strategy strategy;
	private CustomConstraint cp;
	private int[][][]players = new int[ProblemParameters.WEEKS][][];
	private int payoff;
	private static Random rnd;
	private boolean modified = true;
	private int expired=0;
	private int lates=0;
	private int groupImbalanced=0;
	private int lowConstraintViolations=0;
	private int mediumConstraintViolations=0;
	private int highConstraintViolations=0;
	private int highSwapCount = 0;
	private int lowHighCount = 0;
	private int mediumSwapCount = 0;
	private	int count = 0;
	private static boolean ga;
	
	private static int[] targets=null;//Targets from each group
	
	public Player()  {
	
		payoff = 0;
		strategy = Strategy.SWAP_FREE; // default		
		driversList = new ArrayList<Driver>();
		
		if(ga == false) {
			init();
			if (targets==null) {
				int tDrivers = DriverFactory.getDriverList().size();
				targets = new int[Group.values().length];
				for(Group g :Group.values()) {
					int count=0;
					for (Driver d : DriverFactory.getDriverList()) {
						driversList.add(d);
						if (d.group()==g)					
							count++;
					}
					float t =  (count/(float)tDrivers)*ProblemParameters.TRAINING_PLACES;
					targets[g.ordinal()] = Math.round(t);
					targets[g.ordinal()]++;
									System.out.println("Target " + g.name() + " "+targets[g.ordinal()]);
									//System.out.println("Driver " + d + " "+targets[g.ordinal()]);
				}

			}
		}

		if(violations == null)
			violations = new ArrayList<String>();
		if(openToSwap == null)
			openToSwap = new ArrayList<String>();
		if(metConstraints == null)
			metConstraints = new ArrayList<String>();
		if(fullPlayerVariables == null)
			fullPlayerVariables = new ArrayList<String>();
		if(playerProfile == null) {
			playerProfile = new ArrayList<Profile>();
			playerProfile = createPlayerProfile();
		}
		if(free==null && ga == false) {
			try {
				free= new ArrayList<TrainingSlot>();
				free = SlotFactory.getSlotSet();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(playerProfile == null) {
				playerProfile = new ArrayList<Profile>();
				Profile p = new Profile();
				free = new ArrayList<TrainingSlot>();
				try {
					free = SlotFactory.getSlotSet();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for(Driver d : DriverFactory.getDriverList()) {
					try {
						for(TrainingSlot s : free) {
							p.driver = d;
							p.slot = s;
							p.player = this;
							playerProfile.add(p);
							
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
				
			}
		}

		if(playersList == null) {
			playersList = new ArrayList<Player>();
		}

	}

	public static boolean getGA() {
		return Player.ga;
	}
	public static boolean setGA(boolean ga) {
		Player.ga = ga;
		return ga;
	}
	//[week][day][driverID][group][priority][strategy]
	// Function to initialise players when running BRD algorithm on its own
	public void initPlayers() {					 //id group strategy cc  				
		players = new int[ProblemParameters.WEEKS][][];
		for (int x=0;x < players.length; x++ ) {
			players[x] = new int[7][];
			for (int d=0; d < 7; d++) {
				players[x][d] = new int[Group.values().length];

		}
	}
}

	public ArrayList<Profile> createPlayerProfile(){
		if(driversList == null)
			driversList = DriverFactory.getDriverList();
		if(playerProfile == null) {
			playerProfile = new ArrayList<Profile>();
			Profile p = new Profile();
			free = new ArrayList<TrainingSlot>();
			try {
				free = SlotFactory.getSlotSet();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			for(Driver d : DriverFactory.getDriverList()) {
				try {
					for(TrainingSlot s : free) {
						p.driver = d;
						p.slot = s;
						p.player = this;
						playerProfile.add(p);
						
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			
		}
		
		return playerProfile;
		
	}
	//allocate maybe?
	public void mutate() {
		
	/*
	 * 		modified = true;
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
	 * 
	 * 
	 */
		rnd = new Random();
		String playerVariables = "";
		fullPlayerVariables = fullPlayerVars();
		for(String s : fullPlayerVariables) {
			System.out.println("test" + s);
		}
		//Collections.sort(fullPlayerVariables);
		//System.out.println("Sorted:");
		//for(String s : fullPlayerVariables) {
		//	System.out.println(s);
		//}
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		//ArrayList<Integer> day = new ArrayList<Integer>();
		for(Profile p : playerProfile) {
			//System.out.println(p.slot.getWeek());
			tmp.add(p.slot.getWeek());
			//day.add(p.slot.getDay());
		}
		Collections.sort(tmp);
		//Collections.sort(day);
		for (int week =0; week < ProblemParameters.WEEKS; week++) {
			for (int day =0; day < 7; day ++) {
				for (Profile g : playerProfile) {
					if ((g.slot.getWeek() == week) && (g.slot.getDay()==day)) {
						if(g.player.cp.getSource().contains(g.driver.getID()) || g.player.cp.getSource().contains(g.driver.group().name())) {
						System.out.print(week + "," +day +"," + g.driver.getID() +"," +g.driver.group().name() + "," +g.driver.getDuty(week)+","+g.driver.getExpiresWeek() + ","+g.driver.finalYear()+","+g.player.cp.getSource());
						if (this.checkExpiry(g))
								System.out.print("\tLICENSE EXPIRED!");

						if (this.checkLate(g))
							System.out.print("\tON LATE DUTY!");
						
						System.out.println();
						//String inter = findIntermediate(intermediate,g);

						
						//Now check custom constraints
						//this.checkCustomConstraints(g, inter);
						//player.getFullPlayerVariables(g);
						
						}
						else {
							System.out.print(week + "," +day +"," + g.driver.getID() +"," +g.driver.group().name() + "," +g.driver.getDuty(week)+","+g.driver.getExpiresWeek() + ","+g.driver.finalYear());
							if (this.checkExpiry(g))
									System.out.print("\tLICENSE EXPIRED!");

							if (this.checkLate(g))
								System.out.print("\tON LATE DUTY!");
							
							System.out.println();
							//String inter = findIntermediate(intermediate,g);

							
							//Now check custom constraints
							//this.checkCustomConstraints(g, inter);
							//player.getFullPlayerVariables(g);
							
						}
					 }
				}
			}
		}

		
	}
		
		
//		fullPlayerVariables = new ArrayList<String>();
//		fullPlayerVariables = fullPlayerVars();
//		
//		for(String s : fullPlayerVariables) {
//			System.out.println("Player" + s);
//		}
		


	public ArrayList<String> fullPlayerVars(){
		String playerVariables = "";
		//playerProfile = new ArrayList<Profile>();
		//playerProfile = createPlayerProfile();
		
		fullPlayerVariables = new ArrayList<String>();
		for(Profile p1 : playerProfile) {
			for(CustomConstraint cp : customConstraints) {
				p1.player.cp = cp;
				

					if(cp.getSource().contains("high") && (cp.getSource().contains(p1.driver.getID()) || cp.getSource().contains(p1.driver.group().name()))) {
						count = 0;
						count = count + 1;
						strategy = setStrategy(cp);
						playerVariables = p1.slot.getWeek() + "," + p1.slot.getDay() + "," + p1.driver.getID()+","+p1.driver.group()+"," +cp.getSource()+","+strategy;
						fullPlayerVariables.add(playerVariables);
//						System.out.println("Player" + p1.driver.getID() +" "+ p1.driver.group().name()+ ": " +p1.slot.getWeek() + " " + p1.slot.getDay() + " " + p1.driver.getExpiresWeek() + " "+ p1.driver.getDuty(p1.driver.getExpiresWeek()) + " "+strategy + " " + cp.getSource());
						//System.out.println("Player " + playerVariables );
						highConstraintViolations++;
						if(count >= 1)
							break;
					}
					else if(cp.getSource().contains("medium") && (cp.getSource().contains(p1.driver.getID()) || cp.getSource().contains(p1.driver.group().name()))) {
						count = 0;
						count = count + 1;
						strategy = setStrategy(cp);
						playerVariables = p1.slot.getWeek() + "," + p1.slot.getDay() + "," + p1.driver.getID()+","+p1.driver.group()+"," +cp.getSource()+","+strategy;
						fullPlayerVariables.add(playerVariables);
//						System.out.println("Player" + p1.driver.getID() +" "+ p1.driver.group().name()+ ": " +p1.slot.getWeek() + " " + p1.slot.getDay() + " " + p1.driver.getExpiresWeek() + " "+ p1.driver.getDuty(p1.driver.getExpiresWeek()) + " "+strategy + " " + cp.getSource());
						//System.out.println("Player " + playerVariables);
						mediumConstraintViolations++;
						if(count >= 1)
							break;
					}
					else if(cp.getSource().contains("low") && (cp.getSource().contains(p1.driver.getID()) || p1.player.cp.getSource().contains(p1.driver.group().name()))){
						count = 0;
						count = count + 1;
						strategy = setStrategy(cp);
						playerVariables = p1.slot.getWeek() + "," + p1.slot.getDay() + "," + p1.driver.getID()+","+p1.driver.group()+"," +cp.getSource()+","+strategy;
						fullPlayerVariables.add(playerVariables);
						//System.out.println("Player " + playerVariables);
						lowConstraintViolations++;
						//System.out.println("Player" + p1.driver.getID() +" "+ p1.driver.group().name()+ ": " +p1.slot.getWeek() + " " + p1.slot.getDay() + " " + p1.driver.getExpiresWeek() + " "+ p1.driver.getDuty(p1.driver.getExpiresWeek()) + " "+strategy + " " + cp.getSource());
						if(count >= 1)
							break;
					}
					else if (!cp.getSource().contains(p1.driver.getID()) || !cp.getSource().contains(p1.driver.group().name())) {
						count = 0;
						count = count + 1;
						strategy = setStrategyNoConstraints();
						playerVariables = p1.slot.getWeek() + "," + p1.slot.getDay() + "," + p1.driver.getID()+","+p1.driver.group()+","+strategy;
						fullPlayerVariables.add(playerVariables);
						//System.out.println("Player " + playerVariables);
						//System.out.println("Player" + p1.driver.getID() +" "+ p1.driver.group().name()+ ": " +p1.slot.getWeek() + " " + p1.slot.getDay() + " " + p1.driver.getExpiresWeek() + " "+ p1.driver.getDuty(p1.driver.getExpiresWeek()) + " "+strategy );
						if(count >= 1)
							break;
					}

		  }
	   }

		return fullPlayerVariables;
	}
	// Add players to this to fix their schedule
	public void initPlayersFixSchedule(ArrayList<String> violations, ArrayList<String> metConstraints, ArrayList<String> openToSwap) {
		
		players = new int[ProblemParameters.WEEKS][][];
		for (int x=0;x < players.length; x++ ) {
			players[x] = new int[7][];
			for (int d=0; d < 7; d++) {
				players[x][d] = new int[Group.values().length];
			}
		}
	}
//	public void initParticipants() {

//		}
//
//	}
	public void init() {

		initPlayers();
		//Random init
		free = new ArrayList<TrainingSlot>();
		driversList = new ArrayList<Driver>();  
		playerProfile = new ArrayList<Profile>();

		try {
			free.addAll(SlotFactory.getSlotSet() ) ;

			//Now create genes
		for (Driver d : DriverFactory.getDriverList()) {
			//Gene g = new Gene();
			Profile p = new Profile();
			p.driver = d;
			p.slot = getRndUnalloc(d);
			p.player = this;
			playerProfile.add(p);


		}

		} catch (Exception e) {
			e.printStackTrace();
		}
		//intermediateBuffer = new String[chromosome.size()];
	}
	
	public Player (Player other) {
		initPlayers();
		//Copy constructor
		playerProfile = new ArrayList<Profile>();
		fullPlayerVariables = new ArrayList<String>();
		for (Profile og : other.playerProfile) {
			Profile ng = new Profile();
			ng.driver = og.driver;
			ng.slot = og.slot;
			ng.player = og.player;
			playerProfile.add(ng); 
			//fullPlayerVariables.add(ng.slot.getWeek() + "," + ng.slot.getDay() + "," + ng.driver.getID() + "," + ng.driver.group() + "," + cp.getSource() + "," +strategy);
		}

		//intermediateBuffer = new String[chromosome.size()];
	}

// this is the area I need to think about coding the game between two players, checking constraints, weeks and strategies before initiating slot allocation
	public Player(Player p1, Player p2) throws Exception {
		initPlayers();
		modified = true;
		//Crossover constructor
		free = new ArrayList<TrainingSlot>();
		free.addAll(SlotFactory.getSlotSet()) ;
		playerProfile = new ArrayList<Profile>();
		//createPlayers();
		// This works in creating player profiles, so refactor this to reflect the above comment
		for (int count =0; count <  p1.playerProfile.size(); count ++) {
			Profile p  = new Profile();
			Profile p1g =  new Profile();
			Profile p2g = new Profile();
			p1g = p1.playerProfile.get(count);
			p2g =  p2.playerProfile.get(count);
			p.driver = p1g.driver;
			p.player = p1g.player;
			// check for something different here - might be worth comparing strategies with players, and/or check priority
			if (rnd.nextBoolean()) {
				//Add p1
				TrainingSlot s = p1g.slot;
				if (free.contains(s)) {
					p.slot = s;
					free.remove(s);
				}else {
					s = p2g.slot;
					if (free.contains(s)) {
						p.slot = s;
						free.remove(s);
					}else {
						s = free.remove(0);
						p.slot=s;
					}
				}
			}else{
				//Add p2
				TrainingSlot s = p2g.slot;
				if (free.contains(s)) {
					p.slot = s;
					free.remove(s);
				}else {
					s = p1g.slot;
					if (free.contains(s)) {
						p.slot = s;
						free.remove(s);
					}else {
						s = free.remove(0);
						p.slot=s;
					}
				}
			}
			playerProfile.add(p);
		}
		//intermediateBuffer = new String[chromosome.size()];
	}

	
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
	
	private TrainingSlot getRndUnalloc(Driver d) {
		//Bias towards early shift & unexpired
		rnd = new Random();
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
	public ArrayList<String> getFullPlayerVariables() {
		return fullPlayerVariables;
	}
	
	public ArrayList<String> setFullPlayerVariables(ArrayList<String> fullPlayerVariables) {
		this.fullPlayerVariables = fullPlayerVariables;
		return fullPlayerVariables;
	}
	public ArrayList<String> addToPlayerVariables(String variables) {
		fullPlayerVariables.add(variables);
		return fullPlayerVariables;
	}
	public ArrayList<Driver> addToDriverList(Driver driver) {
		driversList.add(driver);
		return driversList;
	}
	public ArrayList<Player> addToPlayerList(Player player) {
		playersList.add(player);
		return playersList;
	}
	// These set of methods show the getters and setters for group and driver violations. These are stored in ArrayLists, which store the driver number, group name, and the week they specified along with the priority
	// T
	public ArrayList<String> getViolations() {
		return violations;
	}    
	public ArrayList<String> setViolations(ArrayList<String> violations) {
		this.violations = violations;
		return violations;
	}
	
	public ArrayList<String> addViolations(String violation) {
		violations.add(violation);
		return violations;
	}
// Happy people to  swap with
	// people who have met constraints
	public ArrayList<String> getMetConstraints() {
		return metConstraints;
	}    
	public ArrayList<String> setMetConstraints(ArrayList<String> metConstraints) {
		this.metConstraints = metConstraints;
		return metConstraints;
	}    
	public ArrayList<String> addMetConstraints(String constraint) {
		metConstraints.add(constraint);
		return metConstraints;
	}
	// people who have no constraints and are happy to swap
	public ArrayList<String> getOpenToSwap() {
		return openToSwap;
	}    
	public ArrayList<String> setOpenToSwap(ArrayList<String> openToSwap) {
		this.openToSwap = openToSwap;
		return openToSwap;
	}
	
	public ArrayList<String> addOpenToSwap(String swap) {
		openToSwap.add(swap);
		return openToSwap;
	}
	

	
	// Strategies for drivers/players
	public Strategy getStrategy() {
		return strategy;
	}
	// Generates a random Enum value based on the Enum.class notation, 
	// in this case it is used for Strategy, which can be seen in the setStrategy method below
    public <T extends Enum<?>> T randomEnum(Class<T> c){
    	rnd = new Random();
        int x = rnd.nextInt(c.getEnumConstants().length);
      
        return c.getEnumConstants()[x];
    }
    // Sets a random strategy for players based on their priority, there are sometimes more than one strategy available 
    // to a player so it is important to account for that
	public Strategy setStrategy( CustomConstraint cp) {
			if(cp.getSource().contains("high")) {
				strategy = randomEnum(Strategy.class);
				Strategy strategy = randomEnum(Strategy.class);
                  if(strategy.equals(Strategy.SWAP_HIGH) ) {
                          while(strategy.equals(Strategy.SWAP_HIGH)) {
                            strategy = randomEnum(Strategy.class);
                            }
                  }
                  else if (strategy.equals(Strategy.SWAP_MEDIUM)) {
	                  while(strategy.equals(Strategy.SWAP_MEDIUM)) {
	                	  strategy = randomEnum(Strategy.class);
	                  }
                  }
                  
                
                if(!strategy.equals(Strategy.SWAP_MEDIUM) || !strategy.equals(Strategy.SWAP_HIGH) ){
                  while(strategy.equals(Strategy.SWAP_MEDIUM)|| strategy.equals(Strategy.SWAP_HIGH)) {
                    if(!strategy.equals(Strategy.SWAP_FREE) || !strategy.equals(Strategy.SWAP_LOW) ){
                    	strategy = randomEnum(Strategy.class);
                    }
                    
                  }
                 
                }
                return strategy;
			}
			else if(cp.getSource().contains("medium")) {
				strategy = randomEnum(Strategy.class);
				Strategy strategy = randomEnum(Strategy.class);
                  if(strategy.equals(Strategy.SWAP_LOW) ) {
                          while(strategy.equals(Strategy.SWAP_LOW)) {
                            strategy = randomEnum(Strategy.class);
                            }
         
         
                  }
                  else if (strategy.equals(Strategy.SWAP_MEDIUM)) {
	                  while(strategy.equals(Strategy.SWAP_MEDIUM)) {
	                	  strategy = randomEnum(Strategy.class);
	                  }
                  }
                  
                
                if(!strategy.equals(Strategy.SWAP_MEDIUM) || !strategy.equals(Strategy.SWAP_LOW) ){
                  while(strategy.equals(Strategy.SWAP_MEDIUM)|| strategy.equals(Strategy.SWAP_LOW)) {
                    if(!strategy.equals(Strategy.SWAP_FREE) || !strategy.equals(Strategy.SWAP_HIGH) ){
                    	strategy = randomEnum(Strategy.class);
                    }
                  }
                 
                }
                return strategy;
				//player.set
			}
			else if(cp.getSource().contains("low")) {
				strategy = randomEnum(Strategy.class);
				Strategy strategy = randomEnum(Strategy.class);
               // System.out.println("Strategy: " + strategy);
                  if(strategy.equals(Strategy.SWAP_LOW) ) {
                          while (strategy.equals(Strategy.SWAP_LOW)) {
                            strategy = randomEnum(Strategy.class);
                            //System.out.println("new strategy: " +strategy);
                          }
         
                  }
                  else if (strategy.equals(Strategy.SWAP_FREE)) {
	                  while(strategy.equals(Strategy.SWAP_FREE)) {
	                	  strategy = randomEnum(Strategy.class);
	                	  //System.out.println("wrong strategy: " +strategy);
	                  }
                  }

                if(!strategy.equals(Strategy.SWAP_LOW) || !strategy.equals(Strategy.SWAP_FREE) ){
                  while(strategy.equals(Strategy.SWAP_LOW)|| strategy.equals(Strategy.SWAP_FREE)) { 
                	  
                	  
                    if(!strategy.equals(Strategy.SWAP_HIGH) || !strategy.equals(Strategy.SWAP_MEDIUM) ){
                    	strategy = randomEnum(Strategy.class);
                    }
                    
                  }
                }
                return strategy;
			}
			return strategy;
		}
		
	public Strategy setStrategyNoConstraints() {
		strategy = randomEnum(Strategy.class);
		Strategy strategy = randomEnum(Strategy.class);
        //System.out.println("Strategy: " + strategy);
          if(strategy.equals(Strategy.SWAP_LOW) ) {
                  while(strategy.equals(Strategy.SWAP_LOW)) {
                    strategy = randomEnum(Strategy.class);
                    }
 
 
          }
          else if (strategy.equals(Strategy.SWAP_FREE)) {
              while(strategy.equals(Strategy.SWAP_FREE)) {
            	  strategy = randomEnum(Strategy.class);
              }
          }
          
        
        if(!strategy.equals(Strategy.SWAP_LOW) || !strategy.equals(Strategy.SWAP_FREE) ){
          while(strategy.equals(Strategy.SWAP_LOW)|| strategy.equals(Strategy.SWAP_FREE)) {
            if(!strategy.equals(Strategy.SWAP_HIGH) || !strategy.equals(Strategy.SWAP_MEDIUM) ){
            	strategy = randomEnum(Strategy.class);
            }

          }
       }
        return strategy;
	}

	// Payoff function
	public int getUtility() {
		return payoff;
	}
	public int setUtility(int payoff) {
		this.payoff = payoff;
		return payoff;
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
			payoff =0;
			lates=0;
			expired=0;
			groupImbalanced=0;
			lowConstraintViolations=0;
			mediumConstraintViolations=0;
			highConstraintViolations=0;
			//Count participants from each group.
			for (int x=0;x < players.length; x++ ) {
				for (int d=0; d < 7; d++) {
					for (int g =0; g < Group.values().length; g++ )

								players[x][d][g]= 0;
							
						}
				}
			}


			for (Profile g : playerProfile) {
				
				if (checkLate(g)) {
					payoff = payoff + ProblemParameters.PENALTY_LATE_SHIFT;
					lates++;
				}
				//Check for training after expiry
				if (checkExpiry(g)) {
					payoff = payoff + ProblemParameters.PENALTY_EXPIRED_LICENSE;
					expired++;
				}else {
				//Encourage training as far before expiry as possible
					if (g.driver.finalYear())
						payoff = payoff + (g.slot.getWeek()/g.driver.getExpiresWeek())*ProblemParameters.PENALTY_ADVANCE_FINAL_WEEK;
				}
				String s = g.driver.getID().trim();
				int id = Integer.parseInt(s);
				//Log participation
				players[g.slot.getWeek()][g.slot.getDay()][g.driver.group().ordinal()]++;

			}
			//Check for imbalance in participants
			checkImbalance();
			//checkCustomConstraints();
			return payoff;
		}
		
	

	private void checkImbalance() { // O(p * d * g), where g is the number of groups, and d is the number of drivers
		for (int[][]  p: players) {
			for (int d=0; d < 7; d++) {
				for (Group g : Group.values()) {

			
							if (p[d][g.ordinal()]>targets[g.ordinal()]) {
								int over = p[d][g.ordinal()]-targets[g.ordinal()];
								groupImbalanced=groupImbalanced+over;
								payoff = payoff +(over *ProblemParameters.PENALTY_GROUP_IMBALANCE);
							}
						
					
				}
			}
		}
	}

	private boolean checkLate(Profile p) {
		if (p.driver.getDuty(p.slot.getWeek())==1) {
			return true;
		}
		return false;
	}

	public boolean checkExpiry(Profile p) {
		int week = p.slot.getWeek();
		if (p.driver.finalYear()) {
			if(week >  p.driver.getExpiresWeek()) {
				return true;
			}
		}
		return false;

	}
	public int calculateUtility() {
		// p1 gets higher payoff
		if(strategy.name() == "SWAP_HIGH" && strategy.name() == "SWAP_FREE") {
			highSwapCount++;
			payoff = ProblemParameters.PENALTY_HIGH_CONSTRAINT_VIOLATION; // payoff is 10
			return payoff;
			
		}
		else if(strategy.name() == "SWAP_HIGH" && strategy.name() == "SWAP_LOW") {
			lowHighCount++;
			payoff = ProblemParameters.PENALTY_HIGH_CONSTRAINT_VIOLATION - ProblemParameters.PENALTY_LOW_CONSTRAINT_VIOLATION; // payoff is 10-1 = 9
			return payoff;
		}
		else if(strategy.name() == "SWAP_MEDIUM" && strategy.name() == "SWAP_FREE") {
			mediumSwapCount++;
			payoff = ProblemParameters.PENALTY_MEDIUM_CONSTRAINT_VIOLATION; // payoff is 5
			return payoff;
			
		}

		return payoff;
	
	}
	public int[][][] getPlayers() {
		return players;
	}
	
	public String stats() {
		return  "utility," +payoff +", imbalance, " + groupImbalanced + "high violations,"+highConstraintViolations +",medium violations,"+mediumConstraintViolations +",low violations,"+lowConstraintViolations ;
	}

}
