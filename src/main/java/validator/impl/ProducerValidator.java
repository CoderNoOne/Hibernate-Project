package validator.impl;

import domain.Producer;
import validator.Validator;

import java.util.HashMap;
import java.util.Map;

public class ProducerValidator implements Validator<Producer> {

  private Map<String, String> errors;

  public ProducerValidator() {
    this.errors = new HashMap<>();
  }

  @Override
  public Map<String, String> validate(Producer producer) {

    if (producer == null) {
      errors.put("Producer object", "Producer object is null");
      return errors;
    }

    if (!isProducerNameValid(producer)) {
      errors.put("Producer name", "Producer name should contain only capital letters and optionally a whitespace between them");
    }

    if (!isTradeValid(producer)) {
      errors.putAll(getTradeValidator(producer).getErrors());
    }

    if(!isCountryValid(producer)){
      errors.putAll(getCountryValidator(producer).getErrors());
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

  private boolean isProducerNameValid(Producer producer) {
    return producer.getName().matches("[A-Z]+(\\s[A-Z])*");
  }

  private boolean isTradeValid(Producer producer) {
    return !getTradeValidator(producer).hasErrors();
  }

  private boolean isCountryValid(Producer producer){
    return !getCountryValidator(producer).hasErrors();
  }

  private TradeValidator getTradeValidator(Producer producer) {
    TradeValidator tradeValidator = new TradeValidator();
    tradeValidator.validate(producer.getTrade());
    return tradeValidator;
  }

  private CountryValidator getCountryValidator(Producer producer) {
    CountryValidator countryValidator = new CountryValidator();
    countryValidator.validate(producer.getCountry());
    return countryValidator;
  }
}
