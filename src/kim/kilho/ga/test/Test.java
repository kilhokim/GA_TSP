package kim.kilho.ga.test;

import kim.kilho.ga.algorithm.lk.LK;
import kim.kilho.ga.algorithm.lk.TSPLib_IO;
import kim.kilho.ga.algorithm.lk.TwoEdgeTour;

/**
 * Test class, only for test-purpose
 */
public class Test {
  public static TSPLib_IO TSP_FILE;

  public static void main(String[] args) {
    LK lk;
    TwoEdgeTour tour;
    int[] optPath;
    int n;

    TSP_FILE = new TSPLib_IO();
    TSP_FILE.readTspFile(args[0]);
    TSP_FILE.constructNN(20, false);

    lk = new LK(TSP_FILE.gNumCity, TSP_FILE.gNumNN, TSP_FILE);
    tour = new TwoEdgeTour(TSP_FILE.gNumCity, TSP_FILE);

    n = TSP_FILE.gNumCity;

    optPath = new int[n];
    tour.makeRandomTour();
    lk.run(tour, null, null, null, null, null);
    double tourcost = tour.evaluate();
    optPath = tour.convertToOrder(optPath, n);

    for (int i = 0; i < optPath.length; i++) {
      System.out.print((optPath[i]+1) + " ");
    }
    System.out.println("");
    System.out.println("tourcost: " + tourcost);
  }


}

