package domain;

import domain.enums.Epayment;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private Epayment epayment;

  @OneToMany(mappedBy = "payment")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<CustomerOrder> customerOrders;
}
