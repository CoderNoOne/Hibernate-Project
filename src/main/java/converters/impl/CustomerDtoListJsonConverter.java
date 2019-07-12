package converters.impl;

import converters.JsonConverter;
import dto.CustomerDto;

import java.util.List;

public class CustomerDtoListJsonConverter extends JsonConverter <List<CustomerDto>> {

  public CustomerDtoListJsonConverter(String jsonFilename) {
    super(jsonFilename);
  }
}
