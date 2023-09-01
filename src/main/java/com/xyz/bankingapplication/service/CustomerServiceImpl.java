package com.xyz.bankingapplication.service;

import com.xyz.bankingapplication.dto.LogonRequest;
import com.xyz.bankingapplication.dto.RegistrationRequest;
import com.xyz.bankingapplication.entity.Customer;
import com.xyz.bankingapplication.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public String register(RegistrationRequest request){
        Optional<Customer> existingCustomer = customerRepository.findByUsername(request.getUsername());
        if(existingCustomer.isPresent()){
            return "username is already existing";
        }
        Customer customer = new Customer();
        customer.setUsername(request.getUsername());
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setAddress(request.getAddress());
        customer.setPassword(PasswordGenerator.generateDefaultPassword());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setNationalId(request.getNationalId());
        customer.setRegistrationDate(request.getRegistrationDate());
        customer.setMobileNumber(request.getMobileNumber());
        customerRepository.save(customer);
        return "Registration Successful";
    }

    public String login(LogonRequest request) {
        Optional<Customer> existingCustomer = customerRepository.findByUsernameAndPassword(request.getUsername(), request.getPassword());
        if(existingCustomer.isPresent()){
            return "Login Successful";
        }
        return "UserName or Password is incorrect/not existing";
    }
}
