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
}
