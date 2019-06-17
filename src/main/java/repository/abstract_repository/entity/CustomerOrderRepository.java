package repository.abstract_repository.entity;

import domain.Category;
import domain.CustomerOrder;
import domain.Product;
import repository.abstract_repository.base.CrudRepository;

import java.util.List;

public interface CustomerOrderRepository extends CrudRepository <CustomerOrder, Long> {
  List<Product> getProductsByNameAndCategory(String name, Category category);
}
