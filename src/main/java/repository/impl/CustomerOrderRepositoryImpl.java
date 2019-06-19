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
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class CustomerOrderRepositoryImpl extends AbstractCrudRepository<CustomerOrder, Long> implements CustomerOrderRepository {


  @Override
  public Map<Category, Map<Product, Integer>> findTheMostExpensiveProductInEachCategoryWithNumberOfPurchases() {


    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    Map<Category, Map<Product, Integer>> resultMap = new HashMap<>();

    try {
      tx.begin();
      var resultList = entityManager
              .createQuery("select e from " + entityType.getSimpleName(), entityType)
              .getResultList();

      resultMap = resultList.stream().map(customerOrder -> customerOrder.getProduct().getCategory()).distinct()
              .collect(Collectors.toMap(
                      Function.identity(),
                      category -> {
                        Map<Category, Integer> theHighestQuantityOrderInEachCategory = resultList.stream().filter(customerOrder -> customerOrder.getProduct().getCategory().equals(category))
                                .collect(Collectors.groupingBy(customerOrder -> customerOrder.getProduct().getCategory(),
                                        Collectors.mapping(CustomerOrder::getQuantity, Collectors.reducing(0, (v1, v2) -> v1 >= v2 ? v1 : v2))));

                        return resultList.stream().filter(customerOrder -> customerOrder.getProduct().getCategory().equals(category))
                                .collect(Collectors.groupingBy(CustomerOrder::getProduct,
                                        Collectors.mapping(CustomerOrder::getQuantity, Collectors.filtering(
                                                quantity -> quantity.intValue() == theHighestQuantityOrderInEachCategory.get(category).intValue(),
                                                Collectors.reducing(0, (v1, v2) -> v1 > v2 ? v1 : v2)))));
                      }));
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

    return resultMap;
  }


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

