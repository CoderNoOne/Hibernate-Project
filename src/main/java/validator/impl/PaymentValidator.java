package validator.impl;

import domain.Payment;
import validator.Validator;

import java.util.HashMap;
import java.util.Map;

public class PaymentValidator implements Validator<Payment> {

  private Map<String, String> errors;

  public PaymentValidator() {
    this.errors = new HashMap<>();
  }

  @Override
  public Map<String, String> validate(Payment payment) {
    if (payment == null) {
      errors.put("Payment object", "Payment object is null");
      return errors;
    }
    return errors;
  }

  @Override
  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  public Map<String, String> getErrors() {
    return errors;
  }
}
