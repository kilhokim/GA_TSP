package kim.kilho.ga.test;

import kim.kilho.ga.gene.Point;
import kim.kilho.ga.io.file.FileManager;

/**
 * Created by kilho on 15. 5. 13.
 */
public class TSPLIB_IO {
  public static final double EPS = 1e-9;
  public static int MAX_COORD_SYSTEM = 20;
  public static int GRAPH_INFO_SIZE;  // TODO: Figure out what this is

  // Indicator that the given problem is whether symmetric TSP or not
  public static final int TYPE_STSP = 0;   // symmetric TSP
  public static final int TYPE_ATSP = 1;   // assymetric TSP

  // External global variables
  public static int gNumCity;
  public static int gType;   // Symmetric or Assymetric?
  public static int gNumNN;  // # of nearest neighbors
  public static double[] gDistMat;   // distance matrix
  public static int[] gNNI;   // nearest neighbors arrays

  // Internal global variables
  POINT[] gNodeCoords;   // When NODE_COORD_SECTION exists, used
  TSP_FILE_INFO gtfi;
  static String gGraphName;   // Graph name (without file extension)
  int gOptimumCost;  // Optimum path cost or lower bound if not, 0
  int[] gOrgInfo;    // idx: remapped city's id
  // value: original city's id

  class POINT {
    double[] pt = new double[2];
  }

  class GRAPH_INFO {
    String graph_name;
    int opt_cost;
  }

  class TSP_FILE_INFO {
    String szGraphName;
    String szType;
    String szComment;
    int nDimension;
    int nCoordDim;
    String szGraphType;
    String szEdgeType;
    String szEdgeWeightType;
    String szEdgeWeightFormat;
    String szEdgeDataFormat;
    String szNodeType;
    String szNodeCoordType;
    String szDisplayDataType;
  }

