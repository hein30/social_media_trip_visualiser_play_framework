package models.footprints;

import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.time.DateUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import mongo.MorphiaHelper;

/**
 * Method to keep track of current extraction statuses.
 */
@Entity(value = "footprints")
public class FlickrUserNamesBotFootPrint {

  @Id
  private ObjectId id;
  private int page;
  private Date minUploadDate;

  public FlickrUserNamesBotFootPrint() {
    page = 1;
    minUploadDate = Date.from(new GregorianCalendar(2017, 0, 1).toInstant());
  }

  public void save() {
    MorphiaHelper.getDatastore().save(this);
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public Date getMinUploadDate() {
    return minUploadDate;
  }

  public void setMinUploadDate(Date minUploadDate) {
    this.minUploadDate = minUploadDate;
  }

  public Date getMaxUploadDate() {
    return DateUtils.addDays(minUploadDate, 1);
  }

  /**
   * Add one day to the min upload date and reset the page number.
   */
  public void increamentDate() {
    minUploadDate = DateUtils.addDays(minUploadDate, 1);
    page = 1;
  }

  public void incrementPageNumber() {
    page++;
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }
}
