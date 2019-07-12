package validator.impl;

import dto.CustomerDto;
import validator.AbstractValidator;

import java.util.Map;

public class CustomerDtoValidator extends AbstractValidator<CustomerDto>{

  @Override
  public Map<String, String> validate(CustomerDto customerDto) {

    errors.clear();

    if (customerDto == null) {
      errors.put("Customer object", "customer object is null");
      return errors;
    }

    if (!isCustomerNameValid(customerDto)) {
      errors.put("Customer name", "Customer name should contains only capital letters and space between letters");
    }
    if (!isCustomerSurnameValid(customerDto)) {
      errors.put("Customer surname", "Customer surname should contains only capital letters and space between letters");
    }
    if (!isCustomerAgeValid(customerDto)) {
      errors.put("Customer age", "Customer age should be greater than or equal to 18");
    }

    if(!isCountryValid(customerDto)){

      errors.putAll(getCountryValidator(customerDto).getErrors());
    }
    return errors;
  }

  private boolean isCustomerNameValid(CustomerDto customerDto) {
    return customerDto.getName().matches("[A-Z]+(\\s[A-Z]+)*");
  }

  private boolean isCustomerSurnameValid(CustomerDto customerDto) {
    return customerDto.getSurname().matches("[A-Z]+(\\s[A-Z]+)*");
  }

  private boolean isCustomerAgeValid(CustomerDto customerDto) {
    return customerDto.getAge() >= 18;
  }

  private boolean isCountryValid(CustomerDto customerDto){
    return !getCountryValidator(customerDto).hasErrors();

  }

  private CountryDtoValidator getCountryValidator(CustomerDto customerDto){
    CountryDtoValidator countryValidator = new CountryDtoValidator();
    countryValidator.validate(customerDto.getCountryDto());
    return countryValidator;
  }

}
