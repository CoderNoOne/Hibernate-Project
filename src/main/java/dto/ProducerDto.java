package dto;

import lombok.*;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ProducerDto {

  private Long id;
  private String name;
  private CountryDto country;
  private TradeDto trade;

}
