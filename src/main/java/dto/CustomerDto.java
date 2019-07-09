package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDto {

  private Long id;
  private String name;
  private String surname;
  private Integer age;
  private CountryDto countryDto;

}
