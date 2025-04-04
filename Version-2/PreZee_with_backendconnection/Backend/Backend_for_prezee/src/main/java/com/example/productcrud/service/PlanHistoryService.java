package com.example.productcrud.service;

import com.example.productcrud.model.PlanHistory;
import com.example.productcrud.repository.PlanHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanHistoryService {

    private final PlanHistoryRepository planHistoryRepository;

    @Autowired
    public PlanHistoryService(PlanHistoryRepository planHistoryRepository) {
        this.planHistoryRepository = planHistoryRepository;
    }

    public List<PlanHistory> findBySubscriberMobile(String mobile) {
        return planHistoryRepository.findBySubscriberMobile(mobile);
    }
}