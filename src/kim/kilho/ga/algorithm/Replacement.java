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
   * Replace the random chromosome in the population.
   * @param population
   * @param p
   * @return Path[]
   */
  public static PathPopulation randomReplacement(PathPopulation population, Path p) {
    Random rnd = new Random();
    int idx = rnd.nextInt(population.size());
    population.set(idx, p);
    return population;
  }

  /**
   * Replacing the worst chromosome in the population.
   * @param population
   * @param p
   * @return Path[]
   */
  public static PathPopulation worstCaseReplacement(PathPopulation population, Path p) {
    int worstCaseIdx = 0;
    double maxFitness = 0;
    for (int i = 0; i < population.size(); i++) {
      if (population.get(i).getFitness() > maxFitness) {
        maxFitness = population.get(i).getFitness();
        worstCaseIdx = i;
      }
    }
    population.set(worstCaseIdx, p);
    return population;
  }

  // TODO:
  // public static Path[] worstParentReplacement(Path[] population, )


}
