package kim.kilho.ga.algorithm;

import kim.kilho.ga.gene.Path;
import kim.kilho.ga.gene.PathPopulation;

import java.util.Random;

/**
 * Replacement algorithms for GA.
 * @reference
 *
 * @author Kilho Kim
 */
public class Replacement {

  /**
   * Replace the random chromosome with the path in the population.
   * @param population
   * @param p
   * @return PathPopulation
   */
  public static PathPopulation randomReplacement(PathPopulation population, Path p) {
    Random rnd = new Random();
    int idx = rnd.nextInt(population.size());
    population.set(idx, p);
    return population;
  }

  /**
   * Replacing the worst chromosome with the path in the population.
   * @param population
   * @param p
   * @return PathPopulation
   */
  public static PathPopulation worstCaseReplacement(PathPopulation population, Path p) {
    int worstCaseIdx = 0;
    double maxDistance = 0;
    for (int i = 0; i < population.size(); i++) {
      // The bigger the distance is, the worse the path is.
      if (population.get(i).getDistance() > maxDistance) {
        maxDistance = population.get(i).getDistance();
        worstCaseIdx = i;
      }
    }
    System.out.println("worstCaseIdx=" + worstCaseIdx + ", maxDistance=" + maxDistance);
    population.set(worstCaseIdx, p);
    return population;
  }

  /**
   * Replacing the worse parent with the path in the population.
   * @param population
   * @param p
   * @param p1
   * @param p2
   * @return PathPopulation
   */
  public static PathPopulation worstParentReplacement(PathPopulation population,
                                                      Path p, Path p1, Path p2) {
    int worstParentIdx = 0;
    // The bigger the distance is, the worse the path is.
    worstParentIdx = p1.getDistance() > p2.getDistance()
                     ? p1.getIdxInPopulation() : p2.getIdxInPopulation();
    System.out.println("worstParentIdx=" + worstParentIdx  + ", maxDistance="
            + (p1.getDistance() > p2.getDistance() ? p1.getDistance() : p2.getDistance()));
    population.set(worstParentIdx, p);
    return population;
  }

  public static PathPopulation worstParentCaseReplacement(PathPopulation population,
                                                          Path p, Path p1, Path p2) {
    // The bigger the distance is, the worse the path is.
    // If two parents' are better than the current path, do worstCaseReplacement:
    if (p.getDistance() > p1.getDistance() && p.getDistance() > p2.getDistance()) {
      return worstCaseReplacement(population, p);
    // If not, do worstParentReplacement:
    } else {
      return worstParentReplacement(population, p, p1, p2);
    }
  }


}
