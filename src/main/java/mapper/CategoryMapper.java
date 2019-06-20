package mapper;

import domain.Category;
import dto.CategoryDto;
import org.mapstruct.Mapper;

@Mapper
public interface CategoryMapper {

  CategoryDto categoryToCategoryDTO(Category category);

  Category categoryDTOToCategory(CategoryDto categoryDTO);
}
