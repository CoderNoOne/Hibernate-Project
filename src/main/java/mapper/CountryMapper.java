package mapper;

import domain.Country;
import dto.CountryDto;
import org.mapstruct.Mapper;

@Mapper
public interface CountryMapper {

  Country countryDtoToCountry(CountryDto countryDTO);

  CountryDto countryToCountryDto(Country country);
  
}
