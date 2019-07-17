package validator.impl;

import dto.PaymentDto;
import validator.AbstractValidator;


import java.util.Map;

public class PaymentDtoValidator extends AbstractValidator<PaymentDto> {

  @Override
  public Map<String, String> validate(PaymentDto paymentDto) {
    errors.clear();
    if (paymentDto == null) {
      errors.put("Payment object", "Payment object is null");
      return errors;
    }

    if (!isEpaymentValid(paymentDto)) {
      errors.put("Epayment object", "Epayment is null");
    }
    return errors;
  }

  private boolean isEpaymentValid(PaymentDto paymentDto) {
    return paymentDto.getEpayment() != null;
  }
}
