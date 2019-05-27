package domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Product {

  @Id
  private Long id;

  private String name;
  private BigDecimal price;

  private Long categoryId;

  private Long producerId;
}
