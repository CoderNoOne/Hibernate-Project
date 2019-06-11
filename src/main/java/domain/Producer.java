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
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  private String name;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "country_id")
  private Country country;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "trade_id")
  private Trade trade;
}
