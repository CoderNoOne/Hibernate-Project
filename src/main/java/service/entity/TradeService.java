package service.entity;

import dto.TradeDto;
import exception.AppException;
import mappers.TradeMapper;
import repository.abstract_repository.entity.TradeRepository;
import repository.impl.TradeRepositoryImpl;

import java.util.Optional;

public class TradeService {

  private final TradeRepository tradeRepository;
  private final TradeMapper tradeMapper;

  public TradeService() {
    this.tradeRepository = new TradeRepositoryImpl();
    this.tradeMapper = new TradeMapper();
  }

  private Optional<TradeDto> addTradeToDb(TradeDto tradeDto) {
    return tradeRepository.addOrUpdate(tradeMapper.mapTradeDtoToTrade(tradeDto))
            .map(tradeMapper::mapTradeToTradeDto);
  }

  public void addTradeToDbFromUserInput(TradeDto tradeDto) {
    if (isTradeUniqueByName(tradeDto.getName())) {
      addTradeToDb(tradeDto);
    } else {
      throw new AppException("Couldn't add a trade to db - trade's not unique by name");
    }
  }

  private boolean isTradeUniqueByName(String name) {
    return getTradeByName(name).isEmpty();
  }

  private Optional<TradeDto> getTradeByName(String name) {
    return tradeRepository.findByName(name)
            .map(tradeMapper::mapTradeToTradeDto);
  }

  public void deleteAllTrades() {
    tradeRepository.deleteAll();
  }

  TradeDto getTradeFromDbIfExists(TradeDto tradeDto) {
    return getTradeByName(tradeDto.getName())
            .orElse(tradeDto);
  }
}
