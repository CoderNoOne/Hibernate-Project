package main;

import configuration.DbConnection;
import domain.enums.EGuarantee;
import dto.*;
import exception.AppException;
import lombok.extern.slf4j.Slf4j;
import service.entity.*;
import util.others.UserDataUtils;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static helper.enums.ErrorMessage.*;
import static helper.enums.TableNames.*;
import static repository.impl.CustomerOrderRepositoryImpl.GUARANTEE_PERIOD_IN_YEARS;
import static util.entity_utils.CategoryUtil.*;
import static util.entity_utils.CountryUtil.*;
import static util.entity_utils.CustomerUtil.*;
import static util.entity_utils.ProducerUtil.createProducerDtoFromUserInput;
import static util.entity_utils.ProducerUtil.getProducerDtoIfValid;
import static util.entity_utils.CustomerOrderUtil.*;
import static util.entity_utils.CustomerOrderUtil.getCustomerOrderIfValid;
import static util.entity_utils.ProductUtil.*;
import static util.entity_utils.ShopUtil.*;
import static util.entity_utils.StockUtil.createStockDtoDetailFromUserInput;
import static util.entity_utils.StockUtil.getStockDtoIfValid;
import static util.entity_utils.TradeUtil.*;
import static util.others.UserDataUtils.*;

@Slf4j
class Menu {

