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
    Path offspring = new Path(p1.getLength(), false); // idxInPopulation has not assigned yet.
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
    // Generate a new offspring with empty path.
    Path offspring = new Path(p1.getLength(), false); // idxInPopulation has not assigned yet.
    int[] newPath = new int[p1.getLength()];

    // Randomly pick two cut points
    int[] cutPointIdxs = ArrayUtils.genRandomIntegers(0, p1.getLength(), 2);
    Arrays.sort(cutPointIdxs);
    System.out.println("cutPointIdxs=[" + cutPointIdxs[0] + "," + cutPointIdxs[1] + "]");

    int i, j = 0;
    // Temporarily store the cut part.
    int[] tmp = new int[cutPointIdxs[1]-cutPointIdxs[0]+1];
    for (i = cutPointIdxs[0]; i <= cutPointIdxs[1]; i++)
      tmp[i-cutPointIdxs[0]] = p1.getPath()[i];

    // Put points after the right limit of the cut part.
    for (i = cutPointIdxs[1]+1; i < p1.getLength(); i++)
      // Put a point only if cut part doesn't contain it.
      if (ArrayUtils.indexOf(tmp, p2.getPath()[i]) == -1)
        newPath[j++] = p2.getPath()[i];

    // Put points in the cut part in newPath.
    for (i = cutPointIdxs[0]; i <= cutPointIdxs[1]; i++)
      newPath[j++] = tmp[i-cutPointIdxs[0]];

    // Put points before the right limit of the cut part
    for (i = 0; i <= cutPointIdxs[1]; i++)
      // Put a point only if cut part doesn't contain it.
      if (ArrayUtils.indexOf(tmp, p2.getPath()[i]) == -1)
        newPath[j++] = p2.getPath()[i];

    offspring.setPath(newPath);

    return offspring;
  }

  /**
   * Partially Matched Crossover(PMX).
   * @param p1
   * @param p2
   * @return Path
   */
  public static Path partiallyMatchedCrossover(Path p1, Path p2) {
    // Generate a new offspring with empty path.
    Path offspring = new Path(p1.getLength(), false); // idxInPopulation has not assigned yet.
    int[] newPath = new int[p1.getLength()];

    // Randomly pick two cut points
    int[] cutPointIdxs = ArrayUtils.genRandomIntegers(0, p1.getLength(), 2);
    Arrays.sort(cutPointIdxs);
    System.out.println("cutPointIdxs=[" + cutPointIdxs[0] + "," + cutPointIdxs[1] + "]");

    int i, k = 0;
    // Temporarily store the cut part.
    int[] tmp = new int[cutPointIdxs[1]-cutPointIdxs[0]+1];
    for (i = cutPointIdxs[0]; i <= cutPointIdxs[1]; i++)
      tmp[i-cutPointIdxs[0]] = p1.getPath()[i];

    // PMX process...
    for (i = 0; i < newPath.length; i++) {
      if (i >= cutPointIdxs[0] && i <= cutPointIdxs[1])
        newPath[i] = tmp[i-cutPointIdxs[0]];
      else {
        k = i;
        while (ArrayUtils.indexOf(tmp, p2.getPath()[k]) != -1)
          k = ArrayUtils.indexOf(tmp, p2.getPath()[k]) + cutPointIdxs[0];
        newPath[i] = p2.getPath()[k];
      }
    }

    offspring.setPath(newPath);

    return offspring;
  }
}
