package domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Producer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "country_id")
  private Country country;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "trade_id")
  private Trade trade;

  @OneToMany(mappedBy = "producer")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<Product> productList;
}
