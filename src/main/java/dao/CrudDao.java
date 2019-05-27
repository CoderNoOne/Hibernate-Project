package dao;

import java.util.List;
import java.util.Optional;

public interface CrudDao<T> {

  boolean add(T t);
  void update(T t);
  void delete(Integer id);
  Optional<T> findById(Integer id);
  List<T> findAll();
  void deleteAll();

}
