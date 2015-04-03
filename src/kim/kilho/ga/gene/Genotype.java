package kim.kilho.ga.gene;

/**
 * Genotype interface. ''DEPRECATED''
 * @author Kilho Kim
 */
public interface Genotype {

  // Get the length of genotype.
  int getLength();

  // Get the idx'th gene in genotype.
  Gene get(int idx);

  // Set the idx'th gene in genotype. If it already exists, replace it.
  void set(Gene gene, int idx);

  // Replace the original genotype with the new genotype made of n offspring.
  void replace(Genotype offspring, int n);

}
