package models.tweets;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import models.geography.BoundingBox;
import org.mongodb.morphia.annotations.Id;

import com.google.common.collect.Lists;

import mongo.MorphiaHelper;

/**
 * Represents a social network user. To be extended by specific APIs' user objects.
 *
 * @author Hein Min Htike
 */

public abstract class SocialNetworkUser {

  @Id
  private String id;
  private String userName;
  private List<BoundingBox> boundingBoxes;

  public SocialNetworkUser() {
    // default constructor for morphia.
    boundingBoxes = new ArrayList<BoundingBox>();
  }

  // save new user only if we don't have this user in database.
  public void save() {
    if (!existsInDatabase()) {
      MorphiaHelper.getDatastore().save(this);
    }
  }

  public void update() {
    MorphiaHelper.getDatastore().save(this);
  }

  public abstract boolean existsInDatabase();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public List<BoundingBox> getBoundingBoxes() {
    return boundingBoxes;
  }

  public void setBoundingBoxes(List<BoundingBox> boundingBoxes) {
    this.boundingBoxes = boundingBoxes;
  }

  protected List<BoundingBox> buildBoundingBoxes(List<Double> pointsList) {
    return Lists.partition(pointsList, 4).stream().map(this::buildOneBoundingBox)
        .collect(Collectors.toList());
  }

  protected BoundingBox buildOneBoundingBox(List<Double> box) {
    return new BoundingBox(box.get(0), box.get(1), box.get(2), box.get(3));
  }
}
