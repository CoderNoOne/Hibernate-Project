package service;

import dao.impl.CustomerDao;
import domain.Customer;

public class CustomerService {

  private CustomerDao customerDao = new CustomerDao();

  public boolean saveCustomer(Customer customer){
    customerDao.add(customer);
  }
}
