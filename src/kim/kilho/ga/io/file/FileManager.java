package kim.kilho.ga.io.file;

import kim.kilho.ga.exception.InvalidInputException;
import kim.kilho.ga.exception.PathException;
import kim.kilho.ga.gene.Path;
import kim.kilho.ga.gene.Point;

import java.io.*;
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
    BufferedReader br = null;
    Point[] points;
    Object[] output = new Object[2];

    // Generate a new file reader
    // throw IOException for the following line:
    br = new BufferedReader(new FileReader(fileName));

    // Get the total number of points from the first line,
    try {
      // catch & throw NumberFormatException for the following line:
      numPoints = Integer.parseInt(br.readLine());
    } catch (NumberFormatException e) {
      throw new InvalidInputException("Invalid input for the number of points.");
    }
    if (numPoints < 1 || numPoints > maxN) {
      throw new InvalidInputException("Invalid input for the number of points.");
    }

    // Get every point from each lines and add it to path,
    points = new Point[numPoints];
    for (i = 0; i < numPoints; i++) {
      coordinates = br.readLine().split(" ");
      // if coordinates' dimension isn't two, throw InvalidInputException.
      if (coordinates.length != 2)
        throw new InvalidInputException("Invalid input for point coordinates");
      points[i] = new Point(Double.parseDouble(coordinates[0]),
                         Double.parseDouble(coordinates[1]));
    }

    // Get the total available time from the last line,
    try {
      // catch & throw NumberFormatException for the following line:
      availableTime = Double.parseDouble(br.readLine());
    } catch (NumberFormatException e) {
      throw new InvalidInputException("Invalid input for the available time.");
    }
    br.close();

    output[0] = points;
    output[1] = availableTime;

    return output;
  }

  /**
   * Write the best result to file.
   * @param inputFileName
   */
  public void write(String inputFileName, Path path) throws IOException {
    // Building the output filename from the input filename.
    StringBuilder outputFileName = new StringBuilder();
    String[] inputFilePaths = inputFileName.split("/");
    String[] inputFileNames = inputFilePaths[inputFilePaths.length-1]
                                .split("\\.");
    for (int i = 0; i < inputFilePaths.length-1; i++) {
      outputFileName.append(inputFilePaths[i]);
      outputFileName.append("/");
    }
    outputFileName.append(inputFileNames[0]);
    outputFileName.append(".out");
    if (inputFileNames.length > 2) {
      for (int i = 2; i < inputFileNames.length; i++) {
        outputFileName.append(".");
        outputFileName.append(inputFileNames[i]);
      }
    }

    BufferedWriter bw = null;

    // Generate a new file writer
    // throw IOException for the following line:
    bw = new BufferedWriter(new FileWriter(outputFileName.toString()));
    bw.write(path.toString());
    bw.close();
  }

}
