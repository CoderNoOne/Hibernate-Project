package service.entity;

import domain.enums.Epayment;
import dto.PaymentDto;
import exception.AppException;
import mappers.PaymentMapper;
import repository.abstract_repository.entity.PaymentRepository;
import repository.impl.PaymentRepositoryImpl;

import java.util.Optional;

public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final PaymentMapper paymentMapper;

  public PaymentService() {
    this.paymentRepository = new PaymentRepositoryImpl();
    this.paymentMapper = new PaymentMapper();
  }

  private Optional<PaymentDto> addPaymentToDb(PaymentDto paymentDto) {
    return paymentRepository
            .addOrUpdate(paymentMapper.mapPaymentDtoToPayment(paymentDto))
            .map(paymentMapper::mapPaymentToPaymentDto);
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
            .map(paymentMapper::mapPaymentToPaymentDto);
  }


  public PaymentDto getPaymentFromDbIfExists(PaymentDto paymentDto) {
    return getPaymentByEpayment(paymentDto.getEpayment())
            .orElse(paymentDto);
  }

  private boolean isPaymentUnique(PaymentDto paymentDto) {
    return getPaymentByEpayment(paymentDto.getEpayment()).isEmpty();
  }


}
