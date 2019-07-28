package service.entity;

import domain.Stock;
import dto.ProducerDto;
import dto.ProductDto;
import dto.ShopDto;
import dto.StockDto;
import exception.AppException;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import mapper.ModelMapper;
import repository.abstract_repository.entity.*;
import repository.impl.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static util.entity_utils.StockUtil.getStockDtoIfValid;

@RequiredArgsConstructor
public class StockService {

  private final StockRepository stockRepository;

  private final CountryRepository countryRepository;
  private final ProductRepository productRepository;
  private final ShopRepository shopRepository;
  private final CategoryRepository categoryRepository;
  private final ProducerRepository producerRepository;
  private final TradeRepository tradeRepository;


  public StockService() {
    this.stockRepository = new StockRepositoryImpl();
    this.shopRepository = new ShopRepositoryImpl();
    this.productRepository = new ProductRepositoryImpl();
    this.countryRepository = new CountryRepositoryImpl();
    this.categoryRepository = new CategoryRepositoryImpl();
    this.producerRepository = new ProducerRepositoryImpl();
    this.tradeRepository = new TradeRepositoryImpl();
  }

  private Optional<StockDto> addStockToDb(StockDto stockDto) {
    return stockRepository.addOrUpdate(ModelMapper.mapStockDtoToStock(stockDto))
            .map(ModelMapper::mapStockToStockDto);
  }

  public void addStockToDbFromUserInput(StockDto stockDto) {

    if (stockDto == null) {
      throw new AppException("StockDto object is null");
    }

    if (!isStockUniqueByShopAndProduct(stockDto.getShopDto(), stockDto.getProductDto())) {
      var stockFromDb = getStockByShopAndProduct(stockDto.getShopDto(), stockDto.getProductDto()).orElseThrow(() -> new AppException("Stock doesn't exist in db"));
      stockFromDb.setQuantity(getStockQuantity(stockFromDb) + stockDto.getQuantity());
      stockDto = stockFromDb;
    }

    addStockToDb(setStockDtoComponentsFromDbIfTheyExist(stockDto));
  }

  private StockDto setStockDtoComponentsFromDbIfTheyExist(StockDto stockDto) {

    return StockDto.builder()
            .id(stockDto.getId())
            .quantity(stockDto.getQuantity())
            .shopDto(setShopComponentFromDb(shopRepository.findShopByNameAndCountry(
                    stockDto.getShopDto().getName(),
                    stockDto.getShopDto().getCountryDto().getName())
                    .map(ModelMapper::mapShopToShopDto)
                    .orElse(stockDto.getShopDto())))
            .productDto(setProductComponentFromDb(productRepository.findByNameAndCategoryAndProducer(
                    stockDto.getProductDto().getName(),
                    ModelMapper.mapCategoryDtoToCategory(stockDto.getProductDto().getCategoryDto()),
                    ModelMapper.mapProducerDtoToProducer(stockDto.getProductDto().getProducerDto()))
                    .map(ModelMapper::mapProductToProductDto)
                    .orElse(stockDto.getProductDto())))
            .build();
  }

  private ProductDto setProductComponentFromDb(ProductDto productDto) {

    return ProductDto.builder()
            .id(productDto.getId())
            .name(productDto.getName())
            .guaranteeComponents(productDto.getGuaranteeComponents())
            .price(productDto.getPrice())
            .producerDto(setProducerComponent(producerRepository.findByNameAndTradeAndCountry(productDto.getProducerDto().getName(),
                    ModelMapper.mapTradeDtoToTrade(productDto.getProducerDto().getTrade()),
                    ModelMapper.mapCountryDtoToCountry(productDto.getProducerDto().getCountry()))
                    .map(ModelMapper::mapProducerToProducerDto)
                    .orElse(productDto.getProducerDto())))
            .categoryDto(categoryRepository.findCategoryByName(productDto.getCategoryDto().getName())
                    .map(ModelMapper::mapCategoryToCategoryDto).orElse(productDto.getCategoryDto()))
            .build();
  }

  private ProducerDto setProducerComponent(ProducerDto producerDto) {
    return ProducerDto.builder()
            .id(producerDto.getId())
            .name(producerDto.getName())
            .country(countryRepository.findCountryByName(producerDto.getCountry().getName())
                    .map(ModelMapper::mapCountryToCountryDto)
                    .orElse(producerDto.getCountry()))
            .trade(tradeRepository.findTradeByName(producerDto.getTrade().getName())
                    .map(ModelMapper::mapTradeToTradeDto)
                    .orElse(producerDto.getTrade()))
            .build();
  }

  private ShopDto setShopComponentFromDb(ShopDto shopDto) {
    return ShopDto.builder()
            .id(shopDto.getId())
            .name(shopDto.getName())
            .countryDto(countryRepository.findCountryByName(shopDto.getCountryDto().getName())
                    .map(ModelMapper::mapCountryToCountryDto).orElse(shopDto.getCountryDto()))
            .build();
  }

