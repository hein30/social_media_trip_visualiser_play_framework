package models.tweets;

import java.util.Date;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import mongo.MorphiaHelper;

/**
 * Represents a twitter status.
 *
 * @author Hein Min Htike
 */
@Entity(value = "twitterStatues")
public class Status {

  @Id
  private String id;
  private String userId;
  private String screenName;
  private Date createdAt;
  private GeoLocation geoLocation;
  private boolean isGeoEnabled;

  public Status() {
    // default constructor
  }

  public Status(twitter4j.Status status) {
    this.id = String.valueOf(status.getId());
    this.userId = String.valueOf(status.getUser().getId());
    this.screenName = String.valueOf(status.getUser().getScreenName());
    this.createdAt = status.getCreatedAt();
    this.geoLocation = new GeoLocation(status.getGeoLocation());
    this.isGeoEnabled = status.getUser().isGeoEnabled();
  }

  public void save() {
    MorphiaHelper.getDatastore().save(this);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getScreenName() {
    return screenName;
  }

  public void setScreenName(String screenName) {
    this.screenName = screenName;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public GeoLocation getGeoLocation() {
    return geoLocation;
  }

  public void setGeoLocation(GeoLocation geoLocation) {
    this.geoLocation = geoLocation;
  }

  public boolean isGeoEnabled() {
    return isGeoEnabled;
  }

  public void setGeoEnabled(boolean geoEnabled) {
    isGeoEnabled = geoEnabled;
  }
}
