package kim.kilho.ga.gene;

import kim.kilho.ga.exception.PathException;
import kim.kilho.ga.util.PointUtils;

import java.util.Random;

/**
 * A sequence of points as a solution for TSP
 * @author Kilho Kim
 */
// TODO: public class Path implements Chromosome {
public class Path {
  private int[] path;  // the sequence of indices of points
  private int length;  // the total length of path sequence
  private double fitness; // fitness for the path


  public Path(int maxLength) {
    path = new int[maxLength];
    length = maxLength;
    // Generate a random order-based path.
    for (int i = 0; i < maxLength; i++)
      path[i] = i;
    shufflePath(path);
    // Initialize fitness as the maximum value.
    fitness = 1e100;
  }

  // Shuffling method for path array.
  private static void shufflePath(int[] path) {
    Random rnd = new Random();
    for (int i = 0; i < path.length; i++) {
      int idx = i + rnd.nextInt(path.length-i);
      int tmp = path[idx]; path[idx] = path[i]; path[i] = tmp;  // Swap
    }
  }

  public int getLength() {
    return length;
  }

  // Set the fitness value.
  public void setFitness(double f) {
    fitness = f;
  }

  // Get the fitness value.
  public double getFitness() {
    return fitness;
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
              points[getPointAt((i + 1) % getLength())]);
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
