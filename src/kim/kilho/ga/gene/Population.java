package kim.kilho.ga.gene;

/**
 * Population interface.  ''DEPRECATED''
 * @author Kilho Kim
 */
@Deprecated
public interface Population {

  /**
   * Get the size of the population.
   * @return int
   */
  int size();

  /**
   * Get the ith chromosome in the population.
   * @param i
   * @return Chromosome
   */
  Chromosome get(int i);

  /**
   * Get the best (found) solution in the population.
   * @return Chromosome
   */
  Chromosome getBest();

}
