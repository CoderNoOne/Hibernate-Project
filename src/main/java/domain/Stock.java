package domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Stock {


  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  private String name;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "shop_id")
  private Shop shop;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "product_id")
  private Product product;


}
