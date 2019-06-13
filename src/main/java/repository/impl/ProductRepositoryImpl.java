package repository.impl;

import domain.Category;
import domain.Producer;
import domain.Product;
import exception.AppException;
import repository.abstract_repository.base.AbstractCrudRepository;
import repository.abstract_repository.entity.ProductRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Arrays;
import java.util.Optional;

public class ProductRepositoryImpl extends AbstractCrudRepository<Product, Long> implements ProductRepository {

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
