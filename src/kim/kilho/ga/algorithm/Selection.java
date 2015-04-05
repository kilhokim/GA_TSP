package kim.kilho.ga.algorithm;

import kim.kilho.ga.exception.SelectionException;
import kim.kilho.ga.gene.Path;

import java.util.Random;

/**
 * Selection algorithms for GA.
 * @author Kilho Kim
 */
public class Selection {

  // Roulette Wheel Selection.
  public static Path RouletteWheelSelection(Path[] population) {
    double sumOfFitness = 0, sum = 0;
    Random rnd = new Random();
    for (int i = 0; i < population.length; i++)
      sumOfFitness += population[i].getFitness();
    double point = rnd.nextDouble() * sumOfFitness;

    for (int i = 0; i < population.length; i++) {
      sum += population[i].getFitness();
      if (point < sum) return population[i];
    }

    throw new SelectionException("Error! This shouldn't be reached!");
  }

  // Tournament Selection - with parameter t in (0.5, 1)
  public static Path TournamentSelection(Path[] population, double t) {
    Random rnd = new Random();
    int idx1 = rnd.nextInt(population.length);
    int idx2 = rnd.nextInt(population.length);
    // To ensure idx1 and idx2 are unique,
    // randomly & repeatedly select idx2 different to idx1.
    while (idx1 == idx2)
      idx2 = rnd.nextInt(population.length);

    Path x1 = population[idx1];
    Path x2 = population[idx2];
    // Ensure x1's fitness is always better(smaller) than x2's
    if (x1.getFitness() > x2.getFitness()) {
      Path tmp = x1; x1 = x2; x2 = tmp; // Swap
    }

    double r = rnd.nextDouble();
    if (t > r)
      return x1;
    else
      return x2;

    // TODO: Implement the normal version of Tournament Selection - with 2^k chromosomes
  }

  // TODO: Rank-based Selection
  // TODO: Sharing

}
