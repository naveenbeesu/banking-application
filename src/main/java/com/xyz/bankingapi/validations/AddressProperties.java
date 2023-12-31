package com.xyz.bankingapi.validations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix="address")
@Getter
@Setter
@Component
public class AddressProperties {
    public List<String> countries;
}
