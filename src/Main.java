import kim.kilho.ga.algorithm.Crossover;
import kim.kilho.ga.algorithm.Mutation;
import kim.kilho.ga.algorithm.Selection;
import kim.kilho.ga.gene.Path;
import kim.kilho.ga.gene.Point;
import kim.kilho.ga.io.file.FileManager;
import kim.kilho.ga.util.PointUtils;

import java.util.Random;

public class Main {

    /*
    // static void Input(String fileName) {  }

    static void Input() {  }

    static void Initialize() {  }

    static int Selection(int selectionMethod) { return -1; }

    static int Roulette() {  return -1; }

    static int Tournament() { return -1; }

    static void Crossover(int p1, int p2, int crossoverMethod) {  }

    static void ER(int p1, int p2) {  }

    static void OX(int p1, int p2) {  }

    static void Mutation(int mutationMethod) {  }

    static void DM() {  }

    static void IVM() { }

    static void SIM() {  }

    static void Evaluation() { }

    static void Replacement(int p1, int p2) {}

    static void GA(int selectionMethod, int crossoverMethod, int mutationMethod) {  }

    static void Output() {}
    */

    public static final int MAXN = 318; // Maximum value of N
    public static final int PSIZE = 100;  // Size of the population
    // Population of solutions.
    static Path[] population = new Path[PSIZE];
    // Best (found) solution.
    static Path record = null;

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
    public static final int TYPICAL_MUTATION = 1;

    // How to run:
    // $ java Test data/cycle.in
    public static void main(String[] args) {

        init(args);
        GA();

        // System.out.println(System.getProperty("user.dir"));
    }

    // Read the test case from file.
    private static void init(String[] args) {
        FileManager fm = new FileManager();
        try {
            Object[] input = fm.read(args[0], MAXN);
            points = (Point[])input[0];
            timeLimit = (Double)input[1];
            // System.out.println("Total number of points=" + points.length);
            for (int i = 0; i < points.length; i++) {
                System.out.println(points[i].toString());
            }
            // System.out.println("Total time limit=" + timeLimit);
            // System.out.println(PointUtils.distance(points[0], points[1]));
            // System.out.println(PointUtils.distance(points[1], points[2]));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * A 'steady-state' GA
     */
    private static void GA() {
        long begin = System.currentTimeMillis()/1000;

        for (int i = 0; i < population.length; i++) {
            population[i] = new Path(MAXN, true);
            // System.out.println("------------ Path #" + i + " -----------------");
            // System.out.println(population[i].toString());
        }

        try {
            // while (true) {
            if (System.currentTimeMillis()/1000 - begin >= timeLimit - 1) {
                System.out.println("break!");
                // break;    // end condition
            }
            // Select two paths p1 and p2 from population
            Path p1 = selection(ROULETTE_WHEEL_SELECTION);
            Path p2 = selection(ROULETTE_WHEEL_SELECTION);
//        Path p1 = selection(TOURNAMENT_SELECTION);
//        Path p2 = selection(TOURNAMENT_SELECTION);
            System.out.println("p1: " + p1.toString());
            System.out.println("p2: " + p2.toString());
//        Path offspring = crossover(p1, p2, ONE_POINT_CROSSOVER);
//        Path offspring = crossover(p1, p2, MULTI_POINT_CROSSOVER);
            Path offspring = crossover(p1, p2, UNIFORM_CROSSOVER);
//        Path offspring = crossover(p1, p2, CYCLE_CROSSOVER);
//        Path offspring = crossover(p1, p2, ORDER_CROSSOVER);
//        Path offspring = crossover(p1, p2, PARTIALLY_MATCHED_CROSSOVER);
//        Path offspring = crossover(p1, p2, ARITHMETIC_CROSSOVER);
//        Path offspring = crossover(p1, p2, EDGE_RECOMBINATION);
            offspring = mutation(offspring, TYPICAL_MUTATION);
            // Path[] offsprings = null; // TODO: add offsprings into this path array
            // }
        } catch (Exception e) {
            e.getMessage();
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
            case 1:
                return Selection.rouletteWheelSelection(population,
                        SELECTION_PRESSURE_PARAM);
            case 2:
                return Selection.tournamentSelection(population,
                        SELECTION_TOURNAMENT_T);
            default:
                throw new Exception("Invalid option input.");
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
                throw new Exception("Invalid option input.");
        }
    }

    private static Path mutation(Path p, int option) throws Exception {
        switch(option) {
            case TYPICAL_MUTATION:
                return Mutation.typicalMutation(p);
            default:
                throw new Exception("Invalid option input.");
        }
    }

    /**
     * Replace one solution from the population with the new offspring.
     * Currently any random solution can be replaced.
     */
    private static void replacement(Path p) {
        // FIXME:
        Random rnd = new Random();
        int idx = rnd.nextInt(population.length);
        population[idx].setFitness(p.getFitness());
        for (int i = 0; i < MAXN; i++)
            population[idx].setPath(p.getPath());
    }
}

