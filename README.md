# banking-application
Banking application with Registration, Login and Payment features

# Scope change and/or assumptions
- Developing microservices like customer-service and account-service would be ideal solution in the real time. But, due to the time constraint and also docker installation issue in my laptop, building all the components in one application so that it will be easy to run and test.
- Since the current application has mainly relational data, used Mysql database to add tables (customer, address, and account), and to store related data, and to perform CRUD operations
- id document size is expecting between 50 kb to 1Mb
- Front end team calls image upload and download endpoints separately
- Default generated password length is 8 and combination of Numbers and Letters
- Creating a separate endpoint for upload of image instead of taking both image and registrationRequest as input params with the Multipart/form-data type instead of json because the input will not be json and as per the input requirements, image upload should not stop user from continuing with the process. Please refer below article
  - https://stackoverflow.com/questions/33279153/rest-api-file-ie-images-processing-best-practices
  - It provides the ability to store images in the cloud and also the RegistrationRequest will be in json format
- OTP Generation: Actual otp generation and sending to mobile involves more complexity so for time being, created a separate endpoint with mobileNumber as path variable that generates otp and stores it as key value pair in the hashmap.
  - when /register endpoint is called with RegistrationRequest including otp, both mobile and otp will be validated with the hash map values and allowed only if the mobileNumber and otp key value pair exists in the HashMap.
  - In the future, it can be created as separate service and can store the otp in cache.
- AccountNumber generation: 10 digit random account number is generated
- currency is considered as EURO, and it can be changed in properties file
- Amount will never become negative and initial amount while creating account is zero
- IBAN length is 18 digits with the combination of countryCode(2)+checkDigit(2)+bankCode(4)+accountNumber(10)
  - Example -> NL08XYZB4333038629
  - bankCode is considered as -> XYZB, it can be changed in properties file
  - checkDigit is randomly generated by the iban4j dependency mentioned below
- IBAN generation: Initially wrote logic to generate and validate the iban. Later, to make it more realistic, added the below Iban dependency to generate and validate the iban
            <groupId>org.iban4j</groupId>
            <artifactId>iban4j</artifactId>
            <version>3.2.6-RELEASE</version>

# How to run this application?
## MySql Setup
  - Install MySql software and schema, username and password as below or mention your own details in the application.properties
  - spring.datasource.url=jdbc:mysql://localhost:3306/mytestdb
    spring.datasource.username=root
    spring.datasource.password=Neevan@6
  - start mysql server/workbench and create three tables as shown below and 
### Customer Table
```js
    CREATE TABLE `customer` (
        `customer_id` bigint NOT NULL AUTO_INCREMENT,
        `first_name` varchar(255) DEFAULT NULL,
        `last_name` varchar(255) DEFAULT NULL,
        `username` varchar(255) DEFAULT NULL,
        `date_of_birth` date NOT NULL,
        `address_id` bigint NOT NULL,
        `mobile_number` varchar(255) DEFAULT NULL,
        `national_id` varchar(255) DEFAULT NULL,
        `password` varchar(255) DEFAULT NULL,
        `registration_date` datetime(6) DEFAULT NULL,
        `last_login_time` datetime(6) DEFAULT NULL,
        `id_document` mediumblob,
        PRIMARY KEY (`customer_id`),
        UNIQUE KEY `customerId_UNIQUE` (`customer_id`),
        UNIQUE KEY `userName_UNIQUE` (`username`),
        KEY `address_id_idx` (`address_id`),
        CONSTRAINT `address_id` FOREIGN KEY (`address_id`) REFERENCES `address` (`address_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
```
### Address Table

```js
    CREATE TABLE `address` (
        `address_id` bigint NOT NULL AUTO_INCREMENT,
        `street` varchar(255) NOT NULL,
        `city` varchar(255) DEFAULT NULL,
        `state` varchar(255) DEFAULT NULL,
        `postal_code` varchar(255) DEFAULT NULL,
        `country` varchar(255) DEFAULT NULL,
        UNIQUE KEY `address_id_UNIQUE` (`address_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
```

### Account Entity

```js
    CREATE TABLE `account` (
`account_id` bigint NOT NULL AUTO_INCREMENT,
`account_number` varchar(255) DEFAULT NULL,
`balance` decimal(38,2) DEFAULT NULL,
`iban` varchar(255) DEFAULT NULL,
`customer_id` bigint DEFAULT NULL,
`account_type` varchar(255) DEFAULT NULL,
`currency` varchar(255) DEFAULT NULL,
`version` int NOT NULL,
PRIMARY KEY (`account_id`),
KEY `customer_id` (`customer_id`),
CONSTRAINT `customer_id` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
```

- Do mvn clean install
- Run as Spring Boot application
- Hit the endpoints in the swagger UI shown below
  - Swagger Documentation URL - http://localhost:8080/swagger
  - or Hit the below endpoints using postman collection attached in the api
  - Import Banking Application.postman_collection.json file in the Postman and hit below endpoints
  - customer-service endpoints
    - POST: http://localhost:8080/register with RegistrationRequest as requestBody
    - GET: http://localhost:8080/logon with LoginRequest as requestBody
    - PUT: http://localhost:8080/upload/{username} with valid image file attached as form-data with File type
    - GET: http://localhost:8080/download/{username} to download the idDocument
    - GET: http://localhost:8080/generateOtp/{mobileNumber} to generate otp for the given mobileNumber

  - account-service endpoints
    - GET: http://localhost:8080/account/overview/{iban} provides account overview
    - PUT: http://localhost:8080/account/transfer with TransferRequest as requestBody

#To Run as Docker image
Please refer this link -> https://www.baeldung.com/dockerizing-spring-boot-application
- Run the docker quick start terminal and it will be up in few minutes
- Move to the project directory i.e. banking-application
- Create Docker image -> docker build –t banking-application.jar
- To check docker image, type command  docker image ls
- You should be able to see the docker image with name -> banking-application.jar
- To run the docker the image  docker run -p 9090:8080 banking-application.jar
- Here, 9090 is docker port where our application will be running, and 8080 is our api’s exposed server port
- It should be up now
- We can access the docker image using port 9090  Post http://docker-ipaddress:9090/register
- And also using api server port 8080 -> POST http://localhost:8080/register

#To Run both Spring Boot Application with MySql as docker images
- To run the complete application along with mysql db ->  docker-compose up
- To check docker images -> docker images
- To check containers -> docker ps
- Refer this link -> https://medium.com/thefreshwrites/the-way-of-dockerize-a-spring-boot-and-mysql-application-with-docker-compose-de2fc03c6a42
