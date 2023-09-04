package com.xyz.bankingapi.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "account")
@Getter
@Setter
@Component
public class AccountProperties {
    public String bankCode;
    public String currency;
}
