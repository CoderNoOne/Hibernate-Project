package converters.impl;

import converters.JsonConverter;
import dto.ProducerDto;

import java.util.List;

public class ProducerDtoListJsonConverter extends JsonConverter <List<ProducerDto>> {

  public ProducerDtoListJsonConverter(String jsonFilename) {
    super(jsonFilename);
  }
}
