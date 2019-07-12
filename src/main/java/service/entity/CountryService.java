package service.entity;

import dto.CountryDto;
import exception.AppException;
import mappers.CountryMapper;
import repository.abstract_repository.entity.CountryRepository;
import repository.impl.CountryRepositoryImpl;


import java.util.Optional;

public class CountryService {

  private final CountryRepository countryRepository;
  private final CountryMapper countryMapper;

  public CountryService() {
    this.countryRepository = new CountryRepositoryImpl();
    this.countryMapper = new CountryMapper();
  }

  public Optional<CountryDto> addCountryToDb(CountryDto countryDto) {

    if (countryDto == null) {
      throw new AppException("Country is null");
    }

    if (!isCountryUniqueByName(countryDto.getName())) {
      throw new AppException("Country is not unique by name: " + countryDto.getName());
    }

    return countryRepository
            .addOrUpdate(countryMapper.mapCountryDtoToCountry(countryDto))
            .map(countryMapper::mapCountryToCountryDto);
  }

  public boolean isCountryUniqueByName(String name) {
    if (name == null) {
      throw new AppException("Country name is null");
    }
    return countryRepository.findCountryByName(name).isEmpty();
  }


  public Optional<CountryDto> getCountryByName(String name) {
    return countryRepository.findCountryByName(name)
            .map(countryMapper::mapCountryToCountryDto);
  }

  public void deleteAllCountries() {
    countryRepository.deleteAll();
  }

  public CountryDto getCountryFromDbIfExists(CountryDto countryDto) {
    return getCountryByName(countryDto.getName()).orElse(countryDto);
  }
}
