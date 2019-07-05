package converters.impl;

import converters.JsonConverter;
import domain.Trade;

import java.util.List;

public class TradeListJsonConverter extends JsonConverter<List<Trade>> {

  public TradeListJsonConverter(String jsonFilename) {
    super(jsonFilename);
  }
}
