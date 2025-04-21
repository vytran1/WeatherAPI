# WeatherAPI
## Goals: After completing several projects in school, I realized that my backend skills were still limited. Most of my knowledge stopped at basic CRUD operations, basic security implementations using Spring Security with username/password (integrated with Thymeleaf or JWT), and social login. However, I lacked experience in more advanced topics such as caching APIs to improve performance or implementing OAuth2 authentication. For these reasons, I decided to build this project.
## Project Description
- This project will provide weather information with four different types which are hour weather, realtime weather, daily weather and full weather in specific location.
- The architecture used in this project is MVC
- This project have three sub projects and I will explain functions of these later.
## Used Technologies
- Java 21
- Spring Boot 3.3.2
- Spring Security
- Spring Data JPA
- Spring Cache
- MySQL Database
- Swagger tool for writing API Documents
## New Features 
- Implementing Oauth2 Authentication with clientId, clientSecret, Registered Server, Authentication Server, Resource Server.
- Using spring cache to cache result from APIs
- Using HATEOAS to add links to response of some api. These links help developers and users explore other aspects of one API more easily.
- Writing API document with swagger tool
## Running Project Steps
1. **Clone the repository**:
  - git clone https://github.com/vytran1/WeatherAPI.git
2. **Importing this project into IDE like Spring Tool Suite, IntelliJ, Eclipse, etc**.
3. **Configure database**:
  - Create a database with the name called weatherdb  
  - Go to src/main/resources/application.yml or application.properties
  - Setting `spring.jpa.hibernate.ddl-auto=` to update
  - Configure your own database credentials like username password
4. **Configure RSA Key pair**:
  - Opening WeatherAPIService and delete certs folder in path /src/main/resources/certs
  - Opening terminal for WeatherAPIService project
  - Typing command `openssl genrsa -out keypair.pem 2048`. This command will generate a rsa key pair file which contains private key and public key.
  - Typing openssl `openssl rsa -in keypair.pem -pubout -out public.pem`. This command will extract public key from the rsa key pair file to a separate file. The public key is used for providing API access token.
  - Typing command `openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private.pem `. This command will extract private key from the rsa key pair file to a separate file. The private key is used for verified the API access token.
5. **Running Test**:
   - Running tests in the WeatherAPIClientManager project which are `testAddAdminUser`, `testAddClientUser`, `testAddSystemApp`, `testAddReaderApp` in `/src/test/
   - Running tests in the WeatherAPIService project.
## Purpose Of Each Sub Projects
### WeatherAPICommon
   - This project contain all entities, their relationship configuration and share these entities to WeatherAPIClientManager and WeatherAPIServie projects.
### WeatherAPIClientManager
   - This module allows developers to register an account and create applications in order to receive a client ID and client secret, which are required to access the weather API services.
### WeatherAPIService
   - Once users have obtained their client ID and client secret from the WeatherAPIClientManager project, they can send these credentials to the WeatherAPIService to obtain an access token. This token must then be included in each request to access weather data. 
## Example Testing Project
 - Get Access Token With Client Id And Client Secret
![image](https://github.com/user-attachments/assets/6bd53678-4f2a-47ff-84a7-b8afe08a2adb)
 - Access Resources with access token
![image](https://github.com/user-attachments/assets/69e8fa78-dcdb-484f-93bb-009f239e6a55)
 

