package util.entity_utils;

import domain.*;
import domain.enums.Epayment;
import exception.AppException;
import validator.impl.CustomerOrderDtoValidator;

import java.util.stream.Collectors;

import static util.others.UserDataUtils.*;
import static util.others.UserDataUtils.getLocalDate;
import static util.others.UserDataUtils.printMessage;

public class CustomerOrderUtil {

  private CustomerOrderUtil() {
  }

  public static CustomerOrderDto createCustomerOrderFromUserInput() {

    return CustomerOrderDto.builder()
            .customer(Customer.builder()
                    .name(getString("Input customer name"))
                    .surname(getString("Input customer surname"))
                    .country(Country.builder()
                            .name(getString("Input country name"))
                            .build())/*customer moze istniec w db - lub nie*/
                    .build())
            .date(getLocalDate("Input order date in format yyyy-MM-dd"))
            .quantity(getInt("Input order quantity"))
            .product(Product.builder()/*moze byc kilka ze względu na innego producenta*/
                    .name(getString("Input product name"))
                    .category(Category.builder()
                            .name(getString("Input category name"))
                            .build())
                    .build())
            .discount(getBigDecimal("Input order discount <0.0,1.0>"))
            .payment(Payment.builder()
                    .epayment(Epayment.valueOf(getString("Input payment type: CASH, CARD, MONEY_TRANSFER")))
                    .build())
            .build();
  }

  public static CustomerOrderDto getCustomerOrderIfValid(CustomerOrderDto customerOrder) {
    var customerOrderValidator = new CustomerOrderDtoValidator();
    var errorsMap = customerOrderValidator.validate(customerOrder);

    if (customerOrderValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("CustomerOrderDto is not valid: " + customerOrderValidator.getErrors());
    }
    return customerOrder;
  }
}
