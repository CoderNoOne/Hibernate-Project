package util.update;

import domain.Product;
import dto.ProductDto;
import util.update.enums.ProductField;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static util.others.UserDataUtils.*;
import static util.others.UserDataUtils.getString;

public interface UpdateProductUtil {

  static ProductDto getUpdatedProduct(ProductDto productDto) {

    List<ProductField> productFields = Arrays.stream(ProductField.values()).collect(Collectors.toList());

    boolean hasNext;
    do {
      printCollectionWithNumeration(productFields);
      ProductField productProperty = ProductField.valueOf(getString("Choose what product property you want to update. Not case sensitive").toUpperCase());

      switch (productProperty) {
        case NAME -> {
          String updatedName = getString("Type product new name");
          productFields.remove(ProductField.NAME);
          productDto.setName(updatedName);
        }

        case PRICE -> {
          BigDecimal updatedProductPrice = getBigDecimal("Type product new price");
          productFields.remove(ProductField.PRICE);
          productDto.setPrice(updatedProductPrice);
        }
        default -> System.out.println("Not valid product property");
      }
      hasNext = getString("Do you want to update other customer property? (Y/N)").equalsIgnoreCase("Y");
    } while (hasNext && !productFields.isEmpty());

    return productDto;
  }

}
