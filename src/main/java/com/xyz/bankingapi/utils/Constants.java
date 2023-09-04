package com.xyz.bankingapi.utils;

import org.springframework.stereotype.Component;

@Component
public class Constants {
    public static final  String INVALID_DOCUMENT_TYPE = "Document type/size is Invalid";
    public static final String[] VALID_CONTENT_TYPES =  {"image/png", "image/jpg", "image/jpeg", "application/pdf"};
    public static final long MAX_DOCUMENT_SIZE = 1048576L;
    public static final long MIN_DOCUMENT_SIZE = 50000L;
    public static final String NO_DOCUMENT_ATTACHED = "No Document Attached";
    public static final String BELOW_MINIMUM_AGE = "Age should be above 18 years";
    public static final int MINIMUM_AGE = 18;

    public static final String NOT_SUPPORTED_COUNTRY = "Registration allowed for customers from ";
    public static final String NO_SUFFICIENT_FUNDS = "No sufficient funds available";
    public static final String ACCOUNT_NOT_FOUND = "Account not found";
    public static final String TRANSFER_SUCCESS = "Success: Amount transferred";
    public static final String CONCURRENT_TRANSACTION_FAILURE = "Failed: Due to concurrent transaction issue";
    public static final String TRANSFER_FAILURE_INVALID_INPUTS = "Failed: Invalid account details";
    public static final String INVALID_IBAN = "Invalid Iban";
    public static final String USERNAME_EXISTING = "username is already existing";
    public static final String REGISTRATION_FAILED = "Registration Failed";
    public static final String ACCOUNT_EXISTING = "Account already exists with customerId";
    public static final String REGISTRATION_SUCCESSFUL = "Registration is Successful";
    public static final String LOGIN_SUCCESSFUL = "Login Successful";
    public static final String LOGIN_FAILED = "UserName or Password is incorrect/not existing";
    public static final String DOCUMENT_UPLOAD_SUCCESS = "Document uploaded successfully";
    public static final String USERNAME_NOT_FOUND  = "Username doesn't exist";
    public static final String INCORRECT_OTP = "Incorrect Otp";

}
