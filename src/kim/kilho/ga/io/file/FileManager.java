package kim.kilho.ga.io.file;

import kim.kilho.ga.exception.InvalidInputException;
import kim.kilho.ga.exception.PathException;
import kim.kilho.ga.gene.Path;
import kim.kilho.ga.gene.Point;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;

/**
 * The manager class which provides the following functions:
 * 1. Receiving input from file.
 * 2. Producing output as file.
 * @author Kilho Kim
 */
public class FileManager {

  public FileManager() {
    // Do nothing.
  }

  /**
   * Receive input from file.
   * @param fileName
   * @return Path
   * @throws IOException
   * @throws InvalidInputException
   * @throws PathException
   */
  public Object[] read(String fileName, int maxN)
          throws IOException, InvalidInputException, PathException {
    int numPoints;
    double availableTime;
    int i;  // iteration variable
    String[] coordinates;  // save line which is read during iteration as coordinates
    BufferedReader bis = null;
    Point[] points;
    Object[] output = new Object[2];

    // Generate a new file reader
    // throw IOException for the following line:
    bis = new BufferedReader(new FileReader(fileName));

    // Get the total number of points from the first line,
    try {
      // catch & throw NumberFormatException for the following line:
      numPoints = Integer.parseInt(bis.readLine());
    } catch (NumberFormatException e) {
      throw new InvalidInputException("Invalid input for the number of points.");
    }
    if (numPoints < 1 || numPoints > maxN) {
      throw new InvalidInputException("Invalid input for the number of points.");
    }

    // Get every point from each lines and add it to path,
    points = new Point[numPoints];
    for (i = 0; i < numPoints; i++) {
      coordinates = bis.readLine().split(" ");
      // if coordinates' dimension isn't two, throw InvalidInputException.
      if (coordinates.length != 2)
        throw new InvalidInputException("Invalid input for point coordinates");
      points[i] = new Point(Double.parseDouble(coordinates[0]),
                         Double.parseDouble(coordinates[1]));
    }

    // Get the total available time from the last line,
    try {
      // catch & throw NumberFormatException for the following line:
      availableTime = Double.parseDouble(bis.readLine());
    } catch (NumberFormatException e) {
      throw new InvalidInputException("Invalid input for the available time.");
    }

    output[0] = points;
    output[1] = availableTime;

    return output;
  }

  /**
   *
   * @param fileName
   */
  public void write(String fileName) {
    // TODO: Complete implementing write method
  }

}
