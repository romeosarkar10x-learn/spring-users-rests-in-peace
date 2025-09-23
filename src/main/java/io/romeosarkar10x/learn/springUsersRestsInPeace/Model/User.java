package io.romeosarkar10x.learn.springUsersRestsInPeace.Model;

import java.util.UUID;
import java.time.LocalDateTime;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


@Table("users")
public class User {
    @PrimaryKey
    @Column("id")
    private UUID id = UUID.randomUUID();

    @NotBlank(message="`username` is required")
    @Size(min=6, max=30, message="`username` should be between 6 to 30 characters")
    @Column("username")
    private final String username;

    @NotBlank(message="`email` is required")
    @Email(message="`email` should be valid")
    @Column("email")
    private final String email;

    @NotBlank(message="`first_name` is required")
    @Column("first_name")
    private final String firstName;

    @NotBlank(message="`last_name` is required")
    @Column("last_name")
    private final String lastName;

    @Column("age")
    private final Integer age;

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Override
    public User clone() {
        var copiedUser = new User(this.username, this.email, this.firstName, this.lastName, this.age);
        copiedUser.id = this.id;
        copiedUser.createdAt = this.createdAt;
        copiedUser.updatedAt = this.updatedAt;

        return copiedUser;
    }

    public boolean equals(User user) {
        if(!this.id.equals(user.id)) {
            return false;
        }

        if(this.username.equals(user.username)) {
            return false;
        }

        if(!this.email.equals(user.email)) {
            return false;
        }

        if(!this.firstName.equals(user.firstName)) {
            return false;
        }

        if(!this.lastName.equals(user.lastName)) {
            return false;
        }

        return this.age.equals(user.age);
    }

    public User(String username, String email, String firstName, String lastName, Integer age) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public UUID getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
