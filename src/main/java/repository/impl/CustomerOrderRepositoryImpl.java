package repository.impl;

import domain.Category;
import domain.CustomerOrder;
import domain.Product;
import exception.AppException;
import lombok.extern.slf4j.Slf4j;
import repository.abstract_repository.base.AbstractCrudRepository;
import repository.abstract_repository.entity.CustomerOrderRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class CustomerOrderRepositoryImpl extends AbstractCrudRepository<CustomerOrder, Long> implements CustomerOrderRepository {

  @Override
  public Map<Product, Integer> findNumberOfOrdersForSpecifiedProducts(List<Product> productList) {

    if (productList == null) {
      throw new AppException("Product list is null");
    }

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    Map<Product, Integer> resultantMap = new HashMap<>();

    try {
      tx.begin();
      resultantMap = entityManager
              .createQuery("select e from " + entityType.getSimpleName() + " as e where e.product IN :products", entityType)
              .setParameter("products", productList)
              .getResultStream()
              .collect(Collectors.groupingBy(CustomerOrder::getProduct, Collectors.summingInt(CustomerOrder::getQuantity)));

      tx.commit();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      System.out.println(Arrays.toString(e.getStackTrace()));
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException("find customer by name surname and country- exception");
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }

    return resultantMap;
  }
}

