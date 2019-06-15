package repository.impl;

import domain.CustomerOrder;
import repository.abstract_repository.base.AbstractCrudRepository;
import repository.abstract_repository.entity.CustomerOrderRepository;

public class CustomerOrderImpl extends AbstractCrudRepository <CustomerOrder, Long> implements CustomerOrderRepository {
}
