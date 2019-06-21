package repository.impl;

import domain.*;
import domain.enums.EGuarantee;
import exception.AppException;
import lombok.extern.slf4j.Slf4j;
import repository.abstract_repository.base.AbstractCrudRepository;
import repository.abstract_repository.entity.CustomerOrderRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class CustomerOrderRepositoryImpl extends AbstractCrudRepository<CustomerOrder, Long> implements CustomerOrderRepository {

  public static final int GUARANTEE_PERIOD_IN_YEARS = 2;


  @Override
  public Map<Customer, Long> findCustomersWhoBoughtAtLeastOneProductProducedInHisNationalCountryAndThenFindNumberOfProductsProducedInDifferentCountryAndBoughtByHim() {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    Map<Customer, Long> resultMap = new HashMap<>();

    try {
      tx.begin();

      var resultList = entityManager
              .createQuery("from " + entityType.getSimpleName(), entityType)
              .getResultList();

      resultMap = resultList.stream().map(CustomerOrder::getCustomer).distinct()
              .collect(Collectors.collectingAndThen(Collectors.toMap(
                      Function.identity(),
                      customer -> (Long) resultList.stream().filter(customerOrder -> customerOrder.getCustomer().equals(customer))
                              .filter(customerOrder -> customerOrder.getProduct().getProducer().getCountry().equals(customer.getCountry())).map(CustomerOrder::getQuantity).count()),
                      map -> map.entrySet().stream().filter(e ->  e.getValue() >= 1).map(Map.Entry::getKey).collect(Collectors.toMap(
                              Function.identity(),
                              customer -> (Long) resultList.stream().filter(customerOrder -> customerOrder.getCustomer().equals(customer))
                                      .filter(customerOrder -> !customerOrder.getProduct().getProducer().getCountry().equals(customer.getCountry())).map(CustomerOrder::getQuantity).count()))));
      tx.commit();
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException("find products ordered by customer grouped by producer - exception");
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }

    return resultMap;
  }

  @Override
  public Map<Producer, List<Product>> findProductsOrderedByCustomerGroupedByProducer(String customerName, String customerSurname, String countryName) {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    Map<Producer, List<Product>> resultMap = new HashMap<>();

    try {
      tx.begin();

      resultMap = entityManager
              .createQuery("select e from " + entityType.getSimpleName() + " as e where e.customer.name = :customerName " +
                      "and e.customer.country.name = :countryName and e.customer.surname = :customerSurname", entityType)
              .setParameter("customerName", customerName)
              .setParameter("customerSurname", customerSurname)
              .setParameter("countryName", countryName)
              .getResultStream().collect(Collectors.groupingBy(customerOrder -> customerOrder.getProduct().getProducer(),
                      Collectors.mapping(CustomerOrder::getProduct, Collectors.toList())));
      tx.commit();
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException("find products ordered by customer grouped by producer - exception");
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }

    return resultMap;


  }

  @Override
  public List<Product> findProductsWithActiveWarrantyAndWithGuaranteeComponents(Set<EGuarantee> guaranteeComponents) {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    List<Product> productList = new ArrayList<>();

    try {
      tx.begin();

      productList = entityManager
              .createQuery("select e from " + entityType.getSimpleName() + " as e where (e.date >= :guaranteeLimit OR CURRENT_DATE < e.date)", entityType)
              .setParameter("guaranteeLimit", LocalDate.now().minusYears(GUARANTEE_PERIOD_IN_YEARS)) /*now - X <= 2 -> now - 2 <= X*/
              .getResultStream().filter(customerOrder -> guaranteeComponents.isEmpty() || customerOrder.getProduct().getGuaranteeComponents().stream().anyMatch(guaranteeComponents::contains))
              .map(CustomerOrder::getProduct)
              .collect(Collectors.toList());

      tx.commit();
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException("find customer by name surname and country- exception");
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }

    return productList;
  }

  @Override
  public List<CustomerOrder> findOrdersOrderedWithinDateRangeAndWithPriceAfterDiscountHigherThan(LocalDate minDate, LocalDate maxDate, BigDecimal minPriceAfterDiscount) {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    List<CustomerOrder> productList = new ArrayList<>();

    try {
      tx.begin();

      productList = entityManager
              .createQuery("select e from " + entityType.getSimpleName() + " as e where e.date between :minDate and :maxDate " +
                      "and e.product.price * (1-e.discount) > :minPrice", entityType)
              .setParameter("minDate", minDate)
              .setParameter("maxDate", maxDate)
              .setParameter("minPrice", minPriceAfterDiscount)
              .getResultList();

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

    return productList;
  }

  @Override
  public List<Product> findProductsOrderedByCustomersFromCountryAndWithAgeWithinRange(String countryName, Integer minAge, Integer maxAge) {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    List<Product> productList = new ArrayList<>();

    try {
      tx.begin();

      productList = entityManager
              .createQuery("select e.product from " + entityType.getSimpleName() + " as e where e.customer.age between :minAge and" +
                      " :maxAge and e.customer.country.name = :countryName", Product.class)
              .setParameter("minAge", minAge)
              .setParameter("maxAge", maxAge)
              .setParameter("countryName", countryName.toUpperCase())
              .getResultList();

        /*   .createQuery("from " + entityType.getSimpleName(), entityType)
             .getResultStream()
              .filter(customerOrder -> customerOrder.getCustomer().getAge() >= minAge &&
                      customerOrder.getCustomer().getAge() <= maxAge)
              .filter(customerOrder -> customerOrder.getCustomer().getCountry().getName().equalsIgnoreCase(countryName))
              .map(CustomerOrder::getProduct)
              .collect(Collectors.toList());*/

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

    return productList;
  }

  @Override
  public Map<Category, Map<Product, Integer>> findTheMostExpensiveOrderedProductInEachCategoryWithNumberOfPurchases() {


    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    Map<Category, Map<Product, Integer>> resultMap = new HashMap<>();

    try {
      tx.begin();
      var resultList = entityManager
              .createQuery("from " + entityType.getSimpleName(), entityType)
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
  public Map<Product, Integer> findNumberOfProductsOrders(List<Product> productList) {

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

