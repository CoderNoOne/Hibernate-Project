package repository.impl;

import domain.Country;
import exception.AppException;
import repository.abstract_repository.base.AbstractCrudRepository;
import repository.abstract_repository.entity.CountryRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Optional;

public class CountryRepositoryImpl extends AbstractCrudRepository<Country, Long> implements CountryRepository {


  @Override
  public void deleteCountryByName(String name) {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    findCountryByName(name).ifPresentOrElse(country -> {
      tx.begin();
      entityManager.remove(entityManager.merge(country));
      tx.commit();
    }, () -> {
      throw new AppException("Customer you wanted to delete: " + name + " doesnt exist in DB");
    });
  }

  public Optional<Country> findCountryByName(String name) {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    Optional<Country> item = Optional.empty();

    try {
      tx.begin();
      item = entityManager
              .createQuery("select e from " + entityType.getSimpleName() + " e where e.name = :name", entityType)
              .setParameter("name", name)
              .getResultList()
              .stream()
              .findFirst();
      tx.commit();
    } catch (Exception e) {
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException("find country by name - exception");
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }

    return item;
  }

}
