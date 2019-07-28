package service.entity;

import domain.Country;
import dto.CountryDto;
import dto.ProducerDto;
import dto.TradeDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import repository.abstract_repository.entity.CountryRepository;
import repository.abstract_repository.entity.ProducerRepository;
import repository.abstract_repository.entity.TradeRepository;

import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@Tag("Services")
@DisplayName("Test cases for ProducerService")
class ProducerServiceTest {

  @Mock
  private ProducerRepository producerRepository;

  @Mock
  private TradeRepository tradeRepository;

  @Mock
  private CountryRepository countryRepository;

  @InjectMocks
  private ProductService productService;

  @Test
  @DisplayName("getAllProducers")
  void test1() {

    //given

    List<ProducerDto> expectedResult = List.of(

            ProducerDto.builder()
                    .id(1L)
                    .name("PRODUCER ONE")
                    .country(CountryDto.builder()
                            .id(1L)
                            .name("COUNTRY ONE")
                            .build())
                    .trade(TradeDto.builder()
                            .id(1L)
                            .name("TRADE")
                            .build())
                    .build(),

            ProducerDto.builder()



                    .build()
    );

//    given(producerRepository.findAll()).willReturn()

    //when
    //then

  }

}
