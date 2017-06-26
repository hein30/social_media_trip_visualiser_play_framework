package models.tweets;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.mongodb.morphia.annotations.Entity;

import com.google.common.collect.Lists;

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

  public TwitterUser(String id, String screenName, List<Double> pointsList) {
    setId(id);
    setUserName(screenName);
    setBoundingBoxes(getBoundingBoxes(pointsList));
    nextProcessTime = new Date();
  }

  public TwitterUser(twitter4j.Status status, List<Double> pointsList) {
    final User user = status.getUser();
    setId(String.valueOf((user.getId())));
    setUserName(user.getScreenName());
    setBoundingBoxes(getBoundingBoxes(pointsList));
    nextProcessTime = new Date();
  }

  private List<BoundingBox> getBoundingBoxes(List<Double> pointsList) {
    return Lists.partition(pointsList, 4).stream().map(this::createBoundingBox)
        .collect(Collectors.toList());
  }

  private BoundingBox createBoundingBox(List<Double> box) {
    return new BoundingBox(box.get(0), box.get(1), box.get(2), box.get(3));
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
