package service.entity;

import dto.CountryDto;
import exception.AppException;
import mapper.ModelMapper;
import repository.abstract_repository.entity.CountryRepository;
import repository.impl.CountryRepositoryImpl;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CountryService {

  private final CountryRepository countryRepository;

  public CountryService() {
    this.countryRepository = new CountryRepositoryImpl();
  }

  public CountryService(CountryRepository countryRepository) {
    this.countryRepository = countryRepository;
  }

  public Optional<CountryDto> addCountryToDb(CountryDto countryDto) {

    if (countryDto == null) {
      throw new AppException("Country is null");
    }

    if (!isCountryUniqueByName(countryDto.getName())) {
      throw new AppException("Country is not unique by name: " + countryDto.getName());
    }

    return countryRepository
            .addOrUpdate(ModelMapper.mapCountryDtoToCountry(countryDto))
            .map(ModelMapper::mapCountryToCountryDto);
  }

  private boolean isCountryUniqueByName(String name) {

    if (name == null) {
      throw new AppException("Country name is null");
    }

    return getCountryByName(name).isEmpty();
  }


  private Optional<CountryDto> getCountryByName(String name) {

    if (name == null) {
      throw new AppException("Country name is null");
    }

    return countryRepository.findCountryByName(name)
            .map(ModelMapper::mapCountryToCountryDto);
  }

  public void deleteAllCountries() {
    countryRepository.deleteAll();
  }

  CountryDto getCountryFromDbIfExists(CountryDto countryDto) {

    if (countryDto == null) {

      throw new AppException("CountryDto is null");
    }

    return getCountryByName(countryDto.getName()).orElse(countryDto);
  }

  public List<CountryDto> findAllCountries() {
    return countryRepository.findAll()
            .stream()
            .map(ModelMapper::mapCountryToCountryDto)
            .collect(Collectors.toList());
  }

  public void deleteCountryByName(String name) {

    if (name == null || name.equals("")) {
      throw new AppException("Country name is null/ undefined: " + name);
    }
    countryRepository.deleteCountryByName(name);
  }
}
