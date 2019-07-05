package validator.impl;

import domain.Payment;
import validator.AbstractValidator;


import java.util.Map;

public class PaymentValidator extends AbstractValidator<Payment> {

  @Override
  public Map<String, String> validate(Payment payment) {
    if (payment == null) {
      errors.put("Payment object", "Payment object is null");
      return errors;
    }
    return errors;
  }

}
