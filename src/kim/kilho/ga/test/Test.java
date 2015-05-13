package kim.kilho.ga.test;


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
    tour = new C2EdgeTour(TSP_FILE.gNumCity);

    n = TSP_FILE.gNumCity;

    optPath = new int[n];
    tour.makeRandomTour();
    lk.run(tour);






  }


}

