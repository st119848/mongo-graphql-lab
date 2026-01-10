package com.example.catalog.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String username;
    private String password; // จะเก็บแบบ Hashed
    private String role; // e.g., "USER", "ADMIN"

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}