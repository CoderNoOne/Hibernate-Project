package domain;

import domain.enums.EGuarantee;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
public class GuaranteeComponent {

  @Id
  private Long productId;

  @Enumerated(EnumType.STRING)
  private EGuarantee guaranteeComponent;
}
