package kim.kilho.ga.test;

/**
 * Created by kilho on 15. 5. 13.
 */
public class CTSPLocalOpt {
  protected CSegmentTree segTree;
  protected CLookbitQueue lookbitQueue;
  protected int numCity;
  protected int numNN;

  public CTSPLocalOpt(int num_city, int num_nn) {
    numCity = num_city;
    numNN = num_nn;
    lookbitQueue = new CLookbitQueue(num_city);
    segTree = new CSegmentTree(num_city);
  }

  public void run(C2EdgeTour tour, C2EdgeTour op1, C2EdgeTour op2,
                  C2EdgeTour op3, C2EdgeTour op4, C2EdgeTour op5) {}
}

