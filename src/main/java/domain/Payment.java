package domain;

import domain.enums.Epayment;

import javax.persistence.*;

@Entity
public class Payment {


  @Id
  private Long id;

  @Column(unique = true)
  @Enumerated(EnumType.ORDINAL)
  private Epayment payment;
}
