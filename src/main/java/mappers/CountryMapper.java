package mappers;

import domain.Country;
import dto.CountryDto;

public class CountryMapper {

  public Country mapCountryDtoToCountry(CountryDto countryDto){

    return Country.builder()
            .id(countryDto.getId())
            .name(countryDto.getName())
            .build();
  }

  public CountryDto mapCountryToCountryDto(Country country){

    return CountryDto.builder()
            .id(country.getId())
            .name(country.getName())
            .build();
  }

}
