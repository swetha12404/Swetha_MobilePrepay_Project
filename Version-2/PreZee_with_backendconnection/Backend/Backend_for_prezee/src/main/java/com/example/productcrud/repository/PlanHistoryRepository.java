package com.example.productcrud.repository;

import com.example.productcrud.model.PlanHistory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
public interface PlanHistoryRepository extends JpaRepository<PlanHistory, Long> {
    List<PlanHistory> findBySubscriberMobile(String mobile);
}