package domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Error {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;
  private LocalDateTime date;
  private String message;
}
