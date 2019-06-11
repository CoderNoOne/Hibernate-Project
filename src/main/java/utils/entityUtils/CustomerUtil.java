package utils.entityUtils;

import domain.Country;
import domain.Customer;
import exception.AppException;
import validator.impl.CustomerValidator;

import java.util.stream.Collectors;

import static utils.UserDataUtils.*;


public final class CustomerUtil {

  private static final CustomerValidator customerValidator = new CustomerValidator();

  private CustomerUtil() {
  }

  public static Customer createCustomer() {

    var name = getString("Input customer name");
    var surname = getString("Input customer surname");
    var age = getInt("Input customer age");
    var country = Country.builder().name(getString("Input country name")).build();

    var customer = Customer.builder()
            .age(age)
            .surname(surname)
            .name(name).country(country)
            .build();

    //walidacja poprawnosci danych
    var errorsMap = customerValidator.validate(customer);

    if (customerValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("Car is not valid");
    }

    return customer;
  }
}
