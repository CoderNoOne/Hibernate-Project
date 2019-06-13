package validator.impl;

import domain.Country;
import service.CountryService;
import validator.Validator;

import java.util.HashMap;
import java.util.Map;

public class CountryValidator implements Validator<Country> {

  private Map<String, String> errors;
//  private CountryService countryService;

  public CountryValidator() {
    this.errors = new HashMap<>();
//    this.countryService = new CountryService();
  }

  @Override
  public Map<String, String> validate(Country country) {

    if (country == null) {
      errors.put("Country object", "Country object is null");
      return errors;
    }

    if (!isCountryNameValid(country)) {
      errors.put("Country name", "Country name should contain only capital letters and optionally a single whitespace between them");
    }

//    if (!isCountryNameUnique(country)) {
//      errors.put("Country name uniqueness", "Country name is not unique - already exists in a database");
//    }
    return errors;
  }

  public Map<String, String> getErrors() {
    return errors;
  }

  @Override
  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  private boolean isCountryNameValid(Country country) {
    return country.getName().matches("[A-Z]+(\\s[A-Z])*");
  }

//  private boolean isCountryNameUnique(Country country) {
//    return countryService.getCountryByName(country.getName()).isEmpty();
//  }
}
