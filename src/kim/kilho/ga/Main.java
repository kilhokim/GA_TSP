package kim.kilho.ga;

import kim.kilho.ga.algorithm.*;
import kim.kilho.ga.gene.Path;
import kim.kilho.ga.gene.PathPopulation;
import kim.kilho.ga.gene.Point;
import kim.kilho.ga.io.file.FileManager;

import java.util.Random;

public class Main {

    public static final int MAXN = 600; // Maximum value of N
    public static final int PSIZE = 50;  // Size of the population
    // Population of solutions.
    static PathPopulation population;
    // Total array of points.
    static Point[] points = null;
    // Time limit for the test case
    static double timeLimit;

    static FileManager fm;

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

      // int[] key = {se, xo, mt, rp};
      double[] result = new double[NUM_ITERATIONS];

      for (int i = 0; i < result.length; i++) {
        init(args);
        GA(TOURNAMENT_SELECTION, ORDER_CROSSOVER,
           DISPLACEMENT_MUTATION, WORST_PARENT_CASE_REPLACEMENT, true);
        System.out.println(population.getRecord().toString());
        result[i] = population.getRecord().getDistance();
        System.out.println(result[i]);
        finalize(args);
      }
    }

    /**
     * Read the test case from file.
     * @param args
     */
    private static void init(String[] args) {
        fm = new FileManager();
        try {
            Object[] input = fm.read(args[0], MAXN);
            points = (Point[])input[0];
            timeLimit = (Double)input[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Write the best result to file.
     * @param args
     */
    private static void finalize(String[] args) {
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
                           boolean localOpt) {
        long beginTime = System.currentTimeMillis()/1000;
        Random rnd = new Random();
        int iter = 0;

        population = new PathPopulation(PSIZE, points.length);
        population.evaluateAll(points);

        try {
            System.out.println("# of points: " + points.length);
            System.out.println("time limit: " + timeLimit);
            System.out.println("GA Start!");
            while (true) {
              // FIXME:
              if (System.currentTimeMillis()/1000 - beginTime >= timeLimit - 1)
                break;    // end condition

              if (iter % 1 == 0) {
                System.out.println("********iter #" + (iter++) + "**********");
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
              offspring = runTwoOpt(offspring, points, beginTime, timeLimit);

              // 5. Evaluate the distance value of newly generated offspring
              //    and update the best record
              offspring.evaluate(points);
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
     * Run 2-Opt algorithm.
     * @param p
     * @return Path
     */
    private static Path runTwoOpt(Path p, Point[] points,
                                  long beginTime, double timeLimit) {
        // System.out.println("Start 2-Opt algorithm!");
        Path offspring = LocalSearch.twoOpt(p, points, beginTime, timeLimit);
        // System.out.println("iter #" + iter + " distance=" + path.evaluate(points));
        // System.out.println("path: " + path.toString());
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

