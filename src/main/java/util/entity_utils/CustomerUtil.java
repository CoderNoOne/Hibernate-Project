package util.entity_utils;

import dto.CountryDto;
import dto.CustomerDto;
import exception.AppException;
import validator.impl.CustomerDtoValidator;

import java.util.stream.Collectors;

import static util.others.UserDataUtils.*;


public interface CustomerUtil {

  static CustomerDto getCustomerDtoToUpdate(Long id) {

    return CustomerDto.builder()
            .id(id)
            .name(getString("Do you want to updateProduct customer name? (y/n)")
                    .equalsIgnoreCase("Y") ? getString("Specify customer new name") : null)
            .surname(getString("Do you want to updateProduct customer surname? (y/n)")
                    .equalsIgnoreCase("Y") ? getString("Specify customer new surname") : null)
            .age(getString("Do you want to updateProduct customer age?(y/n)")
                    .equalsIgnoreCase("Y") ? getInt("Specify user new age") : null)
            .countryDto(getString("Do you want to updateProduct customer country name? (y/n)").equalsIgnoreCase("Y") ? CountryDto.builder()
                    .name(getString("Specify new customer's country name")).build() : null)
            .build();

  }


  static CustomerDto specifyCustomerDtoDetailToDelete() {

    printMessage("\nInput customer's information you want to delete\n");

    return CustomerDto.builder()
            .name(getString("Input customer name"))
            .surname(getString("Input customer surname"))
            .countryDto(CountryDto.builder()
                    .name(getString("Input country name"))
                    .build())
            .build();
  }

  static CustomerDto createCustomerDtoFromUserInput() {

    return CustomerDto.builder()
            .name(getString("Input customer name"))
            .surname(getString("Input customer surname"))
            .age(getInt("Input customer age"))
            .countryDto(CountryDto.builder()
                    .name(getString("Input country name"))
                    .build())
            .build();
  }

  static CustomerDto getCustomerDtoIfValid(CustomerDto customerDto) {

    var customerDtoValidator = new CustomerDtoValidator();
    var errorsMap = customerDtoValidator.validate(customerDto);

    if (customerDtoValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("Customer is not valid: " + customerDtoValidator.getErrors());
    }

    return customerDto;
  }
}
