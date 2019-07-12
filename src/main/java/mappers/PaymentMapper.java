package mappers;

import domain.Payment;
import dto.PaymentDto;

public class PaymentMapper {

  public Payment mapPaymentDtoToPayment(PaymentDto paymentDto) {

    return Payment.builder()
            .id(paymentDto.getId())
            .epayment(paymentDto.getEpayment())
            .build();
  }

  public PaymentDto mapPaymentToPaymentDto(Payment payment) {
    return PaymentDto.builder()
            .id(payment.getId())
            .epayment(payment.getEpayment())
            .build();
  }
}
