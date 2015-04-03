package kim.kilho.ga.gene;

import kim.kilho.ga.exception.PathException;

/**
 * A sequence of points as a solution for TSP
 * @author Kilho Kim
 */
public class Path {
  private Point[] path;
  private int length;

  public Path(int maxLength) {
    path = new Point[maxLength];
    length = 0;
  }

  // Get the length of the path.
  public int getLength() {
    return length;
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

  // Replace the original path with the new path made of n offspring.
  public void replace(Path offspring, int n) {
    // TODO: Complete implementing replace method
  }


}
