package actors.twitter;

/**
 * Protocols used by {@link TweetProcessorActor}
 *
 * @author Hein Min Htike
 */
public class TweetProcessorProtocol {

  public static class ActorStatus {
    private boolean getResponse;

    public ActorStatus(boolean getResponse) {
      this.getResponse = getResponse;
    }

    public boolean isGetResponse() {
      return getResponse;
    }

    public void setGetResponse(boolean getResponse) {
      this.getResponse = getResponse;
    }
  }

  public static class RunActor {
    private boolean getResponse;

    public RunActor(boolean getResponse) {
      this.getResponse = getResponse;
    }

    public boolean isGetResponse() {
      return getResponse;
    }

    public void setGetResponse(boolean getResponse) {
      this.getResponse = getResponse;
    }
  }
}
