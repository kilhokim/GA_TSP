package kim.kilho.ga.gene;

import kim.kilho.ga.exception.PathException;
import kim.kilho.ga.util.ArrayUtils;
import kim.kilho.ga.util.PointUtils;

import java.util.Random;

/**
 * A sequence of points as a solution for TSP
 * @author Kilho Kim
 */
// TODO: public class Path implements Chromosome {
public class Path {
  private int[] path;  // the sequence of indices of points
  private int idxInPopulation;  // the index of the path in population
  private int length;  // the total length of path sequence
  private double fitness; // fitness for the path

  public Path(int maxLength, boolean randomGeneration) {
    length = maxLength;

    // Initialize path as a random order-based path
    // if randomGeneration is checked true.
    if (randomGeneration) {
      // Generate a random order-based path.
      path = ArrayUtils.genRandomIntegers(0, maxLength);
    }
    // Initialize fitness as the maximum value.
    fitness = 1e100;
    // Newly generated path's index in population has not assigned yet.
    idxInPopulation = -1;
  }

  public Path(int maxLength, boolean randomGeneration, int idxInPopulation) {
    length = maxLength;

    // Initialize path as a random order-based path
    // if randomGeneration is checked true.
    if (randomGeneration) {
      // Generate a random order-based path.
      path = ArrayUtils.genRandomIntegers(0, maxLength);
    }
    // Initialize fitness as the maximum value.
    fitness = 1e100;
    idxInPopulation = idxInPopulation;
  }

  public int getLength() {
    return length;
  }

  // Get the index of the path in population.
  public int getIdxInPopulation() {
    return idxInPopulation;
  }

  // Set the index of the path in population.
  public void setIdxInPopulation(int i) {
    idxInPopulation = i;
  }

  // Get the fitness value.
  public double getFitness() {
    return fitness;
  }

  // Set the fitness value.
  public void setFitness(double f) {
    fitness = f;
  }

  // Get the sequence of indices of points.
  public int[] getPath() {
    return path;
  }

  // Set the sequence of indices of points. If it already exists, replace it.
  public void setPath(int[] newPath) {
    path = newPath;
  }

  // Get the idx'th point in the path.
  public int getPointAt(int idx) {
    return path[idx];
  }

  // Calculate the fitness of the path.
  public double evaluate(Point[] points) {
    fitness = 0;
    for (int i = 0; i < getLength(); i++) {
      fitness += PointUtils.distance(points[getPointAt(i)],
              points[getPointAt((i+1) % getLength())]);
    }

    return fitness;
  }

  // String representation of the path.
  public String toString() {
    StringBuilder output = new StringBuilder();
    for (int i = 0; i < path.length; i++) {
      output.append(path[i]);
      output.append(" ");
    }
    return output.toString();
  }

}
