package kim.kilho.ga.test;

import java.util.Arrays;

/**
 * Created by kilho on 15. 5. 13.
 */
public class CLK extends CTSPLocalOpt {
  private static TSPLIB_IO TSP_FILE;
  public static final double EPS = 1e-8;  // FIXME
  public static final int LK_DEPTH = 40;

  // public double[][] dist;  // FIXME
  // public int[] nni;
  private C2EdgeTour tour;
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

  public CLK(int num_city, int num_nn, TSPLIB_IO tsp_file) {
    super(num_city, num_nn);
    System.out.println("Entering CLK()");
    _n = num_city;
    _nnn = num_nn;
    _G = new double[_n+1];
    t = new int[_n*2+2];
    _t3_cand_list = new LK_T3_CAND[_nnn];
    _t5_cand_list = new LK_T5_CAND[_nnn];
    for (int i = 0; i < _nnn; i++) {
      _t3_cand_list[i] = new LK_T3_CAND();
      _t5_cand_list[i] = new LK_T5_CAND();
    }

    // NOTE: ADDED::
    this.TSP_FILE = tsp_file;
    System.out.println("Quitting CLK()");
  }

  @Override
  public void run(C2EdgeTour tour, C2EdgeTour p1, C2EdgeTour p2,
                  C2EdgeTour best, C2EdgeTour worst, C2EdgeTour other) {
    System.out.println("Entering CLK::run()");
    int t1, improved;

    lookbitQueue.construct(tour, p1, p2);
    this.tour = tour;

    // Initialize time-stamping -- segment tree
    segTree.initTimeStamping();

    // Run LK
    segTree.setupCityOrder(tour);
    improved = 1;
    while ((t1 = lookbitQueue.deleteLookbit()) >= 0) {
      if (do_lk_search(t1, improved) > EPS) {
        improved++;
        segTree.setupCityOrder(tour);
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
    t2_cand_list = this.tour.getNeighbor(t[1]);
    if (TSP_FILE.dist(t1, t2_cand_list[0]) - EPS <= TSP_FILE.dist(t1, t2_cand_list[1])) {
      j3 = t2_cand_list[0]; t2_cand_list[0] = t2_cand_list[1]; t2_cand_list[1] = j3;
    }

    for (j1 = 0; j1 < 2; j1++) {
      t[2] = t2_cand_list[j1];
      _i = 1;
      segTree.setupTree(1, t);

      // Get t3 candidate list
      get_t3_cand_list();   // status: 0, seg(1)
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
          this.tour.make2Change(t[1], t[6], t[5], t[4]);
          segTree.setupTree(2, t);
          _i = 2;
        }  // end of t5

        if (_Gstar > EPS) break;  // status: 2change, seg(2)
        // assert(_k == 0);

        // Reverse change to 0, seg(1);
        this.tour.make2Change(t[1], t[4], t[3], t[2]);
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
            this.tour.make3Change(t[1], t[6], t[5], t[4], t[3], t[2]);
            segTree.setupTree(1, t);
            _i = 2;

            if (_t5_cand_list[j3].alter_t6 >= 0) {
              t[6] = _t5_cand_list[j3].alter_t6;
              _i = 3;
              make_three_change(improved);
              search_deeper(improved);
              if (_Gstar > EPS) break;
              // reverse change to 0, seg(1)
              this.tour.make3Change(t[1], t[6], t[5], t[4], t[3], t[2]);
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
            this.tour.make4Change(t[1], t[8], t[7], t[6],
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
      Gix = _G[_i-1] + TSP_FILE.dist(t[ci-1], ct);

      best_nt = Integer.MIN_VALUE;
      best_nnt = Integer.MIN_VALUE;  // ADDED
      best_gain = -1e100;
      for (i = 0; i < _nnn; i++) {
        // 1. Get a next t candidate of current t.
        nt = TSP_FILE.nni(ct, i);   // FIXME: Refers TSP_FILE

        // 2. Check if yi(ct, nt) is valid.
        //    yi must be <not in T> and <not in {x1, x2, ..., xi-1}>.
        if (tour.isThereEdge(ct, nt)) continue;

        // 3. Check if Gi > 0.
        if (Gix - TSP_FILE.dist(ct, nt) <= EPS) break;

        // 4. Get a next next t candidate
        nnt = segTree.getPrev(nt);

        // 5. Check if x{i+1}(nt, nnt) are valid.
        //    1) Check if gain is better than best_gain.
        //    2) x{i+1}s must be <in T> and
        //       <not in {y1, y2, ..., yi-1}>.
        gain = TSP_FILE.dist(nt, nnt) - TSP_FILE.dist(ct, nt);
        if (gain <= best_gain + EPS) continue;

        for (j = 1; j < _i; j++)
          if (tour.isSameEdge(nt, nnt, t[j*2], t[j*2+1]))
            break;
        if (j < _i) continue;
        best_nt = nt;  best_nnt = nnt;  best_gain = gain;
      }
      if (best_nt < 0) break;

      t[ci+1] = best_nt;
      t[ci+2] = best_nnt;
      _i++;
      make_two_change(improved);
    } while (_i <= LK_DEPTH && _G[_i-1] > _Gstar + EPS);

    if (-EPS < _Gstar && _Gstar < EPS)
      reverse_change(start_i);  // restore
  }

  private int check_and_update(int improved) {
    double gistar = TSP_FILE.dist(t[2*_i-1], t[2*_i]) - TSP_FILE.dist(t[2*_i], t[1]);
    if (_G[_i-1]+gistar > _Gstar+EPS) {
      _Gstar = _G[_i-1] + gistar;
      _k = _i;
      return 1;
    }
    return 0;
  }

  private void reverse_change_to_best(int improved) {
    // assert(_k <= _i && _k > 1);
    int i = _i;
    while (i > _k) {
      i--;
      tour.make2Change(t[1], t[i*2+2], t[i*2+1], t[i*2]);
    }

    for (i = _k*2; i > 0; i--)  // set look-biut
      lookbitQueue.addLookbit(t[i]);
  }

  private void reverse_change(int to) {
    // assert(to <= _i && to > 0);
    while (_i > to) {
      _i--;
      tour.make2Change(t[1], t[_i*2+2], t[_i*2+1], t[_i*2]);
    }
  }

  // Get (t3, t4) candidates
  private void get_t3_cand_list() {
    int i, j, t3, t4, alter_t4;
    double x1_d;

    // assert(_i == 1);
    x1_d = TSP_FILE.dist(t[1], t[2]);
    _num_t3_cand_list = 0;
    for (i = 0; i < _nnn; i++) {
      // 1. Get a t3 candidate.
      t3 = TSP_FILE.nni(t[2], i);

      // 2. Check if y1(t2, t3) is valid. y1 must be <not in T0>
      if (tour.isThereEdge(t[2], t3)) continue;

      // 3. Check if G1 > 0.
      if (x1_d - TSP_FILE.dist(t[2], t3) <= EPS) break;

      // 4. Get two t4 candidates.
      t4 = segTree.getPrev(t3);
      alter_t4 = segTree.getNext(t3);

      // 5. Check if x2s(t3, t4) are valid. x2s must be <in T0>.
      //    In this case, this condition is always satisfied.
      _t3_cand_list[_num_t3_cand_list].t3 = t3;
      _t3_cand_list[_num_t3_cand_list].t4 = t4;
      _t3_cand_list[_num_t3_cand_list].alter_t4 = alter_t4;
      _t3_cand_list[_num_t3_cand_list].gain = TSP_FILE.dist(t3, t4) - TSP_FILE.dist(t[2], t3);
      _num_t3_cand_list++;
    }

    // Sort: maximize |x2| - |y1|.
    _t3_cand_list = sort(_t3_cand_list, _num_t3_cand_list);
  }

  // Get (t5, t6) candidates
  private void get_t5_cand_list() {
    int i, j, t5, t6;
    double Gix;

    // assert(_i == 2);
    Gix = _G[1] + TSP_FILE.dist(t[3], t[4]);

    _num_t5_cand_list = 0;
    for (i = 0; i < _nnn; i++) {
      // 1. Get a t5 candidate.
      t5 = TSP_FILE.nni(t[4], i);

      // 2. Check if y2(t4, t5) is valid.
      //    y2 must be <not in T1> and <not in {x1}>
      if (tour.isThereEdge(t[4], t5)) continue;
      if (tour.isSameEdge(t[4], t5, t[1], t[2])) continue;

      // 3. Check if G2 > 0.
      if (Gix - TSP_FILE.dist(t[4], t5) <= EPS) break;

      // 4. Get a t6 candidate.
      t6 = segTree.getPrev(t5);

      // 5. Check if x3(t5, t6) are valid.
      //    x3 must be <in T1> and <not in {y1}>.
      if (tour.isSameEdge(t5, t6, t[2], t[3])) continue;

      _t5_cand_list[_num_t5_cand_list].t5 = t5;
      _t5_cand_list[_num_t5_cand_list].t6 = t6;
      _t5_cand_list[_num_t5_cand_list].gain = TSP_FILE.dist(t5, t6) - TSP_FILE.dist(t[4], t5);
      _num_t5_cand_list++;
    }

    // Sort: maximize |x3| - |y2|.
    _t5_cand_list = sort(_t5_cand_list, _num_t5_cand_list);
  }

  private void get_alter_t5_cand_list() {
    int i, j, t5, tmp;
    int[] t6s, t8s;
    int t6, t7, t8;
    int best_t7, best_t8;
    double Gix, Gixx, gain, best_gain;

    // assert(_i == 2);
    Gix = _G[1] + TSP_FILE.dist(t[3], t[4]);

    _num_t5_cand_list = 0;
    for (i = 0; i < _nnn; i++) {
      // 1. Get a t5 candidate.
      t5 = TSP_FILE.nni(t[4], i);

      // 2. Check if y2(t4, t5) is valid.
      //    y2 must be <not in T1> and <not in {x1}>
      if (tour.isThereEdge(t[4], t5)) continue;
      if (tour.isSameEdge(t[4], t5, t[1], t[2])) continue;

      // 3. Check if G2 > 0.
      if (Gix - TSP_FILE.dist(t[4], t5) <= EPS) break;

      // 4. Get two t6 candidates.
      t6s = tour.getNeighbor(t5);

      // 5. Check if x3s(t5, t6) are valid.
      //    x3s must be <in T1> and <not in {y1}>, <not in {x1, x2}>.
      if (tour.isSameEdge(t5, t6s[0], t[2], t[3]) ||
          tour.isSameEdge(t5, t6s[0], t[1], t[2]) ||
          tour.isSameEdge(t5, t6s[0], t[3], t[4])) t6s[0] = -1;
      if (tour.isSameEdge(t5, t6s[1], t[2], t[3]) ||
          tour.isSameEdge(t5, t6s[1], t[1], t[2]) ||
          tour.isSameEdge(t5, t6s[1], t[3], t[4])) t6s[1] = -1;
      if (t6s[0] < 0 && t6s[1] < 0) continue;
      if (t6s[0] < 0) {
        tmp = t6s[0];  t6s[0] = t6s[1];  t6s[1] = tmp;
      }

      // 6. If t6 is between t2 and t3
      if (segTree.isBetween(t[2], t5, t[3])) {
        _t5_cand_list[_num_t5_cand_list].code = 1;

        // 1. Swap t6s such that maximizing |x3|.
        if (t6s[1] > 0 && TSP_FILE.dist(t5, t6s[0]) > TSP_FILE.dist(t5, t6s[1]) + EPS) {
          tmp = t6s[0];  t6s[0] = t6s[1];  t6s[1] = tmp;
        }

        _t5_cand_list[_num_t5_cand_list].t6 = t6s[0];
        _t5_cand_list[_num_t5_cand_list].alter_t6 = t6s[1];
        _t5_cand_list[_num_t5_cand_list].gain =
                TSP_FILE.dist(t5, t6s[0]) - TSP_FILE.dist(t[4], t5);
      } else {
        _t5_cand_list[_num_t5_cand_list].code = 0;

        // 1. Get valid t6.
        if (segTree.isBetween(t[4], t6s[0], t5))
          t6 = t6s[0];
        else if (t6s[1] >= 0 && segTree.isBetween(t[4], t6s[1], t5))
          t6 = t6s[1];
        else continue;

        best_gain = -1e100;
        best_t7 = Integer.MIN_VALUE;
        best_t8 = Integer.MIN_VALUE;  // ADDED
        Gixx = Gix - TSP_FILE.dist(t[4], t5) + TSP_FILE.dist(t5, t6);
        for (j = 0; j < _nnn; j++) {
          // 1. Get a t7.
          t7 = TSP_FILE.nni(t6, i);

          // 2. Check if y3(t6, t7) is valid.
          //    y3 must be <not in T1> and <not in {x1, y2}>
          if (tour.isThereEdge(t6, t7)) continue;
          if (tour.isSameEdge(t6, t7, t[1], t[2])) continue;
          if (tour.isSameEdge(t6, t7, t[4], t[5])) continue;

          // 3. Check if G3 > 0.
          if (Gixx - TSP_FILE.dist(t6, t7) <= EPS) break;

          // 4. t7 must lie between t2 and t3.
          if (!segTree.isBetween(t[2], t7, t[3])) continue;

          // 5. Get two t8 candidates.
          t8s = tour.getNeighbor(t7);

          // 6. Check if x4s(t7, t8) are valid.
          //    x4s must be <in T1> and <not in {y1, x1, x2}>.
          if (tour.isSameEdge(t7, t8s[0], t[1], t[2]) ||
              tour.isSameEdge(t7, t8s[0], t[3], t[4])) t8s[0] = -1;
          if (tour.isSameEdge(t7, t8s[1], t[1], t[2]) ||
              tour.isSameEdge(t7, t8s[1], t[3], t[4])) t8s[1] = -1;

          if (t8s[0] < 0 && t8s[1] < 0) continue;
          if (t8s[0] < 0) {
            tmp = t8s[0];  t8s[0] = t8s[1];  t8s[1] = tmp;
          }

          // 7. Get valid t8 that maximize |x4|.
          if (t8s[1] >= 0 && TSP_FILE.dist(t7, t8s[0]) < TSP_FILE.dist(t7, t8s[1]) + EPS)
            t8 = t8s[1];
          else
            t8 = t8s[0];
          gain = TSP_FILE.dist(t7, t8) - TSP_FILE.dist(t6, t7);

          // 8. Compare current (t7, t8) with best (t7, t8)
          if (gain > best_gain + EPS) {
            best_t7 = t7;  best_t8 = t8;  best_gain = gain;
          }
        }
        if (best_t7 < 0) continue;

        _t5_cand_list[_num_t5_cand_list].t6 = t6;
        _t5_cand_list[_num_t5_cand_list].t7 = best_t7;
        _t5_cand_list[_num_t5_cand_list].t8 = best_t8;
        _t5_cand_list[_num_t5_cand_list].gain
                = best_gain + TSP_FILE.dist(t5, t6) - TSP_FILE.dist(t[4], t5);
      }
      _t5_cand_list[_num_t5_cand_list].t5 = t5;
      _num_t5_cand_list++;
    }

    // Sort: maximize gain.
    _t5_cand_list = sort(_t5_cand_list, _num_t5_cand_list, EPS);
  }



  private LK_T3_CAND[] sort(LK_T3_CAND[] arr_name, int size) {
    int i, cur, key;
    boolean replace_cond = false;
    LK_T3_CAND tmp;
    for (i = 0; i < size-1; i++) {
      key = i;
      for (cur = i+1; cur < size; cur++) {
        // maximize |x2| - |y1|
        replace_cond = _t3_cand_list[cur].gain > _t3_cand_list[key].gain + EPS;
        if (replace_cond) key = cur;
      }
      tmp = arr_name[i];  arr_name[i] = arr_name[key];  arr_name[key] = tmp;
    }
    return arr_name;
  }

  private LK_T5_CAND[] sort(LK_T5_CAND[] arr_name, int size) {
    int i, cur, key;
    boolean replace_cond = false;
    LK_T5_CAND tmp;
    for (i = 0; i < size-1; i++) {
      key = i;
      for (cur = i+1; cur < size; cur++) {
        // maximize |x3| - |y2|
        replace_cond = _t5_cand_list[cur].gain > _t5_cand_list[key].gain;
        if (replace_cond) key = cur;
      }
      tmp = arr_name[i];  arr_name[i] = arr_name[key];  arr_name[key] = tmp;
    }
    return arr_name;

  }

  private LK_T5_CAND[] sort(LK_T5_CAND[] arr_name, int size, double eps) {
    int i, cur, key;
    boolean replace_cond = false;
    LK_T5_CAND tmp;
    for (i = 0; i < size-1; i++) {
      key = i;
      for (cur = i+1; cur < size; cur++) {
        // maximize gain
        replace_cond = _t5_cand_list[cur].gain > _t5_cand_list[key].gain + eps;
        if (replace_cond) key = cur;
      }
      tmp = arr_name[i];  arr_name[i] = arr_name[key];  arr_name[key] = tmp;
    }
    return arr_name;
  }

  private void make_two_change(int improved) {
    // Now, we know t[1] ~ t[2*i]. We know G[0] ~ G[i-2].
    int ci = (_i-1)*2;

    // Segment tree
    segTree.do2Change(t[1], t[ci], t[ci+1], t[ci+2]);

    // C2EdgeTour
    tour.make2Change(t[1], t[ci], t[ci+1], t[ci+2]);

    _G[_i-1] = _G[_i-2] + TSP_FILE.dist(t[ci-1], t[ci]) - TSP_FILE.dist(t[ci], t[ci+1]);
    check_and_update(improved);
  }

  private void make_three_change(int improved) {
    // Now, we know t[1] ~ t[6]. We know G[0].

    // Segment tree
    segTree.do3Change(t[1], t[2], t[3], t[4], t[5], t[6]);

    // C2EdgeTour
    tour.make3Change(t[1], t[2], t[3], t[4], t[5], t[6]);

    _G[1] = TSP_FILE.dist(t[1], t[2]) - TSP_FILE.dist(t[2], t[3]);
    _G[2] = _G[1] + TSP_FILE.dist(t[3], t[4]) - TSP_FILE.dist(t[4], t[5]);

    check_and_update(improved);
  }

  private void make_four_change(int improved) {
    // Now, we know t[1] ~ t[8]. We know G[0].

    // Segment tree
    segTree.do4Change(t[1], t[2], t[3], t[4], t[5], t[6], t[7], t[8]);

    // C2EdgeTour
    tour.make4Change(t[1], t[2], t[3], t[4], t[5], t[6], t[7], t[8]);

    _G[1] = TSP_FILE.dist(t[1], t[2]) - TSP_FILE.dist(t[2], t[3]);
    _G[2] = _G[1] + TSP_FILE.dist(t[3], t[4]) - TSP_FILE.dist(t[4], t[5]);
    _G[3] = _G[2] + TSP_FILE.dist(t[5], t[6]) - TSP_FILE.dist(t[6], t[7]);

    check_and_update(improved);
  }

}
