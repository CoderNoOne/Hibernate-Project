package utils.entity_utils;

import domain.*;
import domain.enums.Epayment;
import exception.AppException;
import utils.UserDataUtils;
import validator.impl.CustomerOrderValidator;

import java.util.stream.Collectors;

import static utils.UserDataUtils.*;
import static utils.UserDataUtils.getLocalDate;
import static utils.UserDataUtils.printMessage;

public class CustomerOrderUtil {

  private CustomerOrderUtil() {
  }

  public static CustomerOrder createCustomerOrderFromUserInput() {

    return CustomerOrder.builder()
            .customer(Customer.builder()
                    .name(getString("Input customer name"))
                    .surname(getString("Input customer surname"))
                    .country(Country.builder()
                            .name(getString("Input country name"))
                            .build())
                    .build())
            .date(getLocalDate("Input order date"))
            .quantity(getInt("Input order quantity"))
            .product(Product.builder()
                    .name("Input product name")
                    .category(Category.builder()
                            .name("Input category name")
                            .build())
                    .build())
            .discount(getBigDecimal("Input order discount <0.0,1.0>"))
            .payment(Payment.builder()
                    .epayment(Epayment.valueOf(getString("Input payment type")))
                    .build())
            .build();
  }

  public static CustomerOrder getCustomerOrderIfValid(CustomerOrder customerOrder) {
    var customerOrderValidator = new CustomerOrderValidator();
    var errorsMap = customerOrderValidator.validate(customerOrder);

    if (customerOrderValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("CustomerOrder is not valid: " + customerOrderValidator.getErrors());
    }
    return customerOrder;
  }
}
