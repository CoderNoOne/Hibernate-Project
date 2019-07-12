package mappers;

import domain.Trade;
import dto.TradeDto;

public class TradeMapper {

  public Trade mapTradeDtoToTrade(TradeDto tradeDto) {

    return Trade.builder()
            .id(tradeDto.getId())
            .name(tradeDto.getName())
            .build();
  }

  public TradeDto mapTradeToTradeDto (Trade trade){

    return TradeDto.builder()
            .id(trade.getId())
            .name(trade.getName())
            .build();
  }
}
