package service;

import domain.Trade;
import exception.AppException;
import repository.abstract_repository.entity.TradeRepository;
import repository.impl.TradeRepositoryImpl;

import java.util.Optional;

public class TradeService {

  private final TradeRepository tradeRepository;

  public TradeService() {
    this.tradeRepository = new TradeRepositoryImpl();
  }

  public Optional<Trade> addTradeToDb(Trade trade) {
    return tradeRepository.addOrUpdate(trade);
  }

  public void addTradeToDbFromUserInput(Trade trade) {
    if (isTradeUniqueByName(trade.getName())) {
      addTradeToDb(trade);
    } else {
      throw new AppException("Couldn't add a trade to db - trade's not unique by name");
    }
  }

  private boolean isTradeUniqueByName(String name) {
    return getTradeByName(name).isEmpty();
  }

  public Optional<Trade> getTradeByName(String name) {
    return tradeRepository
            .findByName(name);
  }
}
