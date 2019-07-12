package service.entity;

import domain.Product;
import domain.Shop;
import dto.*;
import exception.AppException;
import mappers.ProducerMapper;
import mappers.ProductMapper;
import mappers.ShopMapper;
import mappers.StockMapper;
import repository.abstract_repository.entity.StockRepository;
import repository.impl.StockRepositoryImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static util.entity_utils.ProductUtil.*;
import static util.entity_utils.ShopUtil.*;
import static util.entity_utils.StockUtil.getStockDtoIfValid;
import static util.others.UserDataUtils.getInt;
import static util.others.UserDataUtils.printCollectionWithNumeration;
import static util.update.UpdateStockUtil.getUpdatedStock;

public class StockService {

  private final StockRepository stockRepository;
  private final ShopService shopService;
  private final ProductService productService;
  private final CountryService countryService;
  private final ProducerMapper producerMapper;
  private final StockMapper stockMapper;
  private final ShopMapper shopMapper;
  private final ProductMapper productMapper;

  public StockService() {
    this.stockRepository = new StockRepositoryImpl();
    this.producerMapper = new ProducerMapper();
    this.shopService = new ShopService();
    this.productService = new ProductService();
    this.countryService = new CountryService();
    this.stockMapper = new StockMapper();
    this.shopMapper = new ShopMapper();
    this.productMapper = new ProductMapper();
  }

  private Optional<StockDto> addStockToDb(StockDto stockDto) {
    return stockRepository.addOrUpdate(stockMapper.mapStockDtoToStock(stockDto))
            .map(stockMapper::mapStockToStockDto);
  }

  public void addStockToDbFromUserInput(StockDto stockDto) {
    if (!isStockUniqueByShopAndProduct(stockDto.getShopDto(), stockDto.getProductDto())) {
      var stockFromDb = getStockByShopAndProduct(stockDto.getShopDto(), stockDto.getProductDto()).orElseThrow(() -> new AppException("Stock doesn't exist in db"));
      stockFromDb.setQuantity(getStockQuantity(stockFromDb) + stockDto.getQuantity());
      stockDto = stockFromDb;
    }
    addStockToDb(stockDto);
  }

  private StockDto setStockDtoComponentsFromDbIfTheyExist(StockDto stockDto) {
    return StockDto.builder()
            .id(stockDto.getId())
            .quantity(stockDto.getQuantity())
            .shopDto(shopService.getShopFromDbIfExists(stockDto.getShopDto()))
            .productDto(productService.getProductFromDbIfExists(stockDto.getProductDto()))
            .build();
  }


  public StockDto specifyShop(StockDto stockDto) {

    var shopsByName = shopService.getShopsByName(stockDto.getShopDto().getName());

    var shopDto = !shopsByName.isEmpty() ?
            chooseAvailableShop(shopsByName) : setShopComponentsFromDbIfTheyExist(getShopDtoIfValid(preciseShopDtoDetails(stockDto)));

    stockDto.setShopDto(shopDto);
    return stockDto;

  }

  private ShopDto setShopComponentsFromDbIfTheyExist(ShopDto shopDto) {

    return ShopDto.builder()
            .name(shopDto.getName())
            .countryDto(countryService.getCountryFromDbIfExists(shopDto.getCountryDto()))
            .build();
  }

  private boolean isStockUniqueByShopAndProduct(ShopDto shopDto, ProductDto productDto) {
    return stockRepository.findStockByShopAndProduct(shopMapper.mapShopDtoToShop(shopDto), productMapper.mapProductDtoToProduct(productDto)).isEmpty();
  }

  public void decreaseStockQuantityIfValid(Map<ShopDto, Integer> map, CustomerOrderDto customerOrder) {

    getStockByShopAndProduct(map.keySet().iterator().next(), customerOrder.getProduct())
            .ifPresentOrElse(stockDto -> {
              if (stockDto.getQuantity() >= customerOrder.getQuantity()) {
                decreaseStockQuantityBySpecifiedAmount(stockDto, customerOrder.getQuantity());
              } else {
                throw new AppException(String.format("Not enough products in stock. Customer wants %d but there are only %d products in the stock",
                        customerOrder.getQuantity(), stockDto.getQuantity()));
              }
            }, () -> {
              throw new AppException(String.format("No stock was found for product: %s and for shop: %s",
                      customerOrder.getProduct().getName(), map.keySet().iterator().next().getName()));
            });
  }

  private Integer getStockQuantity(StockDto stockDto) {
    return stockRepository.findStockByShopAndProduct(shopMapper.mapShopDtoToShop(stockDto.getShopDto()), productMapper.mapProductDtoToProduct(stockDto.getProductDto()))
            .orElseThrow(() -> new AppException("No such stock exists")).getQuantity();
  }

  private Optional<StockDto> getStockByShopAndProduct(ShopDto shopDto, ProductDto productDto) {
    return stockRepository
            .findStockByShopAndProduct(shopMapper.mapShopDtoToShop(shopDto), productMapper.mapProductDtoToProduct(productDto))
            .map(stockMapper::mapStockToStockDto);
  }

  Map<ShopDto, Integer> getShopListWithProductInStock(ProductDto productDto) {
    return stockRepository.findShopsWithProductInStock(productMapper.mapProductDtoToProduct(productDto))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                    e -> shopMapper.mapShopToShopDto(e.getKey()),
                    Map.Entry::getValue
            ));
  }

  private void decreaseStockQuantityBySpecifiedAmount(StockDto stockDto, Integer quantity) {
    stockDto.setQuantity(getStockQuantity(stockDto) - quantity);
    addStockToDb(stockDto);
  }

  public List<ProducerDto> getProducersWithTradeAndNumberOfProductsProducedGreaterThan(String tradeName, Integer minAmountOfProducts) {
    return stockRepository
            .findProducersWithTradeAndNumberOfProducedProductsGreaterThan(tradeName, minAmountOfProducts).stream()
            .map(producerMapper::mapProducerToProducerDto).collect(Collectors.toList());
  }

  public void updateStock() {
    printCollectionWithNumeration(getAllStocks());

    long stockId = getInt("Choose stock id you want to update");

    getStockById(stockId)
            .ifPresentOrElse(stockDto ->
                            stockRepository
                                    .addOrUpdate(stockMapper
                                            .mapStockDtoToStock(setStockDtoComponentsFromDbIfTheyExist(getStockDtoIfValid(getUpdatedStock(stockDto))))),
                    () -> {
                      throw new AppException("There is no stock with that id: " + stockId + " in DB");
                    });

  }

  public StockDto specifyProduct(StockDto stockDto) {

    var productList = productService.getProductsByNameAndCategory(stockDto.getProductDto().getName(),
            stockDto.getProductDto().getCategoryDto());

    var productDto = !productList.isEmpty() ? chooseAvailableProduct(productList) : productService.setProductComponentsFromDbIfTheyExist(
            getProductIfValid(preciseProductDtoDetails(stockDto)));

    stockDto.setProductDto(productDto);

    return stockDto;
  }


  private Optional<StockDto> getStockById(long stockId) {
    return stockRepository.findById(stockId)
            .map(stockMapper::mapStockToStockDto);
  }

  private List<StockDto> getAllStocks() {

    return stockRepository.findAll()
            .stream()
            .map(stockMapper::mapStockToStockDto)
            .collect(Collectors.toList());
  }

  public void deleteAllStocks() {
    stockRepository.deleteAll();
  }
}
