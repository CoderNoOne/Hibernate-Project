package validator.impl;

import dto.ProductDto;
import validator.AbstractValidator;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;

public class ProductDtoValidator extends AbstractValidator<ProductDto> {

  @Override
  public Map<String, String> validate(ProductDto productDto) {
    errors.clear();

    if (productDto == null) {
      errors.put("Product object", "Product object is null");
      return errors;
    }

    if (!isProductNameValid(productDto)) {
      errors.put("Product name", "Product name should contain only capital letters and optionally a whitespace between letters");
    }

    if (!isProductPriceValid(productDto)) {
      errors.put("Product price", "Product price should be greater than 0");
    }

    if (!areGuaranteeComponentsValid(productDto)) {
      errors.put("Guarantee components", "Guarantee components should be unique");
    }

    if (!isProducerValid(productDto)) {
      errors.putAll(getProducerDtoValidator(productDto).getErrors());
    }
    if (!isCategoryValid(productDto)) {
      errors.putAll(getCategoryDtoValidator(productDto).getErrors());
    }
    return errors;
  }

  private boolean isProductNameValid(ProductDto productDto) {
    return productDto.getName().matches("[A-Z]+(\\s[A-Z]+)*");
  }

  private boolean isProductPriceValid(ProductDto productDto) {
    return productDto.getPrice() == null || productDto.getPrice().compareTo(BigDecimal.ZERO) > 0;
  }

  private boolean isProducerValid(ProductDto productDto) {
    return !getProducerDtoValidator(productDto).hasErrors();
  }

  private boolean isCategoryValid(ProductDto productDto) {
    return !getCategoryDtoValidator(productDto).hasErrors();
  }

  private boolean areGuaranteeComponentsValid(ProductDto productDto) {
    return productDto.getGuaranteeComponents().stream().allMatch(new HashSet<>()::add);
  }

  private CategoryDtoValidator getCategoryDtoValidator(ProductDto productDto) {
    CategoryDtoValidator categoryDtoValidator = new CategoryDtoValidator();
    categoryDtoValidator.validate(productDto.getCategoryDto());
    return categoryDtoValidator;
  }

  private ProducerDtoValidator getProducerDtoValidator(ProductDto productDto) {
    ProducerDtoValidator producerDtoValidator = new ProducerDtoValidator();
    producerDtoValidator.validate(productDto.getProducerDto());
    return producerDtoValidator;
  }

}
