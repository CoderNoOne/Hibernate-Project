package util.update;

import domain.Country;
import domain.Customer;
import util.update.enums.CustomerField;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static util.others.UserDataUtils.*;

public interface UpdateCustomerUtil {

  static Customer getUpdatedCustomer(Customer customer) {

    List<CustomerField> customerFields = Arrays.stream(CustomerField.values()).collect(Collectors.toList());

    boolean hasNext;
    do {
      printCollectionWithNumeration(customerFields);
      CustomerField customerProperty = CustomerField.valueOf(getString("Choose what customer property you want to update. Not case sensitive").toUpperCase());

      switch (customerProperty) {
        case NAME -> {
          String updatedName = getString("Type customer new name");
          customerFields.remove(CustomerField.NAME);
          customer.setName(updatedName);
        }
        case SURNAME -> {
          String updatedSurname = getString("Type customer new surname");
          customerFields.remove(CustomerField.SURNAME);
          customer.setSurname(updatedSurname);
        }
        case AGE -> {
          int updatedAge = getInt("Type customer new age");
          customerFields.remove(CustomerField.AGE);
          customer.setAge(updatedAge);
        }
        case COUNTRY -> {
          String updatedCountryName = getString("Type customer new country");
          customerFields.remove(CustomerField.COUNTRY);
          customer.setCountry(Country.builder().name(updatedCountryName).build());
        }
        default -> System.out.println("Not valid customer property");
      }
      hasNext = getString("Do you want to update other customer property? (Y/N)").equalsIgnoreCase("Y");
    } while (hasNext && !customerFields.isEmpty());

    return customer;
  }
}