  private final CustomerService customerService = new CustomerService();
  private final ErrorService errorService = new ErrorService();
  private final ShopService shopService = new ShopService();
  private final ProducerService producerService = new ProducerService();
  private final ProductService productService = new ProductService();
  private final StockService stockService = new StockService();
  private final CustomerOrderService customerOrderService = new CustomerOrderService();
  private final CountryService countryService = new CountryService();
  private final CategoryService categoryService = new CategoryService();
  private final TradeService tradeService = new TradeService();

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
          case 10 -> executeOption10();
          case 11 -> executeOption11();
          case 12 -> executeOption12();
          case 13 -> executeOption13();
          case 14 -> executeOption14();
          case 15 -> executeOption15();
          case 16 -> executeOption16();
          case 17 -> executeOption17();
          case 18 -> executeOption18();
          case 19 -> executeOption19();
          case 20 -> executeOption20();
          case 21 -> executeOption21();
          case 22 -> executeOption22();
          case 23 -> executeOption23();
          case 24 -> executeOption24();
          case 25 -> executeOption25();
          case 26 -> executeOption26();
          case 27 -> executeOption27();
          case 28 -> {
            DbConnection.close();
            return;
          }
        }
      } catch (AppException e) {
        log.info(e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        errorService.addErrorToDb(ErrorDto.builder()
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
                    "Option no. 11 - {10}\n" +
                    "Option no. 12 - {11}\n" +
                    "Option no. 13 - {12}\n" +
                    "Option no. 14 - {13}\n" +
                    "Option no. 15 - {14}\n" +
                    "Option no. 16 - {15}\n" +
                    "Option no. 17 - {16}\n" +
                    "Option no. 18 - {17}\n" +
                    "Option no. 19 - {18}\n" +
                    "Option no. 20 - {19}\n" +
                    "Option no. 21 - {20}\n" +
                    "Option no. 22 - {21}\n" +
                    "Option no. 23 - {22}\n" +
                    "Option no. 24 - {23}\n" +
                    "Option no. 25 - {24}\n" +
                    "Option no. 26 - {25}\n" +
                    "Option no. 27 - {26}\n",


            "Go to admin panel",
            "Add new customer",
            "Add new shop",
            "Add new producer",
            "Add new product",
            "Add new stock",
            "Add new customer order",
            "Show the most expensive product in each category",
            "Show distinct products ordered by customer with age within specified range and from specified country - sorted by price in descending order",
            "Show orders with order date within and with price after discount higher than specified ",
            "Show products with active warranty (" + GUARANTEE_PERIOD_IN_YEARS + " years) from order date and with specified list of guarantee components grouped by product category",
            "Show producers with specified trade name and with at least a specified number of products produced",
            "Show products ordered by customer grouped by producer",
            "Show customers who ordered products produced in their national country",
            "Delete customer",
            "Update customer",
            "Update shop",
            "Update product",
            "Update producer",
            "Update stock",
            "Add new country",
            "Add new category",
            "Delete category",
            "Delete country",
            "Add trade",
            "Delete trade",
            "Delete shop",
            "Exit the program"
    ));
  }

  private void executeOption20() {
    try {
      stockService.updateStock();
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", STOCK, ERROR_DURING_UPDATE, e.getMessage()));
    }
  }

  private void executeOption19() {

    try {
      producerService.updateProducer();
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", PRODUCER, ERROR_DURING_UPDATE, e.getMessage()));
    }
  }


  private void executeOption18() {
    try {
      productService.updateProduct();
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", PRODUCT, ERROR_DURING_UPDATE, e.getMessage()));
    }
  }


  private void executeOption17() {
    try {
      shopService.updateShop();
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", SHOP, ERROR_DURING_UPDATE, e.getMessage()));
    }
  }

  private void executeOption16() {
    try {
      customerService.updateCustomer();
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", CUSTOMER, ERROR_DURING_UPDATE, e.getMessage()));
    }
  }

  private void executeOption15() {

    try {
      CustomerDto customerDto = specifyCustomerDtoDetailToDelete();
      customerService.deleteCustomer(customerDto);
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", CUSTOMER, ERROR_DURING_DELETION, e.getMessage()));
    }
  }

  private void executeOption14() {

    customerOrderService
            .getCustomersWhoBoughtAtLeastOneProductProducedInHisNationalCountryAndThenFindNumberOfProductsProducedInDifferentCountryAndBoughtByHim()
            .forEach((customer, quantity) -> printMessage(String.format("Customer: %s Number of products: %d", customer, quantity)));
  }

  private void executeOption13() {

    var customerName = getString("Input customer name");
    var customerSurname = getString("Input customer surname");
    var countryName = getString("Input country name");

    customerOrderService.getProductsOrderedByCustomerGroupedByProducer(customerName, customerSurname, countryName).forEach(
            (producer, productsList) -> {
              printMessage(String.format("\nProducer: %s |  Products list:\n", producer.getName()));
              printCollectionWithNumeration(productsList);
            }
    );

  }

  private void executeOption12() {

    var tradeName = getString("Input trade name");
    var minAmountOfProducts = getInt("Input min number of products produced");

    printCollectionWithNumeration(stockService.getProducersWithTradeAndNumberOfProductsProducedGreaterThan(tradeName, minAmountOfProducts));
  }

  private void executeOption11() {

    Set<EGuarantee> guaranteeSet = new HashSet<>();
    List<EGuarantee> values = new ArrayList<>(List.of(EGuarantee.values()));

    while (getString("Do you want to add searching by components?(Y/N)").equalsIgnoreCase("Y")) {
      int warrantyElementId;
      do {
        printCollectionWithNumeration(values);
        warrantyElementId = getInt("Choose guarantee by number");
      } while (!values.isEmpty() && !(warrantyElementId >= 1 && warrantyElementId <= values.size()));

      guaranteeSet.add(values.get(warrantyElementId - 1));
      values.remove(warrantyElementId - 1);
    }

    customerOrderService.getProductsWithActiveWarrantyAndWithSpecifiedGuaranteeComponentsGroupedByCategory(guaranteeSet)
            .forEach((category, productList) -> {
              printMessage(String.format("\nCategory: %s | Filtered products:\n", category));
              printCollectionWithNumeration(productList);
            });
  }


  private void executeOption10() {

    LocalDate minLocalDate;
    LocalDate maxLocalDate;
    do {
      minLocalDate = getLocalDate("Input min local date");
      maxLocalDate = getLocalDate("Input max local date");
    } while (minLocalDate.compareTo(maxLocalDate) > 0);

    var minPriceAfterDiscount = getBigDecimal("Input product price");

    System.out.println(customerOrderService.getOrdersWithinSpecifiedDateRangeAndWithPriceAfterDiscountHigherThanSpecified(minLocalDate, maxLocalDate, minPriceAfterDiscount));
  }

  private void executeOption9() {

    var countryName = getString("Input country name").toUpperCase();
    int minAge;
    int maxAge;
    do {
      minAge = getInt("Input customer min age");
      maxAge = getInt("Input customer max age");
    } while (minAge > maxAge);

    printCollectionWithNumeration(customerOrderService.getDistinctProductsOrderedByCustomerFromCountryAndWithAgeWithinSpecifiedRangeAndSortedByPriceDescOrder(countryName, minAge, maxAge));
  }

  private void executeOption8() {

    customerOrderService.getTheMostExpensiveProductsInEachCategoryWithAmountOfProductSales().forEach(
            (category, innerMap) -> {
              printMessage(String.format("Category: %sMax quantity: %d The products are: \n", category.getName(), innerMap.values().iterator().next()));
              printCollectionWithNumeration(innerMap.keySet());
            });
  }


  private void executeOption7() {

    try {
      var customerOrder = getCustomerOrderIfValid(customerOrderService.specifyOrderedProductDetail(customerOrderService.specifyCustomerDetail((createCustomerOrderDtoFromUserInput()))));
      stockService.decreaseStockQuantityIfValid(customerOrderService.specifyShopDetailForCustomerOrder(customerOrder), customerOrder);
      customerOrderService.addCustomerOrderToDbFromUserInput(customerOrder);
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", CUSTOMER_ORDER, ERROR_DURING_INSERTION, e.getMessage()));
    }
  }

  private void executeOption6() {

    try {

      stockService
              .addStockToDbFromUserInput(getStockDtoIfValid(stockService.specifyShop(stockService.specifyProduct(createStockDtoDetailFromUserInput()))));

    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", STOCK, ERROR_DURING_INSERTION, e.getMessage()));
    }

  }

  private void executeOption5() {

    try {
      var product = getProductIfValid(createProductDtoFromUserInput());
      productService.addProductToDbFromUserInput(product);

    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", PRODUCT, ERROR_DURING_INSERTION, e.getMessage()));
    }
  }

  private void executeOption4() {

    try {

      var producer = getProducerDtoIfValid(createProducerDtoFromUserInput());
      producerService.addProducerToDbFromUserInput(producer);

    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", PRODUCER, ERROR_DURING_INSERTION, e.getMessage()));
    }
  }


  private void executeOption3() {

    try {

      var shop = getShopDtoIfValid(createShopDtoFromUserInput());
      shopService.addShopToDbFromUserInput(shop);

    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", SHOP, ERROR_DURING_INSERTION, e.getMessage()));
    }

  }

  private void executeOption2() {

    try {

      var customer = getCustomerDtoIfValid(createCustomerDtoFromUserInput());
      customerService.addCustomerToDbFromUserInput(customer);

    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", CUSTOMER, ERROR_DURING_INSERTION, e.getMessage()));
    }
  }

  private void executeOption1() {
    new AdminMenu().showAdminMenu(login());
  }

  private void executeOption21() {

    try {

      var country = getCountryDtoIfValid(createCountryDtoFromUserInput());
      countryService.addCountryToDb(country);

    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", COUNTRY, ERROR_DURING_INSERTION, e.getMessage()));
    }
  }

  private void executeOption22() {

    try {

      var category = getCategoryDtoIfValid(createCategoryDtoFromUserInput());
      categoryService.addCategoryToDb(category);

    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", CATEGORY, ERROR_DURING_INSERTION, e.getMessage()));
    }
  }

  private void executeOption23() {

    try {

      CategoryDto categoryDto = getCategoryDtoIfValid(specifyCategoryDtoDetailToDelete());
      categoryService.deleteCategoryByName(categoryDto.getName());

    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", CATEGORY, ERROR_DURING_DELETION, e.getMessage()));
    }
  }

  private void executeOption24() {

    try {

      CountryDto countryDto = getCountryDtoIfValid(specifyCountryDtoDetailToDelete());
      countryService.deleteCountryByName(countryDto.getName());

    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", COUNTRY, ERROR_DURING_DELETION, e.getMessage()));
    }
  }

  private void executeOption26() {

    try {

      TradeDto tradeDto = getTradeDtoIfValid(specifyTradeDtoDetailToDelete());
      tradeService.deleteTradeByName(tradeDto.getName());

    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", TRADE, ERROR_DURING_DELETION, e.getMessage()));
    }

  }

  private void executeOption25() {

    try {
      var tradeDto = getTradeDtoIfValid(createTradeDtoFromUserInput());
      tradeService.addTradeToDbFromUserInput(tradeDto);

    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", TRADE, ERROR_DURING_INSERTION, e.getMessage()));
    }
  }

  private void executeOption27(){

    try{

      var shopDtp = getShopDtoIfValid(specifyShopDtoDetailToDelete());
      shopService.deleteShopDto(shopDtp);

    }catch (Exception e){
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException(String.format("%s;%s: %s", SHOP, ERROR_DURING_DELETION, e.getMessage()));
    }
  }
}

