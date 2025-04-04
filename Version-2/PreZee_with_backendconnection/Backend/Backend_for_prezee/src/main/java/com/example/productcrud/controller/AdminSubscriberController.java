package com.example.productcrud.controller;

import com.example.productcrud.model.Subscriber;
import com.example.productcrud.model.PlanHistory;
import com.example.productcrud.service.EmailService;
import com.example.productcrud.service.SubscriberService;
import com.example.productcrud.repository.PlanHistoryRepository;
import com.example.productcrud.repository.PlanRepository;
import com.example.productcrud.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admins/subscribers")
@PreAuthorize("hasRole('ADMIN')")
class AdminSubscriberController {

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PlanHistoryRepository planHistoryRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @GetMapping
    public ResponseEntity<List<Subscriber>> getAllSubscribers() {
        return ResponseEntity.ok(subscriberService.getAllSubscribers());
    }

    @GetMapping("/{status}")
    public ResponseEntity<List<Subscriber>> getSubscribersByStatus(@PathVariable String status) {
        Subscriber.Status subscriberStatus = Subscriber.Status.valueOf(status.toUpperCase());
        List<Subscriber> subscribers = subscriberService.getAllSubscribers().stream()
                .filter(s -> s.getStatus() == subscriberStatus)
                .collect(Collectors.toList());
        return ResponseEntity.ok(subscribers);
    }

    @GetMapping("/expiring-subscribers")
    public ResponseEntity<List<Map<String, Object>>> getExpiringSubscribers(@RequestParam(defaultValue = "3") int days) {
        LocalDate today = LocalDate.now();
        LocalDate expiryThreshold = today.plusDays(days);

        List<PlanHistory> expiringPlans = planHistoryRepository.findAll().stream()
                .filter(ph -> ph.getValidity_end() != null)
                .filter(ph -> !ph.getValidity_end().toLocalDate().isBefore(today) &&
                              !ph.getValidity_end().toLocalDate().isAfter(expiryThreshold))
                .collect(Collectors.toList());

        List<Map<String, Object>> expiringSubscribers = expiringPlans.stream()
                .map(ph -> {
                    Map<String, Object> subscriberData = new HashMap<>();
                    Subscriber subscriber = ph.getSubscriber();
                    subscriberData.put("name", subscriber.getName());
                    subscriberData.put("email", subscriber.getEmail());
                    subscriberData.put("mobile", subscriber.getMobile());
                    subscriberData.put("planDetails", ph.getPlan().getDetails() + " (₹" + ph.getAmount() + ")");
                    subscriberData.put("expiryDate", ph.getValidity_end());
                    return subscriberData;
                })
                .distinct()
                .collect(Collectors.toList());

        return ResponseEntity.ok(expiringSubscribers);
    }

    @PostMapping("/notify")
    public ResponseEntity<Map<String, String>> notifySubscriber(@RequestBody NotificationRequest request) {
        Subscriber subscriber = subscriberService.findByMobile(request.getMobile());
        if (subscriber == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Subscriber not found"));
        }
        emailService.sendNotificationEmail(
                subscriber.getEmail(),
                subscriber.getName(),
                "Dear customer, your plan is about to expire."
        );
        return ResponseEntity.ok(Map.of("message", "Notification sent to " + subscriber.getEmail()));
    }

    @PostMapping("/notify-all-expiring")
    public ResponseEntity<Map<String, String>> notifyAllExpiring(@RequestBody NotificationRequest request) {
        LocalDate today = LocalDate.now();
        LocalDate expiryThreshold = today.plusDays(3);

        List<PlanHistory> expiringPlans = planHistoryRepository.findAll().stream()
                .filter(ph -> ph.getValidity_end() != null)
                .filter(ph -> !ph.getValidity_end().toLocalDate().isBefore(today) &&
                              !ph.getValidity_end().toLocalDate().isAfter(expiryThreshold))
                .collect(Collectors.toList());

        List<Subscriber> expiringSubscribers = expiringPlans.stream()
                .map(PlanHistory::getSubscriber)
                .distinct()
                .collect(Collectors.toList());

        expiringSubscribers.forEach(subscriber -> emailService.sendNotificationEmail(
                subscriber.getEmail(),
                subscriber.getName(),
                "Dear customer, your plan is about to expire."
        ));

        return ResponseEntity.ok(Map.of("message", "Notifications sent to " + expiringSubscribers.size() + " subscribers"));
    }

    @GetMapping("/plan-history")
    public ResponseEntity<List<PlanHistory>> getAllPlanHistory() {
        List<PlanHistory> planHistory = planHistoryRepository.findAll();
        return ResponseEntity.ok(planHistory);
    }

    @GetMapping("/dashboard-summary")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalPlans", planRepository.count());
        summary.put("totalSubscribers", subscriberRepository.count());
        summary.put("totalRevenue", planHistoryRepository.findAll().stream()
            .mapToDouble(PlanHistory::getAmount).sum());
        summary.put("activeSubscribers", subscriberRepository.findAll().stream()
            .filter(s -> s.getStatus() == Subscriber.Status.ACTIVE).count());
        summary.put("inactiveSubscribers", subscriberRepository.findAll().stream()
            .filter(s -> s.getStatus() == Subscriber.Status.INACTIVE).count());
        return ResponseEntity.ok(summary);
    }
}

class NotificationRequest {
    private String mobile;
    private String message;

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}