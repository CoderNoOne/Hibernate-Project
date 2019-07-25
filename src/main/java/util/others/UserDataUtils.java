package util.others;

import exception.AppException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;


public final class UserDataUtils {

  private static Scanner sc = new Scanner(System.in);

  private UserDataUtils() {
  }

  public static int getInt(String message) {

    if (message == null) {
      throw new AppException("GET INT - MESSAGE IS NULL");
    }
    printMessage(message);

    String text = sc.nextLine();
    if (!text.matches("[\\d]+")) {
      throw new AppException(("INT VALUE IS NOT CORRECT: " + text));
    }
    return Integer.parseInt(text);
  }

  public static long getLong(String message) {

    if (message == null) {
      throw new AppException("GET LONG - MESSAGE IS NULL");
    }
    printMessage(message);

    String text = sc.nextLine();
    if (!text.matches("[\\d]+")) {
      throw new AppException(("LONG VALUE IS NOT CORRECT: " + text));
    }
    return Long.parseLong(text);
  }

  public static String getString(String inputMessage) {
    if (inputMessage == null) {
      throw new AppException("GET STRING - MESSAGE IS NULL");
    }

    printMessage(inputMessage);
    String input = sc.nextLine();

    if (input.length() == 0) {
      throw new AppException("YOU DIDN'T INPUT ANY VALUE");
    }
    return input;
  }

  public static void printCollectionWithNumeration(Collection<? extends Object> collectionToPrint) {
    AtomicInteger counter = new AtomicInteger(1);
    collectionToPrint.forEach(obj -> printMessage("No. " + counter.getAndIncrement() + " : " + obj));
  }

  public static void printMessage(String message) {
    System.out.println(message);
  }


  public static BigDecimal getBigDecimal(String inputMessage) {

    if (inputMessage == null) {
      throw new AppException("GET BIG DECIMAL - MESSAGE IS NULL");
    }

    printMessage(inputMessage);
    String input = sc.nextLine();

    if (input.length() == 0) {
      throw new AppException("You didn't input any value");
    }

    if (!input.matches("\\d+(\\.\\d+)*")) {
      throw new AppException("Big Decimal Value is not correct: " + input);
    }

    return new BigDecimal(input);
  }

  public static LocalDateTime getLocalDateTime(String message) {

    if (message == null) {
      throw new AppException("GET LOCAL DATE TIME - MESSAGE IS NULL");
    }

    try {
      printMessage(message);
      String date = sc.nextLine();
      return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    } catch (DateTimeParseException e) {
      throw new AppException("DATE FORMAT NOT SUPPORTED");
    }
  }

  public static LocalDate getLocalDate(String message) {

    if (message == null) {
      throw new AppException("GET LOCAL DATE - MESSAGE IS NULL");
    }

    try {
      printMessage(message);
      String date = sc.nextLine();
      return LocalDate.parse(date);
    } catch (DateTimeParseException e) {
      throw new AppException("DATE FORMAT NOT SUPPORTED");
    }
  }

  public static void close() {
    if (sc != null) {
      sc.close();
      sc = null;
    }
  }


  public static String login() {

    String gmailAddress;

    do {
      gmailAddress = getString("Input gmail account addres");
    } while (!gmailAddress.matches(".+@gmail.com"));

    var password = GenerateSecurePasswordUtil.generatePassword();
    EmailUtils.sendPassword(gmailAddress, "Your password", "Your password: " + password);

    if (getString("Check your mail box for password.\nInput generated password from gmail").equals(password)) {
      return gmailAddress;
    } else {
      throw new AppException("Incorrect login to admin panel with email: " + gmailAddress + " Login date: " + LocalDateTime.now());
    }
  }

  public static String defineXlsFileName(String message) {

    if (message == null || message.equals("")) {
      throw new AppException("Message is null or blank");
    }
    printMessage(message);

    String fileName = sc.nextLine();

    if (fileName.length() == 0) {
      throw new AppException("YOU DIDN'T INPUT ANY VALUE");
    }

    return String.format("%s.xlsx", fileName);
  }
}
