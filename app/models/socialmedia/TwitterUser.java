package models.socialmedia;

import java.util.Date;
import java.util.List;

import org.mongodb.morphia.annotations.Entity;

import mongo.MorphiaHelper;
import twitter4j.User;

/**
 * Created by Hein Min Htike on 6/25/2017.
 */
@Entity(value = "twitterUsers")
public class TwitterUser extends SocialNetworkUser {

  private String lastTweetId;
  private Date nextProcessTime;

  public TwitterUser() {
    // default constructor for morphia.
  }

  public TwitterUser(twitter4j.Status status, List<Double> pointsList) {
    final User user = status.getUser();
    setId(String.valueOf((user.getId())));
    setUserName(user.getScreenName());
    setBoundingBoxes(buildBoundingBoxes(pointsList));
    nextProcessTime = new Date();
  }

  @Override
  public boolean existsInDatabase() {
    return MorphiaHelper.getDatastore().createQuery(TwitterUser.class).field("id").equal(getId())
        .count() > 0;
  }

  public String getLastTweetId() {
    return lastTweetId;
  }

  public void setLastTweetId(String lastTweetId) {
    this.lastTweetId = lastTweetId;
  }

  public Date getNextProcessTime() {
    return nextProcessTime;
  }

  public void setNextProcessTime(Date nextProcessTime) {
    this.nextProcessTime = nextProcessTime;
  }
}
