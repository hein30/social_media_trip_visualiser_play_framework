package mongo;

import java.util.Collections;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.typesafe.config.ConfigFactory;

import play.Logger;

public class MorphiaHelper {

  private static final Datastore ds;
  private static final Logger.ALogger LOGGER = Logger.of(MorphiaHelper.class);

  static {
    ds = initDatastore();
  }

  private MorphiaHelper() {
    // private constructor. This is a singleton class.
  }

  private static Datastore initDatastore() {
    final String mongoHost = ConfigFactory.load().getString("mongodb.host");
    final int mongoPort = ConfigFactory.load().getInt("mongodb.port");
    final String mongoDb = ConfigFactory.load().getString("mongodb.db");
    final String mongoUser = ConfigFactory.load().getString("mongodb.user");
    final String mongoPw = ConfigFactory.load().getString("mongodb.pw");

    MongoCredential cred =
        MongoCredential.createCredential(mongoUser, mongoDb, mongoPw.toCharArray());

    ServerAddress address = new ServerAddress(mongoHost, mongoPort);

    MongoClient client =
        new MongoClient(Collections.singletonList(address), Collections.singletonList(cred));

    Morphia morphia = new Morphia();
    return morphia.createDatastore(client, mongoDb);
  }

  public static Datastore getDatastore() {
    return ds;
  }

  public static void ensureIndex(Class clazz) {
    ds.ensureIndexes(clazz);
  }

}
