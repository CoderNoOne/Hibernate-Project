package domain;

import domain.enums.Epayment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "payment")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(unique = true)
  @Enumerated(EnumType.ORDINAL)
  private Epayment epayment;

  @OneToMany(mappedBy = "payment"/*, cascade = CascadeType.MERGE*/)
  private List<CustomerOrder> customerOrders;
}
