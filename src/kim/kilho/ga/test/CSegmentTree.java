package kim.kilho.ga.test;

import java.util.Arrays;

/**
 * Created by kilho on 15. 5. 13.
 */
public class CSegmentTree {
  private static int LK_DEPTH = 40;   // FIXME
  private C2EdgeTour _tour;
  private int _n;   // The number of cities in the tour
  private SEGMENT[] _seg_tree;
  private int _seg_size;  // The number of nodes in segment tree
  private int[] _city_order;   // city# --> order array
  private SEGMENT[] _loc;      // city# --> the segment which includes the city
  private int[] _time_stamp;   // city# --> the last set time
  private int _current_time;   // current time

  class SEGMENT {
    int reversal;   // reversal bit
    int first;    // The first city of this segment
    int last;     // The last city of this segment
    SEGMENT left_child;   // The left child when regarded as tree
    SEGMENT right_child;   // The right child when regarded as tree
    SEGMENT prev;   // The previous segment when regarded as list
    SEGMENT next;   // The next segment when regarded as list
  }

  public CSegmentTree(int size) {
    _n = size;
    _city_order = new int[_n];
    _time_stamp = new int[_n];
    _seg_tree = new SEGMENT[(LK_DEPTH+2)*2];
    for (int i = 0; i < _seg_tree.length; i++)
      _seg_tree[i] = new SEGMENT();
    _loc = new SEGMENT[_n];
    for (int i = 0; i < _loc.length; i++)
      _loc[i] = new SEGMENT();

    _current_time = 1;  // Initial time stamping
    for (int i = 0; i < _n; i++)
      _time_stamp[i] = 0;
  }

  public void initTimeStamping() {
    /*
    _current_time = 1;
    for (int i = 0; i < _n; i++) _time_stamp[i] = 0;
     */
  }

  public void setupCityOrder(C2EdgeTour tour) {
    // assert(tour && tour.isTour());
    int city, order;

    _tour = tour;
    _tour.findFirst(0);
    order = 0;
    while ((city = _tour.findNext()) >= 0)
      _city_order[city] = order++;
    // assert(order == _n);
  }

  public void setupTree(int to, int[] t) {
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
  }

  private SEGMENT findSegment(int city) {
    int order;
    boolean pointIndicator = true;
    SEGMENT seg = null;
    // assert(city >= 0 && city < _n);
    System.out.println("city=" + city);
    // System.out.println("_loc=" + Arrays.toString(_loc));
    // System.out.println("_seg_tree=" + Arrays.toString(_seg_tree));

    // Determine proper root to traverse the tree down.
    if (_time_stamp[city] == _current_time) {
      seg = _loc[city];
      pointIndicator = true;
    } else {
      seg = _seg_tree[0];   // _seg_tree[0] is always real root of the tree
      pointIndicator = false;
    }

    System.out.println("seg.first=" + seg.first + ", seg.last=" + seg.last);

    // Traverse down
    order = _city_order[city];
    System.out.println("order=" + order);
    while (true) {
      if (seg.first <= order && order <= seg.last) {
        if (_time_stamp[city] != _current_time) {
          _loc[city] = seg;
          _time_stamp[city] = _current_time;
        }
        break;
      } else if (seg.first > order)
        seg = seg.left_child;
      else {
        // assert(seg.last < order);
        seg = seg.right_child;
      }
    }

    if (pointIndicator)
      _loc[city] = seg;
    else
      _seg_tree[0] = seg;

    // assert(seg != null);
    return seg;
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

  private void splitSegment(int c1, int c2) {
    SEGMENT s1, s2;
    int o1, o2, tmp;

    s1 = findSegment(c1);
    s2 = findSegment(c2);
    if (s1 != s2) return;

    SEGMENT new1, new2, seg;
    o1 = _city_order[1]; o2 = _city_order[c2];
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

    seg = s1;   // copy s1 to seg
    if (o1-seg.first >= seg.last-o2) {
      new1 = s1; new2 = _seg_tree[_seg_size];
      new1.left_child = seg.left_child;
      new1.right_child = new2;
      new2.left_child = null;
      new2.right_child = seg.right_child;
    } else {
      new2 = s1; new1 = _seg_tree[_seg_size];
      new1.left_child = seg.left_child;
      new1.right_child = null;
      new2.left_child = new1;
      new2.right_child = seg.right_child;
    }
    _seg_size++;

    new1.reversal = seg.reversal;
    new2.reversal = seg.reversal;
    new1.first = seg.first;   new1.last = o1;
    new2.first = o2;          new2.last = seg.last;

    if (seg.reversal == 0) {
      new1.next = new2;  new2.prev = new1;
      // in case of first split, consider carefully
      if (_seg_size == 2) {
        new1.prev = new2;  new2.next = new1;
      } else {
        new1.prev = seg.prev;  new2.next = seg.next;
        seg.prev.next = new1;
        seg.next.prev = new2;
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
      }
    }
    // assert(isCorrect());
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
    SEGMENT left, right, cur, next, tmp;
    if (start == end)
      start.reversal = 1 - start.reversal;  // !start.reversal
    else if (start.prev == end) {
      // assert(end.next == start);
      cur = start;
      while (cur != end) {
        next = cur.next;
        cur.reversal = 1 - cur.reversal;   // !cur.reversal
        tmp = cur.next; cur.next = cur.prev; cur.prev = tmp;
        cur = next;
      }
      end.reversal = 1 - end.reversal;   // !end.reversal
      tmp = end.next; end.next = end.prev; end.prev = tmp;
    } else {
      left = start.prev;  right = end.next;
      left.next = end;    right.prev = start;

      cur = start;
      while (cur != end) {
        next = cur.next;
        cur.reversal = 1 - cur.reversal;   // !cur.reversal
        tmp = cur.next; cur.next = cur.prev; cur.prev = tmp;
        cur = next;
      }
      end.reversal = 1 - end.reversal;   // !end.reversal
      tmp = end.next; end.next = end.prev; end.prev = tmp;
      start.next = right;
      end.prev = left;
    }
    // assert(isCorrect());
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
    splitSegment(t1, t2);  splitSegment(t3, t4);
    s2 = findSegment(t2);  s4 = findSegment(t4);
    // assert(s1 = findSegment(t1));
    // assert(s3 = findSegment(t3));
    // assert(s1.next == s2);  assert(s3.prev == s4);

    reverseSegment(s2, s4);
  }

  public void do3Change(int t1, int t2, int t3,
                        int t4, int t5, int t6) {
    // assert(t2 == getNext(t1));
    // assert(t4 == getNext(t3));
    SEGMENT s1, s2, s3, s4, s5, s6;

    splitSegment(t1, t2);  splitSegment(t3, t4);
    splitSegment(t5, t6);
    s2 = findSegment(t2);  s3 = findSegment(t3);
    s5 = findSegment(t5);  s6 = findSegment(t6);
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
  }

  public void do4Change(int t1, int t2, int t3, int t4,
                        int t5, int t6, int t7, int t8) {
    // assert(t2 == getNext(t1));
    // assert(t4 == getNext(t3));
    // assert(t6 == getNext(t5));
    SEGMENT s2, s3, s4, s6, s7, s8;

    splitSegment(t1, t2);  splitSegment(t3, t4);
    splitSegment(t5, t6);  splitSegment(t7, t8);

    s2 = findSegment(t2);  s3 = findSegment(t3);
    s4 = findSegment(t4);  s6 = findSegment(t6);
    s7 = findSegment(t7);  s8 = findSegment(t8);

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
