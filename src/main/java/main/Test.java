package main;

import org.jasypt.util.password.StrongPasswordEncryptor;
import service.other.DataInitializeService;
import service.other.ExportErrorsToExcelService;
import util.others.EmailUtils;
import util.others.GenerateSecurePasswordUtil;

import java.security.SecureRandom;
import java.util.Random;

public class Test {

  public static void main(String[] args) {
//    new DataInitializeService().init();
//    new ExportErrorsToExcelService().exportToExcel("errors5.xlsx");
//    EmailUtils.sendAsHtmlWithAttachment("firelight.code@gmail.com","Errors", "Błędy z aplikacji znajdują się w załączniku",
//            "errors5.xlsx");

    System.out.println(GenerateSecurePasswordUtil.generatePassword());


  }

  public static String generatePassword(int length) {

    StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();

    Random RANDOM = new SecureRandom();
    String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    StringBuilder returnValue = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
    }
    System.out.println(returnValue);
    return passwordEncryptor.encryptPassword(returnValue.toString());
  }
}
