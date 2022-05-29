package com.oas.osmsbackend.service;

import com.oas.osmsbackend.entity.Dealer;
import com.oas.osmsbackend.exception.ResourceNotFoundException;
import com.oas.osmsbackend.repository.DealerRepository;
import com.oas.osmsbackend.response.DataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;

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

    public DataResponse create(Dealer dealer) {
        return new DataResponse() {{
            put("dealer", dealerRepository.save(dealer));
        }};
    }

    public DataResponse list(Pageable pageable) {
        Page<Dealer> dealerPage = dealerRepository.findAll(pageable);
        return new DataResponse() {{
            put("dealers", dealerPage.getContent());
            put("total", dealerPage.getTotalElements());
        }};
    }

    public DataResponse read(Long dealerId) {
        return new DataResponse() {{
            put("dealer", DealerService.this.get(dealerId));
        }};
    }

    public DataResponse update(Long dealerId, Dealer dealer) {
        Dealer oldDealer = get(dealerId);
        if (dealer.getName() != null) {
            if (!oldDealer.getName().equals(dealer.getName())
                    && dealerRepository.findByName(dealer.getName()).isPresent()) {
                throw new EntityExistsException("经销商 '" + dealer.getName() + "' 已存在");
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
        return new DataResponse() {{
            put("dealer", dealerRepository.save(oldDealer));
        }};
    }

    public void delete(Long dealerId) {
        dealerRepository.delete(get(dealerId));
    }

    public Dealer get(Long dealerId) {
        return dealerRepository.findById(dealerId)
                .orElseThrow(() -> new ResourceNotFoundException("ID为 '" + dealerId + "' 的经销商不存在"));
    }
}
