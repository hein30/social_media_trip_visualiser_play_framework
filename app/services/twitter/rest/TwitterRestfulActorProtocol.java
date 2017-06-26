package services.twitter.rest;

/**
 * Created by Hein Min Htike on 6/26/2017.
 */
public class TwitterRestfulActorProtocol {

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
