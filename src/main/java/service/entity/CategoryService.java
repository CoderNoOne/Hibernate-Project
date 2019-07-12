package service.entity;

import mappers.CategoryMapper;
import domain.Category;
import dto.CategoryDto;
import exception.AppException;
import repository.abstract_repository.entity.CategoryRepository;
import repository.impl.CategoryRepositoryImpl;

import java.util.Optional;

public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  public CategoryService() {
    this.categoryRepository = new CategoryRepositoryImpl();
    this.categoryMapper = new CategoryMapper();
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

  public Optional<CategoryDto> getCategoryByName(String name) {
    return categoryRepository.findCategoryByName(name).isPresent() ?
            Optional.of(categoryMapper.mapCategoryToCategoryDto(categoryRepository.findCategoryByName(name).get()))
            : Optional.empty();
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

  public CategoryDto getCategoryFromDbIfExists(CategoryDto categoryDto) {
    return getCategoryByName(categoryDto.getName()).orElse(categoryDto);
  }

}
