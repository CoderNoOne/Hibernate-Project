package converters.impl;

import converters.JsonConverter;
import domain.Category;

import java.util.List;

public class CategoryListJsonConverter extends JsonConverter <List<Category>> {

  public CategoryListJsonConverter(String jsonFilename) {
    super(jsonFilename);
  }
}
