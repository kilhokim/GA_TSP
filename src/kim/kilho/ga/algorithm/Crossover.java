package kim.kilho.ga.algorithm;

import kim.kilho.ga.exception.CrossoverException;
import kim.kilho.ga.gene.Path;

import java.util.Random;

/**
 * Crossover algorithms for GA.
 * @author Kilho Kim
 */
public class Crossover {

  /**
   * One-point Crossover.
   * @param p1
   * @param p2
   * @return Path
   */
  public static Path onePointCrossover(Path p1, Path p2) {
    // Generate a new offspring with empty path.
    Path offspring = new Path(p1.getLength(), false);
    int[] newPath = new int[p1.getLength()];
    int i;
    Random rnd = new Random();
    // There can exist n-1 operators for length-n chromosomes(0, 1, ..., n-2)
    int cutPoint = rnd.nextInt(p1.getLength()-1);
    for (i = 0; i <= cutPoint; i++)
      newPath[i] = p1.getPath()[i];
    for (i = cutPoint+1; i < newPath.length; i++)
      newPath[i] = p2.getPath()[i];
    offspring.setPath(newPath);

    return offspring;
  }

  /**
   * Multi-point Crossover.
   * @param p1
   * @param p2
   * @return Path
   */
  public static Path multiPointCrossover(Path p1, Path p2, int k) {
    if (k > p1.getLength()-1)
      throw new CrossoverException("Invalid number of cut points.");

    // Generate a new offspring with empty path.
    Path offspring = new Path(p1.getLength(), false);
    int[] newPath = new int[p1.getLength()];
    int i;
    boolean currOnP1;
    Random rnd = new Random();




    return null;
  }

  /**
   * Uniform Crossover.
   * @param p1
   * @param p2
   * @return Path
   */
  public static Path uniformCrossover(Path p1, Path p2) {
    return null;
  }

  /**
   * Cycle Crossover.
   * @param p1
   * @param p2
   * @return Path
   */
  public static Path cycleCrossover(Path p1, Path p2) {
    return null;
  }

  /**
   * Order Crossover.
   * @param p1
   * @param p2
   * @return Path
   */
  public static Path orderCrossover(Path p1, Path p2) {
    return null;
  }

  /**
   * Partially Matched Crossover(PMX).
   * @param p1
   * @param p2
   * @return Path
   */
  public static Path partiallyMatchedCrossover(Path p1, Path p2) {
    return null;
  }

  /**
   * Arithmetic Crossover.
   * @param p1
   * @param p2
   * @return Path
   */
  public static Path arithmeticCrossover(Path p1, Path p2) {
    return null;
  }

  /**
   * Heuristic Crossover.
   * @param p1
   * @param p2
   * @return Path
   */
  public static Path heuristicCrossover(Path p1, Path p2) {
    return null;
  }

  /**
   * Edge Recombination.
   * @param p1
   * @param p2
   * @return Path
   */
  public static Path edgeRecombination(Path p1, Path p2) {
    return null;
  }
}
