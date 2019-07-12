package validator.impl;

import dto.CountryDto;
import validator.AbstractValidator;

import java.util.Map;

public class CountryDtoValidator extends AbstractValidator<CountryDto>{

  @Override
  public Map<String, String> validate(CountryDto countryDto) {
    errors.clear();
    if (countryDto == null) {
      errors.put("Country object", "Country object is null");
      return errors;
    }

    if (!isCountryNameValid(countryDto)) {
      errors.put("Country name", "Country name should contain only capital letters and optionally a single whitespace between them");
    }

    return errors;
  }

  private boolean isCountryNameValid(CountryDto countryDto) {
    return countryDto.getName().matches("[A-Z]+(\\s[A-Z]+)*");
  }
}
