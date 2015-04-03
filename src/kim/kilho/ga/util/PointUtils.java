package kim.kilho.ga.util;

import kim.kilho.ga.gene.Point;

/**
 * Utilities for Point.
 * @author Kilho Kim
 */
public final class PointUtils {

  // Calculate the distance between two points p1 and p2.
  public static double distance(Point p1, Point p2) {
    return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2)
                     + Math.pow(p1.getY() - p2.getY(), 2));
  }
}
