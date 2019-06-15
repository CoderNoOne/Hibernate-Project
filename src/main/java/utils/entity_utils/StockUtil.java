package utils.entity_utils;

import domain.*;
import exception.AppException;
import utils.UserDataUtils;
import validator.impl.StockValidator;

import java.util.stream.Collectors;

import static utils.UserDataUtils.*;
import static utils.UserDataUtils.getString;
import static utils.UserDataUtils.printMessage;

public class StockUtil {

  private static final StockValidator stockValidator = new StockValidator();

  private StockUtil() {
  }

  public static Stock createStockFromUserInput() {

    var stock = Stock.builder()
            .product(Product.builder()
                    .name(getString("Input product name"))
                    .category(Category.builder()
                            .name(getString("Input category name"))
                            .build())
                    .price(getBigDecimal("Input product price"))
                    .producer(Producer.builder()
                            .name(getString("Input producer name"))
                            .country(Country.builder()
                                    .name(getString("Input producer country"))
                                    .build())
                            .trade(Trade.builder()
                                    .name(getString("Input producer trade"))
                                    .build())
                            .build())
                    .build())
            .shop(Shop.builder()
                    .name(getString("Input shop name"))
                    .country(Country.builder()
                            .name(getString("Input shop country name"))
                            .build())
                    .build())
            .quantity(getInt("Input stock quantity"))
            .build();

    var errorsMap = stockValidator.validate(stock);

    if (stockValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("Stock is not valid");
    }
    return stock;
  }

}
