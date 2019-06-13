package validator.impl;

import domain.Trade;
import validator.Validator;

import java.util.HashMap;
import java.util.Map;

public class TradeValidator implements Validator<Trade> {

  private Map<String, String> errors;

  public TradeValidator() {
    this.errors = new HashMap<>();
  }

  @Override
  public Map<String, String> validate(Trade trade) {

    if (trade == null) {
      errors.put("Trade object", "Trade object is null");
      return errors;
    }

    if(!isTradeNameValid(trade)){
      errors.put("Trade name", "Trade name should contain only capital letters and optionally whitespace between letters");
    }

    return null;
  }

  public Map<String, String> getErrors() {
    return errors;
  }

  @Override
  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  private boolean isTradeNameValid(Trade trade) {
    return trade.getName().matches("[A-Z]+(\\s[A-Z])*");
  }

}
