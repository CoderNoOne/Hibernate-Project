package repository.impl;

import domain.Shop;
import exception.AppException;
import repository.abstract_repository.base.AbstractCrudRepository;
import repository.abstract_repository.entity.ShopRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShopRepositoryImpl extends AbstractCrudRepository<Shop, Long> implements ShopRepository {

  @Override
  public List<Shop> findShopListByName(String name) {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    List<Shop> shopList = new ArrayList<>();

    try {
      tx.begin();
      shopList = entityManager
              .createQuery("select e from " + entityType.getSimpleName() + " as e where e.name = :name", entityType)
              .setParameter("name", name)
              .getResultList();
      tx.commit();
    } catch (Exception e) {
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException("find shop by name and country - exception");
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }
    return shopList;
  }


  @Override
  public Optional<Shop> findShopByNameAndCountry(String name, String countryName) {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    Optional<Shop> item = Optional.empty();

    try {
      tx.begin();
      item = entityManager
              .createQuery("select e from " + entityType.getSimpleName() + " e where e.name = :name and e.country.name = :countryName", entityType)
              .setParameter("name", name)
              .setParameter("countryName", countryName)
              .getResultList()
              .stream()
              .findFirst();
      tx.commit();
    } catch (Exception e) {
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException("find shop by name and country - exception");
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }

    return item;
  }
}

