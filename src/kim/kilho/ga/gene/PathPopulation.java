package kim.kilho.ga.gene;

/**
 * Created by kilho on 15. 4. 10.
 */
public class PathPopulation {

  private Path[] population;
  private int size; // the size of the population
  private Path record;  // the best (found) path in the population

  public PathPopulation(int size, int length) {
    population = new Path[size];
    size = size;
    for (int i = 0; i < size; i++) {
      population[i] = new Path(length, i, true);
      // System.out.println("------------ Path #" + i + " -----------------");
      // System.out.println(population[i].toString());
    }
    record = population[0];
  }

  /**
   * Get the size of the population.
   * @return int
   */
  public int size() {
    return size;
  }

  /**
   * Get the ith path in the population.
   * @param i
   * @return Path
   */
  public Path get(int i) {
    return population[i];
  }

  /**
   * Set the ith path.
   * @param i
   * @param path
   */
  public void set(int i, Path path) {
    path.setIdxInPopulation(i);
    population[i] = path;
  }

  /**
   * Get the best (found) path in the population.
   * @return Path
   */
  public Path getBest() {
    return record;
  }


}
