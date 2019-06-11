package service;

import domain.Shop;
import exception.AppException;
import repository.abstract_repository.entity.ShopRepository;
import repository.impl.ShopRepositoryImpl;
import utils.entity_utils.ShopUtil;
import validator.impl.ShopValidator;

import java.util.Optional;
import java.util.stream.Collectors;

import static utils.UserDataUtils.printMessage;
import static utils.entity_utils.ShopUtil.*;

public class ShopService {

  private final ShopRepository shopRepository;
  private final ShopValidator shopValidator;

  public ShopService() {
    shopRepository = new ShopRepositoryImpl();
    shopValidator = new ShopValidator();
  }

  public Shop getValidatedShopFromUserInput() {
    var shop = createShopFromUserInput();
    var errorMap = shopValidator.validate(shop);



    if (shopValidator.hasErrors()) {
      printMessage(errorMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));

      throw new AppException( "Shop is not valid");
    }

    return shop;
  }


  public Optional<Shop> addShopToDb(Shop shop) {

    return shopRepository.addOrUpdate(shop);
  }
}


