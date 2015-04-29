package kim.kilho.ga.exception;

/**
 * Exception when selection algorithm causes errors.
 * @author Kilho Kim
 */
public class SelectionException extends GAException {
  public SelectionException() { super(); }

  public SelectionException(String msg) {
    super(msg);
  }

}
