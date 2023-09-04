package com.xyz.bankingapi.validations;

import com.xyz.bankingapi.dto.RegistrationRequest;
import com.xyz.bankingapi.exceptions.InvalidInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@EnableConfigurationProperties(value = AddressProperties.class)
@TestPropertySource("classpath:application-test.properties")
public class ValidationServiceTest {
    @Autowired
    AddressProperties addressProperties;
    private ValidationService validationService;

    private static RegistrationRequest getRegistrationRequest() {
        RegistrationRequest request = new RegistrationRequest();
        request.setFirstName("Naveen");
        request.setLastName("Beesu");
        request.setUsername("nbeesu");
        request.setDateOfBirth(LocalDate.of(2010, 7, 10));
        request.setStreet("street");
        request.setCity("city");
        request.setCountry("United States");
        request.setPostalCode("postalCode");
        request.setNationalId("NationalId");
        request.setMobileNumber("910010023");
        return request;
    }

    private static MockMultipartFile getMockMultipartFile(Resource fileResource) throws IOException {
        return new MockMultipartFile(
                "idDocument", fileResource.getFilename(),
                MediaType.IMAGE_PNG_VALUE,
                fileResource.getInputStream());
    }

    @BeforeEach
    void setup() {
        validationService = new ValidationService(addressProperties);
    }

    @Test
    public void testRegistrationRequestForInvalidAge() {
        RegistrationRequest request = getRegistrationRequest();

        assertThrows(InvalidInputException.class, () -> validationService.validateRequest(request));

    }

    @Test
    public void testRegistrationRequestForValidAge() {
        RegistrationRequest request = getRegistrationRequest();
        request.setDateOfBirth(LocalDate.of(1992, 7, 10));
        request.setCountry("Netherlands");

        validationService.validateRequest(request);
        doNothing();

    }

    @Test
    public void testRegistrationRequestForNotAllowedCountry() {
        RegistrationRequest request = getRegistrationRequest();

        assertThrows(InvalidInputException.class, () -> validationService.validateRequest(request));

    }

    @Test
    public void testValidDocumentForBelow50kbSize() throws IOException {
        Resource fileResource = new ClassPathResource(
                "batman_Below50kb.jpg");
        MockMultipartFile firstFile = getMockMultipartFile(fileResource);

        assertNotNull(firstFile);

        assertThrows(InvalidInputException.class, () -> validationService.validateDocument(firstFile));

    }

    @Test
    public void testValidDocumentForEmpty() throws IOException {
        Resource fileResource = new ClassPathResource(
                "");
        MockMultipartFile firstFile = getMockMultipartFile(fileResource);

        assertNotNull(firstFile);

        assertThrows(InvalidInputException.class, () -> validationService.validateDocument(firstFile));

    }

    @Test
    public void testValidDocumentForCorrectTypeAndSize() throws IOException {
        Resource fileResource = new ClassPathResource(
                "batman.png");
        MockMultipartFile firstFile = getMockMultipartFile(fileResource);

        assertNotNull(firstFile);
        validationService.validateDocument(firstFile);
        doNothing();
    }

    @Test
    public void testValidDocumentForIncorrectType() throws IOException {
        Resource fileResource = new ClassPathResource(
                "request.json");
        MockMultipartFile firstFile = getMockMultipartFile(fileResource);

        assertNotNull(firstFile);
        assertThrows(InvalidInputException.class, () -> validationService.validateDocument(firstFile));
    }
}
