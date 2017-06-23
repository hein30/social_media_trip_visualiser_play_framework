package models.tweets;

import java.util.List;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import mongo.MorphiaHelper;

@Entity(value = "tweets")
public class Tweet {

  @Id
  private String id;
  private String name;

  public void save() {
    MorphiaHelper.getDatastore().save(this);
  }

  public List<Tweet> query() {
    return MorphiaHelper.getDatastore().createQuery(Tweet.class).asList();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


}
