package mappers;

import domain.Category;
import dto.CategoryDto;

public class CategoryMapper {

  public Category mapCategoryDtoToCategory(CategoryDto categoryDto) {

    return Category.builder()
            .id(categoryDto.getId())
            .name(categoryDto.getName())
            .build();
  }

  public CategoryDto mapCategoryToCategoryDto(Category category) {
    return CategoryDto.builder()
            .id(category.getId())
            .name(category.getName())
            .build();
  }
}
