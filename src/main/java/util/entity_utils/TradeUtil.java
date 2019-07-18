package util.entity_utils;

import domain.Trade;
import dto.TradeDto;
import exception.AppException;
import validator.impl.TradeDtoValidator;

import java.util.stream.Collectors;

import static util.others.UserDataUtils.getString;
import static util.others.UserDataUtils.printMessage;

public interface TradeUtil {

  static TradeDto createTradeDtoFromUserInput() {

    return TradeDto.builder()
            .name(getString("Input trade name"))
            .build();
  }

  static TradeDto getTradeDtoIfValid(TradeDto tradeDto) {

    var tradeDtoValidator = new TradeDtoValidator();
    var errorsMap = tradeDtoValidator.validate(tradeDto);

    if (tradeDtoValidator.hasErrors()) {
      printMessage(errorsMap.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n")));
      throw new AppException("TradeDto is not valid: " + tradeDto.getName());
    }
    return tradeDto;
  }

  static TradeDto specifyTradeDtoDetailToDelete() {
    printMessage("\nInput trade's information you want to delete\n");

    return createTradeDtoFromUserInput();
  }
}
