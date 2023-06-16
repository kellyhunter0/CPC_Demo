import java.util.ArrayList;
import java.util.Random;

public class Player {
	// Drivers who are happy with met constraints that can swap - this will be in the case of drivers who have constraints met but these are lower priority, and the swap could mean that a high priority constraint is met
	private ArrayList<String> metConstraints;
	// Drivers who have no constraints set and are open to swap
	private ArrayList<String> openToSwap;
	// drivers who have constraint violations
	private ArrayList<String> violations;
	private Strategy strategy;
	private CustomConstraint cp;
	private Gene g;
	// [id][group_name][strategy][week][day][priority]
	//[week][day][driverID][group][priority][strategy]
	private String[][][][][][]players;
	private int payoff;
	private static Random rnd;
	
	
	public Player() {
		violations = new ArrayList<String>();
		metConstraints = new ArrayList<String>();
		openToSwap = new ArrayList<String>();
		payoff = 0;
		//int id = Integer.parseInt(g.driver.getID());
		//players[g.slot.getWeek()][g.slot.getDay()][id][g.driver.group().ordinal()][cp.getPriority().ordinal()][this.getStrategy().ordinal()].toString();
		players = new String[ProblemParameters.WEEKS][][][][][];
	}
	
	// decision variables
	
	public String getPlayerVariables() {
		int id = Integer.parseInt(g.driver.getID());
		return players[g.slot.getWeek()][g.slot.getDay()][id][g.driver.group().ordinal()][cp.getPriority().ordinal()][this.getStrategy().ordinal()].toString();
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
	public Strategy setStrategy(Strategy strategy) {
		this.strategy = strategy;
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
			payoff = ProblemParameters.PENALTY_HIGH_CONSTRAINT_VIOLATION; // payoff is 10
			return payoff;
			
		}
		else if(strategy.name() == "SWAP_HIGH" && strategy.name() == "SWAP_LOW") {
			payoff = ProblemParameters.PENALTY_HIGH_CONSTRAINT_VIOLATION - ProblemParameters.PENALTY_LOW_CONSTRAINT_VIOLATION; // payoff is 10-1 = 9
			return payoff;
		}
		else if(strategy.name() == "SWAP_MEDIUM" && strategy.name() == "SWAP_FREE") {
			payoff = ProblemParameters.PENALTY_MEDIUM_CONSTRAINT_VIOLATION; // payoff is 5
			return payoff;
			
		}

		return payoff;
	
	}
	private void improveSol(Player[] pop, ArrayList<TrainingSlot> solution)  {
		//DriverFactory df = new DriverFactory();
		int p1 = rnd.nextInt(pop.length);
		int p2 = rnd.nextInt(pop.length);
		//df.getDriverList(); 
		
		// need to compare custom constraint priority for each individual, and if one ranks higher than the other, then the slot is allocated to the higher priority case
		for(String unhappy : getViolations() ) {
			for(String free : getOpenToSwap()) {
				String unhappyPerson[] = unhappy.split(",");
				String weekPreference = unhappyPerson[2];
				if(free.contains(weekPreference) && free.contains("must be after")) {
					// potential swap after one week!
				} else if (free.contains(weekPreference) && free.contains("must be before")) {
					
				} else if (free.contains(weekPreference) && free.contains("must be in")) {
					
				}
				else if (free.contains(weekPreference) && free.contains("must not be in")) {
					
				}
				
				//String week = Integer.toString(w);
				//int game = game(pop);
				
				if(pop[p1].calculateUtility() > pop[p2].calculateUtility() ) {
					//if(!solution.contains(t) && pop[p1].getIntermediate().toString().contains(week)) {
						
						//allocated
					}
					else {
						
					}
				}
			}
			
		}
	
	
	private static int game(Player[] pop) {
		int p1 = rnd.nextInt(pop.length);
		int p2 = rnd.nextInt(pop.length);
		if(pop[p1].calculateUtility() < pop[p2].calculateUtility()) 
			return p1;
		else
			return p2;
				
	}
}
