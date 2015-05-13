package kim.kilho.ga.test;

import kim.kilho.ga.gene.Path;

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
  }

  public void run(Path path, Path op1, Path op2, Path op3, Path op4, Path op5) {}
}

