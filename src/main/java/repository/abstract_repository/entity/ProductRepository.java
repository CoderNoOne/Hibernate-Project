package repository.abstract_repository.entity;

import domain.Category;
import domain.Producer;
import domain.Product;
import repository.abstract_repository.base.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductRepository extends CrudRepository <Product, Long> {
  Optional<Product> findByNameAndCategoryAndProducer(String name, Category category, Producer producer);

  Map<Category, Product> findProductsWithMostPriceInEveryCategory();

  List<Product> findProductsOrderedByClientsFromCountryAndWithAgeWithinRange(String countryName, Integer minAge, Integer maxAge);
}
