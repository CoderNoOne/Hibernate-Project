package domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(unique = true)
  private String name;

  @OneToMany(mappedBy = "category")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<Product> products;
}
