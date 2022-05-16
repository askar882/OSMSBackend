package com.oas.osmsbackend.service;

import com.oas.osmsbackend.domain.Customer;
import com.oas.osmsbackend.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 客户服务。
 *
 * @author askar882
 * @date 2022/05/16
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Customer create(Customer customer) {
        return customerRepository.save(customer);
    }

    public List<Customer> list() {
        return customerRepository.findAll();
    }

    public Customer get(Long customerId) {
        return customerRepository.getById(customerId);
    }

    public Customer update(Long customerId, Customer customer) {
        return customer;
    }

    public void delete(Long customerId) {
        customerRepository.deleteById(customerId);
    }
}
