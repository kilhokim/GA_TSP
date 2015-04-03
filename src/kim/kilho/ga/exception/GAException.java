package kim.kilho.ga.exception;

/**
 * RuntimeException class only for Genetics Algorithm.
 * @author Kilho Kim
 */
public class GAException extends RuntimeException {
  public GAException() {
    super();
  }

  public GAException(String msg) {
    super(msg);
  }
}
