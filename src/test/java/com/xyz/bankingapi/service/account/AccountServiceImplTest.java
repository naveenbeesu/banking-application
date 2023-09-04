package com.xyz.bankingapi.service.account;

import com.xyz.bankingapi.dto.AccountDetails;
import com.xyz.bankingapi.dto.TransferRequest;
import com.xyz.bankingapi.dto.TransferResponse;
import com.xyz.bankingapi.entity.Account;
import com.xyz.bankingapi.entity.Address;
import com.xyz.bankingapi.entity.Customer;
import com.xyz.bankingapi.exceptions.InvalidIbanException;
import com.xyz.bankingapi.repository.AccountRepository;
import com.xyz.bankingapi.utils.AccountProperties;
import com.xyz.bankingapi.validations.AddressProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static com.xyz.bankingapi.dto.AccountType.SAVINGS;
import static com.xyz.bankingapi.utils.Constants.NO_SUFFICIENT_FUNDS;
import static com.xyz.bankingapi.utils.Constants.TRANSFER_SUCCESS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@EnableConfigurationProperties(value = AddressProperties.class)
@TestPropertySource("classpath:application-test.properties")
public class AccountServiceImplTest {
    @InjectMocks
    private AccountServiceImpl accountService;
    @Mock
    private AccountRepository accountRepository;
    @Autowired
    private IbanServiceImpl ibanService;

    @Autowired
    private AccountProperties accountProperties;

    private static TransferRequest getTransferRequest() {
        TransferRequest request = new TransferRequest();
        request.setSenderIban("NL70XYZB4104657415");
        request.setReceiverIban("NL08XYZB4333038629");
        request.setAmount(100);
        return request;
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

    private static Customer getCustomer(Address addressMock) {
        Customer customerMock = new Customer();
        customerMock.setFirstName("Naveen");
        customerMock.setLastName("Beesu");
        customerMock.setUsername("nbeesu");
        customerMock.setPassword("password");
        customerMock.setDateOfBirth(LocalDate.of(1992, 7, 10));
        customerMock.setAddress(addressMock);
        customerMock.setNationalId("NationalId");
        customerMock.setMobileNumber("910010023");
        return customerMock;
    }

    private static Address getAddress() {
        Address addressMock = new Address();
        addressMock.setAddressId(1L);
        addressMock.setStreet("street");
        addressMock.setCity("city");
        addressMock.setCountry("NL");
        addressMock.setPostalCode("postalCode");
        return addressMock;
    }

    @BeforeEach
    void setup() {
        accountService = new AccountServiceImpl(accountRepository, ibanService, accountProperties);
    }

    @Test
    public void testAccountOverviewSuccess() {
        Account account = getAccount();

        when(accountRepository.findByIban(anyString())).thenReturn(Optional.of(account));

        AccountDetails actualResponse = accountService.getAccountDetails("NL08XYZB4333038629");

        assert actualResponse != null;
        assertEquals("NL08XYZB4333038629", actualResponse.getIban());
        assertEquals(BigDecimal.valueOf(300.00), actualResponse.getBalance());
    }

    @Test
    public void testAccountOverviewFailureForInvalidIban() {
        Account account = getAccount();
        when(accountRepository.findByIban(anyString())).thenReturn(Optional.of(account));

        assertThrows(InvalidIbanException.class, () -> accountService.getAccountDetails("NL08XYZB433303862"));
    }

    @Test
    public void testTransferSuccess() {
        Account account = getAccount();
        Account senderAccountAfterSendingMoney = getAccount();
        TransferRequest request = getTransferRequest();

        when(accountRepository.findByIban(anyString())).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(senderAccountAfterSendingMoney);

        TransferResponse actualResponse = accountService.transfer(request);

        assert actualResponse != null;
        assertEquals(TRANSFER_SUCCESS, actualResponse.getStatus());
        assertEquals(BigDecimal.valueOf(300.00), actualResponse.getOverview().getBalance());
    }

    @Test
    public void testTransferFailure() {
        Account account = getAccount();
        Account senderAccountAfterSendingMoney = getAccount();
        TransferRequest request = getTransferRequest();
        request.setAmount(1000);

        when(accountRepository.findByIban(anyString())).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(senderAccountAfterSendingMoney);

        TransferResponse actualResponse = accountService.transfer(request);

        assert actualResponse != null;
        assertEquals(NO_SUFFICIENT_FUNDS, actualResponse.getStatus());
    }

    @Test
    public void testTransferFailureForInvalidIban() {
        Account account = getAccount();
        Account senderAccountAfterSendingMoney = getAccount();
        TransferRequest request = getTransferRequest();
        request.setSenderIban("");

        when(accountRepository.findByIban(anyString())).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(senderAccountAfterSendingMoney);

        assertThrows(InvalidIbanException.class, () -> accountService.transfer(request));
    }

    @Test
    public void testCreateAccount() {
        Account account = getAccount();
        //Account senderAccountAfterSendingMoney = getAccount();
        Address address = getAddress();
        Customer customer = getCustomer(address);

        when(accountRepository.findByCustomer(any())).thenReturn(Optional.empty());
        when(accountRepository.save(any())).thenReturn(account);

        boolean actualResponse = accountService.createAccount(customer);

        assertTrue(actualResponse);
    }

    @Test
    public void testCreateAccountFailure() {
        Account account = getAccount();
        //Account senderAccountAfterSendingMoney = getAccount();
        Address address = getAddress();
        Customer customer = getCustomer(address);

        when(accountRepository.findByCustomer(any())).thenReturn(Optional.of(account));

        boolean actualResponse = accountService.createAccount(customer);

        assertFalse(actualResponse);
    }
}
