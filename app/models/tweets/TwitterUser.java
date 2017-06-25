package models.tweets;

import java.util.Date;

/**
 * Created by Hein Min Htike on 6/25/2017.
 */
public class TwitterUser extends SocialNetworkUser {

  private String lastTweetId;
  private Date lastProcessed;

  public TwitterUser() {
    super();
    lastProcessed = new Date(0l);
  }

  public String getLastTweetId() {
    return lastTweetId;
  }

  public void setLastTweetId(String lastTweetId) {
    this.lastTweetId = lastTweetId;
  }

  public Date getLastProcessed() {
    return lastProcessed;
  }

  public void setLastProcessed(Date lastProcessed) {
    this.lastProcessed = lastProcessed;
  }
}
