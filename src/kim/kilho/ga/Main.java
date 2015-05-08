package kim.kilho.ga;

import kim.kilho.ga.io.file.FileManager;
import kim.kilho.ga.util.ArrayUtils;

import java.util.Arrays;
import java.util.Random;

public class Main {
    public static final int ROUNDNUM = 1000;

    public static final int MAXN = 600; // Maximum value of N
    public static final int PSIZE = 50;  // Size of the population

    // Population of solutions.
    static int[][] population;

    // Distances for each path.
    static double[] distance;
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
        long beginTime = System.currentTimeMillis()/ROUNDNUM;
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
          Path[] ps = new Path[reps];
          for (int j = 0; j < reps; j++) {
            beginTime = System.currentTimeMillis()/1000;
            Path p = new Path(points.length, true);
            ps[j] = runTwoOpt(p, points, beginTime, timeLimit);
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
                dist[i][j] = Math.sqrt((pX[i]-pX[j])*(pX[i]-pX[j])
                                     + (pY[i]-pY[j])*(pY[i]-pY[j]));
                // dist[i][j] = (double)Math.round(dist[i][j]*ROUNDNUM)/ROUNDNUM;

                // dist[i][j] = Math.sqrt(Math.pow(pX[i] - pX[j], 2)
                        // + Math.pow(pY[i] - pY[j], 2));
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
        distance = new double[population.length];
        record = Double.MAX_VALUE;
        // Generate random order-based ps in the population.
        for (int i = 0; i < population.length; i++) {
          population[i] = ArrayUtils.genRandomIntegers(0, pX.length);
          distance[i] = evaluate(population[i]);
          if (distance[i] < record) {
            record = distance[i];
            bestPath = population[i];
          }
          // System.out.println(Arrays.toString(population[i]));
        }
        // record = Double.MAX_VALUE;

        /*
        for (int i = 0; i < dist.length; i++)
          System.out.println(Arrays.toString(dist[i]));
        */

        // Do local optimization for the ps in population
        for (int i = 0; i < population.length; i++) {
          System.out.println("Optimizing p #" + i + " in population...");
          population[i] = twoOpt(population[i], beginTime, timeLimit);
          distance[i] = evaluate(population[i]);
          // System.out.println(Arrays.toString(population[i]));
          System.out.println(distance[i]);
          if (distance[i] < record) {
            record = distance[i];
            bestPath = population[i];
          }
        }
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

              // 1. Select two ps p1 and p2 from the population
              // TODO: Duplicated parents case?
              int[] tmp1 = selection(selection, SELECTION_TOURNAMENT_T);
              int[] tmp2 = selection(selection, SELECTION_TOURNAMENT_T);
              int[] p1 = new int[pX.length], p2 = new int[pX.length];
              for (int i = 0; i < pX.length; i++) {
                p1[i] = tmp1[i];
                p2[i] = tmp2[i];
              }
              // Extract the indices of parents from the selection result arrays
              int p1Idx = tmp1[pX.length], p2Idx = tmp2[pX.length];

              // 2. Crossover two ps to generate a new offspring
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

              // 5. Replace one of the p in population with the new offspring
              replacement(offspring, replacement, p1, p2, p1Idx, p2Idx);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Evaluate the total distance of the p
     * @param p, dist
     * @return double
     */
    private static double evaluate(int[] p) {
      double distance = 0;
      for (int i = 0; i < p.length; i++)
        distance += dist[p[i]][p[(i+1)%p.length]];

      return distance;
      // return (double)Math.round(distance*ROUNDNUM)/ROUNDNUM;
    }

    /**
     * Re-evaluate and update the distance of the p only for the swapped part.
     */
    private static double distanceGain(int[] p, int i, int k) {
      // double distance = 0;
      // Subtract the original distance of disconnected edges
      // distance -= dist[p[(i-1+p.length)%p.length]][p[i]];
      // distance -= dist[p[k]][p[(k+1)%p.length]];
      // Add the distance of newly replaced edges
      // distance += dist[p[(i-1+p.length)%p.length]][p[k]];
      // distance += dist[p[i]][p[(k+1)%p.length]];

      // return distance;
      double gain = dist[p[(i-1+p.length)%p.length]][p[k]] + dist[p[i]][p[(k+1)%p.length]]
                 - (dist[p[(i-1+p.length)%p.length]][p[i]] + dist[p[k]][p[(k+1)%p.length]]);
      return gain;
      // return (double)Math.round(gain*ROUNDNUM)/ROUNDNUM;
    }


    /**
     * Run 2-Opt algorithm.
     * @param p, beginTime, timeLimit
     * @return int[]
     */
    private static int[] runTwoOpt(int[] p, long beginTime, double timeLimit) {
        // System.out.println("Start 2-Opt algorithm!");
        int[] offspring = twoOpt(p, beginTime, timeLimit);
        // System.out.println("p: " + offspring.toString());
        // System.out.println(offspring.getDistance());
        return offspring;
    }

    /**
     * 2-change for a part of p (p, i, k).
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
        throw new Exception("Invalid input: k exceeded the index limit of p");
      */

      int[] newPath = new int[p.length];
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
      for (j = k+1; j < p.length; j++)
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
      int[] newP = new int[p.length];
      boolean improved = true;
      double bestDistance = 0, distanceGain = 0, newDistance = 0;
      int i, k, j;
      int count = 0;

      while (improved) {
        // TODO: Make it not to repeat from the beginning right after it gets improved
        // System.out.println("Start again 2-Opt");
        improved = false;
        // bestDistance = evaluate(p);
        restart:
        for (i = 0; i < pX.length-1; i++) {
          for (k = i+1; k < pX.length-1; k++) {
            /*
            try {
              Thread.sleep(10);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            */
            count++;
            // Set emergency exit for twoOpt loop
            // in case of timeover:
            if (System.currentTimeMillis()/1000 - beginTime >= timeLimit - 1)
              return p;
            // bestDistance = evaluate(p);
            distanceGain = dist[p[(i-1+p.length)%p.length]][p[k]] + dist[p[i]][p[(k+1)%p.length]]
                        - (dist[p[(i-1+p.length)%p.length]][p[i]] + dist[p[k]][p[(k+1)%p.length]]);
            // bestDistance = dist[p[(i-1+p.length)%p.length]][p[i]] + dist[p[k]][p[(k+1)%p.length]];
            // newDistance = dist[p[(i-1+p.length)%p.length]][p[k]] + dist[p[i]][p[(k+1)%p.length]];
            // newP = twoChange(p, i, k);
            // newDistance = evaluate(newP);
            // distanceGain = distanceGain(p, i, k);
            if (distanceGain < 0) {
            // if (newDistance < bestDistance) {
              // FIXME: If the difference is lower than #, just break the loop
              // if (bestDistance - newDistance < 0.0000001) miniChange++;
              // if (miniChange > 10000) break restart;

              /*
              // Take route[0] to route[i-1]
              // and add them in order to new route:
              for (j = 0; j < i; j++)
                newP[j] = p[j];
              // Take route[i] to route[k]
              // and add them in reverse order to new_route:
              for (j = i; j <= k; j++)
                newP[j] = p[k - (j - i)];
              // Take route[k+1] to end and add them in order to new_route
              for (j = k+1; j < p.length; j++)
                newP[j] = p[j];
              */

              // p = newP;
              // for (int j = 0; j < p.length; j++) p[j] = newP[j];
              p = twoChange(p, i, k);
              improved = true;

              // System.out.println("distanceGain=" + distanceGain + ", newDistance=" + evaluate(newP) + ", bestDistance=" + bestDistance);
              // System.out.println("i: " + i + ", k: " + k);
              // System.out.println(Arrays.toString(p));
              // System.out.println("newDistance=" + newDistance + ", bestDistance=" + bestDistance);
              // System.out.println("newDistance=" + evaluate(p) + ", bestDistance=" + bestDistance);
              break restart;
            }
          }
        }
      }
      System.out.println(count);

      return p;
    }


