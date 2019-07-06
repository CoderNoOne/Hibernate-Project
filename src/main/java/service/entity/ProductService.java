package service.entity;

import domain.Category;
import domain.Producer;
import domain.Product;
import dto.CategoryDto;
import dto.ProductDto;
import exception.AppException;
import mapper.CategoryMapper;
import mapper.ProductMapper;
import org.mapstruct.factory.Mappers;
import repository.abstract_repository.entity.ProductRepository;
import repository.impl.ProductRepositoryImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static util.entity_utils.CustomerUtil.getCustomerIfValid;
import static util.entity_utils.ProductUtil.getProductIfValid;
import static util.others.UserDataUtils.getInt;
import static util.others.UserDataUtils.printCollectionWithNumeration;
import static util.update.UpdateCustomerUtil.getUpdatedCustomer;
import static util.update.UpdateProductUtil.getUpdatedProduct;

public class ProductService {

  private final ProductMapper productMapper;
  private final CategoryMapper categoryMapper;
  private final ProductRepository productRepository;
  private final CategoryService categoryService;
  private final ProducerService producerService;

  public ProductService() {
    this.productRepository = new ProductRepositoryImpl();
    this.productMapper = Mappers.getMapper(ProductMapper.class);
    this.categoryMapper = Mappers.getMapper(CategoryMapper.class);
    this.categoryService = new CategoryService();
    this.producerService = new ProducerService();
  }

  private Optional<Product> addProductToDb(Product product) {
    return productRepository.addOrUpdate(product);
  }


  private Product setProductComponentsFromDbIfTheyExist(Product product) {

    return Product.builder()
            .id(product.getId())
            .name(product.getName())
            .price(product.getPrice())
            .category(categoryService.getCategoryFromDbIfExists(product.getCategory()))
            .guaranteeComponents(product.getGuaranteeComponents())
            .producer(producerService.getProducerFromDbIfExists(producerService.setProducerComponentsFromDbIfTheyExist(product.getProducer())))
            .build();
  }

  public void addProductToDbFromUserInput(Product product) {
    if (!isProductUniqueByNameAndCategoryAndProducer(product.getName(), product.getCategory(), product.getProducer())) {
      throw new AppException("Couldn't add new product to db - product's not unique by name and category and producer");
    }
    addProductToDb(setProductComponentsFromDbIfTheyExist(product));

  }

  private boolean isProductUniqueByNameAndCategoryAndProducer(String name, Category category, Producer producer) {
    return productRepository.findByNameAndCategoryAndProducer(name, category, producer).isEmpty();
  }

  public Optional<Product> getProductByNameAndCategoryAndProducer(String name, Category category, Producer producer) {
    return productRepository.findByNameAndCategoryAndProducer(name, category, producer);
  }

  public List<Product> getProductsByNameAndCategory(String name, Category category) {
    return productRepository.findProductsByNameAndCategory(name, category);
  }

  public Map<CategoryDto, List<ProductDto>> getTheMostExpensiveProductInEachCategory() {

    return productRepository.findTheMostExpensiveProductInEveryCategory().entrySet().stream()
            .collect(Collectors.toMap(
                    e -> categoryMapper.categoryToCategoryDTO(e.getKey()),
                    e -> e.getValue().stream().map(productMapper::productToProductDTO).collect(Collectors.toList())));
  }

  public void deleteAllProducts() {
    productRepository.deleteAll();
  }

  public List<Product> getAllProducts(){
    return productRepository.findAll();
  }

  public Optional <Product> getProductById(Long id){
    return productRepository.findById(id);
  }
  public void updateProduct(){

    printCollectionWithNumeration(getAllProducts());
    long productId = getInt("Choose product id you want to update");

    getProductById(productId)
            .ifPresentOrElse(product ->
                            productRepository.addOrUpdate(setProductComponentsFromDbIfTheyExist(getProductIfValid(getUpdatedProduct(product)))),
                    () -> {
                      throw new AppException("There is no product with that id: " + productId + " in DB");
                    });
  }
}
