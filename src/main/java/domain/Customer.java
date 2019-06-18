package domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  private Integer age;
  private String name;
  private String surname;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "country_id")
  private Country country;

  @OneToMany(mappedBy = "customer")
  private List<CustomerOrder> customerOrders;
}
