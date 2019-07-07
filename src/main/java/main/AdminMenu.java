package main;

import domain.Error;
import exception.AppException;
import lombok.extern.slf4j.Slf4j;
import service.entity.ErrorService;
import service.other.DataService;
import service.other.ExportErrorsToExcelService;
import util.others.EmailUtils;
import util.others.UserDataUtils;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Arrays;

import static util.others.UserDataUtils.defineXlsFileName;
import static util.others.UserDataUtils.printMessage;

@Slf4j
class AdminMenu {

  private final ErrorService errorService = new ErrorService();
  private final DataService dataService = new DataService();
  private final ExportErrorsToExcelService errorsToExcelService = new ExportErrorsToExcelService();

  void showAdminMenu(String userEmail) {
    printMessage(String.format("\nWELCOME %s TO ADMIN PANEL!", userEmail.toUpperCase()));
    loop:
    while (true) {
      showAdminMenuOptions();
      try {

        int option = UserDataUtils.getInt("Input your option");
        switch (option) {
          case 1 -> executeOption1();
          case 2 -> executeOption2();
          case 3 -> executeOption3(userEmail);
          case 4 -> {
            executeOption4();
            break loop;
          }
        }
      } catch (AppException e) {
        log.info(e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        errorService.addErrorToDb(Error.builder()
                .date(LocalDateTime.now()).message(e.getMessage()).build());
      }
    }
  }

  private void executeOption1() {
    dataService.deleteAllContent();
  }

  private void executeOption4() {
    new Menu().mainMenu();
  }

  private void executeOption3(String userEmail) {

    String excelFileName = defineXlsFileName("Input .xlsx filename (without suffix)");
    errorsToExcelService.exportToExcel(excelFileName);
    EmailUtils.sendAsHtmlWithAttachment(userEmail, "Errors. Date: " + LocalDateTime.now(), "Errors from app attachement", excelFileName);
  }

  private void executeOption2() {
    dataService.init();
  }

  private void showAdminMenuOptions() {

    printMessage(MessageFormat.format(
            "\nOption no. 1 - {0}\n" +
                    "Option no. 2 - {1}\n" +
                    "Option no. 3 - {2}\n" +
                    "Option no. 4 - {3}\n",

            "Delete all content from DB",
            "Initialize data",
            "Store errors in .xlsx file - Send it to your email file and then clear error table",
            "Back to main menu"

    ));
  }

}
