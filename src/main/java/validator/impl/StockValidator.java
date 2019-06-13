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

    return errors;
  }

  @Override
  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  private boolean isQuantityValid(Stock stock){
    return stock.getQuantity() >= 0;
  }
}
