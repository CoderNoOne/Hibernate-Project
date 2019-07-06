package service.other;

import converters.impl.*;
import domain.Error;
import exception.AppException;
import lombok.extern.log4j.Log4j;
import service.entity.*;
import validator.impl.*;

import java.time.LocalDateTime;
import java.util.Arrays;

import static util.others.UserDataUtils.printCollectionWithNumeration;

@Log4j
public class DataInitializeService {

  private final CustomerService customerService = new CustomerService();
  private final ErrorService errorService = new ErrorService();
  private final CountryService countryService = new CountryService();
  private final CategoryService categoryService = new CategoryService();
  private final TradeService tradeService = new TradeService();
  private final ShopService shopService = new ShopService();
  private final ProducerService producerService = new ProducerService();
  private final ProductService productService = new ProductService();

  public void deleteAllContent(){
    productService.deleteAllProducts();
    producerService.deleteAllProducers();
    shopService.deleteAllShops();
    customerService.deleteAllCustomers();
    categoryService.deleteAllCategories();
    countryService.deleteAllCountries();
    tradeService.deleteAllTrades();
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
            }).forEach(category -> {
      try {
        categoryService.addCategoryToDb(category);
      } catch (Exception e) {
        log.info(e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        errorService.addErrorToDb(Error.builder()
                .date(LocalDateTime.now()).message(e.getMessage()).build());
      }
    });

  }

  private void initShops(final String shopsJsonFilename) {

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
            }).forEach(shop -> {
      try {
        shopService.addShopToDbFromUserInput(shop);
      } catch (Exception e) {
        log.info(e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        errorService.addErrorToDb(Error.builder()
                .date(LocalDateTime.now()).message(e.getMessage()).build());
      }
    });

  }

  private void initCountries(final String countriesJsonFilename) {

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
            }).forEach(country -> {
      try {
        countryService.addCountryToDb(country);
      } catch (Exception e) {
        log.info(e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        errorService.addErrorToDb(Error.builder()
                .date(LocalDateTime.now()).message(e.getMessage()).build());
      }
    });

  }

  private void initTrades(final String tradesJsonFilename) {

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
            }).forEach(trade -> {
      try {
        tradeService.addTradeToDbFromUserInput(trade);
      } catch (Exception e) {
        log.info(e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        errorService.addErrorToDb(Error.builder()
                .date(LocalDateTime.now()).message(e.getMessage()).build());
      }
    });

  }

  private void initProducers(final String producersJsonFilename) {

    var producerValidator = new ProducerValidator();

    new ProducerListJsonConverter(producersJsonFilename)
            .fromJson()
            .orElseThrow(() -> new AppException("FILE " + producersJsonFilename + " is empty"))
            .stream()
            .filter(producer-> {
              producerValidator.validate(producer);
              if (producerValidator.hasErrors()) {
                printCollectionWithNumeration(producerValidator.getErrors().entrySet());
              }
              return !producerValidator.hasErrors();
            }).forEach(producer -> {
      try {
        producerService.addProducerToDbFromUserInput(producer);
      } catch (Exception e) {
        log.info(e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        errorService.addErrorToDb(Error.builder()
                .date(LocalDateTime.now()).message(e.getMessage()).build());
      }
    });


  }

  private void initCustomers(final String customersJsonFilename) {

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
            .forEach(customer -> {
              try {
                customerService.addCustomerToDbFromUserInput(customer);
              } catch (Exception e) {
                log.info(e.getMessage());
                log.error(Arrays.toString(e.getStackTrace()));
                errorService.addErrorToDb(Error.builder()
                        .date(LocalDateTime.now()).message(e.getMessage()).build());
              }
            });
  }

  private void initProducts(final String productsJsonFilename) {

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
            .forEach(product -> {
              try {
                productService.addProductToDbFromUserInput(product);
              } catch (Exception e) {
                log.info(e.getMessage());
                log.error(Arrays.toString(e.getStackTrace()));
                errorService.addErrorToDb(Error.builder()
                        .date(LocalDateTime.now()).message(e.getMessage()).build());
              }
            });
  }
}