  public void readTspFile(String graphName) {
    FileManager fm = new FileManager();
    try {
      System.out.println("Entering readTspFile()");
      Object[] input = fm.read(graphName, 600);
      Point[] points = (Point[])input[0];
      gtfi.nDimension = points.length;
      // gtfi.nDimension++;   // TODO: Why?
      gNumCity = gtfi.nDimension;

      // readDataInNodeCoord():
      System.out.println("Entering readDataInNodeCoord()");
      int n = gtfi.nDimension;

      gNodeCoords = new POINT[n];
      gtfi.nCoordDim = 2;
      for (int i = 0; i < n; i++) {
        gNodeCoords[i].pt[0] = points[i].getX();
        gNodeCoords[i].pt[1] = points[i].getY();
      }

      gDistMat = new double[(n*(n-1))/2];
      for (int r = 0; r < n-1; r++)
        for (int c = r+1; c < n; c++)
          gDistMat[c*(c-1)/2 + r] = getEuc2DDist(r, c);
      System.out.println("Quitting readDataInNodeCoord()");

      gType = TYPE_STSP;
      System.out.println("Quitting readTspFile()");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public double getEuc2DDist(int c1, int c2) {
    double xd, yd;
    xd = gNodeCoords[c1].pt[0] - gNodeCoords[c2].pt[0];
    yd = gNodeCoords[c1].pt[1] - gNodeCoords[c2].pt[1];
    return Math.sqrt(xd*xd + yd*yd);
  }

  /**
   * TODO: Read path file.
   */
  public boolean readPathFile(String fileName, int[] path, int size) {
    return true;
  }

  /**
   * TODO: Read optimal path file.
   */
  public boolean readOptPathFile(int[] opt_path, int size) {
    return true;
  }

  public void printTspInfo() {
    TSP_FILE_INFO tfi = gtfi;
    System.out.println("Entering printTspInfo()");
    System.out.println("=============== TSP File INFO ===============");
    System.out.println("Graph Name          : " + tfi.szGraphName);
    System.out.println("Type                : " + tfi.szType);
    System.out.println("Dimension           : " + tfi.nDimension);
    if (tfi.szGraphType != null)
      System.out.println("Graph Type          : " + tfi.szGraphType);
    if (tfi.szEdgeType != null)
      System.out.println("Edge Type          : " + tfi.szEdgeType);
    if (tfi.szEdgeWeightType != null)
      System.out.println("Edge Weight Type          : " + tfi.szEdgeWeightType);
    if (tfi.szEdgeWeightFormat != null)
      System.out.println("Edge Weight Format          : " + tfi.szEdgeWeightFormat);
    if (tfi.szEdgeDataFormat != null)
      System.out.println("Edge Data Format            : " + tfi.szEdgeDataFormat);
    if (tfi.szNodeType != null)
      System.out.println("Node Type                   : " + tfi.szNodeType);
    if (tfi.szNodeCoordType != null)
      System.out.println("Node Coord. Type            : " + tfi.szNodeCoordType);
    if (tfi.szDisplayDataType != null)
      System.out.println("Display Data Type           : " + tfi.szDisplayDataType);
    System.out.println("=============================================");
    System.out.println("Quitting printTspInfo()");
  }

  // TODO: Print error
  public void error(String format, String str) {
  }

  // TODO: Print a sequence of coordinates to file, instead of path sequence
  public void printCoords(int[] path, int size) {
    /*
    System.out.println("Entering printCoords()");
    if (gNodeCoords != null) {
      for (int i = 0; i < size; i++) {
        int t = (gOrgInfo != null) ? gOrgInfo[path[i]] : path[i];
        for (int j = 0; j < gtfi.nCoordDim; j++)

      }
    }
    */
  }

  // FIXME: double[] pointer comes in
  public int getCoords(int city, double[] pt, int size) {
    if (gtfi.nCoordDim > size) return -gtfi.nCoordDim;
    for (int j = 0; j < gtfi.nCoordDim; j++)
      pt[j] = gNodeCoords[city].pt[j];

    return -1;
  }

  // TODO: Print the path sequence to file
  public void printPath(int[] path, int size) {

  }

  // Map is remapped ity --> org city.
  public void remapCities(int[] map) {
    int c1, c2, mi, mx;
    gOrgInfo = new int[gNumCity];
    int [] rmap = new int[gNumCity];
    gOrgInfo = map;  // memcpy

    for (int i = 0; i < gNumCity; i++)
      rmap[map[i]] = i;

    // TODO: Is it correct to use double[]?
    double[] temp = new double[(gNumCity*(gNumCity-1))/2];
    gDistMat = temp;  // memcpy
    for (int i = 0; i < gNumCity-1; i++) {
      for (int j = i+1; j < gNumCity; j++) {
        c1 = Math.min(rmap[i], rmap[j]);
        c2 = Math.max(rmap[i], rmap[j]);
        mi = Math.min(i, j);  mx = Math.max(i, j);

        gDistMat[c2*(c2-1)/2+c1] = temp[j*(j-1)/2+i];
      }
    }

    if (gNNI != null) {
      int rcity;
      int[] temp2 = new int[gNumCity*gNumNN];
      temp2 = gNNI;   // memcpy
      for (int i = 0; i < gNumCity; i++) {
        rcity = rmap[i];
        for (int j = 0; j < gNumNN; j++)
          gNNI[rcity*gNumNN+j] = rmap[temp2[i*gNumNN+j]];
      }
    }
  }

  public int getOrgCity(int city) {
    return ((gOrgInfo != null) ? gOrgInfo[city] : city);
  }

  // TODO:
  public int setOptimumCost(String gn) { return -1; }

  public int getOptimumCost() { return gOptimumCost; }

  public void constructNN(int numNN, boolean is_quadrant) {
    System.out.println("Entering constructNN()");
    gNumNN = numNN;
    if (is_quadrant &&
            (gtfi.szEdgeWeightType.equals("EUC_2D") ||
                    gtfi.szEdgeWeightType.equals("CEIL_2D") ||
                    gtfi.szEdgeWeightType.equals("ATT"))) {
      if (numNN % 20 != 0) gNumNN = (numNN/20 + 1) * 20;   // TODO: Why 20?
      if (gNumNN >= gNumCity) gNumNN -= 20;
      gNNI = new int[gNumCity*gNumNN];
      constructQuadNeighbors(gNNI);
    } else {
      if (gNumNN >= gNumCity) gNumNN = gNumCity - 1;
      gNNI = new int[gNumCity*gNumNN];
      constructNormal(gNNI);
    }

    System.out.println("Quitting constructNN()");
  }

  public void constructNormal(int[] NNI) {
    System.out.println("Entering constructNormal");
    int[] neighbors = new int[gNumCity-1];
    double[] ndist = new double[gNumCity-1];

    for (int i = 0; i < gNumCity; i++) {
      for (int j = 0, count = 0; j < gNumCity; j++)
        if (i != j) {
          neighbors[count] = j;
          ndist[count++] = dist(i, j);
        }
      // sort neighbors to "numNN"th.
      sortNeighbors(neighbors, ndist, gNumCity-1, gNumNN);

      for (int j = 0; j < gNumNN; j++)
        NNI[i*gNumNN+j] = neighbors[j];
    }
    System.out.println("Quitting constructNormal");
  }

  public void constructQuadNeighbors(int[] NNI) {
    int dir, i, j, k, t;
    int[][] nns = new int[4][];
    int[] size = new int[4], alloc = new int[4];
    double[][] ndist = new double[4][];
    System.out.println("Entering constructQuadNeighbors()");

    for (i = 0; i < 4; i++) {
      nns[i] = new int[gNumCity-1];
      ndist[i] = new double[gNumCity-1];
    }

    for (i = 0; i < gNumCity; i++) {
      for (j = 0; j < 4; j++) {
        alloc[j] = gNumNN/4;
        size[j] = 0;
      }
      for (j = 0; j < gNumCity; j++)
        if (i != j) {
          dir = (gNodeCoords[i].pt[0] < gNodeCoords[j].pt[0]) ? 0 : 1;
          dir += (gNodeCoords[i].pt[1] < gNodeCoords[j].pt[1]) ? 0 : 2;
          nns[dir][size[dir]] = j;
          ndist[dir][size[dir]++] = dist(i, j);
        }
      // If size[?] < alloc[?], adjust size[*] and alloc[*]
      for (j = 0; j < 4; j++) {
        if (size[j] < alloc[j])
          while (size[j] != alloc[j]) {
            alloc[j]--;
            dir = alloc[j] % 4;
            while (size[dir] <= alloc[dir]) dir = (dir+1) % 4;
            alloc[dir]++;
          }
      }
      // assert(alloc[0] + alloc[1] + alloc[2] + alloc[3] == gNumNN);

      // sort neighbors to "alloc[i]"th. and combine...
      t = 0;
      for (j = 0; j < 4; j++) {
        sortNeighbors(nns[j], ndist[j], size[j], alloc[j]);
        for (k = 0; k < alloc[j]; k++) {
          nns[0][t] = nns[j][k];
          ndist[0][t++] = ndist[j][k];
        }
      }
      // assert(t == gNumNN);
      sortNeighbors(nns[0], ndist[0], gNumNN, gNumNN);
      for (j = 0; j < gNumNN; j++)
        NNI[i*gNumNN+j] = nns[0][j];
    }
    System.out.println("Quitting constructQuadNeighbors()");
  }

  public void sortNeighbors(int[] neighbors, double[] ndist, int size, int numNN) {
    int mini, j, k, t;
    int left, right, prev_right;
    double pivot, doublet;
    // int count = 0; double swap_ratio;

    // assert(size >= numNN);
    if (size == 0 || size == 1) return; // A sort is not needed

    // Quick sort variants
    pivot = 0; left = 0; prev_right = size; right = size;
    while (right > numNN) {
      // save right
      prev_right = right--;
      // calc. pivot value
      pivot = (ndist[0] + ndist[right/4] + ndist[right/2] +
              ndist[(right*3)/4] + ndist[right]) / 5;
      while (left < right) {
        while (ndist[left] < pivot - EPS) left++;
        while (ndist[right] >= pivot - EPS) right--;

        if (left < right) {
          t = neighbors[left];   neighbors[left] = neighbors[right];
          neighbors[right] = t;
          doublet = ndist[left]; ndist[left] = ndist[right];
          ndist[right] = doublet;
          // count++;
        }
      }
      left = 0; right++;
    } // end of quick sort

    // we have the interest only betwen 0 and prev_right
    size = prev_right;

    // Selection sort
    for (j = 0; j < numNN; j++) {
      mini = j;
      for (k = j+1; k < size; k++)
        if (ndist[k] < ndist[mini] - EPS)
          mini = k;
      t = neighbors[mini];  neighbors[mini] = neighbors[j];
      neighbors[j] = t;
      doublet = ndist[mini];  ndist[mini] = ndist[j];
      ndist[j] = doublet;
      // count++;
    }  // end of selection sort
    // System.out.println("prev_right= " + size +
    //                     ", ratio= " + (float)count/(float)gNumCity);

  }

  public double dist(int c1, int c2) {
    return gDistMat[Math.max(c1,c2)*(Math.max(c1,c2)-1)/2 + Math.min(c1,c2)];
  }

}
