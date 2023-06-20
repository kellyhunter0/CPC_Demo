import java.util.ArrayList;
import java.util.Random;

public class Player {
	// Drivers who are happy with met constraints that can swap - this will be in the case of drivers who have constraints met but these are lower priority, and the swap could mean that a high priority constraint is met
	private ArrayList<String> metConstraints;
	// Drivers who have no constraints set and are open to swap
	private ArrayList<String> openToSwap;
	// drivers who have constraint violations
	private ArrayList<String> violations;
	private ArrayList<TrainingSlot> set;
	private ArrayList<String> fullPlayerVariables;
	private ArrayList<Player> playersList;
	private ArrayList<Gene> driversList;
	private ArrayList<TrainingSlot> free;
	private Gene g;
	private Strategy strategy;
	private CustomConstraint cp;
	// [id][group_name][strategy][week][day][priority]
	//[week][day][driverID][group][priority][strategy]
	private String[][][][][][]players;
	private String variables;
	private int payoff;
	private static Random rnd;
	private int highSwapCount;
	private int lowHighCount;
	private int mediumSwapCount;
	
	private static int[] targets=null;//Targets from each group
	
	public Player() {
	
		payoff = 0;
		strategy = Strategy.SWAP_FREE; // default
		//int id = Integer.parseInt(g.driver.getID());
		//players[g.slot.getWeek()][g.slot.getDay()][id][g.driver.group().ordinal()][cp.getPriority().ordinal()][this.getStrategy().ordinal()].toString();
		//players = new int[ProblemParameters.WEEKS][][][][][];
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
		if(violations == null)
			violations = new ArrayList<String>();
		if(openToSwap == null)
			openToSwap = new ArrayList<String>();
		if(metConstraints == null)
			metConstraints = new ArrayList<String>();
		if(fullPlayerVariables == null)
			fullPlayerVariables = new ArrayList<String>();
	}


	//[week][day][driverID][group][priority][strategy]
	// Function to initialise players when running BRD algorithm on its own
	public void initPlayers() {
		players = new String[ProblemParameters.WEEKS][][][][][];
		for (int x=0;x < players.length; x++ ) {
			players[x] = new String[7][][][][];
			for (int d=0; d < 7; d++) {
				players[x][d] = new String[Group.values().length][][][];
			}
		}
	}
	// Add players to this to fix their schedule
	public void initPlayersFixSchedule(ArrayList<String> violations, ArrayList<String> metConstraints, ArrayList<String> openToSwap) {
		
		players = new String[ProblemParameters.WEEKS][][][][][];
		for (int x=0;x < players.length; x++ ) {
			players[x] = new String[7][][][][];
			for (int d=0; d < 7; d++) {
				players[x][d] = new String[Group.values().length][][][];
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
		driversList = new ArrayList<Gene>();                 

		try {
			free.addAll(SlotFactory.getSlotSet() ) ;

			//Now create genes
			for (Driver d : DriverFactory.getDriverList()) {
			Gene g = new Gene();
			//Individual i = new Individual();
				g.driver = d;
			g.slot = getRndUnalloc(d);
			driversList.add(g);
		}

		} catch (Exception e) {
			e.printStackTrace();
		}
		//intermediateBuffer = new String[chromosome.size()];
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
	public ArrayList<Gene> addToDriverList(Gene player) {
		driversList.add(player);
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
	
	// Get slots from TrainingSlot.java
	public ArrayList<TrainingSlot> getFromArrayList(Gene g) {
		for(TrainingSlot ts : set){
			if (ts.toString().contains(g.slot.toString())) {
				System.out.println("Training slot "+ ts.toString());
				System.out.println("Gene driver slot "+ g.slot.toString());
			} else {
				System.out.println("Training slot not found "+ g.slot.toString());
			}
		}
		return set;
	}
	
	// Strategies for drivers/players
	public Strategy getStrategy() {
		return strategy;
	}
    public <T extends Enum<?>> T randomEnum(Class<T> c){
    	rnd = new Random();
        int x = rnd.nextInt(c.getEnumConstants().length);
      
        return c.getEnumConstants()[x];
    }
	public Strategy setStrategy( CustomConstraint cp, Gene g) {
		

			if(cp.getSource().contains("high")) {
				strategy = randomEnum(Strategy.class);
				Strategy strategy = randomEnum(Strategy.class);
               // System.out.println("Strategy: " + strategy);
                  if(strategy.equals(Strategy.SWAP_HIGH) ) {
                          while(strategy.equals(Strategy.SWAP_HIGH)) {
                            strategy = randomEnum(Strategy.class);
                            //System.out.println("new strategy: " +strategy);
                            }
         
         
                  }
                  else if (strategy.equals(Strategy.SWAP_MEDIUM)) {
	                  while(strategy.equals(Strategy.SWAP_MEDIUM)) {
	                	  strategy = randomEnum(Strategy.class);
	                	  //System.out.println("wrong strategy: " +strategy);
	                  }
                  }
                  
                
                if(!strategy.equals(Strategy.SWAP_MEDIUM) || !strategy.equals(Strategy.SWAP_HIGH) ){
                  while(strategy.equals(Strategy.SWAP_MEDIUM)|| strategy.equals(Strategy.SWAP_HIGH)) {

                          //System.out.println("High strategy: " +strategy);
                          
                      
                    if(!strategy.equals(Strategy.SWAP_FREE) || !strategy.equals(Strategy.SWAP_LOW) ){
                    	strategy = randomEnum(Strategy.class);
                    }
                    
                  }
                 
                }
				//player.set
                return strategy;
			}
			else if(cp.getSource().contains("medium")) {
				strategy = randomEnum(Strategy.class);
				Strategy strategy = randomEnum(Strategy.class);
                //System.out.println("Strategy: " + strategy);
                  if(strategy.equals(Strategy.SWAP_LOW) ) {
                          while(strategy.equals(Strategy.SWAP_LOW)) {
                            strategy = randomEnum(Strategy.class);
                            //System.out.println("new strategy: " +strategy);
                            }
         
         
                  }
                  else if (strategy.equals(Strategy.SWAP_MEDIUM)) {
	                  while(strategy.equals(Strategy.SWAP_MEDIUM)) {
	                	  strategy = randomEnum(Strategy.class);
	                	  //System.out.println("wrong strategy: " +strategy);
	                  }
                  }
                  
                
                if(!strategy.equals(Strategy.SWAP_MEDIUM) || !strategy.equals(Strategy.SWAP_LOW) ){
                  while(strategy.equals(Strategy.SWAP_MEDIUM)|| strategy.equals(Strategy.SWAP_LOW)) {

                          //System.out.println("Medium strategy: " +strategy);
                          
                      
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
				//player.set
			}
			

			return strategy;
		}
		
	public Strategy setStrategyNoConstraints(Gene g) {
		strategy = randomEnum(Strategy.class);
		Strategy strategy = randomEnum(Strategy.class);
        //System.out.println("Strategy: " + strategy);
          if(strategy.equals(Strategy.SWAP_LOW) ) {
                  while(strategy.equals(Strategy.SWAP_LOW)) {
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
        
		//player.set
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
	public String[][][][][][] getPlayers() {
		return players;
	}
	
	public String stats() {
		return  "utility," +payoff +",highSwapCount,"+highSwapCount +",lowHighCount,"+lowHighCount +",mediumSwapCount,"+mediumSwapCount ;
	}

}
