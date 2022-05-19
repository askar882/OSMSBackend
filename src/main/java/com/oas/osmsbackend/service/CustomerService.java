package com.oas.osmsbackend.service;

import com.oas.osmsbackend.entity.Customer;
import com.oas.osmsbackend.exception.ResourceNotFoundException;
import com.oas.osmsbackend.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public Customer read(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with ID '" + customerId + "' not found."));
    }

    public Customer update(Long customerId, Customer customer) {
        Customer oldCustomer = read(customerId);
        if (customer.getPhone() != null) {
            if (!oldCustomer.getPhone().equals(customer.getPhone())
                    && customerRepository.findByPhone(customer.getPhone()).isPresent()) {
                throw new EntityExistsException("Customer with phone number '" + customer.getPhone() + "' already exists.");
            }
            oldCustomer.setPhone(customer.getPhone());
        }
        if (customer.getName() != null) {
            oldCustomer.setName(customer.getName());
        }
        if (customer.getGender() != null) {
            oldCustomer.setGender(customer.getGender());
        }
        if (customer.getEmail() != null) {
            oldCustomer.setEmail(customer.getEmail());
        }
        if (customer.getBirthDate() != null) {
            oldCustomer.setBirthDate(customer.getBirthDate());
        }
        if (customer.getAddresses() != null) {
            oldCustomer.setAddresses(customer.getAddresses());
        }
        if (customer.getOrders() != null) {
            oldCustomer.setOrders(customer.getOrders());
        }
        return customerRepository.save(oldCustomer);
    }

    public void delete(Long customerId) {
        customerRepository.delete(read(customerId));
    }
}
