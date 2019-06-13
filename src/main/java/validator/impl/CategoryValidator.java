package validator.impl;

import domain.Category;
import validator.Validator;

import java.util.HashMap;
import java.util.Map;

public class CategoryValidator implements Validator<Category> {

  private Map<String, String> errors;

  public CategoryValidator() {
    this.errors = new HashMap<>();
  }

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

  @Override
  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  public Map<String, String> getErrors() {
    return errors;
  }

  private boolean isCategoryNameValid(Category category) {
    return category.getName().matches("[A-Z]+(\\s[A-Z])*");
  }
}
