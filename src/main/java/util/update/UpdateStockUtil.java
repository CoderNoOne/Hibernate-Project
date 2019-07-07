package util.update;

import domain.Stock;

import static util.others.UserDataUtils.getInt;

public interface UpdateStockUtil {

  static Stock getUpdatedStock(Stock stock) {
    stock.setQuantity(getInt("Type stock quantity"));
    return stock;
  }
}
