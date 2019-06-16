package repository.impl;

import domain.Product;
import domain.Shop;
import domain.Stock;
import exception.AppException;
import repository.abstract_repository.base.AbstractCrudRepository;
import repository.abstract_repository.entity.StockRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Optional;

public class StockRepositoryImpl extends AbstractCrudRepository<Stock, Long> implements StockRepository {

  @Override
  public Optional<Stock> findStockByShopAndProduct(Shop shop, Product product) {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    Optional<Stock> item = Optional.empty();

    try {
      tx.begin();
      item = entityManager
              .createQuery("select e from " + entityType.getSimpleName() + " as e where e.shop.name = :shopName and e.product.name = :productName", entityType)
              .setParameter("shopName", shop.getName())
              .setParameter("productName", product.getName())
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