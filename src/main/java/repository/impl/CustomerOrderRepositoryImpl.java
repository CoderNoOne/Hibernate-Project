package repository.impl;

import domain.Category;
import domain.CustomerOrder;
import domain.Product;
import exception.AppException;
import lombok.extern.slf4j.Slf4j;
import repository.abstract_repository.base.AbstractCrudRepository;
import repository.abstract_repository.entity.CustomerOrderRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CustomerOrderRepositoryImpl extends AbstractCrudRepository<CustomerOrder, Long> implements CustomerOrderRepository {

}
