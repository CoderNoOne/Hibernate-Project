package util.entity_utils;

import domain.Customer;
import dto.CountryDto;
import dto.CustomerDto;
import exception.AppException;
import validator.impl.CustomerDtoValidator;

import java.util.stream.Collectors;

import static util.others.UserDataUtils.*;


public final class CustomerUtil {

  private CustomerUtil() {
  }

  public static CustomerDto specifyCustomerDetailToUpdate(){
    return createCustomerDtoFromUserInput();
  }

  public static CustomerDto specifyCustomerDtoDetailToDelete(){

    return CustomerDto.builder()
            .name(getString("Input customer name"))
            .surname(getString("Input customer surname"))
            .countryDto(CountryDto.builder()
                    .name(getString("Input country name"))
                    .build())
            .build();
  }

  public static CustomerDto createCustomerDtoFromUserInput() {

    return CustomerDto.builder()
            .name(getString("Input customer name"))
            .surname(getString("Input customer surname"))
            .age(getInt("Input customer age"))
            .countryDto(CountryDto.builder()
                    .name(getString("Input country name"))
                    .build())
            .build();
  }

  public static CustomerDto getCustomerDtoIfValid(CustomerDto customerDto){

    var customerDtoValidator = new CustomerDtoValidator();
    var errorsMap = customerDtoValidator.validate(customerDto);

    if (customerDtoValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("Customer is not valid: " + customerDtoValidator.getErrors());
    }

    return customerDto;
  }
}
