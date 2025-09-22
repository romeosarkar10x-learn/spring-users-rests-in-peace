# Spring Framework + Cassandra User Management Backend

## Table of Contents
1. [Spring Framework Overview](#spring-framework-overview)
2. [Project Setup](#project-setup)
3. [Dependencies](#dependencies)
4. [Cassandra Configuration](#cassandra-configuration)
5. [Spring Configuration](#spring-configuration)
6. [User Entity](#user-entity)
7. [Repository Layer](#repository-layer)
8. [Service Layer](#service-layer)
9. [Controller Layer](#controller-layer)
10. [Main Application](#main-application)
11. [Testing](#testing)

## Spring Framework Overview

The Spring Framework is built around these core concepts:

### 1. Inversion of Control (IoC)
Spring manages object creation and dependency injection. Instead of your classes creating their dependencies, Spring injects them.

### 2. Aspect-Oriented Programming (AOP)
Cross-cutting concerns like logging, security, and transactions can be separated from business logic.

### 3. Core Components
- **ApplicationContext**: The Spring IoC container
- **Beans**: Objects managed by Spring
- **Configuration**: How you tell Spring what beans to create and how to wire them

### 4. Configuration Approaches
- **XML Configuration**: Traditional approach using XML files
- **Java Configuration**: Using `@Configuration` classes
- **Annotation-based**: Using annotations like `@Component`, `@Service`, etc.

We'll use Java Configuration with annotations for this project.

## Project Setup

Create a Maven project with this structure:

```
user-management-backend/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── usermanagement/
│   │   │               ├── Application.java
│   │   │               ├── config/
│   │   │               │   ├── AppConfig.java
│   │   │               │   └── CassandraConfig.java
│   │   │               ├── model/
│   │   │               │   └── User.java
│   │   │               ├── repository/
│   │   │               │   └── UserRepository.java
│   │   │               ├── service/
│   │   │               │   ├── UserService.java
│   │   │               │   └── UserServiceImpl.java
│   │   │               └── controller/
│   │   │                   └── UserController.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
└── README.md
```

## Dependencies

**pom.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>user-management-backend</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <spring.version>6.0.15</spring.version>
        <cassandra.driver.version>4.17.0</cassandra.driver.version>
        <jackson.version>2.16.1</jackson.version>
    </properties>

    <dependencies>
        <!-- Spring Core -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${spring.version}</version>
        </dependency>
        
        <!-- Spring Web MVC -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>${spring.version}</version>
        </dependency>

        <!-- Spring Data Cassandra -->
        <dependency>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-cassandra</artifactId>
            <version>4.1.5</version>
        </dependency>

        <!-- Cassandra Driver -->
        <dependency>
            <groupId>com.datastax.oss</groupId>
            <artifactId>java-driver-core</artifactId>
            <version>${cassandra.driver.version}</version>
        </dependency>

        <!-- Jackson for JSON -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>${jackson.version}</version>
        </dependency>

        <!-- Servlet API -->
        <dependency>
            <groupId>jakarta.servlet</groupId>
            <artifactId>jakarta.servlet-api</artifactId>
            <version>6.0.0</version>
            <scope>provided</scope>
        </dependency>

        <!-- Logging -->
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.4.14</version>
        </dependency>

        <!-- Validation -->
        <dependency>
            <groupId>org.hibernate.validator</groupId>
            <artifactId>hibernate-validator</artifactId>
            <version>8.0.1.Final</version>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>${spring.version}</version>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.1</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>
            
            <plugin>
                <groupId>org.eclipse.jetty</groupId>
                <artifactId>jetty-maven-plugin</artifactId>
                <version>11.0.17</version>
                <configuration>
                    <webApp>
                        <contextPath>/</contextPath>
                    </webApp>
                    <httpConnector>
                        <port>8080</port>
                    </httpConnector>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## Cassandra Configuration

**CassandraConfig.java**
```java
package com.example.usermanagement.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import java.net.InetSocketAddress;

@Configuration
@EnableCassandraRepositories(basePackages = "com.example.usermanagement.repository")
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Value("${cassandra.keyspace:user_management}")
    private String keyspaceName;

    @Value("${cassandra.contact-points:127.0.0.1}")
    private String contactPoints;

    @Value("${cassandra.port:9042}")
    private int port;

    @Value("${cassandra.local-datacenter:datacenter1}")
    private String localDatacenter;

    @Override
    protected String getKeyspaceName() {
        return keyspaceName;
    }

    @Override
    protected String getContactPoints() {
        return contactPoints;
    }

    @Override
    protected int getPort() {
        return port;
    }

    @Override
    protected String getLocalDataCenter() {
        return localDatacenter;
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Bean
    @Override
    public CqlSession cassandraSession() {
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress(contactPoints, port))
                .withLocalDatacenter(localDatacenter)
                .withKeyspace(keyspaceName)
                .build();
    }

    @Bean
    public CassandraOperations cassandraTemplate() {
        return new CassandraTemplate(cassandraSession());
    }
}
```

## Spring Configuration

**AppConfig.java**
```java
package com.example.usermanagement.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.example.usermanagement")
@Import(CassandraConfig.class)
public class AppConfig implements WebMvcConfigurer {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper());
        converters.add(converter);
    }
}
```

## User Entity

**User.java**
```java
package com.example.usermanagement.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("users")
public class User {

    @PrimaryKey
    private UUID id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column("username")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column("email")
    private String email;

    @NotBlank(message = "First name is required")
    @Column("first_name")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column("last_name")
    private String lastName;

    @Column("age")
    private Integer age;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public User() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public User(String username, String email, String firstName, String lastName, Integer age) {
        this();
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
```

## Repository Layer

**UserRepository.java**
```java
package com.example.usermanagement.repository;

import com.example.usermanagement.model.User;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CassandraRepository<User, UUID> {

    // Find by username
    @Query("SELECT * FROM users WHERE username = ?0 ALLOW FILTERING")
    Optional<User> findByUsername(String username);

    // Find by email
    @Query("SELECT * FROM users WHERE email = ?0 ALLOW FILTERING")
    Optional<User> findByEmail(String email);

    // Find users by age range
    @Query("SELECT * FROM users WHERE age >= ?0 AND age <= ?1 ALLOW FILTERING")
    List<User> findByAgeBetween(Integer minAge, Integer maxAge);

    // Find users by first name
    @Query("SELECT * FROM users WHERE first_name = ?0 ALLOW FILTERING")
    List<User> findByFirstName(String firstName);
}
```

## Service Layer

**UserService.java**
```java
package com.example.usermanagement.service;

import com.example.usermanagement.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    
    User createUser(User user);
    
    Optional<User> getUserById(UUID id);
    
    Optional<User> getUserByUsername(String username);
    
    Optional<User> getUserByEmail(String email);
    
    List<User> getAllUsers();
    
    List<User> getUsersByAgeBetween(Integer minAge, Integer maxAge);
    
    List<User> getUsersByFirstName(String firstName);
    
    User updateUser(UUID id, User user);
    
    boolean deleteUser(UUID id);
    
    long getUserCount();
}
```

**UserServiceImpl.java**
```java
package com.example.usermanagement.service;

import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        // Check if username already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }
        
        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsersByAgeBetween(Integer minAge, Integer maxAge) {
        return userRepository.findByAgeBetween(minAge, maxAge);
    }

    @Override
    public List<User> getUsersByFirstName(String firstName) {
        return userRepository.findByFirstName(firstName);
    }

    @Override
    public User updateUser(UUID id, User updatedUser) {
        Optional<User> existingUser = userRepository.findById(id);
        
        if (existingUser.isEmpty()) {
            throw new RuntimeException("User not found with id: " + id);
        }

        User user = existingUser.get();
        
        // Update fields if provided
        if (updatedUser.getUsername() != null && !updatedUser.getUsername().equals(user.getUsername())) {
            // Check if new username already exists
            if (userRepository.findByUsername(updatedUser.getUsername()).isPresent()) {
                throw new RuntimeException("Username already exists: " + updatedUser.getUsername());
            }
            user.setUsername(updatedUser.getUsername());
        }
        
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail())) {
            // Check if new email already exists
            if (userRepository.findByEmail(updatedUser.getEmail()).isPresent()) {
                throw new RuntimeException("Email already exists: " + updatedUser.getEmail());
            }
            user.setEmail(updatedUser.getEmail());
        }
        
        if (updatedUser.getFirstName() != null) {
            user.setFirstName(updatedUser.getFirstName());
        }
        
        if (updatedUser.getLastName() != null) {
            user.setLastName(updatedUser.getLastName());
        }
        
        if (updatedUser.getAge() != null) {
            user.setAge(updatedUser.getAge());
        }

        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public boolean deleteUser(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public long getUserCount() {
        return userRepository.count();
    }
}
```

## Controller Layer

**UserController.java**
```java
package com.example.usermanagement.controller;

import com.example.usermanagement.model.User;
import com.example.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Create a new user
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }

    // Get user by username
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.getUserByUsername(username);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }

    // Get user by email
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.getUserByEmail(email);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }

    // Get users by age range
    @GetMapping("/age")
    public ResponseEntity<List<User>> getUsersByAge(
            @RequestParam Integer minAge,
            @RequestParam Integer maxAge) {
        List<User> users = userService.getUsersByAgeBetween(minAge, maxAge);
        return ResponseEntity.ok(users);
    }

    // Get users by first name
    @GetMapping("/firstname/{firstName}")
    public ResponseEntity<List<User>> getUsersByFirstName(@PathVariable String firstName) {
        List<User> users = userService.getUsersByFirstName(firstName);
        return ResponseEntity.ok(users);
    }

    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok(createSuccessResponse("User deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    // Get user count
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getUserCount() {
        long count = userService.getUserCount();
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    // Helper methods
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        return response;
    }

    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }
}
```

## Main Application

**Application.java**
```java
package com.example.usermanagement;

import com.example.usermanagement.config.AppConfig;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;

public class Application implements org.springframework.web.WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // Create the Spring application context
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class);

        // Create the dispatcher servlet
        DispatcherServlet servlet = new DispatcherServlet(context);
        
        // Register the servlet
        ServletRegistration.Dynamic registration = servletContext.addServlet("dispatcher", servlet);
        registration.setLoadOnStartup(1);
        registration.addMapping("/");
    }
}
```

**application.properties**
```properties
# Cassandra Configuration
cassandra.keyspace=user_management
cassandra.contact-points=127.0.0.1
cassandra.port=9042
cassandra.local-datacenter=datacenter1

# Logging
logging.level.com.example.usermanagement=DEBUG
logging.level.org.springframework.data.cassandra=DEBUG
```

## Testing

**UserServiceTest.java**
```java
package com.example.usermanagement.service;

import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@example.com", "John", "Doe", 25);
    }

    @Test
    void createUser_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.createUser(testUser);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).save(testUser);
    }

    @Test
    void createUser_UsernameExists_ThrowsException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.createUser(testUser));
        
        assertTrue(exception.getMessage().contains("Username already exists"));
    }

    @Test
    void getUserById_Found() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserById(userId);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void getUserById_NotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(userId);

        assertFalse(result.isPresent());
    }
}
```

## Running the Application

### 1. Set up Cassandra
Install and start Cassandra locally, or use Docker:

```bash
# Using Docker
docker run --name cassandra -p 9042:9042 -d cassandra:latest

# Wait for Cassandra to start, then create keyspace
docker exec -it cassandra cqlsh -e "CREATE KEYSPACE IF NOT EXISTS user_management WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};"
```

### 2. Build and Run
```bash
# Build the project
mvn clean compile

# Run with Jetty
mvn jetty:run
```

### 3. Test the API
```bash
# Create a user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"johndoe","email":"john@example.com","firstName":"John","lastName":"Doe","age":30}'

# Get all users
curl http://localhost:8080/api/users

# Get user by ID
curl http://localhost:8080/api/users/{user-id}

# Update a user
curl -X PUT http://localhost:8080/api/users/{user-id} \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jane","age":31}'

# Delete a user
curl -X DELETE http://localhost:8080/api/users/{user-id}
```

## Key Spring Concepts Demonstrated

1. **Dependency Injection**: Services are injected into controllers using `@Autowired`
2. **Component Scanning**: `@ComponentScan` automatically discovers beans
3. **Configuration Classes**: `@Configuration` classes replace XML configuration
4. **Web MVC**: `@RestController` and `@RequestMapping` for REST endpoints
5. **Data Access**: Spring Data repositories for database operations
6. **Validation**: Bean validation with `@Valid` and constraint annotations

This setup gives you a solid foundation in Spring Framework fundamentals before moving to Spring Boot!