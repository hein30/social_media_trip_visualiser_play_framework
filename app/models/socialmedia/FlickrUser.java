package models.socialmedia;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.mongodb.morphia.annotations.Entity;

import mongo.MorphiaHelper;

/**
 * Created by Hein Min Htike on 7/18/2017.
 */
@Entity(value = "flickrUsers")
public class FlickrUser extends SocialNetworkUser {
  private Date minUploadDate;
  private int pageNumber;
  private boolean processed;

  public FlickrUser() {
    // default for morphia
  }

  public FlickrUser(String id, List<Double> pointsList) {
    setId(id);
    setBoundingBoxes(buildBoundingBoxes(pointsList));
    minUploadDate = Date.from(new GregorianCalendar(2017, 0, 1).toInstant());
    pageNumber = 1;
  }

  @Override
  public boolean existsInDatabase() {
    return MorphiaHelper.getDatastore().createQuery(FlickrUser.class).field("id").equal(getId())
        .count() > 0;
  }

  public Date getMinUploadDate() {
    return minUploadDate;
  }

  public void setMinUploadDate(Date minUploadDate) {
    this.minUploadDate = minUploadDate;
  }

  public void incrementPageNumber() {
    pageNumber++;
  }

  public int getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(int pageNumber) {
    this.pageNumber = pageNumber;
  }

  public boolean isProcessed() {
    return processed;
  }

  public void setProcessed(boolean processed) {
    this.processed = processed;
  }
}
