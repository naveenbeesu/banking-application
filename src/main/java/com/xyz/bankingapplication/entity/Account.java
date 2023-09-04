package com.xyz.bankingapplication.entity;

import jakarta.persistence.*;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accountNumber;
    private Double balance;
    @ManyToOne
    @JoinColumn(name = "customerId")
    private Customer customer;
}
