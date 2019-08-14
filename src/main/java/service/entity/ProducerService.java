package service.entity;

import domain.Producer;
import dto.CountryDto;
import dto.ProducerDto;
import dto.TradeDto;
import exception.AppException;
import lombok.RequiredArgsConstructor;
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

import static util.entity_utils.ProducerUtil.getProducerDtoIfValid;

@RequiredArgsConstructor
public class ProducerService {

  private final ProducerRepository producerRepository;
  private final TradeRepository tradeRepository;
  private final CountryRepository countryRepository;

  public ProducerService() {
    this.producerRepository = new ProducerRepositoryImpl();
    this.tradeRepository = new TradeRepositoryImpl();
    this.countryRepository = new CountryRepositoryImpl();
  }

  public Optional<ProducerDto> addProducerToDb(ProducerDto producerDto) {

    Producer producer = ModelMapper.mapProducerDtoToProducer(producerDto);

    countryRepository
            .findCountryByName(producer.getCountry().getName())
            .ifPresentOrElse(producer::setCountry, () -> countryRepository.add(producer.getCountry()));

    tradeRepository
            .findTradeByName(producer.getTrade().getName())
            .ifPresentOrElse(producer::setTrade, () -> tradeRepository.add(producer.getTrade()));

    producerRepository.add(producer);

    System.out.println(producer);
    return Optional.of(ModelMapper.mapProducerToProducerDto(producer));

  }


  private ProducerDto setProducerComponentsFromDbIfTheyExist(ProducerDto producerDto) {

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

  public void addProducerToDbFromUserInput(ProducerDto producerDto) {
    if (!isProducerUniqueByNameAndTradeAndCountry(producerDto.getName(), producerDto.getTrade(), producerDto.getCountry())) {
      throw new AppException("Couldn't add new producer to db - producer's not unique by name, trade and country");
    }
    addProducerToDb(/*setProducerComponentsFromDbIfTheyExist*/(producerDto));
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

  public Optional<ProducerDto> updateProducer(ProducerDto producerDtoToUpdate) {

    Long id = producerDtoToUpdate.getId();

    if (id == null) {
      throw new AppException("Producer id is null");
    }

    Producer producerFromDb = producerRepository.findById(id)
            .orElseThrow(() -> new AppException("Producer with id: " + id + " doesn't exist in DB yet"));

    ProducerDto producerToUpdate = ProducerDto.builder()
            .id(id)
            .name(producerDtoToUpdate.getName() != null ? producerDtoToUpdate.getName() : producerFromDb.getName())
            .country(producerDtoToUpdate.getCountry() != null ? producerDtoToUpdate.getCountry() : ModelMapper.mapCountryToCountryDto(producerFromDb.getCountry()))
            .trade(ModelMapper.mapTradeToTradeDto(producerFromDb.getTrade()))
            .build();

    return producerRepository
            .add(ModelMapper.mapProducerDtoToProducer(getProducerDtoIfValid(setProducerComponentsFromDbIfTheyExist(producerToUpdate))))
            .map(ModelMapper::mapProducerToProducerDto);

  }

  public List<ProducerDto> getAllProducers() {
    return producerRepository.findAll()
            .stream()
            .map(ModelMapper::mapProducerToProducerDto)
            .collect(Collectors.toList());
  }

}
