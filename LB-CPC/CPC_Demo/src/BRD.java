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
// 

public class BRD {
	private static Random rnd = new Random();
	
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
		// check the priority
	}
	
	private static void checkPriority(CustomConstraint c, ArrayList<CustomConstraint> cp ) {
		if(c.getPriority() == ConstraintPriority.high) {
			
		}
		else if(c.getPriority() == ConstraintPriority.medium) {
			
		}
		else {
			
		}
	}
	
	private static void findBestSlot() {
		// k best slots
		// sorting algorithm maybe?
	}
	
	private static void owner(ArrayList<TrainingSlot> solution) {
		// swap maybe?
	}
	
}
