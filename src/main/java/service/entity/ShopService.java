package service.entity;

import domain.Shop;
import dto.CountryDto;
import dto.ShopDto;
import exception.AppException;
import mapper.ModelMapper;
import repository.abstract_repository.entity.CountryRepository;
import repository.abstract_repository.entity.ShopRepository;
import repository.impl.CountryRepositoryImpl;
import repository.impl.ShopRepositoryImpl;


import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static util.entity_utils.ShopUtil.getShopDtoIfValid;
import static util.others.UserDataUtils.*;
import static util.update.UpdateShopUtil.getUpdatedShop;


public class ShopService {

  private final ShopRepository shopRepository;

  //  private final CountryService countryService;
  private final CountryRepository countryRepository;

  public ShopService() {
    this.shopRepository = new ShopRepositoryImpl();
    this.countryRepository = new CountryRepositoryImpl();
//    this.countryService = new CountryService();
  }

  public ShopService(ShopRepository shopRepository, /*CountryService countryService*/ CountryRepository countryRepository) {
    this.shopRepository = shopRepository;
//    this.countryService = countryService;
    this.countryRepository = countryRepository;
  }

  private ShopDto setShopComponentsFromDbIfTheyExist(ShopDto shopDto) {

    if (shopDto == null) {
      throw new AppException("Shop object is null");
    }
    return ShopDto.builder()
            .id(shopDto.getId())
            .name(shopDto.getName())
            .countryDto(countryRepository.findCountryByName(shopDto.getCountryDto().getName()).map(ModelMapper::mapCountryToCountryDto).orElse(shopDto.getCountryDto()))
//            .countryDto(countryService.getCountryFromDbIfExists(shopDto.getCountryDto()))
            .build();
  }

  private Optional<ShopDto> addShopToDb(ShopDto shopDto) {

    return shopRepository
            .addOrUpdate(ModelMapper.mapShopDtoToShop(shopDto))
            .map(ModelMapper::mapShopToShopDto);
  }

  public void addShopToDbFromUserInput(ShopDto shopDto) {

    if (shopDto == null) {
      throw new AppException("ShopDto object is null");
    }

    if (isShopUniqueByNameAndCountry(shopDto.getName(), shopDto.getCountryDto())) {
      addShopToDb(setShopComponentsFromDbIfTheyExist(shopDto));
    } else {
      throw new AppException(String.format("You couldn't add shop to db. Shop is not unique by name: %s and country: %s", shopDto.getName(), shopDto.getCountryDto().getName()));
    }
  }

  private boolean isShopUniqueByNameAndCountry(String name, CountryDto countryDto) {
    if (name == null || name.equals("")) {
      throw new AppException(String.format("Shop name is undefined/null: %s", name));
    }

    return shopRepository
            .findShopByNameAndCountry(name, countryDto.getName()).isEmpty();
  }

  private Optional<ShopDto> getShopDtoByNameAndCountry(String name, String countryName) {

    if(name == null || countryName == null){
      throw new AppException(String.format("Shop name or/and countryName is null: Shop name:%sCountry name: %s", name, countryName));
    }
    return shopRepository.findShopByNameAndCountry(name, countryName)
            .map(ModelMapper::mapShopToShopDto);
  }

  List<ShopDto> getShopsByName(String name) {
    return shopRepository.findShopListByName(name)
            .stream()
            .map(ModelMapper::mapShopToShopDto)
            .collect(Collectors.toList());
  }

  ShopDto getShopFromDbIfExists(ShopDto shopDto) {

    if (shopDto == null) {
      throw new AppException("ShopDto object is null");
    }

    return getShopDtoByNameAndCountry(shopDto.getName(), shopDto.getCountryDto().getName()).orElse(shopDto);
  }

  public List<ShopDto> getAllShops() {
    return shopRepository.findAll()
            .stream()
            .map(ModelMapper::mapShopToShopDto)
            .collect(Collectors.toList());
  }

  private Optional<ShopDto> getShopById(Long shopId) {

    if (shopId == null) {
      throw new AppException("ShopId is null");
    }

    return shopRepository.findShopById(shopId)
            .map(ModelMapper::mapShopToShopDto);
  }

  public void updateShop() {

    printCollectionWithNumeration(getAllShops());

    long shopId = getInt("Choose shop id you want to update");

    getShopById(shopId)
            .ifPresentOrElse(shopDto ->
                            shopRepository
                                    .addOrUpdate(ModelMapper
                                            .mapShopDtoToShop(setShopComponentsFromDbIfTheyExist(getShopDtoIfValid(getUpdatedShop(shopDto))))),
                    () -> {
                      throw new AppException("There is no shop with that id: " + shopId + " in DB");
                    });
  }

  public void deleteShopDto(ShopDto shopDtp) {
    if (shopDtp == null) {
      throw new AppException("Shop is null/ undefined");
    }
    shopRepository.deleteShopDto(shopDtp);
  }

  public void deleteAllShops() {
    shopRepository.deleteAll();
  }
}


