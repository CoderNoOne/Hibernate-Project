package validator.impl;

import domain.Product;
import validator.Validator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ProductValidator implements Validator<Product> {

  private Map<String, String> errors;

  public ProductValidator() {
    this.errors = new HashMap<>();
  }

  @Override
  public Map<String, String> validate(Product product) {

    if (product == null) {
      errors.put("Product object", "Product object is null");
      return errors;
    }

    if (!isProductNameValid(product)) {
      errors.put("Product name", "Product name should contain only capital letters and optionally a whitespace between letters");
    }

    if (!isProductPriceValid(product)) {
      errors.put("Product price", "Product price should be greater than 0");
    }

    return errors;
  }

  public Map<String, String> getErrors() {
    return errors;
  }

  @Override
  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  private boolean isProductNameValid(Product product) {
    return product.getName().matches("[A-Z]+(\\s[A-Z])*");
  }

  private boolean isProductPriceValid(Product product) {
    return product.getPrice().compareTo(BigDecimal.ZERO) > 0;
  }
}
