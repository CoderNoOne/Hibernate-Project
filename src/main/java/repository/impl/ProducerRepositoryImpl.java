package repository.impl;

import domain.Country;
import domain.Producer;
import domain.Trade;
import exception.AppException;
import repository.abstract_repository.base.AbstractCrudRepository;
import repository.abstract_repository.entity.ProducerRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Arrays;
import java.util.Optional;

public class ProducerRepositoryImpl extends AbstractCrudRepository<Producer, Long> implements ProducerRepository {

  @Override
  public Optional<Producer> findByNameAndTradeAndCountry(String name, Trade trade, Country country) {

    if (country == null) {
      throw new AppException("Producer object is null");
    }

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    Optional<Producer> item = Optional.empty();

    try {
      tx.begin();
      item = entityManager
              .createQuery("select e from " + entityType.getSimpleName() + " as e where e.name = :name and e.trade.name = :tradeName and e.country.name = :countryName", entityType)
              .setParameter("name", name)
              .setParameter("tradeName", trade.getName())
              .setParameter("countryName", country.getName())
              .getResultList()
              .stream()
              .findFirst();
      tx.commit();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      System.out.println(Arrays.toString(e.getStackTrace()));
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException("find prodcuer by name trade and country - exception");
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }

    return item;
  }
}

