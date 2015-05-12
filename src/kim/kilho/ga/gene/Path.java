package kim.kilho.ga.gene;

import kim.kilho.ga.util.ArrayUtils;
import kim.kilho.ga.util.PointUtils;

import java.util.Random;


/**
 * A sequence of points as a solution for TSP
 * @author Kilho Kim
 */
public class Path {
  private int[] path;  // the sequence of indices of points
  private int idxInPopulation;  // the index of the path in population
  private int length;  // the total length of path sequence
  private double distance; // total distance for the path

  // TODO: Clearly define variables below:
  private int edge_start;
  private int edge_v1;
  // FIXME: Are these e1 and e2 locus-based form?
  // city i --> city j : city i & j are connected.
  // If j<0, city i is not connected to city j in #e1#
  public int[] e1;
  // city i --> city k : city i & k are connected.
  // If k<0, city i is not connected to city k in #e2#
  public int[] e2;
  // Internal variables used by findFirst() and findNext()
  private int end, prev, cur;
  // The flag that indicates the usage of multi graph
  private boolean isMult;


  public Path() {
    distance = -1;
    length = 0;
    e1 = null;
    e2 = null;
  }

  public int getHammingDist(Path path) {
    int count;
    int[] vs;

    enumEdgeFirst(0);
    count = 0;
    while ((vs = enumEdgeNext()) != null)
      if (path.isThereEdge(vs[0], vs[1])) count++;
    // assert(getLength() - count >= 0);
    return (getLength() - count);
  }

  // TODO: Should load distance matrix when it is called
  public double evaluate(double[][] dist) {
    int[] vs;

    distance = 0;
    enumEdgeFirst(0);
    while ((vs = enumEdgeNext()) != null)
      distance += dist[vs[0]][vs[1]];
    return distance;
  }

  public void enumEdgeFirst(int start) {
    findFirst(start);
    edge_start = findNext();
    edge_v1 = findNext();
  }

  public int[] enumEdgeNext() {
    int v1, v2;
    if (edge_start < 0) return null;
    v1 = edge_v1;
    edge_v1 = findNext();
    v2 = edge_v1;
    if (edge_v1 < 0) {
      v2 = edge_start;
      edge_start = -1;
    }
    // assert(isThereEdge(new_v1, new_v2));
    int[] new_vs = {v1, v2};
    return new_vs;
  }

  public int[] convertToOrder(int[] dest, int size) {
    // assert(size >= gNumCity);
    int city, order;
    findFirst(0);
    order = 0;
    while ((city = findNext()) >= 0)
      dest[order++] = city;

    return dest;
  }

  public int[] makeRandomOrderTour(int[] dest, int length) {
    int i, j, k, tmp;
    Random rand = new Random();
    for (i = 0; i < length; i++)
      dest[i] = i;
    for (i = 0; i < length/2; i++) {
      j = rand.nextInt(length);
      k = rand.nextInt(length);
      tmp = dest[j]; dest[j] = dest[k]; dest[k] = tmp;
    }

    return dest;
  }


  public Path(int length) {
    create(length);
  }

  public void create(int length) {
    this.length = length;
    e1 = new int[length];
    e2 = new int[length];
    isMult = false;
    for (int i = 0; i < length; i++) {
      e1[i] = -1;
      e2[i] = -1;
    }
  }


  public void makeRandomTour() {
    int[] order_array = new int[length];
    order_array = makeRandomOrderTour(order_array, length);
    for (int i = 0; i < length; i++) {
      e1[i] = -1;
      e2[i] = -1;
    }
    for (int i = 0; i < length-1; i++)
      addEdge(order_array[i], order_array[i+1]);
    addEdge(order_array[0], order_array[length-1]);
    // assert(isTour());
  }

  public void findFirst(int start) {
    prev = e2[start];
    end = e2[start];
    cur = start;
  }

  // TODO: Understand what this method does
  public int findNext() {
    if (cur < 0) return -1;
    int temp = cur;
    cur = (e1[cur] == prev) ? e2[cur] : e1[cur];
    prev = temp;
    if (prev == end) cur = -1;
    return prev;
  }

