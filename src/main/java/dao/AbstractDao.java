package dao;

import configuration.EntityManagerWrapper;
import configuration.JpaConfiguration;
import domain.Error;
import exception.AppException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDao<T> implements CrudDao<T> {

  private Class<T> type = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
  private EntityManagerFactory entityManagerFactory = JpaConfiguration.getInstance().getEntityManagerFactory();


  @Override
  public boolean add(T t) {

    if (t == null) {
      insertError(new Error.ErrorBuilder().date(LocalDate.now()).message(";").build);
      throw new AppException("Id is null");
    }

    EntityManagerWrapper entityManagerWrapper = new EntityManagerWrapper(entityManagerFactory);
    EntityTransaction tx = null;
    boolean isAdded = true;

    try {
      EntityManager entityManager = entityManagerWrapper.getEntityManager();
      tx = entityManager.getTransaction();

      tx.begin();
      entityManager.persist(t);
      tx.commit();

    } catch (Exception e) {
      isAdded = false;

      Error error = new Error.ErrorBuilder().message(";").date(LocalDate.now()).build();
      insertError(error);

      if (tx != null) {
        tx.rollback();
      }
    }

    return isAdded;
  }

  @Override
  public void update(T t) {


  }

  @Override
  public void delete(Integer id) {

    if (id == null) {
      insertError(new Error.ErrorBuilder().date(LocalDate.now()).message(";").build);
      throw new AppException("Id is null");
    }

    EntityManagerWrapper entityManagerWrapper = new EntityManagerWrapper(entityManagerFactory);
    EntityTransaction tx = null;

    try (entityManagerWrapper) {
      EntityManager entityManager = entityManagerWrapper.getEntityManager();
      tx = entityManager.getTransaction();

      tx.begin();
      T toDelete = entityManager.find(type, id);
      entityManager.remove(toDelete);
      tx.commit();

    } catch (Exception e) {
      Error error = new Error.ErrorBuilder().message(";").date(LocalDate.now()).build();
      insertError(error);
      if (tx != null) {
        tx.rollback();
      }
    }
  }

  @Override
  public Optional<T> findById(Integer id) {

    if (id == null) {
      insertError(new Error.ErrorBuilder().date(LocalDate.now()).message(";").build);
      throw new AppException("Id is null");
    }

    EntityManagerWrapper entityManagerWrapper = new EntityManagerWrapper(entityManagerFactory);
    EntityTransaction tx = null;
    try (entityManagerWrapper) {
      EntityManager entityManager = entityManagerWrapper.getEntityManager();
      tx = entityManager.getTransaction();

      tx.begin();
      Optional<T> t = Optional.ofNullable(entityManager.find(type, id));
      tx.commit();

      return t;

    } catch (Exception e) {
      Error error = new Error.ErrorBuilder().message(";").date(LocalDate.now()).build();
      insertError(error);
      if (tx != null) {
        tx.rollback();
      }
    }
    return Optional.empty();
  }

  @Override
  public List<T> findAll() {

    EntityManagerWrapper entityManagerWrapper = new EntityManagerWrapper(entityManagerFactory);
    EntityTransaction tx = null;

    try (entityManagerWrapper) {
      EntityManager entityManager = entityManagerWrapper.getEntityManager();
      tx = entityManager.getTransaction();

      tx.begin();
      List<T> resultList = entityManager.createQuery("select * from " + type, type).getResultList();
      tx.commit();

      return resultList;
    } catch (Exception e) {

      Error error = new Error.ErrorBuilder().message(";").date(LocalDate.now()).build();
      insertError(error);
      if (tx != null) {
        tx.rollback();
      }
    }
    return Collections.emptyList();
  }

  @Override
  public void deleteAll() {

    EntityManagerWrapper entityManagerWrapper = new EntityManagerWrapper(entityManagerFactory);
    EntityTransaction tx = null;

    try (entityManagerWrapper) {
      EntityManager entityManager = entityManagerWrapper.getEntityManager();
      tx = entityManager.getTransaction();
      tx.begin();
      entityManager.createQuery("delete from " + type).executeUpdate();
      tx.commit();

    } catch (Exception e) {
      Error error = new Error.ErrorBuilder().message(";").date(LocalDate.now()).build();
      insertError(error);
      if (tx != null) {
        tx.rollback();
      }
    }
  }

  private void insertError(Error error) {

    EntityManagerWrapper entityManagerWrapper = new EntityManagerWrapper(entityManagerFactory);
    EntityTransaction tx = null;

    try (entityManagerWrapper) {
      EntityManager entityManager = entityManagerWrapper.getEntityManager();
      tx = entityManager.getTransaction();
      tx.begin();
      entityManager.persist(error);
      tx.commit();

    } catch (Exception e) {
      if (tx != null) {
        tx.rollback();
      }
    }
  }
}
