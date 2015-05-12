package kim.kilho.ga.test;

import kim.kilho.ga.algorithm.Crossover;
import kim.kilho.ga.algorithm.LocalSearch;
import kim.kilho.ga.algorithm.Mutation;
import kim.kilho.ga.gene.Path;
import kim.kilho.ga.util.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;

/**
 * Test class, only for test-purpose
 */
public class Test {

  public static void main(String[] args) {


    // crossoverTest();
    // mutationTest();
  }




  /*
  public static void localSearchTest() {
    Path p = new Path(8, true);
    int[] arr1 = {0,1,2,3,4,5,6,7};
    p.setPath(arr1);
    System.out.println("p: " + p.toString());

    Path newP = LocalSearch.twoChange(p, 1, 7, null);
    System.out.println("newP: " + newP.toString());
  }

  public static void crossoverTest() {
    Path p1 = new Path(10, true);
    Path p2 = new Path(10, true);

//    int[] arr1 = {0,1,2,3,4,5,6,8,7,9};
//    int[] arr2 = {2,5,0,9,7,3,8,6,1,4};
//    p1.setPath(arr1);
//    p2.setPath(arr2);
    System.out.println("p1: " + p1.toString());
    System.out.println("p2: " + p2.toString());

    Path offspring = Crossover.edgeRecombination(p1, p2);
    System.out.println("offspring: " + offspring.toString());
  }

  public static void mutationTest() {
    Path p1 = new Path(10, true);
    System.out.println("p1: " + p1.toString());
    Path offspring = Mutation.scrambleMutation(p1);
    System.out.println("offspring: " + offspring.toString());
  }
  */
}

class CLookbitQueue {
  private int[] look_bit_queue;   // Lookbit queue
  private int length;   // Size of lookbit queue
  private int look_head;   // The head part of lookbit queue
  private int look_tail;   // The tail part of lookbit queue

  public CLookbitQueue(int length) {
    // assert(size > 0);
    look_bit_queue = new int[length];
    this.length = length;
  }

  // Construct an initial lookbit queue from c1 & c2
  public void construct(Path path, Path c1, Path c2) {
    // assert(tour.getLength() == this.length);
    int i, t;
    int[] ns;
    for (i = 0; i < length; i++)
      look_bit_queue[i] = -1;
    look_head = -1;
    look_tail = -1;
    if (c1 == null) {
      ns = path.getNeighbor(length/2);
      t = ns[0]; i = ns[1];
      for (i = 0; i < length; i++)
        addLookbit((t+i) % length);
    } else {
      // assert(c2);
      for (i = 0; i < length; i++) {
        if (!path.isThereEdge(i, c1.e1[i]) ||
            !path.isThereEdge(i, c1.e2[i]) ||
            !path.isThereEdge(i, c2.e1[i]) ||
            !path.isThereEdge(i, c2.e2[i]))
          addLookbit(i);
      }
    }
  }

  // Add a new city c into lookbit queue
  public void addLookbit(int c) {
    // assert(0 <= c && c <= length);
    // There is already the city in look_queue
    if (c == look_head || c == look_tail || look_bit_queue[c] >= 0)
      return;

    // Case 1: When look_queue is empty.
    if (look_tail < 0) {
      look_head = c;
      look_tail = c;
    } else {
      look_bit_queue[look_tail] = c;
      look_tail = c;
    }
  }

  // Pick up one city from lookbit queue
  public int deleteLookbit() {
    int res;
    // When look_queue is empty.
    if (look_head < 0)
      res = -1;
    // When there is one element in queue.
    else if (look_head == look_tail) {
      res = look_head;
      look_head = -1;
      look_tail = -1;
    } else {
      res = look_head;
      look_head = look_bit_queue[res];
      look_bit_queue[res] = -1;
    }
    return res;
  }
}

class CSegmentTree {
  private static int LK_DEPTH = 40;   // FIXME
  private Path _path;
  private int _n;   // The number of cities in the path
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

    _current_time = 1;  // Initial time stamping
    for (int i = 0; i < _n; i++)
      _time_stamp[i] = 0;
  }

  public void setupCityOrder(Path path) {
    // assert(path && path.isTour());
    int city, order;

    _path = path;
    _path.findFirst(0);
    order = 0;
    while ((city = _path.findNext()) >= 0)
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
      _path.make2Change(t[1], t[4], t[3], t[2]);
      do2Change(t[1], t[2], t[3], t[4]);
      _path.make2Change(t[1], t[2], t[3], t[4]);
    }
  }

  private SEGMENT findSegment(int city) {
    int order;
    SEGMENT seg = null;
    // assert(city >= 0 && city < _n);

    // Determine proper root to traverse the tree down.
    if (_time_stamp[city] == _current_time)
      seg = _loc[city];
    else
      seg = _seg_tree[0];   // _seg_tree[0] is always real root of the tree

    // Traverse down
    order = _city_order[city];
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
    // assert(seg != null);
    return seg;
  }

  private int getCityByOrder(int order, int referCity) {
    int city;
    order = (order + _n) % _n;
    city = _path.e1[referCity];
    if (city >= 0 && _city_order[city] == order)
      return city;
    city = _path.e2[referCity];
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
      new2.right_child = seg.right_child;;
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


  // TODO: Finish methods - isCorrect() and so on...

}

class CTSPLocalOpt {
  private int numCity;
  private int numNN;

  public CTSPLocalOpt(int num_city, int num_nn) {
    numCity = num_city;
    numNN = num_nn;
  }
}

class CLK extends CTSPLocalOpt {
  private Path[] paths;
  private int _i;   // Current level
  private int _k;   // Level to obtain $G^*$
  private double _G;  // Gain up to current level
  private double _Gstar;  // The highest gain up to now
  int[] t;  // Array t: $t_1, t_2, t_3, ... $
  int _n;  // The number of cities
  int _nnn;   // The number of the nearest neighboring cities



  public CLK(int num_city, int num_nn) {

  }
}
