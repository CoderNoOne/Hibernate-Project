package validator.impl;

import domain.Product;
import validator.Validator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
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

    if (!areGuaranteeComponentsValid(product)) {
      errors.put("Guarantee components", "Guarantee components should be unique");
    }

    if (!isProducerValid(product)) {
      errors.putAll(getProducerValidator(product).getErrors());
    }
    if (!isCategoryValid(product)) {
      errors.putAll(getCategoryValidator(product).getErrors());
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
    return product.getName().matches("[A-Z]+(\\s[A-Z]+)*");
  }

  private boolean isProductPriceValid(Product product) {
    return product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) > 0;
  }

  private boolean isProducerValid(Product product) {
    return !getProducerValidator(product).hasErrors();
  }

  private boolean isCategoryValid(Product product) {
    return !getCategoryValidator(product).hasErrors();
  }

  private boolean areGuaranteeComponentsValid(Product product) {
    return product.getGuaranteeComponents().stream().allMatch(new HashSet<>()::add);
  }

  private CategoryValidator getCategoryValidator(Product product) {
    CategoryValidator categoryValidator = new CategoryValidator();
    categoryValidator.validate(product.getCategory());
    return categoryValidator;
  }

  private ProducerValidator getProducerValidator(Product product) {
    ProducerValidator producerValidator = new ProducerValidator();
    producerValidator.validate(product.getProducer());
    return producerValidator;
  }

}
