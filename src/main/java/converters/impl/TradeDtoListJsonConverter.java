package converters.impl;

import converters.JsonConverter;
import dto.TradeDto;

import java.util.List;

public class TradeDtoListJsonConverter extends JsonConverter<List<TradeDto>> {

  public TradeDtoListJsonConverter(String jsonFilename) {
    super(jsonFilename);
  }
}
