package com.xyz.bankingapplication.utils;

import org.springframework.stereotype.Component;

@Component
public class Constants {
    public static final  String INVALID_DOCUMENT_TYPE = "Document type/size is Invalid";
    public static final String[] VALID_CONTENT_TYPES =  {"image/png", "image/jpg", "image/jpeg", "application/pdf"};
    public static final long MAX_DOCUMENT_SIZE = 1048576L;
    public static final long MIN_DOCUMENT_SIZE = 50000L;
    public static final String NO_DOCUMENT_ATTACHED = "No Document Attached";

}