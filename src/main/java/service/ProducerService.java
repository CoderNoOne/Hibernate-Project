package service;

import domain.Country;
import domain.Producer;
import domain.Trade;
import exception.AppException;
import repository.abstract_repository.entity.ProducerRepository;
import repository.impl.ProducerRepositoryImpl;


import java.util.Optional;

public class ProducerService {

  private final ProducerRepository producerRepository;

  public ProducerService() {
    this.producerRepository = new ProducerRepositoryImpl();
  }

  public Optional<Producer> addProducerToDb(Producer producer) {
    return producerRepository.addOrUpdate(producer);
  }

  public void addProducerToDbFromUserInput(Producer producer) {
    if (!isProducerUniqueByNameAndTradeAndCountry(producer.getName(), producer.getTrade(), producer.getCountry())) {
      addProducerToDb(producer);
    } else {
      throw new AppException("Couldn't add new producer to db - producer's not unique by name, trade and country");
    }
  }

  private boolean isProducerUniqueByNameAndTradeAndCountry(String name, Trade trade, Country country) {
    if (trade == null) {
      throw new AppException("Trade is null");
    }

    if (country == null) {
      throw new AppException("Country is null");
    }

    return producerRepository.findByNameAndTradeAndCountry(name, trade, country).isEmpty();
      /*      || !producerRepository.findByNameAndTradeAndCountry(name, trade, country).get().getCountry().getName().equals(country.getName())
            || !producerRepository.findByNameAndTradeAndCountry(name, trade, country).get().getTrade().getName().equals(trade.getName());*/
  }

  public Optional<Producer> getProducerByNameAndTradeAndCountry(String name, Trade trade, Country country) {
    return producerRepository.findByNameAndTradeAndCountry(name, trade, country);
  }

//  public List<Product> getMostExpensiveProductsInEachCategory(){
//
//
//
//  }
}
