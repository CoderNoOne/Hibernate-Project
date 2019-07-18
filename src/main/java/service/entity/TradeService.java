package service.entity;

import dto.TradeDto;
import exception.AppException;
import mapper.ModelMapper;
import repository.abstract_repository.entity.TradeRepository;
import repository.impl.TradeRepositoryImpl;

import java.util.Optional;

public class TradeService {

  private final TradeRepository tradeRepository;

  public TradeService() {
    this.tradeRepository = new TradeRepositoryImpl();
  }

  public TradeService(TradeRepository tradeRepository) {
    this.tradeRepository = tradeRepository;
  }

  private Optional<TradeDto> addTradeToDb(TradeDto tradeDto) {
    return tradeRepository.addOrUpdate(ModelMapper.mapTradeDtoToTrade(tradeDto))
            .map(ModelMapper::mapTradeToTradeDto);
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
    return tradeRepository.findTradeByName(name)
            .map(ModelMapper::mapTradeToTradeDto);
  }

  public void deleteAllTrades() {
    tradeRepository.deleteAll();
  }

  TradeDto getTradeFromDbIfExists(TradeDto tradeDto) {
    return getTradeByName(tradeDto.getName())
            .orElse(tradeDto);
  }

  public void deleteTradeByName(String name) {

    if (name == null || name.equals("")) {
      throw new AppException("Trade name is null/ undefined: " + name);
    }
    tradeRepository.deleteTradeByName(name);
  }
}
