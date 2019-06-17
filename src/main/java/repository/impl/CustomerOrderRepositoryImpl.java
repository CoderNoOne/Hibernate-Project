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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CustomerOrderRepositoryImpl extends AbstractCrudRepository<CustomerOrder, Long> implements CustomerOrderRepository {

  @Override
  public List<Product> getProductsByNameAndCategory(String name, Category category) {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    List<Product> resultList = new ArrayList<>();

    try {
      tx.begin();

      resultList = entityManager
              .createQuery("from " + entityType.getSimpleName(), entityType)
              .getResultStream()
              .map(CustomerOrder::getProduct)
              .filter(product -> product.getName().equals(name) && product.getCategory().equals(category))
              .collect(Collectors.toList());
      tx.commit();

    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));

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
}
