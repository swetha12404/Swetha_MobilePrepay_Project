package com.example.productcrud.repository;

import com.example.productcrud.model.Plan;
import com.example.productcrud.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Integer> {
    List<Plan> findByCategory(Category category);
}