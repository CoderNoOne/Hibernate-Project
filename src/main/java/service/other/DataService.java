package service.other;

import converters.impl.*;
import dto.ErrorDto;
import exception.AppException;
import lombok.extern.log4j.Log4j;
import service.entity.*;
import validator.impl.*;

import java.time.LocalDateTime;
import java.util.Arrays;

import static util.others.UserDataUtils.printCollectionWithNumeration;

@Log4j
public class DataService {

  private final CustomerService customerService = new CustomerService();
  private final CountryService countryService = new CountryService();
  private final CategoryService categoryService = new CategoryService();
  private final TradeService tradeService = new TradeService();
  private final ShopService shopService = new ShopService();
  private final ProducerService producerService = new ProducerService();
  private final ProductService productService = new ProductService();
  private final StockService stockService = new StockService();
  private final CustomerOrderService customerOrderService = new CustomerOrderService();
  private final ErrorService errorService = new ErrorService();

  public void deleteAllContent() {

    try {

      customerOrderService.deleteAllCustomerOrders();
      stockService.deleteAllStocks();
      productService.deleteAllGuaranteeComponents();
      productService.deleteAllProducts();
      producerService.deleteAllProducers();
      shopService.deleteAllShops();
      customerService.deleteAllCustomers();
      categoryService.deleteAllCategories();
      countryService.deleteAllCountries();
      tradeService.deleteAllTrades();

    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("DATA SERVICE ERROR (DELETING ALL CONTENT): %s", e.getMessage()));
    }
  }

  public void init() {

    initCountries("./example_data/exampleCountries.json");
    initCustomers("./example_data/exampleCustomers.json");
    initCategories("./example_data/exampleCategories.json");
    initTrades("./example_data/exampleTrades.json");
    initShops("./example_data/exampleShops.json");
    initProducers("./example_data/exampleProducers.json");
    initProducts("./example_data/exampleProducts.json");

  }

  private void initCategories(final String categoriesJsonFilename) {

    try {
      var categoryValidator = new CategoryDtoValidator();
      new CategoryDtoListJsonConverter(categoriesJsonFilename)
              .fromJson()
              .orElseThrow(() -> new AppException("FILE " + categoriesJsonFilename + " is empty"))
              .stream()
              .filter(category -> {
                categoryValidator.validate(category);
                if (categoryValidator.hasErrors()) {
                  printCollectionWithNumeration(categoryValidator.getErrors().entrySet());
                }
                return !categoryValidator.hasErrors();
              }).forEach(categoryService::addCategoryToDb);
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      errorService.addErrorToDb(ErrorDto.builder()
              .date(LocalDateTime.now()).message(e.getMessage()).build());
    }
  }

  private void initShops(final String shopsJsonFilename) {

    try {
      var shopValidator = new ShopDtoValidator();
      new ShopDtoListJsonConverter(shopsJsonFilename)
              .fromJson()
              .orElseThrow(() -> new AppException("FILE " + shopsJsonFilename + " is empty"))
              .stream()
              .filter(shop -> {
                shopValidator.validate(shop);
                if (shopValidator.hasErrors()) {
                  printCollectionWithNumeration(shopValidator.getErrors().entrySet());
                }
                return !shopValidator.hasErrors();
              }).forEach(shopService::addShopToDbFromUserInput);
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      errorService.addErrorToDb(ErrorDto.builder()
              .date(LocalDateTime.now()).message(e.getMessage()).build());
    }
  }

  private void initCountries(final String countriesJsonFilename) {

    try {
      var countryValidator = new CountryDtoValidator();

      new CountryDtoListJsonConverter(countriesJsonFilename)
              .fromJson()
              .orElseThrow(() -> new AppException("FILE " + countriesJsonFilename + " is empty"))
              .stream()
              .filter(country -> {
                countryValidator.validate(country);
                if (countryValidator.hasErrors()) {
                  printCollectionWithNumeration(countryValidator.getErrors().entrySet());
                }
                return !countryValidator.hasErrors();
              }).forEach(countryService::addCountryToDb);
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      errorService.addErrorToDb(ErrorDto.builder()
              .date(LocalDateTime.now()).message(e.getMessage()).build());
    }
  }

  private void initTrades(final String tradesJsonFilename) {

    try {
      var tradeValidator = new TradeDtoValidator();

      new TradeDtoListJsonConverter(tradesJsonFilename)
              .fromJson()
              .orElseThrow(() -> new AppException("FILE " + tradesJsonFilename + " is empty"))
              .stream()
              .filter(trade -> {
                tradeValidator.validate(trade);
                if (tradeValidator.hasErrors()) {
                  printCollectionWithNumeration(tradeValidator.getErrors().entrySet());
                }
                return !tradeValidator.hasErrors();
              }).forEach(tradeService::addTradeToDbFromUserInput);
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      errorService.addErrorToDb(ErrorDto.builder()
              .date(LocalDateTime.now()).message(e.getMessage()).build());
    }

  }

  private void initProducers(final String producersJsonFilename) {

    try {
      var producerValidator = new ProducerDtoValidator();

      new ProducerDtoListJsonConverter(producersJsonFilename)
              .fromJson()
              .orElseThrow(() -> new AppException("FILE " + producersJsonFilename + " is empty"))
              .stream()
              .filter(producer -> {
                producerValidator.validate(producer);
                if (producerValidator.hasErrors()) {
                  printCollectionWithNumeration(producerValidator.getErrors().entrySet());
                }
                return !producerValidator.hasErrors();
              }).forEach(productDto -> {
        try {
          producerService.addProducerToDbFromUserInput(productDto);
        } catch (Exception e) {
          log.info(e.getMessage());
          log.error(Arrays.toString(e.getStackTrace()));
          errorService.addErrorToDb(ErrorDto.builder()
                  .date(LocalDateTime.now()).message(e.getMessage()).build());
        }
      });
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      errorService.addErrorToDb(ErrorDto.builder()
              .date(LocalDateTime.now()).message(e.getMessage()).build());
    }
  }

  private void initCustomers(final String customersJsonFilename) {

    try {
      var customerValidator = new CustomerDtoValidator();

      new CustomerDtoListJsonConverter(customersJsonFilename)
              .fromJson()
              .orElseThrow(() -> new AppException("FILE " + customersJsonFilename + " is empty"))
              .stream()
              .filter(customer -> {
                customerValidator.validate(customer);
                if (customerValidator.hasErrors()) {
                  printCollectionWithNumeration(customerValidator.getErrors().entrySet());
                }
                return !customerValidator.hasErrors();
              })
              .forEach(customerDto -> {
                try {
                  customerService.addCustomerToDbFromUserInput(customerDto);
                } catch (Exception e) {
                  log.info(e.getMessage());
                  log.error(Arrays.toString(e.getStackTrace()));
                  errorService.addErrorToDb(ErrorDto.builder()
                          .date(LocalDateTime.now()).message(e.getMessage()).build());
                }
              });
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      errorService.addErrorToDb(ErrorDto.builder()
              .date(LocalDateTime.now()).message(e.getMessage()).build());
    }
  }

  private void initProducts(final String productsJsonFilename) {

    try {
      var productValidator = new ProductDtoValidator();

      new ProductDtoListJsonConverter(productsJsonFilename)
              .fromJson()
              .orElseThrow(() -> new AppException("FILE " + productsJsonFilename + " is empty"))
              .stream()
              .filter(product -> {
                productValidator.validate(product);
                if (productValidator.hasErrors()) {
                  printCollectionWithNumeration(productValidator.getErrors().entrySet());
                }
                return !productValidator.hasErrors();
              })
              .forEach(productService::addProductToDbFromUserInput);
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      errorService.addErrorToDb(ErrorDto.builder()
              .date(LocalDateTime.now()).message(e.getMessage()).build());
    }
  }
}
