package com.xyz.bankingapplication.validations;

import com.xyz.bankingapplication.dto.RegistrationRequest;
import com.xyz.bankingapplication.exceptions.InvalidInputException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;

import static com.xyz.bankingapplication.utils.Constants.*;

@Service
public class ValidationService {

    private final AddressProperties addressProperties;

    public ValidationService(AddressProperties addressProperties) {
        this.addressProperties = addressProperties;
    }

    public void validateRequest(RegistrationRequest request) {
        isValidAge(request.getDateOfBirth());
        isAllowedCountry(request.getCountry());
    }

    private void isAllowedCountry(String country) {
        if (!addressProperties.getCountries().contains(country)) {
            throw new InvalidInputException("country", NOT_SUPPORTED_COUNTRY + addressProperties.getCountries());
        }
    }

    private void isValidAge(LocalDate dateOfBirth) {
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(dateOfBirth, currentDate);
        if (period.getYears() <= MINIMUM_AGE) {
            throw new InvalidInputException("dateOfBirth", BELOW_MINIMUM_AGE);
        }
    }

    public void validateDocument(MultipartFile idDocument) {
        if (idDocument == null || idDocument.isEmpty()) {
            throw new InvalidInputException("idDocument", NO_DOCUMENT_ATTACHED);
        }
        String contentType = idDocument.getContentType();
        Long documentSize = idDocument.getSize();
        if (!(isValidContentType(contentType) && isAllowedSize(documentSize))) {
            throw new InvalidInputException("idDocument", INVALID_DOCUMENT_TYPE);
        }
    }

    private boolean isAllowedSize(Long documentSize) {
        return documentSize <= MAX_DOCUMENT_SIZE && documentSize > MIN_DOCUMENT_SIZE;
    }

    private boolean isValidContentType(String contentType) {
        return Arrays.asList(VALID_CONTENT_TYPES).contains(contentType);
    }
}
