package service;

import domain.Country;
import repository.abstract_repository.entity.CountryRepository;
import repository.impl.CountryRepositoryImpl;
import validator.impl.CustomerValidator;

import java.util.Optional;

public class CountryService {

  private final CountryRepository countryRepository;
  private final CustomerValidator customerValidator;

  public CountryService() {
    countryRepository = new CountryRepositoryImpl();
    customerValidator = new CustomerValidator();
  }

  public Optional<Country> addCountryToDb(Country country) {

    return countryRepository.addOrUpdate(country);
  }


  public Optional<Country> findCountryByName(String name) {
    return countryRepository.findCountryByName(name);
  }
}
