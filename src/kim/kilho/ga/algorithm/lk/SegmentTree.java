package kim.kilho.ga.algorithm.lk;

import java.util.Arrays;

/**
 * Created by kilho on 15. 5. 13.
 */
public class SegmentTree {
  private static int LK_DEPTH = 40;   // FIXME

  private TwoEdgeTour _tour;
  private int _n;   // The number of cities in the tour
  private SEGMENT[] _seg_tree;
  private int _seg_size;  // The number of nodes in segment tree
  private int[] _city_order;   // city# --> order array
  private SEGMENT[] _loc;      // city# --> the segment which includes the city
  private int[] _time_stamp;   // city# --> the last set time
  private int _current_time;   // current time

  class SEGMENT implements Cloneable {
    int idx_seg_tree = -1;    // ADDED: index in _seg_tree
    int idx_loc = -1;   // ADDED: index in _loc
    boolean from_loc;   // ADDED: indicator if it is from _loc
    int reversal;   // reversal bit
    int first;    // The first city of this segment
    int last;     // The last city of this segment
    SEGMENT left_child = null;   // The left child when regarded as tree
    SEGMENT right_child = null;   // The right child when regarded as tree
    SEGMENT prev = null;   // The previous segment when regarded as list
    SEGMENT next = null;   // The next segment when regarded as list

    @Override
    protected Object clone() throws CloneNotSupportedException {
      return super.clone();
    }

    public String toString() {
      return String.valueOf(this.idx_seg_tree);
    }
  }

  public SegmentTree(int size) {
    _n = size;
    _city_order = new int[_n];
    _time_stamp = new int[_n];
    _seg_tree = new SEGMENT[(LK_DEPTH+2)*2];
    for (int i = 0; i < _seg_tree.length; i++) {
      _seg_tree[i] = new SEGMENT();
      _seg_tree[i].idx_seg_tree = -1;
      _seg_tree[i].first = -1;
      _seg_tree[i].last = -1;
      // _seg_tree[i].from_loc = false;
    }
    _loc = new SEGMENT[_n];
    for (int i = 0; i < _loc.length; i++) {
      _loc[i] = new SEGMENT();
      _loc[i].idx_loc = -1;
      // _loc[i].index = i;
      // _loc[i].from_loc = true;
    }

    _current_time = 1;  // Initial time stamping
    for (int i = 0; i < _n; i++)
      _time_stamp[i] = 0;
  }

  public void initTimeStamping() {
    _current_time = 1;
    for (int i = 0; i < _n; i++) _time_stamp[i] = 0;
  }

  public void setupCityOrder(TwoEdgeTour tour) {
    // assert(tour && tour.isTour());
    int city, order;

    _tour = tour;
    _tour.findFirst(0);
    order = 0;
    while ((city = _tour.findNext()) >= 0) {
      _city_order[city] = order++;
    }
    System.out.println("_city_order=" + Arrays.toString(_city_order));
    // assert(order == _n);
  }

  public void setupTree(int to, int[] t) {
    System.out.println("**Entering setupTree()");
    // assert(to <= 2);

    // Change current timestamp
    if (_current_time > Integer.MAX_VALUE - 100) {
      _current_time = 1;
      for (int i = 0; i < _n; i++)
        _time_stamp[i] = 0;
    } else
      _current_time++;

    // Make root
    if (to <= 2) {
      _seg_tree[0].idx_seg_tree = 0;
      _seg_tree[0].first = 0;
      _seg_tree[0].last = _n-1;
      _seg_tree[0].left_child = null;
      _seg_tree[0].right_child = null;
      _seg_tree[0].prev = _seg_tree[0];
      _seg_tree[0].next = _seg_tree[0];
      if ((_city_order[t[1]]+1)%_n == _city_order[t[2]])
        _seg_tree[0].reversal = 0;
      else
        _seg_tree[0].reversal = 1;
      _seg_size = 1;
    }
    if (to == 2) {
      // assert(_seg_size == 1);
      _tour.make2Change(t[1], t[4], t[3], t[2]);
      do2Change(t[1], t[2], t[3], t[4]);
      _tour.make2Change(t[1], t[2], t[3], t[4]);
    }
    System.out.println("_tour.e1=" + Arrays.toString(_tour.e1));
    System.out.println("_tour.e2=" + Arrays.toString(_tour.e2));
    System.out.println("_seg_tree=");
    for (int i = 0; i < _seg_size; i++) {
      SEGMENT seg = _seg_tree[i];
      System.out.println("seg=" + seg + ", seg.first=" + seg.first + ", seg.last=" + seg.last
              + ", seg.left_child=" + seg.left_child + ", seg.right_child=" + seg.right_child);
    }
    System.out.println("_time_stamp=" + Arrays.toString(_time_stamp));
    System.out.println("_current_time=" + _current_time);
    System.out.println("**Quitting setupTree()");
  }

