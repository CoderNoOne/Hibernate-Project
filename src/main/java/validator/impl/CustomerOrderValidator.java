package validator.impl;

import domain.CustomerOrder;
import validator.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class CustomerOrderValidator implements Validator<CustomerOrder> {

  private Map<String, String> errors;

  public CustomerOrderValidator() {
    this.errors = new HashMap<>();
  }

  @Override
  public Map<String, String> validate(CustomerOrder customerOrder) {

    if (customerOrder == null) {
      errors.put("CustomerOrder object", "CutomerOrder object is null");
      return errors;
    }

    if (!isDiscountValid(customerOrder)) {
      errors.put("Discount", "Discount shoould be in the range <0.0,1.0>");
    }

    if (!isOrderDateValid(customerOrder)) {
      errors.put("Order date", "Order date should be at present day or in the future");
    }
    return errors;
  }

  public Map<String, String> getErrors() {
    return errors;
  }

  @Override
  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  private boolean isDiscountValid(CustomerOrder customerOrder) {
    return customerOrder.getDiscount().compareTo(BigDecimal.ZERO) >= 0 && customerOrder.getDiscount().compareTo(BigDecimal.ONE) <= 0;
  }

  private boolean isOrderDateValid(CustomerOrder customerOrder) {
    return customerOrder.getDate().compareTo(LocalDate.now()) >= 0;
  }
}
