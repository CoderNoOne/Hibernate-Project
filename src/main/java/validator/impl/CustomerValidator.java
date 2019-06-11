package validator.impl;

import domain.Customer;
import validator.Validator;

import java.util.Map;

public class CustomerValidator implements Validator<Customer> {

  @Override
  public Map<String, String> validate(Customer customer) {
    return null;
  }

  @Override
  public boolean hasErrors() {
    return false;
  }

  @Override
  public boolean validateEntity(Customer customer) {
    return false;
  }
}
