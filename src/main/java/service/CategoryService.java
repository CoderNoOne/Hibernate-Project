package service;

import domain.Category;
import repository.abstract_repository.entity.CategoryRepository;
import repository.impl.CategoryRepositoryImpl;

import java.util.Optional;

public class CategoryService {

  private final CategoryRepository categoryRepository;

  public CategoryService() {
    this.categoryRepository = new CategoryRepositoryImpl();
  }

  public Optional<Category> addCategoryToDb(Category category){
    return categoryRepository.addOrUpdate(category);
  }

  public Optional <Category> getCategoryByName(String name){
    return categoryRepository.findCategoryByName(name);
  }

  public boolean isCategoryUniqueByName(String name){
    return getCategoryByName(name).isEmpty();
  }
}
