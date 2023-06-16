import java.util.ArrayList;
import java.util.Random;
//
//Create initial solution
//
//:restart
//For each unhappy person p
//	For each unallocated slot s
//		If alloc(p,s) improves
//			Alloc(ps,s)
//			:restart
//
//For each unhappy person p
//	s = slot(p)
//	s’  = findbestSlot() //k best slots??
//	p’ = owner(s’)
//	if (alloc(p’,s)  improves
//		alloc(p’s)
//		alloc(p,s’)
//	else
//		For each unallocated slot us
//			If alloc(p’,us) improves
//				alloc(p’,us)
//				alloc(p,s’)
//				:restart
//
//
//Improves == improvement in global fitness.
/*__________________________
 * 
 * Some notes
 *  originally from: https://cs.idc.ac.il/~tami/Papers/sagt17.pdf 
 * _________________________
 * A game G has a set D of n drivers. Each driver, i, ha a strategy space Pi, and the driver chooses a strategy p sub i is an element of the set P
 * 
 *  Strategy profiles are a vector of strategies for each player, 
 *  	p=(p sub i ,..., p sub n) 
 *  The strategy profile for each player i is denoted by p sub -i.
 *  Therefore, it is useful to denote a strategy profile as p = (p sub i, p sub -i)
 *  
 *  For a set of drivers I, p sub I and p sub -I, 
 *  the strategy profile of drivers in I and in D/I will result in (p sub I, p sub -I)
 *  
 *  Each driver will have a cost function:
 *   - 	cost function { c sub i : P -> R sup >= 0 } 
 *  	where 
 *  		c sub i(p) denotes drivers i's cost in the strategy profile p. 
 *  
 *  Each driver wishes to minimise their cost, and in the context of this problem specifically:
 *  	- Drivers wish to minimise the number of high priority 
 *  	  constraints that are broken in the solution set provided by the GA. The goal is to have a swap strategy based on priority, high constraint violations should be reduced, and any other parts should be fixed if possible
 *  The associated cost of broken constraints based on their priority rating can be found in the ProblemParameters.java file
 */
import java.util.regex.Matcher;


public class BRD {
	private static Random rnd = new Random();
	// Drivers who are happy with met constraints that can swap - this will be in the case of drivers who have constraints met but these are lower priority, and the swap could mean that a high priority constraint is met
	private ArrayList<String> metConstraints;
	// Drivers who have no constraints set and are open to swap
	private ArrayList<String> openToSwap;
	// drivers who have constraint violations
	private ArrayList<String> violations;
	private boolean isFound;

	public BRD() {
		violations = new ArrayList<String>();
		metConstraints = new ArrayList<String>();
		openToSwap = new ArrayList<String>();
		isFound = false;
	}
	
	
	public BRD(ArrayList<String> metConstraints, ArrayList<String> openToSwap, ArrayList<String> violations) {
		this.violations = violations;
		this.openToSwap = openToSwap;
		this.metConstraints = metConstraints;
		isFound = false;
		
		if(violations.size() == 0) 
			violations = new ArrayList<String>();
		
		if(openToSwap.size() == 0) 
			openToSwap = new ArrayList<String>();
		
		if(metConstraints.size() == 0) 
			openToSwap = new ArrayList<String>();
		
	}
	/*
	 * This function will run Best Response Dynamics, also known as Better Response Dynamics, as a means of comparison to the GA
	 */
	private static void runBRD(boolean verbose, int runs) throws Exception {
		for(int run = 0; run < runs; run++) {
			ProblemParameters.EVALS = 0;
			Player[] population = new Player[ProblemParameters.POP_SIZE];
			int bestFit = Integer.MAX_VALUE;
			Individual best =null;
			
			
		}
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
	
	

	
	// Set of methods 
	
	/*
	 * This function hopes to improve the output generated from the GA, and will fix solutions with broken constraints based on priority
	 */

	
	private boolean freeSlot(ArrayList<TrainingSlot> solution, TrainingSlot t) {
	
		if(solution.contains(t))
			return false;
		else
			return true; // call allocate() here
	
	}
	
	private static void allocate(ArrayList<TrainingSlot> solution, TrainingSlot t, Individual[] pop) {
		// allocate slot to a person in the population
		// check priority and prioritise high constraints that have been broken, 
		//as well as fix other schedules that are 'broken'
		// Could use the gale shapely algorithm to allocate: https://www.sanfoundry.com/java-program-gale-shapley-algorithm/
	}
	
	/*
	 * Checks priority level of custom constraints and 
	 */
	private static void checkPriority(CustomConstraint c, ArrayList<CustomConstraint> cp ) {
		if(c.getPriority() == ConstraintPriority.high) {
			
		}
		else if(c.getPriority() == ConstraintPriority.medium) {
			
		}
		else {
			
		}
	}
	/*
	 * This function calculates the cost of a drivers strategy profile and map this to a social cost
	 * So, if drivers swap training days, this assigns a cost to each driver. The lower the cost, the better the utility is for that driver. 
	 */
	private static void socialObjective() {
		
	}
	
	/*
	 * Finds the best slot based on the cost of the social objective function
	 */
	private static void findBestSlot() {
		// k best slots
		// sorting algorithm maybe?
	}
	
	/*
	 * Finds owner of the slot when a best slot is found
	 */
	private static void owner(ArrayList<TrainingSlot> solution) {
		// swap maybe?
	}
	
}
