package com.xyz.bankingapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long account_id;
    private String accountNumber;
    private String iban;
    private BigDecimal balance;
    private String currency;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    private String accountType;
    @Version
    private int version; //Optimistic locking version
}
