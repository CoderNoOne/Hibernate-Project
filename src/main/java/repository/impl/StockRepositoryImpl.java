package repository.impl;

import domain.CustomerOrder;
import domain.Product;
import domain.Shop;
import domain.Stock;
import exception.AppException;
import repository.abstract_repository.base.AbstractCrudRepository;
import repository.abstract_repository.entity.StockRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.*;
import java.util.stream.Collectors;

public class StockRepositoryImpl extends AbstractCrudRepository<Stock, Long> implements StockRepository {


  @Override
  public Map<Shop, Integer> findShopsWithProductInStock(Product product) {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    Map<Shop, Integer> resultantMap = new HashMap<>();
    try {
      tx.begin();

      resultantMap = entityManager
              .createQuery("from " + entityType.getSimpleName(), entityType)
              .getResultStream()
              .filter(stock ->stock.getProduct().equals(product))
              .map(Stock::getShop)
              .collect(Collectors.toMap(
                      shop -> shop,
                      shop -> shop.getStocks().stream().mapToInt(Stock::getQuantity).sum()));

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
    return resultantMap;

  }

  @Override
  public Integer readStockQuantityById(Long id) {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    Integer quantity = -1;
    try {
      tx.begin();
      quantity = entityManager
              .createQuery("select e.quantity from " + entityType.getSimpleName() + " as e where e.id = :id", entityType)
              .setParameter("id", id)
              .getFirstResult();
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
    return quantity;

  }

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
