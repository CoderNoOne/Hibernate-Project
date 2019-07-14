package service.entity;

import mappers.CategoryMapper;
import dto.CategoryDto;
import exception.AppException;
import repository.abstract_repository.entity.CategoryRepository;
import repository.impl.CategoryRepositoryImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  public CategoryService() {
    this.categoryRepository = new CategoryRepositoryImpl();
    this.categoryMapper = new CategoryMapper();
  }

  public CategoryService(CategoryRepository categoryRepository){
    this.categoryRepository = categoryRepository;
    this.categoryMapper = new CategoryMapper();
  }

  public Optional<CategoryDto> addCategoryToDb(CategoryDto categoryDto) {

    if (categoryDto == null) {
      throw new AppException("Category is null");
    }

    if (!isCategoryUniqueByName(categoryDto.getName())) {
      throw new AppException("Category is not unique by name: " + categoryDto.getName());
    }
    return categoryRepository
            .addOrUpdate(categoryMapper.mapCategoryDtoToCategory(categoryDto))
            .map(categoryMapper::mapCategoryToCategoryDto);
  }

  public Optional<CategoryDto> getCategoryByName(String name) {

    return categoryRepository.findCategoryByName(name)
            .map(categoryMapper::mapCategoryToCategoryDto);
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
  public List<CategoryDto> findAllCountries(){
    return categoryRepository.findAll()
            .stream()
            .map(categoryMapper::mapCategoryToCategoryDto)
            .collect(Collectors.toList());
  }


}
