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
   * Cycle Crossover.
   * @param p1
   * @param p2
   * @return Path
   */
  public static Path cycleCrossover(Path p1, Path p2) {
    // Generate a new offspring with empty path.
    Path offspring = new Path(p1.getLength(), false);
    int[] newPath = new int[p1.getLength()];
    // Indices array which stores occupied indices in newPath.
    int[] occupiedIdxs = new int[p1.getLength()];
    int i = 0, count = 0, initIdx = 0;

    boolean currOnP1 = true;  // Get points from p1 at the starting point.
    while (count < p1.getLength()) {
      occupiedIdxs[count++] = i;
      if (currOnP1) {
        newPath[i] = p1.getPath()[i];
        i = ArrayUtils.indexOf(p1.getPath(), p2.getPath()[i]);
      } else {
        newPath[i] = p2.getPath()[i];
        i = ArrayUtils.indexOf(p2.getPath(), p1.getPath()[i]);
      }
      // If the indicator i comes back to the initial index,
      // switch on/off the currOnP1 value and reset the initIdx.
      if (i == initIdx) {
        currOnP1 = !currOnP1;
        initIdx = 0;
        // Set the initIdx to the minimum index
        // among indices which is not stored in occupiedIdxs.
        while (ArrayUtils.indexOf(occupiedIdxs, initIdx) >= 0) {
          initIdx += 1;
        }

        i = initIdx;
      }
    }
    offspring.setPath(newPath);

    return offspring;
  }

  /**
   * Order Crossover.
   * @param p1
   * @param p2
   * @return Path
   */
  public static Path orderCrossover(Path p1, Path p2) {
    // TODO
    return null;
  }

  /**
   * Partially Matched Crossover(PMX).
   * @param p1
   * @param p2
   * @return Path
   */
  public static Path partiallyMatchedCrossover(Path p1, Path p2) {
    // TODO
    return null;
  }

  /**
   * Edge Recombination.
   * @param p1
   * @param p2
   * @return Path
   */
  public static Path edgeRecombination(Path p1, Path p2) {
    // WARNING: Is this one kind of local optimum?
    return null;
  }
}
