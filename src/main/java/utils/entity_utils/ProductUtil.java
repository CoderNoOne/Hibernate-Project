package utils.entity_utils;

import domain.*;
import domain.enums.EGuarantee;
import exception.AppException;
import utils.UserDataUtils;
import validator.impl.ProductValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
            .guaranteeComponent(createGuaranteeComponentsFromUserInput())
            .build();

    var errorsMap = productValidator.validate(product);

    if (productValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("Product is not valid: " + productValidator.getErrors());
    }
    return product;
  }

  private static List<EGuarantee> createGuaranteeComponentsFromUserInput() {

    List<EGuarantee> guaranteeListInput = new ArrayList<>();

    while (getString("Do you want to add guarantee component? (Y/N)").equalsIgnoreCase("Y")) {

      printCollectionWithNumeration(Arrays.asList(EGuarantee.values()));
      var eGuarantee = UserDataUtils.getInt("Choose what eguarantee you want to update. Choose number");

      switch (eGuarantee) {
        case 1 -> guaranteeListInput.add(EGuarantee.HELP_DESK);
        case 2 -> guaranteeListInput.add(EGuarantee.MONEY_BACK);
        case 3 -> guaranteeListInput.add(EGuarantee.SERVICE);
        case 4 -> guaranteeListInput.add(EGuarantee.EXCHANGE);
        default -> System.out.println("Not proper value");
      }
    }
    return guaranteeListInput;
  }
}
