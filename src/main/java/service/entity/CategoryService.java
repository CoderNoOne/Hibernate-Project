package service.entity;

import dto.CategoryDto;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import mapper.ModelMapper;
import repository.abstract_repository.entity.CategoryRepository;
import repository.impl.CategoryRepositoryImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public CategoryService() {
    this.categoryRepository = new CategoryRepositoryImpl();
  }

  public Optional<CategoryDto> addCategoryToDb(CategoryDto categoryDto) {

    if (categoryDto == null) {
      throw new AppException("Category is null");
    }

    if (!isCategoryUniqueByName(categoryDto.getName())) {
      throw new AppException("Category is not unique by name: " + categoryDto.getName());
    }
    return categoryRepository
            .addOrUpdate(ModelMapper.mapCategoryDtoToCategory(categoryDto))
            .map(ModelMapper::mapCategoryToCategoryDto);
  }

  Optional<CategoryDto> getCategoryByName(String name) {

    if (name == null || name.equals("")) {
      throw new AppException("Category name is null/undefined");
    }
    return categoryRepository.findCategoryByName(name)
            .map(ModelMapper::mapCategoryToCategoryDto);
  }

  private boolean isCategoryUniqueByName(String name) {

    if (name == null || name.equals("")) {
      throw new AppException("Category name is null/undefined");
    }

    return getCategoryByName(name).isEmpty();
  }

  public void deleteAllCategories() {
    categoryRepository.deleteAll();
  }

  CategoryDto getCategoryFromDbIfExists(CategoryDto categoryDto) {

    if (categoryDto == null) {
      throw new AppException("CategoryDto is null");
    }
    return getCategoryByName(categoryDto.getName()).orElse(categoryDto);
  }

  List<CategoryDto> findAllCategories() {
    return categoryRepository.findAll()
            .stream()
            .map(ModelMapper::mapCategoryToCategoryDto)
            .collect(Collectors.toList());
  }

  public void deleteCategoryByName(String name) {

    if (name == null || name.equals("")) {
      throw new AppException("Category name is null/ undefined: " + name);
    }
    categoryRepository.deleteCategoryByName(name);
  }
}
