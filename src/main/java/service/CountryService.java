package service;

import domain.Country;
import repository.abstract_repository.entity.CountryRepository;
import repository.impl.CountryRepositoryImpl;


import java.util.Optional;

public class CountryService {

  private final CountryRepository countryRepository;


  public CountryService() {
    countryRepository = new CountryRepositoryImpl();
  }

  public Optional<Country> addCountryToDb(Country country) {

    return countryRepository.addOrUpdate(country);
  }


  public Optional<Country> getCountryByName(String name) {
    return countryRepository.findCountryByName(name);
  }

}
