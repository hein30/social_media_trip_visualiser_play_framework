package models.tweets;

import java.util.List;

import org.mongodb.morphia.annotations.Entity;

import mongo.MorphiaHelper;

@Entity(value = "tweets")
public class Tweet {

  private String name;

  public void save() {
    MorphiaHelper.getDatastore().save(this);
  }

  public List<Tweet> query() {
    return MorphiaHelper.getDatastore().createQuery(Tweet.class).asList();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
