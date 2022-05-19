package com.oas.osmsbackend.service;

import com.oas.osmsbackend.domain.Customer;
import com.oas.osmsbackend.exception.ResourceNotFoundException;
import com.oas.osmsbackend.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityExistsException;
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
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with ID '" + customerId + "' not found."));
    }

    public Customer update(Long customerId, Customer customer) {
        Customer oldCustomer = get(customerId);
        if (!StringUtils.hasText(customer.getPhone())) {
            customer.setPhone(oldCustomer.getPhone());
        } else if (!oldCustomer.getPhone().equals(customer.getPhone())
                && customerRepository.findByPhone(customer.getPhone()).isPresent()) {
            throw new EntityExistsException("Customer with phone number '" + customer.getPhone() + "' already exists.");
        }
        if (!StringUtils.hasText(customer.getName())) {
            customer.setName(oldCustomer.getName());
        }
        if (customer.getGender() == null) {
            customer.setGender(oldCustomer.getGender());
        }
        if (customer.getEmail() == null) {
            customer.setEmail(oldCustomer.getEmail());
        }
        if (customer.getBirthDate() == null) {
            customer.setBirthDate(oldCustomer.getBirthDate());
        }
        if (customer.getAddresses() == null) {
            customer.setAddresses(oldCustomer.getAddresses());
        }
        customer.setOrders(oldCustomer.getOrders());
        return customerRepository.save(customer);
    }

    public void delete(Long customerId) {
        customerRepository.deleteById(customerId);
    }
}
