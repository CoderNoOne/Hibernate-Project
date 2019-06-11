package main;

import configuration.DbConnection;
import domain.Error;
import exception.AppException;
import helper.OptionalHelper;
import service.CountryService;
import service.CustomerService;
import service.ErrorService;
import service.ShopService;
import utils.UserDataUtils;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Arrays;

import static utils.UserDataUtils.printMessage;
import static utils.entity_utils.CustomerUtil.createCustomerFromUserInput;


class Menu {

  private final CustomerService customerService = new CustomerService();
  private final CountryService countryService = new CountryService();
  private final ErrorService errorService = new ErrorService();
  private final ShopService shopService = new ShopService();


  void mainMenu() {
    showMenuOptions();
    while (true) {
      try {

        int option = UserDataUtils.getInt("Input your option");
        switch (option) {
          case 1 -> executeOption1();
          case 2 -> executeOption2();
          case 3 -> showOption3();
          case 4 -> showOption4();
          case 10 -> DbConnection.close();
        }
      } catch (AppException e) {
        System.out.println(e.getMessage());
        System.out.println(Arrays.toString(e.getStackTrace()));
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
            "Add new shop"

    ));
  }

  private void showOption4() {

  }

  private void showOption3() {

  }

  private void executeOption2() {

//    try {
    var validatedShop = shopService.getValidatedShopFromUserInput();

    OptionalHelper.of(countryService
            .getCountryByName(validatedShop.getCountry().getName()))
            .
                    shopService.addShopToDb(validatedShop);

//    } catch (Exception e) {
//
//      throw new AppException("");
//    }

  }

  private void executeOption1() {

    try {
      var customer = createCustomerFromUserInput();

      var country = OptionalHelper.of(countryService
              .getCountryByName(customer.getCountry().getName()))
              .ifNotPresent(() ->
                      countryService.addCountryToDb(customer.getCountry()).orElseThrow(() -> new AppException(";Country is null")));

      customer.setCountry(country);
      customerService.addCustomerToDb(customer);
    } catch (Exception e) {
      //logowanie bledow
      throw new AppException("");
    }
  }
}
