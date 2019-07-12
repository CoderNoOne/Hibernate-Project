package validator.impl;


import dto.TradeDto;
import validator.AbstractValidator;

import java.util.Map;

public class TradeDtoValidator extends AbstractValidator<TradeDto> {

  @Override
  public Map<String, String> validate(TradeDto tradeDto) {
    errors.clear();
    if (tradeDto == null) {
      errors.put("Trade object", "Trade object is null");
      return errors;
    }

    if (!isTradeNameValid(tradeDto)) {
      errors.put("Trade name", "Trade name should contain only capital letters and optionally whitespace between letters");
    }
    return errors;
  }

  private boolean isTradeNameValid(TradeDto tradeDto) {
    return tradeDto.getName().matches("[A-Z]+(\\s[A-Z]+)*");
  }

}
