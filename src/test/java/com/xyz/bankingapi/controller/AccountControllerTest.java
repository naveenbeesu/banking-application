package com.xyz.bankingapi.controller;

import com.xyz.bankingapi.dto.AccountDetails;
import com.xyz.bankingapi.dto.TransferRequest;
import com.xyz.bankingapi.dto.TransferResponse;
import com.xyz.bankingapi.entity.Account;
import com.xyz.bankingapi.entity.Customer;
import com.xyz.bankingapi.service.account.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static com.xyz.bankingapi.dto.AccountType.SAVINGS;
import static com.xyz.bankingapi.utils.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AccountControllerTest {
    @InjectMocks
    private AccountController accountController;
    @Mock
    private AccountServiceImpl accountService;

    private static TransferRequest getTransferRequest() {
        TransferRequest request = new TransferRequest();
        request.setSenderIban("NL70XYZB4104657415");
        request.setReceiverIban("NL08XYZB4333038629");
        request.setAmount(100);
        return request;
    }

    private static TransferResponse getTransferResponse() {
        AccountDetails accountOverview = getAccountDetails();

        TransferResponse mockResponse = new TransferResponse();
        mockResponse.setStatus(TRANSFER_SUCCESS);
        mockResponse.setOverview(accountOverview);
        return mockResponse;
    }

    private static AccountDetails getAccountDetails() {
        AccountDetails accountOverview = new AccountDetails();
        Account account = getAccount();
        accountOverview.setAccountNumber(account.getAccountNumber());
        accountOverview.setIban(account.getIban());
        accountOverview.setBalance(account.getBalance());
        accountOverview.setAccountType(account.getAccountType());
        return accountOverview;
    }

    private static Account getAccount() {
        Account account = new Account();
        account.setAccount_id(1L);
        account.setAccountType(String.valueOf(SAVINGS));
        Customer customer = new Customer();
        account.setCustomer(customer);
        account.setCurrency("EUR");
        account.setIban("NL08XYZB4333038629");
        account.setAccountNumber("4333038629");
        account.setBalance(BigDecimal.valueOf(300.00));
        return account;
    }

    @Test
    public void testAccountOverviewSuccess() {
        AccountDetails mockResponse = getAccountDetails();
        mockResponse.setIban("sample-iban");
        when(accountService.getAccountDetails(any())).thenReturn(mockResponse);

        ResponseEntity<AccountDetails> actualResponseEntity = accountController.getOverview("sample-iban");

        assertEquals(HttpStatusCode.valueOf(200), actualResponseEntity.getStatusCode());
        AccountDetails actualResponse = actualResponseEntity.getBody();

        assert actualResponse != null;
        assertEquals("sample-iban", actualResponse.getIban());
        assertEquals(BigDecimal.valueOf(300.00), actualResponse.getBalance());
    }

    @Test
    public void testAccountOverviewFailure() {
        when(accountService.getAccountDetails(any())).thenReturn(null);

        ResponseEntity<AccountDetails> actualResponseEntity = accountController.getOverview("sample-iban");

        assertEquals(HttpStatusCode.valueOf(404), actualResponseEntity.getStatusCode());
    }

    @Test
    public void testTransferSuccess() {
        TransferRequest request = getTransferRequest();
        TransferResponse mockResponse = getTransferResponse();

        when(accountService.transfer(any())).thenReturn(mockResponse);

        ResponseEntity<TransferResponse> actualResponseEntity = accountController.transfer(request);

        assertEquals(HttpStatusCode.valueOf(200), actualResponseEntity.getStatusCode());
        TransferResponse actualResponse = actualResponseEntity.getBody();

        assert actualResponse != null;
        assertEquals(TRANSFER_SUCCESS, actualResponse.getStatus());
        assertEquals(BigDecimal.valueOf(300.00), actualResponse.getOverview().getBalance());
    }

    @Test
    public void testTransferFailureForInsufficientFunds() {
        TransferRequest request = getTransferRequest();
        request.setAmount(10000);
        TransferResponse mockResponse = new TransferResponse();
        mockResponse.setStatus(NO_SUFFICIENT_FUNDS);

        when(accountService.transfer(any())).thenReturn(mockResponse);

        ResponseEntity<TransferResponse> actualResponseEntity = accountController.transfer(request);

        assertEquals(HttpStatusCode.valueOf(200), actualResponseEntity.getStatusCode());
        TransferResponse actualResponse = actualResponseEntity.getBody();

        assert actualResponse != null;
        assertEquals(NO_SUFFICIENT_FUNDS, actualResponse.getStatus());
    }

    @Test
    public void testTransferFailureForInvalidIban() {
        TransferRequest request = getTransferRequest();
        request.setSenderIban("");
        TransferResponse mockResponse = new TransferResponse();
        mockResponse.setStatus(TRANSFER_FAILURE_INVALID_INPUTS);

        when(accountService.transfer(any())).thenReturn(mockResponse);

        ResponseEntity<TransferResponse> actualResponseEntity = accountController.transfer(request);

        assertEquals(HttpStatusCode.valueOf(200), actualResponseEntity.getStatusCode());
        TransferResponse actualResponse = actualResponseEntity.getBody();

        assert actualResponse != null;
        assertEquals(TRANSFER_FAILURE_INVALID_INPUTS, actualResponse.getStatus());
    }
}
