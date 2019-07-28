package repository.abstract_repository.entity;

import domain.Payment;
import domain.enums.Epayment;
import repository.abstract_repository.base.CrudRepository;

import java.util.Optional;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

  Optional<Payment> findPaymentByEPayment(Epayment epayment);
}
