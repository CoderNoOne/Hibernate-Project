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
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String name;

  @OneToMany(mappedBy = "country")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Set<Shop> shop;

  @OneToMany(mappedBy = "country")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<Customer> customers;

  @OneToMany(mappedBy = "country")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<Producer> producers;
}
