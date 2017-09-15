package models.socialmedia;

/**
 * Created by Hein Min Htike on 6/26/2017.
 */
public class RateLimitException extends Exception {

  public RateLimitException() {
    super();
  }

  public RateLimitException(String message) {
    super(message);
  }
}
