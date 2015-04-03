package kim.kilho.ga.exception;

/**
 * Exception when receiving invalid input for GA.
 * @author Kilho Kim
 */
public class InvalidInputException extends GAException {
  public InvalidInputException() {
  }

  public InvalidInputException(String msg) {
    super(msg);
  }
}
