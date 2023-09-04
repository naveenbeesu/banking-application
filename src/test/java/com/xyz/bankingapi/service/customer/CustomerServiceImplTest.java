package com.xyz.bankingapi.service.customer;

import com.xyz.bankingapi.dto.LogonRequest;
import com.xyz.bankingapi.dto.RegistrationRequest;
import com.xyz.bankingapi.dto.RegistrationResponse;
import com.xyz.bankingapi.entity.Address;
import com.xyz.bankingapi.entity.Customer;
import com.xyz.bankingapi.repository.AddressRepository;
import com.xyz.bankingapi.repository.CustomerRepository;
import com.xyz.bankingapi.service.account.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CustomerServiceImplTest {
    @InjectMocks
    private CustomerServiceImpl customerService;
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AccountServiceImpl accountService;

    @Test
    public void testRegisterSuccess() {
        RegistrationRequest request = getRegistrationRequest();
        Address addressMock = getAddress();
        Customer customerMock = getCustomer(addressMock);
        when(customerRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(customerRepository.save(any())).thenReturn(customerMock);
        when(addressRepository.save(any())).thenReturn(addressMock);
        when(accountService.createAccount(any())).thenReturn(true);

        RegistrationResponse actualResponse = customerService.register(request);

        assertEquals("Registration is Successful", actualResponse.getStatus());
        assertEquals("nbeesu", actualResponse.getUsername());
        assertEquals("password", actualResponse.getPassword());

    }

    @Test
    public void testRegisterForExistingCustomer() {
        RegistrationRequest request = getRegistrationRequest();
        Address addressMock = getAddress();

        Customer customerMock = getCustomer(addressMock);
        Optional<Customer> customerOptional = Optional.of(customerMock);
        when(customerRepository.findByUsername(anyString())).thenReturn(customerOptional);

        RegistrationResponse actualResponse = customerService.register(request);

        assertEquals("username is already existing", actualResponse.getStatus());

    }

    @Test
    public void testLogonForExistingCustomer() {
        LogonRequest request = getLogonRequest();
        Address addressMock = getAddress();

        Customer customerMock = getCustomer(addressMock);
        Optional<Customer> customerOptional = Optional.of(customerMock);

        when(customerRepository.findByUsernameAndPassword(anyString(), anyString())).thenReturn(customerOptional);

        String actualResponse = customerService.logon(request);

        assertEquals("Login Successful", actualResponse);

    }

    @Test
    public void testLogonForNewCustomer() {
        LogonRequest request = getLogonRequest();

        when(customerRepository.findByUsernameAndPassword(anyString(), anyString())).thenReturn(Optional.empty());

        String actualResponse = customerService.logon(request);

        assertEquals("UserName or Password is incorrect/not existing", actualResponse);

    }

    @Test
    public void testUploadImageSuccess() throws IOException {

        Address addressMock = getAddress();
        Customer customerMock = getCustomer(addressMock);
        Optional<Customer> customerOptional = Optional.of(customerMock);

        when(customerRepository.findByUsername(anyString())).thenReturn(customerOptional);
        when(customerRepository.save(any())).thenReturn(customerMock);

        MockMultipartFile firstFile = getMockMultipartFile();

        assertNotNull(firstFile);

        String actualResponse = customerService.uploadImage(firstFile, "nbeesu");

        assertEquals("Document uploaded successfully", actualResponse);

    }

    @Test
    public void testUploadImageFailure() throws IOException {

        when(customerRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        MockMultipartFile firstFile = getMockMultipartFile();
        assertNotNull(firstFile);
        String actualResponse = customerService.uploadImage(firstFile, "nbeesu");

        assertEquals("Username doesn't exist", actualResponse);

    }

    @Test
    public void testDownloadImageSuccess() throws IOException {


        MockMultipartFile firstFile = getMockMultipartFile();
        assertNotNull(firstFile);
        byte[] mockImageResponse = firstFile.getBytes();

        Address addressMock = getAddress();
        Customer customerMock = getCustomer(addressMock);
        customerMock.setIdDocument(mockImageResponse);
        Optional<Customer> customerOptional = Optional.of(customerMock);

        when(customerRepository.findByUsername(anyString())).thenReturn(customerOptional);

        byte[] actualResponse = customerService.downloadImage("nbeesu");

        assertEquals(mockImageResponse, actualResponse);

    }

    @Test
    public void testDownloadImageFailure() {

        Address addressMock = getAddress();
        Customer customerMock = getCustomer(addressMock);
        customerMock.setIdDocument(null);
        Optional<Customer> customerOptional = Optional.of(customerMock);

        when(customerRepository.findByUsername(anyString())).thenReturn(customerOptional);

        byte[] actualResponse = customerService.downloadImage("nbeesu");

        assertNull(actualResponse);

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
        addressMock.setCountry("Netherlands");
        addressMock.setPostalCode("postalCode");
        return addressMock;
    }

    private static RegistrationRequest getRegistrationRequest() {
        RegistrationRequest request = new RegistrationRequest();
        request.setFirstName("Naveen");
        request.setLastName("Beesu");
        request.setUsername("nbeesu");
        request.setDateOfBirth(LocalDate.of(1992, 7, 10));
        request.setStreet("street");
        request.setCity("city");
        request.setCountry("Netherlands");
        request.setPostalCode("postalCode");
        request.setNationalId("NationalId");
        request.setMobileNumber("910010023");
        return request;
    }

    private static LogonRequest getLogonRequest() {
        LogonRequest request = new LogonRequest();
        request.setUsername("nbeesu");
        request.setPassword("password");
        return request;
    }

    private static MockMultipartFile getMockMultipartFile() throws IOException {
        Resource fileResource = new ClassPathResource(
                "batman.png");

        return new MockMultipartFile(
                "idDocument",fileResource.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                fileResource.getInputStream());
    }
}
