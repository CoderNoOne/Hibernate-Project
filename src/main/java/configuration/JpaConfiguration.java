package configuration;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JpaConfiguration {

  private static JpaConfiguration ourInstance = new JpaConfiguration();
  private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("HBN");

  public static JpaConfiguration getInstance() {
    return ourInstance;
  }

  private JpaConfiguration() {
  }

  public EntityManagerFactory getEntityManagerFactory() {
    return entityManagerFactory;
  }
}
