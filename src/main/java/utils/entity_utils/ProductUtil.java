package utils.entity_utils;

import domain.*;
import domain.enums.EGuarantee;
import exception.AppException;
import utils.others.UserDataUtils;
import validator.impl.ProductValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static utils.others.UserDataUtils.*;

public final class ProductUtil {

  private static final ProductValidator productValidator = new ProductValidator();

  private ProductUtil() {
  }

  public static Product createProductFromUserInput() {

    return Product.builder()
            .category(Category.builder()
                    .name(getString("Input category name"))
                    .build())
            .name(getString("Input product name"))
            .price(getBigDecimal("Input product price"))
            .producer(Producer.builder()
                    .name(getString("Input producer name"))
                    .country(Country.builder().name(getString("Input country name")).build())
                    .trade(Trade.builder().name(getString("Input trade name")).build())
                    .build())
            .guaranteeComponent(createGuaranteeComponentsFromUserInput())
            .build();
  }

  public static Product getProductIfValid(Product product) {

    var productValidator = new ProductValidator();
    var errorsMap = productValidator.validate(product);

    if (productValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("Product is not valid" + errorsMap);
    }
    return product;
  }

  public static Product preciseProductDetails(Stock stock) {

    printMessage(String.format("Any product with specified name:%s and category: %s  exists in a DB. You need to specify product details: "
            , stock.getProduct().getName(), stock.getProduct().getCategory().getName()));

    return Product.builder()
            .name(stock.getProduct().getName())
            .category(stock.getProduct().getCategory())
            .price(getBigDecimal("Input product price"))
            .guaranteeComponent(createGuaranteeComponentsFromUserInput())
            .producer(Producer.builder()
                    .name(getString("Input producer name"))
                    .country(Country.builder()
                            .name(getString("Input producer country name"))
                            .build())
                    .trade(Trade.builder()
                            .name(getString("Input producer trade name"))
                            .build())
                    .build())
            .build();
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
        default -> printMessage("Not proper value");
      }
    }
    return guaranteeListInput;
  }

  public static Product chooseAvailableProduct(List<Product> productList) {

    if(productList.isEmpty()){
      throw new AppException("There are no products who meet specified criteria");
    }

    int productNumber;
    do {
      printCollectionWithNumeration(productList.stream().map(Product::getName).collect(Collectors.toList()));
      productNumber = getInt("Choose product by number");
    } while (!(productNumber >= 1 && productNumber <= productList.size()));

    return productList.get(productNumber - 1);
  }

}

