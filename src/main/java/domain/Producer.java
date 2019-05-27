package domain;

import javax.persistence.Id;

public class Producer {

  @Id
  private Long id;

  private String name;
  private Long countryId;
  private Long tradeId;
}
