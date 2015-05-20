package kim.kilho.ga;

import kim.kilho.ga.algorithm.Crossover;
import kim.kilho.ga.algorithm.Mutation;
import kim.kilho.ga.algorithm.Replacement;
import kim.kilho.ga.algorithm.Selection;
import kim.kilho.ga.algorithm.lk.LK;
import kim.kilho.ga.algorithm.lk.TwoEdgeTour;
import kim.kilho.ga.gene.Path;
import kim.kilho.ga.gene.PathPopulation;
import kim.kilho.ga.gene.Point;
import kim.kilho.ga.io.file.FileManager;
import kim.kilho.ga.io.file.TSPLib_IO;
import kim.kilho.ga.util.PointUtils;

import java.util.Random;

public class Main {

    public static final int MAXN = 1500; // Maximum value of N
    public static final int PSIZE = 50;  // Size of the population

    static TSPLib_IO TSP_FILE;

    // Population of solutions.
    static PathPopulation population;

    // Best records in the population.
    static Path recordPath;
    static double record;

    // The number of generations.
    static int numGenerations;

    // The number of local optimizations.
    static int numLKs;

    // Constants
    // Selection
    public static final int ROULETTE_WHEEL_SELECTION = 1;
      public static final double SELECTION_PRESSURE_PARAM = 3;
    public static final int TOURNAMENT_SELECTION = 2;
      public static final double SELECTION_TOURNAMENT_T = 0.8;

    // Crossover
    public static final int CYCLE_CROSSOVER = 1;
    public static final int ORDER_CROSSOVER = 2;
    public static final int PARTIALLY_MATCHED_CROSSOVER = 3;
    public static final int EDGE_RECOMBINATION = 4;

    // Mutation
    public static final double MUTATION_PROBABILITY = 0.3;
    public static final int DISPLACEMENT_MUTATION = 1;
    public static final int EXCHANGE_MUTATION = 2;
    public static final int INSERTION_MUTATION = 3;
    public static final int SIMPLE_INVERSION_MUTATION = 4;
    public static final int INVERSION_MUTATION = 5;
    public static final int SCRAMBLE_MUTATION = 6;

    // Replacement
    public static final int RANDOM_REPLACEMENT = 1;
    public static final int WORST_CASE_REPLACEMENT = 2;
    public static final int WORST_PARENT_REPLACEMENT = 3;
    public static final int WORST_PARENT_CASE_REPLACEMENT = 4;

