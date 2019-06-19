package mapper;

import domain.Category;
import dto.CategoryDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CategoryMapper {

  CategoryDTO categoryToCategoryDTO(Category category);

  Category categoryDTOToCategory(CategoryDTO categoryDTO);
}
