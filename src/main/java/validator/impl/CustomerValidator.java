package validator.impl;

import domain.Customer;
import validator.AbstractValidator;

import java.util.Map;

public class CustomerValidator extends AbstractValidator<Customer>{

  @Override
  public Map<String, String> validate(Customer customer) {

    errors.clear();

    if (customer == null) {
      errors.put("Customer object", "customer object is null");
      return errors;
    }

    if (!isCustomerNameValid(customer)) {
      errors.put("Customer name", "Customer name should contains only capital letters and space between letters");
    }
    if (!isCustomerSurnameValid(customer)) {
      errors.put("Customer surname", "Customer surname should contains only capital letters and space between letters");
    }
    if (!isCustomerAgeValid(customer)) {
      errors.put("Customer age", "Customer age should be greater than or equal to 18");
    }

    if(!isCountryValid(customer)){

      errors.putAll(getCountryValidator(customer).getErrors());
    }
    return errors;
  }

  private boolean isCustomerNameValid(Customer customer) {
    return customer.getName().matches("[A-Z]+(\\s[A-Z]+)*");
  }

  private boolean isCustomerSurnameValid(Customer customer) {
    return customer.getSurname().matches("[A-Z]+(\\s[A-Z]+)*");
  }

  private boolean isCustomerAgeValid(Customer customer) {
    return customer.getAge() >= 18;
  }

  private boolean isCountryValid(Customer customer){
    return !getCountryValidator(customer).hasErrors();

  }

  private CountryValidator getCountryValidator(Customer customer){
    CountryValidator countryValidator = new CountryValidator();
    countryValidator.validate(customer.getCountry());
    return countryValidator;
  }

}
