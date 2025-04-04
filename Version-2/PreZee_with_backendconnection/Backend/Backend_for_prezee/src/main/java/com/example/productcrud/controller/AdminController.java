package com.example.productcrud.controller;

import com.example.productcrud.model.Admin;
import com.example.productcrud.model.Plan;
import com.example.productcrud.model.Subscriber;
import com.example.productcrud.model.TransactionHistory;
import com.example.productcrud.model.PlanHistory;
import com.example.productcrud.repository.PlanRepository;
import com.example.productcrud.repository.SubscriberRepository;
import com.example.productcrud.repository.TransactionHistoryRepository;
import com.example.productcrud.repository.PlanHistoryRepository;
import com.example.productcrud.service.AdminService;
import com.example.productcrud.service.PlanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admins")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;
    private final PlanService planService;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private PlanHistoryRepository planHistoryRepository;

    public AdminController(AdminService adminService, PlanService planService) {
        this.adminService = adminService;
        this.planService = planService;
    }

    @PostMapping("/register")
    public ResponseEntity<Admin> registerAdmin(@RequestBody Admin admin) {
        return ResponseEntity.ok(adminService.saveAdmin(admin));
    }

    @GetMapping("/plans")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Plan>> getAllPlans() {
        return ResponseEntity.ok(planService.getAllPlans());
    }

    @PostMapping("/plans")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Plan> createPlan(@RequestBody Plan plan) {
        return ResponseEntity.ok(planService.savePlan(plan));
    }

    @GetMapping("/dashboard-summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        List<Plan> plans = planService.getAllPlans();
        List<Subscriber> subscribers = subscriberRepository.findAll();
        List<TransactionHistory> transactions = transactionHistoryRepository.findAll();

        long totalPlans = plans.size();
        long totalSubscribers = subscribers.size();
        double totalRevenue = transactions.stream()
                .mapToDouble(TransactionHistory::getAmountPaid)
                .sum();

        long activeSubscribers = subscribers.stream()
                .filter(s -> s.getStatus() == Subscriber.Status.ACTIVE)
                .count();
        long inactiveSubscribers = totalSubscribers - activeSubscribers;

        Map<String, Object> summary = Map.of(
                "totalPlans", totalPlans,
                "totalSubscribers", totalSubscribers,
                "totalRevenue", totalRevenue,
                "activeSubscribers", activeSubscribers,
                "inactiveSubscribers", inactiveSubscribers
        );

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/expiring-subscribers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Subscriber>> getExpiringSubscribers(@RequestParam int days) {
        LocalDate today = LocalDate.now();
        LocalDate expiryThreshold = today.plusDays(days);

        List<PlanHistory> expiringPlans = planHistoryRepository.findAll().stream()
                .filter(ph -> ph.getValidity_end() != null)
                .filter(ph -> !ph.getValidity_end().toLocalDate().isBefore(today) && //data before 
                              !ph.getValidity_end().toLocalDate().isAfter(expiryThreshold)) //data after
                .collect(Collectors.toList());

        List<Subscriber> expiringSubscribers = expiringPlans.stream()
                .map(PlanHistory::getSubscriber)
                .distinct()
                .collect(Collectors.toList());

        return ResponseEntity.ok(expiringSubscribers);
    }

    @GetMapping("/subscriber-details/{mobile}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getSubscriberDetails(@PathVariable String mobile) {
        Subscriber subscriber = subscriberRepository.findByMobile(mobile);
        if (subscriber == null) {
            return ResponseEntity.notFound().build();
        }

        List<TransactionHistory> rechargeHistory = transactionHistoryRepository.findAll().stream()
                .filter(t -> t.getSubscriber().getMobile().equals(mobile))
                .collect(Collectors.toList());

        Map<String, Object> details = Map.of(
                "subscriber", subscriber,
                "rechargeHistory", rechargeHistory
        );

        return ResponseEntity.ok(details);
    }

    @GetMapping("/monthly-growth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getMonthlyGrowth() {
        Map<String, Long> monthlyGrowth = transactionHistoryRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionDate().toLocalDate().getYear() + "-" + 
                             String.format("%02d", t.getTransactionDate().toLocalDate().getMonthValue()),
                        Collectors.counting()
                ));

        return ResponseEntity.ok(monthlyGrowth);
    }
}