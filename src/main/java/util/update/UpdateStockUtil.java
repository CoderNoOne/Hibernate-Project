package util.update;

import dto.StockDto;

import static util.others.UserDataUtils.getInt;

public interface UpdateStockUtil {

  static StockDto getUpdatedStock(StockDto stockDto) {
    stockDto.setQuantity(getInt("Type stock quantity"));
    return stockDto;
  }
}
