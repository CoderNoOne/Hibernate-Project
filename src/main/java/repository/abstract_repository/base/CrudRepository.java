package repository.abstract_repository.base;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID> {
  Optional<T> add(T t);

  Optional<T> findById(ID id);

  List<T> findAll();

  Optional<T> deleteById(ID id);

  void deleteAll();
}
