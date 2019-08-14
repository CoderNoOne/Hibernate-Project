package service.entity;

import dto.TradeDto;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import mapper.ModelMapper;
import repository.abstract_repository.entity.TradeRepository;
import repository.impl.TradeRepositoryImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TradeService {

  private final TradeRepository tradeRepository;

  public TradeService() {
    this.tradeRepository = new TradeRepositoryImpl();
  }

  private Optional<TradeDto> addTradeToDb(TradeDto tradeDto) {

    if (tradeDto == null) {
      throw new AppException("TradeDto object is null");
    }
    return tradeRepository.add(ModelMapper.mapTradeDtoToTrade(tradeDto))
            .map(ModelMapper::mapTradeToTradeDto);
  }

  public void addTradeToDbFromUserInput(TradeDto tradeDto) {

    if (tradeDto == null) {
      throw new AppException("TradeDto object is null");
    }

    if (isTradeUniqueByName(tradeDto.getName())) {
      addTradeToDb(tradeDto);
    } else {
      throw new AppException("Couldn't add a trade to db - trade's not unique by name: " + tradeDto.getName());
    }
  }

  private boolean isTradeUniqueByName(String name) {

    if (name == null || name.equals("")) {
      throw new AppException(String.format("Trade name is undefined/null: %s", name));
    }
    return getTradeByName(name).isEmpty();
  }

  private Optional<TradeDto> getTradeByName(String name) {

    if (name == null || name.equals("")) {
      throw new AppException(String.format("Trade name is undefined/null: %s", name));
    }
    return tradeRepository.findTradeByName(name)
            .map(ModelMapper::mapTradeToTradeDto);
  }

  public void deleteAllTrades() {
    tradeRepository.deleteAll();
  }

  TradeDto getTradeFromDbIfExists(TradeDto tradeDto) {

    if (tradeDto == null) {
      throw new AppException("TradeDto object is null");
    }
    return getTradeByName(tradeDto.getName())
            .orElse(tradeDto);
  }

  public void deleteTradeByName(String name) {

    if (name == null || name.equals("")) {
      throw new AppException("Trade name is null/ undefined: " + name);
    }
    tradeRepository.deleteTradeByName(name);
  }

  public List<TradeDto> getAllTrades() {

    return tradeRepository
            .findAll()
            .stream()
            .map(ModelMapper::mapTradeToTradeDto)
            .collect(Collectors.toList());
  }
}
