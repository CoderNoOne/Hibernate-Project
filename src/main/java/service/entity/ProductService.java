package service.entity;

import dto.ProducerDto;
import mapper.ModelMapper;
import dto.CategoryDto;
import dto.ProductDto;
import exception.AppException;
import repository.abstract_repository.entity.CategoryRepository;
import repository.abstract_repository.entity.ProducerRepository;
import repository.abstract_repository.entity.ProductRepository;
import repository.impl.CategoryRepositoryImpl;
import repository.impl.ProducerRepositoryImpl;
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

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ProducerRepository producerRepository;

  public ProductService() {
    this.productRepository = new ProductRepositoryImpl();
    this.categoryRepository = new CategoryRepositoryImpl();
    this.producerRepository = new ProducerRepositoryImpl();
  }

  public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ProducerRepository producerRepository) {
    this.productRepository = productRepository;
    this.producerRepository = producerRepository;
    this.categoryRepository = categoryRepository;
  }


  private Optional<ProductDto> addProductToDb(ProductDto productDto) {
    return productRepository
            .addOrUpdate(ModelMapper.mapProductDtoToProduct(productDto))
            .map(ModelMapper::mapProductToProductDto);
  }


  private ProductDto setProductComponentsFromDbIfTheyExist(ProductDto productDto) {

    return ProductDto.builder()
            .id(productDto.getId())
            .name(productDto.getName())
            .price(productDto.getPrice())
            .categoryDto(categoryRepository.findCategoryByName(productDto.getCategoryDto().getName())
                    .map(ModelMapper::mapCategoryToCategoryDto)
                    .orElse(productDto.getCategoryDto()))
            .guaranteeComponents(productDto.getGuaranteeComponents())
            .producerDto(producerRepository.findByNameAndTradeAndCountry(
                    productDto.getProducerDto().getName(),
                    ModelMapper.mapTradeDtoToTrade(productDto.getProducerDto().getTrade()),
                    ModelMapper.mapCountryDtoToCountry(productDto.getProducerDto().getCountry()))
                    .map(ModelMapper::mapProducerToProducerDto).orElse(productDto.getProducerDto()))
            .build();

  }

  public void addProductToDbFromUserInput(ProductDto productDto) {
    if (!isProductUniqueByNameAndCategoryAndProducer(productDto.getName(), productDto.getCategoryDto(), productDto.getProducerDto())) {
      throw new AppException(String.format("Couldn't add new product to db - product: %s is not unique by name and category and producer", productDto));
    }
    addProductToDb(setProductComponentsFromDbIfTheyExist(productDto));

  }

  private boolean isProductUniqueByNameAndCategoryAndProducer(String name, CategoryDto categoryDto, ProducerDto producerDto) {
    return productRepository.findByNameAndCategoryAndProducer(name, ModelMapper.mapCategoryDtoToCategory(categoryDto),
            ModelMapper.mapProducerDtoToProducer(producerDto)).isEmpty();
  }

  public Map<CategoryDto, List<ProductDto>> getTheMostExpensiveProductInEachCategory() {

    return productRepository.findTheMostExpensiveProductInEveryCategory().entrySet().stream()
            .collect(Collectors.toMap(
                    e -> ModelMapper.mapCategoryToCategoryDto(e.getKey()),
                    e -> e.getValue().stream().map(ModelMapper::mapProductToProductDto).collect(Collectors.toList())));
  }

  public void deleteAllProducts() {
    productRepository.deleteAll();
  }

  private List<ProductDto> getAllProducts() {
    return productRepository.findAll()
            .stream()
            .map(ModelMapper::mapProductToProductDto)
            .collect(Collectors.toList());
  }

  private Optional<ProductDto> getProductDtoById(Long id) {
    return productRepository.findShopById(id)
            .map(ModelMapper::mapProductToProductDto);

  }

  public void updateProduct() {

    printCollectionWithNumeration(getAllProducts());
    long productId = getInt("Choose product id you want to update");

    getProductDtoById(productId)
            .ifPresentOrElse(productDto ->
                            productRepository
                                    .addOrUpdate(ModelMapper.mapProductDtoToProduct(setProductComponentsFromDbIfTheyExist(getProductIfValid(getUpdatedProduct(productDto)))))
                                    .map(ModelMapper::mapProductToProductDto),
                    () -> {
                      throw new AppException("There is no product with that id: " + productId + " in DB");
                    });
  }

  public void deleteAllGuaranteeComponents() {
    productRepository.deleteAllGuaranteeComponents();
  }
}
