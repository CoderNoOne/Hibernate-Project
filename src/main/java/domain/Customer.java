package domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Customer {

  @Id
  private Long id;

  private Integer age;
  private String name;
  private String surname;
  private Long countryId;

  private Customer() {
  }
}
