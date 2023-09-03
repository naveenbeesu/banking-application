package com.xyz.customer.repository;

import com.xyz.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUsername(String username);

    Optional<Customer> findByUsernameAndPassword(String username, String password);
}
