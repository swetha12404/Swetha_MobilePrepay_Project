package com.example.productcrud.service;

import com.example.productcrud.model.Subscriber;
import com.example.productcrud.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriberService {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    public Subscriber saveSubscriber(Subscriber subscriber) {
        // Encode the password before saving
        subscriber.setPassword(passwordEncoder.encode(subscriber.getPassword()));
        return subscriberRepository.save(subscriber);
    }

    public Subscriber findByMobile(String mobile) {
        return subscriberRepository.findByMobile(mobile);
    }

    public List<Subscriber> getAllSubscribers() {
        return subscriberRepository.findAll();
    }
}