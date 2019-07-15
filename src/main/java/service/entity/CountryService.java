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

  public boolean isCountryUniqueByName(String name) {
    if (name == null) {
      throw new AppException("Country name is null");
    }
    return countryRepository.findCountryByName(name).isEmpty();
  }


  public Optional<CountryDto> getCountryByName(String name) {
    return countryRepository.findCountryByName(name)
            .map(ModelMapper::mapCountryToCountryDto);
  }

  public void deleteAllCountries() {
    countryRepository.deleteAll();
  }

  public CountryDto getCountryFromDbIfExists(CountryDto countryDto) {
    return getCountryByName(countryDto.getName()).orElse(countryDto);
  }

  public List<CountryDto> findAllCountries() {
    return countryRepository.findAll()
            .stream()
            .map(ModelMapper::mapCountryToCountryDto)
            .collect(Collectors.toList());
  }
}
