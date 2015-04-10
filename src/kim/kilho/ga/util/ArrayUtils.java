package kim.kilho.ga.util;

import kim.kilho.ga.exception.InvalidParamException;

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
    for (i = start; i < end; i++) {
      output[i-start] = i;
    }
    for (i = start; i < end; i++) {
      int idx = i + rnd.nextInt(end-i);
      int tmp = output[idx-start]; output[idx-start] = output[i-start]; output[i-start] = tmp;  // swap
    }

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
    if (start > end) throw new InvalidParamException("Invalid parameter: start, end");
    if (num > end-start) throw new InvalidParamException("Invalid parameter: num");

    int[] randomInts = new int[end-start];
    int[] output = new int[num];
    int i;
    Random rnd = new Random();
    for (i = start; i < end; i++) {
      randomInts[i-start] = i;
    }
    for (i = start; i < end; i++) {
      int idx = i + rnd.nextInt(end-i);
      int tmp = randomInts[idx-start]; randomInts[idx-start] = randomInts[i-start]; randomInts[i-start] = tmp;  // swap
    }

    for (i = 0; i < num; i++)
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
