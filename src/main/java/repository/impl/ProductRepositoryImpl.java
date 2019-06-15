package repository.impl;

import domain.Category;
import domain.CustomerOrder;
import domain.Producer;
import domain.Product;
import exception.AppException;
import repository.abstract_repository.base.AbstractCrudRepository;
import repository.abstract_repository.entity.ProductRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProductRepositoryImpl extends AbstractCrudRepository<Product, Long> implements ProductRepository {


  @Override
  public List<Product> findProductsByNameAndCategory(String name, Category category) {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    List<Product> resultList = new ArrayList<>();

    try {
      tx.begin();

      resultList = entityManager
              .createQuery("select e from " + entityType.getSimpleName() + " as e where e.name = :name and e.category.name = :categoryName", entityType)
              .setParameter("name", name)
              .setParameter("categoryName", category.getName())
              .getResultStream()
              .collect(Collectors.toList());

      tx.commit();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      System.out.println(Arrays.toString(e.getStackTrace()));
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException("find products by name and category - exception");
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }

    return resultList;
  }

  @Override
  public List<Product> findProductsOrderedByClientsFromCountryAndWithAgeWithinRange(String countryName, Integer minAge, Integer maxAge) {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    List<Product> resultList = new ArrayList<>();

    try {
      tx.begin();

      resultList = entityManager
              .createQuery("from " + entityType.getSimpleName(), entityType)
              .getResultStream()
              .filter(product -> product.getCustomerOrders().stream().anyMatch(
                      customerOrder -> customerOrder.getCustomer().getCountry().getName().equals(countryName) &&
                              customerOrder.getCustomer().getAge() >= minAge && customerOrder.getCustomer().getAge() <= maxAge))
              .sorted(Comparator.comparing(Product::getPrice).reversed())
              .collect(Collectors.toList());

      tx.commit();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      System.out.println(Arrays.toString(e.getStackTrace()));
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException("find product by name category and producer - exception");
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }

    return resultList;
  }

  @Override
  public Map<Category, Product> findProductsWithMostPriceInEveryCategory() {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    Map<Category, Product> resultantMap = new HashMap<>();

    try {
      tx.begin();

      resultantMap = entityManager
              .createQuery("from " + entityType.getSimpleName(), entityType)
              .getResultStream()
              .collect(Collectors.collectingAndThen(Collectors.groupingBy(Product::getCategory,
                      Collectors.maxBy(Comparator.comparing(Product::getPrice))),
                      map -> map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()))));
      tx.commit();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      System.out.println(Arrays.toString(e.getStackTrace()));
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException("find product by name category and producer - exception");
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }

    return resultantMap;
  }


  @Override
  public Optional<Product> findByNameAndCategoryAndProducer(String name, Category category, Producer producer) {

    if (category == null) {
      throw new AppException("Category is null");
    }
    if (producer == null) {
      throw new AppException("producer is null");
    }

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    Optional<Product> item = Optional.empty();

    try {
      tx.begin();
      item = entityManager
              .createQuery("select e from " + entityType.getSimpleName() + " as e where e.name = :name and e.category.name = :categoryName and e.producer.name = :producerName", entityType)
              .setParameter("name", name)
              .setParameter("categoryName", category.getName())
              .setParameter("producerName", producer.getName())
              .getResultList()
              .stream()
              .findFirst();
      tx.commit();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      System.out.println(Arrays.toString(e.getStackTrace()));
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException("find product by name category and producer - exception");
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }

    return item;
  }
}
