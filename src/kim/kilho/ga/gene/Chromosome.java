package kim.kilho.ga.gene;

/**
 * Chromosome interface. ''DEPRECATED''
 * @author Kilho Kim
 */
public interface Chromosome {

  // Get the length of chromosome.
  int getLength();

  // Get the idx'th gene in chromosome.
  Gene get(int idx);

  // Set the idx'th gene in chromosome. If it already exists, replace it.
  void set(Gene gene, int idx);

  // Replace the original chromosome with the new chromosome made of n offspring.
  void replace(Chromosome offspring, int n);

}
