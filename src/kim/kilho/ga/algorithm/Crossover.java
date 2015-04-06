package kim.kilho.ga.algorithm;

import kim.kilho.ga.exception.CrossoverException;
import kim.kilho.ga.gene.Path;
import kim.kilho.ga.util.ArrayUtils;

import java.util.Arrays;
import java.util.Random;

/**
 * Crossover algorithms for GA.
 * @author Kilho Kim
 */
public class Crossover {

  /**
   * Multi-point Crossover,
   * which is a generalized version of One-point Crossover.
   * @param p1
   * @param p2
   * @return Path
   */
  public static Path multiPointCrossover(Path p1, Path p2, int numCutPoints) {
    if (numCutPoints > p1.getLength()-1)
      throw new CrossoverException("Invalid number of cut points.");

    // Generate a new offspring with empty path.
    Path offspring = new Path(p1.getLength(), false);
    int[] newPath = new int[p1.getLength()];
    int i, j = 0;

    int[] cutPointIdxCandidates = ArrayUtils.genRandomIntegers(0, p1.getLength()-1);
    int[] cutPointIdxs = new int[numCutPoints];
    for (i = 0; i < numCutPoints; i++)
      cutPointIdxs[i] = cutPointIdxCandidates[i];
    // Sort the cutPointIdxs array in ascending order.
    Arrays.sort(cutPointIdxs);

    boolean currOnP1 = true;  // Get points from p1 at the starting point.
    for (i = 0; i < p1.getLength(); i++) {
      if (currOnP1)
        newPath[i] = p1.getPath()[i];
      else
        newPath[i] = p2.getPath()[i];
      // If the current point is one of the cutting point indices in cutPointIdxs,
      // switch on/off the currOnP1 value and increase j.
      if (j < cutPointIdxs.length && i == cutPointIdxs[j]) {
        currOnP1 = !currOnP1; j++;
      }
    }

    offspring.setPath(newPath);

    return offspring;
  }

  /**
   * Uniform Crossover.
   * @param p1
   * @param p2
   * @return Path
   */
  public static Path uniformCrossover(Path p1, Path p2) {
    return null;
  }

  /**
   * Cycle Crossover.
   * @param p1
   * @param p2
   * @return Path
   */
  public static Path cycleCrossover(Path p1, Path p2) {
    return null;
  }

  /**
   * Order Crossover.
   * @param p1
   * @param p2
   * @return Path
   */
  public static Path orderCrossover(Path p1, Path p2) {
    return null;
  }

  /**
   * Partially Matched Crossover(PMX).
   * @param p1
   * @param p2
   * @return Path
   */
  public static Path partiallyMatchedCrossover(Path p1, Path p2) {
    return null;
  }

  /**
   * Arithmetic Crossover.
   * @param p1
   * @param p2
   * @return Path
   */
  public static Path arithmeticCrossover(Path p1, Path p2) {
    return null;
  }

  /**
   * Heuristic Crossover.
   * @param p1
   * @param p2
   * @return Path
   */
  public static Path heuristicCrossover(Path p1, Path p2) {
    return null;
  }

  /**
   * Edge Recombination.
   * @param p1
   * @param p2
   * @return Path
   */
  public static Path edgeRecombination(Path p1, Path p2) {
    return null;
  }
}
