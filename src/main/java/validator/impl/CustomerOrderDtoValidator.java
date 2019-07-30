package validator.impl;

import dto.CustomerOrderDto;
import validator.AbstractValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class CustomerOrderDtoValidator extends AbstractValidator<CustomerOrderDto> {

  @Override
  public Map<String, String> validate(CustomerOrderDto customerOrderDto) {
    errors.clear();
    if (customerOrderDto == null) {
      errors.put("CustomerOrderDto object", "CustomerOrder object is null");
      return errors;
    }

    if (!isDiscountValid(customerOrderDto)) {
      errors.put("Discount", "Discount should be in the range <0.0,1.0>");
    }

    if (!isOrderDateValid(customerOrderDto)) {
      errors.put("Order date", "Order date should be at present day or in the future");
    }

    if (!isProductQuantityValid(customerOrderDto)) {
      errors.put("Product Quantity", "Product quantity should be greater than 0");

    }
    if (!isProductValid(customerOrderDto)) {
      errors.putAll(getProductValidator(customerOrderDto).getErrors());
    }
    if (!isCustomerValid(customerOrderDto)) {
      errors.putAll(getCustomerValidator(customerOrderDto).getErrors());
    }
    if (!isPaymentValid(customerOrderDto)) {
      errors.putAll(getPaymentValidator(customerOrderDto).getErrors());
    }

    return errors;
  }

  private boolean isProductQuantityValid(CustomerOrderDto customerOrderDto) {
    return customerOrderDto.getQuantity() != null && customerOrderDto.getQuantity() > 0;
  }

  private boolean isDiscountValid(CustomerOrderDto customerOrder) {
    return customerOrder.getDiscount() != null && customerOrder.getDiscount().compareTo(BigDecimal.ZERO) >= 0 && customerOrder.getDiscount().compareTo(BigDecimal.ONE) <= 0;
  }

  private boolean isOrderDateValid(CustomerOrderDto customerOrder) {
    return customerOrder.getDate() != null && customerOrder.getDate().compareTo(LocalDate.now()) >= 0;
  }

  private boolean isPaymentValid(CustomerOrderDto customerOrder) {
    return customerOrder.getPayment() != null && !getPaymentValidator(customerOrder).hasErrors();
  }

  private boolean isCustomerValid(CustomerOrderDto customerOrder) {
    return customerOrder.getCustomer() != null && !getCustomerValidator(customerOrder).hasErrors();
  }

  private boolean isProductValid(CustomerOrderDto customerOrder) {
    return customerOrder.getProduct() != null && !getProductValidator(customerOrder).hasErrors();
  }

  private ProductDtoValidator getProductValidator(CustomerOrderDto customerOrder) {
    ProductDtoValidator productValidator = new ProductDtoValidator();
    productValidator.validate(customerOrder.getProduct());
    return productValidator;
  }

  private CustomerDtoValidator getCustomerValidator(CustomerOrderDto customerOrderDto) {
    var customerDtoValidator = new CustomerDtoValidator();
    customerDtoValidator.validate(customerOrderDto.getCustomer());
    return customerDtoValidator;
  }

  private PaymentDtoValidator getPaymentValidator(CustomerOrderDto customerOrder) {
    var paymentValidator = new PaymentDtoValidator();
    paymentValidator.validate(customerOrder.getPayment());
    return paymentValidator;
  }
}
