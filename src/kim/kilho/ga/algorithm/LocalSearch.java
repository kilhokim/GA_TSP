package kim.kilho.ga.algorithm;

import kim.kilho.ga.exception.LocalSearchException;
import kim.kilho.ga.gene.Path;
import kim.kilho.ga.gene.Point;

/**
 * Local Search algorithms for GA.
 * @author Kilho Kim
 */
public class LocalSearch {

  /**
   * 2-change for a part of path (path, i, k).
   *   take route[0] to route[i-1] and add them in order to new route
   *   take route[i] to route[k] and add them in reverse order to new_route
   *   take route[k+1] to end and add them in order to new_route
   *   return new_route;
   *   @param p
   *   @param i
   *   @param k
   *   @return Path
   */
  public static Path twoChange(Path p, int i, int k, Point[] points) {
    if (i > k)
      throw new LocalSearchException("Invalid input: " +
                                     "i can't be higher than k");
    if (k > p.getLength()-1)
      throw new LocalSearchException("Invalid input: " +
                                     "k exceeded the index limit of path");

    Path newP = new Path(p.getLength(), false);
    int[] newPath = new int[p.getLength()];
    int j;

    // Take route[0] to route[i-1]
    // and add them in order to new route:
    for (j = 0; j < i; j++)
      newPath[j] = p.getPoint(j);

    // Take route[i] to route[k]
    // and add them in reverse order to new_route:
    for (j = i; j <= k; j++)
      newPath[j] = p.getPoint(k-(j-i));

    // Take route[k+1] to end and add them in order to new_route
    for (j = k+1; j < p.getLength(); j++)
      newPath[j] = p.getPoint(j);

    newP.setPath(newPath);
    newP.setDistance(p.getDistance());
    newP.reEvaluate(i, k, points);
    return newP;
  }

  /**
   * The complete 2-opt swap algorithm.
   * (Source: 2-Opt, Wikipedia)
   *   repeat until no improvement is made:
   *     start again:
   *     best distance = calculateTotalDistance(existing route)
   *     for (i = 0; i < number of nodes eligible to be swapped - 1; i++):
   *       for (k = i + 1; k < number of nodes eligible to be swapped; k++):
   *         new route = twoOptSwap(existing route, i, k)
   *         new distance = calculateTotalDistance(new_route)
   *         if (new distance < best distance):
   *           existing route = new_route
   *           goto start_again
   * @param p
   * @return Path
   */
  public static Path twoOpt(Path p, Point[] points,
                            long beginTime, double timeLimit) {
    Path currP = null;
    boolean improved = true;
    double bestDistance = 0, newDistance = 0;
    int i, k;

    while (improved) {
      // System.out.println("Start again 2-Opt");
      improved = false;
      bestDistance = p.getDistance();
      // TODO: Make it not to repeat from the beginning right after it gets improved
      startAgain:
        for (i = 0; i < p.getLength()-1; i++) {
          for (k = i+1; k < p.getLength(); k++) {
            // Set emergency exit for twoOpt loop
            // in case of timeover:
            if (System.currentTimeMillis()/1000 - beginTime >= timeLimit - 1)
              return p;
            currP = twoChange(p, i, k, points);
            newDistance = currP.getDistance();
            // newDistance = currP.evaluate(points);
            if (newDistance < bestDistance) {
              p = currP;
              improved = true;
              // System.out.println(p.toString());
              // System.out.println("newDistance=" + newDistance +
                                 // ", bestDistance=" + bestDistance);
              break startAgain;
            }
          }
        }
    }

    return p;
  }
}
