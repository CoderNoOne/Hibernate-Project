package service.entity;

import domain.enums.Epayment;
import dto.PaymentDto;
import exception.AppException;
import mapper.ModelMapper;
import repository.abstract_repository.entity.PaymentRepository;
import repository.impl.PaymentRepositoryImpl;

import java.util.Optional;

public class PaymentService {

  private final PaymentRepository paymentRepository;


  public PaymentService() {
    this.paymentRepository = new PaymentRepositoryImpl();
  }

  public PaymentService(PaymentRepository paymentRepository) {
    this.paymentRepository = paymentRepository;

  }

  private Optional<PaymentDto> addPaymentToDb(PaymentDto paymentDto) {
    return paymentRepository
            .addOrUpdate(ModelMapper.mapPaymentDtoToPayment(paymentDto))
            .map(ModelMapper::mapPaymentToPaymentDto);
  }

  public void addPaymentToDbFromUserInput(PaymentDto paymentDto) {
    if (!isPaymentUnique(paymentDto)) {
      addPaymentToDb(paymentDto);
    } else {
      throw new AppException("Couldn't add new payment - payment's not unique");
    }
  }

  public Optional<PaymentDto> getPaymentByEpayment(Epayment epayment) {
    return paymentRepository.findPaymentByEPayment(epayment)
            .map(ModelMapper::mapPaymentToPaymentDto);
  }


  public PaymentDto getPaymentFromDbIfExists(PaymentDto paymentDto) {
    return getPaymentByEpayment(paymentDto.getEpayment())
            .orElse(paymentDto);
  }

  private boolean isPaymentUnique(PaymentDto paymentDto) {
    return getPaymentByEpayment(paymentDto.getEpayment()).isEmpty();
  }


}
