package repository.impl;

import domain.Payment;
import domain.enums.Epayment;
import exception.AppException;
import repository.abstract_repository.base.AbstractCrudRepository;
import repository.abstract_repository.entity.PaymentRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Arrays;
import java.util.Optional;

public class PaymentRepositoryImpl  extends AbstractCrudRepository <Payment, Long> implements PaymentRepository {

  @Override
  public Optional<Payment> findPaymentByEPayment(Epayment epayment) {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction tx = entityManager.getTransaction();

    Optional<Payment> item = Optional.empty();

    try {
      tx.begin();
      item = entityManager
              .createQuery("select e from " + entityType.getSimpleName() + " as e where e.epayment = :epayment", entityType)
              .setParameter("epayment", epayment)
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
      throw new AppException("find payment by epayment - exception");
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }

    return item;
  }
}
