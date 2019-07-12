package converters.impl;

import converters.JsonConverter;
import dto.CategoryDto;

import java.util.List;

public class CategoryDtoListJsonConverter extends JsonConverter <List<CategoryDto>> {

  public CategoryDtoListJsonConverter(String jsonFilename) {
    super(jsonFilename);
  }
}
