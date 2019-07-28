package util.entity_utils;

import domain.enums.Epayment;
import dto.*;
import exception.AppException;
import validator.impl.CustomerOrderDtoValidator;

import java.util.stream.Collectors;

import static util.others.UserDataUtils.*;

public interface CustomerOrderUtil {

  static CustomerOrderDto createCustomerOrderDtoFromUserInput() {

    return CustomerOrderDto.builder()
            .customer(CustomerDto.builder()
                    .name(getString("Input customer name"))
                    .surname(getString("Input customer surname"))
                    .countryDto(CountryDto.builder()
                            .name(getString("Input country name"))
                            .build())
                    .build())
            .date(getLocalDate("Input order date in format yyyy-MM-dd"))
            .quantity(getInt("Input order quantity"))
            .product(ProductDto.builder()
                    .name(getString("Input product name"))
                    .categoryDto(CategoryDto.builder()
                            .name(getString("Input category name"))
                            .build())
                    .build())
            .discount(getBigDecimal("Input order discount <0.0,1.0>"))
            .payment(PaymentDto.builder()
                    .epayment(Epayment.valueOf(getString("Input payment type: CASH, CARD, MONEY_TRANSFER")))
                    .build())
            .build();
  }

  static CustomerOrderDto getCustomerOrderIfValid(CustomerOrderDto customerOrder) {
    var customerOrderValidator = new CustomerOrderDtoValidator();
    var errorsMap = customerOrderValidator.validate(customerOrder);

    if (customerOrderValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("CustomerOrderDto is not valid: " + customerOrderValidator.getErrors());
    }
    return customerOrder;
  }
}
