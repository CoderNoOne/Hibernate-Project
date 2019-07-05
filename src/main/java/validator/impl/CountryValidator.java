package validator.impl;

import domain.Country;
import validator.AbstractValidator;

import java.util.Map;

public class CountryValidator extends AbstractValidator<Country>{

  @Override
  public Map<String, String> validate(Country country) {
    errors.clear();
    if (country == null) {
      errors.put("Country object", "Country object is null");
      return errors;
    }

    if (!isCountryNameValid(country)) {
      errors.put("Country name", "Country name should contain only capital letters and optionally a single whitespace between them");
    }

    return errors;
  }

  private boolean isCountryNameValid(Country country) {
    return country.getName().matches("[A-Z]+(\\s[A-Z]+)*");
  }
}
