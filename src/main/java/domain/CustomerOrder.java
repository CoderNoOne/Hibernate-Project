package domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CustomerOrder {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  private LocalDate date;

  @Column(scale = 2, precision = 19)
  private BigDecimal discount;

  private Integer quantity;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "payment_id")
  private Payment payment;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "product_id")
  private Product product;

}