  private boolean isStockUniqueByShopAndProduct(ShopDto shopDto, ProductDto productDto) {
    return stockRepository.findStockByShopAndProduct(ModelMapper.mapShopDtoToShop(shopDto), ModelMapper.mapProductDtoToProduct(productDto)).isEmpty();
  }

  public void decreaseStockQuantity(ShopDto shopDto, ProductDto productDto, Integer quantity) {

    getStockByShopAndProduct(shopDto, productDto).ifPresent(stockDto -> {
      stockDto.setQuantity(getStockQuantity(stockDto) - quantity);
      addStockToDb(stockDto);
    });

  }

  private Integer getStockQuantity(StockDto stockDto) {
    return stockRepository.findStockByShopAndProduct(ModelMapper.mapShopDtoToShop(stockDto.getShopDto()), ModelMapper.mapProductDtoToProduct(stockDto.getProductDto()))
            .orElseThrow(() -> new AppException("No such stock exists")).getQuantity();
  }

  private Optional<StockDto> getStockByShopAndProduct(ShopDto shopDto, ProductDto productDto) {
    return stockRepository
            .findStockByShopAndProduct(ModelMapper.mapShopDtoToShop(shopDto), ModelMapper.mapProductDtoToProduct(productDto))
            .map(ModelMapper::mapStockToStockDto);
  }

  public Map<ShopDto, Integer> getShopListWithProductInStock(ProductDto productDto) {
    return stockRepository.findShopsWithProductInStock(ModelMapper.mapProductDtoToProduct(productDto))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                    e -> ModelMapper.mapShopToShopDto(e.getKey()),
                    Map.Entry::getValue
            ));
  }

  public List<ProducerDto> getProducersWithTradeAndNumberOfProductsProducedGreaterThan(String tradeName, Integer minAmountOfProducts) {

    if (tradeName == null) {
      throw new AppException("Trade name is null");
    }

    if (minAmountOfProducts == null) {
      throw new AppException("Minimum number of products is null");
    }

    return stockRepository.findStocksWithProducerTradeName(tradeName)
            .stream().collect(Collectors.collectingAndThen(
                    Collectors.groupingBy(stock -> stock.getProduct().getProducer(), Collectors.summingInt(Stock::getQuantity)),
                    map -> map.entrySet().stream().filter(e -> e.getValue() > minAmountOfProducts).map(Map.Entry::getKey)
                            .map(ModelMapper::mapProducerToProducerDto)
                            .collect(Collectors.toList())));
  }

  public Optional<StockDto> updateStock(StockDto stockDtoToUpdate) {

    Long id = stockDtoToUpdate.getId();

    if (id == null) {
      throw new AppException("Stock id is null");
    }

    Stock stockFromDb = stockRepository.findById(id)
            .orElseThrow(() -> new AppException("Stock with id: " + id + " doesn't exist in DB yet"));

    StockDto stockToUpdate = StockDto.builder()
            .id(id)
            .quantity(stockDtoToUpdate.getQuantity() != null ? stockDtoToUpdate.getQuantity() : stockFromDb.getQuantity())
            .productDto(ModelMapper.mapProductToProductDto(stockFromDb.getProduct()))
            .shopDto(ModelMapper.mapShopToShopDto(stockFromDb.getShop()))
            .build();

    return stockRepository
            .addOrUpdate(ModelMapper.mapStockDtoToStock(getStockDtoIfValid(setStockDtoComponentsFromDbIfTheyExist(stockToUpdate))))
            .map(ModelMapper::mapStockToStockDto);

  }

  private Optional<StockDto> getStockById(long stockId) {
    return stockRepository.findById(stockId)
            .map(ModelMapper::mapStockToStockDto);
  }

  public List<StockDto> getAllStocks() {

    return stockRepository.findAll()
            .stream()
            .map(ModelMapper::mapStockToStockDto)
            .collect(Collectors.toList());
  }

  public void deleteAllStocks() {
    stockRepository.deleteAll();
  }

  public Map<ProductDto, Set<ShopDto>> getProductDtoAndShopWithQuantityInStockMoreThan(List<ProductDto> productDtos, Integer quantity) {

    return getAllStocks().stream()
            .filter(stockDto -> productDtos.contains(stockDto.getProductDto()))
            .collect(Collectors.collectingAndThen(Collectors.groupingBy(StockDto::getProductDto
                    , Collectors.collectingAndThen(Collectors.groupingBy(StockDto::getShopDto, Collectors.summingInt(StockDto::getQuantity)),
                            innerMap -> innerMap.entrySet()
                                    .stream()
                                    .filter(innerEntry -> innerEntry.getValue() >= quantity)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toSet()))),
                    map -> map.entrySet()
                            .stream()
                            .filter(e -> !e.getValue().isEmpty())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue))));
  }
}
