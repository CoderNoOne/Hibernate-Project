package utils.entity_utils;

import domain.Stock;
import exception.AppException;
import validator.impl.StockValidator;

import java.util.stream.Collectors;

import static utils.UserDataUtils.getString;
import static utils.UserDataUtils.printMessage;

public class StockUtil {

  private static final StockValidator stockValidator = new StockValidator();

  private StockUtil() {
  }

  public static Stock createStockFromUserInput() {

    Stock stock = Stock.builder().name(getString("Input stock name")).build();
    var errorsMap = stockValidator.validate(stock);

    if (stockValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("Stock is not valid");
    }

    return stock;

  }

}
