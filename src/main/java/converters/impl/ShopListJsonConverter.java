package converters.impl;

import converters.JsonConverter;
import domain.Shop;

import java.util.List;

public class ShopListJsonConverter extends JsonConverter <List<Shop>> {

  public ShopListJsonConverter(String jsonFilename) {
    super(jsonFilename);
  }
}
