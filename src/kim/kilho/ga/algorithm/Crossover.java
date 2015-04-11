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
   * Cycle Crossover(CX).
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
        newPath[i] = p1.getPoint(i);
        i = ArrayUtils.indexOf(p1.getPath(), p2.getPoint(i));
      } else {
        newPath[i] = p2.getPoint(i);
        i = ArrayUtils.indexOf(p2.getPath(), p1.getPoint(i));
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
   * Order Crossover(OX).
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
      tmp[i-cutPointIdxs[0]] = p1.getPoint(i);

    // Put points after the right limit of the cut part.
    if (cutPointIdxs[1] < p1.getLength()-1) { // the right cut point must not be the last point
      for (i = cutPointIdxs[1] + 1; i < p1.getLength(); i++)
        // Put a point only if cut part doesn't contain it.
        if (ArrayUtils.indexOf(tmp, p2.getPoint(i)) == -1)
          newPath[j++] = p2.getPoint(i);
    }

    // Put points in the cut part in newPath.
    for (i = cutPointIdxs[0]; i <= cutPointIdxs[1]; i++)
      newPath[j++] = tmp[i-cutPointIdxs[0]];

    // Put points before the right limit of the cut part
    for (i = 0; i <= cutPointIdxs[1]; i++)
      // Put a point only if cut part doesn't contain it.
      if (ArrayUtils.indexOf(tmp, p2.getPoint(i)) == -1)
        newPath[j++] = p2.getPoint(i);

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
      tmp[i-cutPointIdxs[0]] = p1.getPoint(i);

    // PMX process...
    for (i = 0; i < newPath.length; i++) {
      if (i >= cutPointIdxs[0] && i <= cutPointIdxs[1])
        newPath[i] = tmp[i-cutPointIdxs[0]];
      else {
        k = i;
        while (ArrayUtils.indexOf(tmp, p2.getPoint(k)) != -1)
          k = ArrayUtils.indexOf(tmp, p2.getPoint(k)) + cutPointIdxs[0];
        newPath[i] = p2.getPoint(k);
      }
    }

    offspring.setPath(newPath);

    return offspring;
  }

  public static Path edgeRecombination(Path p1, Path p2) {
    // Generate a new offspring with empty path.
    Path offspring = new Path(p1.getLength(), false); // idxInPopulation has not assigned yet.
    int[] newPath = new int[p1.getLength()];
    int i, j, k;
    for (i = 0; i < newPath.length; i++)
      newPath[i] = -1;

    // Edge table for recombination
    int[][] edgeTable = new int[p1.getLength()][4];
    // Actual length of the each row of edgeTable
    int[] edgeTableLength = new int[p1.getLength()];
    // Initialize every element in edge table as -1
    for (i = 0; i < edgeTable.length; i++) {
      for (j = 0; j < 4; j++) {
        edgeTable[i][j] = -1;
      }
    }

    Path[] ps = {p1, p2};
    Path currP;
    int currPoint, prevPoint, nextPoint;
    for (k = 0; k < ps.length; k++) {
      currP = ps[k];
      // Search parent path and update the edge table
      for (i = 0; i < currP.getLength(); i++) {
        currPoint = currP.getPoint(i);
        // Search previous point by branching if statement
        // in order to prevent ArrayIndexOutOfBoundsException
        if (i == 0)
          prevPoint = currP.getPoint(currP.getLength()-1);
        else
          prevPoint = currP.getPoint(i-1);
        // Search next point
        nextPoint = currP.getPoint((i+1)%currP.getLength());

        // Add prevPoint to the currPoint row in edgeTable if not exists
        if (ArrayUtils.indexOf(edgeTable[currPoint], prevPoint) == -1) {
          edgeTable[currPoint][edgeTableLength[currPoint]] = prevPoint;
          edgeTableLength[currPoint] += 1;
        }
        // Add nextPoint to the currPoint row in edgeTable if not exists
        if (ArrayUtils.indexOf(edgeTable[currPoint], nextPoint) == -1) {
          edgeTable[currPoint][edgeTableLength[currPoint]] = nextPoint;
          edgeTableLength[currPoint] += 1;
        }
      }
    }

    // DEBUG
    for (i = 0; i < edgeTable.length; i++) {
      System.out.println(i + ": " + Arrays.toString(edgeTable[i]));
    }

    i = 0;
    Random rnd = new Random();
    int minLength, currPointIdx, currInvestigatingPoint;
    int nextPointIdx = rnd.nextInt(edgeTable.length);
    newPath[i] = p1.getPoint(i);
    currPointIdx = newPath[i++];
    while (i < newPath.length) {
      // Set minLength to 5 (and must be lowered)
      minLength = 5;
      for (j = 0; j < edgeTableLength[currPointIdx]; j++) {
        currInvestigatingPoint = edgeTable[currPointIdx][j];
        // If newPath doesn't have currently investigating point yet,
        if (ArrayUtils.indexOf(newPath, currInvestigatingPoint) == -1) {
          if (edgeTableLength[currInvestigatingPoint] < minLength) {
            minLength = edgeTableLength[currInvestigatingPoint];
            nextPointIdx = currInvestigatingPoint;
          } else if (edgeTableLength[currInvestigatingPoint] == minLength)
            nextPointIdx = rnd.nextDouble() > 0.5 ? nextPointIdx : currInvestigatingPoint;
        }
      }
      // If it passed every points in the row
      // (minLength has not changed and is still 5)
      if (minLength == 5)
        for (k = 0; k < p1.getLength(); k++)
          // If k has not included in newPath yet, start again from it
          if (ArrayUtils.indexOf(newPath, k) == -1)
            nextPointIdx = k;
      newPath[i++] = nextPointIdx;
      currPointIdx = nextPointIdx;
    }

    offspring.setPath(newPath);

    return offspring;
  }
}
