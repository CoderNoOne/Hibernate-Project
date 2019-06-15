package validator.impl;

import domain.Stock;
import validator.Validator;

import java.util.HashMap;
import java.util.Map;

public class StockValidator implements Validator<Stock> {

  private Map<String, String> errors;

  public StockValidator() {
    this.errors = new HashMap<>();
  }

  @Override
  public Map<String, String> validate(Stock stock) {

    if(stock == null){
      errors.put("Stock object", "Stock object is null");
      return errors;
    }

    if(!isQuantityValid(stock)){
      errors.put("Quantity", "quantity should be greater or equal to 0");
    }

    if(!isProductValid(stock)){
      errors.putAll(getProductValidator(stock).getErrors());
    }

    if(!isShopValid(stock)){
      errors.putAll(getShopValidator(stock).getErrors());
    }
    return errors;
  }

  private boolean isShopValid(Stock stock) {
    return !getShopValidator(stock).hasErrors();
  }

  private boolean isProductValid(Stock stock) {
    return !getProductValidator(stock).hasErrors();
  }

  @Override
  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  private boolean isQuantityValid(Stock stock){
    return stock.getQuantity() >= 0;
  }

  private ProductValidator getProductValidator(Stock stock){
    ProductValidator productValidator = new ProductValidator();
    productValidator.validate(stock.getProduct());
    return productValidator;
  }

  private ShopValidator getShopValidator(Stock stock){
    ShopValidator shopValidator = new ShopValidator();
    shopValidator.validate(stock.getShop());
    return shopValidator;
  }
}
