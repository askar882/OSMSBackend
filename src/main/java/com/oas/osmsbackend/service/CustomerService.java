package com.oas.osmsbackend.service;

import com.oas.osmsbackend.entity.Customer;
import com.oas.osmsbackend.exception.ResourceNotFoundException;
import com.oas.osmsbackend.repository.CustomerRepository;
import com.oas.osmsbackend.response.DataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;

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

    public DataResponse create(Customer customer) {
        return new DataResponse() {{
            put("customer", customerRepository.save(customer));
        }};
    }

    public DataResponse list(Pageable pageable) {
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        return new DataResponse() {{
            put("customers", customerPage.getContent());
            put("total", customerPage.getTotalElements());
        }};
    }

    public DataResponse read(Long customerId) {
        Customer customer = get(customerId);
        return new DataResponse() {{
            put("customer", customer);
        }};
    }

    public DataResponse update(Long customerId, Customer customer) {
        Customer oldCustomer = get(customerId);
        if (customer.getPhone() != null) {
            if (!oldCustomer.getPhone().equals(customer.getPhone())
                    && customerRepository.findByPhone(customer.getPhone()).isPresent()) {
                throw new EntityExistsException("客户电话号码 '" + customer.getPhone() + "' 已存在");
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
        return new DataResponse() {{
            put("customer", customerRepository.save(oldCustomer));
        }};
    }

    public void delete(Long customerId) {
        customerRepository.delete(get(customerId));
    }

    public Customer get(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("ID为 '" + customerId + "' 的客户不存在"));
    }
}
