package main;

import configuration.DbConnection;
import domain.Error;
import exception.AppException;
import helper.OptionalHelper;
import lombok.extern.slf4j.Slf4j;
import service.*;
import utils.UserDataUtils;
import utils.entity_utils.ProducerUtil;
import utils.entity_utils.ProductUtil;
import utils.entity_utils.ShopUtil;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Arrays;

import static helper.enums.ErrorMessage.ERROR_DURING_INSERTION;
import static helper.enums.TableNames.*;
import static utils.UserDataUtils.printMessage;
import static utils.entity_utils.CustomerUtil.createCustomerFromUserInput;

@Slf4j
class Menu {

  private final CustomerService customerService = new CustomerService();
  private final CountryService countryService = new CountryService();
  private final ErrorService errorService = new ErrorService();
  private final ShopService shopService = new ShopService();
  private final ProducerService producerService = new ProducerService();
  private final TradeService tradeService = new TradeService();
  private final CategoryService categoryService = new CategoryService();
  private final ProductService productService = new ProductService();

  void mainMenu() {
    while (true) {
      showMenuOptions();
      try {

        int option = UserDataUtils.getInt("Input your option");
        switch (option) {
          case 1 -> executeOption1();
          case 2 -> executeOption2();
          case 3 -> executeOption3();
          case 4 -> executeOption4();
          case 10 -> DbConnection.close();
        }
      } catch (AppException e) {
        log.info(e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        errorService.addErrorToDb(Error.builder()
                .date(LocalDateTime.now()).message(e.getMessage()).build());
      }
    }
  }

  private void showMenuOptions() {

    printMessage(MessageFormat.format(
            "\nOption no. 1 - {0}\n" +
                    "Option no. 2 - {1}\n" +
                    "Option no. 3 - {2}\n" +
                    "Option no. 4 - {3}\n" +
                    "Option no. 5 - {4}\n" +
                    "Option no. 6 - {5}\n" +
                    "Option no. 7 - {6}\n" +
                    "Option no. 8 - {7}\n" +
                    "Option no. 9 - {8}\n" +
                    "Option no. 10 - {9}\n" +
                    "Option no. 11 - {10}",

            "Add new Customer",
            "Add new shop",
            "Add new producer",
            "Add new product",
            "Add new stock",
            "Add new Customer order"

    ));
  }

  private void executeOption6() {

    try {

    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", CUSTOMER_ORDER, ERROR_DURING_INSERTION, e.getMessage()));
    }
  }



  private void executeOption5() {

    try {



    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", STOCK, ERROR_DURING_INSERTION, e.getMessage()));
    }
  }

  private void executeOption4() {

    try {
      var product = ProductUtil.createProductFromUserInput();

      var category = OptionalHelper.of(categoryService
              .getCategoryByName(product.getCategory().getName()))
              .ifNotPresent(() ->
                      categoryService.addCategoryToDb(product.getCategory()).orElseThrow(() -> new AppException("Category is null")));

      var country = OptionalHelper.of(countryService
              .getCountryByName(product.getProducer().getCountry().getName()))
              .ifNotPresent(() ->
                      countryService.addCountryToDb(product.getProducer().getCountry()).orElseThrow(() -> new AppException("Country is null")));

      product.getProducer().setCountry(country);

      var trade = OptionalHelper.of(tradeService
              .getTradeByName(product.getProducer().getTrade().getName()))
              .ifNotPresent(() ->
                      tradeService.addTradeToDb(product.getProducer().getTrade()).orElseThrow(() -> new AppException("Trade is null")));

      product.getProducer().setTrade(trade);

      var producer = OptionalHelper.of(producerService
              .getProducerByNameAndTradeAndCountry(
                      product.getProducer().getName(), product.getProducer().getTrade(), product.getProducer().getCountry()))
              .ifNotPresent(() ->
                      producerService.addProducerToDb(product.getProducer()).orElseThrow(() -> new AppException("Producer is null")));

      product.setCategory(category);
      product.setProducer(producer);
      productService.addProductToDbFromUserInput(product);

    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", PRODUCT, ERROR_DURING_INSERTION, e.getMessage()));
    }
  }

  private void executeOption3() {

    try {
      var producer = ProducerUtil.createProducerFromUserInput();

      var country = OptionalHelper.of(countryService
              .getCountryByName(producer.getCountry().getName()))
              .ifNotPresent(() ->
                      countryService.addCountryToDb(producer.getCountry()).orElseThrow(() -> new AppException("Country is null")));

      var trade = OptionalHelper.of(tradeService
              .getTradeByName(producer.getTrade().getName()))
              .ifNotPresent(() ->
                      tradeService.addTradeToDb(producer.getTrade()).orElseThrow(() -> new AppException("Trade is null")));

      producer.setCountry(country);
      producer.setTrade(trade);
      producerService.addProducerToDb(producer);

    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", PRODUCER, ERROR_DURING_INSERTION, e.getMessage()));
    }
  }

  private void executeOption2() {

    try {
      var shop = ShopUtil.createShopFromUserInput();

      var country = OptionalHelper.of(countryService
              .getCountryByName(shop.getCountry().getName()))
              .ifNotPresent(() ->
                      countryService.addCountryToDb(shop.getCountry()).orElseThrow(() -> new AppException("Country is null")));

      shop.setCountry(country);

      shopService.addShopToDbFromUserInput(shop);
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", SHOP, ERROR_DURING_INSERTION, e.getMessage()));
    }

  }

  private void executeOption1() {

    try {
      var customer = createCustomerFromUserInput();

      //walidacja czy country istnieje juz w bazie danych
      var country = OptionalHelper.of(countryService
              .getCountryByName(customer.getCountry().getName()))
              .ifNotPresent(() ->
                      countryService.addCountryToDb(customer.getCountry()).orElseThrow(() -> new AppException("Country is null")));

      customer.setCountry(country);
      //walidacja uniqueness customera na podstawie imie + nazwisko + country + dodanie jesli unique
      customerService.addCustomerToDbFromUserInput(customer);
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", CUSTOMER, ERROR_DURING_INSERTION, e.getMessage()));
    }
  }
}