  // ADDED: Set segment
  private void setSegment(SEGMENT seg) {
//    if (seg.idx_loc >= 0)
//      _loc[seg.idx_loc] = seg;
    _seg_tree[seg.idx_seg_tree] = seg;
  }

  private int getCityByOrder(int order, int referCity) {
    int city;
    order = (order + _n) % _n;
    city = _tour.e1[referCity];
    if (city >= 0 && _city_order[city] == order)
      return city;
    city = _tour.e2[referCity];
    if (city >= 0 && _city_order[city] == order)
      return city;
    // assert(0);   // invalid referCity or order;
    return -1;
  }

  private SEGMENT findSegment(int city) {
    System.out.println("**Entering findSegment()");
    System.out.println("_seg_tree=");
    for (int i = 0; i < _seg_size; i++) {
      SEGMENT seg = _seg_tree[i];
      System.out.println("seg=" + seg + ", seg.first=" + seg.first + ", seg.last=" + seg.last
                         + ", seg.left_child=" + seg.left_child + ", seg.right_child=" + seg.right_child);
    }
    int order;
    SEGMENT seg = null;
    // assert(city >= 0 && city < _n);

    System.out.print("_current_time=" + _current_time + ", city=" + city + ", _time_stamp=" + Arrays.toString(_time_stamp) + ", _loc=[ ");
    for (int i = 0; i < _loc.length; i++)
      System.out.print(_loc[i] + ", ");
    System.out.println("]");

    // Determine proper root to traverse the tree down.
    if (_time_stamp[city] == _current_time) {
      seg = _loc[city];
      System.out.println("seg = _loc["+city+"] = " + seg);
    } else {
      seg = _seg_tree[0];   // _seg_tree[0] is always real root of the tree
      System.out.println("seg = _seg_tree[0] = " + seg);
    }

    // Traverse down
    order = _city_order[city];
    while (true) {
//      try {
        // System.out.println("seg.idx_seg_tree=" + seg.idx_seg_tree + ", seg.first=" + seg.first + ", seg.last=" + seg.last);
//        Thread.sleep(0);
        // System.out.println("while (true) in findSegment()...");
        System.out.println("seg=" + seg + ", seg.first=" + seg.first + ", order=" + order + ", seg.last=" + seg.last);
        if (seg.first <= order && order <= seg.last) {
          if (_time_stamp[city] != _current_time) {
            seg.idx_loc = city;   // ADDED
            _loc[city] = seg;
            System.out.println("***** Save _loc[" + city + "]=" + _loc[city]);
            _time_stamp[city] = _current_time;
            // System.out.println("_current_time=" + _current_time + ", _time_stamp=" + Arrays.toString(_time_stamp));
          }
          break;
        } else if (seg.first > order) {
          seg = seg.left_child;
          // System.out.println("seg.left_child.idx_seg_tree=" + seg.idx_seg_tree);
          // System.out.println("seg.left_child.idx_seg_tree=" + seg.idx_seg_tree);
        }
        else {
          // assert(seg.last < order);
          seg = seg.right_child;
          // System.out.println("seg.right_child.idx_seg_tree=" + seg.idx_seg_tree);
          // System.out.println("seg.right_child.idx_seg_tree=" + seg.idx_seg_tree);
        }
//      } catch (CloneNotSupportedException e) {
//        e.printStackTrace();
//      }
    }

    // assert(seg != null);
    System.out.println("**Quitting findSegment()");
    return seg;
  }

