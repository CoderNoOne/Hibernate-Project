package service.entity;

import dto.CountryDto;
import dto.ProducerDto;
import dto.TradeDto;
import exception.AppException;
import mappers.CountryMapper;
import mappers.ProducerMapper;
import mappers.TradeMapper;
import repository.abstract_repository.entity.ProducerRepository;
import repository.impl.ProducerRepositoryImpl;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static util.entity_utils.ProducerUtil.getProducerDtoIfValid;
import static util.others.UserDataUtils.getInt;
import static util.others.UserDataUtils.printCollectionWithNumeration;
import static util.update.UpdateProducerUtil.getUpdatedProducerDto;

public class ProducerService {

  private final ProducerRepository producerRepository;
  private final TradeService tradeService;
  private final CountryService countryService;
  private final ProducerMapper producerMapper;
  private final TradeMapper tradeMapper;
  private final CountryMapper countryMapper;

  public ProducerService() {
    this.producerRepository = new ProducerRepositoryImpl();
    this.tradeService = new TradeService();
    this.countryService = new CountryService();
    this.producerMapper = new ProducerMapper();
    this.tradeMapper = new TradeMapper();
    this.countryMapper = new CountryMapper();
  }

  public Optional<ProducerDto> addProducerToDb(ProducerDto producerDto) {
    return producerRepository
            .addOrUpdate(producerMapper.mapProducerDtoToProducer(producerDto))
            .map(producerMapper::mapProducerToProducerDto);
  }


  public ProducerDto setProducerComponentsFromDbIfTheyExist(ProducerDto producerDto) {

    return ProducerDto.builder()
            .id(producerDto.getId())
            .name(producerDto.getName())
            .trade(tradeService.getTradeFromDbIfExists(producerDto.getTrade()))
            .country(countryService.getCountryFromDbIfExists(producerDto.getCountry()))
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
            name, tradeMapper.mapTradeDtoToTrade(tradeDto), countryMapper.mapCountryDtoToCountry(countryDto)).isEmpty();
  }

  public Optional<ProducerDto> getProducerDtoByNameAndTradeAndCountry(String name, TradeDto tradeDto, CountryDto countryDto) {
    return producerRepository.findByNameAndTradeAndCountry(name, tradeMapper.mapTradeDtoToTrade(tradeDto), countryMapper.mapCountryDtoToCountry(countryDto))
            .map(producerMapper::mapProducerToProducerDto);
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
                                    .addOrUpdate(producerMapper
                                            .mapProducerDtoToProducer(setProducerComponentsFromDbIfTheyExist(getProducerDtoIfValid(getUpdatedProducerDto(producerDto)))))
                                    .map(producerMapper::mapProducerToProducerDto),
                    () -> {
                      throw new AppException("There is no producer with that id: " + producerId + " in DB");
                    });
  }

  private Optional<ProducerDto> getProducerDtoById(long producerId) {
    return producerRepository.findById(producerId)
            .map(producerMapper::mapProducerToProducerDto);
  }

  private List<ProducerDto> getAllProducers() {
    return producerRepository.findAll()
            .stream()
            .map(producerMapper::mapProducerToProducerDto)
            .collect(Collectors.toList());
  }

}
