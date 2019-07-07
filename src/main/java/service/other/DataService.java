package service.other;

import converters.impl.*;
import exception.AppException;
import lombok.extern.log4j.Log4j;
import service.entity.*;
import validator.impl.*;

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

  public void deleteAllContent() {

    try {

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

    initCountries("exampleCountries.json");
    initCustomers("exampleCustomers.json");
    initCategories("exampleCategories.json");
    initTrades("exampleTrades.json");
    initShops("exampleShops.json");
    initProducers("exampleProducers.json");
    initProducts("exampleProducts.json");

  }

  private void initCategories(final String categoriesJsonFilename) {

    try {
      var categoryValidator = new CategoryValidator();
      new CategoryListJsonConverter(categoriesJsonFilename)
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
      throw new AppException(String.format("DATA SERVICE ERROR (CATEGORIES INITIALIZING): %s", e.getMessage()));
    }
  }

  private void initShops(final String shopsJsonFilename) {

    try {
      var shopValidator = new ShopValidator();
      new ShopListJsonConverter(shopsJsonFilename)
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
      throw new AppException(String.format("DATA SERVICE ERROR (SHOPS INITIALIZING): %s", e.getMessage()));
    }
  }

  private void initCountries(final String countriesJsonFilename) {

    try {
      var countryValidator = new CountryValidator();

      new CountryListJsonConverter(countriesJsonFilename)
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
      throw new AppException(String.format("DATA SERVICE ERROR (COUNTRIES INITIALIZING): %s", e.getMessage()));
    }
  }

  private void initTrades(final String tradesJsonFilename) {

    try {
      var tradeValidator = new TradeValidator();

      new TradeListJsonConverter(tradesJsonFilename)
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
      throw new AppException(String.format("DATA SERVICE ERROR (TRADES INITIALIZING): %s", e.getMessage()));
    }

  }

  private void initProducers(final String producersJsonFilename) {

    try {
      var producerValidator = new ProducerValidator();

      new ProducerListJsonConverter(producersJsonFilename)
              .fromJson()
              .orElseThrow(() -> new AppException("FILE " + producersJsonFilename + " is empty"))
              .stream()
              .filter(producer -> {
                producerValidator.validate(producer);
                if (producerValidator.hasErrors()) {
                  printCollectionWithNumeration(producerValidator.getErrors().entrySet());
                }
                return !producerValidator.hasErrors();
              }).forEach(producerService::addProducerToDbFromUserInput);
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("DATA SERVICE ERROR (PRODUCERS INITIALIZING): %s", e.getMessage()));
    }
  }

  private void initCustomers(final String customersJsonFilename) {

    try {
      var customerValidator = new CustomerValidator();

      new CustomerListJsonConverter(customersJsonFilename)
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
              .forEach(customerService::addCustomerToDbFromUserInput);
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("DATA SERVICE ERROR (CUSTOMERS INITIALIZING): %s", e.getMessage()));
    }
  }

  private void initProducts(final String productsJsonFilename) {

    try {
      var productValidator = new ProductValidator();

      new ProductListJsonConverter(productsJsonFilename)
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
      throw new AppException(String.format("DATA SERVICE ERROR (PRODUCTS INITIALIZING): %s", e.getMessage()));
    }
  }
}
