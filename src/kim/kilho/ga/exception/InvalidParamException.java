package kim.kilho.ga.exception;

/**
 * Exception when call a method with invalid param(s).
 * @author Kilho Kim
 */
public class InvalidParamException extends GAException {
  public InvalidParamException() {
  }

  public InvalidParamException(String msg) { super(msg); }
}
