package service.entity;

import com.sun.xml.bind.v2.TODO;
import domain.Product;
import dto.ProducerDto;
import mapper.ModelMapper;
import dto.CategoryDto;
import dto.ProductDto;
import exception.AppException;
import repository.abstract_repository.entity.*;
import repository.impl.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.Collectors;


import static util.entity_utils.ProductUtil.getProductDtoIfValid;

public class ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ProducerRepository producerRepository;
  private final TradeRepository tradeRepository;
  private final CountryRepository countryRepository;

  public ProductService() {
    this.productRepository = new ProductRepositoryImpl();
    this.categoryRepository = new CategoryRepositoryImpl();
    this.producerRepository = new ProducerRepositoryImpl();
    this.tradeRepository = new TradeRepositoryImpl();
    this.countryRepository = new CountryRepositoryImpl();
  }

  public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ProducerRepository producerRepository, TradeRepository tradeRepository, CountryRepository countryRepository) {
    this.productRepository = productRepository;
    this.producerRepository = producerRepository;
    this.categoryRepository = categoryRepository;
    this.tradeRepository = tradeRepository;
    this.countryRepository = countryRepository;
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
            .producerDto(setProducerComponents(
                    producerRepository.findByNameAndTradeAndCountry(
                            productDto.getProducerDto().getName(),
                            ModelMapper.mapTradeDtoToTrade(productDto.getProducerDto().getTrade()),
                            ModelMapper.mapCountryDtoToCountry(productDto.getProducerDto().getCountry()))
                            .map(ModelMapper::mapProducerToProducerDto).orElse(productDto.getProducerDto())))
            .build();

  }

  private ProducerDto setProducerComponents(ProducerDto producerDto) {

    return ProducerDto.builder()
            .id(producerDto.getId())
            .name(producerDto.getName())
            .trade(tradeRepository.findTradeByName(producerDto.getTrade().getName())
                    .map(ModelMapper::mapTradeToTradeDto)
                    .orElse(producerDto.getTrade()))
            .country(countryRepository.findCountryByName(producerDto.getCountry().getName())
                    .map(ModelMapper::mapCountryToCountryDto)
                    .orElse(producerDto.getCountry()))
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

  public List<ProductDto> getAllProducts() {
    return productRepository.findAll()
            .stream()
            .map(ModelMapper::mapProductToProductDto)
            .collect(Collectors.toList());
  }

  private Optional<ProductDto> getProductDtoById(Long id) {
    return productRepository.findById(id)
            .map(ModelMapper::mapProductToProductDto);

  }

  public Optional<ProductDto> updateProduct(ProductDto productDtoToUpdate) {
    if (productDtoToUpdate.getId() == null) {
      throw new AppException("Customer id is null");
    }

    Product productFromDb = productRepository.findById(productDtoToUpdate.getId())
            .orElseThrow(() -> new AppException("Product with id: " + productDtoToUpdate.getId() + " doesn't exist in DB yet"));

    ProductDto productToUpdate = ProductDto.builder()
            .id(productDtoToUpdate.getId())
            .name(productDtoToUpdate.getName() != null ? productDtoToUpdate.getName() : productFromDb.getName())
            .price(productDtoToUpdate.getPrice() != null ? productDtoToUpdate.getPrice() : productFromDb.getPrice())
            .producerDto(ModelMapper.mapProducerToProducerDto(productFromDb.getProducer()))
            .categoryDto(ModelMapper.mapCategoryToCategoryDto(productFromDb.getCategory()))
            .guaranteeComponents(productDtoToUpdate.getGuaranteeComponents() != null ? productDtoToUpdate.getGuaranteeComponents() : productFromDb.getGuaranteeComponents())
            .build();

    return productRepository
            .addOrUpdate(ModelMapper.mapProductDtoToProduct(getProductDtoIfValid(setProductComponentsFromDbIfTheyExist((productToUpdate)))))
            .map(ModelMapper::mapProductToProductDto);
  }


  public void deleteAllGuaranteeComponents() {
    productRepository.deleteAllGuaranteeComponents();
  }
}
