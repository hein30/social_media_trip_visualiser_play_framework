package mongo;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.typesafe.config.ConfigFactory;

public class MorphiaHelper {

  private static final Datastore ds;

  static {
    ds = initDatastore();
  }

  private static Datastore initDatastore() {
    Morphia morphia = new Morphia();

    MongoClient mongoClient = new MongoClient(ConfigFactory.load().getString("mongodb.host"),
        ConfigFactory.load().getInt("mongodb.port"));

    return morphia.createDatastore(mongoClient, ConfigFactory.load().getString("mongodb.database"));
  }

  private MorphiaHelper() {
    // private constructor. This is a singleton class.
  }

  public static Datastore getDatastore() {
    return ds;
  }

}