    // How to run:
    // $ java Main data/cycle.in
    public static void main(String[] args) {
      final int NUM_ITERATIONS = 1;
      // final int NUM_ITERATIONS = 100;

      // int[] key = {se, xo, mt, rp};
      double[] result = new double[NUM_ITERATIONS];
      Path[] resultPath = new Path[NUM_ITERATIONS];
      int[] resultNumLKs = new int[NUM_ITERATIONS];
      int[] resultNumGenerations = new int[NUM_ITERATIONS];
      long[] resultTimeSpent = new long[NUM_ITERATIONS];


      for (int i = 0; i < result.length; i++) {
        System.out.println("************ iter #" + i + " ***************");
        long beginTime = System.currentTimeMillis()/1000;
        long timeSpent;
        init(args);
        numGenerations = 0;   // Initialize numGenerations
        numLKs = 0;  // Initialize numLKs

        /*
        // FIXME: Uncomment below to start hybrid GA
        GA(TOURNAMENT_SELECTION, ORDER_CROSSOVER,
           DISPLACEMENT_MUTATION, WORST_PARENT_CASE_REPLACEMENT,
           beginTime, true);
        recordPath = population.getRecord();
        record = population.getRecord().getDistance();
        */

        // FIXME: Uncomment below to start a single LK
        Path offspring = new Path(TSP_FILE.gNumCity, true);
        offspring = runLK(offspring, beginTime, TSP_FILE.timeLimit);
        recordPath = offspring;
        record = offspring.getDistance();

        /*
        // FIXME: Uncomment below to start a Multi-start LK
        int reps = 10;
        Path minOffspring = new Path(TSP_FILE.gNumCity, true);
        double minOffspringDist = Double.MAX_VALUE;
        int j;
        for (j = 0; j < reps; j++) {
          Path offspring = new Path(TSP_FILE.gNumCity, true);
          offspring = runLK(offspring, beginTime, TSP_FILE.timeLimit);
          if (minOffspringDist > offspring.getDistance()) {
            minOffspringDist = offspring.getDistance();
            minOffspring = offspring;
          }
          if (System.currentTimeMillis()/1000 - beginTime >= TSP_FILE.timeLimit - 1)
            break;    // end condition
        }
        recordPath = minOffspring;
        record = minOffspringDist;
        */

        result[i] = record;
        resultPath[i] = recordPath;
        resultNumLKs[i] = numLKs;
        resultNumGenerations[i] = numGenerations;
        timeSpent = System.currentTimeMillis()/1000 - beginTime;
        resultTimeSpent[i] = timeSpent;
        System.out.println("path: " + resultPath[i].toString());
        System.out.println("distance: " + result[i]);
        System.out.println("numLKs: " + resultNumLKs[i]);
        System.out.println("numGenerations: " + resultNumGenerations[i]);
        System.out.println("timeSpent: " + timeSpent);
        // System.out.println("reps: " + j);


        // finalize(args);
      }

      System.out.println("distances:");
      for (int i = 0; i < NUM_ITERATIONS; i++)
        System.out.println(result[i]);
      System.out.println("paths:");
      for (int i = 0; i < NUM_ITERATIONS; i++)
        System.out.println(resultPath[i].toString());
      System.out.println("numLKs:");
      for (int i = 0; i < NUM_ITERATIONS; i++)
        System.out.println(resultNumLKs[i]);
      System.out.println("numGenerations:");
      for (int i = 0; i < NUM_ITERATIONS; i++)
        System.out.println(resultNumGenerations[i]);
      System.out.println("timeSpents:");
      for (int i = 0; i < NUM_ITERATIONS; i++)
        System.out.println(resultTimeSpent[i]);
    }

    /**
     * Read the test case from file.
     * @param args
     */
    private static void init(String[] args) {
        TSP_FILE = new TSPLib_IO();
        TSP_FILE.readTspFile(args[0], MAXN);
        TSP_FILE.constructNN(20, false);
    }