  private void splitSegment(int c1, int c2) throws CloneNotSupportedException {
    System.out.println("**Entering splitSegment()");
    SEGMENT s1, s2;
    int o1, o2, tmp;

    s1 = findSegment(c1);
    s2 = findSegment(c2);
    // System.out.println("s1.idx_seg_tree=" + s1.idx_seg_tree + ", s1.first=" + s1.first + ", s1.last=" + s1.last);
    // System.out.println("s2.idx_seg_tree=" + s2.idx_seg_tree + ", s2.first=" + s2.first + ", s2.last=" + s2.last);
    // System.out.println("s1.idx_seg_tree=" + s1.idx_seg_tree + ", s2.idx_seg_tree=" + s2.idx_seg_tree);
    // if (s1.index != s2.index) return;
    if (s1.idx_seg_tree != s2.idx_seg_tree) {
      System.out.println("**Quitting splitSegment()");
      return;
    }

    SEGMENT new1, new2, seg;
    o1 = _city_order[c1]; o2 = _city_order[c2];
    if (o1 > o2) {
      tmp = o1; o1 = o2; o2 = tmp;
      tmp = c1; c1 = c2; c2 = tmp;
    }

    // Rare case: when succ(c1) == c2, o1 == _n-1 and o2 == 0
    if (o1 == 0 && o2 == _n-1) {
      o1 = _n-2;
      c1 = getCityByOrder(o1, c2);
    }
    // assert((o1+1)%_n == o2);

    System.out.println("o1=" + o1 + ", o2=" + o2);
//    System.out.println("s1.left_child=" + s1.left_child.idx_seg_tree
//                     + ", s1.right_child=" + s1.right_child.idx_seg_tree);
    seg = (SEGMENT)s1.clone();   // FIXME: copy s1 to seg
    // System.out.println("###seg.idx_seg_tree=" + seg.idx_seg_tree + ", seg.first=" + seg.first + ", seg.last=" + seg.last);
    if (o1-seg.first >= seg.last-o2) {
      // System.out.println("new1 = s1");
      new1 = s1; new2 = _seg_tree[_seg_size];  new2.idx_seg_tree = _seg_size;
      new1.left_child = seg.left_child;
      new1.right_child = new2;
//      System.out.println("new1.left_child=" + new1.left_child.idx_seg_tree
//                       + ", new1.right_child=" + new1.right_child.idx_seg_tree);
      new2.left_child = null;
      new2.right_child = seg.right_child;
//      System.out.println("new2.left_child=" + new2.left_child
//                       + ", new2.right_child=" + new2.right_child.idx_seg_tree);
    } else {
      // System.out.println("new2 = s1");
      new2 = s1; new1 = _seg_tree[_seg_size];  new1.idx_seg_tree = _seg_size;
      new1.left_child = seg.left_child;
      new1.right_child = null;
//      System.out.println("new1.left_child=" + new1.left_child.idx_seg_tree
//                       + ", new1.right_child=" + new1.right_child);
      new2.left_child = new1;
      new2.right_child = seg.right_child;
//      System.out.println("new2.left_child=" + new2.left_child.idx_seg_tree
//                       + ", new2.right_child=" + new2.right_child.idx_seg_tree);
    }
    _seg_size++;

    new1.reversal = seg.reversal;
    new2.reversal = seg.reversal;
    new1.first = seg.first;
    new1.last = o1;
    new2.first = o2;
    new2.last = seg.last;

    if (seg.reversal == 0) {
      new1.next = new2;  new2.prev = new1;
      // in case of first split, consider carefully
      if (_seg_size == 2) {
        new1.prev = new2;  new2.next = new1;
      } else {
        new1.prev = seg.prev;  new2.next = seg.next;
        seg.prev.next = new1;
        seg.next.prev = new2;
        // setSegment(seg);
      }
    } else {
      new1.prev = new2;  new2.next = new1;
      // in case of first split, consider carefully
      if (_seg_size == 2) {
        new1.next = new2;  new2.prev = new1;
      } else {
        new1.next = seg.next;  new2.prev = seg.prev;
        seg.prev.next = new2;
        seg.next.prev = new1;
        // setSegment(seg);
      }
    }

    System.out.println("new1=" + new1 + ", new2=" + new2 + ", new2.idx_loc=" + new2.idx_loc);
    setSegment(new1);
    setSegment(new2);
    // assert(isCorrect());
    // ADDED:
    // System.out.println("new1.idx_seg_tree=" + new1.idx_seg_tree + ", new1.first=" + new1.first + ", new1.last=" + new1.last);
    // System.out.println("new2.idx_seg_tree=" + new2.idx_seg_tree + ", new2.first=" + new2.first + ", new2.last=" + new2.last);
    // System.out.println("seg.idx_seg_tree=" + seg.idx_seg_tree + ", seg.first=" + seg.first + ", seg.last=" + seg.last);
    System.out.println("#####SPLITTED SEGMENT=");
    for (int i = 0; i < _seg_size; i++) {
      SEGMENT segm = _seg_tree[i];
      System.out.println("seg=" + segm + ", seg.first=" + segm.first + ", seg.last=" + segm.last
              + ", seg.left_child=" + segm.left_child + ", seg.right_child=" + segm.right_child);

    }
    System.out.println("**Quitting splitSegment()");
  }

