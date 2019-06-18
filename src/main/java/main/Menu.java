package main;

import configuration.DbConnection;
import domain.*;
import domain.Error;
import exception.AppException;
import lombok.extern.slf4j.Slf4j;
import service.*;
import utils.others.UserDataUtils;
import utils.entity_utils.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;

import static helper.enums.ErrorMessage.ERROR_DURING_INSERTION;
import static helper.enums.TableNames.*;
import static utils.others.UserDataUtils.printMessage;
import static utils.entity_utils.CustomerOrderUtil.*;
import static utils.entity_utils.CustomerOrderUtil.getCustomerOrderIfValid;
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
  private final CustomerOrderService customerOrderService = new CustomerOrderService();

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
          case 5 -> executeOption5();
          case 6 -> executeOption6();
          case 7 -> executeOption7();
          case 8 -> executeOption8();
          case 9 -> executeOption9();
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
            "Add new Customer order",
            "Show the most expensive product in each category"

    ));
  }


  private void executeOption9() {

  }

  private void executeOption8() {

  }

  private void executeOption7() {

    productService.getTheMostExpensiveProductInEachCategory().forEach((category, productList) -> {
      printMessage("Category: " + category + " productList: " + productList);

      System.out.println(customerOrderService.getNumberOfOrdersForSpecifiedProducts(productList));
    });
  }


  private void executeOption6() {

    try {

      var customerOrder = getCustomerOrderIfValid(specifyCustomerDetail(specifyOrderedProductDetail(createCustomerOrderFromUserInput())));

      customerOrder.setPayment(getPaymentFromDbIfExists(customerOrder.getPayment()));
      decreaseStockQuantityIfValid(specifyShopDetailForCustomerOrder(customerOrder), customerOrder);

      customerOrderService.addCustomerOrderToDbFromUserInput(customerOrder);
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", CUSTOMER_ORDER, ERROR_DURING_INSERTION, e.getMessage()));
    }
  }

  private CustomerOrder specifyCustomerDetail(CustomerOrder customerOrder) {

    var customerName = customerOrder.getCustomer().getName();
    var customerSurname = customerOrder.getCustomer().getSurname();
    var customerCountry = customerOrder.getCustomer().getCountry();

    customerService.getCustomerByNameAndSurnameAndCountry(customerName,
            customerSurname, customerCountry)
            .ifPresentOrElse(
                    customerOrder::setCustomer,
                    () -> {
                      throw new AppException(String.format("There is no customer in a db with: name: %s surname: %s country: %s",
                              customerName, customerSurname, customerCountry.getName()));
                    });

    return customerOrder;
  }

  private void decreaseStockQuantityIfValid(Map<Shop, Integer> map, CustomerOrder customerOrder) {

    stockService.getStockByShopAndProduct(map.keySet().iterator().next(), customerOrder.getProduct())
            .ifPresentOrElse(stock -> {
              if (stock.getQuantity() >= customerOrder.getQuantity()) {
                stockService.decreaseStockQuantityBySpecifiedAmount(stock, customerOrder.getQuantity());
              } else {
                throw new AppException(String.format("Not enough products in stock. Customer wants %d but there are only %d products in the stock",
                        customerOrder.getQuantity(), stock.getQuantity()));
              }
            }, () -> {
              throw new AppException(String.format("No stock was found for product: %s and for shop: %s",
                      customerOrder.getProduct().getName(), map.keySet().iterator().next().getName()));
            });
  }


  private Map<Shop, Integer> specifyShopDetailForCustomerOrder(CustomerOrder customerOrder) {

    Map<Shop, Integer> shopMap = stockService.getShopListWithProductInStock(customerOrder.getProduct());

    if (!shopMap.isEmpty()) {
      var shop = chooseAvailableShop(new ArrayList<>(shopMap.keySet()));

      return Collections.singletonMap(shop, shopMap.get(shop));
    }

    throw new AppException("Product of interest isn't for sale in any of the registered shops in a DB");
  }


  private CustomerOrder specifyOrderedProductDetail(CustomerOrder customerOrderFromUserInput) {

    var productsByNameAndCategory = productService.getProductsByNameAndCategory(customerOrderFromUserInput.getProduct().getName(),
            customerOrderFromUserInput.getProduct().getCategory());

    if (!productsByNameAndCategory.isEmpty()) {
      customerOrderFromUserInput.setProduct(chooseAvailableProduct(productsByNameAndCategory));
      return customerOrderFromUserInput;
    }

    throw new AppException(String.format("There wasn't any product in a DB for product name: %s and product category: %s",
            customerOrderFromUserInput.getProduct().getName(), customerOrderFromUserInput.getProduct().getCategory().getName()));
  }

  private void executeOption5() {

    try {

      var stock = getStockIfValid(specifyShop(specifyProduct(createStockDetailFromUserInput())));

      stockService.addStockToDbFromUserInput(stock);

    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", STOCK, ERROR_DURING_INSERTION, e.getMessage()));
    }

  }

  private Stock specifyProduct(Stock stock) {

    var productList = productService.getProductsByNameAndCategory(stock.getProduct().getName(),
            stock.getProduct().getCategory());

    var product = !productList.isEmpty() ? chooseAvailableProduct(productList) : setProductComponentsFromDbIfTheyExist(
            getProductIfValid(preciseProductDetails(stock)));

    stock.setProduct(product);

    return stock;
  }

  private Product setProductComponentsFromDbIfTheyExist(Product product) {

   return Product.builder()
            .name(product.getName())
            .category(getCategoryFromDbIfExists(product.getCategory()))
           .guaranteeComponent(product.getGuaranteeComponent())
            .producer(getProducerFromDbIfExists(Producer.builder()
                    .name(product.getProducer().getName())
                    .country(getCountryFromDbIfExists(product.getProducer().getCountry()))
                    .trade(getTradeFromDbIfExists(product.getProducer().getTrade()))
                    .build()))
            .build();

  }

  private Stock specifyShop(Stock stock) {

    var shopsByName = shopService.getShopsByName(stock.getShop().getName());

    var shop = !shopsByName.isEmpty() ?
            chooseAvailableShop(shopsByName) : setShopComponentsFromDbIfTheyExist(getShopIfValid(preciseShopDetails(stock)));

    stock.setShop(shop);
    return stock;

  }

  private Shop setShopComponentsFromDbIfTheyExist(Shop shop) {

    return Shop.builder()
            .name(shop.getName())
            .country(getCountryFromDbIfExists(shop.getCountry()))
            .build();
  }

  private void executeOption4() {

    try {
      var product = ProductUtil.createProductFromUserInput();

      product.setCategory(getCategoryFromDbIfExists(product.getCategory()));
      product.setProducer(getProducerFromDbIfExists(product.getProducer()));

      product.getProducer().setCountry(getCountryFromDbIfExists(product.getProducer().getCountry()));
      product.getProducer().setTrade(getTradeFromDbIfExists(product.getProducer().getTrade()));

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

      producer.setCountry(getCountryFromDbIfExists(producer.getCountry()));
      producer.setTrade(getTradeFromDbIfExists(producer.getTrade()));

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
      shop.setCountry(getCountryFromDbIfExists(shop.getCountry()));
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
      customer.setCountry(getCountryFromDbIfExists(customer.getCountry()));
      customerService.addCustomerToDbFromUserInput(customer);

    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", CUSTOMER, ERROR_DURING_INSERTION, e.getMessage()));
    }
  }


  private Country getCountryFromDbIfExists(Country country) {
    return countryService.getCountryByName(country.getName()).orElse(country);
  }

  private Category getCategoryFromDbIfExists(Category category) {

    return categoryService.getCategoryByName(category.getName()).orElse(category);
  }

  private Payment getPaymentFromDbIfExists(Payment payment) {
    return paymentService.getPaymentByEpayment(payment.getEpayment()).orElse(payment);
  }

  private Customer getCustomerFromDbIfExists(Customer customer) {
    return customerService.getCustomerByNameAndSurnameAndCountry(customer.getName(), customer.getSurname(), customer.getCountry()).orElse(customer);
  }

  private Shop getShopFromDbIfExists(Shop shop) {
    return shopService.getShopByNameAndCountry(shop.getName(), shop.getCountry().getName()).orElse(shop);
  }

  private Trade getTradeFromDbIfExists(Trade trade) {
    return tradeService.getTradeByName(trade.getName()).orElse(trade);
  }

  private Producer getProducerFromDbIfExists(Producer producer) {
    return producerService.getProducerByNameAndTradeAndCountry(
            producer.getName(), producer.getTrade(),
            producer.getCountry()).orElse(producer);
  }

  private Product getProductFromDbIfExists(Product product) {
    return productService
            .getProductByNameAndCategoryAndProducer(product.getName(), product.getCategory(),
                    product.getProducer()).orElse(product);
  }
}
