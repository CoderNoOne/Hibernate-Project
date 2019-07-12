package mappers;

import domain.Country;
import domain.Producer;
import domain.Trade;
import dto.CountryDto;
import dto.ProducerDto;
import dto.TradeDto;

public class ProducerMapper {

  public Producer mapProducerDtoToProducer(ProducerDto producerDto){

    return Producer.builder()
            .id(producerDto.getId())
            .name(producerDto.getName())
            .trade(Trade.builder()
                    .id(producerDto.getTrade().getId())
                    .name(producerDto.getTrade().getName())
                    .build())
            .country(Country.builder()
                    .id(producerDto.getCountry().getId())
                    .name(producerDto.getCountry().getName())
                    .build())
            .build();
  }

  public ProducerDto mapProducerToProducerDto (Producer producer){

    return ProducerDto.builder()
            .id(producer.getId())
            .name(producer.getName())
            .trade(TradeDto.builder()
                    .id(producer.getTrade().getId())
                    .name(producer.getTrade().getName())
                    .build())
            .country(CountryDto.builder()
                    .id(producer.getCountry().getId())
                    .name(producer.getCountry().getName())
                    .build())
            .build();
  }
}
