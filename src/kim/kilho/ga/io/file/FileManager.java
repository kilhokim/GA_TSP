package kim.kilho.ga.io.file;

import kim.kilho.ga.exception.InvalidInputException;

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
  private int numPoints;

  /**
   * Receive input from file.
   * @param fileName
   * @return FIXME: File
   */
  public File read(String fileName)
          throws IOException, InvalidInputException {
    int i;  // iteration variable
    String[] coordinates;  // save line which is read during iteration as coordinates
    BufferedReader bis = null;

    // Generate a new file reader
    // throw IOException for the following line:
    bis = new BufferedReader(new FileReader(fileName));

    // throw NumberFormatException for the following line:
    try {
      numPoints = Integer.parseInt(bis.readLine());
    } catch (NumberFormatException e) {
      throw new InvalidInputException("Invalid input for the number of points.");
    }

    for (i = 0; i < numPoints; i++) {
      coordinates = bis.readLine().split(" ");
      if (coordinates.length != 2)
        throw new InvalidInputException("Invalid input for point coordinates");


    }






    return null;
  }

  /**
   *
   * @param fileName
   */
  public void write(String fileName) {

  }



}
