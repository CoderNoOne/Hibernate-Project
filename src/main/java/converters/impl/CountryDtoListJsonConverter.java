package converters.impl;

import converters.JsonConverter;
import dto.CountryDto;

import java.util.List;

public class CountryDtoListJsonConverter extends JsonConverter <List<CountryDto>> {

  public CountryDtoListJsonConverter(String jsonFilename) {
    super(jsonFilename);
  }
}
