package repository.abstract_repository.entity;

import domain.Category;
import domain.Producer;
import domain.Product;
import domain.enums.EGuarantee;
import repository.abstract_repository.base.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductRepository extends CrudRepository <Product, Long> {
  Optional<Product> findByNameAndCategoryAndProducer(String name, Category category, Producer producer);


  List<Product> findProductsByNameAndCategory(String name, Category category);

  Map<Category, List<Product>> findTheMostExpensiveProductInEveryCategory();
  List<Product> findProductsOrderedByClientsFromCountryAndWithAgeWithinRange(String countryName, Integer minAge, Integer maxAge);

  void deleteAllGuaranteeComponents();

}
