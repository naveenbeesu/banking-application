package com.xyz.bankingapi.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class TransferResponse {
    String status;
    AccountDetails overview;
}
