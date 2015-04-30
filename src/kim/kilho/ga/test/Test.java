package kim.kilho.ga.test;

import kim.kilho.ga.algorithm.Crossover;
import kim.kilho.ga.algorithm.LocalSearch;
import kim.kilho.ga.algorithm.Mutation;
import kim.kilho.ga.gene.Path;
import kim.kilho.ga.util.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;

/**
 * Test class, only for test-purpose
 */
public class Test {

  public static void main(String[] args) {


    // crossoverTest();
    // mutationTest();
    localSearchTest();
  }

  public static void localSearchTest() {
    Path p = new Path(8, true);
    int[] arr1 = {0,1,2,3,4,5,6,7};
    p.setPath(arr1);
    System.out.println("p: " + p.toString());

    Path newP = LocalSearch.twoChange(p, 3, 6);
    System.out.println("newP: " + newP.toString());
  }

  public static void crossoverTest() {
    Path p1 = new Path(10, true);
    Path p2 = new Path(10, true);

//    int[] arr1 = {0,1,2,3,4,5,6,8,7,9};
//    int[] arr2 = {2,5,0,9,7,3,8,6,1,4};
//    p1.setPath(arr1);
//    p2.setPath(arr2);
    System.out.println("p1: " + p1.toString());
    System.out.println("p2: " + p2.toString());

    Path offspring = Crossover.edgeRecombination(p1, p2);
    System.out.println("offspring: " + offspring.toString());
  }

  public static void mutationTest() {
    Path p1 = new Path(10, true);
    System.out.println("p1: " + p1.toString());
    Path offspring = Mutation.scrambleMutation(p1);
    System.out.println("offspring: " + offspring.toString());
  }
}
