package converters.impl;

import converters.JsonConverter;
import domain.Product;

import java.util.List;

public class ProductListJsonConverter extends JsonConverter <List<Product>> {

  public ProductListJsonConverter(String jsonFilename) {
    super(jsonFilename);
  }
}
