package kim.kilho.ga.algorithm.lk;

import java.util.Arrays;

/**
 * Look bit queue class.
 * @author Kilho Kim
 * @reference C++ code of Genetics Algorithm class, Prof. Moon, Seoul National University, 2015
 */
public class LookbitQueue {
  private int[] look_bit_queue;   // Lookbit queue
  private int length;   // Size of lookbit queue
  private int look_head;   // The head part of lookbit queue
  private int look_tail;   // The tail part of lookbit queue

  public LookbitQueue(int length) {
    // assert(size > 0);
    look_bit_queue = new int[length];
    this.length = length;
  }

  // Construct an initial lookbit queue from c1 & c2
  public void construct(TwoEdgeTour tour, TwoEdgeTour c1, TwoEdgeTour c2) {
    // assert(tour.getLength() == this.length);
    int i, t;
    int[] ns;
    for (i = 0; i < length; i++)
      look_bit_queue[i] = -1;
    look_head = -1;
    look_tail = -1;
    if (c1 == null) {
      ns = tour.getNeighbor(length/2);
      t = ns[0]; i = ns[1];
      for (i = 0; i < length; i++)
        addLookbit((t+i) % length);
    } else {
      // assert(c2);
      for (i = 0; i < length; i++) {
        if (!tour.isThereEdge(i, c1.e1[i]) ||
            !tour.isThereEdge(i, c1.e2[i]) ||
            !tour.isThereEdge(i, c2.e1[i]) ||
            !tour.isThereEdge(i, c2.e2[i]))
          addLookbit(i);
      }
    }
    // System.out.println("look_bit_queue=" + Arrays.toString(look_bit_queue));
    // System.out.println("look_head=" + look_head);
    // System.out.println("look_tail=" + look_tail);
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
