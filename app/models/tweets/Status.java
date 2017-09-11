package models.tweets;

import java.util.Date;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import com.flickr4java.flickr.photos.Photo;

import models.trip.GeoLocation;
import mongo.MorphiaHelper;

/**
 * Represents a twitter status.
 *
 * @author Hein Min Htike
 */
@Entity(value = "socialMediaPosts")
public class Status {

  static {
    MorphiaHelper.ensureIndex(Status.class);
  }

  @Id
  private String id;
  private String userId;
  private String screenName;
  @Indexed
  private Date createdAt;
  private GeoLocation geoLocation;
  private boolean endPointUsed;
  private boolean startPointUsed;
  private Source source;

  public Status() {
    // default constructor
  }

  public Status(twitter4j.Status status) {
    this.id = String.valueOf(status.getId());
    this.userId = String.valueOf(status.getUser().getId());
    this.screenName = String.valueOf(status.getUser().getScreenName());
    this.createdAt = status.getCreatedAt();
    this.geoLocation = new GeoLocation(status.getGeoLocation());
    this.source = Source.TWITTER;
  }

  public Status(Photo photo) {
    this.id = String.valueOf(photo.getId());
    this.userId = String.valueOf(photo.getOwner().getId());
    this.screenName = userId;
    this.createdAt = photo.getDateTaken();
    this.geoLocation =
        new GeoLocation(photo.getGeoData().getLatitude(), photo.getGeoData().getLongitude());
    this.source = Source.FLICKR;
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

  public boolean isEndPointUsed() {
    return endPointUsed;
  }

  public void setEndPointUsed(boolean endPointUsed) {
    this.endPointUsed = endPointUsed;
  }

  public boolean isStartPointUsed() {
    return startPointUsed;
  }

  public void setStartPointUsed(boolean startPointUsed) {
    this.startPointUsed = startPointUsed;
  }

  public Source getSource() {
    return source;
  }

  public void setSource(Source source) {
    this.source = source;
  }
}
