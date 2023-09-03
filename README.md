# banking-application

Banking application with Registration, Login and Payment features

# Scope change and/or assumptions

- id document size is expecting between 50 kb to 1Mb
- Front end team calls image upload and download endpoints separatley
- Default generated password length is 8 and combination of Numbers and Letters
- Creating a separate endpoint for upload of image instead of taking both image and registrationRequest as input params
  with the Multipart/form-data type instead of json because the input will not be json and as per the input
  requirements, image upload should not stop user from continuing with the process. Please refer below article
    - https://stackoverflow.com/questions/33279153/rest-api-file-ie-images-processing-best-practices
    - It provides the ability to store images in the cloud and also the RegistrationRequest will be in json format
