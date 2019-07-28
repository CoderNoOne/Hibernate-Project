package repository.impl;

import domain.Country;
import domain.Customer;
import exception.AppException;
import repository.abstract_repository.base.AbstractCrudRepository;
import repository.abstract_repository.entity.CustomerRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Arrays;
import java.util.Optional;

public class CustomerRepositoryImpl extends AbstractCrudRepository<Customer, Long> implements CustomerRepository {

  @Override
  public void deleteCustomer(Customer customerToDelete) {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    findByNameAndSurnameAndCountry(customerToDelete.getName(), customerToDelete.getSurname(), customerToDelete.getCountry()).ifPresentOrElse(customer -> {
      tx.begin();
      entityManager.remove(entityManager.merge(customer));
      tx.commit();
    }, () -> {
      throw new AppException("Customer you wanted to delete: " + customerToDelete + " doesnt exist in DB");
    });
  }

  @Override
  public Optional<Customer> findByNameAndSurnameAndCountry(String name, String surname, Country country) {

    if (country == null) {
      throw new AppException("Country object is null");
    }

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    Optional<Customer> item = Optional.empty();

    try {
      tx.begin();
      item = entityManager
              .createQuery("select e from " + entityType.getSimpleName() + " as e where e.name = :name and e.surname = :surname and e.country.name = :countryName", entityType)
              .setParameter("name", name)
              .setParameter("surname", surname)
              .setParameter("countryName", country.getName())
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
      throw new AppException("find customer by name surname and country- exception");
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }

    return item;
  }
}

