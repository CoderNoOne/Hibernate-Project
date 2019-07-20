package service.entity;

import dto.CountryDto;
import dto.ProducerDto;
import dto.TradeDto;
import exception.AppException;
import mapper.ModelMapper;
import repository.abstract_repository.entity.CountryRepository;
import repository.abstract_repository.entity.ProducerRepository;
import repository.abstract_repository.entity.TradeRepository;
import repository.impl.CountryRepositoryImpl;
import repository.impl.ProducerRepositoryImpl;
import repository.impl.TradeRepositoryImpl;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static util.entity_utils.ProducerUtil.createProducerDtoFromUserInput;
import static util.entity_utils.ProducerUtil.getProducerDtoIfValid;
import static util.others.UserDataUtils.getInt;
import static util.others.UserDataUtils.printCollectionWithNumeration;
import static util.update.UpdateProducerUtil.getUpdatedProducerDto;

public class ProducerService {

  private final ProducerRepository producerRepository;

  private final TradeRepository tradeRepository;
  private final CountryRepository countryRepository;

  public ProducerService() {
    this.producerRepository = new ProducerRepositoryImpl();
    this.tradeRepository = new TradeRepositoryImpl();
    this.countryRepository = new CountryRepositoryImpl();
  }

  public ProducerService(ProducerRepository producerRepository, TradeRepository tradeRepository, CountryRepository countryRepository) {
    this.producerRepository = producerRepository;
    this.tradeRepository = tradeRepository;
    this.countryRepository = countryRepository;
  }

  public Optional<ProducerDto> addProducerToDb(ProducerDto producerDto) {
    return producerRepository
            .addOrUpdate(ModelMapper.mapProducerDtoToProducer(producerDto))
            .map(ModelMapper::mapProducerToProducerDto);
  }


  public ProducerDto setProducerComponentsFromDbIfTheyExist(ProducerDto producerDto) {

    return ProducerDto.builder()
            .id(producerDto.getId())
            .name(producerDto.getName())
            .trade(tradeRepository.findTradeByName(producerDto.getTrade().getName())
                    .map(ModelMapper::mapTradeToTradeDto)
                    .orElse(producerDto.getTrade()))
            .country(countryRepository.findCountryByName(producerDto.getCountry().getName())
                    .map(ModelMapper::mapCountryToCountryDto)
                    .orElse(producerDto.getCountry()))
            .build();
  }

  public ProducerDto getProducerFromDbIfExists(ProducerDto producerDto) {
    return getProducerDtoByNameAndTradeAndCountry(
            producerDto.getName(), producerDto.getTrade(),
            producerDto.getCountry()).orElse(producerDto);
  }

  public void addProducerToDbFromUserInput(ProducerDto producerDto) {
    if (!isProducerUniqueByNameAndTradeAndCountry(producerDto.getName(), producerDto.getTrade(), producerDto.getCountry())) {
      throw new AppException("Couldn't add new producer to db - producer's not unique by name, trade and country");
    }
    addProducerToDb(setProducerComponentsFromDbIfTheyExist(producerDto));
  }

  private boolean isProducerUniqueByNameAndTradeAndCountry(String name, TradeDto tradeDto, CountryDto countryDto) {
    if (tradeDto == null) {
      throw new AppException("Trade is null");
    }

    if (countryDto == null) {
      throw new AppException("Country is null");
    }

    return producerRepository.findByNameAndTradeAndCountry(
            name, ModelMapper.mapTradeDtoToTrade(tradeDto), ModelMapper.mapCountryDtoToCountry(countryDto)).isEmpty();
  }

  public Optional<ProducerDto> getProducerDtoByNameAndTradeAndCountry(String name, TradeDto tradeDto, CountryDto countryDto) {
    return producerRepository.findByNameAndTradeAndCountry(name, ModelMapper.mapTradeDtoToTrade(tradeDto), ModelMapper.mapCountryDtoToCountry(countryDto))
            .map(ModelMapper::mapProducerToProducerDto);
  }

  public void deleteAllProducers() {
    producerRepository.deleteAll();
  }

  public void updateProducer() {

    printCollectionWithNumeration(getAllProducers());
    long producerId = getInt("Choose producer id you want to update");

    getProducerDtoById(producerId)
            .ifPresentOrElse(producerDto ->
                            producerRepository
                                    .addOrUpdate(ModelMapper
                                            .mapProducerDtoToProducer(setProducerComponentsFromDbIfTheyExist(getProducerDtoIfValid(getUpdatedProducerDto(producerDto)))))
                                    .map(ModelMapper::mapProducerToProducerDto),
                    () -> {
                      throw new AppException("There is no producer with that id: " + producerId + " in DB");
                    });
  }

  private Optional<ProducerDto> getProducerDtoById(long producerId) {
    return producerRepository.findShopById(producerId)
            .map(ModelMapper::mapProducerToProducerDto);
  }

  private List<ProducerDto> getAllProducers() {
    return producerRepository.findAll()
            .stream()
            .map(ModelMapper::mapProducerToProducerDto)
            .collect(Collectors.toList());
  }

}
