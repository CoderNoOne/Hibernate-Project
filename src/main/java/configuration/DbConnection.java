package configuration;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DbConnection {

  private static DbConnection ourInstance = new DbConnection();
  private static  EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("HBN");

  public static DbConnection getInstance() {
    return ourInstance;
  }

  private DbConnection() {
  }

  public EntityManagerFactory getEntityManagerFactory() {
    return entityManagerFactory;
  }

  public static void close(){

    if(entityManagerFactory != null){
      entityManagerFactory.close();
    }
  }
}
