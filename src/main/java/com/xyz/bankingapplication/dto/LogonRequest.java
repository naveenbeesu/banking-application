package com.xyz.bankingapplication.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class LogonRequest {
    String username;
    String password;

}
