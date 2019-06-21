package domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String name;

  @OneToMany(mappedBy = "category")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<Product> products;
}
