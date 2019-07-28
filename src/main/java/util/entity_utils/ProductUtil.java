package util.entity_utils;

import domain.enums.EGuarantee;
import dto.*;
import exception.AppException;
import util.others.UserDataUtils;
import validator.impl.ProductDtoValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static util.others.UserDataUtils.*;

public interface ProductUtil {


  static ProductDto specifyProductForStock(ProductDto productDto, List<ProductDto> productsList) {

    return (!productsList.isEmpty() ?
            chooseAvailableProduct(productsList) : getProductDtoIfValid(preciseProductDtoDetails(productDto)));
  }

  static ProductDto getProductDtoToUpdate(Long id) {

    return ProductDto.builder()
            .id(id)
            .name(getString("Do you want to update product name? (y/n)")
                    .equalsIgnoreCase("Y") ? getString("Specify new product name") : null)
            .price(getString("Do you want to update product price (y/n)")
                    .equalsIgnoreCase("y") ? getBigDecimal("Specify product new price") : null)
            .guaranteeComponents(getString("Do you want to update product guarantee components? (y/n)")
                    .equalsIgnoreCase("y") ? createGuaranteeComponentsFromUserInput() : null)
            .build();
  }

  static ProductDto createProductDtoFromUserInput() {

    return ProductDto.builder()
            .categoryDto(CategoryDto.builder()
                    .name(getString("Input category name"))
                    .build())
            .name(getString("Input product name"))
            .price(getBigDecimal("Input product price"))
            .producerDto(ProducerDto.builder()
                    .name(getString("Input producer name"))
                    .country(CountryDto.builder()
                            .name(getString("Input country name"))
                            .build())
                    .trade(TradeDto.builder()
                            .name(getString("Input trade name"))
                            .build())
                    .build())
            .guaranteeComponents(createGuaranteeComponentsFromUserInput())
            .build();
  }

  static ProductDto getProductDtoIfValid(ProductDto product) {

    var productValidator = new ProductDtoValidator();
    var errorsMap = productValidator.validate(product);

    if (productValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("Product is not valid" + errorsMap);
    }
    return product;
  }

  static ProductDto preciseProductDtoDetails(ProductDto productDto) {

    printMessage(String.format("Any product with specified name:%s and category: %s  exists in a DB. You need to specify product details: "
            , productDto.getName(), productDto.getCategoryDto().getName()));

    return ProductDto.builder()
            .name(productDto.getName())
            .categoryDto(productDto.getCategoryDto())
            .price(getBigDecimal("Input product price"))
            .guaranteeComponents(createGuaranteeComponentsFromUserInput())
            .producerDto(ProducerDto.builder()
                    .name(getString("Input producer name"))
                    .country(CountryDto.builder()
                            .name(getString("Input producer country name"))
                            .build())
                    .trade(TradeDto.builder()
                            .name(getString("Input producer trade name"))
                            .build())
                    .build())
            .build();
  }

  static List<EGuarantee> createGuaranteeComponentsFromUserInput() {

    List<EGuarantee> guaranteeListInput = new ArrayList<>();

    while (getString("Do you want to add guarantee component? (Y/N)").equalsIgnoreCase("Y")) {

      printCollectionWithNumeration(Arrays.asList(EGuarantee.values()));
      var eGuarantee = UserDataUtils.getInt("Choose what eguarantee you want to add. Choose number");

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


  static ProductDto chooseAvailableProduct(Collection<ProductDto> products) {

    if (products.isEmpty()) {
      throw new AppException("There are no products who meet specified criteria");
    }

    List<ProductDto> productLists = new ArrayList<>(products);
    int productNumber;
    do {
      printCollectionWithNumeration(productLists.stream().map(ProductDto::getName).collect(Collectors.toList()));
      productNumber = getInt("Choose product by number");
    } while (!(productNumber >= 1 && productNumber <= productLists.size()));

    return productLists.get(productNumber - 1);
  }

}

