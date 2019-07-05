package converters.impl;

import converters.JsonConverter;
import domain.Country;

import java.util.List;

public class CountryListJsonConverter extends JsonConverter <List<Country>> {

  public CountryListJsonConverter(String jsonFilename) {
    super(jsonFilename);
  }
}
