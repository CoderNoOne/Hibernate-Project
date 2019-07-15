package service.entity;

import dto.ProducerDto;
import mapper.ModelMapper;
import dto.CategoryDto;
import dto.ProductDto;
import exception.AppException;
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

  private final ProductRepository productRepository;
  private final CategoryService categoryService;
  private final ProducerService producerService;

  public ProductService() {
    this.productRepository = new ProductRepositoryImpl();
    this.categoryService = new CategoryService();
    this.producerService = new ProducerService();
  }

  public ProductService(ProductRepository productRepository, CategoryService categoryService, ProducerService producerService) {
    this.productRepository = productRepository;
    this.categoryService = categoryService;
    this.producerService = producerService;
  }


  private Optional<ProductDto> addProductToDb(ProductDto productDto) {
    return productRepository
            .addOrUpdate(ModelMapper.mapProductDtoToProduct(productDto))
            .map(ModelMapper::mapProductToProductDto);
  }


  ProductDto setProductComponentsFromDbIfTheyExist(ProductDto productDto) {

   return ProductDto.builder()
            .id(productDto.getId())
            .name(productDto.getName())
            .price(productDto.getPrice())
            .categoryDto(categoryService.getCategoryFromDbIfExists(productDto.getCategoryDto()))
            .guaranteeComponents(productDto.getGuaranteeComponents())
            .producerDto(producerService.getProducerFromDbIfExists(producerService.setProducerComponentsFromDbIfTheyExist(productDto.getProducerDto())))
            .build();

  }

  ProductDto getProductFromDbIfExists(ProductDto productDto) {

    return getProductByNameAndCategoryAndProducer(productDto.getName(), productDto.getCategoryDto(),
            productDto.getProducerDto())
            .orElse(productDto);
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

  private Optional<ProductDto> getProductByNameAndCategoryAndProducer(String name, CategoryDto categoryDto, ProducerDto producerDto) {
    return Optional.ofNullable(
            ModelMapper.mapProductToProductDto(
                    productRepository.findByNameAndCategoryAndProducer(name, ModelMapper.mapCategoryDtoToCategory(categoryDto), ModelMapper.mapProducerDtoToProducer(producerDto))
                            .orElseThrow(() -> new AppException("No product was found for name: " + name + " category " +
                                    categoryDto + " and producer: " + producerDto))));
  }

  List<ProductDto> getProductsByNameAndCategory(String name, CategoryDto categoryDto) {
    return productRepository.findProductsByNameAndCategory(name, ModelMapper.mapCategoryDtoToCategory(categoryDto))
            .stream()
            .map(ModelMapper::mapProductToProductDto)
            .collect(Collectors.toList());
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
    return productRepository.findById(id)
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
