package kim.kilho.ga.test;


import java.util.Arrays;

/**
 * Test class, only for test-purpose
 */
public class Test {
  public static TSPLIB_IO TSP_FILE;

  public static void main(String[] args) {
    CLK lk;
    C2EdgeTour tour;
    int[] optPath;
    int n;

    TSP_FILE = new TSPLIB_IO();
    TSP_FILE.readTspFile("cycle.in");
    TSP_FILE.constructNN(20, false);

    lk = new CLK(TSP_FILE.gNumCity, TSP_FILE.gNumNN, TSP_FILE);
    tour = new C2EdgeTour(TSP_FILE.gNumCity, TSP_FILE);

    n = TSP_FILE.gNumCity;

    optPath = new int[n];
    tour.makeRandomTour();
    lk.run(tour, null, null, null, null, null);
    double tourcost = tour.evaluate();
    optPath = tour.convertToOrder(optPath, n);

    System.out.println(Arrays.toString(optPath));
    System.out.println("tourcost: " + tourcost);
  }


}

