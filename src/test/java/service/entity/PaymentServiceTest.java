package service.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import repository.abstract_repository.entity.PaymentRepository;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@Tag("Services")
@DisplayName("Test cases for PaymentService")
class PaymentServiceTest {

  @Mock
  private PaymentRepository paymentRepository;

  @InjectMocks
  private PaymentService paymentService;


}
