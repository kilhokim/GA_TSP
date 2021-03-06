package kim.kilho.ga.exception;

/**
 * Exception when selection algorithm causes errors.
 * @author Kilho Kim
 */
public class CrossoverException extends GAException {
  public CrossoverException() { super(); }

  public CrossoverException(String msg) {
    super(msg);
  }

}
