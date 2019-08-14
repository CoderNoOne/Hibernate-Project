package repository.abstract_repository.base;

import configuration.DbConnection;
import exception.AppException;
import lombok.extern.slf4j.Slf4j;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class AbstractCrudRepository<T, Id> implements CrudRepository<T, Id> {

  protected final EntityManagerFactory entityManagerFactory = DbConnection.getInstance().getEntityManagerFactory();

  protected final Class<T> entityType = (Class<T>) (((ParameterizedType) this.getClass().getGenericSuperclass())).getActualTypeArguments()[0];
  protected final Class<Id> idType = (Class<Id>) (((ParameterizedType) this.getClass().getGenericSuperclass())).getActualTypeArguments()[1];

  @Override
  public Optional<T> add(T t) {

    if (t == null) {
      throw new AppException(entityType.getSimpleName() + ";add or updateProduct - object is null");
    }

    Optional<T> item = Optional.empty() ;

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    try {

      tx.begin();

      entityManager.persist(t);
      item = Optional.of(t);

      tx.commit();

    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));

      if (tx != null) {
        tx.rollback();
      }
      throw new AppException(entityType.getSimpleName() + ";add or updateProduct - exception");
    } finally {

      if (entityManager != null) {
        entityManager.close();
      }
    }

    return item;
  }

  @Override
  public List<T> findAll() {

    List<T> items = new ArrayList<>();
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    try {
      tx.begin();
      items = entityManager
              .createQuery("from " + entityType.getSimpleName(), entityType)
              .getResultList();
      tx.commit();
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException(entityType.getSimpleName() + ";find all - exception: " + e.getMessage());
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }

    return items;
  }

  @Override
  public Optional<T> findById(Id id) {

    if (id == null) {
      throw new AppException(entityType.getSimpleName() + ";find by id - id is null");
    }

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    Optional<T> item = Optional.empty();

    try {
      tx.begin();
      item = Optional.ofNullable(entityManager.find(entityType, id));
      tx.commit();
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException(entityType.getSimpleName() + ";find by id - exception");
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }

    return item;
  }

  @Override
  public Optional<T> deleteById(Id id) {

    if (id == null) {
      throw new AppException(entityType.getSimpleName() + ";deleteById - exeption - id is null");
    }

    Optional<T> item = Optional.empty();

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    try {
      tx.begin();
      item = entityManager
              .createQuery("select e from " + entityType.getSimpleName() + " e where e.id = :id", entityType)
              .setParameter("id", id)
              .getResultList()
              .stream()
              .findFirst()
              .flatMap(element -> {
                entityManager.remove(element);
                return Optional.ofNullable(element);
              });
      tx.commit();
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException(entityType.getSimpleName() + ";find by id - exception in deleteById method");
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }

    return item;
  }

  @Override
  public void deleteAll() {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    try {
      tx.begin();
      entityManager
              .createQuery("delete from " + entityType.getSimpleName() + " as e where e.id >= 1 ")
              .executeUpdate();
      tx.commit();
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException(entityType.getSimpleName() + ";delete all - exception in deleteAll method");
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }
  }
}
