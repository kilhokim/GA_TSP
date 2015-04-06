package kim.kilho.ga.util;

import java.util.Random;

/**
 * Additional utilities for array operation.
 * @author Kilho Kim
 */
public final class ArrayUtils {

  /**
   * Generate the array with random integer
   * from the interval [start, end)
   * @param start
   * @param end
   * @return int[]
   */
  public static int[] genRandomIntegers(int start, int end) {
    int[] output = new int[end-start];
    int i;
    Random rnd = new Random();
    for (i = start; i < end; i++) {
      output[i] = i;
    }
    for (i = start; i < end; i++) {
      int idx = i + rnd.nextInt(end-i);
      int tmp = output[idx]; output[idx] = output[i]; output[i] = tmp;  // swap
    }

    return output;
  }
}