  public boolean isThereEdge(int v1, int v2) {
    return ((e1[v1] == v2) || (e2[v1] == v2));
  }

  public void addEdge(int v1, int v2) {
    // assert(isMult || !isThereEdge(v1, v2));
    // assert(v1 != v2);
    // assert(e1[v1] < 0 || e2[v1] < 0);
    // assert(e1[v2] < 0 || e2[v2] < 0);
    // Add v2 in v1 side
    if (e1[v1] < 0) e1[v1] = v2;
    else            e2[v1] = v2;
    // Add v1 in v2 side
    if (e1[v2] < 0) e1[v2] = v1;
    else            e2[v2] = v1;
  }

  public void addEdgeSafely(int v1, int v2) {
    if (!isThereEdge(v1, v2)) addEdge(v1, v2);
  }

  public void deleteEdge(int v1, int v2) {
    int[] t;
    // assert(isThereEdge(v1, v2));
    // assert(v1 != v2);
    // Delete v2 in v1 side
    if (e1[v1] == v2) e1[v1] = -1;
    else              e2[v1] = -1;
    // Delete v1 in v2 side
    if (e1[v2] == v1) e1[v2] = -1;
    else              e2[v2] = -1;
  }

  public int getNumEdge() {
    int n = 0;
    for (int i = 0; i < length; i++) {
      if (e1[i] >= 0) n++;
      if (e2[i] >= 0) n++;
    }
    // assert(n % 2 == 0);
    return n/2;
  }

  // TODO: Understand what this method does
  public boolean isTour() {
    for (int i = 0; i < length; i++)
      if (getEdgeSize(i) <= 1)
        return false;

    findFirst(0);
    int n = 0;
    while (findNext() >= 0) {
      if (n > length) return false;
      n++;
    }
    if (n < length) return false;
    return (getNumEdge() == length);
  }

  public boolean isDisjointCycle() {
    for (int i = 0; i < length; i++) {
      // assert(getEdgeSize(i) == 2);
    }
    findFirst(0);
    int n = 0;
    while (findNext() >= 0) n++;
    if (n == length) return false;
    else return true;
  }

  public void make2Change(int t1, int t2, int t3, int t4) {
    deleteEdge(t1, t2); deleteEdge(t3, t4);
    addEdge(t1, t4);    addEdge(t2, t3);
  }

  public void make3Change(int t1, int t2, int t3,
                          int t4, int t5, int t6) {
    deleteEdge(t1, t2); deleteEdge(t3, t4); deleteEdge(t5, t6);
    addEdge(t1, t6);    addEdge(t2, t3);    addEdge(t4, t5);
  }

  public void make4Change(int t1, int t2, int t3, int t4,
                          int t5, int t6, int t7, int t8) {
    deleteEdge(t1, t2); deleteEdge(t3, t4);
    deleteEdge(t5, t6); deleteEdge(t7, t8);
    addEdge(t1, t8);    addEdge(t2, t3);
    addEdge(t4, t5);    addEdge(t6, t7);
  }

  // TODO: Understand what this method does
  public boolean isBetween(int prev, int cur, int city, int end) {
    if (city == cur || city == end) return true;
    int next;
    while ((next = getNext(prev, cur)) >= 0) {
      if (next == end) return false;
      if (next == city) return true;
      prev = cur;
      cur = next;
    }
    return false;
  }

  // FIXME: Does it really return void?
  public void convertFromOrder(int[] src, int length) {
    // assert(length == this.length);
    for (int i = 0; i < this.length; i++) {
      e1[i] = -1;
      e2[i] = -1;
    }
    for (int i = 0; i < this.length; i++)
      addEdge(src[i], src[(i+1)&this.length]);
    // assert(isTour());
  }

  public Path constructTabuEdge(Path tabu) {
    int ep;
    tabu.deleteAllEdge();
    for (int i = 0; i < length; i++) {
      ep = getEndPoint(i);
      if (i < ep) tabu.addEdge(i, ep);
    }

    return tabu;
  }

