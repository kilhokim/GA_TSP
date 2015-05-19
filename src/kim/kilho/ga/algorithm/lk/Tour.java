package kim.kilho.ga.algorithm.lk;

import java.util.Random;

/**
 * Parent class for tour used in TSP.
 * @author Kilho Kim
 * @reference C++ code of Genetics Algorithm class, Prof. Moon, Seoul National University, 2015
 */
public abstract class Tour {
  protected static TSPLib_IO TSP_FILE;

  // Internal variables used for traversal in findEdgeFirst() and findEdgeNext()
  private int edge_start;
  private int edge_v1;

  protected int length;  // the total length of tour sequence
  protected double distance; // total distance for the tour

  /**
   * Default constructor.
   */
  public Tour() {
    distance = -1;
    length = 0;
    TSP_FILE = null;
  }

  public Tour(TSPLib_IO tsp_file) {
    distance = -1;
    length = 0;
    TSP_FILE = tsp_file;
  }

  public int getHammingDist(Tour tour) {
    int count;
    int[] vs;
    enumEdgeFirst(0);
    count = 0;
    while ((vs = enumEdgeNext()) != null)
      if (tour.isThereEdge(vs[0], vs[1])) count++;
    // assert(getLength() - count >= 0);
    return (getLength() - count);
  }

  public abstract void makeRandomTour();

  public abstract boolean isThereEdge(int v1, int v2);

  public abstract void findFirst(int start);

  public abstract int findNext();

  public abstract boolean isTour();

  public abstract void convertFromOrder(int[] src, int size);

  public abstract void make2Change(int v1, int n1, int v2, int n2);

  public abstract void make3Change(int v1, int n1, int v2, int n2,
                                   int v3, int n3);

  public abstract void make4Change(int v1, int n1, int v2, int n2,
                                   int v3, int n3, int v4, int n4);

  public double evaluate() {
    int[] vs;

    distance = 0;
    enumEdgeFirst(0);
    while ((vs = enumEdgeNext()) != null)
      distance += TSP_FILE.dist(vs[0], vs[1]);
    return distance;
  }

  public int getLength() {
    return length;
  }

  public double getDistance() {
    return distance;
  }

  public void enumEdgeFirst(int start) {
    findFirst(start);
    edge_start = findNext();
    edge_v1 = findNext();
  }

  public int[] enumEdgeNext() {
    int v1, v2;
    if (edge_start < 0) return null;
    v1 = edge_v1;
    edge_v1 = findNext();
    v2 = edge_v1;
    if (edge_v1 < 0) {
      v2 = edge_start;
      edge_start = -1;
    }
    // assert(isThereEdge(new_v1, new_v2));
    int[] new_vs = {v1, v2};
    return new_vs;
  }

  public int[] convertToOrder(int[] dest, int size) {
    // assert(size >= gNumCity);
    int city, order;
    findFirst(0);
    order = 0;
    while ((city = findNext()) >= 0)
      dest[order++] = city;

    return dest;
  }

  public int[] makeRandomOrderTour(int[] dest, int length) {
    int i, j, k, tmp;
    Random rand = new Random();
    for (i = 0; i < length; i++)
      dest[i] = i;
    for (i = 0; i < length/2; i++) {
      j = rand.nextInt(length);
      k = rand.nextInt(length);
      tmp = dest[j]; dest[j] = dest[k]; dest[k] = tmp;
    }

    return dest;
  }



}
