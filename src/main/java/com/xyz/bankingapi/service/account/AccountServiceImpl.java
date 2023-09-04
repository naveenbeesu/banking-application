package com.xyz.bankingapi.service.account;

import com.xyz.bankingapi.dto.AccountDetails;
import com.xyz.bankingapi.dto.AccountType;
import com.xyz.bankingapi.dto.TransferRequest;
import com.xyz.bankingapi.dto.TransferResponse;
import com.xyz.bankingapi.entity.Account;
import com.xyz.bankingapi.entity.Customer;
import com.xyz.bankingapi.exceptions.AccountNotFoundException;
import com.xyz.bankingapi.repository.AccountRepository;
import com.xyz.bankingapi.utils.AccountProperties;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

import static com.xyz.bankingapi.utils.Constants.*;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    private final IbanService ibanService;

    private final AccountProperties accountProperties;

    public AccountServiceImpl(AccountRepository accountRepository, IbanServiceImpl ibanService, AccountProperties accountProperties) {
        this.accountRepository = accountRepository;
        this.ibanService = ibanService;
        this.accountProperties = accountProperties;
    }

    public AccountDetails getAccountDetails(String iban) {
        isValidIban(iban);
        AccountDetails accountDetails = new AccountDetails();
        log.info("Before calling accountRepository to find accountId");
        Optional<Account> accountExisting = accountRepository.findByIban(iban);
        if (accountExisting.isPresent()) {
            Account account = accountExisting.get();
            getAccountDetails(accountDetails, account);
        }
        log.info("after calling accountRepository to find accountId");
        return accountDetails;
    }

    private static void getAccountDetails(AccountDetails accountDetails, Account account) {
        accountDetails.setAccountNumber(account.getAccountNumber());
        accountDetails.setIban(account.getIban());
        accountDetails.setBalance(account.getBalance());
        accountDetails.setAccountType(account.getAccountType());
    }

    @Transactional
    public TransferResponse transfer(TransferRequest request) {
        String senderIban = request.getSenderIban();
        String receiverIban = request.getReceiverIban();
        double amount = request.getAmount();
        TransferResponse response = new TransferResponse();
        //validate iban details of sender and receiver
        if (!isValidIban(senderIban) || !isValidIban(receiverIban) || !ibanService.areSameBank(senderIban, receiverIban, accountProperties.getBankCode())) {
            response.setStatus(TRANSFER_FAILURE_INVALID_INPUTS);
        } else {
            //transfers amount
            transferAmount(senderIban, receiverIban, amount, response);
        }
        return response;
    }

    private boolean isValidIban(String iban) {
        ibanService.validateIban(iban);
        return true;
    }

    private void transferAmount(String senderIban, String receiverIban, double amount, TransferResponse response) {
        try {
            //checks if sender account exists
            Account senderAccount = accountRepository.findByIban(senderIban)
                    .orElseThrow(() -> new AccountNotFoundException("senderIban", ACCOUNT_NOT_FOUND));
            //checks for sufficient funds in sender account
            if (senderAccount.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
                log.info(NO_SUFFICIENT_FUNDS);
                response.setStatus(NO_SUFFICIENT_FUNDS);
            } else {
                senderAccount.setBalance(senderAccount.getBalance().subtract(BigDecimal.valueOf(amount)));
                //checks if receiver account exists
                Account receiverAccount = accountRepository.findByIban(receiverIban)
                        .orElseThrow(() -> new AccountNotFoundException("receiverIban", ACCOUNT_NOT_FOUND));
                receiverAccount.setBalance(receiverAccount.getBalance().add(BigDecimal.valueOf(amount)));

                Account senderAccountAfterDeduction = accountRepository.save(senderAccount);
                accountRepository.save(receiverAccount);

                AccountDetails accountDetails = new AccountDetails();
                getAccountDetails(accountDetails, senderAccountAfterDeduction); //gets updated sender account details
                response.setStatus(TRANSFER_SUCCESS);
                response.setOverview(accountDetails);
            }
        } catch (AccountNotFoundException ex) {
            log.error(ACCOUNT_NOT_FOUND + ex.getMessage());
            throw new AccountNotFoundException("iban", ACCOUNT_NOT_FOUND);
        } catch(OptimisticLockingFailureException ex){
            log.error(CONCURRENT_TRANSACTION_FAILURE + ex.getMessage());
            response.setStatus(CONCURRENT_TRANSACTION_FAILURE);
        }
    }

    @Transactional
    public boolean createAccount(Customer customer) {
        //generates accountNumber
        String accountNumber = AccountNumberGenerator.generateAccountNumber();
        //generates iban
        String iban = ibanService.generateIban(customer.getAddress().getCountry(), accountNumber, accountProperties.getBankCode());
        //checks if the already exists
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
        account.setCurrency(accountProperties.getCurrency());
        account.setAccountType(AccountType.SAVINGS.toString());
        accountRepository.save(account);

        return true;
    }
}
