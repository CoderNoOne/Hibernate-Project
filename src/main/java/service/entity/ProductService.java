package service.entity;

import mappers.CategoryMapper;
import mappers.ProductMapper;
import domain.Category;
import domain.Producer;
import domain.Product;
import dto.CategoryDto;
import dto.ProductDto;
import exception.AppException;
import org.mapstruct.factory.Mappers;
import repository.abstract_repository.entity.ProductRepository;
import repository.impl.ProductRepositoryImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


import static util.entity_utils.ProductUtil.getProductIfValid;
import static util.others.UserDataUtils.getInt;
import static util.others.UserDataUtils.printCollectionWithNumeration;
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


  public ProductDto setProductComponentsFromDbIfTheyExist(ProductDto productDto) {

    return ProductDto.builder()
            .id(productDto.getId())
            .name(productDto.getName())
            .price(productDto.getPrice())
            .categoryDto(categoryService.getCategoryFromDbIfExists(productDto.getCategoryDto()))
            .guaranteeComponents(productDto.getGuaranteeComponents())
            .producer(producerService.getProducerFromDbIfExists(producerService.setProducerComponentsFromDbIfTheyExist(productDto.getProducer())))
            .build();
  }

  public ProductDto getProductFromDbIfExists(Product product) {
    ProductDto other = productMapper.mapProductToProductDto(product);

    return productMapper.mapProductToProductDto(getProductByNameAndCategoryAndProducer(product.getName(), product.getCategory(),
            product.getProducer()).orElse(product)));
  }

  public void addProductToDbFromUserInput(Product product) {
    if (!isProductUniqueByNameAndCategoryAndProducer(product.getName(), product.getCategory(), product.getProducer())) {
      throw new AppException(String.format("Couldn't add new product to db - product: %s is not unique by name and category and producer", product));
    }
    addProductToDb(setProductComponentsFromDbIfTheyExist(product));

  }

  private boolean isProductUniqueByNameAndCategoryAndProducer(String name, Category category, Producer producer) {
    return productRepository.findByNameAndCategoryAndProducer(name, category, producer).isEmpty();
  }

  public Optional<ProductDto> getProductByNameAndCategoryAndProducer(String name, Category category, Producer producer) {
    return Optional.ofNullable(
            productMapper.mapProductToProductDto(
                    productRepository.findByNameAndCategoryAndProducer(name, category, producer)
                            .orElseThrow(() -> new AppException("No product was found for name: " + name + " category " +
                                    category + " and producer: " + producer))));
  }

  public List<ProductDto> getProductsByNameAndCategory(String name, Category category) {
    return productRepository.findProductsByNameAndCategory(name, category)
            .stream()
            .map(productMapper::mapProductToProductDto)
            .collect(Collectors.toList());
  }

  public Map<CategoryDto, List<ProductDto>> getTheMostExpensiveProductInEachCategory() {

    return productRepository.findTheMostExpensiveProductInEveryCategory().entrySet().stream()
            .collect(Collectors.toMap(
                    e -> categoryMapper.mapCategoryToCategoryDto(e.getKey()),
                    e -> e.getValue().stream().map(productMapper::mapProductToProductDto).collect(Collectors.toList())));
  }

  public void deleteAllProducts() {
    productRepository.deleteAll();
  }

  public List<ProductDto> getAllProducts() {
    return productRepository.findAll()
            .stream()
            .map(productMapper::mapProductToProductDto)
            .collect(Collectors.toList());
  }

  public Optional<ProductDto> getProductDtoById(Long id) {
    return Optional.ofNullable(productMapper.mapProductToProductDto(
            productRepository.findById(id).orElseThrow(() -> new AppException("There's not a product with id: " + id))));
  }

  public void updateProduct() {

    printCollectionWithNumeration(getAllProducts());
    long productId = getInt("Choose product id you want to update");

    getProductDtoById(productId)
            .ifPresentOrElse(productDto ->
                            productRepository.addOrUpdate(setProductComponentsFromDbIfTheyExist(getProductIfValid(getUpdatedProduct(productDto)))),
                    () -> {
                      throw new AppException("There is no product with that id: " + productId + " in DB");
                    });
  }

  public void deleteAllGuaranteeComponents() {
    productRepository.deleteAllGuaranteeComponents();
  }
}
