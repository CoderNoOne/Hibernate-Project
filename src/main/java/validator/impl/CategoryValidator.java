package validator.impl;

import domain.Category;
import validator.AbstractValidator;

import java.util.Map;

public class CategoryValidator extends AbstractValidator<Category> {

  @Override
  public Map<String, String> validate(Category category) {

    if (category == null) {
      errors.put("Category object", "Category object is null");
      return errors;
    }

    if (!isCategoryNameValid(category)) {
      errors.put("Category name", "Category name is not valid");
    }
    return errors;
  }

  private boolean isCategoryNameValid(Category category) {
    return category.getName().matches("[A-Z]+(\\s[A-Z]+)*");
  }
}
