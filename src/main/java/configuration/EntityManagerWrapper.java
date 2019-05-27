package configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class EntityManagerWrapper implements AutoCloseable {

  private EntityManager entityManager;

  public EntityManagerWrapper(EntityManagerFactory entityManagerFactory) {
    entityManager = entityManagerFactory.createEntityManager();
  }

  @Override
  public void close() {
    if (entityManager != null) {
      entityManager.close();
    }
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }
}
