package com.example.productcrud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.productcrud.model.Subscriber;

public interface SubscriberRepository extends JpaRepository<Subscriber, Integer> {
    Subscriber findByMobile(String mobile); // For login
}