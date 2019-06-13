package utils.entity_utils;

import domain.Category;
import domain.Country;
import domain.Producer;
import domain.Product;
import exception.AppException;
import utils.UserDataUtils;
import validator.impl.ProductValidator;

import java.util.stream.Collectors;

public final class ProductUtil {

  private static final ProductValidator productValidator = new ProductValidator();

  private ProductUtil() {
  }

  public static Product createProductFromUserInput() {

    var name = UserDataUtils.getString("Input product name");
    var price = UserDataUtils.getBigDecimal("Input product price");
    var category = Category.builder().name(UserDataUtils.getString("Input category name")).build();
    var country = Country.builder().name(UserDataUtils.getString("Input country name")).build();
    var producer = Producer.builder().name(UserDataUtils.getString("Input producer name")).country(country).build();

    // nazwy usług gwarancyjnych  jeszcze trzeba dodać

    var product = Product.builder()
            .category(category)
            .name(name)
            .price(price)
            .producer(producer)
            .build();

    var errorsMap = productValidator.validate(product);

    if (productValidator.hasErrors()) {
      UserDataUtils.printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("Product is not valid: " + productValidator.getErrors());
    }
    return product;
  }
}
