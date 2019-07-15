package service.entity;

import dto.CountryDto;
import dto.ShopDto;
import exception.AppException;
import mapper.ModelMapper;
import repository.abstract_repository.entity.ShopRepository;
import repository.impl.ShopRepositoryImpl;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static util.entity_utils.ShopUtil.getShopDtoIfValid;
import static util.others.UserDataUtils.getInt;
import static util.others.UserDataUtils.printCollectionWithNumeration;
import static util.update.UpdateShopUtil.getUpdatedShop;


public class ShopService {

  private final ShopRepository shopRepository;
  private final CountryService countryService;

  public ShopService() {
    this.shopRepository = new ShopRepositoryImpl();
    this.countryService = new CountryService();
  }

  public ShopService(ShopRepository shopRepository, CountryService countryService) {
    this.shopRepository = shopRepository;
    this.countryService = countryService;
  }

  private ShopDto setShopComponentsFromDbIfTheyExist(ShopDto shopDto) {

    return ShopDto.builder()
            .id(shopDto.getId())
            .name(shopDto.getName())
            .countryDto(countryService.getCountryFromDbIfExists(shopDto.getCountryDto()))
            .build();
  }

  private Optional<ShopDto> addShopToDb(ShopDto shopDto) {

    return shopRepository
            .addOrUpdate(ModelMapper.mapShopDtoToShop(shopDto))
            .map(ModelMapper::mapShopToShopDto);
  }

  public void addShopToDbFromUserInput(ShopDto shopDto) {
    if (isShopUniqueByNameAndCountry(shopDto.getName(), shopDto.getCountryDto())) {
      addShopToDb(setShopComponentsFromDbIfTheyExist(shopDto));
    } else {
      throw new AppException("You couldn't add shop to db. Shop is not unique by name and country");
    }
  }

  private boolean isShopUniqueByNameAndCountry(String name, CountryDto countryDto) {
    if (name == null) {
      throw new AppException("Shop name is null");
    }

    return shopRepository
            .findShopByNameAndCountry(name, countryDto.getName()).isEmpty();
  }

  private Optional<ShopDto> getShopDtoByNameAndCountry(String name, String countryName) {
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
    return getShopDtoByNameAndCountry(shopDto.getName(), shopDto.getCountryDto().getName()).orElse(shopDto);
  }

  public void deleteAllShops() {
    shopRepository.deleteAll();
  }

  private List<ShopDto> getAllShops() {
    return shopRepository.findAll()
            .stream()
            .map(ModelMapper::mapShopToShopDto)
            .collect(Collectors.toList());
  }

  private Optional<ShopDto> getShopById(Long shopId) {
    return shopRepository.findById(shopId)
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
}


