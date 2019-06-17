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
public class Shop {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  private String name;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "country_id")
  private Country country;

  @OneToMany(mappedBy = "shop")
  private List<Stock> stocks;
}
