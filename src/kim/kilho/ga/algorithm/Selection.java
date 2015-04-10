package kim.kilho.ga.algorithm;

import kim.kilho.ga.exception.SelectionException;
import kim.kilho.ga.gene.Path;
import kim.kilho.ga.gene.PathPopulation;
import kim.kilho.ga.util.ArrayUtils;

import java.util.Random;

/**
 * Selection algorithms for GA.
 * @author Kilho Kim
 */
public class Selection {

  /**
   * Roulette Wheel Selection.
   * @param population
   * @return Path
   */
  public static Path rouletteWheelSelection(PathPopulation population, double selectionPressureParam) {
    double sumOfFitness = 0, sum = 0;
    double minCost = Double.MAX_VALUE, maxCost = 0, currCost;
    // Fitness values used in Selection
    // for each solution in the original population.
    double[] selectionFitness = new double[population.size()];
    Random rnd = new Random();
    int i;

    // Pick up the maximum and the minimum fitness value.
    for (i = 0; i < population.size(); i++) {
      currCost = population.get(i).getFitness();
      if (currCost > maxCost)
        maxCost = currCost;
      else if (currCost < minCost)
        minCost = currCost;
    }
    // Calculate the Selection fitness values for each solution.
    for (i = 0; i < population.size(); i++) {
      currCost = population.get(i).getFitness();
      selectionFitness[i] = (maxCost - currCost)
              + (maxCost - minCost)/(selectionPressureParam-1);
    }

    // TODO: Rank-based Selection
    // TODO: Sharing

    for (i = 0; i < population.size(); i++) {
      sumOfFitness += selectionFitness[i];
      // System.out.println("#" + i + " sumOfFitness=" + sumOfFitness);
    }
    double point = rnd.nextDouble() * sumOfFitness;
    // System.out.println("point=" + point);

    for (i = 0; i < population.size(); i++) {
      sum += selectionFitness[i];
      // System.out.println("#" + i + " sum=" + sum);
      if (point < sum) return population.get(i);
    }

    throw new SelectionException("Error! This shouldn't be reached!");
  }

  /**
   * Tournament Selection - with parameter t in (0.5, 1)
   * @param population
   * @param t
   * @return
   */
  public static Path tournamentSelection(PathPopulation population, double t) {
    Random rnd = new Random();
    int i, k = 0;
    while ((int)Math.pow(2, k) < population.size()) k++;
    // The number of candidates for the tournament.
    int numCandidates = (int)Math.pow(2, k-1);
    // Indices randomly picked up in [0, population.length)
    int[] idxs = ArrayUtils.genRandomIntegers(0, population.size());

    // DEBUG:
    // System.out.print("idxs= ");
    // for (i = 0; i < numCandidates; i++)
      // System.out.print(idxs[i] + " ");
    // System.out.print("\n");

    // Tournament candidates array where the selected paths are saved.
    Path[] candidates = new Path[numCandidates];
    for (i = 0; i < numCandidates; i++)
      candidates[i] = population.get(idxs[i]);

    return tournament(candidates, t)[0];
  }

  /**
   * Pick up the tournament round's final winner.
   * @param candidates
   * @param t
   * @return Path[]
   */
  private static Path[] tournament(Path[] candidates, double t) {
    Random rnd = new Random();
    if (candidates.length == 1) return candidates;  // Break condition

    // System.out.println("*** NEW ROUND ***");
    Path[] nextRoundCandidates = new Path[candidates.length/2];
    for (int i = 0; i < candidates.length/2; i++) {
      Path x1 = candidates[2*i];
      Path x2 = candidates[2*i+1];
      // Ensure x1's fitness is always better(smaller) than x2's
      if (x1.getFitness() > x2.getFitness()) {
        Path tmp = x1; x1 = x2; x2 = tmp; // Swap
      }
      // System.out.println("x1.fitness=" + x1.getFitness() + ", x2.fitness=" + x2.getFitness());

      double r = rnd.nextDouble();
      // System.out.println("r=" + r);
      if (t > r)
        nextRoundCandidates[i] = x1;
      else
        nextRoundCandidates[i] = x2;
    }

    // System.out.println("***nextRoundCandidates= ");
    // for (int i = 0; i < nextRoundCandidates.length; i++) {
      // System.out.println(nextRoundCandidates[i]);
    // }
    return tournament(nextRoundCandidates, t); // Run current method recursively
  }


}
