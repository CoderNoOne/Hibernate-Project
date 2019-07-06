package main;

import domain.Error;
import exception.AppException;
import lombok.extern.slf4j.Slf4j;
import service.entity.ErrorService;
import service.other.DataInitializeService;
import service.other.ExportErrorsToExcelService;
import util.others.EmailUtils;
import util.others.UserDataUtils;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Arrays;

import static util.others.UserDataUtils.defineXlsFileName;
import static util.others.UserDataUtils.printMessage;

@Slf4j
public class AdminMenu {

  private final ErrorService errorService = new ErrorService();
  private final DataInitializeService dataInitializeService = new DataInitializeService();
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
          case 2 -> executeOption2(userEmail);
          case 3 -> {
            executeOption3();
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

  private void executeOption3() {
    new Menu().mainMenu();
  }

  private void executeOption2(String userEmail) {

    String excelFileName = defineXlsFileName("Input .xlsx filename (without suffix)");
    errorsToExcelService.exportToExcel(excelFileName);
    EmailUtils.sendAsHtmlWithAttachment(userEmail, "Errors. Date: " + LocalDateTime.now(), "Errors from app attachement", excelFileName);
  }

  private void executeOption1() {
    dataInitializeService.init();
  }

  private void showAdminMenuOptions() {

    printMessage(MessageFormat.format(
            "\nOption no. 1 - {0}\n" +
                    "Option no. 2 - {1}\n" +
                    "Option no. 3 - {2}\n",

            "Initialize data",
            "Store errors in .xlsl file and then clear error table",
            "Back to main menu"

    ));
  }

}
