package com.example.productcrud.service;

import com.example.productcrud.model.TransactionHistory;
import com.example.productcrud.repository.TransactionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionHistoryService {

    private final TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    public TransactionHistoryService(TransactionHistoryRepository transactionHistoryRepository) {
        this.transactionHistoryRepository = transactionHistoryRepository;
    }

    public List<TransactionHistory> findBySubscriberMobile(String mobile) {
        return transactionHistoryRepository.findBySubscriberMobile(mobile);
    }
}