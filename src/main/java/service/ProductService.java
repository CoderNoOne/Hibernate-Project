package service;

import domain.Category;
import domain.Producer;
import domain.Product;
import exception.AppException;
import repository.abstract_repository.entity.ProductRepository;
import repository.impl.ProductRepositoryImpl;

import java.util.Optional;

public class ProductService {

  private final ProductRepository productRepository;

  public ProductService() {
    this.productRepository = new ProductRepositoryImpl();
  }

  public Optional<Product> addProductToDb(Product product) {
    return productRepository.addOrUpdate(product);
  }

  public void addProductToDbFromUserInput(Product product) {
    if (!) {
      addProductToDb(product);
    } else {
      throw new AppException("Couldn't add new product to db - product's not unique by name and category and producent");
    }
  }

  private boolean isProductUniqueByNameAndCategoryAndProducer(String name, Category category, Producer producer){
    return productRepository.findByNameAndCategoryAndProducer(name, category, producer);
  }

}
