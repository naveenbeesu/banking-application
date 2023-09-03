package com.xyz.accountservice.service;

import com.xyz.accountservice.dto.AccountDetails;
import com.xyz.accountservice.dto.AccountType;
import com.xyz.accountservice.dto.TransferRequest;
import com.xyz.accountservice.entity.Account;
import com.xyz.accountservice.entity.Customer;
import com.xyz.accountservice.exceptions.AccountNotFoundException;
import com.xyz.accountservice.repository.AccountRepository;
import com.xyz.accountservice.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    private final CustomerRepository customerRepository;

    private final IbanService ibanService;

    public AccountServiceImpl(AccountRepository accountRepository, IbanServiceImpl ibanService, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.ibanService = ibanService;
        this.customerRepository = customerRepository;
    }

    public AccountDetails getDetails(Long accountId) {
        AccountDetails accountDetails = new AccountDetails();
        log.info("Before calling accountRepository to find accountId");
        Optional<Account> accountExisting = accountRepository.findById(accountId);
        if (accountExisting.isPresent()) {
            Account account = accountExisting.get();
            accountDetails.setAccountNumber(account.getAccountNumber());
            accountDetails.setIban(account.getIban());
            accountDetails.setBalance(account.getBalance());
            accountDetails.setAccountType(account.getAccountType());
        }
        log.info("after calling accountRepository to find accountId");
        return accountDetails;
    }

    public boolean transfer(TransferRequest request) {
        String senderIban = request.getSenderIban();
        String receiverIban = request.getReceiverIban();
        double amount = request.getAmount();

        if (!isValidIban(senderIban) || !isValidIban(receiverIban) || !areSameBank(senderIban, receiverIban)) {
            return false;
        }

        // Check and deduct amount from sender Account
        boolean amountDeductedFromSender = deductAmountFromSenderAccount(senderIban, amount);

        // if the amount is deducted from sender, add the amount to receiver
        if (amountDeductedFromSender) {
            boolean amountAddedToReceiver = addAmountToReceiverAccount(receiverIban, amount);
            return amountAddedToReceiver;
        }

        return false;
    }

    private boolean isValidIban(String iban) {
        // Implement IBAN validation logic (not shown in this simplified example)
        return true;
    }

    private boolean areSameBank(String senderIban, String receiverIban) {
        // Implement logic to check if sender and receiver belong to the same bank
        return true;
    }

    private boolean deductAmountFromSenderAccount(String iban, double amount) {
        try {
            Account account = accountRepository.findByIban(iban)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));

            // Perform optimistic locking
            account.setBalance(account.getBalance().subtract(BigDecimal.valueOf(amount)));

            // Add business logic here, e.g., checking for sufficient balance

            accountRepository.save(account);
            return true; // Deduction successful
        } catch (AccountNotFoundException | OptimisticLockingFailureException ex) {
            return false;
        }
    }

    private boolean addAmountToReceiverAccount(String iban, double amount) {
        // Simulate adding funds to the receiver's account (update the database)
        // Return true if successful, otherwise false
        return true;
    }

    public String createAccount(long customerId) {
        log.info("Before calling customerRepository to check existing customerId");
        Optional<Customer> customerExisting = customerRepository.findById(customerId);
        if (customerExisting.isEmpty()) return "CustomerId not Found";
        log.info("after calling customerRepository to check existing customerId");

        Customer customer = customerExisting.get();
        String accountNumber = AccountNumberGenerator.generateAccountNumber() + customerId;
        String iban = ibanService.generateIban(customer.getAddress().getCountry(), accountNumber);
        log.info("Before calling accountRepository to check existing customer");
        Optional<Account> accountExisting = accountRepository.findByCustomer(customer);
        if (accountExisting.isPresent()) {
            return "Account is already created";
        }
        log.info("after calling accountRepository to check existing customer");
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setIban(iban);
        account.setCustomer(customer);
        account.setCurrency("EUR");
        account.setAccountType(AccountType.SAVINGS.toString());
        Account accountSaved = accountRepository.save(account);
        return "Account Created";
    }
}
