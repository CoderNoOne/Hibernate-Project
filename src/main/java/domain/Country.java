package domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Country {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column
  private String name;

  @OneToMany(mappedBy = "country"/*, cascade = CascadeType.MERGE*/)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Set<Shop> shop;

  @OneToMany(mappedBy = "country"/*, cascade = CascadeType.MERGE*/)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<Customer> customers;

  @OneToMany(mappedBy = "country"/*, cascade = CascadeType.MERGE*/)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<Producer> producers;
}
