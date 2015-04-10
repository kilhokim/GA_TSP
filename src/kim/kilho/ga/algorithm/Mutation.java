package kim.kilho.ga.algorithm;

import kim.kilho.ga.gene.Path;
import kim.kilho.ga.util.ArrayUtils;

import java.util.Arrays;
import java.util.Random;

/**
 * Mutation algorithms for GA.
 * @reference Larranaga et al., Genetic Algorithms for The Travelling Salesman Problem:
 *                              A Review of Representations and Operators
 * @author Kilho Kim
 */
public class Mutation {

  /**
   * Displacement Mutation.
   * @param p
   * @return Path
   */
  public static Path displacementMutation(Path p) {
    // Generate a new offspring with empty path.
    Path offspring = new Path(p.getLength(), false); // idxInPopulation has not assigned yet.
    int[] newPath = new int[p.getLength()];

    Random rnd = new Random();

    // Randomly pick two indices as starting index and ending index of subpath
    // System.out.println("p.getLength()=" + p.getLength());
    int[] subpathIdxs = ArrayUtils.genRandomIntegers(0, p.getLength(), 2);
    // System.out.println("subpathIdxs=" + Arrays.toString(subpathIdxs));
    Arrays.sort(subpathIdxs);
    // System.out.println("subpathIdxs=" + Arrays.toString(subpathIdxs));
    // Randomly pick a index as inserting point index of subpath
    int insertIdx = rnd.nextInt(p.getLength());

    // If the length of subpath equals the original length of parent
    if (subpathIdxs[1] - subpathIdxs[0] + 1 == p.getLength()) {
      insertIdx = 0;
      // System.out.println("insertIdx=" + insertIdx);
      newPath = p.getPath();
    } else {
      // Ensure insertIdx is not in between the starting index and ending index
      while (insertIdx >= subpathIdxs[0] && insertIdx <= subpathIdxs[1])
        insertIdx = rnd.nextInt(p.getLength());
      // System.out.println("insertIdx=" + insertIdx);

      // Insert the subpath right after the inserting point
      int i, j = 0, k = subpathIdxs[0];
      for (i = 0; i < newPath.length; i++) {
        if (i >= subpathIdxs[0] && i <= subpathIdxs[1]) continue;
        newPath[j++] = p.getPoint(i);
        if (i == insertIdx)
          while (k <= subpathIdxs[1])
            newPath[j++] = p.getPoint(k++);
      }
    }


    offspring.setPath(newPath);
    return offspring;
  }

  /**
   * Exchange Mutation.
   * @param p
   * @return Path
   */
  public static Path exchangeMutation(Path p) {
    // Generate a new offspring with empty path.
    Path offspring = new Path(p.getLength(), false); // idxInPopulation has not assigned yet.
    int[] newPath = new int[p.getLength()];

    // Randomly pick two indices
    int[] exchangeIdxs = ArrayUtils.genRandomIntegers(0, p.getLength(), 2);
    // System.out.println("exchangeIdxs=" + Arrays.toString(exchangeIdxs));

    for (int i = 0; i < newPath.length; i++) {
      if (i == exchangeIdxs[0])
        newPath[i] = p.getPoint(exchangeIdxs[1]);
      else if (i == exchangeIdxs[1])
        newPath[i] = p.getPoint(exchangeIdxs[0]);
      else
        newPath[i] = p.getPoint(i);
      }

    offspring.setPath(newPath);
    return offspring;
  }

  /**
   * Insertion Mutation.
   * It's actually a special case of Displacement Mutation.
   * @param p
   * @return Path
   */
  public static Path insertionMutation(Path p) {
    // Generate a new offspring with empty path.
    Path offspring = new Path(p.getLength(), false); // idxInPopulation has not assigned yet.
    int[] newPath = new int[p.getLength()];
    Random rnd = new Random();

    // Randomly pick two indices as removing index and inserting index of point
    int[] randomIdxs = ArrayUtils.genRandomIntegers(0, p.getLength(), 2);
    int removingIdx = randomIdxs[0];
    int insertingIdx = randomIdxs[1];
    // System.out.println("removingIdx=" + removingIdx + ", insertingIdx=" + insertingIdx);

    int i, j = 0;
    for (i = 0; i < newPath.length; i++) {
      if (i == removingIdx) continue;
      newPath[j++] = p.getPoint(i);
      if (i == insertingIdx)
        newPath[j++] = p.getPoint(removingIdx);
    }

    offspring.setPath(newPath);
    return offspring;
  }

