package kim.kilho.ga.gene;

import kim.kilho.ga.util.ArrayUtils;
import kim.kilho.ga.util.PointUtils;


/**
 * A sequence of points as a solution for TSP
 * @author Kilho Kim
 */
public class Path {
  private int[] path;  // the sequence of indices of points
  private int idxInPopulation;  // the index of the path in population
  private int length;  // the total length of path sequence
  private double distance; // total distance for the path

  public Path(int maxLength, boolean randomGeneration) {
    length = maxLength;

    // Initialize path as a random order-based path
    // if randomGeneration is checked true.
    if (randomGeneration) {
      // Generate a random order-based path.
      path = ArrayUtils.genRandomIntegers(0, maxLength);
      // System.out.println("newly generated path=" + Arrays.toString(path));
    }
    // Initialize distance as the maximum value.
    distance = 1e100;
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
      // System.out.println("newly generated path #" + idxInPopulation + "=" + Arrays.toString(path));
    }
    // Initialize distance as the maximum value.
    distance = 1e100;
    this.idxInPopulation = idxInPopulation;
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

  // Get the distance value.
  public double getDistance() {
    return distance;
  }

  // Set the distance value.
  public void setDistance(double d) {
    distance = d;
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
  public int getPoint(int idx) {
    return path[idx];
  }

  // Calculate the distance of the path.
  public double evaluate(Point[] points) {
    distance = 0;
    // System.out.println("getLength()=" + getLength());
    for (int i = 0; i < getLength(); i++) {
      // System.out.println("points[getPointAt(i)]=" + points[getPointAt(i)]);
      distance += PointUtils.distance(points[getPoint(i)],
              points[getPoint((i+1) % getLength())]);
    }

    return distance;
  }

  // String representation of the path.
  public String toString() {
    StringBuilder output = new StringBuilder();
    for (int i = 0; i < path.length; i++) {
      output.append(path[i]);
      if (i < path.length-1)
        output.append(" ");
    }
    return output.toString();
  }

}