  public boolean canAddEdge(Path tabu, int v1, int v2) {
    if (tabu.isThereEdge(v1, v2) || isThereEdge(v1, v2) ||
        getEdgeSize(v1) == 2 || getEdgeSize(v2) == 2)
      return false;
    else
      return true;
  }

  public Path addEdgeWithTabu(Path tabu, int v1, int v2) {
    // assert(tabu.getEdgeSize(v1) < 2 && tabu.getEdgeSize(v2) < 2);
    int ep1, ep2;
    int[] ns;

    addEdge(v1, v2);
    ns = tabu.getNeighbor(v1);
    ep1 = (ns[0] >= 0) ? ns[0] : ns[1];
    if (ep1 >= 0) tabu.deleteEdge(v1, ep1);
    else          ep1 = v1;

    ns = tabu.getNeighbor(v2);
    ep2 = (ns[0] > 0) ? ns[0] : ns[1];
    if (ep2 >= 0) tabu.deleteEdge(v2, ep2);
    else          ep2 = v2;

    tabu.addEdge(ep1, ep2);

    return tabu;
  }

  // TODO: Understand what this method does
  public Path connectRandom(Path tabu) {
    int i, j, k, nv, len, rne, tmp;
    int[] av;
    Random rand = new Random();
    len = getLength();
    av = new int[len];

    // Save the cities that there are in the end of a path
    nv = 0;
    for (i = 0; i < len; i++)
      if (getEdgeSize(i) <= 1)
        av[nv++] = i;

    for (i = 0; i < nv/2; i++) {
      j = rand.nextInt(nv);
      k = rand.nextInt(nv);
      tmp = av[j]; av[j] = av[k]; av[k] = tmp;
    }
    rne = len - getNumEdge(); // We must add the rne number of edges
    while (rne > 1) {
      for (i = 0; i < nv; i++)
        if (av[i] >= 0) break;
      j = i;
      for (i++; i < nv; i++)
        if (av[i] >= 0 && !tabu.isThereEdge(av[j], av[i])) break;
      k = i;
      // assert(av[j] >= 0 && av[k] >= 0);
      // assert(getEdgeSize(av[j]) < 2 && getEdgeSize(av[k]) < 2);

      tabu = addEdgeWithTabu(tabu, av[j], av[k]);
      if (getEdgeSize(av[j]) == 2) av[j] = -1;
      if (getEdgeSize(av[k]) == 2) av[k] = -1;

      rne--;
    }

    /* Necessary step:
       The use of tabu edge make the tour a Hamiltonian PATH after all,
       less the tour is already a complete tour. (before this function call)
     */
    // assert(getNumEdge() >= getSize() -1);
    if (getNumEdge() == getLength() - 1) {
      for (i = 0; i < nv; i++)
        if (av[i] >= 0) break;
      j = i;
      for (i++; i < nv; i++)
        if (av[i] >= 0) break;
      k = i;
      addEdge(av[j], av[k]);
    }
    // assert(isTour());

    return tabu;
  }

  public int getEndPoint(int v) {
  int es, ep, prev_ep, tmp;
    es = getEdgeSize(v);

    if (es == 0 || es == 2) return -1;
    ep = (e1[v] >= 0) ? e1[v] : e2[v];
    prev_ep = v;

    while (getEdgeSize(ep) == 2) {
      tmp = ep;
      ep = (e1[ep] == prev_ep) ? e2[ep] : e1[ep];
      prev_ep = tmp;
      if (ep == v) return -1;
    }
    // assert(getEdgeSize(ep) == 1);
    return ep;
  }








  public void deleteAllEdge() {
    for (int i = 0; i < length; i++) {
      e1[i] = -1;
      e2[i] = -1;
    }
  }

  public int[] getNeighbor(int v) {
    int[] ns = {e1[v], e2[v]};
    return ns;
  }

  public int getNext(int prev, int cur) {
    return (e1[cur] == prev) ? e2[cur] : e1[cur];
  }

  public boolean isSameEdge(int v1, int v2, int v3, int v4) {
    return ((v1 == v3 && v2 == v4) || (v1 == v4 && v2 == v3));
  }

