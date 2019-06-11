package domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  private String name;
  private BigDecimal price;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "category_id")
  private Category category;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "producer_id")
  private Producer producer;

  @OneToMany(mappedBy = "product")
  private List<GuaranteeComponent> guaranteeComponent;

  @OneToMany(mappedBy = "product")
  private List<CustomerOrder> customerOrders;

  @OneToMany(mappedBy = "product")
  private List<Stock> stocks;
}
