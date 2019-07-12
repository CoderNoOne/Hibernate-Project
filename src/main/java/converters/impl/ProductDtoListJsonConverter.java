package converters.impl;

import converters.JsonConverter;
import dto.ProductDto;

import java.util.List;

public class ProductDtoListJsonConverter extends JsonConverter <List<ProductDto>> {

  public ProductDtoListJsonConverter(String jsonFilename) {
    super(jsonFilename);
  }
}
