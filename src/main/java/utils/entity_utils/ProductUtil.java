package utils.entity_utils;

import domain.*;
import exception.AppException;
import utils.UserDataUtils;
import validator.impl.ProductValidator;

import java.util.stream.Collectors;

import static utils.UserDataUtils.*;

public final class ProductUtil {

  private static final ProductValidator productValidator = new ProductValidator();

  private ProductUtil() {
  }

  public static Product createProductFromUserInput() {

    var product = Product.builder()
            .category(Category.builder().name(getString("Input category name")).build())
            .name(getString("Input product name"))
            .price(getBigDecimal("Input product price"))
            .producer(Producer.builder()
                    .name(getString("Input producer name"))
                    .country(Country.builder().name(getString("Input country name")).build())
                    .trade(Trade.builder().name(getString("Input trade name")).build())
                    .build())
            .build();

    // nazwy usług gwarancyjnych  jeszcze trzeba dodać guarantee_components

    var errorsMap = productValidator.validate(product);

    if (productValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("Product is not valid: " + productValidator.getErrors());
    }
    return product;
  }
}
