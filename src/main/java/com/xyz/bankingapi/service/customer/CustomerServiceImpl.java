package com.xyz.bankingapi.service.customer;

import com.xyz.bankingapi.dto.LogonRequest;
import com.xyz.bankingapi.dto.RegistrationRequest;
import com.xyz.bankingapi.dto.RegistrationResponse;
import com.xyz.bankingapi.entity.Address;
import com.xyz.bankingapi.entity.Customer;
import com.xyz.bankingapi.repository.AddressRepository;
import com.xyz.bankingapi.repository.CustomerRepository;
import com.xyz.bankingapi.service.account.AccountServiceImpl;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static com.xyz.bankingapi.utils.Constants.*;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    private final AccountServiceImpl accountService;

    public CustomerServiceImpl(CustomerRepository customerRepository, AddressRepository addressRepository, AccountServiceImpl accountService) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.accountService = accountService;
    }

    @Transactional
    public RegistrationResponse register(RegistrationRequest request){
        Optional<Customer> existingCustomer = customerRepository.findByUsername(request.getUsername());
        RegistrationResponse response = new RegistrationResponse();
        if(existingCustomer.isPresent()){
            response.setStatus(USERNAME_EXISTING);
            return response;
        }

        Address address = new Address();
        insertAddress(request, address);

        Customer customer = new Customer();
        Customer customerCreated = insertCustomerDetails(request, customer, address);

        if(customerCreated==null){
            response.setStatus(REGISTRATION_FAILED);
        } else {
            if(!accountService.createAccount(customerCreated)){
                response.setStatus(ACCOUNT_EXISTING);
            } else {
                response.setStatus(REGISTRATION_SUCCESSFUL);
                response.setUsername(customerCreated.getUsername());
                response.setPassword(customerCreated.getPassword());
            }
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
            return LOGIN_SUCCESSFUL;
        }
        return LOGIN_FAILED;
    }

    @Override
    public String uploadImage(MultipartFile idDocument, String username) throws IOException {
        Optional<Customer> existingCustomer = customerRepository.findByUsername(username);

        if(existingCustomer.isPresent()){
            Customer customer = existingCustomer.get();
            byte[] document = idDocument.getBytes();
            customer.setIdDocument(document);
            customerRepository.save(customer);
            return DOCUMENT_UPLOAD_SUCCESS;
        }
        log.info(USERNAME_NOT_FOUND);
        return USERNAME_NOT_FOUND;
    }

    public byte[] downloadImage(String username) {
        Optional<Customer> existingCustomer = customerRepository.findByUsername(username);
        if(existingCustomer.isPresent()){
            return existingCustomer.get().getIdDocument();
        }
        log.info(USERNAME_NOT_FOUND);
        return null;
    }
}
