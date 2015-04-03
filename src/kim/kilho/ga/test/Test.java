package kim.kilho.ga.test;

import kim.kilho.ga.exception.InvalidInputException;
import kim.kilho.ga.gene.Path;
import kim.kilho.ga.io.file.FileManager;
import kim.kilho.ga.util.PointUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Test class, only for test-purpose
 */
public class Test {

  // How to run:
  // $ java Test data/cycle.in
  public static void main(String[] args) {
    /*
    String currLine = "45.0";
    System.out.println(Arrays.toString(currLine.split(" ")));
    */

    FileManager fm = new FileManager();
    try {
      Path path = fm.read(args[0], 318);
      System.out.println("Total length=" + path.getLength());
      for (int i = 0; i < path.getLength(); i++) {
        System.out.println(path.get(i).toString());
      }
      System.out.println("Total available time=" + path.getAvailableTime());
      System.out.println(PointUtils.distance(path.get(0), path.get(1)));
      System.out.println(PointUtils.distance(path.get(1), path.get(2)));
    } catch (Exception e) {
      e.getMessage();
    }
    // System.out.println(System.getProperty("user.dir"));
  }

}
