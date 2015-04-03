package kim.kilho.ga.gene;

import kim.kilho.ga.exception.PathException;
import kim.kilho.ga.util.PointUtils;

/**
 * A sequence of points as a solution for TSP
 * @author Kilho Kim
 */
public class Path {
  private Point[] path;
  private int length;
  private double availableTime; // available time for solving the GA problem.
  private double fitness; // fitness for the path


  public Path(int maxLength) {
    path = new Point[maxLength];
    length = 0;
    availableTime = 0;
  }

  // Get the length of the path.
  public int getLength() {
    return length;
  }

  // Set the available time.
  public void setAvailableTime(double time) {
    availableTime = time;
  }

  // Get the available time.
  public double getAvailableTime() {
    return availableTime;
  }

  // Set the fitness value.
  public void setFitness(double f) {
    fitness = f;
  }

  // Get the fitness value.
  public double getFitness() {
    return fitness;
  }

  // Add the new point in the path.
  public void add(Point point) {
    if (length < path.length)
      path[length++] = point;
    else
      throw new PathException("The path is full of points.");
  }

  // Get the idx'th point in the path.
  public Point get(int idx) {
    return path[idx];
  }

  // Set the idx'th point in the path. If it already exists, replace it.
  public void set(Point point, int idx) {
    path[idx] = point;
  }

  // Calculate the fitness of the path.
  public double evaluate(Path path) {
    fitness = 0;
    for (int i = 0; i < path.getLength(); i++) {
      fitness += PointUtils.distance(path.get(i),
              path.get((i + 1) % path.getLength()));
    }

    return fitness;
  }


  // Replace the original path with the new path made of n offspring.
  public void replace(Path offspring, int n) {
    // TODO: Complete implementing replace method
  }


}
