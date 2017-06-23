package mongo;

import java.util.Collections;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
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

    MongoCredential cred = MongoCredential.createCredential("hein", "zz", "haha".toCharArray());

    ServerAddress address = new ServerAddress("host", 123);

    MongoClient anotherCl =
        new MongoClient(Collections.singletonList(address), Collections.singletonList(cred));

    return morphia.createDatastore(mongoClient, ConfigFactory.load().getString("mongodb.database"));
  }

  private MorphiaHelper() {
    // private constructor. This is a singleton class.
  }

  public static Datastore getDatastore() {
    return ds;
  }

}
