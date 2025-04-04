package com.example.productcrud.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.productcrud.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Admin findByUsername(String username); // For login
}