  public void reverseTree() {
    SEGMENT start, end;
    if (_seg_size == 1)
      reverseSegment(_seg_tree[0], _seg_tree[0]);
    else if (_seg_size > 1) {
      end = _seg_tree[0];
      start = _seg_tree[0].next;
      reverseSegment(start, end);
    }
  }

  private void reverseSegment(SEGMENT start, SEGMENT end) {
    System.out.println("**Entering reverseSegment()");
    SEGMENT left, right, cur, next, tmp;
    // System.out.println("start.prev == end : " + (start.prev.idx_seg_tree == end.idx_seg_tree));
    if (start.idx_seg_tree == end.idx_seg_tree) {
      start.reversal = 1 - start.reversal;  // !start.reversal

      setSegment(start);
    } else if (start.prev.idx_seg_tree == end.idx_seg_tree) {
      // assert(end.next == start);
      cur = start;
      while (cur.idx_seg_tree != end.idx_seg_tree) {
        next = cur.next;
        cur.reversal = 1 - cur.reversal;   // !cur.reversal
        tmp = cur.next; cur.next = cur.prev; cur.prev = tmp;
        setSegment(cur);
        cur = next;
      }
      end.reversal = 1 - end.reversal;   // !end.reversal
      tmp = end.next; end.next = end.prev; end.prev = tmp;

      setSegment(end);
    } else {
      left = start.prev;  right = end.next;
      left.next = end;    right.prev = start;

      setSegment(left); setSegment(right);
      cur = start;
      while (cur.idx_seg_tree != end.idx_seg_tree) {
        next = cur.next;
        cur.reversal = 1 - cur.reversal;   // !cur.reversal
        tmp = cur.next; cur.next = cur.prev; cur.prev = tmp;
        setSegment(cur);
        cur = next;
      }
      end.reversal = 1 - end.reversal;   // !end.reversal
      tmp = end.next; end.next = end.prev; end.prev = tmp;
      start.next = right;
      end.prev = left;

      setSegment(start);
      setSegment(end);
    }
    // assert(isCorrect());
    System.out.println("**Quitting reverseSegment()");
  }

  private boolean isCorrect() {
    SEGMENT start, current;
    int num_segment = 0;

    start = _seg_tree[0];
    current = start.next;
    num_segment++;

    while (current != start) {
      num_segment++;
      current = current.next;
      // assert(num_segment <= (LK_DEPTH+2)*2);
    }
    // assert(num_segment == _seg_size);
    return (num_segment == _seg_size);
  }

  public void do2Change(int t1, int t2, int t3, int t4) {
    // assert(t2 == getNext(t1));
    // assert(t4 == getPrev(t3));
    SEGMENT s1, s2, s3, s4;

    System.out.println("t1=" + t1 + ", t2=" + t2);
    System.out.println("t3=" + t3 + ", t4=" + t4);
    try {
      splitSegment(t1, t2);
      splitSegment(t3, t4);
      s2 = findSegment(t2);
      s4 = findSegment(t4);
      // assert(s1 = findSegment(t1));
      // assert(s3 = findSegment(t3));
      // assert(s1.next == s2);  assert(s3.prev == s4);

      reverseSegment(s2, s4);
    } catch (CloneNotSupportedException e) { e.printStackTrace(); }
  }

