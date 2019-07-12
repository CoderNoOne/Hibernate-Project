package domain;

import domain.enums.Epayment;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.ORDINAL)
  private Epayment epayment;

  @OneToMany(mappedBy = "payment")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<CustomerOrder> customerOrders;
}
