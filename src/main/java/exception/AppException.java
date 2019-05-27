package exception;

public class AppException extends RuntimeException {

  private String message;

  public AppException(String message) {
    this.message = message;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
