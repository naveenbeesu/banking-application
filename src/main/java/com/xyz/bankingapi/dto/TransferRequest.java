package com.xyz.bankingapi.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class TransferRequest {
    String senderIban;
    String receiverIban;
    double amount;
}
