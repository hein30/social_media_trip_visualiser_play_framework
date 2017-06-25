package models.tweets;

import org.mongodb.morphia.annotations.Id;

import mongo.MorphiaHelper;

/**
 * Represents a social network user. To be extended by specific APIs' user objects.
 *
 * @author Hein Min Htike
 */

public class SocialNetworkUser {

  @Id
  private String id;
  private String userName;

  public SocialNetworkUser() {
    // default constructor for morphia.
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

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }
}
