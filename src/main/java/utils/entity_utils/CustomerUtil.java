package utils.entity_utils;

import domain.Country;
import domain.Customer;
import exception.AppException;
import validator.impl.CustomerValidator;

import java.util.stream.Collectors;

import static utils.others.UserDataUtils.*;


public final class CustomerUtil {

  private CustomerUtil() {
  }

  public static Customer createCustomerFromUserInput() {

    return Customer.builder()
            .name(getString("Input customer name"))
            .surname(getString("Input customer surname"))
            .age(getInt("Input customer age"))
            .country(Country.builder()
                    .name(getString("Input country name"))
                    .build())
            .build();
  }

  public static Customer getCustomerIfValid(Customer customer){

    var customerValidator = new CustomerValidator();
    var errorsMap = customerValidator.validate(customer);

    if (customerValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("Customer is not valid: " + customerValidator.getErrors());
    }

    return customer;
  }
}
