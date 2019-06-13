package repository.abstract_repository.entity;

import domain.Category;
import repository.abstract_repository.base.CrudRepository;

import java.util.Optional;

public interface CategoryRepository  extends CrudRepository <Category,Long> {
  Optional<Category> findCategoryByName(String name);
}