  public void do3Change(int t1, int t2, int t3,
                        int t4, int t5, int t6) {
    // assert(t2 == getNext(t1));
    // assert(t4 == getNext(t3));
    SEGMENT s1, s2, s3, s4, s5, s6;

    try {
      splitSegment(t1, t2);
      splitSegment(t3, t4);
      splitSegment(t5, t6);
      s2 = findSegment(t2);
      s3 = findSegment(t3);
      s5 = findSegment(t5);
      s6 = findSegment(t6);
      // assert(s1 = findSegment(t1));
      // assert(s4 = findSegment(t4));
      // assert(s1.next == s2);  assert(s3.prev == s4);

      if (getNext(t5) == t6) {
        reverseSegment(s2, s3);
        reverseSegment(s3, s6);
        reverseSegment(s5, s2);
      } else {
        reverseSegment(s2, s6);
        reverseSegment(s5, s3);
      }
      // assert(findSegment(t3) == s3);
      // assert(findSegment(t6) == s6);
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
  }

  public void do4Change(int t1, int t2, int t3, int t4,
                        int t5, int t6, int t7, int t8) {
    // assert(t2 == getNext(t1));
    // assert(t4 == getNext(t3));
    // assert(t6 == getNext(t5));
    SEGMENT s2, s3, s4, s6, s7, s8;

    try {
      splitSegment(t1, t2);
      splitSegment(t3, t4);
      splitSegment(t5, t6);
      splitSegment(t7, t8);

      s2 = findSegment(t2);
      s3 = findSegment(t3);
      s4 = findSegment(t4);
      s6 = findSegment(t6);
      s7 = findSegment(t7);
      s8 = findSegment(t8);

      if (getNext(t7) == t8) {
        reverseSegment(s2, s3);
        reverseSegment(s3, s8);
        reverseSegment(s7, s2);
        reverseSegment(s4, s6);
      } else {
        reverseSegment(s2, s8);
        reverseSegment(s7, s3);
        reverseSegment(s4, s6);
      }
      // assert(findSegment(t3) == s3);
      // assert(findSegment(t8) == s8);
    } catch (CloneNotSupportedException e) { e.printStackTrace(); }
  }

  public int getNext(int city) {
    int c, a_order, b_order = 0;
    SEGMENT a, b;
    a = findSegment(city);
    a_order = _city_order[city];

    if (a.reversal == 0) {
      if (a_order < a.last) b_order = a_order + 1;
      else if (a_order == a.last) {
        b = a.next;
        b_order = (b.reversal == 0) ? b.first : b.last;
      }
    } else {
      if (a_order > a.first) b_order = a_order - 1;
      else if (a_order == a.first) {
        b = a.next;
        b_order = (b.reversal == 0) ? b.first : b.last;
      }
    }
    c = getCityByOrder(b_order, city);
    // assert(_tour.isThereEdge(city, c));
    return c;
  }

  public int getPrev(int city) {
    int i, c, a_order, b_order = 0;
    SEGMENT a, b;

    a = findSegment(city);
    a_order = _city_order[city];

    if (a.reversal == 0) {
      if (a_order > a.first) b_order = a_order - 1;
      else if (a_order == a.first) {
        b = a.prev;
        b_order = (b.reversal == 0) ? b.last : b.first;
      }
    } else {
      if (a_order < a.last) b_order = a_order + 1;
      else if (a_order == a.last) {
        b = a.prev;
        b_order = (b.reversal == 0) ? b.last : b.first;
      }
    }
    c = getCityByOrder(b_order, city);
    // assert(_tour.isThereEdge(city, c));
    return c;
  }

  public boolean isBetween(int start, int middle, int end) {
    if (middle == start || middle == end) return true;
    if (start == end) return false;

    int starto, mido, endo;
    boolean res;
    SEGMENT ss, sm, se, cur, next;

    starto = _city_order[start];
    mido = _city_order[middle];
    endo = _city_order[end];

    if (_seg_size == 1) {
      ss = _seg_tree[0];
      sm = _seg_tree[0];
      se = _seg_tree[0];
    } else {
      ss = findSegment(start);
      sm = findSegment(middle);
      se = findSegment(end);
    }

    // if ss == se, direction of tour is important.
    if (ss == sm && ss == se) {
      // in this situation, we process 6 cases.
      if (starto < mido && mido < endo) res = true;
      else if (starto < endo && endo < mido) res = false;
      else if (mido < starto && starto < endo) res = false;
      else if (mido < endo && endo < starto) res = true;
      else if (endo < mido && mido < starto) res = false;
      else res = true;
      if (ss.reversal == 1) res = !res;
    } else if (ss == se)
      res = (ss.reversal == 1) ? (starto < endo) : (endo < starto);
    else if (ss == sm)
      res = (ss.reversal == 1) ? (mido < starto) : (starto < mido);
    else if (se == sm)
      res = (sm.reversal == 1) ? (endo < mido) : (mido < endo);
      // Now, ss != sm != se, thus calculate using segments.
    else {
      cur = ss;
      while ((cur = cur.next) != se)
        if (cur == sm) break;
      res = (cur == sm);
    }
    return res;
  }
}
