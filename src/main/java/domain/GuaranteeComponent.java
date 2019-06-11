package domain;

import domain.enums.EGuarantee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "guarantee_components")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GuaranteeComponent  {

  @Id
  @Column(name = "product_id")
  private Long id;

  @MapsId
  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "product_id", unique = true)
  private Product product;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "guarantee_component")
  private EGuarantee guaranteeComponent;
}
