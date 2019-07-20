package repository.impl;

import domain.*;
import exception.AppException;
import lombok.extern.slf4j.Slf4j;
import repository.abstract_repository.base.AbstractCrudRepository;
import repository.abstract_repository.entity.CustomerOrderRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Slf4j
public class CustomerOrderRepositoryImpl extends AbstractCrudRepository<CustomerOrder, Long> implements CustomerOrderRepository {

  public static final int GUARANTEE_PERIOD_IN_YEARS = 2;


  public void add(Customer customer){

  }

  @Override
  public List<CustomerOrder> findProductsOrderedByCustomer(String customerName, String customerSurname, String countryName) {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    List<CustomerOrder> resultList = new ArrayList<>();

    try {
      tx.begin();

      resultList = entityManager
              .createQuery("select e from " + entityType.getSimpleName() + " as e where e.customer.name = :customerName " +
                      "and e.customer.country.name = :countryName and e.customer.surname = :customerSurname", entityType)
              .setParameter("customerName", customerName)
              .setParameter("customerSurname", customerSurname)
              .setParameter("countryName", countryName)
              .getResultList();

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

    return resultList;
  }

  @Override
  public List<CustomerOrder> findProductsWithActiveWarranty() {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    List<CustomerOrder> productList = new ArrayList<>();

    try {
      tx.begin();

      productList = entityManager
              .createQuery("select e from " + entityType.getSimpleName() + " as e where (e.date >= :guaranteeLimit OR CURRENT_DATE < e.date)", entityType)
              .setParameter("guaranteeLimit", LocalDate.now().minusYears(GUARANTEE_PERIOD_IN_YEARS))
              .getResultList();

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
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException("find orders within date range and with price after discount - exception");
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
}

