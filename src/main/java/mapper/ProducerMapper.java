package mapper;

import domain.Producer;
import dto.ProducerDto;
import org.mapstruct.Mapper;

@Mapper
public interface ProducerMapper {

  ProducerDto producerToProducerDto (Producer producer);
  Producer prodcerDtoToProducer (ProducerDto producerDto);
}
