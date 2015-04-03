package kim.kilho.ga.gene;

/**
 * Two-dimensional point coordinates for TSP
 * @author Kilho Kim
 */
public class Point implements Gene {
  double x;
  double y;

  public Point() {

  }

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public void setPoint(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public double getX() {
    return this.x;
  }

  public double getY() {
    return this.y;
  }
}
