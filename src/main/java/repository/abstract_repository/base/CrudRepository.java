package repository.abstract_repository.base;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID> {
  Optional<T> addOrUpdate(T t);

  Optional<T> findShopById(ID id);

  List<T> findAll();

  Optional<T> deleteById(ID id);

  void deleteAll();
}
