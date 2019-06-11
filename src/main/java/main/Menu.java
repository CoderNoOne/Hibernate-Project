package main;

import configuration.DbConnection;
import domain.Error;
import exception.AppException;
import helper.OptionalHelper;
import service.CountryService;
import service.CustomerService;
import service.ErrorService;
import utils.UserDataUtils;
import utils.entityUtils.CustomerUtil;

import java.text.MessageFormat;
import java.time.LocalDateTime;

import static utils.UserDataUtils.printMessage;


class Menu {

  private final CustomerService customerService = new CustomerService();
  private final CountryService countryService = new CountryService();
  private final ErrorService errorService = new ErrorService();


  void mainMenu() {
    showMenuOptions();
    while (true) {
      try {

        int option = UserDataUtils.getInt("Input your option");
        switch (option) {
          case 1 -> showOption1();
          case 2 -> showOption2();
          case 3 -> showOption3();
          case 4 -> showOption4();
          case 10 -> DbConnection.close();
        }
      } catch (AppException e) {
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
            "Add new movie from json file",
            "Generate example data for table movies and customers",
            "Movie and customer table management",
            "Buy a ticket",
            "History - summary",
            "Some statistics",
            "Move in time forwardly",
            "Move in time backwardly",
            "Show main menu options",
            "Exit the program"
    ));
  }

  private void showOption4() {

  }

  private void showOption3() {

  }

  private void showOption2() {

  }

  private void showOption1() {

    var customer = CustomerUtil.createCustomer();

    var country = OptionalHelper.of(countryService.
            findCountryByName(customer.getCountry().getName()))
            .ifNotPresent(() ->
                    countryService.addCountryToDb(customer.getCountry()).orElseThrow(() -> new AppException(";Country is null")));

    customer.setCountry(country);
    customerService.addCustomerToDb(customer);

  }
}
