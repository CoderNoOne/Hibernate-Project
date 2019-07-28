package service.entity;

import lombok.RequiredArgsConstructor;
import repository.abstract_repository.entity.PaymentRepository;
import repository.impl.PaymentRepositoryImpl;

@RequiredArgsConstructor
public class PaymentService {

  private final PaymentRepository paymentRepository;

  public PaymentService() {
    this.paymentRepository = new PaymentRepositoryImpl();
  }

  public void deleteAllPayments() {
    paymentRepository.deleteAll();
  }
}
