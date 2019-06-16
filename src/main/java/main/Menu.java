package main;

import configuration.DbConnection;
import domain.*;
import domain.Error;
import exception.AppException;
import helper.OptionalHelper;
import lombok.extern.slf4j.Slf4j;
import service.*;
import utils.UserDataUtils;
import utils.entity_utils.*;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Arrays;

import static helper.enums.ErrorMessage.ERROR_DURING_INSERTION;
import static helper.enums.TableNames.*;
import static utils.UserDataUtils.printMessage;
import static utils.entity_utils.CustomerUtil.createCustomerFromUserInput;
import static utils.entity_utils.ProductUtil.*;
import static utils.entity_utils.ShopUtil.*;
import static utils.entity_utils.StockUtil.createStockDetailFromUserInput;
import static utils.entity_utils.StockUtil.getStockIfValid;

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
  private final StockService stockService = new StockService();
  private final PaymentService paymentService = new PaymentService();

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
          case 5 -> executeOption5b();
          case 6 -> executeOption6();
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

      /*var customerOrder = CustomerOrderUtil.createCustomerOrderFromUserInput();

      //1
      //customerCountry
      var customerCountry = countryService.getCountryByName(customerOrder.getCustomer().getCountry().getName())
              .orElse(customerOrder.getCustomer().getCountry());
      customerOrder.getCustomer().setCountry(customerCountry);
      //customer
      var customer = customerService.getCustomerByNameAndSurnameAndCountry(
              customerOrder.getCustomer().getName(), customerOrder.getCustomer().getSurname(), customerOrder.getCustomer().getCountry())
              .orElse(customerOrder.getCustomer());

      //2
      //category
      var category = categoryService.getCategoryByName(customerOrder.getProduct().getCategory().getName()).orElse(
              customerOrder.getProduct().getCategory());

      customerOrder.getProduct().setCategory(category);

      //product
      var productsList = productService
              .getProductsByNameAndCategory(customerOrder.getProduct().getName(), customerOrder.getProduct().getCategory());

      if(!productsList.isEmpty()){
        //wtedy wyjatek ?
      }else {
        // zdecyduj ktory wyvrac z powyzszych + sprawdz czy quantity <= w stocku quantity
      }
      printCollectionWithNumeration(productsList);
      //3
      var payment = paymentService.getPaymentByEpayment(customerOrder.getPayment().getEpayment())
              .orElse(customerOrder.getPayment());

      customerOrder.getProduct().setProducer(producer);
      customerOrder.setPayment(payment);

      customerOrderService.
      //payment*/


    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", CUSTOMER_ORDER, ERROR_DURING_INSERTION, e.getMessage()));
    }
  }


  /*1*/
  private Country getCountryFromDbIfExists(Country country) {
    return countryService.getCountryByName(country.getName()).orElse(country);
  }

  /*2*/
  private Category getCategoryFromDbIfExists(Category category) {

    return categoryService.getCategoryByName(category.getName()).orElse(category);
  }


  /*3*/
  private Shop getShopFromDbIfExists(Shop shop) {
    return shopService.getShopByNameAndCountry(shop.getName(), shop.getCountry().getName()).orElse(shop);
  }

  /*4*/

  private Trade getTradeFromDbIfExists(Trade trade) {
    return tradeService.getTradeByName(trade.getName()).orElse(trade);
  }

  /*5*/
  private Producer getProducerFromDbIfExists(Producer producer) {
    return producerService.getProducerByNameAndTradeAndCountry(
            producer.getName(), producer.getTrade(),
            producer.getCountry()).orElse(producer);
  }

  /*6*/
  private Product getProductFromDbIfExists(Product product) {
    return productService
            .getProductByNameAndCategoryAndProducer(product.getName(), product.getCategory(),
                    product.getProducer()).orElse(product);
  }


  private void executeOption5b() {


    try {

      var stock = getStockIfValid(specifyShop(specifyProduct(createStockDetailFromUserInput())));

      stock.getProduct().setCategory(getCategoryFromDbIfExists(stock.getProduct().getCategory()));
      stock.getProduct().setProducer(getProducerFromDbIfExists(stock.getProduct().getProducer()));

      stock.getProduct().getProducer().setTrade(getTradeFromDbIfExists(stock.getProduct().getProducer().getTrade()));
      stock.getProduct().getProducer().setCountry(getCountryFromDbIfExists(stock.getProduct().getProducer().getCountry()));

      stock.getShop().setCountry(getCountryFromDbIfExists(stock.getShop().getCountry()));

      stock.setProduct(getProductFromDbIfExists(stock.getProduct()));
      stock.setShop(getShopFromDbIfExists(stock.getShop()));

      stockService.addStockToDbFromUserInput(stock);

    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", STOCK, ERROR_DURING_INSERTION, e.getMessage()));
    }

  }

  private Stock specifyProduct(Stock stockDetailFromUserInput) {

    var productList = productService.getProductsByNameAndCategory(stockDetailFromUserInput.getProduct().getName(),
            stockDetailFromUserInput.getProduct().getCategory());

    var product = !productList.isEmpty() ? chooseAvailableProduct(productList) : getProductIfValid(preciseProductDetails(stockDetailFromUserInput));

    stockDetailFromUserInput.setProduct(product);
    return stockDetailFromUserInput;
  }


  private Stock specifyShop(Stock stockFromUserInput) {

    var shopsByName = shopService.getShopsByName(stockFromUserInput.getShop().getName());

    var shop = !shopsByName.isEmpty() ?
            chooseAvailableShop(shopsByName) : getShopIfValid(preciseShopDetails(stockFromUserInput));

    stockFromUserInput.setShop(shop);
    return stockFromUserInput;

  }

  private void executeOption5() {

    try {

      /*var stock = createStockDetailFromUserInput();

      var shopCountry = countryService.getCountryByName(stock.getShop().getCountry().getName()).orElse(stock.getShop().getCountry());

      stock.getShop().setCountry(shopCountry);

      var category = categoryService.getCategoryByName(stock.getProduct().getCategory().getName()).orElse(stock.getProduct().getCategory());

      var shop = shopService
              .getShopByNameAndCountry(stock.getShop().getName(), stock.getShop().getCountry().getName()).orElse(stock.getShop());

      stock.getProduct().setCategory(category);

      var trade = tradeService
              .getTradeByName(stock.getProduct().getProducer().getTrade().getName()).orElse(stock.getProduct().getProducer().getTrade());

      stock.getProduct().getProducer().setTrade(trade);

      var prodcuerCountry = countryService
              .getCountryByName(stock.getProduct().getProducer().getCountry().getName()).orElse(stock.getProduct().getProducer().getCountry());

      stock.getProduct().getProducer().setCountry(prodcuerCountry);

      var producer = producerService
              .getProducerByNameAndTradeAndCountry(
                      stock.getProduct().getProducer().getName(), stock.getProduct().getProducer().getTrade(),
                      stock.getProduct().getProducer().getCountry()).orElse(stock.getProduct().getProducer());

      stock.getProduct().setProducer(producer);

      var product = productService
              .getProductByNameAndCategoryAndProducer(stock.getProduct().getName(), stock.getProduct().getCategory(),
                      stock.getProduct().getProducer()).orElse(stock.getProduct());

      stock.setProduct(product);
      stock.setShop(shop);
      stockService.addStockToDbFromUserInput(stock);*/

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
      var country = countryService.getCountryByName(producer.getCountry().getName()).orElse(producer.getCountry());
      var trade = tradeService.getTradeByName(producer.getTrade().getName()).orElse(producer.getTrade());

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

      var shop = createShopFromUserInput();
      var country = countryService.getCountryByName(shop.getCountry().getName()).orElse(shop.getCountry());
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
      var country = countryService.getCountryByName(customer.getCountry().getName()).orElse(customer.getCountry());
      customer.setCountry(country);
      customerService.addCustomerToDbFromUserInput(customer);

    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", CUSTOMER, ERROR_DURING_INSERTION, e.getMessage()));
    }
  }
}
