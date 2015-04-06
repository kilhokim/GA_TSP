package kim.kilho.ga.test;

import kim.kilho.ga.algorithm.Crossover;
import kim.kilho.ga.gene.Path;
import kim.kilho.ga.util.ArrayUtils;

import java.util.Arrays;

/**
 * Test class, only for test-purpose
 */
public class Test {

  public static void main(String[] args) {
    crossoverTest();
  }

  public static void crossoverTest() {
    Path p1 = new Path(10, true);
    Path p2 = new Path(10, true);
    System.out.println("p1: " + p1.toString());
    System.out.println("p2: " + p2.toString());

    Path offspring = Crossover.multiPointCrossover(p1, p2, 9);
    System.out.println("offspring: " + offspring.toString());

  }


}
