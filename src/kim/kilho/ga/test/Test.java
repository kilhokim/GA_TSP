package kim.kilho.ga.test;

import kim.kilho.ga.algorithm.Crossover;
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
    String inputFileName = "data/cycle.in.101";
    StringBuilder outputFileName = new StringBuilder();
    String[] inputFilePaths = inputFileName.split("/");
    String[] inputFileNames = inputFilePaths[inputFilePaths.length-1]
                                 .split("\\.");
    for (int i = 0; i < inputFilePaths.length-1; i++) {
      outputFileName.append(inputFilePaths[i]);
      outputFileName.append("/");
    }
    outputFileName.append(inputFileNames[0]);
    outputFileName.append(".out");
    if (inputFileNames.length > 2) {
      for (int i = 2; i < inputFileNames.length; i++) {
        outputFileName.append(".");
        outputFileName.append(inputFileNames[i]);
      }
    }
    System.out.println(outputFileName.toString());
//    System.out.println(Arrays.toString(inputFileNames));


    // Collections.shuffle(Arrays.asList(array));
    // int[] array = ArrayUtils.genRandomIntegers(0, 6);
    // System.out.println(Arrays.toString(array));



    // crossoverTest();
    // mutationTest();
  }

  public static void crossoverTest() {
    Path p1 = new Path(10, true);
    Path p2 = new Path(10, true);

    System.out.println("p1: " + p1.toString());
    System.out.println("p2: " + p2.toString());

    Path offspring = Crossover.partiallyMatchedCrossover(p1, p2);
    System.out.println("offspring: " + offspring.toString());
  }

  public static void mutationTest() {
    Path p1 = new Path(10, true);
    System.out.println("p1: " + p1.toString());
    Path offspring = Mutation.scrambleMutation(p1);
    System.out.println("offspring: " + offspring.toString());
  }


}
