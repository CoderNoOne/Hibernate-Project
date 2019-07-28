package domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Stock {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer quantity;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "shop_id")
  private Shop shop;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "product_id")
  private Product product;

}
