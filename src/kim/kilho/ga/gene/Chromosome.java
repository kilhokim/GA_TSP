package kim.kilho.ga.gene;

/**
 * Chromosome interface. ''DEPRECATED''
 * @author Kilho Kim
 */
public interface Chromosome {

  // Get the length of the chromosome.
  int getLength();

  // Set the fitness value.
  void setFitness(double f);

  // Get the fitness value.
  double getFitness();

    // Get the idx'th gene index in the chromosome.
  int get(int idx);

  // Set the idx'th gene in the chromosome. If it already exists, replace it.
  void set(int g, int idx);

  // Calculate the fitness of the chromosome.
  double evaluate(Gene[] genes);

}
