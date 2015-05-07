package kim.kilho.ga;

import kim.kilho.ga.algorithm.*;
import kim.kilho.ga.io.file.FileManager;
import kim.kilho.ga.util.ArrayUtils;

import java.util.Arrays;
import java.util.Random;

public class Main {

    public static final int MAXN = 600; // Maximum value of N
    public static final int PSIZE = 50;  // Size of the population

    // Population of solutions.
    static int[][] population;

    // Best records in the population.
    static int[] bestPath;
    static double record;

    // Total array of points.
    static double[] pX, pY;
    static double[][] dist;

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
        long beginTime = System.currentTimeMillis()/1000;
        init(args);
        GA(TOURNAMENT_SELECTION, ORDER_CROSSOVER,
           DISPLACEMENT_MUTATION, WORST_PARENT_CASE_REPLACEMENT,
           beginTime, true);
        System.out.println(Arrays.toString(bestPath));
        result[i] = record;
        System.out.println(result[i]);

        /*
          FIXME: Uncomment below to start a single 2-Opt
          Path p = new Path(points.length, true);
          runTwoOpt(p, points, beginTime, timeLimit);
         */

        /*
          FIXME: Uncomment below to start a Multi-start 2-Opt
          int reps = 10;
          Path[] paths = new Path[reps];
          for (int j = 0; j < reps; j++) {
            beginTime = System.currentTimeMillis()/1000;
            Path p = new Path(points.length, true);
            paths[j] = runTwoOpt(p, points, beginTime, timeLimit);
          }
         */
        // finalize(args);
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
            // points = (Point[])input[0];
            pX = (double[])input[0];
            pY = (double[])input[1];
            timeLimit = (Double)input[2];
            dist = new double[pX.length][pX.length];
            for (int i = 0; i < pX.length; i++) {
              for (int j = 0; j < pX.length; j++) {
                dist[i][j] = Math.sqrt(Math.pow(pX[i] - pX[j], 2)
                        + Math.pow(pY[i] - pY[j], 2));
              }
            }
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
        fm.write(args[0], bestPath);
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

        population = new int[PSIZE][pX.length];
        // Do local optimization for the paths in population
        for (int i = 0; i < PSIZE; i++) {
          System.out.println("Optimizing path #" + i + " in population...");
          population[i] = runTwoOpt(population[i], beginTime, timeLimit);
        }
        System.exit(0);
        // population.evaluateAll(points);

