package validator.impl;

import dto.CategoryDto;
import validator.AbstractValidator;

import java.util.Map;

public class CategoryDtoValidator extends AbstractValidator<CategoryDto> {

  @Override
  public Map<String, String> validate(CategoryDto categoryDto) {

    errors.clear();
    if (categoryDto == null) {
      errors.put("Category object", "Category object is null");
      return errors;
    }

    if (!isCategoryNameValid(categoryDto)) {
      errors.put("Category name", "Category name is not valid");
    }
    return errors;
  }

  private boolean isCategoryNameValid(CategoryDto categoryDto) {
    return categoryDto.getName() != null && categoryDto.getName().matches("[A-Z]+(\\s[A-Z]+)*");
  }
}
