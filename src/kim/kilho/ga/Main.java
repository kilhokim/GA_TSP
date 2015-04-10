package kim.kilho.ga;

import kim.kilho.ga.algorithm.Crossover;
import kim.kilho.ga.algorithm.Mutation;
import kim.kilho.ga.algorithm.Replacement;
import kim.kilho.ga.algorithm.Selection;
import kim.kilho.ga.gene.Path;
import kim.kilho.ga.gene.PathPopulation;
import kim.kilho.ga.gene.Point;
import kim.kilho.ga.io.file.FileManager;
import kim.kilho.ga.util.PointUtils;

import java.util.Random;

public class Main {

    public static final int MAXN = 318; // Maximum value of N
    public static final int PSIZE = 100;  // Size of the population
    // Population of solutions.
    static PathPopulation population;
    // Total array of points.
    static Point[] points = null;
    // Time limit for the test case
    static double timeLimit;

    // Constants
    // Selection
    public static final int ROULETTE_WHEEL_SELECTION = 1;
      public static final double SELECTION_PRESSURE_PARAM = 3;
      public static final double SELECTION_TOURNAMENT_T = 0.9;
    public static final int TOURNAMENT_SELECTION = 2;

    // Crossover
    public static final int CYCLE_CROSSOVER = 1;
    public static final int ORDER_CROSSOVER = 2;
    public static final int PARTIALLY_MATCHED_CROSSOVER = 3;
    public static final int EDGE_RECOMBINATION = 4;

    // Mutation
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
    // $ java Test data/cycle.in
    public static void main(String[] args) {

        init(args);
        GA();

        System.out.println("The best record: " + population.getRecord().toString());
        System.out.println("fitness: " + population.getRecord().getFitness());
    }

    // Read the test case from file.
    private static void init(String[] args) {
        FileManager fm = new FileManager();
        try {
            Object[] input = fm.read(args[0], MAXN);
            points = (Point[])input[0];
            timeLimit = (Double)input[1];
            System.out.println("Total number of points=" + points.length);
            for (int i = 0; i < points.length; i++) {
                System.out.println(points[i].toString());
            }
            System.out.println("Total time limit=" + timeLimit);
            // System.out.println(PointUtils.distance(points[0], points[1]));
            // System.out.println(PointUtils.distance(points[1], points[2]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A 'steady-state' GA
     */
    private static void GA() {
        int iter = 0;
        long beginTime = System.currentTimeMillis()/1000;

        population = new PathPopulation(PSIZE, points.length);
        population.evaluateAll(points);

        System.out.println("Start GA!");
        try {
            while (true) {
              System.out.println("**********iter #" + (++iter) + "************");
              if (System.currentTimeMillis()/1000 - beginTime >= timeLimit - 1) {
                System.out.println("break!");
                break;    // end condition
              }

              if (iter > 100) {
                System.out.println("break!");
                break;
              }

              // 1. Select two paths p1 and p2 from the population
              // TODO: Duplicated parents case?
//              Path p1 = selection(ROULETTE_WHEEL_SELECTION);
//              Path p2 = selection(ROULETTE_WHEEL_SELECTION);
              Path p1 = selection(TOURNAMENT_SELECTION);
              Path p2 = selection(TOURNAMENT_SELECTION);
              System.out.println("p1: " + p1.toString()
                      + " , idx=" + p1.getIdxInPopulation()
                      + ", fitness=" + p1.getFitness());
              System.out.println("p2: " + p2.toString()
                      + " , idx=" + p2.getIdxInPopulation()
                      + ", fitness=" + p2.getFitness());

              // 2. Crossover two paths to generate a new offspring
//              Path offspring = crossover(p1, p2, CYCLE_CROSSOVER);
//                Path offspring = crossover(p1, p2, ORDER_CROSSOVER);
                Path offspring = crossover(p1, p2, PARTIALLY_MATCHED_CROSSOVER);
              System.out.println("offspring after crossover: " + offspring.toString());

              // 3. Mutate the newly generated offspring
//              offspring = mutation(offspring, DISPLACEMENT_MUTATION);
//              offspring = mutation(offspring, EXCHANGE_MUTATION);
//              offspring = mutation(offspring, INSERTION_MUTATION);
//              offspring = mutation(offspring, SIMPLE_INVERSION_MUTATION); // TODO: Problem...
              offspring = mutation(offspring, INVERSION_MUTATION);
//              offspring = mutation(offspring, SCRAMBLE_MUTATION);
              System.out.println("offspring after mutation: " + offspring.toString());

              // 4. Evaluate the fitness value of newly generated offspring
              //    and update the best record
              offspring.evaluate(points);
              System.out.println("fitness of the offspring=" + offspring.getFitness());
              if (population.getRecord().getFitness() > offspring.getFitness())
                  population.setRecord(offspring);

              // 5. Replace one of the path in population with the new offspring
//              population = replacement(offspring, RANDOM_REPLACEMENT, p1, p2);
//              population = replacement(offspring, WORST_CASE_REPLACEMENT, p1, p2);
//              population = replacement(offspring, WORST_PARENT_REPLACEMENT, p1, p2);
              population = replacement(offspring, WORST_PARENT_CASE_REPLACEMENT, p1, p2);
              System.out.println("current record=" + population.getRecord());
              System.out.println("fitness=" + population.getRecord().getFitness());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Choose one path from the population.
     * Currently this operator randomly chooses one with uniform distribution.
     * @return Path
     */
    private static Path selection(int option) throws Exception {
        switch(option) {
            // 1. Roulette Wheel Selection.
            case ROULETTE_WHEEL_SELECTION:
                return Selection.rouletteWheelSelection(population,
                        SELECTION_PRESSURE_PARAM);
            case TOURNAMENT_SELECTION:
                return Selection.tournamentSelection(population,
                        SELECTION_TOURNAMENT_T);
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
     * Currently any random solution can be replaced.
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

