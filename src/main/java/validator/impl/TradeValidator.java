package validator.impl;

import domain.Trade;
import validator.AbstractValidator;

import java.util.Map;

public class TradeValidator extends AbstractValidator<Trade> {

  @Override
  public Map<String, String> validate(Trade trade) {

    if (trade == null) {
      errors.put("Trade object", "Trade object is null");
      return errors;
    }

    if (!isTradeNameValid(trade)) {
      errors.put("Trade name", "Trade name should contain only capital letters and optionally whitespace between letters");
    }
    return errors;
  }

  private boolean isTradeNameValid(Trade trade) {
    return trade.getName().matches("[A-Z]+(\\s[A-Z]+)*");
  }

}
