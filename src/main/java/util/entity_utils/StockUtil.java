package util.entity_utils;

import domain.*;
import exception.AppException;
import validator.impl.StockValidator;

import java.util.stream.Collectors;

import static util.others.UserDataUtils.*;
import static util.others.UserDataUtils.getString;
import static util.others.UserDataUtils.printMessage;

public class StockUtil {

  private StockUtil() {
  }

  public static Stock createStockDetailFromUserInput() {

    return Stock.builder()
            .product(Product.builder()
                    .name(getString("Input product name"))
                    .category(Category.builder()
                            .name(getString("Input category name"))
                            .build())
                    .build())
            .shop(Shop.builder()
                    .name(getString("Input shop name"))
                    .build())
            .quantity(getInt("Input stock quantity"))
            .build();
  }

  public static Stock getStockIfValid(Stock stock) {

    if (stock == null) {
      throw new AppException("Stock is null");
    }
    var stockValidator = new StockValidator();
    var errorsMap = stockValidator.validate(stock);

    if (stockValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("Stock is not valid");
    }
    return stock;
  }
}
