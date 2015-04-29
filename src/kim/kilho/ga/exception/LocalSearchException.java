package kim.kilho.ga.exception;

/**
 * Exception when local search algorithm causes errors.
 * @author Kilho Kim
 */
public class LocalSearchException extends GAException {
  public LocalSearchException() { super(); }

  public LocalSearchException(String msg) {
    super(msg);
  }
}
