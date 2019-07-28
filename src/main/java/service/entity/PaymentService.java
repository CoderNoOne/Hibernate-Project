package service.entity;

import repository.abstract_repository.entity.PaymentRepository;
import repository.impl.PaymentRepositoryImpl;

public class PaymentService {

  private final PaymentRepository paymentRepository;

  public PaymentService() {
    this.paymentRepository = new PaymentRepositoryImpl();
  }

  public PaymentService(PaymentRepository paymentRepository) {
    this.paymentRepository = paymentRepository;

  }

  public void deleteAllPayments() {
    paymentRepository.deleteAll();
  }
}
