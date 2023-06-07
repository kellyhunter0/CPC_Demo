import java.util.ArrayList;
import java.util.Random;

public class GA {
	private static Random rnd = new Random();



	public static void run(boolean verbose, int runs) throws Exception {
		for (int run = 0; run < runs; run++) {
			long start = System.currentTimeMillis();
			ProblemParameters.EVALS=0;
			Individual[] population = new Individual[ProblemParameters.POP_SIZE];
			int bestFit = Integer.MAX_VALUE;
			Individual best =null;

			//System.out.println("Run " + run);

			for (int x=0; x < population.length; x++) {
				population[x] = new Individual();
				if (population[x].fitness()<bestFit) {
					bestFit = population[x].fitness();
					best = population[x];
					//				System.out.println("Best =" + bestFit);
				}
			}



			int gen=0;
			int left=ProblemParameters.TIME_OUT;
			if (verbose) {
				System.out.println(gen + ",left,"+left+"," +best.stats()); // gen is the count
			}
			while(left >0){
				gen++;
				Individual child;
				if (rnd.nextBoolean()) {
					child = new Individual(population[tournament(population)]);
				}else {
					child = new Individual(population[tournament(population)],population[tournament(population)]);
				}

				child.mutate();



				int rip = rip(population);
				if (population[rip].fitness() > child.fitness()) {
					population[rip] = child;

					if(child.fitness()<bestFit) {
						left=ProblemParameters.TIME_OUT;
						best = child;
						bestFit = child.fitness();
					}
				}
				left--;
				if (ProblemParameters.EVALS >= ProblemParameters.MAX_EVALS)
					left =0; //Force timeout
				// Only outputs information in the thousands, so 1000, 2000, and so on
				if ((gen%1000 ==0)&&(verbose))
					System.out.println(gen + ",left,"+left+"," +best.stats());

			}
			
//			System.out.println("Final");
			long elapsed = System.currentTimeMillis() - start;
			double millisEval = elapsed / (double)ProblemParameters.EVALS;
			System.out.println("DataSet,"+ProblemParameters.DATASET_SEED+",Constraints,"+ProblemParameters.CUSTOMCONSTRAINTS+ ",run,"+run+"," +best.stats()+",Evals,"+ProblemParameters.EVALS+",MiilsEval,"+millisEval+",elapsed,"+elapsed);
//			best.printSummary();
			if (verbose) {
				best.printSol();
				
//				System.out.println("Intermediate");
//				for (String line : best.getIntermediate())
//					System.out.println(line);
			}
		}
		
	}

	private static int tournament(Individual[] pop) {
		int p1 = rnd.nextInt(pop.length);
		int p2 = rnd.nextInt(pop.length);
		if (pop[p1].fitness() < pop[p2].fitness())
			return p1;
		else
			return p2;	
	}


	private static int rip(Individual[] pop) {
		int p1 = rnd.nextInt(pop.length);
		int p2 = rnd.nextInt(pop.length);
		if (pop[p1].fitness() > pop[p2].fitness())
			return p1;
		else
			return p2;	
	}
	//	private static void test() throws Exception {
	////		for (int x=0; x < 200; x++) {
	//			Individual i = new Individual();			
	//			Individual z = new Individual();			
	//			
	//			System.out.println(i.fitness());
	//			System.out.println(z.fitness());
	////			if ((x%2)==0)
	////				i = new Individual(new Individual(),new Individual());
	////			else
	////				i = new Individual(new Individual());
	//
	//			
	////			for (int c=0; c < 100; c++) {
	////				i.mutate();
	////				System.out.println(i.fitness());
	////			}
	////		}
	//	}

}
