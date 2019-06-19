package domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  private String name;
  private String surname;
  private Integer age;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "country_id")
  private Country country;

  @OneToMany(mappedBy = "customer")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<CustomerOrder> customerOrders;
}
