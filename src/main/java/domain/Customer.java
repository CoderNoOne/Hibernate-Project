package domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String surname;
  private Integer age;

  @ManyToOne(cascade = {CascadeType.MERGE})
  @JoinColumn(name = "country_id")
  private Country country;

  @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<CustomerOrder> customerOrders;
}
