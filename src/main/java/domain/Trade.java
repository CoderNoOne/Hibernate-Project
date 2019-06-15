package domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Trade {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(unique = true)
  private String name;

  @OneToMany(mappedBy = "trade"/*, cascade = CascadeType.MERGE*/)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<Producer> producers;
}
