package services.trips;

/**
 * Protocols used by {@link TripProcessorActor}
 *
 * @author Hein Min Htike
 */
public class TripProcessorProtocol {

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
