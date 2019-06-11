package domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;


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

  @OneToMany(mappedBy = "product")
  private List<Product> products;
}
