package com.example.productcrud.service;


import com.example.productcrud.model.Admin;
import com.example.productcrud.model.Subscriber;
import com.example.productcrud.repository.AdminRepository;
import com.example.productcrud.repository.SubscriberRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final SubscriberRepository subscriberRepository;

    public CustomUserDetailsService(AdminRepository adminRepository, SubscriberRepository subscriberRepository) {
        this.adminRepository = adminRepository;
        this.subscriberRepository = subscriberRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Check Admin first
        Admin admin = adminRepository.findByUsername(username);
        if (admin != null) {
            return User.withUsername(admin.getUsername())
                    .password(admin.getPassword())
                    .roles("ADMIN")
                    .build();
        }

        // Check Subscriber (using mobile as user_name)
        Subscriber subscriber = subscriberRepository.findByMobile(username);
        if (subscriber != null) {
            return User.withUsername(subscriber.getMobile())
                    .password(subscriber.getPassword())
                    .roles("SUBSCRIBER")
                    .build();
        }
        throw new UsernameNotFoundException("User not found with username/mobile: " + username);
    }
}