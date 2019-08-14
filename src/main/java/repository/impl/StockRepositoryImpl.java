package repository.impl;

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
  public void increaseStockQuantity(Long stockId, Integer quantity) {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();


    try {
      tx.begin();
      entityManager
              .createQuery("select e from " + entityType.getSimpleName() + " as e where e.id =:stockId", entityType)
              .setParameter("stockId", stockId)
              .getResultList()
              .stream()
              .findFirst()
              .ifPresentOrElse(stock -> stock.setQuantity(stock.getQuantity() + quantity),
                      () -> {
                        throw new AppException("Stock with shop:" + stockId + " doesn't exist!");
                      });

      tx.commit();
    } catch (Exception e) {
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException("decrease stock quantity - exception: " + e.getMessage());
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }
  }

  @Override
  public void decreaseStockQuantity(Stock stock, Integer quantity) {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();


    try {
      tx.begin();

      stock = entityManager.merge(stock);
      stock.setQuantity(stock.getQuantity() - quantity);

      tx.commit();
    } catch (Exception e) {
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException("decrease stock quantity - exception");
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }
  }

  @Override
  public List<Stock> findStocksWithProducerTradeName(String tradeName) {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    List<Stock> stockList = new ArrayList<>();
    try {
      tx.begin();

      stockList = entityManager.createQuery("select e from " + entityType.getSimpleName() + " as e where e.product.producer.trade.name = :tradeName", entityType)
              .setParameter("tradeName", tradeName)
              .getResultList();

      tx.commit();
    } catch (Exception e) {
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException("find stocks with producer's trade name - exception");
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }
    return stockList;
  }

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
              .filter(stock -> stock.getProduct().getName().equals(product.getName()))
              .map(Stock::getShop)
              .distinct()
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
