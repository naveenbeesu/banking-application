package com.xyz.bankingapplication.validations;

import com.xyz.bankingapplication.exceptions.InvalidInputException;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

import static com.xyz.bankingapplication.utils.Constants.*;

@Service
public class ValidationService {
    public void validateDocument(MultipartFile idDocument) {
        if(idDocument==null || idDocument.isEmpty()){
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
