package service.entity;

import domain.Category;
import exception.AppException;
import repository.abstract_repository.entity.CategoryRepository;
import repository.impl.CategoryRepositoryImpl;

import java.util.Optional;

public class CategoryService {

  private final CategoryRepository categoryRepository;

  public CategoryService() {
    this.categoryRepository = new CategoryRepositoryImpl();
  }

  public Optional<Category> addCategoryToDb(Category category) {

    if (category == null) {
      throw new AppException("Category is null");
    }

    if (!isCategoryUniqueByName(category.getName())) {
      throw new AppException("Category is not unique by name: " + category.getName());
    }
    return categoryRepository.addOrUpdate(category);
  }

  public Optional<Category> getCategoryByName(String name) {
    return categoryRepository.findCategoryByName(name);
  }


  private boolean isCategoryUniqueByName(String name) {

    if (name == null) {
      throw new AppException("Category name is null");
    }
    return getCategoryByName(name).isEmpty();
  }

  public void deleteAllCategories() {
    categoryRepository.deleteAll();
  }

}
