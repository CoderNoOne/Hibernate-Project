package repository.impl;

import domain.Shop;
import exception.AppException;
import repository.abstract_repository.base.AbstractCrudRepository;
import repository.abstract_repository.entity.ShopRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Optional;

public class ShopRepositoryImpl extends AbstractCrudRepository<Shop, Long> implements ShopRepository {

  @Override
  public Optional<Shop> findShopByNameAndCountry(String name, Long countryId) {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    Optional<Shop> item = Optional.empty();

    try {
      tx.begin();
      item = entityManager
              .createQuery("select e from " + entityType.getSimpleName() + " e where e.name = :name and e.country_id = :countryId", entityType)
              .setParameter("name", name)
              .setParameter("countryId", countryId)
              .getResultList()
              .stream()
              .findFirst();
      tx.commit();
    } catch (Exception e) {
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException("find shop by name and country- exception");
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }

    return item;
  }
}

