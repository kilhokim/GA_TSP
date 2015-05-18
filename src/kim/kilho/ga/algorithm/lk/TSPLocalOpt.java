package kim.kilho.ga.algorithm.lk;

/**
 * Parent class for implementing local optimization algorithm for solving TSP.
 * @author Kilho Kim
 * @reference C++ code of Genetics Algorithm class, Prof. Moon, Seoul National University, 2015
 */
public abstract class TSPLocalOpt {
  protected SegmentTree segTree;         // Segment tree
  protected LookbitQueue lookbitQueue;   // Look-bit queue
  protected int numCity;                 // The number of cities
  protected int numNN;                   // The number of neighboring cities

  /**
   * Constructor.
   * @param num_city  : The number of cities
   * @param num_nn    : The number of neighboring cities to investigate
   */
  public TSPLocalOpt(int num_city, int num_nn) {
    numCity = num_city;
    numNN = num_nn;
    lookbitQueue = new LookbitQueue(num_city);
    segTree = new SegmentTree(num_city);
  }

  /**
   * Runs local optimization algorithm.
   * @param tour  : The tour which local optimization algorithm is applied to
   * @param op1   : Optional tour #1
   * @param op2   : Optional tour #2
   * @param op3   : Optional tour #3
   * @param op4   : Optional tour #4
   * @param op5   : Optional tour #5
   */
  public abstract void run(TwoEdgeTour tour, TwoEdgeTour op1, TwoEdgeTour op2,
                           TwoEdgeTour op3, TwoEdgeTour op4, TwoEdgeTour op5);
}
