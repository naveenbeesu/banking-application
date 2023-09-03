package com.xyz.bankingapplication.service;

import com.xyz.bankingapplication.dto.LogonRequest;
import com.xyz.bankingapplication.dto.RegistrationRequest;
import com.xyz.bankingapplication.dto.RegistrationResponse;
import com.xyz.bankingapplication.entity.Address;
import com.xyz.bankingapplication.entity.Customer;
import com.xyz.bankingapplication.repository.AddressRepository;
import com.xyz.bankingapplication.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository, AddressRepository addressRepository) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional
    public RegistrationResponse register(RegistrationRequest request){
        Optional<Customer> existingCustomer = customerRepository.findByUsername(request.getUsername());
        RegistrationResponse response = new RegistrationResponse();
        if(existingCustomer.isPresent()){
            response.setStatus("username is already existing");
            return response;
        }

        Address address = new Address();
        insertAddress(request, address);

        Customer customer = new Customer();
        Customer customerCreated = insertCustomerDetails(request, customer, address);

        if(customerCreated==null){
            response.setStatus("Registration Failed");
        } else {
            response.setStatus("Registration is Successful");
            response.setUsername(customerCreated.getUsername());
            response.setPassword(customerCreated.getPassword());
        }
        return response;
    }

    private Customer insertCustomerDetails(RegistrationRequest request, Customer customer, Address address) {
        customer.setUsername(request.getUsername());
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setAddress(address);
        customer.setPassword(PasswordGenerator.generateDefaultPassword());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setNationalId(request.getNationalId());
        customer.setRegistrationDate(request.getRegistrationDate());
        customer.setMobileNumber(request.getMobileNumber());

        return customerRepository.save(customer);
    }

    private void insertAddress(RegistrationRequest request, Address address) {
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());

        //save new address to database
        addressRepository.save(address);
    }

    public String logon(LogonRequest request) {
        Optional<Customer> existingCustomer = customerRepository.findByUsernameAndPassword(request.getUsername(), request.getPassword());
        if(existingCustomer.isPresent()){
            return "Login Successful";
        }
        return "UserName or Password is incorrect/not existing";
    }

    @Override
    public String uploadImage(MultipartFile idDocument, String username) throws IOException {
        Optional<Customer> existingCustomer = customerRepository.findByUsername(username);

        if(existingCustomer.isPresent()){
            Customer customer = existingCustomer.get();
            byte[] document = idDocument.getBytes();
            customer.setIdDocument(document);
            customerRepository.save(customer);
            return "Document uploaded successfully";
        }
        log.info("username doesn't exist");
        return "Username doesn't exist";
    }

    public byte[] downloadImage(String username) {
        Optional<Customer> existingCustomer = customerRepository.findByUsername(username);
        if(existingCustomer.isPresent()){
            return existingCustomer.get().getIdDocument();
        }
        log.info("username doesn't exist");
        return null;
    }
}