  /*  FIXME: Remove this method if found out useless
  public boolean isThereEdge2(int v1, int v2) {
    return ((e1[v1] == v2) || (e2[v1] == v2));
  }
  */

  public void setMultiGraphFlag(boolean flag) {
    isMult = flag;
  }

  public int getEdgeSize(int v1) {
    if (e1[v1] >= 0 && e2[v1] >= 0)       return 2;
    else if (e1[v1] >= 0 || e2[v1] >= 0)  return 1;
    else                                  return 0;
  }




  //////////////////////// ORIGINAL METHODS ////////////////////////////
  public Path(int maxLength, boolean randomGeneration) {
    length = maxLength;

    path = null;
    // Initialize path as a random order-based path
    // if randomGeneration is checked true.
    if (randomGeneration) {
      // Generate a random order-based path.
      path = ArrayUtils.genRandomIntegers(0, maxLength);
      // System.out.println("newly generated path=" + Arrays.toString(path));
    }
    // Initialize distance as the maximum value.
    distance = Double.MAX_VALUE;
    // Newly generated path's index in population has not assigned yet.
    idxInPopulation = -1;
  }

  public Path(int maxLength, boolean randomGeneration, int idxInPopulation) {
    length = maxLength;

    path = null;
    // Initialize path as a random order-based path
    // if randomGeneration is checked true.
    if (randomGeneration) {
      // Generate a random order-based path.
      path = ArrayUtils.genRandomIntegers(0, maxLength);
      // System.out.println("newly generated path #" + idxInPopulation + "=" + Arrays.toString(path));
    }
    // Initialize distance as the maximum value.
    distance = Double.MAX_VALUE;
    this.idxInPopulation = idxInPopulation;
  }

  public int getLength() {
    return length;
  }

  // Get the index of the path in population.
  public int getIdxInPopulation() {
    return idxInPopulation;
  }

  // Set the index of the path in population.
  public void setIdxInPopulation(int i) {
    idxInPopulation = i;
  }

  // Get the distance value.
  public double getDistance() {
    return distance;
  }

  // Set the distance value.
  public void setDistance(double d) {
    distance = d;
  }

  // Get the sequence of indices of points.
  public int[] getPath() {
    return path;
  }

  // Set the sequence of indices of points. If it already exists, replace it.
  public void setPath(int[] newPath) {
    path = newPath;
  }

  // Get the idx'th point in the path.
  public int getPoint(int idx) {
    return path[idx];
  }

  // Set the idx'th point in the path.
  public void setPoint(int idx, int pt) {
    path[idx] = pt;
  }

  // Calculate the distance of the path.
  /*
  public double evaluate(Point[] points) {
    distance = 0;
    for (int i = 0; i < getLength(); i++)
      distance += PointUtils.distance(getPoint(i), getPoint((i+1) % getLength()));

    return distance;
  }
  */

  // Re-evaluate and update the distance of the path only for the swapped part.
  /*
  public double reEvaluate(int i, int k, Point[] points) {
    // NOTE: If the distance is MAX_VALUE, the current path must be evaluated
    // at the very first time
    if (distance == Double.MAX_VALUE)
      distance = evaluate(points);

    double newDistance = distance;

    // Subtract the original distance of disconnected edges
    newDistance -= PointUtils.distance(getPoint((i-1+getLength())%getLength()),
                      getPoint(k));
    newDistance -= PointUtils.distance(getPoint(i),
                      getPoint((k+1) % getLength()));
    // Add the distance of newly replaced edges
    newDistance += PointUtils.distance(getPoint((i-1+getLength())%getLength()),
                      getPoint(i));
    newDistance += PointUtils.distance(getPoint(k),
                      getPoint((k+1) % getLength()));

    return newDistance;
  }
  */

  // String representation of the path.
  public String toString() {
    StringBuilder output = new StringBuilder();
    for (int i = 0; i < path.length; i++) {
      output.append(path[i]);
      if (i < path.length-1)
        output.append(" ");
    }
    return output.toString();
  }

}
