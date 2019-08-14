package domain;

import domain.enums.EGuarantee;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private BigDecimal price;

  @ManyToOne(/*cascade = CascadeType.MERGE*/)
  @JoinColumn(name = "category_id")
  private Category category;

  @ManyToOne(/*cascade = CascadeType.MERGE*/)
  @JoinColumn(name = "producer_id")
  private Producer producer;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "guarantee_component", joinColumns = @JoinColumn(name = "product_id"))
  @Column(name = "guarantee_component")
  @Enumerated(EnumType.ORDINAL)
  private List<EGuarantee> guaranteeComponents = new ArrayList<>();

  @OneToMany(mappedBy = "product")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<CustomerOrder> customerOrders;

  @OneToMany(mappedBy = "product")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<Stock> stocks;
}
