package repository.impl;

import domain.Trade;
import exception.AppException;
import repository.abstract_repository.base.AbstractCrudRepository;
import repository.abstract_repository.entity.TradeRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Optional;

public class TradeRepositoryImpl extends AbstractCrudRepository<Trade, Long> implements TradeRepository {

  @Override
  public void deleteTradeByName(String name) {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    findTradeByName(name).ifPresentOrElse(trade -> {
      tx.begin();
      entityManager.remove(entityManager.merge(trade));
      tx.commit();
    }, () -> {
      throw new AppException("Trade you wanted to delete: " + name + " doesnt exist in DB");
    });
  }

  @Override
  public Optional<Trade> findTradeByName(String name) {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    Optional<Trade> item = Optional.empty();

    try {
      tx.begin();
      item = entityManager
              .createQuery("select e from " + entityType.getSimpleName() + " as e where e.name = :name", entityType)
              .setParameter("name", name)
              .getResultList()
              .stream()
              .findFirst();
      tx.commit();
    } catch (Exception e) {
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException("find trade by name - exception");
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }
    return item;
  }
}

