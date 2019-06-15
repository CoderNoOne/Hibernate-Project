package utils.entity_utils;

import domain.*;
import domain.enums.Epayment;
import exception.AppException;
import utils.UserDataUtils;
import validator.impl.CustomerOrderValidator;

import java.util.stream.Collectors;

import static utils.UserDataUtils.printMessage;

public class CustomerOrderUtil {

  private CustomerOrderUtil() {
  }

  public static CustomerOrder createCustomerOrderFromUserInput() {

    var customerOrder = CustomerOrder.builder()
            .customer(Customer.builder()
                    .name(UserDataUtils.getString("Input customer name"))
                    .surname(UserDataUtils.getString("Input customer surname"))
                    .country(Country.builder()
                            .name(UserDataUtils.getString("Input country name"))
                            .build())
                    .build())
            .date(UserDataUtils.getLocalDate("Input order date"))
            .product(Product.builder()
                    .name("Input product name")
                    .category(Category.builder()
                            .name("Input category name")
                            .build())
                    .build())
            .quantity(UserDataUtils.getInt("Input order quantity"))
            .discount(UserDataUtils.getBigDecimal("Input order discount <0.0,1.0>"))
            .payment(Payment.builder()
                    .payment(Epayment.valueOf(UserDataUtils.getString("Input payment type")))
                    .build())
            .build();

    var customerOrderValidator = new CustomerOrderValidator();
    var errorsMap = customerOrderValidator.validate(customerOrder);

    if (customerOrderValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("CustomerOrder is not valid: " + customerOrderValidator.getErrors());
    }
    return customerOrder;
  }
}
