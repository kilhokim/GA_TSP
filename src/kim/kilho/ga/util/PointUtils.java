package kim.kilho.ga.util;

import kim.kilho.ga.gene.Point;

/**
 * Utilities for Point.
 * @author Kilho Kim
 */
public final class PointUtils {

  static double[][] dist = null;

  public static void calculate(Point[] points) {
    dist = new double[points.length][points.length];
    for (int i = 0; i < points.length; i++) {
      for (int j = 0; j < points.length; j++) {
        dist[i][j] = Math.sqrt(Math.pow(points[i].getX() - points[j].getX(), 2)
                     + Math.pow(points[i].getY() - points[j].getY(), 2));
      }
    }

  }

  // Calculate the distance between two points p1 and p2.
  public static double distance(int i, int j) {
    /*
    if (dist == null)
      throw new Exception("Distance has not calculated yet");
    */

    return dist[i][j];
    /*
    return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2)
                     + Math.pow(p1.getY() - p2.getY(), 2));
                     */
  }
}