  /**
   * Simple Inversion Mutation.
   * @param p
   * @return Path
   */
  public static Path simpleInversionMutation(Path p) {
    // Generate a new offspring with empty path.
    Path offspring = new Path(p.getLength(), false); // idxInPopulation has not assigned yet.
    int[] newPath = new int[p.getLength()];

    // Randomly pick two cut points
    int[] cutPointIdxs = ArrayUtils.genRandomIntegers(0, p.getLength(), 2);
    Arrays.sort(cutPointIdxs);
    System.out.println("cutPointIdxs=" + Arrays.toString(cutPointIdxs));

    int i, k = cutPointIdxs[1];
    for (i = 0; i < newPath.length; i++) {
      if (i == cutPointIdxs[0]) {
        while (k >= cutPointIdxs[0]) {
          // System.out.println("k=" + k);
          newPath[i++] = p.getPoint(k--);
        }
        i--;
        // newPath[i] = p.getPoint(i);
      } else
        newPath[i] = p.getPoint(i);
    }

    offspring.setPath(newPath);
    return offspring;
  }

  /**
   * Inversion Mutation.
   * @param p
   * @return Path
   */
  public static Path inversionMutation(Path p) {
    // Generate a new offspring with empty path.
    Path offspring = new Path(p.getLength(), false); // idxInPopulation has not assigned yet.
    int[] newPath = new int[p.getLength()];

    Random rnd = new Random();

    // Randomly pick two cut points' indices
    int[] cutPointIdxs = ArrayUtils.genRandomIntegers(0, p.getLength(), 2);
    Arrays.sort(cutPointIdxs);
    // Randomly pick a index as inserting point index
    int insertIdx = rnd.nextInt(p.getLength());
    System.out.println("cutPointIdxs=" + Arrays.toString(cutPointIdxs));

    // If the length of cut part equals the original length of parent
    if (cutPointIdxs[1] - cutPointIdxs[0] + 1 == p.getLength()) {
      insertIdx = 0;
      System.out.println("insertIdx=" + insertIdx);
      newPath = p.getPath();
    } else {
      // Ensure insertIdx is not in between the cutting indices
      while (insertIdx >= cutPointIdxs[0] && insertIdx <= cutPointIdxs[1])
        insertIdx = rnd.nextInt(p.getLength());
      System.out.println("insertIdx=" + insertIdx);

      // Insert the inversed subpath right after the inserting point
      int i, j = 0, k = cutPointIdxs[1];
      for (i = 0; i < newPath.length; i++) {
        if (i >= cutPointIdxs[0] && i <= cutPointIdxs[1]) continue;
        newPath[j++] = p.getPoint(i);
        if (i == insertIdx)
          while (k >= cutPointIdxs[0])
            newPath[j++] = p.getPoint(k--);
      }
    }

    offspring.setPath(newPath);
    return offspring;
  }

  /**
   * Scramble Mutation.
   * @param p
   * @return Path
   */
  public static Path scrambleMutation(Path p) {
    // Generate a new offspring with empty path.
    Path offspring = new Path(p.getLength(), false); // idxInPopulation has not assigned yet.
    int[] newPath = new int[p.getLength()];

    // Randomly pick two cut points
    int[] cutPointIdxs = ArrayUtils.genRandomIntegers(0, p.getLength(), 2);
    Arrays.sort(cutPointIdxs);
    System.out.println("cutPointIdxs=[" + cutPointIdxs[0] + "," + cutPointIdxs[1] + "]");

    int[] subpathIdxs = ArrayUtils.genRandomIntegers(cutPointIdxs[0], cutPointIdxs[1]);
    int i, k = 0;
    for (i = 0; i < newPath.length; i++) {
      if (i == cutPointIdxs[0]) {
        while (k < subpathIdxs.length)
          newPath[i++] = p.getPoint(subpathIdxs[k++]);
        newPath[i] = p.getPoint(i);
      } else
        newPath[i] = p.getPoint(i);
    }

    offspring.setPath(newPath);
    return offspring;
  }

}
