package validator.impl;


import dto.CustomerOrderDto;
import validator.AbstractValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class CustomerOrderDtoValidator extends AbstractValidator<CustomerOrderDto> {

  @Override
  public Map<String, String> validate(CustomerOrderDto customerOrder) {
    errors.clear();
    if (customerOrder == null) {
      errors.put("CustomerOrderDto object", "CutomerOrder object is null");
      return errors;
    }

    if (!isDiscountValid(customerOrder)) {
      errors.put("Discount", "Discount shoould be in the range <0.0,1.0>");
    }

    if (!isOrderDateValid(customerOrder)) {
      errors.put("Order date", "Order date should be at present day or in the future");
    }

    if(!isProductValid(customerOrder)){
      errors.putAll(getProductValidator(customerOrder).getErrors());
    }
    if(!isCustomerValid(customerOrder)){
      errors.putAll(getCustomerValidator(customerOrder).getErrors());
    }
    if(!isPaymentValid(customerOrder)){
      errors.putAll(getPaymentValidator(customerOrder).getErrors());
    }

    return errors;
  }

  private boolean isDiscountValid(CustomerOrderDto customerOrder) {
    return customerOrder.getDiscount().compareTo(BigDecimal.ZERO) >= 0 && customerOrder.getDiscount().compareTo(BigDecimal.ONE) <= 0;
  }

  private boolean isOrderDateValid(CustomerOrderDto customerOrder) {
    return customerOrder.getDate().compareTo(LocalDate.now()) >= 0;
  }

  private boolean isPaymentValid(CustomerOrderDto customerOrder){
    return !getPaymentValidator(customerOrder).hasErrors();
  }

  private boolean isCustomerValid(CustomerOrderDto customerOrder){
    return !getCustomerValidator(customerOrder).hasErrors();
  }

  private boolean isProductValid(CustomerOrderDto customerOrder){
    return !getProductValidator(customerOrder).hasErrors();
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
