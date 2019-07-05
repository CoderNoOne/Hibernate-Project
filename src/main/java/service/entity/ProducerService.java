package service.entity;

import domain.Country;
import domain.Producer;
import domain.Trade;
import exception.AppException;
import repository.abstract_repository.entity.ProducerRepository;
import repository.impl.ProducerRepositoryImpl;


import java.util.Optional;

public class ProducerService {

  private final ProducerRepository producerRepository;
  private final TradeService tradeService;
  private final CountryService countryService;

  public ProducerService() {
    this.producerRepository = new ProducerRepositoryImpl();
    this.tradeService = new TradeService();
    this.countryService = new CountryService();
  }

  public Optional<Producer> addProducerToDb(Producer producer) {
    return producerRepository.addOrUpdate(producer);
  }


  public Producer setProducerComponentsFromDbIfTheyExist(Producer producer) {

    return Producer.builder()
            .name(producer.getName())
            .trade(tradeService.getTradeFromDbIfExists(producer.getTrade()))
            .country(countryService.getCountryFromDbIfExists(producer.getCountry()))
            .build();
  }

  public Producer getProducerFromDbIfExists(Producer producer) {
    return getProducerByNameAndTradeAndCountry(
            producer.getName(), producer.getTrade(),
            producer.getCountry()).orElse(producer);
  }

  public void addProducerToDbFromUserInput(Producer producer) {
    if (!isProducerUniqueByNameAndTradeAndCountry(producer.getName(), producer.getTrade(), producer.getCountry())) {
      throw new AppException("Couldn't add new producer to db - producer's not unique by name, trade and country");
    }
    addProducerToDb(setProducerComponentsFromDbIfTheyExist(producer));
  }

  private boolean isProducerUniqueByNameAndTradeAndCountry(String name, Trade trade, Country country) {
    if (trade == null) {
      throw new AppException("Trade is null");
    }

    if (country == null) {
      throw new AppException("Country is null");
    }

    return producerRepository.findByNameAndTradeAndCountry(name, trade, country).isEmpty();
  }

  public Optional<Producer> getProducerByNameAndTradeAndCountry(String name, Trade trade, Country country) {
    return producerRepository.findByNameAndTradeAndCountry(name, trade, country);
  }

  public void deleteAllProducers() {
    producerRepository.deleteAll();
  }

}
