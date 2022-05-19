package com.oas.osmsbackend.service;

import com.oas.osmsbackend.entity.Dealer;
import com.oas.osmsbackend.exception.ResourceNotFoundException;
import com.oas.osmsbackend.repository.DealerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.util.List;

/**
 * 经销商服务。
 * 
 * @author askar882
 * @date 2022/05/19
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DealerService {
    private final DealerRepository dealerRepository;

    public Dealer create(Dealer dealer) {
        return dealerRepository.save(dealer);
    }

    public List<Dealer> list() {
        return dealerRepository.findAll();
    }

    public Dealer read(Long dealerId) {
        return dealerRepository.findById(dealerId)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer with ID '" + dealerId + "' not found."));
    }

    public Dealer update(Long dealerId, Dealer dealer) {
        Dealer oldDealer = read(dealerId);
        if (dealer.getName() != null) {
            if (!oldDealer.getName().equals(dealer.getName())
                    && dealerRepository.findByName(dealer.getName()).isPresent()) {
                throw new EntityExistsException("Dealer with name '" + dealer.getName() + "' already exists.");
            }
            oldDealer.setName(dealer.getName());
        }
        if (dealer.getContact() != null) {
            oldDealer.setContact(dealer.getContact());
        }
        if (dealer.getPhone() != null) {
            oldDealer.setPhone(dealer.getPhone());
        }
        if (dealer.getAddress() != null) {
            oldDealer.setAddress(dealer.getAddress());
        }
        return dealerRepository.save(oldDealer);
    }

    public void delete(Long dealerId) {
        dealerRepository.delete(read(dealerId));
    }
}
