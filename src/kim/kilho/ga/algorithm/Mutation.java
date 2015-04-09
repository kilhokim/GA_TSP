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
    Path offspring = new Path(p.getLength(), false);
    int[] newPath = new int[p.getLength()];

    Random rnd = new Random();

    // Randomly pick two indices as starting index and ending index of subpath
    int[] subpathIdxs = ArrayUtils.genRandomIntegers(0, p.getLength(), 2);
    Arrays.sort(subpathIdxs);
    System.out.println("subpath_start=" + subpathIdxs[0] + ", subpath_end=" + subpathIdxs[1]);
    // Randomly pick a index as inserting point index of subpath
    int insertIdx = rnd.nextInt(p.getLength());
    // Ensure insertIdx is not in between the starting index and ending index
    while (insertIdx >= subpathIdxs[0] && insertIdx <= subpathIdxs[1])
      insertIdx = rnd.nextInt(p.getLength());
    System.out.println("insert_idx=" + insertIdx);

    // Inserting the subpath right after the inserting point
    int i, j = 0, k = subpathIdxs[0];
    for (i = 0; i < newPath.length; i++) {
      if (i >= subpathIdxs[0] && i <= subpathIdxs[1]) continue;
      newPath[j++] = p.getPath()[i];
      if (i == insertIdx)
        while (k <= subpathIdxs[1])
          newPath[j++] = p.getPath()[k++];
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
    Path offspring = new Path(p.getLength(), false);
    int[] newPath = new int[p.getLength()];

    // Randomly pick two indices
    int[] exchangeIdxs = ArrayUtils.genRandomIntegers(0, p.getLength(), 2);

    for (int i = 0; i < newPath.length; i++) {
      if (i == exchangeIdxs[0])
        newPath[i] = p.getPath()[exchangeIdxs[1]];
      else if (i == exchangeIdxs[1])
        newPath[i] = p.getPath()[exchangeIdxs[0]];
      else
        newPath[i] = p.getPath()[i];
      }

    offspring.setPath(newPath);
    return offspring;
  }

  public static Path insertionMutation(Path p) {
    return null;
  }

  public static Path simpleInversionMutation(Path p) {
    return null;
  }

  public static Path inversionMutation(Path p) {
    return null;
  }

  public static Path scrambleMutation(Path p) {
    return null;
  }

}
