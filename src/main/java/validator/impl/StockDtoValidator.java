package validator.impl;

import dto.StockDto;
import validator.AbstractValidator;

import java.util.Map;

public class StockDtoValidator extends AbstractValidator<StockDto> {

  @Override
  public Map<String, String> validate(StockDto stockDto) {
    errors.clear();

    if (stockDto == null) {
      errors.put("Stock object", "Stock object is null");
      return errors;
    }

    if (!isQuantityValid(stockDto)) {
      errors.put("Quantity", "quantity should be greater or equal to 0");
    }

    if (!isProductValid(stockDto)) {
      errors.putAll(getProductValidator(stockDto).getErrors());
    }

    if (!isShopValid(stockDto)) {
      errors.putAll(getShopValidator(stockDto).getErrors());
    }
    return errors;
  }

  private boolean isShopValid(StockDto stockDto) {
    return stockDto.getShopDto() != null && !getShopValidator(stockDto).hasErrors();

  }

  private boolean isProductValid(StockDto stockDto) {
    return stockDto.getProductDto() != null && !getProductValidator(stockDto).hasErrors();

  }

  private boolean isQuantityValid(StockDto stockDto) {
    return stockDto.getQuantity() != null && stockDto.getQuantity() >= 0;

  }

  private ProductDtoValidator getProductValidator(StockDto stockDto) {
    ProductDtoValidator productValidator = new ProductDtoValidator();
    productValidator.validate(stockDto.getProductDto());
    return productValidator;
  }

  private ShopDtoValidator getShopValidator(StockDto stockDto) {
    ShopDtoValidator shopValidator = new ShopDtoValidator();
    shopValidator.validate(stockDto.getShopDto());
    return shopValidator;
  }
}
