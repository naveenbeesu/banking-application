package com.xyz.bankingapi.controller;

import com.xyz.bankingapi.dto.LogonRequest;
import com.xyz.bankingapi.dto.RegistrationRequest;
import com.xyz.bankingapi.dto.RegistrationResponse;
import com.xyz.bankingapi.service.account.OtpService;
import com.xyz.bankingapi.service.customer.CustomerServiceImpl;
import com.xyz.bankingapi.validations.ValidationService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CustomerControllerTest {
    @InjectMocks
    private CustomerController customerController;
    @Mock
    private CustomerServiceImpl customerService;

    @Mock
    private OtpService otpService;

    @Mock
    private ValidationService validationService;

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
                "idDocument", fileResource.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                fileResource.getInputStream());
    }

    @Test
    public void testRegisterSuccess() {
        RegistrationRequest request = getRegistrationRequest();
        RegistrationResponse mockResponse = new RegistrationResponse();
        mockResponse.setStatus("Registration is Successful");
        mockResponse.setUsername("nbeesu");
        mockResponse.setPassword("password");

        when(customerService.register(any())).thenReturn(mockResponse);
        when(otpService.validateOtp(any(), any())).thenReturn(true);

        ResponseEntity<RegistrationResponse> actualResponseEntity = customerController.register(request);

        assertEquals(HttpStatusCode.valueOf(200), actualResponseEntity.getStatusCode());
        RegistrationResponse actualResponse = actualResponseEntity.getBody();

        assert actualResponse != null;
        assertEquals("Registration is Successful", actualResponse.getStatus());
        assertEquals("nbeesu", actualResponse.getUsername());
        assertEquals("password", actualResponse.getPassword());

    }

    @Test
    public void testRegisterForExistingCustomer() {

        RegistrationRequest request = getRegistrationRequest();
        RegistrationResponse mockResponse = new RegistrationResponse();
        mockResponse.setStatus("username is already existing");

        when(customerService.register(any())).thenReturn(mockResponse);
        when(otpService.validateOtp(any(), any())).thenReturn(true);

        ResponseEntity<RegistrationResponse> actualResponseEntity = customerController.register(request);

        assertEquals(HttpStatusCode.valueOf(200), actualResponseEntity.getStatusCode());
        RegistrationResponse actualResponse = actualResponseEntity.getBody();

        assert actualResponse != null;

        assertEquals("username is already existing", actualResponse.getStatus());

    }

    @Test
    public void testLogonForExistingCustomer() {
        LogonRequest request = getLogonRequest();
        String mockResponse = "Login Successful";

        when(customerService.logon(any())).thenReturn(mockResponse);

        ResponseEntity<String> actualResponseEntity = customerController.logon(request);

        assertEquals(HttpStatusCode.valueOf(200), actualResponseEntity.getStatusCode());
        String actualResponse = actualResponseEntity.getBody();

        assert actualResponse != null;

        assertEquals("Login Successful", actualResponse);

    }

    @Test
    public void testLogonForNewCustomer() {
        LogonRequest request = getLogonRequest();
        String mockResponse = "UserName or Password is incorrect/not existing";

        when(customerService.logon(any())).thenReturn(mockResponse);

        ResponseEntity<String> actualResponseEntity = customerController.logon(request);

        assertEquals(HttpStatusCode.valueOf(200), actualResponseEntity.getStatusCode());
        String actualResponse = actualResponseEntity.getBody();

        assert actualResponse != null;

        assertEquals("UserName or Password is incorrect/not existing", actualResponse);

    }

    @Test
    public void testUploadImageSuccess() throws IOException {

        String mockResponse = "Document uploaded successfully";
        when(customerService.uploadImage(any(), any())).thenReturn(mockResponse);

        MockMultipartFile firstFile = getMockMultipartFile();

        assertNotNull(firstFile);

        ResponseEntity<String> actualResponseEntity = customerController.uploadImage("nbeesu", firstFile);

        assertEquals(HttpStatusCode.valueOf(200), actualResponseEntity.getStatusCode());
        String actualResponse = actualResponseEntity.getBody();
        assert actualResponse != null;
        assertEquals("Document uploaded successfully", actualResponse);
    }

    @Test
    public void testUploadImageFailure() throws IOException {

        String mockResponse = "Username doesn't exist";
        when(customerService.uploadImage(any(), any())).thenReturn(mockResponse);

        MockMultipartFile firstFile = getMockMultipartFile();

        assertNotNull(firstFile);

        ResponseEntity<String> actualResponseEntity = customerController.uploadImage("nbeesu", firstFile);

        assertEquals(HttpStatusCode.valueOf(200), actualResponseEntity.getStatusCode());
        String actualResponse = actualResponseEntity.getBody();
        assert actualResponse != null;
        assertEquals("Username doesn't exist", actualResponse);
    }

    @Test
    public void testDownloadImageSuccess() throws IOException {

        MockMultipartFile firstFile = getMockMultipartFile();
        assertNotNull(firstFile);
        byte[] mockImageResponse = firstFile.getBytes();

        when(customerService.downloadImage(any())).thenReturn(mockImageResponse);

        ResponseEntity<?> actualResponseEntity = customerController.downloadImage("nbeesu");

        assertEquals(HttpStatusCode.valueOf(200), actualResponseEntity.getStatusCode());
        byte[] actualResponse;
        actualResponse = (byte[]) actualResponseEntity.getBody();
        assert actualResponse != null;

        assertEquals(mockImageResponse, actualResponse);

    }

    @Test
    public void testDownloadImageFailure() {
        when(customerService.downloadImage(any())).thenReturn(null);

        ResponseEntity<?> actualResponseEntity = customerController.downloadImage("nbeesu");

        assertEquals(HttpStatusCode.valueOf(200), actualResponseEntity.getStatusCode());
        byte[] actualResponse;
        actualResponse = (byte[]) actualResponseEntity.getBody();

        assertNull(actualResponse);

    }
}
