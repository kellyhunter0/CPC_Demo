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
	public static int fit;
//	private ArrayList<Gene> chromosome;
//	private ArrayList<TrainingSlot> free;
//	private Player[] pop;
	/*
	 * This function will run Best Response Dynamics, also known as Better Response Dynamics, as a means of comparison to the GA
	 */
	public static void runBRD(boolean verbose, int runs)  {
		int lowFree = 0;
		
		for(int run = 0; run < runs; run++) {
			ArrayList<String> high = new ArrayList<String>();
			ArrayList<String> med = new ArrayList<String>();
			ArrayList<String> low = new ArrayList<String>();
			long start = System.currentTimeMillis();
			verbose = true;
			ProblemParameters.EVALS = 0;
			Player[] player1 = new Player[ProblemParameters.POP_SIZE];
			//Player[] player2 = new Player[ProblemParameters.POP_SIZE];
			int utility = Integer.MAX_VALUE;
			fit = utility;
			Player bestResponse =null;
			//Player bestResponse2 =null;
			
//			ArrayList<Profile> profiles = new ArrayList<Profile>();
//			ArrayList<Player> players = new ArrayList<Player>();
//			ArrayList<Player> tempList = new ArrayList<Player>();
//			ArrayList<String> violations = new ArrayList<String>();
		//ArrayList<TrainingSlot> open = new ArrayList<TrainingSlot>();
			
		//	open = SlotFactory.getSlotSet();
			
			
			
			//System.out.println("Run " + run);
			for (int x=0; x < player1.length; x++) {
				
				player1[x] = new Player();
				
				if (player1[x].fitness()<utility) {
					utility = player1[x].fitness();
					bestResponse = player1[x];
					
									//System.out.println("Best =" + utility);
				}
			}

			
			Player.setHighFree(high);
			Player.setLowFree(low);
			Player.setMediumFree(med);
			//while(bestResponse.)

			int nOperations = 0;
			int left = ProblemParameters.TIME_OUT;
			if(verbose) {
				System.out.println(nOperations + ",left,"+left+"," +bestResponse.stats() ); // gen is the count
			}

			
			while(left >0){
				nOperations++;
				Profile p1 = new Profile();
			//	Profile p2 = new Profile();
				
					
				  p1.player = new Player(player1[game(player1)]);
				 // p1.player.swapsBool = false;

				 //p1.player.bestResponse();

				 
				 // p2.player = new Player(player2[game(player2)]);
				 // p2.player.bestResponse();

				//  for(TrainingSlot s :)
				  while(!p1.player.bestResponse()) {
						  p1.player.bestResponse();
					  
				  }


				int fit = fitness(player1);
				//int fit2 = fitness(player2);
				if (player1[fit].fitness() > p1.player.fitness()) {
					player1[fit] = p1.player;

					if(p1.player.fitness()<utility) {
						left=ProblemParameters.TIME_OUT;
						bestResponse = p1.player;
						utility = p1.player.fitness();
	
					}
				}

				//p1.player.bestResponse();
				

				left--;
				if (ProblemParameters.EVALS >= ProblemParameters.MAX_EVALS)
					left =0; //Force timeout
				// Only outputs information in the thousands, so 1000, 2000, and so on
				if (nOperations%1000 ==0 && verbose) 
					System.out.println(nOperations + ",left,"+left+"," +bestResponse.stats() + " "); // gen is the count
			//	}
			}
			
//			System.out.println("Final");
			long elapsed = System.currentTimeMillis() - start;
			double millisEval = elapsed / (double)ProblemParameters.EVALS;
			System.out.println("DataSet,"+ProblemParameters.DATASET_SEED+",Constraints,"+ProblemParameters.CUSTOMCONSTRAINTS+ ",run,"+run+"," +bestResponse.stats()+",Evals,"+ProblemParameters.EVALS+",MiilsEval,"+millisEval+",elapsed,"+elapsed);
//			best.printSummary();
			if (verbose) {
				bestResponse.printSol();
				
			}
		}
	}
	
	public Player getPlayer(Player[] p, Gene g) {
		int id = Integer.parseInt(g.driver.getID());
		//return players[g.slot.getWeek()][g.slot.getDay()][id][g.driver.group().ordinal()][cp.getPriority().ordinal()][this.getStrategy().ordinal()];
		if(p[id].toString().equals(g.driver.getID().toString())) {
			return p[id];
		}
		return p[id];
		
	}
	public static void improveSol(Player player)  {
		//DriverFactory df = new DriverFactory();
		Player[] pop = new Player[ProblemParameters.POP_SIZE];
	
		int p1 = rnd.nextInt(pop.length);
		int p2 = rnd.nextInt(pop.length);
		Player player2 = new Player();
		//df.getDriverList(); 
		for (int a = 0; a < pop.length; a++) { //To Display all current Student Information.
		    //   list[i] = new student();
		    player = pop[a];
		    //player.initPlayersFixSchedule(player.getViolations(), player.getMetConstraints(), player.getOpenToSwap());
		    
		}
		
		// need to compare custom constraint priority for each individual, and if one ranks higher than the other, then the slot is allocated to the higher priority case
//		for(String unhappy : pop[p1].getViolations() ) {
//			for(String free : pop[p2].getOpenToSwap()) {
//				String unhappyPerson[] = unhappy.split(",");
//				String weekPreference = unhappyPerson[2];
//				
//				String openToSwapWeek[] = free.split(",");
//				String swapWeek = openToSwapWeek[0];
//				if(swapWeek.contains(weekPreference) && free.contains("must be after")) {
//					// potential swap after one week!
//					System.out.println("Unhappy person week preference: " + weekPreference + "Swap week: " + swapWeek + "");
//					System.out.println("Unhappy string: " + unhappy);
//					System.out.println("Free slot string: " + free);
//				} else if (swapWeek.contains(weekPreference) && free.contains("must be before")) {
//					// allocate
//					System.out.println("Unhappy person week preference: " + weekPreference + "Swap week: " + swapWeek + "");
//					System.out.println("Unhappy string: " + unhappy);
//					System.out.println("Free slot string: " + free);
//				} else if (swapWeek.contains(weekPreference) && free.contains("must be in")) {
//					// allocate
//					System.out.println("Unhappy person week preference: " + weekPreference + "Swap week: " + swapWeek + "");
//					System.out.println("Unhappy string: " + unhappy);
//					System.out.println("Free slot string: " + free);
//				}
//				else if (swapWeek.contains(weekPreference) && free.contains("must not be in")) {
//					// allocate
//					System.out.println("Unhappy person week preference: " + weekPreference + "Swap week: " + swapWeek + "");
//					System.out.println("Unhappy string: " + unhappy);
//					System.out.println("Free slot string: " + free);
//				}
//				
//				//String week = Integer.toString(w);
//				int game = game(pop);
//				
//				if(pop[game].calculateUtility() > pop[p2].calculateUtility() ) {
//					//if(!solution.contains(t) && pop[p1].getIntermediate().toString().contains(week)) {
//						System.out.println("p1 " + pop[game].getUtility());
//						//allocated
//					}
//					else {
//						System.out.println("p2 " + pop[p2].getUtility());
//					}
//				}
//			}
			
		} 
	
	
	private static int game(Player[] pop) {
		int p1 = rnd.nextInt(pop.length);
		int p2 = rnd.nextInt(pop.length);
		if(pop[p1].fitness() < pop[p2].fitness()) 
			return p1;
		else
			return p2;
				
	}
	
	private static int fitness(Player[] pop) {
		int p1 = rnd.nextInt(pop.length);
		int p2 = rnd.nextInt(pop.length);
		if(pop[p1].fitness() > pop[p2].fitness()) 
			return p1;
		else
			return p2;
				
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
