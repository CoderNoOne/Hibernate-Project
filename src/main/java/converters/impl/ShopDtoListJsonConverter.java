package converters.impl;

import converters.JsonConverter;
import dto.ShopDto;

import java.util.List;

public class ShopDtoListJsonConverter extends JsonConverter <List<ShopDto>> {

  public ShopDtoListJsonConverter(String jsonFilename) {
    super(jsonFilename);
  }
}
