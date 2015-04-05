package kim.kilho.ga.test;

import kim.kilho.ga.algorithm.Selection;
import kim.kilho.ga.exception.InvalidInputException;
import kim.kilho.ga.gene.Path;
import kim.kilho.ga.gene.Point;
import kim.kilho.ga.io.file.FileManager;
import kim.kilho.ga.util.PointUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * Test class, only for test-purpose
 */
public class Test {

  public static final int MAXN = 318; // Maximum value of N
  public static final int PSIZE = 100;  // Size of the population
  // Population of solutions.
  static Path[] population = new Path[PSIZE];
  // Best (found) solution.
  static Path record = new Path(MAXN);

  // Total array of points.
  static Point[] points = null;

  // Time limit for the test case
  static double timeLimit;


  // How to run:
  // $ java Test data/cycle.in
  public static void main(String[] args) {

    init(args);
    GA();

    // System.out.println(System.getProperty("user.dir"));
  }

  // Read the test case from file.
  private static void init(String[] args) {
    FileManager fm = new FileManager();
    try {
      Object[] input = fm.read(args[0], MAXN);
      points = (Point[])input[0];
      timeLimit = (Double)input[1];
      // System.out.println("Total number of points=" + points.length);
      for (int i = 0; i < points.length; i++) {
        System.out.println(points[i].toString());
      }
      // System.out.println("Total time limit=" + timeLimit);
      // System.out.println(PointUtils.distance(points[0], points[1]));
      // System.out.println(PointUtils.distance(points[1], points[2]));
    } catch (Exception e) {
      e.getMessage();
    }
  }

  /**
   * A 'steady-state' GA
   */
  private static void GA() {
    long begin = System.nanoTime();

    for (int i = 0; i < population.length; i++) {
      population[i] = new Path(MAXN);
      System.out.println("------------ Path #" + i + " -----------------");
      System.out.println(population[i].toString());
    }

    try {
      while (true) {
        if (System.nanoTime() - begin >= timeLimit - 1) break;    // end condition
        // for i = 1 to k
        // Select two paths p1 and p2 from population
          Path p1 = selection(1);
          Path p2 = selection(1);
          Path offspring = crossover(p1, p2);
          offspring = mutation(offspring);
          Path[] offsprings = null; // TODO: add offsprings into this path array
      }
    } catch (Exception e) {
      e.getMessage();
    }
  }



  /**
   * Choose one path from the population.
   * Currently this operator randomly chooses one with uniform distribution.
   * @return Path
   */
  private static Path selection(int option) throws Exception {
    switch(option) {
      // 1. Roulette Wheel Selection.
      case 1:
        return Selection.RouletteWheelSelection(population);
      case 2:
        return Selection.TournamentSelection(population, 0.9);
      default:
        throw new Exception("Invalid option input");


    }



    /*
    Random rnd = new Random();
    return population[rnd.nextInt(population.length)];
    */
  }

  private static Path crossover(Path p1, Path p2) {
    // TODO:
    return null;
  }

  private static Path mutation(Path p) {
    // TODO:
    return null;
  }

  /**
   * Replace one solution from the population with the new offspring.
   * Currently any random solution can be replaced.
   */
  private static void replacement(Path p) {
    // FIXME:
    Random rnd = new Random();
    int idx = rnd.nextInt(population.length);
    population[idx].setFitness(p.getFitness());
    for (int i = 0; i < MAXN; i++)
      population[idx].setPath(p.getPath());
  }

}