    /**
     * Write the best result to file.
     * @param args
     */
    private static void finalize(String[] args) {
      FileManager fm = new FileManager();
      try {
        fm.write(args[0], population.getRecord());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    /**
     * A 'steady-state' GA
     */
    private static void GA(int selection, int crossover,
                           int mutation, int replacement,
                           long beginTime, boolean localOpt) {
        Random rnd = new Random();
      int iter = 0;

      population = new PathPopulation(PSIZE, TSP_FILE.gNumCity);
      // Do local optimization for the paths in population
        for (int i = 0; i < PSIZE; i++) {
          System.out.println("Optimizing path #" + i + " in population...");
          Path p = runLK(population.get(i), beginTime, TSP_FILE.timeLimit);
          population.set(i, p);
          if (population.getRecord().getDistance() > p.getDistance())
            population.setRecord(p);
        }
        // System.exit(0);
        // population.evaluateAll(points);

        try {
            System.out.println("# of points: " + TSP_FILE.gNumCity);
            System.out.println("time limit: " + TSP_FILE.timeLimit);
            System.out.println("GA Start!");
            while (true) {
              if (System.currentTimeMillis()/1000 - beginTime >= TSP_FILE.timeLimit - 1)
                break;    // end condition
              numGenerations++;

              if (++iter % 100 == 0) {
                System.out.println("********iter #" + iter + "**********");
                System.out.println(population.getRecord().toString());
                System.out.println(population.getRecord().getDistance());
              }

              // 1. Select two paths p1 and p2 from the population
              // TODO: Duplicated parents case?
              Path p1 = selection(selection, SELECTION_TOURNAMENT_T);
              Path p2 = selection(selection, SELECTION_TOURNAMENT_T);

              // 2. Crossover two paths to generate a new offspring
              Path offspring = crossover(p1, p2, crossover);

              // 3. Mutate the newly generated offspring
              //    if generated [0, 1) random value exceeds
              //    the mutation probability
              if (rnd.nextDouble() < MUTATION_PROBABILITY)
                offspring = mutation(offspring, mutation);

              // 4. Do local optimization
              // 5. Evaluate the distance value of newly generated offspring
              //    and update the best record
              if (localOpt)
                offspring = runLK(offspring, beginTime, TSP_FILE.timeLimit);

              if (population.getRecord().getDistance() > offspring.getDistance())
                population.setRecord(offspring);

              // 5. Replace one of the path in population with the new offspring
              population = replacement(offspring, replacement, p1, p2);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Run LK algorithm.
     * @param p
     * @return Path
     */
    private static Path runLK(Path p, long beginTime, double timeLimit) {
        numLKs++;
        // System.out.println("Start LK algorithm!");
        // Path offspring = LocalSearch.twoOpt(p, points, beginTime, timeLimit);
        LK lk = new LK(TSP_FILE.gNumCity, TSP_FILE.gNumNN, TSP_FILE);
        TwoEdgeTour offspringTour = new TwoEdgeTour(TSP_FILE.gNumCity, TSP_FILE);
        int n = TSP_FILE.gNumCity;
        int[] offspringPath = new int[n];

        // Run LK based on current offspring path
        offspringTour.convertFromPath(p.getPath());
        // offspringTour.makeRandomTour();
        lk.run(offspringTour, null, null, null, null, null);
        double tourcost = offspringTour.evaluate();

        // Convert TwoEdgeTour to Path and set distance
        offspringPath = offspringTour.convertToOrder(offspringPath, n);
        Path offspring = new Path(n, false);
        offspring.setPath(offspringPath);
        offspring.setDistance(tourcost);
        // System.out.println("path: " + offspring.toString());
        // System.out.println(offspring.getDistance());
        return offspring;
    }

    /**
     * Choose one path from the population.
     * @return Path
     * @exception Exception
     * @return Path
     */
    private static Path selection(int option, double selectionParam) throws Exception {
        switch(option) {
            case ROULETTE_WHEEL_SELECTION:
                return Selection.rouletteWheelSelection(population,
                        selectionParam);
            case TOURNAMENT_SELECTION:
                return Selection.tournamentSelection(population,
                        selectionParam);
            default:
                throw new Exception("Invalid option param.");
        }
    }

    private static Path crossover(Path p1, Path p2, int option) throws Exception {
        switch(option) {
            case CYCLE_CROSSOVER:
                return Crossover.cycleCrossover(p1, p2);
            case ORDER_CROSSOVER:
                return Crossover.orderCrossover(p1, p2);
            case PARTIALLY_MATCHED_CROSSOVER:
                return Crossover.partiallyMatchedCrossover(p1, p2);
            case EDGE_RECOMBINATION:
                return Crossover.edgeRecombination(p1, p2);
            default:
                throw new Exception("Invalid option param.");
        }
    }

    private static Path mutation(Path p, int option) throws Exception {
        switch(option) {
            case DISPLACEMENT_MUTATION:
                return Mutation.displacementMutation(p);
            case EXCHANGE_MUTATION:
                return Mutation.exchangeMutation(p);
            case INSERTION_MUTATION:
                return Mutation.insertionMutation(p);
            case SIMPLE_INVERSION_MUTATION:
                return Mutation.simpleInversionMutation(p);
            case INVERSION_MUTATION:
                return Mutation.inversionMutation(p);
            case SCRAMBLE_MUTATION:
                return Mutation.scrambleMutation(p);
            default:
                throw new Exception("Invalid option param.");
        }
    }

    /**
     * Replace one solution from the population with the new offspring.
     * @param p
     * @param option
     * @param p1
     * @prarm p2
     * @exception Exception
     * @return PathPopulation
     */
    private static PathPopulation replacement(Path p, int option,
                                              Path p1, Path p2) throws Exception {
        switch(option) {
            case RANDOM_REPLACEMENT:
                return Replacement.randomReplacement(population, p);
            case WORST_CASE_REPLACEMENT:
                return Replacement.worstCaseReplacement(population, p);
            case WORST_PARENT_REPLACEMENT:
                return Replacement.worstParentReplacement(population, p, p1, p2);
            case WORST_PARENT_CASE_REPLACEMENT:
                return Replacement.worstParentCaseReplacement(population, p, p1, p2);
            default:
                throw new Exception("Invalid option param.");
        }
    }
}

