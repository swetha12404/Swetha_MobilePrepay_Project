package com.example.productcrud.controller;

import com.example.productcrud.model.Plan;
import com.example.productcrud.model.Category;
import com.example.productcrud.repository.PlanRepository;
import com.example.productcrud.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<Plan>> getAllPlans() {
        return ResponseEntity.ok(planRepository.findAll());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Plan>> getPlansByCategory(@PathVariable Integer categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        return category.map(value -> ResponseEntity.ok(planRepository.findByCategory(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/category/name/{categoryName}") //admin page plan
    public ResponseEntity<List<Plan>> getPlansByCategoryName(@PathVariable String categoryName) {
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new RuntimeException("Category not found: " + categoryName));
        List<Plan> plans = planRepository.findByCategory(category);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plan> getPlanById(@PathVariable Integer id) {
        return planRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") //add by plan id
    public ResponseEntity<Plan> createPlan(@RequestBody Plan plan) {
        Optional<Category> category = categoryRepository.findById(plan.getCategory().getCategoryId());
        if (category.isPresent()) {
            plan.setCategory(category.get());
            return ResponseEntity.ok(planRepository.save(plan));
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}") //edit by plan id
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Plan> updatePlan(@PathVariable Integer id, @RequestBody Plan planDetails) {
        Optional<Plan> planOptional = planRepository.findById(id);
        if (planOptional.isPresent()) {
            Plan plan = planOptional.get();
            plan.setDetails(planDetails.getDetails());
            plan.setAmount(planDetails.getAmount());
            plan.setValidityDays(planDetails.getValidityDays());

            if (planDetails.getCategory() != null) {
                Optional<Category> category = categoryRepository.findById(planDetails.getCategory().getCategoryId());
                if (category.isPresent()) {
                    plan.setCategory(category.get());
                } else {
                    return ResponseEntity.badRequest().build();
                }
            }

            return ResponseEntity.ok(planRepository.save(plan));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}") //delete plan
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePlan(@PathVariable Integer id) {
        Optional<Plan> plan = planRepository.findById(id);
        if (plan.isPresent()) {
            planRepository.delete(plan.get());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/categories") //adding category
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return ResponseEntity.ok(categoryRepository.save(category));
    }
}