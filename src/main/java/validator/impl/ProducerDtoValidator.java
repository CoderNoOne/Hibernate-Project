package validator.impl;

import dto.ProducerDto;
import validator.AbstractValidator;

import java.util.Map;

public class ProducerDtoValidator extends AbstractValidator<ProducerDto>  {

  @Override
  public Map<String, String> validate(ProducerDto producerDto) {
    errors.clear();
    if (producerDto == null) {
      errors.put("Producer object", "Producer object is null");
      return errors;
    }

    if (!isProducerNameValid(producerDto)) {
      errors.put("Producer name", "Producer name should contain only capital letters and optionally a whitespace between them");
    }

    if (!isTradeValid(producerDto)) {
      errors.putAll(getTradeValidator(producerDto).getErrors());
    }

    if(!isCountryValid(producerDto)){
      errors.putAll(getCountryValidator(producerDto).getErrors());
    }
    return errors;
  }

  private boolean isProducerNameValid(ProducerDto producerDto) {
    return producerDto.getName().matches("[A-Z]+(\\s[A-Z]+)*");
  }

  private boolean isTradeValid(ProducerDto producerDto) {
    return !getTradeValidator(producerDto).hasErrors();
  }

  private boolean isCountryValid(ProducerDto producerDto){
    return !getCountryValidator(producerDto).hasErrors();
  }

  private TradeDtoValidator getTradeValidator(ProducerDto producerDto) {
    TradeDtoValidator tradeValidator = new TradeDtoValidator();
    tradeValidator.validate(producerDto.getTrade());
    return tradeValidator;
  }

  private CountryDtoValidator getCountryValidator(ProducerDto producerDto) {
    CountryDtoValidator countryValidator = new CountryDtoValidator();
    countryValidator.validate(producerDto.getCountry());
    return countryValidator;
  }
}