        try {
            System.out.println("# of points: " + pX.length);
            System.out.println("time limit: " + timeLimit);
            System.out.println("GA Start!");
            while (true) {
              // FIXME:
              if (System.currentTimeMillis()/1000 - beginTime >= timeLimit - 1)
                break;    // end condition

              if (iter % 1 == 0) {
                System.out.println("********iter #" + (iter++) + "**********");
                System.out.println(Arrays.toString(bestPath));
                System.out.println(record);
              }

              // 1. Select two paths p1 and p2 from the population
              // TODO: Duplicated parents case?
              int[] p1 = selection(selection, SELECTION_TOURNAMENT_T);
              int[] p2 = selection(selection, SELECTION_TOURNAMENT_T);

              // 2. Crossover two paths to generate a new offspring
              int[] offspring = crossover(p1, p2, crossover);

              // 3. Mutate the newly generated offspring
              //    if generated [0, 1) random value exceeds
              //    the mutation probability
              if (rnd.nextDouble() < MUTATION_PROBABILITY)
                offspring = mutation(offspring, mutation);

              // 4. Do local optimization
              if (localOpt)
                offspring = runTwoOpt(offspring, beginTime, timeLimit);

              // 5. Evaluate the distance value of newly generated offspring
              //    and update the best record
              double offspringDist = evaluate(offspring);
              if (record > offspringDist) {
                bestPath = offspring;
                record = offspringDist;
              }

              // 5. Replace one of the path in population with the new offspring
              replacement(offspring, replacement, p1, p2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Evaluate the total distance of the path
     * @param path, dist
     * @return double
     */
    private static double evaluate(int[] path) {
      double distance = 0;
      for (int i = 0; i < path.length; i++) {
        distance += dist[i][(i+1)%path.length];
      }

      return distance;
    }

    /**
     * Re-evaluate and update the distance of the path only for the swapped part.
     */
    private static double reEvaluate(int[] path, int i, int k) {
      double distance = evaluate(path);
      // Subtract the original distance of disconnected edges
      distance -= dist[(i-1+path.length)%path.length][k];
      distance -= dist[i][(k+1)%path.length];
      // Add the distance of newly replaced edges
      distance += dist[(i-1+path.length)%path.length][i];
      distance += dist[k][(k+1)%path.length];

      return distance;
    }


    /**
     * Run 2-Opt algorithm.
     * @param path, pX, pY, beginTime, timeLimit
     * @return int[]
     */
    private static int[] runTwoOpt(int[] path, long beginTime, double timeLimit) {
        // System.out.println("Start 2-Opt algorithm!");
        int[] offspring = twoOpt(path, beginTime, timeLimit);
        // System.out.println("path: " + offspring.toString());
        // System.out.println(offspring.getDistance());
        return offspring;
    }

    /**
     * 2-change for a part of path (path, i, k).
     *   take route[0] to route[i-1] and add them in order to new route
     *   take route[i] to route[k] and add them in reverse order to new_route
     *   take route[k+1] to end and add them in order to new_route
     *   return new_route;
     *   @param p
     *   @param i
     *   @param k
     *   @return Path
     */
    public static int[] twoChange(int[] p, int i, int k) {
      /*
      if (i > k)
        throw new Exception("Invalid input: i can't be higher than k");
      if (k > pX.length-1)
        throw new Exception("Invalid input: k exceeded the index limit of path");
      */

      int[] newPath = new int[pX.length];
      int j;

      // Take route[0] to route[i-1]
      // and add them in order to new route:
      for (j = 0; j < i; j++)
        newPath[j] = p[j];

      // Take route[i] to route[k]
      // and add them in reverse order to new_route:
      for (j = i; j <= k; j++)
        newPath[j] = p[k-(j-i)];

      // Take route[k+1] to end and add them in order to new_route
      for (j = k+1; j < pX.length; j++)
        newPath[j] = p[j];

      return newPath;
    }

    /**
     * The complete 2-opt swap algorithm.
     * (Source: 2-Opt, Wikipedia)
     *   repeat until no improvement is made:
     *     start again:
     *     best distance = calculateTotalDistance(existing route)
     *     for (i = 0; i < number of nodes eligible to be swapped - 1; i++):
     *       for (k = i + 1; k < number of nodes eligible to be swapped; k++):
     *         new route = twoOptSwap(existing route, i, k)
     *         new distance = calculateTotalDistance(new_route)
     *         if (new distance < best distance):
     *           existing route = new_route
     *           goto start_again
     * @param p
     * @return Path
     */
    public static int[] twoOpt(int[] p, long beginTime, double timeLimit) {
      boolean improved = true;
      double bestDistance = 0, newDistance = 0;
      int i, k;
      int miniChange = 0;
      int count = 0;

      while (improved) {
        // TODO: Make it not to repeat from the beginning right after it gets improved
        // System.out.println("Start again 2-Opt");
        improved = false;
        bestDistance = evaluate(p);
        restart:
        for (i = 0; i < pX.length-1; i++) {
          for (k = i+1; k < pX.length; k++) {
            count++;
            // Set emergency exit for twoOpt loop
            // in case of timeover:
            if (System.currentTimeMillis()/1000 - beginTime >= timeLimit - 1)
              return p;
            newDistance = reEvaluate(p, i, k);
            if (newDistance < bestDistance) {
              // FIXME: If the difference is lower than #, just break the loop
              // if (bestDistance - newDistance < 0.0000001) miniChange++;
              // if (miniChange > 10000) break restart;
              p = twoChange(p, i, k);
              improved = true;
              /*
              System.out.println("newDistance=" + newDistance +
                      ", bestDistance=" + bestDistance);
                      */
              break restart;
            }
          }
        }
      }
      System.out.println(count);

      return p;
    }


    /**
     * Choose one path from the population.
     * @return Path
     * @exception Exception
     * @return Path
     */
    private static int[] selection(int option, double selectionParam) throws Exception {
        switch(option) {
            /*
            case ROULETTE_WHEEL_SELECTION:
                return Selection.rouletteWheelSelection(population,
                        selectionParam);
            */
            case TOURNAMENT_SELECTION:
                return tournamentSelection(selectionParam);
            default:
                throw new Exception("Invalid option param.");
        }
    }

    /**
     * Tournament Selection - with parameter t in (0.5, 1)
     * @param t
     * @return int[]
     */
    public static int[] tournamentSelection(double t) {
      Random rnd = new Random();
      int i, k = 0;
      while ((int)Math.pow(2, k) < population.length) k++;
      // The number of candidates for the tournament.
      int numCandidates = (int)Math.pow(2, k-1);
      // Indices randomly picked up in [0, population.length)
      int[] idxs = ArrayUtils.genRandomIntegers(0, population.length);

      // Tournament candidates array where the selected paths are saved.
      // NOTE: The index will be included as the very last element
      int[][] candidates = new int[numCandidates][pX.length];
      for (i = 0; i < numCandidates; i++) {
        candidates[i] = population[idxs[i]];
        // System.out.println("candidates #" + i + "=" + candidates[i].toString()
        //                    + ", distance=" + candidates[i].getDistance());
      }

      return tournament(candidates, t)[0];
    }

    /**
     * Pick up the tournament round's final winner.
     * @param candidates
     * @param t
     * @return int[]
     */
    private static int[][] tournament(int[][] candidates, double t) {
      Random rnd = new Random();
      if (candidates.length == 1) return candidates;  // Break condition

      // System.out.println("*** NEW ROUND ***");
      int[][] nextRoundCandidates = new int[candidates.length/2][];
      for (int i = 0; i < candidates.length/2; i++) {
        int[] x1 = candidates[2*i];
        int[] x2 = candidates[2*i+1];
        // Ensure x1's distance is always better(smaller) than x2's
        if (evaluate(x1) > evaluate(x2)) {
          int[] tmp = x1; x1 = x2; x2 = tmp; // Swap
        }
        // System.out.println("x1.distance=" + x1.getDistance() + ", x2.distance=" + x2.getDistance());

        double r = rnd.nextDouble();
        // System.out.println("r=" + r);
        if (t > r)
          nextRoundCandidates[i] = x1;
        else
          nextRoundCandidates[i] = x2;
        // System.out.println("***nextRoundCandidates=" + nextRoundCandidates[i].toString());
      }

      // for (int i = 0; i < nextRoundCandidates.length; i++) {
      // System.out.println(nextRoundCandidates[i]);
      // }
      return tournament(nextRoundCandidates, t); // Run current method recursively
    }

    private static int[] crossover(int[] p1, int[] p2, int option) throws Exception {
        switch(option) {
            /*
            case CYCLE_CROSSOVER:
                return Crossover.cycleCrossover(p1, p2);
            */
            case ORDER_CROSSOVER:
                return orderCrossover(p1, p2);
            /*
            case PARTIALLY_MATCHED_CROSSOVER:
                return Crossover.partiallyMatchedCrossover(p1, p2);
            case EDGE_RECOMBINATION:
                return Crossover.edgeRecombination(p1, p2);
            */
            default:
                throw new Exception("Invalid option param.");
        }
    }

    /**
     * Order Crossover(OX).
     * @param p1
     * @param p2
     * @return int[]
     */
    public static int[] orderCrossover(int[] p1, int[] p2) {
      // Generate a new offspring with empty path.
      int[] newPath = new int[p1.length];

      // Randomly pick two cut points
      int[] cutPointIdxs = ArrayUtils.genRandomIntegers(0, p1.length, 2);
      Arrays.sort(cutPointIdxs);
      // System.out.println("cutPointIdxs=[" + cutPointIdxs[0] + "," + cutPointIdxs[1] + "]");

      int i, j = 0;
      // Temporarily store the cut part.
      int[] tmp = new int[cutPointIdxs[1]-cutPointIdxs[0]+1];
      for (i = cutPointIdxs[0]; i <= cutPointIdxs[1]; i++)
        tmp[i-cutPointIdxs[0]] = p1[i];

      // Put points after the right limit of the cut part.
      if (cutPointIdxs[1] < p1.length-1) { // the right cut point must not be the last point
        for (i = cutPointIdxs[1] + 1; i < p1.length; i++)
          // Put a point only if cut part doesn't contain it.
          if (ArrayUtils.indexOf(tmp, p2[i]) == -1)
            newPath[j++] = p2[i];
      }

      // Put points in the cut part in newPath.
      for (i = cutPointIdxs[0]; i <= cutPointIdxs[1]; i++)
        newPath[j++] = tmp[i-cutPointIdxs[0]];

      // Put points before the right limit of the cut part
      for (i = 0; i <= cutPointIdxs[1]; i++)
        // Put a point only if cut part doesn't contain it.
        if (ArrayUtils.indexOf(tmp, p2[i]) == -1)
          newPath[j++] = p2[i];

      return newPath;
    }

    private static int[] mutation(int[] p, int option) throws Exception {
        switch(option) {
            case DISPLACEMENT_MUTATION:
                return displacementMutation(p);
            /*
            case EXCHANGE_MUTATION: return Mutation.exchangeMutation(p);
            case INSERTION_MUTATION:
                return Mutation.insertionMutation(p);
            case SIMPLE_INVERSION_MUTATION:
                return Mutation.simpleInversionMutation(p);
            case INVERSION_MUTATION:
                return Mutation.inversionMutation(p);
            case SCRAMBLE_MUTATION:
                return Mutation.scrambleMutation(p);
            */
            default:
                throw new Exception("Invalid option param.");
        }
    }

    /**
     * Displacement Mutation.
     * @param p
     * @return int[]
     */
    public static int[] displacementMutation(int[] p) {
      // Generate a new offspring with empty path.
      int[] newPath = new int[p.length];

      Random rnd = new Random();

      // Randomly pick two indices as starting index and ending index of subpath
      // System.out.println("p.getLength()=" + p.getLength());
      int[] subpathIdxs = ArrayUtils.genRandomIntegers(0, p.length, 2);
      // System.out.println("subpathIdxs=" + Arrays.toString(subpathIdxs));
      Arrays.sort(subpathIdxs);
      // System.out.println("subpathIdxs=" + Arrays.toString(subpathIdxs));
      // Randomly pick a index as inserting point index of subpath
      int insertIdx = rnd.nextInt(p.length);

      // If the length of subpath equals the original length of parent
      if (subpathIdxs[1] - subpathIdxs[0] + 1 == p.length) {
        insertIdx = 0;
        // System.out.println("insertIdx=" + insertIdx);
        newPath = p;
      } else {
        // Ensure insertIdx is not in between the starting index and ending index
        while (insertIdx >= subpathIdxs[0] && insertIdx <= subpathIdxs[1])
          insertIdx = rnd.nextInt(p.length);
        // System.out.println("insertIdx=" + insertIdx);

        // Insert the subpath right after the inserting point
        int i, j = 0, k = subpathIdxs[0];
        for (i = 0; i < newPath.length; i++) {
          if (i >= subpathIdxs[0] && i <= subpathIdxs[1]) continue;
          newPath[j++] = p[i];
          if (i == insertIdx)
            while (k <= subpathIdxs[1])
              newPath[j++] = p[k++];
        }
      }

      return newPath;
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
    private static void replacement(int[] p, int option,
                                              int[] p1, int[] p2) throws Exception {
        switch(option) {
            /*
            case RANDOM_REPLACEMENT:
                return Replacement.randomReplacement(population, p);
            case WORST_CASE_REPLACEMENT:
                return Replacement.worstCaseReplacement(population, p);
            case WORST_PARENT_REPLACEMENT:
                return Replacement.worstParentReplacement(population, p, p1, p2);
            */
            case WORST_PARENT_CASE_REPLACEMENT:
                worstParentCaseReplacement(p, p1, p2);
            default:
                throw new Exception("Invalid option param.");
        }
    }

    public static void worstParentCaseReplacement(int[] p, int[] p1, int[] p2) {
      // The bigger the distance is, the worse the path is.
      // If two parents' are better than the current path, do worstCaseReplacement:
      double pDist = evaluate(p);
      if (pDist > evaluate(p1) && pDist > evaluate(p2)) {
        worstCaseReplacement(p);
        // If not, do worstParentReplacement:
      } else {
        worstParentReplacement(p, p1, p2);
      }
    }

    /**
     * Replacing the worst chromosome with the path in the population.
     * @param p
     */
    public static void worstCaseReplacement(int[] p) {
      int worstCaseIdx = 0;
      double maxDistance = 0;
      for (int i = 0; i < population.length; i++) {
        // The bigger the distance is, the worse the path is.
        double iDist = evaluate(population[i]);
        if (iDist > maxDistance) {
          maxDistance = iDist;
          worstCaseIdx = i;
        }
      }
      // System.out.println("worstCaseIdx=" + worstCaseIdx + ", maxDistance=" + maxDistance);
      population[worstCaseIdx] = p;
    }

    /**
     * Replacing the worse parent with the path in the population.
     * @param p
     * @param p1
     * @param p2
     */
    public static void worstParentReplacement(int[] p, int[] p1, int[] p2) {
      int worstParentIdx = 0;
      // The bigger the distance is, the worse the path is.
      worstParentIdx = evaluate(p1) > evaluate(p2)
              ? p1.getIdxInPopulation() : p2.getIdxInPopulation();
      /*
      System.out.println("worstParentIdx=" + worstParentIdx  + ", maxDistance="
              + (p1.getDistance() > p2.getDistance() ? p1.getDistance() : p2.getDistance()));
              */
      population[worstParentIdx] = p;
    }
}

