package repository.impl;

import domain.Category;
import exception.AppException;
import lombok.extern.slf4j.Slf4j;
import repository.abstract_repository.base.AbstractCrudRepository;
import repository.abstract_repository.entity.CategoryRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class CategoryRepositoryImpl extends AbstractCrudRepository<Category, Long> implements CategoryRepository {

  @Override
  public Optional<Category> findCategoryByName(String name) {


    if (name == null) {
      throw new AppException("Category name is null");
    }

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    Optional<Category> item = Optional.empty();

    try {
      tx.begin();
      item = entityManager
              .createQuery("select e from " + entityType.getSimpleName() + " as e where e.name = :name", entityType)
              .setParameter("name", name)
              .getResultList()
              .stream()
              .findFirst();
      tx.commit();
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      if (tx != null) {
        tx.rollback();
      }
      throw new AppException("find category by name - exception");
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }

    return item;
  }
}
