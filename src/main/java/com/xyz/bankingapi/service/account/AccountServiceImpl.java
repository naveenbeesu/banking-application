package com.xyz.bankingapi.service.account;

import com.xyz.bankingapi.dto.AccountDetails;
import com.xyz.bankingapi.dto.AccountType;
import com.xyz.bankingapi.dto.TransferRequest;
import com.xyz.bankingapi.entity.Account;
import com.xyz.bankingapi.entity.Customer;
import com.xyz.bankingapi.exceptions.AccountNotFoundException;
import com.xyz.bankingapi.repository.AccountRepository;
import com.xyz.bankingapi.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    private final IbanService ibanService;

    public AccountServiceImpl(AccountRepository accountRepository, IbanServiceImpl ibanService) {
        this.accountRepository = accountRepository;
        this.ibanService = ibanService;
    }

    public AccountDetails getAccountDetails(String iban) {
        AccountDetails accountDetails = new AccountDetails();
        log.info("Before calling accountRepository to find accountId");
        Optional<Account> accountExisting = accountRepository.findByIban(iban);
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

    @Transactional
    public boolean transfer(TransferRequest request) {
        String senderIban = request.getSenderIban();
        String receiverIban = request.getReceiverIban();
        double amount = request.getAmount();

        if (!isValidIban(senderIban) || !isValidIban(receiverIban) || !ibanService.areSameBank(senderIban, receiverIban)) {
            return false;
        }

        // Check and deduct amount from sender Account
        return transferAmount(senderIban, receiverIban, amount);
    }

    private boolean isValidIban(String iban) {
        ibanService.validateIban(iban);
        return true;
    }

    private boolean transferAmount(String senderIban, String receiverIban, double amount) {
        try {
            Account senderAccount = accountRepository.findByIban(senderIban)
                    .orElseThrow(() -> new AccountNotFoundException("iban", "Sender Account not found"));

            if (senderAccount.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
                log.info("No sufficient funds available");
                return false;
            }
            senderAccount.setBalance(senderAccount.getBalance().subtract(BigDecimal.valueOf(amount)));

            Account receiverAccount = accountRepository.findByIban(receiverIban)
                    .orElseThrow(() -> new AccountNotFoundException("iban", "Receiver Account not found"));
            receiverAccount.setBalance(receiverAccount.getBalance().add(BigDecimal.valueOf(amount)));

            accountRepository.save(senderAccount);
            accountRepository.save(receiverAccount);
            return true; // Amount deducted successfully
        } catch (AccountNotFoundException | OptimisticLockingFailureException ex) {
            return false;
        }
    }

    @Transactional
    public boolean createAccount(Customer customer) {

        String accountNumber = AccountNumberGenerator.generateAccountNumber();
        String iban = ibanService.generateIban(customer.getAddress().getCountry(), accountNumber);

        log.info("Before calling accountRepository to check existing customer");
        Optional<Account> accountExisting = accountRepository.findByCustomer(customer);
        if (accountExisting.isPresent()) {
            return false;
        }
        log.info("after calling accountRepository to check existing customer");

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setIban(iban);
        account.setCustomer(customer);
        account.setBalance(BigDecimal.ZERO);
        account.setCurrency("EUR");
        account.setAccountType(AccountType.SAVINGS.toString());
        accountRepository.save(account);

        return true;
    }
}
