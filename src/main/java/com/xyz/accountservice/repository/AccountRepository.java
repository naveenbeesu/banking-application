package com.xyz.accountservice.repository;

import com.xyz.accountservice.entity.Account;
import com.xyz.accountservice.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByCustomer(Customer customer);

    Optional<Account> findByIban(String iban);
}
