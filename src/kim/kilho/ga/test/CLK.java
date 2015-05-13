package kim.kilho.ga.test;

import kim.kilho.ga.gene.Path;

/**
 * Created by kilho on 15. 5. 13.
 */
public class CLK extends CTSPLocalOpt {
  public static final double EPS = 1e-8;  // FIXME
  public double[][] dist;  // FIXME
  public int[] nni;
  private Path path;
  private int _i;   // Current level
  private int _k;   // Level to obtain $G^*$
  private double[] _G;  // Gain up to current level
  private double _Gstar;  // The highest gain up to now
  private int[] t;  // Array t: $t_1, t_2, t_3, ... $
  private int _n;  // The number of cities
  private int _nnn;   // The number of the nearest neighboring cities
  private int _num_t3_cand_list;   // The number of $t_3$ candidates
  private LK_T3_CAND[] _t3_cand_list; // The array of $t_3$ candidates
  private int _num_t5_cand_list;   // The number of $t_5$ candidates
  private LK_T5_CAND[] _t5_cand_list; // The array of $t_5$ candidates

  class LK_T3_CAND { int t3; int t4; int alter_t4; double gain; }
  class LK_T5_CAND { int t5; int t6; int alter_t6;
    int t7; int t8; int code; double gain; }

  public CLK(int num_city, int num_nn) {
    super(num_city, num_nn);
    System.out.println("Entering CLK()");
    _n = num_city;
    _nnn = num_nn;
    _G = new double[_n+1];
    t = new int[_n*2+2];
    _t3_cand_list = new LK_T3_CAND[_nnn];
    _t5_cand_list = new LK_T5_CAND[_nnn];
    System.out.println("Quitting CLK()");
  }

  @Override
  public void run(Path path, Path p1, Path p2,
                  Path best, Path worst, Path other) {
    System.out.println("Entering CLK::run()");
    int t1, improved;

    lookbitQueue.construct(path, p1, p2);
    this.path = path;

    // Initialize time-stamping -- segment tree
    segTree.initTimeStamping();

    // Run LK
    segTree.setupCityOrder(path);
    improved = 1;
    while ((t1 = lookbitQueue.deleteLookbit()) >= 0) {
      if (do_lk_search(t1, improved) > EPS) {
        improved++;
        segTree.setupCityOrder(path);
      } else
        improved = 0;
    }
    System.out.println("Quitting CLK::run()");
  }

  private double do_lk_search(int t1, int improved) {
    int[] t2_cand_list;
    int j1 = 0, j2 = 0, j3 = 0;

    _i = _k = 0;
    _G[0] = 0;  _Gstar = 0;
    t[1] = t1;

    // Get t2 candidate list
    t2_cand_list = this.path.getNeighbor(t[1]);
    if (dist[t1][t2_cand_list[0]] - EPS <= dist[t1][t2_cand_list[1]]) {
      j3 = t2_cand_list[0]; t2_cand_list[0] = t2_cand_list[1]; t2_cand_list[1] = j3;
    }

    for (j1 = 0; j1 < 2; j1++) {
      t[2] = t2_cand_list[j1];
      _i = 1;
      segTree.setupTree(1, t);

      // Get t3 candidate list
      get_t3_candidate_list();   // status: 0, seg(1)
      for (j2 = 0; j2 < _num_t3_cand_list; j2++) {
        // status: 0, seg(1)
        t[3] = _t3_cand_list[j2].t3;
        // Choose t4 to be a connected graph. normal t4
        t[4] = _t3_cand_list[j2].t4;
        _i = 2;
        make_two_change(improved);

        get_t5_cand_list(); // get t5 cand. list with normal t4
        for (j3 = 0; j3 < _num_t5_cand_list; j3++) {
          // status: 2change, seg(2)
          t[5] = _t5_cand_list[j3].t5;
          t[6] = _t5_cand_list[j3].t6;
          _i = 3;
          make_two_change(improved);

          if (_G[_i-1] > _Gstar+EPS)  // further searching
            search_deeper(improved);   // status
          if (_Gstar > EPS) break;

          // when fail, reverse change to 2change, seg(2)
          this.path.make2Change(t[1], t[6], t[5], t[4]);
          segTree.setupTree(2, t);
          _i = 2;
        }  // end of t5

        if (_Gstar > EPS) break;  // status: 2change, seg(2)
        // assert(_k == 0);

        // Reverse change to 0, seg(1);
        this.path.make2Change(t[1], t[4], t[3], t[2]);
        segTree.setupTree(1, t);
        _i = 1;

        // Try for disconnected graph
        t[4] = _t3_cand_list[j2].alter_t4;
        _i = 2;
        get_alter_t5_cand_list();
        for (j3 = 0; j3 < _num_t5_cand_list; j3++) {
          // status: 0, seg(1)
          t[5] = _t5_cand_list[j3].t5;

          // If t5 is between t2 and t3
          if (_t5_cand_list[j3].code == 1) {
            t[6] = _t5_cand_list[j3].t6;
            _i = 3;
            make_three_change(improved);
            search_deeper(improved);  // status: 3change, seg(3)
            if (_Gstar > EPS) break;
            // reverse change to 0, seg(1)
            this.path.make3Change(t[1], t[6], t[5], t[4], t[3], t[2]);
            segTree.setupTree(1, t);
            _i = 2;

            if (_t5_cand_list[j3].alter_t6 >= 0) {
              t[6] = _t5_cand_list[j3].alter_t6;
              _i = 3;
              make_three_change(improved);
              search_deeper(improved);
              if (_Gstar > EPS) break;
              // reverse change to 0, seg(1)
              this.path.make3Change(t[1], t[6], t[5], t[4], t[3], t[2]);
              segTree.setupTree(1, t);
              _i = 2;
            }
          }  // end of t6
          else {  // if t5 lies between t1 and t4
            t[6] = _t5_cand_list[j3].t6;  _i = 3;
            t[7] = _t5_cand_list[j3].t7;
            t[8] = _t5_cand_list[j3].t8;  _i = 4;
            make_four_change(improved);
            search_deeper(improved);
            if (_Gstar > EPS) break;
            // reverse change to 0, seg(1)
            this.path.make4Change(t[1], t[8], t[7], t[6],
                    t[5], t[4], t[3], t[2]);
            segTree.setupTree(1, t);
            _i = 2;
          }  // end of t6
        }  // end of alter t5
        if (_Gstar > EPS) break;
        _i = 1;
      } // end of t3
      if (_Gstar > EPS) break;
    } // end of t2
    if (_Gstar > EPS) {
      System.out.println(String.format("t1=%d, j1=%d, j2=%d, alt j3=%d,", t1, j1, j2, j3));
      System.out.println(String.format(" i=%d,  k=%d, Gs=%d", _i, _k, _Gstar));
      reverse_change_to_best(improved);
    } else {
      System.out.println("all backtracking failed - t1=" + t1);
    }
    return _Gstar;
  }

  private void search_deeper(int improved) {
    int i, j, start_i;
    int ci, ct, nt, nnt;
    int best_nt, best_nnt;
    double Gix, gain, best_gain;

    start_i = _i;   // start_i is in no-change status.
    do {
      // Get t[2i+1], t[2i+2]
      ci = _i*2;  ct = t[ci];
      Gix = _G[_i-1] + dist[t[ci-1]][ct];

      best_nt = Integer.MIN_VALUE;
      best_gain = -1e100;
      for (i = 0; i < _nnn; i++) {
        // 1. Get a next t candidate of current t.
        // TODO:
        nt = nni(ct, i);
      }
    }

  }


}
