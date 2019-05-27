package domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Stock {


  @Id
  private Long id;

  private String name;
  private Long countryId;

}
