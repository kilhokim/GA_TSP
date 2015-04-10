package kim.kilho.ga.util;

import kim.kilho.ga.exception.InvalidParamException;

import java.util.Arrays;
import java.util.Collections;
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
    if (start > end) throw new InvalidParamException("Invalid parameter: start, end");
    int[] output = new int[end-start];
    int i;
    Random rnd = new Random();
    for (i = 0; i < output.length; i++) {
      output[i] = start + i;
    }
    for (i = 0; i < output.length; i++) {
      int idx = i + rnd.nextInt(end-i);
      int tmp = output[idx]; output[idx] = output[i]; output[i] = tmp;  // swap
    }
    // TODO: Consider changing above to:
    // Collections.shuffle(Arrays.asList(output));
    // System.out.println(Arrays.toString(output));

    return output;
  }

  /**
   * Generate the array with random integer
   * from the interval [start, end) - with length num
   * @param start
   * @param end
   * @return int[]
   */
  public static int[] genRandomIntegers(int start, int end, int num)
          throws InvalidParamException {
    if (num > end-start) throw new InvalidParamException("Invalid parameter: num");

    int[] randomInts = genRandomIntegers(start, end);
    int[] output = new int[num];

    for (int i = 0; i < num; i++)
      output[i] = randomInts[i];

    return output;
  }

  /**
   * A simple method for finding an index of a certain value in array.
   * FIXME: Replace this method to Arrays.asList(int[]).indexOf(int)
   *        if performance problem occurs.
   * @param array
   * @param value
   * @return
   */
  public static int indexOf(int[] array, int value) {
    for (int i = 0; i < array.length; i++) {
      if (array[i] == value)
        return i;
    }

    return -1;
  }
}
