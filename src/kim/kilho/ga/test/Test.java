package kim.kilho.ga.test;

import kim.kilho.ga.util.ArrayUtils;

/**
 * Test class, only for test-purpose
 */
public class Test {

  public static void main(String[] args) {
    int[] array = ArrayUtils.genRandomIntegers(0, 100);
    for (int i = 0; i < array.length; i++) {
      System.out.println(array[i]);
    }

  }

}
