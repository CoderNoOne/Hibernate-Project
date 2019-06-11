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

  public void setCountryInfo(Country country){
    this.id = country.id;
    this.name = country.name;
  }

  @OneToMany(mappedBy = "country")
  private Set<Shop> shop;

  @OneToMany(mappedBy = "country")
  private List<Customer> customers;

  @OneToMany(mappedBy = "country")
  private List<Producer> producers;
}
