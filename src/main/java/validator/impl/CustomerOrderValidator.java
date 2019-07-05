package validator.impl;

import domain.CustomerOrder;
import validator.AbstractValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class CustomerOrderValidator extends AbstractValidator<CustomerOrder> {

  @Override
  public Map<String, String> validate(CustomerOrder customerOrder) {
    errors.clear();
    if (customerOrder == null) {
      errors.put("CustomerOrder object", "CutomerOrder object is null");
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

  private boolean isDiscountValid(CustomerOrder customerOrder) {
    return customerOrder.getDiscount().compareTo(BigDecimal.ZERO) >= 0 && customerOrder.getDiscount().compareTo(BigDecimal.ONE) <= 0;
  }

  private boolean isOrderDateValid(CustomerOrder customerOrder) {
    return customerOrder.getDate().compareTo(LocalDate.now()) >= 0;
  }

  private boolean isPaymentValid(CustomerOrder customerOrder){
    return !getPaymentValidator(customerOrder).hasErrors();
  }

  private boolean isCustomerValid(CustomerOrder customerOrder){
    return !getCustomerValidator(customerOrder).hasErrors();
  }

  private boolean isProductValid(CustomerOrder customerOrder){
    return !getProductValidator(customerOrder).hasErrors();
  }

  private ProductValidator getProductValidator(CustomerOrder customerOrder) {
    ProductValidator productValidator = new ProductValidator();
    productValidator.validate(customerOrder.getProduct());
    return productValidator;
  }

  private CustomerValidator getCustomerValidator(CustomerOrder customerOrder) {
    var customerValidator = new CustomerValidator();
    customerValidator.validate(customerOrder.getCustomer());
    return customerValidator;
  }

  private PaymentValidator getPaymentValidator(CustomerOrder customerOrder) {
    var paymentValidator = new PaymentValidator();
    paymentValidator.validate(customerOrder.getPayment());
    return paymentValidator;
  }
}
