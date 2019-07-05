package service.entity;

import domain.Payment;
import domain.enums.Epayment;
import exception.AppException;
import repository.abstract_repository.entity.PaymentRepository;
import repository.impl.PaymentRepositoryImpl;

import java.util.Optional;

public class PaymentService {

  private final PaymentRepository paymentRepository;

  public PaymentService() {
    this.paymentRepository = new PaymentRepositoryImpl();
  }

  private Optional<Payment> addPaymentToDb(Payment payment) {
    return paymentRepository.addOrUpdate(payment);
  }

  public void addPaymentToDbFromUserInput(Payment payment){
    if(!isPaymentUnique(payment)){
      addPaymentToDb(payment);
    }else {
      throw new AppException("Couldn't add new payment - payment's not unique");
    }
  }

  public Optional<Payment> getPaymentByEpayment(Epayment epayment){
    return paymentRepository.findPaymentByEPayment(epayment);
  }

  private boolean isPaymentUnique(Payment payment) {
    return getPaymentByEpayment(payment.getEpayment()).isEmpty();
  }


}