    /**
     * Choose one p from the population.
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
      int i, j, k = 0;
      while ((int)Math.pow(2, k) < population.length) k++;
      // The number of candidates for the tournament.
      int numCandidates = (int)Math.pow(2, k-1);
      // Indices randomly picked up in [0, population.length)
      int[] idxs = ArrayUtils.genRandomIntegers(0, population.length);

      // Tournament candidates array where the selected ps are saved.
      // NOTE: The index will be included as the very last element
      int[][] candidates = new int[numCandidates][pX.length+1];
      for (i = 0; i < numCandidates; i++) {
        for (j = 0; j < pX.length; j++)
          candidates[i][j] = population[idxs[i]][j];
        // Save the parent's index
        candidates[i][pX.length] = idxs[i];
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
      // NOTE: The index will be included as the very last element
      int[][] nextRoundCandidates = new int[candidates.length/2][];
      for (int i = 0; i < nextRoundCandidates.length; i++) {
        int[] tmp1 = candidates[2*i]; // pX.length+1 (index included)
        int[] tmp2 = candidates[2*i+1]; // pX.length+1 (index included)
        int[] x1 = new int[pX.length], x2 = new int[pX.length];
        for (int j = 0; j < pX.length; j++) {
          x1[j] = tmp1[j];
          x2[j] = tmp2[j];
        }

        // Ensure x1's distance is always better(smaller) than x2's
        if (evaluate(x1) > evaluate(x2)) {
          int[] tmp = x1; x1 = x2; x2 = tmp; // Swap
        }
        // System.out.println("x1.distance=" + x1.getDistance() + ", x2.distance=" + x2.getDistance());

        double r = rnd.nextDouble();
        // System.out.println("r=" + r);
        if (t > r) {
          nextRoundCandidates[i] = tmp1;
        }
        else
          nextRoundCandidates[i] = tmp2;
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
      // Generate a new offspring with empty p.
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
      // Generate a new offspring with empty p.
      int[] newPath = new int[p.length];

      Random rnd = new Random();

      // Randomly pick two indices as starting index and ending index of subp
      // System.out.println("p.getLength()=" + p.getLength());
      int[] subpIdxs = ArrayUtils.genRandomIntegers(0, p.length, 2);
      // System.out.println("subpIdxs=" + Arrays.toString(subpIdxs));
      Arrays.sort(subpIdxs);
      // System.out.println("subpIdxs=" + Arrays.toString(subpIdxs));
      // Randomly pick a index as inserting point index of subp
      int insertIdx = rnd.nextInt(p.length);

      // If the length of subp equals the original length of parent
      if (subpIdxs[1] - subpIdxs[0] + 1 == p.length) {
        insertIdx = 0;
        // System.out.println("insertIdx=" + insertIdx);
        newPath = p;
      } else {
        // Ensure insertIdx is not in between the starting index and ending index
        while (insertIdx >= subpIdxs[0] && insertIdx <= subpIdxs[1])
          insertIdx = rnd.nextInt(p.length);
        // System.out.println("insertIdx=" + insertIdx);

        // Insert the subp right after the inserting point
        int i, j = 0, k = subpIdxs[0];
        for (i = 0; i < newPath.length; i++) {
          if (i >= subpIdxs[0] && i <= subpIdxs[1]) continue;
          newPath[j++] = p[i];
          if (i == insertIdx)
            while (k <= subpIdxs[1])
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
    private static void replacement(int[] p, int option, int[] p1, int[] p2,
                                    int p1Idx, int p2Idx) throws Exception {
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
                worstParentCaseReplacement(p, p1, p2, p1Idx, p2Idx);
                break;
            default:
                throw new Exception("Invalid option param.");
        }
    }

    public static void worstParentCaseReplacement(int[] p, int[] p1, int[] p2,
                                                  int p1Idx, int p2Idx) {
      // The bigger the distance is, the worse the p is.
      // If two parents' are better than the current p, do worstCaseReplacement:
      double pDist = evaluate(p);
      if (pDist > evaluate(p1) && pDist > evaluate(p2)) {
        worstCaseReplacement(p);
        // If not, do worstParentReplacement:
      } else {
        worstParentReplacement(p, p1, p2, p1Idx, p2Idx);
      }
    }

    /**
     * Replacing the worst chromosome with the p in the population.
     * @param p
     */
    public static void worstCaseReplacement(int[] p) {
      int worstCaseIdx = 0;
      double maxDistance = 0;
      for (int i = 0; i < population.length; i++) {
        // The bigger the distance is, the worse the p is.
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
     * Replacing the worse parent with the p in the population.
     * @param p
     * @param p1
     * @param p2
     */
    public static void worstParentReplacement(int[] p, int[] p1, int[] p2,
                                              int p1Idx, int p2Idx) {
      int worstParentIdx = 0;
      // The bigger the distance is, the worse the p is.
      worstParentIdx = evaluate(p1) > evaluate(p2) ? p1Idx : p2Idx;
      /*
      System.out.println("worstParentIdx=" + worstParentIdx  + ", maxDistance="
              + (p1.getDistance() > p2.getDistance() ? p1.getDistance() : p2.getDistance()));
              */
      population[worstParentIdx] = p;
    }
}

