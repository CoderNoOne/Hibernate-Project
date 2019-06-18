package repository.abstract_repository.entity;

import domain.Category;
import domain.CustomerOrder;
import domain.Product;
import repository.abstract_repository.base.CrudRepository;

import java.util.List;
import java.util.Map;

public interface CustomerOrderRepository extends CrudRepository <CustomerOrder, Long> {

  Map<Product, Integer> findNumberOfOrdersForSpecifiedProducts(List<Product> productList);
}
