package service.entity;

import domain.Country;
import exception.AppException;
import repository.abstract_repository.entity.CountryRepository;
import repository.impl.CountryRepositoryImpl;


import java.util.Optional;

public class CountryService {

  private final CountryRepository countryRepository;


  public CountryService() {
    countryRepository = new CountryRepositoryImpl();
  }

  public Optional<Country> addCountryToDb(Country country) {

    if (country == null) {
      throw new AppException("Country is null");
    }

    if (!isCountryUniqueByName(country.getName())) {
      throw new AppException("Country is not unique by name: " + country.getName());
    }

    return countryRepository.addOrUpdate(country);
  }

  public boolean isCountryUniqueByName(String name) {
    if (name == null) {
      throw new AppException("Country name is null");
    }
    return countryRepository.findCountryByName(name).isEmpty();
  }


  public Optional<Country> getCountryByName(String name) {
    return countryRepository.findCountryByName(name);
  }

  public void deleteAllCountries(){
    countryRepository.deleteAll();
  }

  public Country getCountryFromDbIfExists(Country country) {
    return getCountryByName(country.getName()).orElse(country);
  }

}
