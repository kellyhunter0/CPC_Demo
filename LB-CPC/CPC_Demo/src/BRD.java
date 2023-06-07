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
 *  	  constraints that are broken in the solution set provided by the GA. 
 *  The associated cost of broken constraints based on their priority rating can be found in the ProblemParameters.java file
 */


public class BRD {
	private static Random rnd = new Random();
	private static ArrayList<Driver> drivers;
	
	
	/*
	 * This function will run Best Response Dynamics, also known as Better Response Dynamics, as a means of comparison to the GA
	 */
	private static void runBRD(boolean verbose, int runs) throws Exception {
		for(int run = 0; run < runs; run++) {
			ProblemParameters.EVALS = 0;
			Individual[] population = new Individual[ProblemParameters.POP_SIZE];
			int bestFit = Integer.MAX_VALUE;
			Individual best =null;
			
			
		}
	}
	
	private static ArrayList<Driver> getDriverSchedule() {
		return drivers;
	}
	private static ArrayList<Driver> setDriverSchedule(ArrayList<Driver> drivers) {
		return drivers;
	}
	
	/*
	 * This function hopes to improve the output generated from the GA, and will fix solutions with broken constraints based on priority
	 */
	private static void improveSol(Individual[] pop, ArrayList<TrainingSlot> solution)  {
		//DriverFactory df = new DriverFactory();
		int p1 = rnd.nextInt(pop.length);
		int p2 = rnd.nextInt(pop.length);
		//df.getDriverList(); 
		
		// need to compare custom constraint priority for each individual, and if one ranks higher than the other, then the slot is allocated to the higher priority case
		for(Individual p : pop) {
			for(TrainingSlot t : solution) {
				int d = t.getDay();
				int w = t.getWeek();
				int n = t.getNo();
				
				String week = Integer.toString(w);
				int game = game(pop);
				
				if(pop[p1].fitness() > pop[p2].fitness() && !solution.contains(t)) {
					if(!solution.contains(t) && pop[p1].getIntermediate().toString().contains(week)) {
						
						//allocated
					}
					else {
						
					}
				}
			}
			
		}
	}
	
	private static int game(Individual[] pop) {
		int p1 = rnd.nextInt(pop.length);
		int p2 = rnd.nextInt(pop.length);
		if(pop[p1].fitness() < pop[p2].fitness()) 
			return p1;
		else
			return p2;
				
	}
	
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
